package guiAdminHome;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.Database;
import guiCreatePostReply.ViewCreatePostReply;
import guiDeletePostReply.ViewDeletePostReply;
import guiEditPostReply.ViewEditPostReply;
import guiManageGrades.ViewManageGrades;
import guiManageParameters.ViewManageParameters;
import guiManageThreads.ViewManageThreads;
import guiOpenCloseRequests.ViewOpenCloseRequests;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

/*******
 * <p> Title: GUIAdminHomePage Class. </p>
 * 
 * <p> Description: The Java/FX-based Admin Home Page.  This class provides the controller actions
 * basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page contains a number of buttons that have not yet been implemented.  WHen those buttons
 * are pressed, an alert pops up to tell the user that the function associated with the button has
 * not been implemented. Also, be aware that What has been implemented may not work the way the
 * final product requires and there maybe defects in this code.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * @author Diana Davidsen
 * @author Lynn Robert Carter
 * @author Jessica Lara Valdez
 *  
 */

public class ControllerAdminHome {
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	
	/**********
	 * <p> 
	 * 
	 * Title: performInvitation () Method. </p>
	 * 
	 * <p> Description: Protected method to send an email inviting a potential user to establish
	 * an account and a specific role. </p>
	 */
	protected static void performInvitation () {
		// Verify that the email address is valid - If not alert the user and return
		String emailAddress = ViewAdminHome.text_InvitationEmailAddress.getText();
		if (invalidEmailAddress(emailAddress)) {
			return;
		}
		
		// Check to ensure that we are not sending a second message with a new invitation code to
		// the same email address.  
		if (theDatabase.emailaddressHasBeenUsed(emailAddress)) {
			ViewAdminHome.alertEmailError.setContentText(
					"An invitation has already been sent to this email address.");
			ViewAdminHome.alertEmailError.showAndWait();
			return;
		}
		
		// Inform the user that the invitation has been sent and display the invitation code
		String theSelectedRole = (String) ViewAdminHome.combobox_SelectRole.getValue();
		String invitationCode = theDatabase.generateInvitationCode(emailAddress,
				theSelectedRole);
		String msg = "Code: " + invitationCode + " for role " + theSelectedRole + 
				" was sent to: " + emailAddress;
		System.out.println(msg);
		ViewAdminHome.alertEmailSent.setContentText(msg);
		ViewAdminHome.alertEmailSent.showAndWait();
		
		// Update the Admin Home pages status
		ViewAdminHome.text_InvitationEmailAddress.setText("");
		ViewAdminHome.label_NumberOfInvitations.setText("Number of outstanding invitations: " + 
				theDatabase.getNumberOfInvitations());
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: manageInvitations () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void manageInvitations () {
		System.out.println("\n*** WARNING ***: Manage Invitations Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.setTitle("*** WARNING ***");
		ViewAdminHome.alertNotImplemented.setHeaderText("Manage Invitations Issue");
		ViewAdminHome.alertNotImplemented.setContentText("Manage Invitations Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.showAndWait();
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: setOnetimePassword () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void setOnetimePassword () {	
		// Verify that the username is valid - If not alert the user and return
		String username = ViewAdminHome.getOTPUsername();
		
		if (!theDatabase.doesUserExist(username)) {
			ViewAdminHome.alertNotImplemented.setTitle("*** ERROR ***");
			ViewAdminHome.alertNotImplemented.setHeaderText("OTP Issue");
			ViewAdminHome.alertNotImplemented.setContentText("Invalid username");
			ViewAdminHome.alertNotImplemented.showAndWait();
			return;
		}
		
		String password = theDatabase.generateNewOTP(username);
		
		String msg = "Password: " + password +  " was sent to: " + username;
		System.out.println(msg);
		
		ViewAdminHome.alertEmailSent.setTitle("Password Sent");
		ViewAdminHome.alertEmailSent.setHeaderText("New Password!");
		ViewAdminHome.alertEmailSent.setContentText(msg);
		ViewAdminHome.alertEmailSent.showAndWait();
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: deleteUser () Method. </p>
	 * 
	 * <p> Description: This method can only be used by an admin to remove any user except themselves. 
	 * It calls the newly added class for DeleteUser</p>
	 */
	protected static void deleteUser() {
		guiDeleteUser.ViewDeleteUser.displayDeleteUser(ViewAdminHome.theStage, 
				ViewAdminHome.theUser);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: listUsers () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void listusers() {
		List<String> users = theDatabase.getDetailedUserList();
		if (users.size() == 0) {
			System.out.println("No users found.");
			
			Alert useralert = new Alert(Alert.AlertType.INFORMATION);
			useralert.setTitle("All Users");
			useralert.setHeaderText("No Users");
			useralert.setContentText("There are no accounts in the system.");
			useralert.showAndWait();
		} else {
			String list = "User List Format:\n" 
					+ "Username, First Name, Last Name, Email Address, Admin?, Staff?, Student?\n"
					+ "***If a detail is empty, it has not been setup by that user yet.\n";
			for (String u : users) {
				System.out.println(u);
				list += u + "\n";
			}
			
			TextArea textArea = new TextArea(list.toString());
		    textArea.setEditable(false);
		    textArea.setWrapText(true);
		    textArea.setPrefWidth(600);
		    textArea.setPrefHeight(400);
		    textArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 13;");
			
			Alert useralert = new Alert(Alert.AlertType.INFORMATION);
			useralert.setTitle("All Users");
			useralert.setHeaderText("Registered Users");
			useralert.getDialogPane().setContent(textArea);
			useralert.showAndWait();
		}
				
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: addRemoveRoles () Method. </p>
	 * 
	 * <p> Description: Protected method that allows an admin to add and remove roles for any of
	 * the users currently in the system.  This is done by invoking the AddRemoveRoles Page. There
	 * is no need to specify the home page for the return as this can only be initiated by and
	 * Admin.</p>
	 */
	protected static void addRemoveRoles() {
		guiAddRemoveRoles.ViewAddRemoveRoles.displayAddRemoveRoles(ViewAdminHome.theStage, 
				ViewAdminHome.theUser);
	}
	
	protected static void viewPosts() {
		boolean search = ViewAdminHome.showChoiceAlert();
		List<String> posts;
		
		// Choose between viewing all or search for specific posts
		if (search) {
			String keyword = ViewAdminHome.getKeyword();
			// Handle empty input
	        if (keyword == null) {
	            return;
	        }
	        posts = theDatabase.getPostListKeyword(keyword);
		}
		else {
			posts = theDatabase.getPostList();
		}
		
		if (posts.size() == 0) {
			System.out.println("No posts found.");
			
			Alert postsalert = new Alert(Alert.AlertType.INFORMATION);
			postsalert.setTitle("All Posts");
			postsalert.setHeaderText("No Posts");
			postsalert.setContentText("There are no posts in the system.");
			postsalert.showAndWait();
		} else {
			String list = "Post Format:\n" 
					+ "Thread, PostID, Original Post ID, # of Replies, Author, Title, Body Text, Read?\n"
					+ "***If Thread is null, the post is a reply.\n"
					+ "***If a reply's Parent Post ID is 0, the original post was deleted.\n";
			for (String u : posts) {
				System.out.println(u);
				list += u + "\n";
			}
			
			TextArea textArea = new TextArea(list.toString());
		    textArea.setEditable(false);
		    textArea.setWrapText(true);
		    textArea.setPrefWidth(600);
		    textArea.setPrefHeight(400);
		    textArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 13;");

		    // Use Alert with custom content
		    Alert postsAlert = new Alert(Alert.AlertType.INFORMATION);
		    postsAlert.setTitle("All Posts");
		    postsAlert.setHeaderText("List of Posts");
		    postsAlert.getDialogPane().setContent(textArea);
		    postsAlert.showAndWait();
		}
	}
	
	protected static void createPostReply () {
		ViewCreatePostReply.displayCreatePostReply(ViewAdminHome.theStage, 
				ViewAdminHome.theUser);
	}
	
	protected static void editPostReply () {
		ViewEditPostReply.displayEditPostReply(ViewAdminHome.theStage, 
				ViewAdminHome.theUser);
	}
	
	protected static void deletePostReply () {
		ViewDeletePostReply.displayDeletePostReply(ViewAdminHome.theStage, 
				ViewAdminHome.theUser);
	}
	
	protected static void manageThreads () {
		ViewManageThreads.displayManageThreads(ViewAdminHome.theStage, 
				ViewAdminHome.theUser);
	}
	
	protected static void viewParameters() {
		List<String> params = theDatabase.getParamList();
		if (params.size() == 0) {
			System.out.println("No parameters found.");
			
			Alert useralert = new Alert(Alert.AlertType.INFORMATION);
			useralert.setTitle("All Parameters");
			useralert.setHeaderText(null);
			useralert.setContentText("There are no parameters in the system.");
			useralert.showAndWait();
		} else {
			String list = "";
			for (String u : params) {
				System.out.println(u);
				list += u + "\n";
			}
			
			Alert paramalert = new Alert(Alert.AlertType.INFORMATION);
			paramalert.setTitle("All Parameters");
			paramalert.setHeaderText(null);
			paramalert.setContentText(list);
			paramalert.showAndWait();
		}
	}
	
	protected static void manageParameters() {
		ViewManageParameters.displayManageParameters(ViewAdminHome.theStage, 
				ViewAdminHome.theUser);
		
	}
	
	protected static void viewGrades() {
		List<String> grades = new ArrayList<String>();
		try {
			grades = theDatabase.getGradesList();
		}
		catch (SQLException e) {
			System.err.println("*** ERROR *** Database error: " + e.getMessage());
            e.printStackTrace();
            System.exit(0);
		}
		
		if (grades.size() == 0) {
			System.out.println("No grades found.");
			
			Alert postsalert = new Alert(Alert.AlertType.INFORMATION);
			postsalert.setTitle("All Grades");
			postsalert.setHeaderText("No Grades");
			postsalert.setContentText("There are no grades in the system yet.");
			postsalert.showAndWait();
		} else {
			String list = "Grade Format:\n" 
					+ "GradeID, ID of the graded post, Student, Grader, Score (0-100), Optional Comments\n";
			for (String u :grades) {
				System.out.println(u);
				list += u + "\n";
			}
			
			TextArea textArea = new TextArea(list.toString());
		    textArea.setEditable(false);
		    textArea.setWrapText(true);
		    textArea.setPrefWidth(600);
		    textArea.setPrefHeight(400);
		    textArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 13;");

		    // Use Alert with custom content
		    Alert postsAlert = new Alert(Alert.AlertType.INFORMATION);
		    postsAlert.setTitle("All Grades");
		    postsAlert.setHeaderText("List of All Grades");
		    postsAlert.getDialogPane().setContent(textArea);
		    postsAlert.showAndWait();
		}
	}
	
	protected static void manageGrades() {
		ViewManageGrades.displayManageGrades(ViewAdminHome.theStage, 
				ViewAdminHome.theUser);
	}
	
	protected static void viewAdminRequests() {
		List<String> requests = theDatabase.getRequestsList();
		if (requests.size() == 0) {
			System.out.println("No requests found.");
			
			Alert postsalert = new Alert(Alert.AlertType.INFORMATION);
			postsalert.setTitle("All Requests");
			postsalert.setHeaderText("No Admin Requests");
			postsalert.setContentText("There are no admin requests in the system yet.");
			postsalert.showAndWait();
		} else {
			String list = "Admin Request List Format:\n" 
					+ "RequestID, Initial Requester, Request Description, Action Taken by Admin, Status\n"
					+ "***If request is new, action taken will be null.\n";
			for (String u : requests) {
				System.out.println(u);
				list += u + "\n";
			}
			
			TextArea textArea = new TextArea(list.toString());
		    textArea.setEditable(false);
		    textArea.setWrapText(true);
		    textArea.setPrefWidth(600);
		    textArea.setPrefHeight(400);
		    textArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 13;");

		    // Use Alert with custom content
		    Alert postsAlert = new Alert(Alert.AlertType.INFORMATION);
		    postsAlert.setTitle("All Requests");
		    postsAlert.setHeaderText("List of All Admin Requests");
		    postsAlert.getDialogPane().setContent(textArea);
		    postsAlert.showAndWait();
		}
	}
	
	protected static void closeRequest() {
		ViewOpenCloseRequests.displayOpenCloseRequests(ViewAdminHome.theStage, 
				ViewAdminHome.theUser);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: invalidEmailAddress () Method. </p>
	 * 
	 * <p> Description: Protected method that is intended to check an email address before it is
	 * used to reduce errors.  The code currently only checks to see that the email address is not
	 * empty.  In the future, a syntactic check must be performed and maybe there is a way to check
	 * if a properly email address is active.</p>
	 * 
	 * @param emailAddress	This String holds what is expected to be an email address
	 */
	protected static boolean invalidEmailAddress(String emailAddress) {
		if (emailAddress.length() == 0) {
			ViewAdminHome.alertEmailError.setContentText(
					"Correct the email address and try again.");
			ViewAdminHome.alertEmailError.showAndWait();
			return true;
		}
		return false;
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performLogout () Method. </p>
	 * 
	 * <p> Description: Protected method that logs this user out of the system and returns to the
	 * login page for future use.</p>
	 */
	public static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewAdminHome.theStage);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performQuit () Method. </p>
	 * 
	 * <p> Description: Protected method that gracefully terminates the execution of the program.
	 * </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}
