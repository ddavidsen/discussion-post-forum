package guiStaff;

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

public class ControllerStaffHome {

	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */
	
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	
	
	protected static void viewPosts() {
		boolean search = ViewStaffHome.showChoiceAlert();
		List<String> posts;
		
		// Choose between viewing all or search for specific posts
		if (search) {
			String keyword = ViewStaffHome.getKeyword();
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
		ViewCreatePostReply.displayCreatePostReply(ViewStaffHome.theStage, 
				ViewStaffHome.theUser);
	}
	
	protected static void editPostReply () {
		ViewEditPostReply.displayEditPostReply(ViewStaffHome.theStage, 
				ViewStaffHome.theUser);
	}
	
	protected static void deletePostReply () {
		ViewDeletePostReply.displayDeletePostReply(ViewStaffHome.theStage, 
				ViewStaffHome.theUser);
	}
	
	protected static void manageThreads () {
		ViewManageThreads.displayManageThreads(ViewStaffHome.theStage, 
				ViewStaffHome.theUser);
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
		ViewManageParameters.displayManageParameters(ViewStaffHome.theStage, 
				ViewStaffHome.theUser);
		
	}
	
	protected static void viewGrades() {
		List<String> grades = new ArrayList<>();
		try {
			grades = theDatabase.getGradesList();
		}
		catch (SQLException e) {
			System.err.println("*** ERROR *** Database error: " + e.getMessage());
            e.printStackTrace();
            System.exit(0);
		}
		
		if (grades.size() == 0) {
			System.out.println("No posts found.");
			
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
		ViewManageGrades.displayManageGrades(ViewStaffHome.theStage, 
				ViewStaffHome.theUser);
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
	
	protected static void openRequest() {
		ViewOpenCloseRequests.displayOpenCloseRequests(ViewStaffHome.theStage, 
				ViewStaffHome.theUser);
	}

	
 	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: This method logs out the current user and proceeds to the normal login
	 * page where existing users can log in or potential new users with a invitation code can
	 * start the process of setting up an account. </p>
	 * 
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewStaffHome.theStage);
	}
	
	
	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program.  It leaves the
	 * database in a state where the normal login page will be displayed when the application is
	 * restarted.</p>
	 * 
	 */	
	protected static void performQuit() {
		System.exit(0);
	}
}
