package guiNewAccount;

import java.sql.SQLException;

import database.Database;
import entityClasses.User;
import passwordPopUpWindow.Model;
import userNameRecognizerTestbed.UserNameRecognizer;

public class ControllerNewAccount {
	
	/*-********************************************************************************************

	The User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/
	
	protected static String username = "";
	protected static String password1 = "";
	protected static String password2 = "";		
	private static String unverifiedUsername = "";
	private static String unverifiedPassword1 = "";
	private static String unverifiedPassword2 = "";


	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	
	//************************************************************************
	// Methods for verification are same as those from ControllerFIrstAdmin 
	
	// Verify username
	protected static void setUsername() {
        unverifiedUsername = ViewNewAccount.text_Username.getText();
        String result = UserNameRecognizer.checkForValidUserName(unverifiedUsername);

        if (result.contains("*** ERROR ***")) {
            ViewNewAccount.label_UsernameError.setText(result);
            username = "";
        } else {
            ViewNewAccount.label_UsernameError.setText("");
            username = unverifiedUsername;
        }
        updateButtonState();
    }
	
	// Verify password
	protected static void setPassword1() {
        unverifiedPassword1 = ViewNewAccount.text_Password1.getText();
        String result = Model.evaluatePassword(unverifiedPassword1);

        if (result.contains("*** ERROR ***")) {
            ViewNewAccount.label_Password1Error.setText(result);
            password1 = "";
        } else {
            ViewNewAccount.label_Password1Error.setText("");
            password1 = unverifiedPassword1;
        }
        updateButtonState();
    }
	
	// Verify password
	protected static void setPassword2() {
        unverifiedPassword2 = ViewNewAccount.text_Password2.getText();
        String result = Model.evaluatePassword(unverifiedPassword2);

        if (result.contains("*** ERROR ***")) {
            ViewNewAccount.label_Password2Error.setText(result);
            password2 = "";
        } else {
            ViewNewAccount.label_Password2Error.setText("");
            password2 = unverifiedPassword2;
        }
        updateButtonState();
    }
	
	// This method makes it so the user cant make an account with a username or password that isnt allowed
	private static void updateButtonState() {
		boolean validUsername = !ViewNewAccount.label_UsernameError.getText().contains("*** ERROR ***") 
                && !username.isEmpty();

		boolean validPass1 = !ViewNewAccount.label_Password1Error.getText().contains("*** ERROR ***")
             && !password1.isEmpty();

		boolean validPass2 = !ViewNewAccount.label_Password2Error.getText().contains("*** ERROR ***")
             && !password2.isEmpty();

        ViewNewAccount.button_UserSetup.setDisable(!(validUsername && validPass1 && validPass2));
    }
	
	/**********
	 * <p> Method: public doCreateUser() </p>
	 * 
	 * <p> Description: This method is called when the user has clicked on the User Setup
	 * button.  This method checks the input fields to see that they are valid.  If so, it then
	 * creates the account by adding information to the database.
	 * 
	 * The method reaches batch to the view page and to fetch the information needed rather than
	 * passing that information as parameters.
	 * 
	 */	
	protected static void doCreateUser() {
		// Changed the code so that the verification and setting of the username and password
		// are contained in seperate methods
		
		
		// Display key information to the log
		System.out.println("** Account for Username: " + username + "; theInvitationCode: "+
				ViewNewAccount.theInvitationCode + "; email address: " + 
				ViewNewAccount.emailAddress + "; Role: " + ViewNewAccount.theRole);
		
		// Initialize local variables that will be created during this process
		int roleCode = 0;
		User user = null;

		// Make sure the two passwords are the same.	
		if (ViewNewAccount.text_Password1.getText().
				compareTo(ViewNewAccount.text_Password2.getText()) == 0) {
			
			// The passwords match so we will set up the role and the User object base on the 
			// information provided in the invitation
			if (ViewNewAccount.theRole.compareTo("Admin") == 0) {
				roleCode = 1;
				user = new User(username, password1, "", "", "", "", "", true, false, false);
			} else if (ViewNewAccount.theRole.compareTo("Staff") == 0) {
				roleCode = 2;
				user = new User(username, password1, "", "", "", "", "", false, true, false);
			} else if (ViewNewAccount.theRole.compareTo("Student") == 0) {
				roleCode = 3;
				user = new User(username, password1, "", "", "", "", "", false, false, true);
			} else {
				System.out.println(
						"**** Trying to create a New Account for a role that does not exist!");
				System.exit(0);
			}
			
			// Unlike the FirstAdmin, we know the email address, so set that into the user as well.
        	user.setEmailAddress(ViewNewAccount.emailAddress);

        	// Inform the system about which role will be played
			applicationMain.FoundationsMain.activeHomePage = roleCode;
			
        	// Create the account based on user and proceed to the user account update page
            try {
            	// Create a new User object with the pre-set role and register in the database
            	theDatabase.register(user);
            } catch (SQLException e) {
                System.err.println("*** ERROR *** Database error: " + e.getMessage());
                e.printStackTrace();
                System.exit(0);
            }
            
            // The account has been set, so remove the invitation from the system
            theDatabase.removeInvitationAfterUse(
            		ViewNewAccount.text_Invitation.getText());
            
            // Set the database so it has this user and the current user
            theDatabase.getUserAccountDetails(username);

            // Navigate to the Welcome Login Page
            guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewNewAccount.theStage, user);
		}
		else {
			// The two passwords are NOT the same, so clear the passwords, explain the passwords
			// must be the same, and clear the message as soon as the first character is typed.
			ViewNewAccount.text_Password1.setText("");
			ViewNewAccount.text_Password2.setText("");
			ViewNewAccount.alertUsernamePasswordError.showAndWait();
		}
	}

	
	/**********
	 * <p> Method: public performQuit() </p>
	 * 
	 * <p> Description: This method is called when the user has clicked on the Quit button.  Doing
	 * this terminates the execution of the application.  All important data must be stored in the
	 * database, so there is no cleanup required.  (This is important so we can minimize the impact
	 * of crashed.)
	 * 
	 */	
	protected static void performQuit() {
		System.out.println("Perform Quit");
		System.exit(0);
	}	
}
