package guiFirstAdmin;

import java.sql.SQLException;

import database.Database;
import entityClasses.User;
import javafx.stage.Stage;
import userNameRecognizerTestbed.UserNameRecognizer;
import passwordPopUpWindow.Model;

public class ControllerFirstAdmin {
	/*-********************************************************************************************

	The controller attributes for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/
	
	protected static String adminUsername = "";
	protected static String adminPassword1 = "";
	protected static String adminPassword2 = "";		
	private static String unverifiedAdminUsername = "";
	private static String unverifiedAdminPassword1 = "";
	private static String unverifiedAdminPassword2 = "";	
	protected static Database theDatabase = applicationMain.FoundationsMain.database;	
	
	/*-********************************************************************************************

	The User Interface Actions for this page
	
	*/
	
	
	/**********
	 * <p> Method: setAdminUsername() </p>
	 * 
	 * <p> Description: This method is called when the user adds text to the username field in the
	 * View.  A private local copy of what was last entered is kept here.</p>
	 * 
	 */
	protected static void setAdminUsername() {
		unverifiedAdminUsername = ViewFirstAdmin.text_AdminUsername.getText();
		String result = UserNameRecognizer.checkForValidUserName(unverifiedAdminUsername);
		
		if (result.contains("*** ERROR ***")) {
	        ViewFirstAdmin.label_UsernameError.setText(result);
	    } else {
	        ViewFirstAdmin.label_UsernameError.setText(""); 
	        adminUsername = unverifiedAdminUsername;
	    }
		updateButtonState();
	}
	
	
	/**********
	 * <p> Method: setAdminPassword1() </p>
	 * 
	 * <p> Description: This method is called when the user adds text to the password 1 field in
	 * the View.  A private local copy of what was last entered is kept here.</p>
	 * 
	 */
	protected static void setAdminPassword1() {
		unverifiedAdminPassword1 = ViewFirstAdmin.text_AdminPassword1.getText();
		ViewFirstAdmin.label_PasswordsDoNotMatch.setText("");
		
		String result = Model.evaluatePassword(unverifiedAdminPassword1);
		
		if (result.contains("*** ERROR ***")) {
	        ViewFirstAdmin.label_Password1Error.setText(result);
	    } else {
	        ViewFirstAdmin.label_Password1Error.setText("");
	        adminPassword1 = unverifiedAdminPassword1;
	    }
		updateButtonState();
	}
	
	
	/**********
	 * <p> Method: setAdminPassword2() </p>
	 * 
	 * <p> Description: This method is called when the user adds text to the password 2 field in
	 * the View.  A private local copy of what was last entered is kept here.</p>
	 * 
	 */
	protected static void setAdminPassword2() {
		unverifiedAdminPassword2 = ViewFirstAdmin.text_AdminPassword2.getText();		
		ViewFirstAdmin.label_PasswordsDoNotMatch.setText("");
		
		String result = Model.evaluatePassword(unverifiedAdminPassword2);
		
		if (result.contains("*** ERROR ***")) {
	        ViewFirstAdmin.label_Password2Error.setText(result);
	    } else {
	        ViewFirstAdmin.label_Password2Error.setText("");
	        adminPassword2 = unverifiedAdminPassword2;
	    }
		updateButtonState();
	}
	
	
	/**********
	 * <p> Method: doSetupAdmin() </p>
	 * 
	 * <p> Description: This method is called when the user presses the button to set up the Admin
	 * account.  It start by trying to establish a new user and placing that user into the
	 * database.  If that is successful, we proceed to the UserUpdate page.</p>
	 * 
	 */
	protected static void doSetupAdmin(Stage ps, int r) {
		
		// Make sure the two passwords are the same
		if (adminPassword1.compareTo(adminPassword2) == 0) {
        	// Create the passwords and proceed to the user home page
        	User user = new User(adminUsername, adminPassword1, "", "", "", "", "", true, false, 
        			false);
            try {
            	// Create a new User object with admin role and register in the database
            	theDatabase.register(user);
            	}
            catch (SQLException e) {
                System.err.println("*** ERROR *** Database error trying to register a user: " + 
                		e.getMessage());
                e.printStackTrace();
                System.exit(0);
            }
            
            // User was established in the database, so navigate to the User Update Page
        	guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewFirstAdmin.theStage, user);
        
        	
		}
		else {
			// The two passwords are NOT the same, so clear the passwords, explain the passwords
			// must be the same, and clear the message as soon as the first character is typed.
			ViewFirstAdmin.text_AdminPassword1.setText("");
			ViewFirstAdmin.text_AdminPassword2.setText("");
			ViewFirstAdmin.label_PasswordsDoNotMatch.setText(
					"The two passwords must match. Please try again!");
		}
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
		System.out.println("Perform Quit");
		System.exit(0);
	}	
	
	private static void updateButtonState() {
		boolean validUsername = !ViewFirstAdmin.label_UsernameError.getText().contains("*** ERROR ***") 
                && !adminUsername.isEmpty();

		boolean validPass1 = !ViewFirstAdmin.label_Password1Error.getText().contains("*** ERROR ***")
             && !adminPassword1.isEmpty();

		boolean validPass2 = !ViewFirstAdmin.label_Password2Error.getText().contains("*** ERROR ***")
             && !adminPassword2.isEmpty();	

	    ViewFirstAdmin.button_AdminSetup.setDisable(!(validUsername && validPass1 && validPass2));
	}
}



