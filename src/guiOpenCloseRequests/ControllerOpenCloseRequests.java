package guiOpenCloseRequests;

import java.sql.SQLException;

import database.Database;
import entityClasses.User;
import javafx.stage.Stage;

public class ControllerOpenCloseRequests {
	
	/**
	* Controller for the Manage Requests page
	*
	* This class provides static methods used by the ControllerOpenCloseRequests class 
	* handle user interactions such as creating, opening and closing requests,
	* it also allows for logging out and exiting the application. <p>
	* <p>
	*/

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;		
	protected static Stage theStage = ViewOpenCloseRequests.theStage;		
	protected static User theUser = ViewOpenCloseRequests.theUser;
	
	
	/**********
	 * <p> Method: repaintTheWindow() </p>
	 * 
	 * <p> Description: This method determines the current state of the window and then establishes
	 * the appropriate list of widgets in the Pane to show the proper set of current values. </p>
	 * 
	 */
	protected static void repaintTheWindow() {
		// Clear what had been displayed
		ViewOpenCloseRequests.theRootPane.getChildren().clear();
		ViewOpenCloseRequests.theSelectedOption = (String) ViewOpenCloseRequests.combobox_SelectAction.getValue();
		
		// Determine which of the views to show to the user
		// User is Admin
		if (applicationMain.FoundationsMain.activeHomePage == 1) {
			ViewOpenCloseRequests.theRootPane.getChildren().addAll(
					ViewOpenCloseRequests.label_PageTitle, ViewOpenCloseRequests.label_UserDetails, 
					ViewOpenCloseRequests.button_UpdateThisUser, ViewOpenCloseRequests.line_Separator1,
					ViewOpenCloseRequests.line_Separator4, ViewOpenCloseRequests.button_Return,
					ViewOpenCloseRequests.button_Logout, ViewOpenCloseRequests.button_Quit,
					ViewOpenCloseRequests.label_EnterRequestID, ViewOpenCloseRequests.text_EnterRequestID,
					ViewOpenCloseRequests.button_CloseRequest, ViewOpenCloseRequests.label_EnterActionTaken, 
					ViewOpenCloseRequests.text_EnterActionTaken);
		}
		
		// User is Staff
		else {
			// Selected "New Admin Request"
			if (ViewOpenCloseRequests.theSelectedOption.compareTo("Create New Admin Request") == 0) {
				ViewOpenCloseRequests.theRootPane.getChildren().addAll(
						ViewOpenCloseRequests.label_PageTitle, ViewOpenCloseRequests.label_UserDetails, 
						ViewOpenCloseRequests.button_UpdateThisUser, ViewOpenCloseRequests.line_Separator1,
						ViewOpenCloseRequests.label_SelectAction, ViewOpenCloseRequests.combobox_SelectAction, 
						ViewOpenCloseRequests.line_Separator4, ViewOpenCloseRequests.button_Return,
						ViewOpenCloseRequests.button_Logout, ViewOpenCloseRequests.button_Quit,
						ViewOpenCloseRequests.label_EnterDescription, ViewOpenCloseRequests.text_EnterDescription,
						ViewOpenCloseRequests.button_NewRequest);
			}
			
			//Selected "Re-Open Request"
			else {
				ViewOpenCloseRequests.theRootPane.getChildren().addAll(
						ViewOpenCloseRequests.label_PageTitle, ViewOpenCloseRequests.label_UserDetails, 
						ViewOpenCloseRequests.button_UpdateThisUser, ViewOpenCloseRequests.line_Separator1,
						ViewOpenCloseRequests.label_SelectAction, ViewOpenCloseRequests.combobox_SelectAction, 
						ViewOpenCloseRequests.line_Separator4, ViewOpenCloseRequests.button_Return,
						ViewOpenCloseRequests.button_Logout, ViewOpenCloseRequests.button_Quit,
						ViewOpenCloseRequests.label_EnterOpenRequestID, ViewOpenCloseRequests.text_EnterOpenRequestID,
						ViewOpenCloseRequests.label_EnterNewRequestDescription, ViewOpenCloseRequests.text_EnterNewRequestDescription,
						ViewOpenCloseRequests.button_OpenRequest);
			}
		}
		
		// Add the list of widgets to the stage and show it
		
		// Set the title for the window
		ViewOpenCloseRequests.theStage.setTitle("Operations Page");
		ViewOpenCloseRequests.theStage.setScene(ViewOpenCloseRequests.theManageGradesScene);
		ViewOpenCloseRequests.theStage.show();
	}
	

	/**
	 * <p> Method: performCloseRequest() </p>
	 *
	 * <p> Description: This method closes a request in the system and 
	 * shows the error message if the id or message doesn't satisfy guidelines.  </p>
	 * 
	 */

	protected static void performCloseRequest() {
		
		int requestID;
		String actionTaken = ViewOpenCloseRequests.text_EnterActionTaken.getText();
		
		// If input is not a number, reject it
		try {
			requestID = Integer.parseInt(ViewOpenCloseRequests.text_EnterRequestID.getText());
		} catch (NumberFormatException e) {
			ViewOpenCloseRequests.alert.setTitle("*** ERROR ***");
			ViewOpenCloseRequests.alert.setHeaderText("Close Request Issue");
			ViewOpenCloseRequests.alert.setContentText("RequestID must be a integer number!");
			ViewOpenCloseRequests.alert.showAndWait();
		    return;
		}

		// If requestID is invalid, reject it
		try {
			if (!theDatabase.requestExists(requestID)) {
				ViewOpenCloseRequests.alert.setTitle("*** ERROR ***");
				ViewOpenCloseRequests.alert.setHeaderText("Close Request Issue");
				ViewOpenCloseRequests.alert.setContentText("Invalid RequestID");
				ViewOpenCloseRequests.alert.showAndWait();
				return;
			}
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error: " + e.getMessage());
            e.printStackTrace();
            System.exit(0);
		}
		
		// If actionTaken is empty, reject it
		if (actionTaken.isEmpty()) {
			ViewOpenCloseRequests.alert.setTitle("*** ERROR ***");
			ViewOpenCloseRequests.alert.setHeaderText("Close Request Issue");
			ViewOpenCloseRequests.alert.setContentText("Action Taken cannot be empty!");
			ViewOpenCloseRequests.alert.showAndWait();
			return;
		}
		
		try {
			theDatabase.closeRequest(requestID, actionTaken);
			ViewOpenCloseRequests.alert.setTitle("Success");
			ViewOpenCloseRequests.alert.setHeaderText(null);
			ViewOpenCloseRequests.alert.setContentText("Request closed successfully!");
			ViewOpenCloseRequests.alert.showAndWait();
			ViewOpenCloseRequests.text_EnterRequestID.clear();
			ViewOpenCloseRequests.text_EnterActionTaken.clear();
			performReturn();
		}
		
		catch (SQLException e) {
			System.err.println("*** ERROR *** Database error: " + e.getMessage());
            e.printStackTrace();
            System.exit(0);
		}
		
		
	}
	
	
	/**
	 * <p> Method: performMakeNewRequest() </p>
	 *
	 * <p> Description: This method makes a new admin request using the fields entered 
	 * by the user. </p>
	 *
	 *
	 */

	protected static void performMakeNewRequest() {
		String description = ViewOpenCloseRequests.text_EnterDescription.getText();;
		
		// If request description is empty, reject it
		if (description.isEmpty()) {
			ViewOpenCloseRequests.alert.setTitle("*** ERROR ***");
			ViewOpenCloseRequests.alert.setHeaderText("New Admin Request Issue");
			ViewOpenCloseRequests.alert.setContentText("Request description cannot be empty!");
			ViewOpenCloseRequests.alert.showAndWait();
			return;
		}
		
		try {
			theDatabase.insertRequest(applicationMain.FoundationsMain.activeUser.getUserName(), description);
			ViewOpenCloseRequests.alert.setTitle("Success");
			ViewOpenCloseRequests.alert.setHeaderText(null);
			ViewOpenCloseRequests.alert.setContentText("Admin request made successfully!");
			ViewOpenCloseRequests.alert.showAndWait();
			ViewOpenCloseRequests.text_EnterDescription.clear();
			performReturn();
		}
		catch (SQLException e) {
			System.err.println("*** ERROR *** Database error: " + e.getMessage());
            e.printStackTrace();
            System.exit(0);
		}
		
		
	}
	
	/**
	 * <p> Method: performReOpenRequst() </p>
	 *
	 * <p> Description: This method reopens a previously closed request and updates it with new 
	 * information. </p>
	 *
	 *
	 */
	protected static void performReOpenRequest() {
		int requestID;
		String description = ViewOpenCloseRequests.text_EnterNewRequestDescription.getText();
		
		// If input is not a number, reject it
		try {
			requestID = Integer.parseInt(ViewOpenCloseRequests.text_EnterOpenRequestID.getText());
		} catch (NumberFormatException e) {
			ViewOpenCloseRequests.alert.setTitle("*** ERROR ***");
			ViewOpenCloseRequests.alert.setHeaderText("Reopen Request Issue");
			ViewOpenCloseRequests.alert.setContentText("RequestID must be a integer number!");
			ViewOpenCloseRequests.alert.showAndWait();
		    return;
		}

		// If requestID is invalid, reject it
		try {
			if (!theDatabase.requestExists(requestID)) {
				ViewOpenCloseRequests.alert.setTitle("*** ERROR ***");
				ViewOpenCloseRequests.alert.setHeaderText("Reopen Request Issue");
				ViewOpenCloseRequests.alert.setContentText("Invalid RequestID");
				ViewOpenCloseRequests.alert.showAndWait();
				return;
			}
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error: " + e.getMessage());
            e.printStackTrace();
            System.exit(0);
		}
		
		// If request description is empty, reject it
		if (description.isEmpty()) {
			ViewOpenCloseRequests.alert.setTitle("*** ERROR ***");
			ViewOpenCloseRequests.alert.setHeaderText("Reopen Request Issue");
			ViewOpenCloseRequests.alert.setContentText("New description cannot be empty!");
			ViewOpenCloseRequests.alert.showAndWait();
			return;
		}
		
		try {
			theDatabase.openRequest(requestID, description);
			ViewOpenCloseRequests.alert.setTitle("Success");
			ViewOpenCloseRequests.alert.setHeaderText(null);
			ViewOpenCloseRequests.alert.setContentText("Admin request reopened successfully!");
			ViewOpenCloseRequests.alert.showAndWait();
			ViewOpenCloseRequests.text_EnterDescription.clear();
			ViewOpenCloseRequests.text_EnterOpenRequestID.clear();
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
	 * <p> Description: This method returns the user to the Home page. </p>
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
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewOpenCloseRequests.theStage);
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