package guiDeleteUser;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import database.Database;
import javafx.scene.control.ButtonType;
import javafx.collections.FXCollections;

public class ControllerDeleteUser {
	
	/*-********************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;		

	
	/**********
	 * <p> Method: doSelectUser() </p>
	 * 
	 * <p> Description: This method uses the ComboBox widget, fetches which item in the ComboBox
	 * was selected (a user in this case), and establishes that user and the current user, setting
	 * easily accessible values without needing to do a query. </p>
	 * 
	 */
	protected static void doSelectUser() {
		ViewDeleteUser.theSelectedUser = 
				(String) ViewDeleteUser.combobox_SelectUser.getValue();
		theDatabase.getUserAccountDetails(ViewDeleteUser.theSelectedUser);
		repaintTheWindow();
	}
	
	
	/**********
	 * <p> Method: repaintTheWindow() </p>
	 * 
	 * <p> Description: This method determines the current state of the window and then establishes
	 * the appropriate list of widgets in the Pane to show the proper set of current values. </p>
	 * 
	 */
	protected static void repaintTheWindow() {
		// Clear what had been displayed
		ViewDeleteUser.theRootPane.getChildren().clear();
		
		// Determine which of the two views to show to the user
		if (ViewDeleteUser.theSelectedUser.compareTo("<Select a User>") == 0) {
			// Only show the request to select a user to be updated and the ComboBox
			ViewDeleteUser.theRootPane.getChildren().addAll(
					ViewDeleteUser.label_PageTitle, ViewDeleteUser.label_UserDetails, 
					ViewDeleteUser.button_UpdateThisUser, ViewDeleteUser.line_Separator1,
					ViewDeleteUser.label_SelectUser, ViewDeleteUser.combobox_SelectUser, 
					ViewDeleteUser.line_Separator4, ViewDeleteUser.button_Return,
					ViewDeleteUser.button_Logout, ViewDeleteUser.button_Quit);
		}
		else {
			// Show all the fields as there is a selected user (as opposed to the prompt)
			ViewDeleteUser.theRootPane.getChildren().addAll(
					ViewDeleteUser.label_PageTitle, ViewDeleteUser.label_UserDetails,
					ViewDeleteUser.button_UpdateThisUser, ViewDeleteUser.line_Separator1,
					ViewDeleteUser.label_SelectUser,
					ViewDeleteUser.combobox_SelectUser, 
					ViewDeleteUser.button_DeleteUser,
					ViewDeleteUser.line_Separator4, 
					ViewDeleteUser.button_Return,
					ViewDeleteUser.button_Logout,
					ViewDeleteUser.button_Quit);
		}
		
		// Add the list of widgets to the stage and show it
		
		// Set the title for the window
		ViewDeleteUser.theStage.setTitle("CSE 360 Foundation Code: Admin Opertaions Page");
		ViewDeleteUser.theStage.setScene(ViewDeleteUser.theRemoveUserScene);
		ViewDeleteUser.theStage.show();
	}
	
	/**********
	 * <p> Method: refreshUserList() </p>
	 * 
	 * <p> Description: Helper method to ensure the userList gets updated upon an added or removed user. </p>
	 * 
	 */
	protected static void refreshUserList() {
		List<String> userList = theDatabase.getUserList();
	    ViewDeleteUser.combobox_SelectUser.setItems(
	        FXCollections.observableArrayList(userList)
	    );

	    if (!userList.isEmpty()) {
	        ViewDeleteUser.combobox_SelectUser.getSelectionModel().select(0);
	        ViewDeleteUser.theSelectedUser = ViewDeleteUser.combobox_SelectUser.getSelectionModel().getSelectedItem();
	    } else {
	        ViewDeleteUser.theSelectedUser = "<Select a User>";
	    }
	}
	
	/**********
	 * <p> Method: performDeleteUser() </p>
	 * 
	 * <p> Description: This method deletes the selected user given they are not the current user </p>
	 * 
	 */
	protected static void performDeleteUser() {
		if (ViewDeleteUser.theSelectedUser.equals(ViewDeleteUser.theUser.getUserName())) {
			ViewDeleteUser.cantDeleteAlert.setTitle("*** ERROR ***");
			ViewDeleteUser.cantDeleteAlert.setHeaderText("Delete User Issue");
			ViewDeleteUser.cantDeleteAlert.setContentText("You cannot delete yourself!");
			ViewDeleteUser.cantDeleteAlert.showAndWait();
		}
		
		else {
			ViewDeleteUser.deleteUserAlert.setTitle("*** CONFIRM ***");
			ViewDeleteUser.deleteUserAlert.setHeaderText("Confirm Delete User");
			ViewDeleteUser.deleteUserAlert.setContentText("Are you sure you want to delete the selected user?");
			Optional<ButtonType> result = ViewDeleteUser.deleteUserAlert.showAndWait();
			
			if (result.isPresent() && result.get() == ButtonType.YES) {
				try {
			        theDatabase.deleteUser(ViewDeleteUser.theSelectedUser);

			        refreshUserList();
			        repaintTheWindow();

			    } catch (SQLException e) {
			        e.printStackTrace();
			    }
			}
			
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
		guiAdminHome.ViewAdminHome.displayAdminHome(ViewDeleteUser.theStage,
				ViewDeleteUser.theUser);
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
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewDeleteUser.theStage);
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