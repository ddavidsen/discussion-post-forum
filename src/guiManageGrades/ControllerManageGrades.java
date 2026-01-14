package guiManageGrades;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import applicationMain.FoundationsMain;
import database.Database;
import entityClasses.User;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class ControllerManageGrades {
	
	/**
	* Controller for the Manage Grades page
	*
	* This class provides static methods used by the ControllerManageGrades class 
	* handle user interactions such as creating, editing and deleting grades,
	* it also allows for logging out and exiting the application. <p>
	* <p>
	*/

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;		
	protected static Stage theStage = ViewManageGrades.theStage;		
	protected static User theUser = ViewManageGrades.theUser;
	
	
	/**********
	 * <p> Method: repaintTheWindow() </p>
	 * 
	 * <p> Description: This method determines the current state of the window and then establishes
	 * the appropriate list of widgets in the Pane to show the proper set of current values. </p>
	 * 
	 */
	protected static void repaintTheWindow() {
		// Clear what had been displayed
		ViewManageGrades.theRootPane.getChildren().clear();
		ViewManageGrades.theSelectedOption = (String) ViewManageGrades.combobox_SelectAction.getValue();
		
		// Determine which of the views to show to the user
		if (ViewManageGrades.theSelectedOption.compareTo("Create") == 0) {
			ViewManageGrades.theRootPane.getChildren().addAll(
					ViewManageGrades.label_PageTitle, ViewManageGrades.label_UserDetails, 
					ViewManageGrades.button_UpdateThisUser, ViewManageGrades.line_Separator1,
					ViewManageGrades.label_SelectAction, ViewManageGrades.combobox_SelectAction, 
					ViewManageGrades.line_Separator4, ViewManageGrades.button_Return,
					ViewManageGrades.button_Logout, ViewManageGrades.button_Quit,
					ViewManageGrades.label_EnterPostID, ViewManageGrades.text_EnterPostID,
					ViewManageGrades.button_CreateGrade, ViewManageGrades.label_EnterScore, 
					ViewManageGrades.text_EnterScore, 
					ViewManageGrades.label_EnterComments, 
					ViewManageGrades.text_EnterComments, 
					ViewManageGrades.button_ViewParameters);
		}
		
		else if (ViewManageGrades.theSelectedOption.compareTo("Edit") == 0) {
			ViewManageGrades.theRootPane.getChildren().addAll(
					ViewManageGrades.label_PageTitle, ViewManageGrades.label_UserDetails, 
					ViewManageGrades.button_UpdateThisUser, ViewManageGrades.line_Separator1,
					ViewManageGrades.label_SelectAction, ViewManageGrades.combobox_SelectAction, 
					ViewManageGrades.line_Separator4, ViewManageGrades.button_Return,
					ViewManageGrades.button_Logout, ViewManageGrades.button_Quit,
					ViewManageGrades.label_EnterGradeID, ViewManageGrades.text_EnterGradeID,
					ViewManageGrades.label_EnterNewScore, ViewManageGrades.text_EnterNewScore,
					ViewManageGrades.button_EditGrade, ViewManageGrades.button_ViewParameters);
		}
		
		else {
			ViewManageGrades.theRootPane.getChildren().addAll(
					ViewManageGrades.label_PageTitle, ViewManageGrades.label_UserDetails, 
					ViewManageGrades.button_UpdateThisUser, ViewManageGrades.line_Separator1,
					ViewManageGrades.label_SelectAction, ViewManageGrades.combobox_SelectAction, 
					ViewManageGrades.line_Separator4, ViewManageGrades.button_Return,
					ViewManageGrades.button_Logout, ViewManageGrades.button_Quit,
					ViewManageGrades.label_EnterDeleteGradeID, ViewManageGrades.text_EnterDeleteGradeID,
					ViewManageGrades.button_DeleteGrade, ViewManageGrades.button_ViewParameters);
		}
		
		// Add the list of widgets to the stage and show it
		
		// Set the title for the window
		ViewManageGrades.theStage.setTitle("CSE 360 Foundation Code: Opertaions Page");
		ViewManageGrades.theStage.setScene(ViewManageGrades.theManageGradesScene);
		ViewManageGrades.theStage.show();
	}
	

	/**
	 * <p> Method: performCreateGrade() </p>
	 *
	 * <p> Description: This method creates a grade in the system and 
	 * shows the error message if the grade doesn't satisfy guidelines.  </p>
	 * 
	 */

	protected static void performCreateGrade() {
		int postID;
		int score;
		String comments = ViewManageGrades.text_EnterComments.getText();
		
		// If input is not a number, reject it
		try {
			postID = Integer.parseInt(ViewManageGrades.text_EnterPostID.getText());
		} catch (NumberFormatException e) {
			ViewManageGrades.alert.setTitle("*** ERROR ***");
			ViewManageGrades.alert.setHeaderText("Publish Grade Issue");
			ViewManageGrades.alert.setContentText("PostID must be a integer number!");
			ViewManageGrades.alert.showAndWait();
		    return;
		}
		
		try {
			score = Integer.parseInt(ViewManageGrades.text_EnterScore.getText());
		} catch (NumberFormatException e) {
			ViewManageGrades.alert.setTitle("*** ERROR ***");
			ViewManageGrades.alert.setHeaderText("Publish Grade Issue");
			ViewManageGrades.alert.setContentText("Score must be a integer number!");
			ViewManageGrades.alert.showAndWait();
		    return;
		}

		// If postID is invalid, reject it
		try {
			if (!theDatabase.postExists(postID)) {
				ViewManageGrades.alert.setTitle("*** ERROR ***");
				ViewManageGrades.alert.setHeaderText("Publish Grade Issue");
				ViewManageGrades.alert.setContentText("Invalid PostID");
				ViewManageGrades.alert.showAndWait();
				return;
			}
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error: " + e.getMessage());
            e.printStackTrace();
            System.exit(0);
		}
		
		// If score is not in valid range, reject it
		if (score < 0 || score > 100) {
			ViewManageGrades.alert.setTitle("*** ERROR ***");
			ViewManageGrades.alert.setHeaderText("Publish Grade Issue");
			ViewManageGrades.alert.setContentText("Score must be between 0-100");
			ViewManageGrades.alert.showAndWait();
			return;
		}
		
		try {
			theDatabase.insertGrade(postID, theDatabase.getAuthorOfPost(postID), 
					FoundationsMain.activeUser.getUserName(), score, comments);
			ViewManageGrades.alert.setTitle("Success");
			ViewManageGrades.alert.setHeaderText(null);
			ViewManageGrades.alert.setContentText("Grade published successfully!");
			ViewManageGrades.alert.showAndWait();
			ViewManageGrades.text_EnterPostID.clear();
			ViewManageGrades.text_EnterScore.clear();
			performReturn();
		}
		
		catch (SQLException e) {
			System.err.println("*** ERROR *** Database error: " + e.getMessage());
            e.printStackTrace();
            System.exit(0);
		}
		
	}
	
	
	/**
	 * <p> Method: performEditGrade() </p>
	 *
	 * <p> Description: This method helps edit the selected grade,
	 *  and show the new results otherwise show the error message. </p>
	 *
	 *
	 */

	protected static void performEditGrade() {
		int gradeID; 
		int score;
		
		// If input is not a number, reject it
		try {
			gradeID = Integer.parseInt(ViewManageGrades.text_EnterGradeID.getText());
		} catch (NumberFormatException e) {
			ViewManageGrades.alert.setTitle("*** ERROR ***");
			ViewManageGrades.alert.setHeaderText("Publish Grade Issue");
			ViewManageGrades.alert.setContentText("GradeID must be a integer number!");
			ViewManageGrades.alert.showAndWait();
		    return;
		}
		
		// If score is not a number, reject it
		try {
			score = Integer.parseInt(ViewManageGrades.text_EnterNewScore.getText());
		} catch (NumberFormatException e) {
			ViewManageGrades.alert.setTitle("*** ERROR ***");
			ViewManageGrades.alert.setHeaderText("Publish Grade Issue");
			ViewManageGrades.alert.setContentText("Score must be a integer number!");
			ViewManageGrades.alert.showAndWait();
		    return;
		}
		
		try {
			theDatabase.editGrade(gradeID, score);
			ViewManageGrades.alert.setTitle("Success");
			ViewManageGrades.alert.setHeaderText(null);
			ViewManageGrades.alert.setContentText("Grade published successfully!");
			ViewManageGrades.alert.showAndWait();
			ViewManageGrades.text_EnterGradeID.clear();
			ViewManageGrades.text_EnterNewScore.clear();
			performReturn();
		}
		catch (SQLException e) {
			System.err.println("*** ERROR *** Database error: " + e.getMessage());
            e.printStackTrace();
            System.exit(0);
		}
		
	}
	
	/**
	 * <p> Method: performDeleteParameter() </p>
	 *
	 * <p> Description: This method helps delete the selected grade,
	 *  and show the new results otherwise show the error message. </p>
	 *
	 *
	 */
	protected static void performDeleteGrade() {
		
		int gradeID; 
		
		// If input is not a number, reject it
		try {
			gradeID = Integer.parseInt(ViewManageGrades.text_EnterDeleteGradeID.getText());
		} catch (NumberFormatException e) {
			ViewManageGrades.alert.setTitle("*** ERROR ***");
			ViewManageGrades.alert.setHeaderText("Delete Grade Issue");
			ViewManageGrades.alert.setContentText("GradeID must be a number!");
			ViewManageGrades.alert.showAndWait();
		    return;
		}
		
		ViewManageGrades.confirm.setTitle("Delete Grade Message");
		ViewManageGrades.confirm.setHeaderText(null);
		ViewManageGrades.confirm.setContentText("Are you sure you want to remove this grade?");
		Optional<ButtonType> result = ViewManageGrades.confirm.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.YES) {
			
			try {
				theDatabase.deleteGrade(gradeID);
				ViewManageGrades.alert.setTitle("Success");
				ViewManageGrades.alert.setHeaderText(null);
				ViewManageGrades.alert.setContentText("Grade removed successfully!");
				ViewManageGrades.alert.showAndWait();
				ViewManageGrades.text_EnterDeleteGradeID.clear();
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
	 *  <p> Method: viewParameters() </p>
	 *  
	 *  <p> Description: This method displays parameters created by staff or admin users to assist
	 *  with their grading process. </p>
	 *  
	 */
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
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewManageGrades.theStage);
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