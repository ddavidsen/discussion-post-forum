package database;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.User;

/*******
 * <p> Title: Database Class. </p>
 * 
 * <p> Description: This is an in-memory database built on H2.  Detailed documentation of H2 can
 * be found at https://www.h2database.com/html/main.html (Click on "PDF (2MP) for a PDF of 438 pages
 * on the H2 main page.)  This class leverages H2 and provides numerous special supporting methods.
 * </p>
 * 
 * @author Diana Davidsen
 * @author Lynn Robert Carter
 * @author Jessica Lara Valdez
 * 
 */

/*
 * The Database class is responsible for establishing and managing the connection to the database,
 * and performing operations such as user registration, login validation, handling invitation 
 * codes, and numerous other database related functions.
 */
public class Database {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase;AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=TRUE";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	//  Shared variables used within this class
	private Connection connection = null;		// Singleton to access the database 
	private Statement statement = null;			// The H2 Statement is used to construct queries
	
	// These are the easily accessible attributes of the currently logged-in user
	// This is only useful for single user applications
	private String currentUsername;
	private String currentPassword;
	private String currentFirstName;
	private String currentMiddleName;
	private String currentLastName;
	private String currentPreferredFirstName;
	private String currentEmailAddress;
	private boolean currentAdminRole;
	private boolean currentStaffRole;
	private boolean currentStudentRole;

	/*******
	 * <p> Method: Database </p>
	 * 
	 * <p> Description: The default constructor used to establish this singleton object.</p>
	 * 
	 */
	
	public Database () {
		
	}
	
	
/*******
 * <p> Method: connectToDatabase </p>
 * 
 * <p> Description: Used to establish the in-memory instance of the H2 database from secondary
 *		storage.</p>
 *
 * @throws SQLException when the DriverManager is unable to establish a connection
 * 
 */
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			connection.setAutoCommit(true);
			// You can use this command to clear the database and restart from fresh.
			//statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	
/*******
 * <p> Method: createTables </p>
 * 
 * <p> Description: Used to create new instances of the database tables used by this class.</p>
 * 
 */
	private void createTables() throws SQLException {
		// Create the user database
		// NOTE: added oneTimePassword column to support one-time-password (OTP) flows.
		String userTable = "CREATE TABLE IF NOT EXISTS userDB ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(512), "
				+ "oneTimePassword VARCHAR(255), "
				+ "firstName VARCHAR(255), "
				+ "middleName VARCHAR(255), "
				+ "lastName VARCHAR (255), "
				+ "preferredFirstName VARCHAR(255), "
				+ "emailAddress VARCHAR(255), "
				+ "adminRole BOOL DEFAULT FALSE, "
				+ "staffRole BOOL DEFAULT FALSE, "
				+ "studentRole BOOL DEFAULT FALSE, "
				+ "otpExpiration TIMESTAMP)";
		statement.execute(userTable);
		
		// Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	    		+ "emailAddress VARCHAR(255), "
	            + "role VARCHAR(10))";
	    statement.execute(invitationCodesTable);
	    
	    // Create the settings table for first admin setup tracking
	    String settingsTable = "CREATE TABLE IF NOT EXISTS settings ("
	            + "id INT PRIMARY KEY, "
	            + "first_admin_setup_complete BOOLEAN DEFAULT FALSE)";
	    statement.execute(settingsTable);

	    // Ensure a default row exists
	    String insertDefaultSettings = "INSERT INTO settings (id, first_admin_setup_complete) "
	            + "SELECT 1, FALSE "
	            + "WHERE NOT EXISTS (SELECT 1 FROM settings WHERE id = 1)";
	    statement.execute(insertDefaultSettings);
	    
	    // Table to store all threads
	    String threadsTable = "CREATE TABLE IF NOT EXISTS threadsDB ("
	    		+ "threadName VARCHAR(255) PRIMARY KEY)";
	    statement.execute(threadsTable);
	    
	    // Create default general thread on database creation
	    String insertDefaultThread = "INSERT INTO threadsDB (threadName) "
	    		+ "SELECT 'General' "
	    		+ "WHERE NOT EXISTS (SELECT 1 FROM threadsDB WHERE threadName = 'General')";
	    statement.execute(insertDefaultThread);
	    
	    // Table to store all posts and replies
	    String postsTable = "CREATE TABLE IF NOT EXISTS postsDB ("
	    		+ "postID INT AUTO_INCREMENT PRIMARY KEY, "
	    		+ "author VARCHAR(100) NOT NULL, "
	    		+ "title VARCHAR(255), "
	    		+ "body CLOB NOT NULL, "
	    		+ "parentThread VARCHAR(255), "
	    		+ "numReplies INT DEFAULT 0, "
	    		+ "parentPostID INT, " 
	    		+ "FOREIGN KEY (parentPostID) REFERENCES postsDB(postID) ON DELETE SET NULL, "
	    		+ "FOREIGN KEY (parentThread) REFERENCES threadsDB(threadName) ON DELETE CASCADE ON UPDATE CASCADE, "
	    		+ "read VARCHAR(10))";
	    statement.execute(postsTable);
	    
	    // Table to store the set of parameters
	    String parametersTable = "CREATE TABLE IF NOT EXISTS parameterDB ("
	    		+ "paramNum INT AUTO_INCREMENT PRIMARY KEY, "
	    		+ "parameter VARCHAR(255) NOT NULL)";
	    statement.execute(parametersTable);
	    
	    String gradesTable = "CREATE TABLE IF NOT EXISTS gradesDB ("
	    		+ "id INT AUTO_INCREMENT PRIMARY KEY, "
	    		+ "postID INT, "
	    		+ "student VARCHAR(100) NOT NULL, "
	    		+ "grader VARCHAR(100) NOT NULL, "
	    		+ "score INT, "
	    		+ "comments CLOB)";
	    statement.execute(gradesTable);
	    
	    String requestsTable = "CREATE TABLE IF NOT EXISTS requestsDB ("
	    		+ "id INT AUTO_INCREMENT PRIMARY KEY, "
	    		+ "requester VARCHAR (100) NOT NULL, "
	    		+ "requestDescription VARCHAR (255) NOT NULL, "
	    		+ "actionTaken VARCHAR (255), "
	    		+ "status VARCHAR (100) NOT NULL)";
	    statement.execute(requestsTable);

	}


/*******
 * <p> Method: isDatabaseEmpty </p>
 * 
 * <p> Description: If the user database has no rows, true is returned, else false.</p>
 * 
 * @return true if the database is empty, else it returns false
 * 
 */
	public boolean isDatabaseEmpty() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count") == 0;
			}
		}  catch (SQLException e) {
	        return false;
	    }
		return true;
	}
	
	
/*******
 * <p> Method: getNumberOfUsers </p>
 * 
 * <p> Description: Returns an integer .of the number of users currently in the user database. </p>
 * 
 * @return the number of user records in the database.
 * 
 */
	public int getNumberOfUsers() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch (SQLException e) {
	        return 0;
	    }
		return 0;
	}

/*******
 * <p> Method: register(User user) </p>
 * 
 * <p> Description: Creates a new row in the database using the user parameter. </p>
 * 
 * @throws SQLException when there is an issue creating the SQL command or executing it.
 * 
 * @param user specifies a user object to be added to the database.
 * 
 */
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO userDB (userName, password, firstName, middleName, "
				+ "lastName, preferredFirstName, emailAddress, adminRole, staffRole, studentRole) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			currentUsername = user.getUserName();
			pstmt.setString(1, currentUsername);
			
			currentPassword = user.getPassword();
			pstmt.setString(2, currentPassword);
			
			currentFirstName = user.getFirstName();
			pstmt.setString(3, currentFirstName);
			
			currentMiddleName = user.getMiddleName();			
			pstmt.setString(4, currentMiddleName);
			
			currentLastName = user.getLastName();
			pstmt.setString(5, currentLastName);
			
			currentPreferredFirstName = user.getPreferredFirstName();
			pstmt.setString(6, currentPreferredFirstName);
			
			currentEmailAddress = user.getEmailAddress();
			pstmt.setString(7, currentEmailAddress);
			
			currentAdminRole = user.getAdminRole();
			pstmt.setBoolean(8, currentAdminRole);
			
			currentStaffRole = user.getStaffRole();
			pstmt.setBoolean(9, currentStaffRole);
			
			currentStudentRole = user.getStudentRole();
			pstmt.setBoolean(10, currentStudentRole);
			
			pstmt.executeUpdate();
		}
		
	}
	
	/*******
	 * <p> Method: deleteUser(String username) </p>
	 * 
	 * <p> Description: Deletes a users row in the database using their username. </p>
	 * 
	 * @param username specifies the user to delete from the database.
	 * 
	 */
	public void deleteUser(String username) throws SQLException {

	    // SQL statement to delete a user
	    String deleteUser = "DELETE FROM userDB WHERE userName = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(deleteUser)) {
	        pstmt.setString(1, username);  
	        pstmt.executeUpdate();  
	    }
	}
	
/*******
 *  <p> Method: List getUserList() </p>
 *  
 *  <P> Description: Generate an List of Strings, one for each user in the database,
 *  starting with "Select User" at the start of the list. </p>
 *  
 *  @return a list of userNames found in the database.
 */
	public List<String> getUserList () {
		List<String> userList = new ArrayList<String>();
		userList.add("<Select a User>");
		String query = "SELECT userName FROM userDB";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				userList.add(rs.getString("userName"));
			}
		} catch (SQLException e) {
	        return null;
	    }
//		System.out.println(userList);
		return userList;
	}
	
	/*******
	 *  <p> Method: List getDetailedUserList() </p>
	 *  
	 *  <P> Description: Generate an List of Strings, one for each user in the database,
	 *  including all user details </p>
	 *  
	 *  @return a list of users found in the database.
	 */
	public List<String> getDetailedUserList () {
		List<String> userList = new ArrayList<String>();
		String query = "SELECT userName, firstName, lastName, emailAddress, adminRole, staffRole, studentRole FROM userDB";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				userList.add(rs.getString("userName") + ", " +
						rs.getString("firstName") + ", " +
						rs.getString("lastName") + ", " +
						rs.getString("emailAddress") + ", " + 
						Boolean.toString(rs.getBoolean("adminRole")) + ", " +
						Boolean.toString(rs.getBoolean("staffRole")) + ", " +
						Boolean.toString(rs.getBoolean("studentRole")));
			}
		} catch (SQLException e) {
			System.err.println("*** ERROR *** getPostList(): " + e.getMessage());
	    }
		return userList;
	}

	
/*******
 * <p> Method: insertPost(Post post) </p>
 * 
 * <p> Description: Creates a new row in the database using the post parameter. </p>
 * 
 * @throws SQLException when there is an issue creating the SQL command or executing it.
 * 
 * @param post specifies a post object to be added to the database.
 * 
 */
	public void insertPost(Post post) throws SQLException {
		
		//Used for searching read or unread feature
		double randomNum = Math.random();
		String read;
		if (randomNum < 0.5) { read = "unread"; }
		else { read = "read"; }
		
		// Insert post
		String insertPost = "INSERT INTO postsDB (author, title, body, parentThread, read) "
				+ "VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertPost)) {
			pstmt.setString(1, post.getAuthor());
			
			pstmt.setString(2, post.getTitle());
			
			pstmt.setString(3, post.getBody());
					
			pstmt.setString(4, post.getParentThread());
			
			pstmt.setString(5, read);
			
			pstmt.executeUpdate();
		}
	}

/*******
 * <p> Method: insertReply(Reply reply) </p>
 * 
 * <p> Description: Creates a new row in the database using the reply parameter. </p>
 * 
 * @throws SQLException when there is an issue creating the SQL command or executing it.
 * 
 * @param reply specifies a reply object to be added to the database.
 * 
 */
	public void insertReply(Reply reply) throws SQLException {
		//Used for searching read or unread feature
		double randomNum = Math.random();
		String read;
		if (randomNum < 0.5) { read = "unread"; }
		else { read = "read"; }
				
		// Insert post
		String insertReply = "INSERT INTO postsDB (author, body, parentPostID, read) "
				+ "VALUES (?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertReply)) {
			pstmt.setString(1, reply.getAuthor());
			
			pstmt.setString(2, reply.getBody());
			
			pstmt.setInt(3, reply.getParentPostID());
			
			pstmt.setString(4, read);
			
			pstmt.executeUpdate();
		}
		
		// Update number of replies if a reply gets added
		String updateParent = "UPDATE postsDB SET numReplies = numReplies + 1 WHERE postID = ?";
		try (PreparedStatement pstmt2 = connection.prepareStatement(updateParent)) {
		    pstmt2.setInt(1, reply.getParentPostID());
		    pstmt2.executeUpdate();
		}
	}
	
/*******
 * <p> Method: postExists(int postID) </p>
 * 
 * <p> Description: Selects a row in the database using the postID parameter. </p>
 * 
 * @throws SQLException when there is an issue creating the SQL command or executing it.
 * 
 * @param postID specifies a post to search for in the database
 * 
 * @return returns true if the post was found in the database.
 * 
 */
	public boolean postExists(int postID) throws SQLException {
	    String query = "SELECT 1 FROM postsDB WHERE postID = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, postID);
	        ResultSet rs = pstmt.executeQuery();
	        return rs.next();
	    } catch (SQLException e) {
	        System.err.println("*** ERROR *** postExists(): " + e.getMessage());
	        return false;
	    }
	}
	
/*******
 * <p> Method: editPostReply(int posdID, String newBody) </p>
 * 
 * <p> Description: Edits a row in the database </p>
 * 
 * @throws SQLException when there is an issue creating the SQL command or executing it.
 * 
 * @param postID specifies a post to search for
 * 
 * @param newBody specifies the new body text of the found post
 * 
 * @return returns true if the post was successfully edited.
 * 
 */
	public boolean editPostReply(int postID, String newBody) throws SQLException {
		String editPost = "UPDATE postsDB SET body = ? WHERE postID = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(editPost)) {
	        pstmt.setString(1, newBody);
	        pstmt.setInt(2, postID);
	        int affectedRows = pstmt.executeUpdate();
	        return affectedRows > 0;
	    }
	}
	
/*******
 * <p> Method: deletePostReply(int postID) </p>
 * 
 * <p> Description: Deletes a row in the database using the postID parameter. </p>
 * 
 * @throws SQLException when there is an issue creating the SQL command or executing it.
 * 
 * @param postID specifies the post to search for and delete
 * 
 * @return returns true if the post was successfully deleted.
 * 
 */
	public boolean deletePostReply(int postID) throws SQLException {
	    String deletePost = "DELETE FROM postsDB WHERE postID = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(deletePost)) {
	        pstmt.setInt(1, postID);
	        int affectedRows = pstmt.executeUpdate();
	        return affectedRows > 0;
	    }
	}
	
	/*******
	 *  <p> Method: List getPostList() </p>
	 *  
	 *  <P> Description: Generate an List of Strings, one for each post in the database</p>
	 *  
	 *  @return a list of posts with their details found in the database.
	 */
	public List<String> getPostList () {
		List<String> postList = new ArrayList<String>();
		String query = "SELECT parentThread, postID, author, title, parentPostID, body, numReplies, read FROM postsDB";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				postList.add(rs.getString("parentThread") + ", " +
						rs.getInt("postID") + ", " + 
						rs.getInt("parentPostID") + ", " +
						rs.getInt("numReplies") + ", " +
						rs.getString("author") + ", " +
						rs.getString("title") + ", " +
						rs.getString("body") + ", " + 
						rs.getString("read"));
			}
		} catch (SQLException e) {
			System.err.println("*** ERROR *** getPostList(): " + e.getMessage());
	    }
		return postList;
	}
	
	/*******
	 *  <p> Method: List getPostListKeyword() </p>
	 *  
	 *  <P> Description: Generate an List of Strings, one for each post in the database
	 *  that contains the given keyword anywhere.</p>
	 *  
	 *  @return a list of posts with their details found in the database.
	 */
	public List<String> getPostListKeyword (String keyword) {
		List<String> postList = new ArrayList<String>();
		String query = "SELECT parentThread, postID, author, title, parentPostID, body, numReplies, read FROM postsDB "
				+ "WHERE COALESCE(parentThread, '') LIKE ? "
				+ "OR COALESCE(CAST(postID AS VARCHAR), '') LIKE ? "
				+ "OR COALESCE(author, '') LIKE ? "
				+ "OR COALESCE(title, '') LIKE ? "
				+ "OR COALESCE(CAST(parentPostID AS VARCHAR), '') LIKE ? "
				+ "OR COALESCE(SUBSTRING(body, 1, 10000), '') LIKE ? "
				+ "OR COALESCE(CAST(numReplies AS VARCHAR), '') LIKE ? "
				+ "OR COALESCE(read, '') LIKE ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			String pattern = "%" + keyword + "%";
			for (int i = 1; i <= 8; i++) {
				pstmt.setString(i,  pattern);
			}
			
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				postList.add(rs.getString("parentThread") + ", " +
						rs.getInt("postID") + ", " + 
						rs.getInt("parentPostID") + ", " +
						rs.getInt("numReplies") + ", " +
						rs.getString("author") + ", " +
						rs.getString("title") + ", " +
						rs.getString("body") + ", " + 
						rs.getString("read"));
			}
		} catch (SQLException e) {
			System.err.println("*** ERROR *** getPostList(): " + e.getMessage());
	    }
		return postList;
	}
	
	/*******
	 *  <p> Method: List getAuthorofPost(int postID) </p>
	 *  
	 *  <P> Description: Given a postID, give the author of that post. </p>
	 *  
	 *  @param postID specifies the postID we want the author for
	 *  
	 *  @return username of the post's author
	 */
	public String getAuthorOfPost(int postID) {
		String query = "SELECT author FROM postsDB WHERE postID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, postID);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	        	return rs.getString("author");
	        }
		} catch (SQLException e) {
			System.err.println("*** ERROR *** getAuthorOfPostt(): " + e.getMessage());
	    }
		return "";
	}
	
	
/*******
 *  <p> Method: List getThreadList() </p>
 *  
 *  <P> Description: Generate an List of Strings, one for each thread in the database. </p>
 *  
 *  @return a list of threads found in the database.
 *  
 */
	public ObservableList<String> getThreadList () {
		ObservableList<String> threadList = FXCollections.observableArrayList();
		String query = "SELECT threadName FROM threadsDB";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				threadList.add(rs.getString("threadName"));
			}
		} catch (SQLException e) {
			return FXCollections.observableArrayList();
	    }
		return threadList;
	}
	
	/*******
	 * <p> Method: insertThread(String threadName) </p>
	 * 
	 * <p> Description: Creates a new row in the database using the thread name parameter. </p>
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 * 
	 * @param threadName specifies the name of the thread to be added.
	 * 
	 */
	public void insertThread(String threadName) throws SQLException {
		String insertThread = "INSERT INTO threadsDB (threadName) "
				+ "VALUES(?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertThread)) {
			pstmt.setString(1, threadName);
			
			pstmt.executeUpdate();
		}
	}
	
	/*******
	 * <p> Method: editThread(String oldName, String newName) </p>
	 * 
	 * <p> Description: Edits a row in the database </p>
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 * 
	 * @param oldName specifies a thread to search for.
	 * 
	 * @param newName specifies the new name of the thread.
	 * 
	 * @return returns true if the thread was successfully edited.
	 * 
	 */
	public boolean editThread(String oldName, String newName) throws SQLException {
		String editThread = "UPDATE threadsDB SET threadName = ? WHERE threadName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(editThread)) {
	        pstmt.setString(1, newName);
	        pstmt.setString(2, oldName);
	        int affectedRows = pstmt.executeUpdate();
	        return affectedRows > 0;
	    }
	}
	
	/*******
	 * <p> Method: deleteThread(String name) </p>
	 * 
	 * <p> Description: Deletes a row in the database using the name parameter. </p>
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 * 
	 * @param name specifies the thread to search for and delete.
	 * 
	 * @return returns true if the thread is successfully deleted.
	 * 
	 */
	public boolean deleteThread(String name) throws SQLException {
		String deleteThread = "DELETE FROM threadsDB WHERE threadName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(deleteThread)) {
	        pstmt.setString(1, name);
	        int affectedRows = pstmt.executeUpdate();
	        return affectedRows > 0;
	    }
	}
	
	/*******
	 * <p> Method: threadExists(int name) </p>
	 * 
	 * <p> Description: Selects a row in the database using the name parameter. </p>
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 * 
	 * @param name specifies a thread to search for in the database.
	 * 
	 * @return returns true if the thread is found in the database.
	 * 
	 */
	public boolean threadExists(String name) throws SQLException {
	    String query = "SELECT 1 FROM threadsDB WHERE threadName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, name);
	        ResultSet rs = pstmt.executeQuery();
	        return rs.next();
	    } catch (SQLException e) {
	        System.err.println("*** ERROR *** threadExists(): " + e.getMessage());
	        return false;
	    }
	}
	
	/*******
	 *  <p> Method: List getParamList() </p>
	 *  
	 *  <P> Description: Generate an List of Strings, one for each parameter in the database.</p>
	 *  
	 *  @return a list of parameters found in the database.
	 *  
	 */
	public List<String> getParamList () {
		List<String> paramList = new ArrayList<String>();
		String query = "SELECT paramNum, parameter FROM parameterDB";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				paramList.add(rs.getInt("paramNum") + ". " + rs.getString("parameter"));
			}
		} catch (SQLException e) {
	        return null;
	    }
		return paramList;
	}
	
	/*******
	 *  <p> Method: List getParamIDList() </p>
	 *  
	 *  <P> Description: Generate an List of integers, one for each parameter in the database.</p>
	 *  
	 *  @return a list of parameter IDs found in the database.
	 *  
	 */
	public List<Integer> getParamIDList () {
		List<Integer> paramIDList = new ArrayList<Integer>();
		String query = "SELECT paramNum FROM parameterDB";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				paramIDList.add(rs.getInt("paramNum"));
			}
		} catch (SQLException e) {
	        return null;
	    }
		return paramIDList;
	}
	
	/*******
	 * <p> Method: insertParam(String parameter) </p>
	 * 
	 * <p> Description: Creates a new row in the database using the parameter text. </p>
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 * 
	 * @param parameter specifies a parameter object to be added to the database.
	 * 
	 */
	public void insertParam(String parameter) throws SQLException {
		String insertParam = "INSERT INTO parameterDB (parameter) "
				+ "VALUES(?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertParam)) {

			pstmt.setString(1, parameter);
			
			pstmt.executeUpdate();
		}
	}
	
	/*******
	 * <p> Method: editParam(int paramNum, String newText) </p>
	 * 
	 * <p> Description: Edits a row in the database </p>
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 * 
	 * @param paramNum specifies a parameter to search for
	 * 
	 * @param newText specifies the new text of the found parameter.
	 * 
	 * @return returns true if the parameter is successfully deleted.
	 * 	  
	 */
	public boolean editParam(int paramNum, String newText) throws SQLException {
		String editParam = "UPDATE parameterDB SET parameter = ? WHERE paramNum = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(editParam)) {
	        pstmt.setString(1, newText);
	        pstmt.setInt(2, paramNum);
	        int affectedRows = pstmt.executeUpdate();
	        return affectedRows > 0;
	    }
	}
	
	/*******
	 * <p> Method: deleteParam(int paramNum) </p>
	 * 
	 * <p> Description: Deletes a row in the database using the paramNum. </p>
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 * 
	 * @param paramNum specifies the parameter to search for and delete
	 * 
	 * @return returns true if the parameter is successfully deleted.
	 * 
	 */
	public boolean deleteParam(int paramNum) throws SQLException {
		String deleteParam = "DELETE FROM parameterDB WHERE paramNum = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(deleteParam)) {
	        pstmt.setInt(1, paramNum);
	        int affectedRows = pstmt.executeUpdate();
	        return affectedRows > 0;
	    }
	}
	
	/*******
	 * <p> Method: paramExists(int paramNum) </p>
	 * 
	 * <p> Description: Selects a row in the database using the paramNum. </p>
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 * 
	 * @param paramNum specifies a parameter to search for in the database
	 * 
	 * @return returns true if the parameter is found in the database.
	 * 
	 */
	public boolean paramExists(int paramNum) throws SQLException {
	    String query = "SELECT 1 FROM parameterDB WHERE paramNum = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, paramNum);
	        ResultSet rs = pstmt.executeQuery();
	        return rs.next();
	    } catch (SQLException e) {
	        System.err.println("*** ERROR *** paramExists(): " + e.getMessage());
	        return false;
	    }
	}
	
	/*******
	 *  <p> Method: List getGradesList() </p>
	 *  
	 *  <P> Description: Generate a List of Strings, one for each grade in the database. </p>
	 *  
	 *  @throws SQLException when there is an issue creating the SQL command or executing it.
	 *  
	 *  @return a list of grades found in the database.
	 *  
	 */
	public List<String> getGradesList () throws SQLException {
		List<String> gradeList = new ArrayList<String>();
		String query = "SELECT id, postID, student, grader, score, comments FROM gradesDB";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				gradeList.add(rs.getInt("id") + ", " + rs.getInt("postID") + ", " + rs.getString("student") + ", " + rs.getString("grader")
				+ ", " + rs.getInt("score") + ", " + rs.getString("comments"));
			}
		} catch (SQLException e) {
	        return null;
	    }
		return gradeList;
	}
	
	/*******
	 *  <p> Method: List getStudentGradesList(String studentUsername) </p>
	 *  
	 *  <P> Description: Generate an List of Strings, same as getGradesList, but only gets
	 *  grades for the specified student.</p>
	 *  
	 *  @param studentUsername specifies the student that we want to get grades for.
	 *  
	 *  @return a list of grades found in the database.
	 *  
	 */
	public List<String> getStudentGradesList (String studentUsername) {
		List<String> gradeList = new ArrayList<String>();
		String query = "SELECT id, postID, student, grader, score, comments FROM gradesDB WHERE student = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, studentUsername);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				gradeList.add(rs.getInt("id") + ", " + rs.getInt("postID") + ", " + rs.getString("student") + ", " + rs.getString("grader")
				+ ", " + rs.getInt("score") + ", " + rs.getString("comments"));
			}
		} catch (SQLException e) {
	        return null;
	    }
		return gradeList;
	}
	
	/*******
	 *  <p> Method: List getGradesList() </p>
	 *  
	 *  <P> Description: Generate an List of Integers, one for each grades ID in the database.</p>
	 *  
	 *  @return a list of grade IDs found in the database.
	 *  
	 */
	public List<Integer> getGradesIDList () {
		List<Integer> gradeIDList = new ArrayList<Integer>();
		String query = "SELECT id FROM gradesDB";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				gradeIDList.add(rs.getInt("id"));
			}
		} catch (SQLException e) {
	        return null;
	    }
		return gradeIDList;
	}
	
	/*******
	 * <p> Method: insertGrade(int postID, String student, String grader, int score, String comments) </p>
	 * 
	 * <p> Description: Creates a new row in the database using the grade details. </p>
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 * 
	 * @param postID specifies the post being graded.
	 * 
	 * @param student specifies the student (or author of the post) being graded.
	 * 
	 * @param grader specifies the staff or admin user which graded the post.
	 * 
	 * @param score specifies the score given for the assignment.
	 * 
	 * @param comments includes optional comments from the grader.
	 * 
	 */
	public void insertGrade(int postID, String student, String grader, int score, String comments) throws SQLException {
		String insertGrade = "INSERT INTO gradesDB (postID, student, grader, score, comments) "
				+ "VALUES(?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertGrade)) {

			pstmt.setInt(1, postID);
			pstmt.setString(2, student);
			pstmt.setString(3, grader);
			pstmt.setInt(4, score);
			pstmt.setString(5, comments);
			
			pstmt.executeUpdate();
		}
	}
	
	/*******
	 * <p> Method: editGrade(int gradeID, int newScore) </p>
	 * 
	 * <p> Description: Edits a row in the database </p>
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 * 
	 * @param gradeID specifies the grade to search for
	 * 
	 * @param newScore specifies the new score of the found grade.
	 * 
	 * @return returns true if the grade was successfully edited.
	 * 
	 */
	public boolean editGrade(int gradeID, int newScore) throws SQLException {
		String editGrade = "UPDATE gradesDB SET score = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(editGrade)) {
	        pstmt.setInt(1, newScore);
	        pstmt.setInt(2, gradeID);
	        int affectedRows = pstmt.executeUpdate();
	        return affectedRows > 0;
	    }
	}
	
	/*******
	 * <p> Method: deleteGrade(int gradeID) </p>
	 * 
	 * <p> Description: Deletes a row in the database using the gradeID parameter. </p>
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 * 
	 * @param gradeID specifies the grade to search for and delete
	 * 
	 * @return returns true if the grade was successfully deleted.
	 * 
	 */
	public boolean deleteGrade(int gradeID) throws SQLException {
		String deleteGrade = "DELETE FROM gradesDB WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(deleteGrade)) {
	        pstmt.setInt(1, gradeID);
	        int affectedRows = pstmt.executeUpdate();
	        return affectedRows > 0;
	    }
	}
	
	/*******
	 * <p> Method: gradeExists(int gradeID) </p>
	 * 
	 * <p> Description: Selects a row in the database using the gradeID parameter. </p>
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 * 
	 * @param gradeID specifies a grade to search for in the database
	 * 
	 * @return returns true if the grade is found.
	 * 
	 */
	public boolean gradeExists(int gradeID) throws SQLException {
	    String query = "SELECT 1 FROM gradesDB WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, gradeID);
	        ResultSet rs = pstmt.executeQuery();
	        return rs.next();
	    } catch (SQLException e) {
	        System.err.println("*** ERROR *** gradeExists(): " + e.getMessage());
	        return false;
	    }
	}
	
	/*******
	 *  <p> Method: List getRequestsList() </p>
	 *  
	 *  <P> Description: Generate a List of Strings, one for each request in the database. </p>
	 *  
	 *  @return a list of requests found in the database.
	 *  
	 */
	public List<String> getRequestsList () {
		List<String> requestList = new ArrayList<String>();
		String query = "SELECT id, requester, requestDescription, actionTaken, status FROM requestsDB";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				requestList.add(rs.getInt("id") + ", " + rs.getString("requester") + ", " + 
						rs.getString("requestDescription") + ", " + rs.getString("actionTaken")
						+ ", " + rs.getString("status"));
			}
		} catch (SQLException e) {
	        return requestList;
	    }
		return requestList;
	}
	
	/*******
	 * <p> Method: insertRequest(String username, String description) </p>
	 * 
	 * <p> Description: Creates a new row in the database using the request details. </p>
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 * 
	 * @param username specifies the initial user which made the request.
	 * 
	 * @param description specifies action which needs to be done.
	 * 
	 * 
	 */
	public void insertRequest(String username, String description) throws SQLException {
		String insertRequest = "INSERT INTO requestsDB (requester, requestDescription, status) "
				+ "VALUES(?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertRequest)) {

			pstmt.setString(1, username);
			pstmt.setString(2, description);
			pstmt.setString(3, "open");
			
			pstmt.executeUpdate();
		}
	}
	
	/*******
	 * <p> Method: openRequest(int requestID, String description) </p>
	 * 
	 * <p> Description: Edits a row in the database </p>
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 * 
	 * @param requestID specifies the request to search for
	 * 
	 * @param description specifies the new description of the action needed.
	 * 
	 * @return returns true if the request was successfully edited.
	 * 
	 */
	public boolean openRequest(int requestID, String description) throws SQLException {
		String editRequest = "UPDATE requestsDB SET requestDescription = ?, status = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(editRequest)) {
	        pstmt.setString(1, description);
	        pstmt.setString(2, "open");
	        pstmt.setInt(3,  requestID);
	        int affectedRows = pstmt.executeUpdate();
	        return affectedRows > 0;
	    }
	}
	
	/*******
	 * <p> Method: closeRequest(int requestID, String actionTaken) </p>
	 * 
	 * <p> Description: Edits a row in the database </p>
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 * 
	 * @param requestID specifies the request to search for
	 * 
	 * @param actionTaken specifies the action that the admin user performed.
	 * 
	 * @return returns true if the request was successfully edited.
	 * 
	 */
	public boolean closeRequest(int requestID, String actionTaken) throws SQLException {
		String editRequest = "UPDATE requestsDB SET actionTaken = ?, status = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(editRequest)) {
	        pstmt.setString(1, actionTaken);
	        pstmt.setString(2, "closed");
	        pstmt.setInt(3, requestID);
	        int affectedRows = pstmt.executeUpdate();
	        return affectedRows > 0;
	    }
	}
	
	/*******
	 * <p> Method: requestExists(int requestID) </p>
	 * 
	 * <p> Description: Selects a row in the database using the requestID parameter. </p>
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 * 
	 * @param requestID specifies a request to search for in the database
	 * 
	 * @return returns true if the request is found.
	 * 
	 */
	public boolean requestExists(int requestID) throws SQLException {
	    String query = "SELECT 1 FROM requestsDB WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, requestID);
	        ResultSet rs = pstmt.executeQuery();
	        return rs.next();
	    } catch (SQLException e) {
	        System.err.println("*** ERROR *** requestExists(): " + e.getMessage());
	        return false;
	    }
	}
	
/*******
 * <p> Method: boolean loginAdmin(User user) </p>
 * 
 * <p> Description: Check to see that a user with the specified username, password, and role
 * 		is the same as a row in the table for the username, password, and role. </p>
 * 
 * @param user specifies the specific user that should be logged in playing the Admin role.
 * 
 * @return true if the specified user has been logged in as an Admin else false.
 * 
 */
	public boolean loginAdmin(User user){
		// Validates an admin user's login credentials so the user can login in as an Admin.
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "adminRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();	// If a row is returned, rs.next() will return true		
		} catch  (SQLException e) {
	        e.printStackTrace();
	    }
		return false;
	}
	
	
/*******
 * <p> Method: boolean loginStaffRole(User user) </p>
 * 
 * <p> Description: Check to see that a user with the specified username, password, and role
 * 		is the same as a row in the table for the username, password, and role. </p>
 * 
 * @param user specifies the specific user that should be logged in playing the Student role.
 * 
 * @return true if the specified user has been logged in as an Student else false.
 * 
 */
	public boolean loginStaffRole(User user) {
		// Validates a student user's login credentials.
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "staffRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch  (SQLException e) {
		       e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p> Method: boolean loginStudentRole(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username, password, and role
	 * 		is the same as a row in the table for the username, password, and role. </p>
	 * 
	 * @param user specifies the specific user that should be logged in playing the Reviewer role.
	 * 
	 * @return true if the specified user has been logged in as an Student else false.
	 * 
	 */
	// Validates a reviewer user's login credentials.
	public boolean loginStudentRole(User user) {
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "studentRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch  (SQLException e) {
		       e.printStackTrace();
		}
		return false;
	}
	
	
	/*******
	 * <p> Method: boolean doesUserExist(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username is  in the table. </p>
	 * 
	 * @param userName specifies the specific user that we want to determine if it is in the table.
	 * 
	 * @return true if the specified user is in the table else false.
	 * 
	 */
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM userDB WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}

	
	/*******
	 * <p> Method: int getNumberOfRoles(User user) </p>
	 * 
	 * <p> Description: Determine the number of roles a specified user plays. </p>
	 * 
	 * @param user specifies the specific user that we want to determine if it is in the table.
	 * 
	 * @return the number of roles this user plays (0 - 5).
	 * 
	 */	
	// Get the number of roles that this user plays
	public int getNumberOfRoles (User user) {
		int numberOfRoles = 0;
		if (user.getAdminRole()) numberOfRoles++;
		if (user.getStaffRole()) numberOfRoles++;
		if (user.getStudentRole()) numberOfRoles++;
		return numberOfRoles;
	}	

	
	/*******
	 * <p> Method: String generateInvitationCode(String emailAddress, String role) </p>
	 * 
	 * <p> Description: Given an email address and a roles, this method establishes and invitation
	 * code and adds a record to the InvitationCodes table.  When the invitation code is used, the
	 * stored email address is used to establish the new user and the record is removed from the
	 * table.</p>
	 * 
	 * @param emailAddress specifies the email address for this new user.
	 * 
	 * @param role specified the role that this new user will play.
	 * 
	 * @return the code of six characters so the new user can use it to securely setup an account.
	 * 
	 */
	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode(String emailAddress, String role) {
	    String code = UUID.randomUUID().toString().substring(0, 6); // Generate a random 6-character code
	    String query = "INSERT INTO InvitationCodes (code, emailaddress, role) VALUES (?, ?, ?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.setString(2, emailAddress);
	        pstmt.setString(3, role);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return code;
	}

	
	/*******
	 * <p> Method: int getNumberOfInvitations() </p>
	 * 
	 * <p> Description: Determine the number of outstanding invitations in the table.</p>
	 *  
	 * @return the number of invitations in the table.
	 * 
	 */
	// Number of invitations in the database
	public int getNumberOfInvitations() {
		String query = "SELECT COUNT(*) AS count FROM InvitationCodes";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch  (SQLException e) {
	        e.printStackTrace();
	    }
		return 0;
	}
	
	
	/*******
	 * <p> Method: boolean emailaddressHasBeenUsed(String emailAddress) </p>
	 * 
	 * <p> Description: Determine if an email address has been user to establish a user.</p>
	 * 
	 * @param emailAddress is a string that identifies a user in the table
	 *  
	 * @return true if the email address is in the table, else return false.
	 * 
	 */
	// Check to see if an email address is already in the database
	public boolean emailaddressHasBeenUsed(String emailAddress) {
	    String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE emailAddress = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, emailAddress);
	        ResultSet rs = pstmt.executeQuery();
	        System.out.println(rs);
	        if (rs.next()) {
	            // Mark the code as used
	        	return rs.getInt("count")>0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return false;
	}
	
	
	/*******
	 * <p> Method: String getRoleGivenAnInvitationCode(String code) </p>
	 * 
	 * <p> Description: Get the role associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 * @return the role for the code or an empty string.
	 * 
	 */
	// Obtain the roles associated with an invitation code.
	public String getRoleGivenAnInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("role");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return "";
	}

	
	/*******
	 * <p> Method: String getEmailAddressUsingCode (String code ) </p>
	 * 
	 * <p> Description: Get the email addressed associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 * @return the email address for the code or an empty string.
	 * 
	 */
	// For a given invitation code, return the associated email address of an empty string
	public String getEmailAddressUsingCode (String code ) {
	    String query = "SELECT emailAddress FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("emailAddress");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return "";
	}
	
	
	/*******
	 * <p> Method: void removeInvitationAfterUse(String code) </p>
	 * 
	 * <p> Description: Remove an invitation record once it is used.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 */
	// Remove an invitation using an email address once the user account has been setup
	public void removeInvitationAfterUse(String code) {
	    String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	        	int counter = rs.getInt(1);
	            // Only do the remove if the code is still in the invitation table
	        	if (counter > 0) {
        			query = "DELETE FROM InvitationCodes WHERE code = ?";
	        		try (PreparedStatement pstmt2 = connection.prepareStatement(query)) {
	        			pstmt2.setString(1, code);
	        			pstmt2.executeUpdate();
	        		}catch (SQLException e) {
	        	        e.printStackTrace();
	        	    }
	        	}
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return;
	}
	
	
	/*******
	 * <p> Method: String getFirstName(String username) </p>
	 * 
	 * <p> Description: Get the first name of a user given that user's username.</p>
	 * 
	 * @param postID is post we want to get the author of.
	 * 
	 * @return the first name of a user given that user's username 
	 *  
	 */
	// Get the First Name
	public String getAuthor(int postID) {
		String query = "SELECT author FROM postsDB WHERE postID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, postID);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("author");
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	/*******
	 * <p> Method: String getFirstName(String username) </p>
	 * 
	 * <p> Description: Get the first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the first name of a user given that user's username 
	 *  
	 */
	// Get the First Name
	public String getFirstName(String username) {
		String query = "SELECT firstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("firstName"); // Return the first name if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	

	/*******
	 * <p> Method: void updateFirstName(String username, String firstName) </p>
	 * 
	 * <p> Description: Update the first name of a user given that user's username and the new
	 *		first name.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @param firstName is the new first name for the user
	 *  
	 */
	// update the first name
	public void updateFirstName(String username, String firstName) {
	    String query = "UPDATE userDB SET firstName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, firstName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentFirstName = firstName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	
	/*******
	 * <p> Method: String getMiddleName(String username) </p>
	 * 
	 * <p> Description: Get the middle name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the middle name of a user given that user's username 
	 *  
	 */
	// get the middle name
	public String getMiddleName(String username) {
		String query = "SELECT MiddleName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("middleName"); // Return the middle name if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}

	
	/*******
	 * <p> Method: void updateMiddleName(String username, String middleName) </p>
	 * 
	 * <p> Description: Update the middle name of a user given that user's username and the new
	 * 		middle name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param middleName is the new middle name for the user
	 *  
	 */
	// update the middle name
	public void updateMiddleName(String username, String middleName) {
	    String query = "UPDATE userDB SET middleName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, middleName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentMiddleName = middleName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getLastName(String username) </p>
	 * 
	 * <p> Description: Get the last name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the last name of a user given that user's username 
	 *  
	 */
	// get he last name
	public String getLastName(String username) {
		String query = "SELECT LastName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("lastName"); // Return last name role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updateLastName(String username, String lastName) </p>
	 * 
	 * <p> Description: Update the middle name of a user given that user's username and the new
	 * 		middle name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param lastName is the new last name for the user
	 *  
	 */
	// update the last name
	public void updateLastName(String username, String lastName) {
	    String query = "UPDATE userDB SET lastName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, lastName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentLastName = lastName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getPreferredFirstName(String username) </p>
	 * 
	 * <p> Description: Get the preferred first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the preferred first name of a user given that user's username 
	 *  
	 */
	// get the preferred first name
	public String getPreferredFirstName(String username) {
		String query = "SELECT preferredFirstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("firstName"); // Return the preferred first name if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updatePreferredFirstName(String username, String preferredFirstName) </p>
	 * 
	 * <p> Description: Update the preferred first name of a user given that user's username and
	 * 		the new preferred first name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param preferredFirstName is the new preferred first name for the user
	 *  
	 */
	// update the preferred first name of the user
	public void updatePreferredFirstName(String username, String preferredFirstName) {
	    String query = "UPDATE userDB SET preferredFirstName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, preferredFirstName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentPreferredFirstName = preferredFirstName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getEmailAddress(String username) </p>
	 * 
	 * <p> Description: Get the email address of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the email address of a user given that user's username 
	 *  
	 */
	// get the email address
	public String getEmailAddress(String username) {
		String query = "SELECT emailAddress FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("emailAddress"); // Return the email address if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updateEmailAddress(String username, String emailAddress) </p>
	 * 
	 * <p> Description: Update the email address name of a user given that user's username and
	 * 		the new email address.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param emailAddress is the new preferred first name for the user
	 *  
	 */
	// update the email address
	public void updateEmailAddress(String username, String emailAddress) {
	    String query = "UPDATE userDB SET emailAddress = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, emailAddress);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentEmailAddress = emailAddress;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: boolean getUserAccountDetails(String username) </p>
	 * 
	 * <p> Description: Get all the attributes of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return true of the get is successful, else false
	 *  
	 */
	// get the attributes for a specified user
	public boolean getUserAccountDetails(String username) {
		String query = "SELECT * FROM userDB WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();			
	        if (!rs.next()) {
	            return false;
	        }
	    	currentUsername = rs.getString(2);
	    	currentPassword = rs.getString(3);
	    	currentFirstName = rs.getString(4);
	    	currentMiddleName = rs.getString(5);
	    	currentLastName = rs.getString(6);
	    	currentPreferredFirstName = rs.getString(7);
	    	currentEmailAddress = rs.getString(8);
	    	currentAdminRole = Boolean.parseBoolean(rs.getString("adminRole"));
	    	currentStaffRole = Boolean.parseBoolean(rs.getString("staffRole"));
	    	currentStudentRole = Boolean.parseBoolean(rs.getString("studentRole"));
			return true;
	    } catch (SQLException e) {
	    	e.printStackTrace();
			return false;
	    }
	}
	
	
	/*******
	 * <p> Method: boolean updateUserRole(String username, String role, String value) </p>
	 * 
	 * <p> Description: Update a specified role for a specified user's and set and update all the
	 * 		current user attributes.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param role is string that specifies the role to update
	 * 
	 * @param value is the string that specified TRUE or FALSE for the role
	 * 
	 * @return true if the update was successful, else false
	 *  
	 */
	// Update a users role
	public boolean updateUserRole(String username, String role, String value) {
		if (role.compareTo("Admin") == 0) {
			String query = "UPDATE userDB SET adminRole = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentAdminRole = true;
				else
					currentAdminRole = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Staff") == 0) {
			String query = "UPDATE userDB SET staffRole = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentStaffRole = true;
				else
					currentStaffRole = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Student") == 0) {
			String query = "UPDATE userDB SET studentRole = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentStudentRole = true;
				else
					currentStudentRole = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		return false;
	}
	
	
	// Attribute getters for the current user
	/*******
	 * <p> Method: String getCurrentUsername() </p>
	 * 
	 * <p> Description: Get the current user's username.</p>
	 * 
	 * @return the username value is returned
	 *  
	 */
	public String getCurrentUsername() { return currentUsername;};

	
	/*******
	 * <p> Method: String getCurrentPassword() </p>
	 * 
	 * <p> Description: Get the current user's password.</p>
	 * 
	 * @return the password value is returned
	 *  
	 */
	public String getCurrentPassword() { return currentPassword;};

	
	/*******
	 * <p> Method: String getCurrentFirstName() </p>
	 * 
	 * <p> Description: Get the current user's first name.</p>
	 * 
	 * @return the first name value is returned
	 *  
	 */
	public String getCurrentFirstName() { return currentFirstName;};

	
	/*******
	 * <p> Method: String getCurrentMiddleName() </p>
	 * 
	 * <p> Description: Get the current user's middle name.</p>
	 * 
	 * @return the middle name value is returned
	 *  
	 */
	public String getCurrentMiddleName() { return currentMiddleName;};

	
	/*******
	 * <p> Method: String getCurrentLastName() </p>
	 * 
	 * <p> Description: Get the current user's last name.</p>
	 * 
	 * @return the last name value is returned
	 *  
	 */
	public String getCurrentLastName() { return currentLastName;};

	
	/*******
	 * <p> Method: String getCurrentPreferredFirstName( </p>
	 * 
	 * <p> Description: Get the current user's preferred first name.</p>
	 * 
	 * @return the preferred first name value is returned
	 *  
	 */
	public String getCurrentPreferredFirstName() { return currentPreferredFirstName;};

	
	/*******
	 * <p> Method: String getCurrentEmailAddress() </p>
	 * 
	 * <p> Description: Get the current user's email address name.</p>
	 * 
	 * @return the email address value is returned
	 *  
	 */
	public String getCurrentEmailAddress() { return currentEmailAddress;};

	
	/*******
	 * <p> Method: boolean getCurrentAdminRole() </p>
	 * 
	 * <p> Description: Get the current user's Admin role attribute.</p>
	 * 
	 * @return true if this user plays an Admin role, else false
	 *  
	 */
	public boolean getCurrentAdminRole() { return currentAdminRole;};

	
	/*******
	 * <p> Method: boolean getCurrentStaffRole() </p>
	 * 
	 * <p> Description: Get the current user's Student role attribute.</p>
	 * 
	 * @return true if this user plays a Student role, else false
	 *  
	 */
	public boolean getCurrentStaffRole() { return currentStaffRole;};

	
	/*******
	 * <p> Method: boolean getCurrentStudentRole() </p>
	 * 
	 * <p> Description: Get the current user's Reviewer role attribute.</p>
	 * 
	 * @return true if this user plays a Reviewer role, else false
	 *  
	 */
	public boolean getCurrentStudentRole() { return currentStudentRole;};

	
	/*******
	 * <p> Debugging method</p>
	 * 
	 * <p> Description: Debugging method that dumps the database of the console.</p>
	 * 
	 * @throws SQLException if there is an issues accessing the database.
	 * 
	 */
	// Dumps the database.
	public void dump() throws SQLException {
		String query = "SELECT * FROM userDB";
		ResultSet resultSet = statement.executeQuery(query);
		ResultSetMetaData meta = resultSet.getMetaData();
		while (resultSet.next()) {
		for (int i = 0; i < meta.getColumnCount(); i++) {
		System.out.println(
		meta.getColumnLabel(i + 1) + ": " +
				resultSet.getString(i + 1));
		}
		System.out.println();
		}
		resultSet.close();
	}


	/*******
	 * <p> Method: void closeConnection()</p>
	 * 
	 * <p> Description: Closes the database statement and connection.</p>
	 * 
	 */
	// Closes the database statement and connection.
	public void closeConnection() {
		try{ 
			if(statement!=null) {
				statement.execute("SHUTDOWN");
				statement.close(); 
			}
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}
	
	/*******
	 * <p> Method: boolean isFirstAdminSetupComplete()</p>
	 * 
	 * <p> Description: Used by ViewUserUpdate to determine if the user will be able to proceed
	 * directly to home or if they will have to login again (first admin).</p>
	 * 
	 */
	
	public boolean isFirstAdminSetupComplete() {
	    String query = "SELECT first_admin_setup_complete FROM settings WHERE id = 1";
	    try (PreparedStatement pstmt = connection.prepareStatement(query);
	         ResultSet rs = pstmt.executeQuery()) {  // include ResultSet here
	        if (rs.next()) {
	            return rs.getBoolean("first_admin_setup_complete");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return false; // default to false if something goes wrong
	    
	}
	
	/*******
	 * <p> Method: void setFirstAdminSetupComplete()</p>
	 * 
	 * <p> Description: Used by ViewUserUpdate to set the variable when the first admin's setup is complete. </p>
	 * 
	 */
	
	public void setFirstAdminSetupComplete() {
	    String query = "UPDATE settings SET first_admin_setup_complete = TRUE WHERE id = 1";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.executeUpdate();
	        connection.commit();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	}

	/*******
	 * <p> Method: boolean setPasswordForUser(String username, String newPassword) </p>
	 * 
	 * <p> Description: Update the password for a given user. This method sets the
	 * password field for the specified username in the userDB table. It returns true
	 * if exactly one row was updated, false otherwise.</p>
	 * 
	 * @param username specifies the user whose password will be updated
	 * @param newPassword specifies the new password to be stored
	 * 
	 * @return true if the update was successful, else false
	 * 
	 */
	public boolean setPasswordForUser(String username, String newPassword) {
	    String query = "UPDATE userDB SET password = ? WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, newPassword);
	        pstmt.setString(2, username);
	        int updated = pstmt.executeUpdate();
	        return updated == 1;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public String generateNewOTP(String username) {
	    String password = UUID.randomUUID().toString().substring(0, 6);
	    String query = "UPDATE userDB SET oneTimePassword = ?, otpExpiration = ? WHERE userName = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, password);
	        pstmt.setTimestamp(2, Timestamp.from(Instant.now().plus(24, ChronoUnit.HOURS)));
	        pstmt.setString(3, username);
	        int rowsUpdated = pstmt.executeUpdate();
	        if (rowsUpdated != 1) {
	            System.out.println("Warning: OTP not set, user may not exist!");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return password;
	}

	/*******
	 * <p> Method: String getCurrentOneTimePassword() </p>
	 * 
	 * <p> Description: Return the currently stored one-time-password (OTP) for the
	 * user tracked as currentUsername. Returns null if none exists or if currentUsername
	 * is not set.</p>
	 * 
	 */
	public String getCurrentOneTimePassword(String username) {
	    String query = "SELECT oneTimePassword FROM userDB WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("oneTimePassword");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return null;
	}
	
	public Timestamp getOTPExpiration(String username) {
		String query = "SELECT otpExpiration FROM userDB WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getTimestamp("otpExpiration");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return Timestamp.from(Instant.now());
	}

	/*******
	 * <p> Method: void clearCurrentOneTimePassword() </p>
	 * 
	 * <p> Description: Clear the one-time password for the current user. The database
	 * schema includes a oneTimePassword column; this method clears that field for the
	 * currently tracked user (currentUsername). If currentUsername is null or the update
	 * fails, the method will quietly return after printing the exception.</p>
	 * 
	 */
	public void clearCurrentOneTimePassword(String username) {

	    String query = "UPDATE userDB SET oneTimePassword = NULL WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}



	
}


