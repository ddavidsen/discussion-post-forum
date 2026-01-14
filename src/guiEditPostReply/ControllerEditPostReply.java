package guiEditPostReply;

import java.sql.SQLException;

import applicationMain.FoundationsMain;
import database.Database;

import entityClasses.User;
import javafx.stage.Stage;

public class ControllerEditPostReply {
	
	/*-********************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;		
	protected static Stage theStage = ViewEditPostReply.theStage;		
	protected static User theUser = applicationMain.FoundationsMain.activeUser;
	protected static int theRole = applicationMain.FoundationsMain.activeHomePage;
	
	/**
	 * <p> Method: performEditPostReply() </p>
	 *
	 * <p> Description: This method removes an existing role to the list of role in the ComboBox
	 * select list. </p>
	 *
	 */

	protected static void performEditPostReply() {
		String newBody = ViewEditPostReply.text_NewBody.getText();
		int postID;
		
		// If input is not a number, reject it
		try {
		    postID = Integer.parseInt(ViewEditPostReply.text_PostID.getText());
		} catch (NumberFormatException e) {
		    ViewEditPostReply.showAlert("Invalid PostID");
		    return;
		}
		
		// If input is not a valid postID, reject it
		try {
			if (!theDatabase.postExists(postID)) {
				ViewEditPostReply.showAlert("Invalid PostID");
				return;
			}
		} catch (SQLException e) {
            System.err.println("*** ERROR *** Database error: " + e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }
		
		// if input is empty, reject it
		if (newBody.isEmpty()) {
			ViewEditPostReply.showAlert("Body cannot be empty");
			return;
		}
		
		System.out.println("Author: [" + theDatabase.getAuthor(postID) + "]");
		System.out.println("User: [" + FoundationsMain.activeUser.getUserName() + "]");
		System.out.println("Role: [" + FoundationsMain.activeHomePage + "]");
		// If the user is not an admin, and the post isnt theirs, do not allow it
		if ((!theDatabase.getAuthor(postID).equals(FoundationsMain.activeUser.getUserName())) && 
				(FoundationsMain.activeHomePage != 1)) {
			ViewEditPostReply.showAlert("You cannot edit a post that isnt yours!");
			return;
		}
		
		
		try {
			theDatabase.editPostReply(postID, newBody);
			ViewEditPostReply.showAlert("Post/Reply edited successfully!");
			ViewEditPostReply.text_NewBody.clear();
			ViewEditPostReply.text_PostID.clear();
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
		User theUser = applicationMain.FoundationsMain.activeUser;
		theRole = applicationMain.FoundationsMain.activeHomePage;
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
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewEditPostReply.theStage);
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