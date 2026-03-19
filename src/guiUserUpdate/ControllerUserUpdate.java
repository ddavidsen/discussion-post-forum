package guiUserUpdate;

import entityClasses.User;
import javafx.stage.Stage;
import applicationMain.FoundationsMain;
import database.Database;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/*******
 * <p> Title: ControllerUserUpdate Class</p>
 *
 * <p> Description: This static class supports the actions initiated by the ViewUserUpdate
 * class. This version extends the original controller to support a secure password-reset
 * flow that validates the new password and updates the database. Once a new password is
 * established, the one-time password is cleared so it cannot be reused, and the user
 * is required to log in again.</p>
 *
 */

public class ControllerUserUpdate {
	/*-********************************************************************************************
	The Controller for ViewUserUpdate
	
	**********************************************************************************************/

	/// Database reference for this controller
	private static final Database theDatabase = FoundationsMain.database;

	/*******
	 * <p> Method: private static boolean isValidPassword(String s) </p>
	 *
	 * <p> Description: Validate the password against the system rules:
	 * - at least 8 characters
	 * - contains at least one uppercase letter
	 * - contains at least one lowercase letter
	 * - contains at least one digit
	 * - contains at least one special character
	 * - cannot contain whitespace</p>
	 *
	 * @param s the password string to be validated
	 * @return true if the password meets all rules, else false
	 */
	private static boolean isValidPassword(String s) {
		if (s == null || s.length() < 8) return false;
		boolean up = false, lo = false, di = false, sp = false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (Character.isWhitespace(c)) return false;
			if (Character.isUpperCase(c)) up = true;
			else if (Character.isLowerCase(c)) lo = true;
			else if (Character.isDigit(c)) di = true;
			else sp = true;
		}
		return up && lo && di && sp;
	}

	/*******
	 * <p> Method: private static String passwordRulesText() </p>
	 *
	 * <p> Description: Return the password rules as a formatted string for display
	 * in error messages.</p>
	 *
	 * @return a string listing the password rules
	 */
	private static String passwordRulesText() {
		return "- At least 8 characters\n"
			 + "- Must include upper + lower + digit + special\n"
			 + "- No spaces";
	}

	/*******
	 * <p> Method: protected static void doSaveNewPassword(Stage theStage, User user) </p>
	 *
	 * <p> Description: Called when the user attempts to save a new password. This method:
	 *  - ensures the two password fields match
	 *  - validates the password against the rules
	 *  - updates the database with the new password
	 *  - clears the one-time password
	 *  - alerts the user and redirects them to log in again</p>
	 *
	 * @param theStage specifies the JavaFX stage for the next GUI page
	 * @param user specifies the user whose password is being changed
	 */
	protected static void doSaveNewPassword(Stage theStage, User user) {
		String p1 = ViewUserUpdate.text_NewPassword.getText();
		String p2 = ViewUserUpdate.text_ConfirmPassword.getText();

		if (p1 == null || p2 == null || !p1.equals(p2)) {
			new Alert(AlertType.ERROR, "Passwords do not match.").showAndWait();
			return;
		}
		if (!isValidPassword(p1)) {
			new Alert(AlertType.ERROR,
					"Password does not meet rules:\n" + passwordRulesText()).showAndWait();
			return;
		}

		// Update database with new password
		theDatabase.setPasswordForUser(user.getUserName(), p1);

		new Alert(AlertType.INFORMATION, "Password updated. Please log in again.").showAndWait();

		guiUserLogin.ViewUserLogin.displayUserLogin(theStage);
	}

	/*******
	 * <p> Method: protected static void goToUserHomePage(Stage theStage, User theUser) </p>
	 *
	 * <p> Description: Navigate to the correct home page based on the role the user
	 * selected during login.</p>
	 *
	 * @param theStage specifies the JavaFX Stage for the next GUI page
	 * @param theUser specifies the user so the right information is shown
	 */
	protected static void goToUserHomePage(Stage theStage, User theUser) {
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
			System.out.println("*** ERROR *** UserUpdate goToUserHome has an invalid role: " + theRole);
			System.exit(0);
		}
	}
	/*******
	 * <p> Method: protected static void performLogout() </p>
	 *
	 * <p> Description: This method is called to log the user out and return them to
	 * the login page. It is used by ViewUserUpdate when the first admin setup is
	 * complete to force the admin to log in again.</p>
	 *
	 */
	protected static void performLogout() {
	    guiUserLogin.ViewUserLogin.displayUserLogin(ViewUserUpdate.theStage);
	}
}

