package guiCreatePostReply;

import java.sql.SQLException;

import database.Database;

import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.User;
import javafx.stage.Stage;

public class ControllerCreatePostReply {
	
	/*-********************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;		
	protected static Stage theStage = ViewCreatePostReply.theStage;		
	protected static User theUser = ViewCreatePostReply.theUser;			
	
	
	//CHANGE THIS
	//If "Post" button is selected, populate the page one way
	//If "Reply" button is selected, populate the page another way
	/**********
	 * <p> Method: repaintTheWindow() </p>
	 * 
	 * <p> Description: This method determines the current state of the window and then establishes
	 * the appropriate list of widgets in the Pane to show the proper set of current values. </p>
	 * 
	 */
	protected static void repaintTheWindow() {
		// Clear what had been displayed
		ViewCreatePostReply.theRootPane.getChildren().clear();
		ViewCreatePostReply.theSelectedOption = 
				(String) ViewCreatePostReply.combobox_SelectPostorReply.getValue();
		
		// Determine which of the two views to show to the user
		if (ViewCreatePostReply.theSelectedOption.compareTo("Post") == 0) {
			// Only show the request to select a user to be updated and the ComboBox
			ViewCreatePostReply.theRootPane.getChildren().addAll(
					ViewCreatePostReply.label_PageTitle, ViewCreatePostReply.label_UserDetails, 
					ViewCreatePostReply.button_UpdateThisUser, ViewCreatePostReply.line_Separator1,
					ViewCreatePostReply.label_SelectPostorReply, ViewCreatePostReply.combobox_SelectPostorReply, 
					ViewCreatePostReply.label_SelectThread, ViewCreatePostReply.combobox_SelectThread,
					ViewCreatePostReply.label_PostTitle, ViewCreatePostReply.text_PostTitle,
					ViewCreatePostReply.label_PostBody, ViewCreatePostReply.text_PostBody,
					ViewCreatePostReply.button_CreatePost,
					ViewCreatePostReply.line_Separator4, ViewCreatePostReply.button_Return,
					ViewCreatePostReply.button_Logout, ViewCreatePostReply.button_Quit);
		}
		else {
			// Show all the fields as there is a selected user (as opposed to the prompt)
			ViewCreatePostReply.theRootPane.getChildren().addAll(
					ViewCreatePostReply.label_PageTitle, ViewCreatePostReply.label_UserDetails,
					ViewCreatePostReply.button_UpdateThisUser, ViewCreatePostReply.line_Separator1,
					ViewCreatePostReply.label_SelectPostorReply,
					ViewCreatePostReply.combobox_SelectPostorReply, 
					ViewCreatePostReply.label_PostID, ViewCreatePostReply.text_PostID,
					ViewCreatePostReply.label_ReplyBody, ViewCreatePostReply.text_ReplyBody,
					ViewCreatePostReply.button_CreateReply,
					ViewCreatePostReply.line_Separator4, 
					ViewCreatePostReply.button_Return,
					ViewCreatePostReply.button_Logout,
					ViewCreatePostReply.button_Quit);
		}

		
		// Set the title for the window
		ViewCreatePostReply.theStage.setTitle("Admin Opertaions Page");
		ViewCreatePostReply.theStage.setScene(ViewCreatePostReply.theCreatePostReplyScene);
		ViewCreatePostReply.theStage.show();
	}
	
	
	/**********
	 * <p> Method: performAddRole() </p>
	 * 
	 * <p> Description: This method adds a new role to the list of role in the ComboBox select
	 * list. </p>
	 * 
	 */
	public static void performCreatePost() {
		String author = ViewCreatePostReply.theUser.getUserName();
		String thread = ViewCreatePostReply.combobox_SelectThread.getValue();
		String title = ViewCreatePostReply.text_PostTitle.getText();
		String body = ViewCreatePostReply.text_PostBody.getText();
		
		// If inputs are empty, reject them
		if (title.isEmpty()) {
			ViewCreatePostReply.showAlert("Title cannot be empty");
			return;
		}
		
		if (body.isEmpty()) {
			ViewCreatePostReply.showAlert("Body cannot be empty");
			return;
		}
		
		Post newPost = new Post(author, title, body, thread);
		
		try {
			theDatabase.insertPost(newPost);
			ViewCreatePostReply.showAlert("Post created successfully!");
			ViewCreatePostReply.text_PostTitle.clear();
			ViewCreatePostReply.text_PostBody.clear();
			performReturn();
		}
		catch (SQLException e) {
            System.err.println("*** ERROR *** Database error: " + e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }
		
	}
	
	
	/**********
	 * <p> Method: performRemoveRole() </p>
	 * 
	 * <p> Description: This method removes an existing role to the list of role in the ComboBox
	 * select list. </p>
	 * 
	 */
	protected static void performCreateReply() {
		String author = ViewCreatePostReply.theUser.getUserName();
		String body = ViewCreatePostReply.text_ReplyBody.getText();
		int parentPostID;
		
		// If input is not a number, reject it
		try {
		    parentPostID = Integer.parseInt(ViewCreatePostReply.text_PostID.getText());
		} catch (NumberFormatException e) {
		    ViewCreatePostReply.showAlert("Invalid PostID");
		    return;
		}
		
		// If input is not an existing postID, reject it
		try {
			if (!theDatabase.postExists(parentPostID)) {
				ViewCreatePostReply.showAlert("Invalid PostID");
				return;
			}
		} catch (SQLException e) {
            System.err.println("*** ERROR *** Database error: " + e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }
		
		// in input is empty, reject it
		if (body.isEmpty()) {
			ViewCreatePostReply.showAlert("Body cannot be empty");
			return;
		}
		
		Reply newReply = new Reply(author, body, parentPostID);
		
		try {
			theDatabase.insertReply(newReply);
			ViewCreatePostReply.showAlert("Reply created successfully!");
			ViewCreatePostReply.text_ReplyBody.clear();
			ViewCreatePostReply.text_PostID.clear();
			performReturn();
		}
		catch (SQLException e) {
            System.err.println("*** ERROR *** Database error: " + e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }
	}
	
	
	/**********
	 * <p> Method: performReturn() </p>
	 * 
	 * <p> Description: This method returns the user (who must be an Admin as only admins are the
	 * only users who have access to this page) to the Admin Home page. </p>
	 * 
	 */
	protected static void performReturn() {
		int theRole = applicationMain.FoundationsMain.activeHomePage;
		switch (theRole) {
		case 1:
			guiAdminHome.ViewAdminHome.displayAdminHome(theStage, theUser);
			break;
		case 2:
			guiStaff.ViewStaffHome.displayStaffHome(theStage, theUser);
			break;
		case 3:
			guiStudent.ViewStudentHome.displayStudentHome(theStage, theUser);
			break;
		default:
			System.out.println("*** ERROR *** performReturn has an invalid role: " + theRole);
			System.exit(0);
		}
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
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewCreatePostReply.theStage);
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