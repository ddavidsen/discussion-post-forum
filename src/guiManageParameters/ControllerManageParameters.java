package guiManageParameters;

import java.sql.SQLException;
import java.util.Optional;

import database.Database;
import entityClasses.User;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class ControllerManageParameters {
	
	/**
	* Controller for the Manage Parameters page
	*
	* This class provides static methods used by the ControllerManageParameters class handle user interactions such as creating, editing and deleting parameters,
	* it also allows for logging out and exiting the application. <p>
	* <p>
	*/

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;		
	protected static Stage theStage = ViewManageParameters.theStage;		
	protected static User theUser = ViewManageParameters.theUser;
	
	
	/**********
	 * <p> Method: repaintTheWindow() </p>
	 * 
	 * <p> Description: This method determines the current state of the window and then establishes
	 * the appropriate list of widgets in the Pane to show the proper set of current values. </p>
	 * 
	 */
	protected static void repaintTheWindow() {
		// Clear what had been displayed
		ViewManageParameters.theRootPane.getChildren().clear();
		ViewManageParameters.theSelectedOption = (String) ViewManageParameters.combobox_SelectAction.getValue();
		
		// Determine which of the views to show to the user
		if (ViewManageParameters.theSelectedOption.compareTo("Create") == 0) {
			ViewManageParameters.theRootPane.getChildren().addAll(
					ViewManageParameters.label_PageTitle, ViewManageParameters.label_UserDetails, 
					ViewManageParameters.button_UpdateThisUser, ViewManageParameters.line_Separator1,
					ViewManageParameters.label_SelectAction, ViewManageParameters.combobox_SelectAction, 
					ViewManageParameters.line_Separator4, ViewManageParameters.button_Return,
					ViewManageParameters.button_Logout, ViewManageParameters.button_Quit,
					ViewManageParameters.label_EnterParameter, ViewManageParameters.text_EnterParameter,
					ViewManageParameters.button_CreateParameter);
		}
		
		else if (ViewManageParameters.theSelectedOption.compareTo("Edit") == 0) {
			ViewManageParameters.theRootPane.getChildren().addAll(
					ViewManageParameters.label_PageTitle, ViewManageParameters.label_UserDetails, 
					ViewManageParameters.button_UpdateThisUser, ViewManageParameters.line_Separator1,
					ViewManageParameters.label_SelectAction, ViewManageParameters.combobox_SelectAction, 
					ViewManageParameters.line_Separator4, ViewManageParameters.button_Return,
					ViewManageParameters.button_Logout, ViewManageParameters.button_Quit,
					ViewManageParameters.label_SelectEditParameter, ViewManageParameters.text_SelectParameter,
					ViewManageParameters.label_EnterNewParameter, ViewManageParameters.text_EnterNewParameter,
					ViewManageParameters.button_EditParameter);
		}
		
		else {
			ViewManageParameters.theRootPane.getChildren().addAll(
					ViewManageParameters.label_PageTitle, ViewManageParameters.label_UserDetails, 
					ViewManageParameters.button_UpdateThisUser, ViewManageParameters.line_Separator1,
					ViewManageParameters.label_SelectAction, ViewManageParameters.combobox_SelectAction, 
					ViewManageParameters.line_Separator4, ViewManageParameters.button_Return,
					ViewManageParameters.button_Logout, ViewManageParameters.button_Quit,
					ViewManageParameters.label_SelectDeleteParameter, ViewManageParameters.text_SelectParameter,
					ViewManageParameters.button_DeleteParameter);
		}
		
		// Add the list of widgets to the stage and show it
		
		// Set the title for the window
		ViewManageParameters.theStage.setTitle("Operations Page");
		ViewManageParameters.theStage.setScene(ViewManageParameters.theAddRemoveRolesScene);
		ViewManageParameters.theStage.show();
	}
	

	/**
	 * <p> Method: performCreateParameter() </p>
	 *
	 * <p> Description: This method shows the error message if the parameter doesn't satisfy guidelines.  </p>
	 *  <p>
	 */

	protected static void performCreateParameter() {
		String newParameter = (String) ViewManageParameters.text_EnterParameter.getText();

		// If input is empty, reject it
		if (newParameter.isEmpty()) {
			ViewManageParameters.alert.setTitle("*** ERROR ***");
			ViewManageParameters.alert.setHeaderText("Create Parameter Issue");
			ViewManageParameters.alert.setContentText("Text cannot be empty!");
			ViewManageParameters.alert.showAndWait();
			return;
		}
		
		else {
			try {
				theDatabase.insertParam(newParameter);
				ViewManageParameters.alert.setTitle("Success");
				ViewManageParameters.alert.setHeaderText(null);
				ViewManageParameters.alert.setContentText("Parameter created successfully!");
				ViewManageParameters.alert.showAndWait();
				ViewManageParameters.text_EnterParameter.clear();
				performReturn();
			}
			catch (SQLException e) {
				System.err.println("*** ERROR *** Database error: " + e.getMessage());
	            e.printStackTrace();
	            System.exit(0);
			}
		}
	}
	
	
	/**
	 * <p> Method: performEditParameter() </p>
	 *
	 * <p> Description: This method helps edit the selected parameter, and show the new results otherwise show the error message. </p>
	 *
	 *
	 */

	protected static void performEditParameter() {
		int paramNum;
		// If input is not a number, reject it
		try {
			paramNum = Integer.parseInt(ViewManageParameters.text_SelectParameter.getText());
		} catch (NumberFormatException e) {
			ViewManageParameters.alert.setTitle("*** ERROR ***");
			ViewManageParameters.alert.setHeaderText("Publish Grade Issue");
			ViewManageParameters.alert.setContentText("GradeID must be a integer number!");
			ViewManageParameters.alert.showAndWait();
		    return;
		}
		
		try {
			theDatabase.editParam(paramNum, (String) ViewManageParameters.text_EnterNewParameter.getText());
			ViewManageParameters.alert.setTitle("Success");
			ViewManageParameters.alert.setHeaderText(null);
			ViewManageParameters.alert.setContentText("Parameter changed successfully!");
			ViewManageParameters.alert.showAndWait();
			ViewManageParameters.text_EnterNewParameter.clear();
			performReturn();
		}
		catch (SQLException e) {
			System.err.println("*** ERROR *** Database error: " + e.getMessage());
            e.printStackTrace();
            System.exit(0);
		}
		
		

	}
	
	protected static void performDeleteParameter() {
		int paramNum;
		// If input is not a number, reject it
		try {
			paramNum = Integer.parseInt(ViewManageParameters.text_SelectParameter.getText());
		} catch (NumberFormatException e) {
			ViewManageParameters.alert.setTitle("*** ERROR ***");
			ViewManageParameters.alert.setHeaderText("Publish Grade Issue");
			ViewManageParameters.alert.setContentText("GradeID must be a integer number!");
			ViewManageParameters.alert.showAndWait();
		    return;
		}
		
		ViewManageParameters.confirm.setTitle("Delete Parameter Message");
		ViewManageParameters.confirm.setHeaderText(null);
		ViewManageParameters.confirm.setContentText("Are you sure you want to delete this parameter?");
		Optional<ButtonType> result = ViewManageParameters.confirm.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.YES) {
			
			try {
				theDatabase.deleteParam(paramNum);
				ViewManageParameters.alert.setTitle("Success");
				ViewManageParameters.alert.setHeaderText(null);
				ViewManageParameters.alert.setContentText("Parameter deleted successfully!");
				ViewManageParameters.alert.showAndWait();
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
	 * <p> Method: performReturn() </p>
	 * 
	 * <p> Description: This method returns the user (who must be an Admin as only admins are the
	 * only users who have access to this page) to the Admin Home page. </p>
	 * 
	 */
	protected static void performReturn() {
		int theRole = applicationMain.FoundationsMain.activeHomePage;
		User theUser = applicationMain.FoundationsMain.activeUser;
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
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewManageParameters.theStage);
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