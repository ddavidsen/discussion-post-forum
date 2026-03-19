package guiEditPostReply;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import entityClasses.User;

/**
* <p> Title: ViewEditPostReply Class. </p>
*
* <p> Description: This method helps edit a post and to view a reply.</p>
* 
* 
*/


public class ViewEditPostReply {
	
	/*-*******************************************************************************************

	Attributes
	
	*/
	
	// These are the application values required by the user interface
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	
	// These are the widget attributes for the GUI. There are 3 areas for this GUI.
	
	// GUI Area 1: It informs the user about the purpose of this page, whose account is being used,
	// and a button to allow this user to update the account settings.
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Account Update");
	
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);
	
	// When no user has been selected, only Area 2a is shown.  If a user in the ComboBox in Area 1a
	// has been specified, then Area 2b is made visible.
	
	// Area 2a: This allows the admin to select a user of the system as the first step in adding or
	// removing a role.  The act of selecting a user causes the change is the GUI.  The Admin does
	// not need to push a button to make this happen.
	protected static Label label_Reminder = new Label("Reminder: You may only edit your own posts!");
	
	// Area 2b: Labels and text boxes for postID and new body text
	protected static Label label_PostID = new Label("Enter the PostID to edit:");
	protected static TextField text_PostID = new TextField();
	protected static Label label_NewBody = new Label("Enter new body text:");
	protected static TextField text_NewBody = new TextField();
	protected static Button button_EditPost = new Button("Edit this Post/Reply");
		
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator4 = new Line(20, 525, width-20,525);
	
	// GUI Area 3: This is last of the GUI areas.  It is used for quitting the application, logging
	// out, and on other pages a return is provided so the user can return to a previous page when
	// the actions on that page are complete.  Be advised that in most cases in this code, the 
	// return is to a fixed page as opposed to the actual page that invoked the pages.
	protected static Button button_Return = new Button("Return");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");
	
	// Alerts for notifications to the user
	protected static Alert alert = new Alert(AlertType.INFORMATION);
	protected static Alert comfirm = new Alert(AlertType.CONFIRMATION);

	// This is the end of the GUI objects for the page.
	
	// These attributes are used to configure the page and populate it with this user's information
	private static ViewEditPostReply theView;	// Used to determine if instantiation of the class
												// is needed
	

	protected static Stage theStage;			// The Stage that JavaFX has established for us
	protected static Pane theRootPane;			// The Pane that holds all the GUI widgets 
	protected static User theUser;				// The current user of the application
	
	public static Scene theEditPostReplyScene = null;	// The Scene each invocation populates
	protected static String theSelectedOption = "";	// The user whose roles are being updated
	protected static String thePostThread = "";		// The role being added
	protected static String theParentPostID = "";		// The roles being removed



	/*-*******************************************************************************************

	Constructors
	
	*/

	/**********
	 * <p> Method: displayEditPostReply (Stage ps, User user) </p>
	 *
	 * <p> Description: This method enables for the users to be able to edit the replies under each
	 * post.
	 *
	 * Sets references to the JavaFX stage and the current User, created the singleton view instance if
	 * necessary, and shows the scene for editing a post or reply.
	 * 
	 *
	 * @param ps specifies the JavaFX Stage to be used for this GUI
	 *
	 * @param user the User currently logged in (used for display and permissions).
	 *
	 */

	public static void displayEditPostReply(Stage ps, User user) {
		
		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;
		
		// If not yet established, populate the static aspects of the GUI by creating the 
		// singleton instance of this class
		if (theView == null) theView = new ViewEditPostReply();
		
		// Update the current users username, since it may change between views of this page
		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
		
		// Set the title for the window
		ViewEditPostReply.theStage.setTitle("Operations Page");
		ViewEditPostReply.theStage.setScene(ViewEditPostReply.theEditPostReplyScene);
		ViewEditPostReply.theStage.show();
	}

	
	/**
	 * <p> Method: ViewEditPostReply() </p>
	 *
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object.
	 *
	 * This is a singleton, so this is performed just one.  Subsequent uses fill in the changeable
	 * fields using the ViewEditPostReply method.</p>
	 *
	 */

	public ViewEditPostReply() {
		
		// This page is used by all roles, so we do not specify the role being used		
			
		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theEditPostReplyScene = new Scene(theRootPane, width, height);
		
		// Populate the window with the title and other common widgets and set their static state
		
		// GUI Area 1
		label_PageTitle.setText("Edit Post/Reply Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
		
		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((event) -> 
			{guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser); });
		
		// GUI Area 2a
		setupLabelUI(label_Reminder, "Arial", 20, 300, Pos.BASELINE_LEFT, 20, 130);
		
		// GUI Area 2b
		setupLabelUI(label_PostID, "Arial", 16, 300, Pos.BASELINE_LEFT, 20, 170);
		setupTextUI(text_PostID, "Arial", 18, 300, Pos.BASELINE_LEFT, 280, 170, true);
		setupLabelUI(label_NewBody, "Arial", 16, 300, Pos.BASELINE_LEFT, 20, 210);
		setupTextUI(text_NewBody, "Arial", 18, 300, Pos.BASELINE_LEFT, 20, 250, true);
		setupButtonUI(button_EditPost, "Dialog", 18, 210, Pos.CENTER, 300, 450);
		ViewEditPostReply.button_EditPost.setOnAction((event) -> 
		{ControllerEditPostReply.performEditPostReply(); });
		
		// GUI Area 3		
		setupButtonUI(button_Return, "Dialog", 18, 210, Pos.CENTER, 20, 540);
		button_Return.setOnAction((event) -> {ControllerEditPostReply.performReturn(); });

		setupButtonUI(button_Logout, "Dialog", 18, 210, Pos.CENTER, 300, 540);
		button_Logout.setOnAction((event) -> {ControllerEditPostReply.performLogout(); });
    
		setupButtonUI(button_Quit, "Dialog", 18, 210, Pos.CENTER, 570, 540);
		button_Quit.setOnAction((event) -> {ControllerEditPostReply.performQuit(); });
		
		// This is the end of the GUI Widgets for the page
		
		// Set the widgets into the root pane
		ViewEditPostReply.theRootPane.getChildren().addAll(
				ViewEditPostReply.label_PageTitle, ViewEditPostReply.label_UserDetails,
				ViewEditPostReply.button_UpdateThisUser, ViewEditPostReply.line_Separator1,
				ViewEditPostReply.label_Reminder,
				ViewEditPostReply.label_PostID, ViewEditPostReply.text_PostID,
				ViewEditPostReply.label_NewBody, ViewEditPostReply.text_NewBody,
				ViewEditPostReply.button_EditPost,
				ViewEditPostReply.line_Separator4, 
				ViewEditPostReply.button_Return,
				ViewEditPostReply.button_Logout,
				ViewEditPostReply.button_Quit);
	}	

	/*-*******************************************************************************************

	Helper methods used to minimizes the number of lines of code needed above
	
	*/

	/**********
	 * Private local method to initialize the standard fields for a label
	 * 
	 * @param l		The Label object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x,
			double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);		
	}
	
	
	/**********
	 * Private local method to initialize the standard fields for a button
	 * 
	 * @param b		The Button object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	protected static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x,
			double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}

	/**********
	 * Private local method to initialize the standard fields for a ComboBox
	 * 
	 * @param c		The ComboBox object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the ComboBox
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	protected static void setupComboBoxUI(ComboBox <String> c, String ff, double f, double w,
			double x, double y){
		c.setStyle("-fx-font: " + f + " " + ff + ";");
		c.setMinWidth(w);
		c.setLayoutX(x);
		c.setLayoutY(y);
	}
	
	/**********
	 * Private local method to initialize the standard fields for a text field
	 */
	private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, boolean e){
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);		
		t.setEditable(e);
	}
	
	protected static void showAlert(String message) {
		alert.setTitle("Create Post/Reply Message");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}