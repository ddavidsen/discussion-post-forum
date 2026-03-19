package guiManageThreads;

import java.sql.SQLException;
import java.util.Optional;

import database.Database;
import entityClasses.User;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class ControllerManageThreads {
	
	/*-********************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;		
	protected static Stage theStage = ViewManageThreads.theStage;		
	protected static User theUser = ViewManageThreads.theUser;
	
	
	/**********
	 * <p> Method: repaintTheWindow() </p>
	 * 
	 * <p> Description: This method determines the current state of the window and then establishes
	 * the appropriate list of widgets in the Pane to show the proper set of current values. </p>
	 * 
	 */
	protected static void repaintTheWindow() {
		// Clear what had been displayed
		ViewManageThreads.theRootPane.getChildren().clear();
		ViewManageThreads.theSelectedOption = (String) ViewManageThreads.combobox_SelectAction.getValue();
		
		ViewManageThreads.threadList.setAll(theDatabase.getThreadList());
		ViewManageThreads.combobox_SelectThread.getSelectionModel().select(0);
		
		// Determine which of the views to show to the user
		if (ViewManageThreads.theSelectedOption.compareTo("Create") == 0) {
			ViewManageThreads.theRootPane.getChildren().addAll(
					ViewManageThreads.label_PageTitle, ViewManageThreads.label_UserDetails, 
					ViewManageThreads.button_UpdateThisUser, ViewManageThreads.line_Separator1,
					ViewManageThreads.label_SelectAction, ViewManageThreads.combobox_SelectAction, 
					ViewManageThreads.line_Separator4, ViewManageThreads.button_Return,
					ViewManageThreads.button_Logout, ViewManageThreads.button_Quit,
					ViewManageThreads.label_EnterName, ViewManageThreads.text_EnterName,
					ViewManageThreads.button_CreateThread);
		}
		
		else if (ViewManageThreads.theSelectedOption.compareTo("Edit") == 0) {
			ViewManageThreads.theRootPane.getChildren().addAll(
					ViewManageThreads.label_PageTitle, ViewManageThreads.label_UserDetails, 
					ViewManageThreads.button_UpdateThisUser, ViewManageThreads.line_Separator1,
					ViewManageThreads.label_SelectAction, ViewManageThreads.combobox_SelectAction, 
					ViewManageThreads.line_Separator4, ViewManageThreads.button_Return,
					ViewManageThreads.button_Logout, ViewManageThreads.button_Quit,
					ViewManageThreads.label_SelectEditThread, ViewManageThreads.combobox_SelectThread,
					ViewManageThreads.label_EnterNewName, ViewManageThreads.text_EnterNewName,
					ViewManageThreads.button_EditThread);
		}
		
		else {
			ViewManageThreads.theRootPane.getChildren().addAll(
					ViewManageThreads.label_PageTitle, ViewManageThreads.label_UserDetails, 
					ViewManageThreads.button_UpdateThisUser, ViewManageThreads.line_Separator1,
					ViewManageThreads.label_SelectAction, ViewManageThreads.combobox_SelectAction, 
					ViewManageThreads.line_Separator4, ViewManageThreads.button_Return,
					ViewManageThreads.button_Logout, ViewManageThreads.button_Quit,
					ViewManageThreads.label_SelectDeleteThread, ViewManageThreads.combobox_SelectThread,
					ViewManageThreads.button_DeleteThread);
		}
		
		// Add the list of widgets to the stage and show it
		
		// Set the title for the window
		ViewManageThreads.theStage.setTitle("Admin Operations Page");
		ViewManageThreads.theStage.setScene(ViewManageThreads.theAddRemoveRolesScene);
		ViewManageThreads.theStage.show();
	}
	
	
	/**********
	 * <p> Method: performCreateThread() </p>
	 * 
	 * <p> Description: This method create a new thread
	 * </p>
	 * 
	 */
	protected static void performCreateThread() {
		String newThreadName = (String) ViewManageThreads.text_EnterName.getText();
		
		// If thread with this name exists, do not allow another one with the same name
		try {
			if (theDatabase.threadExists(newThreadName)) {
				ViewManageThreads.alert.setTitle("*** ERROR ***");
				ViewManageThreads.alert.setHeaderText("Create Thread Issue");
				ViewManageThreads.alert.setContentText("This thread already exists!");
				ViewManageThreads.alert.showAndWait();
				return;
			}
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error: " + e.getMessage());
            e.printStackTrace();
            System.exit(0);
		}
		
		// If input is empty, reject it
		if (newThreadName.isEmpty()) {
			ViewManageThreads.alert.setTitle("*** ERROR ***");
			ViewManageThreads.alert.setHeaderText("Create Thread Issue");
			ViewManageThreads.alert.setContentText("Name cannot be empty!");
			ViewManageThreads.alert.showAndWait();
			return;
		}
		
		else {
			try {
				theDatabase.insertThread(newThreadName);
				ViewManageThreads.alert.setTitle("Success");
				ViewManageThreads.alert.setHeaderText(null);
				ViewManageThreads.alert.setContentText("Thread created successfully!");
				ViewManageThreads.alert.showAndWait();
				ViewManageThreads.text_EnterName.clear();
				performReturn();
			}
			catch (SQLException e) {
				System.err.println("*** ERROR *** Database error: " + e.getMessage());
	            e.printStackTrace();
	            System.exit(0);
			}
		}
	}
	
	
	/**********
	 * <p> Method: performEditThread() </p>
	 * 
	 * <p> Description: This method allows for the editing of threads from the
	 * select list. </p>
	 * 
	 */
	protected static void performEditThread() {
		// Determine which item in the ComboBox list was selected
		ViewManageThreads.theSelectedThread = (String) ViewManageThreads.
				combobox_SelectThread.getValue();
		
		//If the current admin has selected the General Thread, and they try to remove the General
		//Thread it will present with an error messsage
		if (ViewManageThreads.theSelectedThread.equals("General")) {
			ViewManageThreads.alert.setTitle("*** ERROR ***");
			ViewManageThreads.alert.setHeaderText("Edit Thread Issue");
			ViewManageThreads.alert.setContentText("You cannot edit the General thread!");
			ViewManageThreads.alert.showAndWait();
			return;
		}
		
		else {
			//If not the general thread then the selected thread can be changed 
			try {
				theDatabase.editThread(ViewManageThreads.theSelectedThread, (String) ViewManageThreads.text_EnterNewName.getText());
				ViewManageThreads.alert.setTitle("Success");
				ViewManageThreads.alert.setHeaderText(null);
				ViewManageThreads.alert.setContentText("Thread name changed successfully!");
				ViewManageThreads.alert.showAndWait();
				ViewManageThreads.text_EnterNewName.clear();
				performReturn();
			}
			catch (SQLException e) {
				System.err.println("*** ERROR *** Database error: " + e.getMessage());
	            e.printStackTrace();
	            System.exit(0);
			}
		}
		

	}
	
	/**********
	 * <p> Method: performDeleteThread() </p>
	 * 
	 * <p> Description: This method removes an existing thread. 
	 * Does not allow for the deletion of the General Thread
	 * </p>
	 * 
	 */
	protected static void performDeleteThread() {
		// Determine which item in the ComboBox list was selected
		ViewManageThreads.theSelectedThread = (String) ViewManageThreads.
				combobox_SelectThread.getValue();
		
		//If the current admin has selected the General Thread, and they try to remove the General Thread, present an error
		if (ViewManageThreads.theSelectedThread.equals("General")) {
			ViewManageThreads.alert.setTitle("*** ERROR ***");
			ViewManageThreads.alert.setHeaderText("Edit Thread Issue");
			ViewManageThreads.alert.setContentText("You cannot delete the General thread!");
			ViewManageThreads.alert.showAndWait();
			return;
		}
		
		else {
			ViewManageThreads.confirm.setTitle("Delete Thread Message");
			ViewManageThreads.confirm.setHeaderText("Are you sure?");
			ViewManageThreads.confirm.setContentText("This will delete all posts in the thread");
			Optional<ButtonType> result = ViewManageThreads.confirm.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.YES) {
				
				try {
					theDatabase.deleteThread(ViewManageThreads.theSelectedThread);
					ViewManageThreads.alert.setTitle("Success");
					ViewManageThreads.alert.setHeaderText(null);
					ViewManageThreads.alert.setContentText("Thread deleted successfully!");
					ViewManageThreads.alert.showAndWait();
					performReturn();
				}
				catch (SQLException e) {
					System.err.println("*** ERROR *** Database error: " + e.getMessage());
		            e.printStackTrace();
		            System.exit(0);
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
		int theRole = applicationMain.FoundationsMain.activeHomePage;
		switch (theRole) {
		case 1:
			guiAdminHome.ViewAdminHome.displayAdminHome(theStage, theUser);
			break;
		case 2:
			guiStaff.ViewStaffHome.displayStaffHome(theStage, theUser);
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
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewManageThreads.theStage);
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