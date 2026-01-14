package guiUserLogin;
import applicationMain.FoundationsMain;
import database.Database;
import entityClasses.User;
import javafx.stage.Stage;

import java.sql.Timestamp;
import java.time.Instant;

public class ControllerUserLogin {
	
	/*-********************************************************************************************
	The User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	private static Stage theStage;	
	protected static User user;
	
	/**********
	 * <p> Method: public doLogin() </p>
	 *
	 * <p> Description: This method is called when the user has clicked on the Login button. This
	 * method checks the username and password to see if they are valid.  If so, it then logs that
	 * user in my determining which role to use.
	 *
	 * The method reaches batch to the view page and to fetch the information needed rather than
	 * passing that information as parameters.
	 *
	 */	
	protected static void doLogin(Stage ts) {
		user = null;
		FoundationsMain.activeUser = null;
		FoundationsMain.activeHomePage = 0;
		
		System.out.println("*** DEBUG: doLogin called");
		theStage = ts;
		String username = ViewUserLogin.text_Username.getText();
		String password = ViewUserLogin.text_Password.getText();
		boolean loginResult = false;
   	
		// Fetch the user and verify the username
    	if (theDatabase.getUserAccountDetails(username) == false) {
    		// Don't provide too much information.  Don't say the username is invalid or the
    		// password is invalid.  Just say the pair is invalid.
			ViewUserLogin.alertUsernamePasswordError.setContentText(
					"Incorrect username/password. Try again!");
			ViewUserLogin.alertUsernamePasswordError.showAndWait();
			return;
    	}
    	
		System.out.println("*** Username is valid");
		
		//If a one-time password (OTP) exists for the current user and the
		//password supplied matches that OTP then the OTP is cleared and the user is sent to
		//the ViewUserUpdate page to establish a new permanent password.
		String currentOtp = theDatabase.getCurrentOneTimePassword(username);
		Timestamp otpExpiration = theDatabase.getOTPExpiration(username);
		
		if (currentOtp != null && !currentOtp.isEmpty ()) {
			if (password.equals(currentOtp) && Instant.now().isBefore(otpExpiration.toInstant())) {
				theDatabase.clearCurrentOneTimePassword(username);
				
				entityClasses.User user = new entityClasses.User(
						username, "<OTP-LOGIN>",
						theDatabase.getCurrentFirstName(),
						theDatabase.getCurrentMiddleName(), theDatabase.getCurrentLastName(),
		    			theDatabase.getCurrentPreferredFirstName(), theDatabase.getCurrentEmailAddress(),
		    			theDatabase.getCurrentAdminRole(),
		    			theDatabase.getCurrentStaffRole(), theDatabase.getCurrentStudentRole()
		    			);
				
				guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, user);
				return;
				
			}
		}
	
		// This will stop the normal login path
		// else: fall through to normal password check
		
		// Check to see that the login password matches the account password
	   	String actualPassword = theDatabase.getCurrentPassword();
	   	
	   	if (password.compareTo(actualPassword) != 0) {
	   		ViewUserLogin.alertUsernamePasswordError.setContentText(
	   				"Incorrect username/password. Try again!");
	   		ViewUserLogin.alertUsernamePasswordError.showAndWait();
	   		return;
	   	}
	   	
		System.out.println("*** Password is valid for this user");
			
		// Establish this user's details
	   	user = new User(username, password, theDatabase.getCurrentFirstName(),
	   			theDatabase.getCurrentMiddleName(), theDatabase.getCurrentLastName(),
	   			theDatabase.getCurrentPreferredFirstName(), theDatabase.getCurrentEmailAddress(),
	   			theDatabase.getCurrentAdminRole(),
	   			theDatabase.getCurrentStaffRole(), theDatabase.getCurrentStudentRole());
	   	
	   	FoundationsMain.activeUser = user;
	   	
	   	// See which home page dispatch to use
			int numberOfRoles = theDatabase.getNumberOfRoles(user);		
			System.out.println("*** The number of roles: "+ numberOfRoles);
			if (numberOfRoles == 1) {
				// Single Account Home Page - The user has no choice here
				
				// Admin role
				if (user.getAdminRole()) {
					loginResult = theDatabase.loginAdmin(user);
					if (loginResult) {
						guiAdminHome.ViewAdminHome.displayAdminHome(theStage, user);
						FoundationsMain.activeHomePage = 1;
					}
				} else if (user.getStaffRole()) {
					loginResult = theDatabase.loginStaffRole(user);
					if (loginResult) {
						guiStaff.ViewStaffHome.displayStaffHome(theStage, user);
						FoundationsMain.activeHomePage = 2;
					}
				} else if (user.getStudentRole()) {
					loginResult = theDatabase.loginStudentRole(user);
					if (loginResult) {
						guiStudent.ViewStudentHome.displayStudentHome(theStage, user);
						FoundationsMain.activeHomePage = 3;
					}
					// Other roles
				} else {
					System.out.println("***** UserLogin goToUserHome request has an invalid role");
				}
			} else if (numberOfRoles > 1) {
				// Multiple Account Home Page - The user chooses which role to play
				System.out.println("*** Going to displayMultipleRoleDispatch");
				guiMultipleRoleDispatch.ViewMultipleRoleDispatch.
					displayMultipleRoleDispatch(theStage, user);
			}
		}
	
		
	/**********
	 * <p> Method: setup() </p>
	 *
	 * <p> Description: This method is called to reset the page and then populate it with new
	 * content.</p>
	 *
	 */
	protected static void doSetupAccount(Stage theStage, String invitationCode) {
		guiNewAccount.ViewNewAccount.displayNewAccount(theStage, invitationCode);
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
