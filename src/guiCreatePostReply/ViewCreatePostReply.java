package guiCreatePostReply;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
import database.Database;
import entityClasses.User;

/*******
 * <p> Title: GUIAddRemoveRolesPage Class. </p>
 * 
 * <p> Description: The Java/FX-based page for changing the assigned roles to users.</p>
 * 
 * 
 * @author Diana Davidsen
 * 
 *  
 */

public class ViewCreatePostReply {
	
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
	
	//This is for testing PostClass in postTestingTestingAutomation
	public static Label label_TestStatus = new Label();
	
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);
	
	// When no user has been selected, only Area 2a is shown.  If a user in the ComboBox in Area 1a
	// has been specified, then Area 2b is made visible.
	
	// Area 2a: This allows the admin to select a user of the system as the first step in adding or
	// removing a role.  The act of selecting a user causes the change is the GUI.  The Admin does
	// not need to push a button to make this happen.
	protected static Label label_SelectPostorReply = new Label("Do you want to make a new post or reply?");
	protected static ComboBox <String> combobox_SelectPostorReply = new ComboBox <String>();
	
	// Area 2b: Widgets shown when "post" is selected
	protected static Label label_SelectThread = new Label("Thread for this post:");
	protected static List<String> threadList = new ArrayList<String>();
	public static ComboBox <String> combobox_SelectThread = new ComboBox <String>();
	public static Label label_PostTitle = new Label("Enter a title:");
	public static TextField text_PostTitle = new TextField();
	public static Label label_PostBody = new Label("Enter post body text:");
	public static TextField text_PostBody = new TextField();
	protected static Button button_CreatePost = new Button("Create Post");
	
	// Area 2b: Widgets shown when "reply" is selected
	protected static Label label_PostID = new Label("Enter the PostID to reply to:");
	protected static TextField text_PostID = new TextField();
	protected static Label label_ReplyBody = new Label("Enter reply body text:");
	protected static TextField text_ReplyBody = new TextField();
	protected static Button button_CreateReply = new Button("Create Reply");
		
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator4 = new Line(20, 525, width-20,525);
	
	// GUI Area 3: This is last of the GUI areas.  It is used for quitting the application, logging
	// out, and on other pages a return is provided so the user can return to a previous page when
	// the actions on that page are complete.  Be advised that in most cases in this code, the 
	// return is to a fixed page as opposed to the actual page that invoked the pages.
	protected static Button button_Return = new Button("Return");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");
	
	// Alerts for invalid inputs
	protected static Alert alert = new Alert(AlertType.INFORMATION);

	// This is the end of the GUI objects for the page.
	
	// These attributes are used to configure the page and populate it with this user's information
	private static ViewCreatePostReply theView;	// Used to determine if instantiation of the class
												// is needed
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;		

	protected static Stage theStage;			// The Stage that JavaFX has established for us
	protected static Pane theRootPane;			// The Pane that holds all the GUI widgets 
	protected static User theUser;				// The current user of the application
	
	public static Scene theCreatePostReplyScene = null;	// The Scene each invocation populates
	protected static String theSelectedOption = "";	// The user whose roles are being updated
	protected static String thePostThread = "";		// The role being added
	protected static String theParentPostID = "";		// The roles being removed



	/*-*******************************************************************************************

	Constructors
	
	*/

	/**********
	 * <p> Method: displayAddRemoveRoles(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the AddRevove page to be displayed.
	 * 
	 * It first sets up very shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup.  If not, it instantiates the class, 
	 * initializes all the static aspects of the GUI widgets (e.g., location on the page, font,
	 * size, and any methods to be performed).
	 * 
	 * After the instantiation, the code then populates the elements that change based on the user
	 * and the system's current state.  It then sets the Scene onto the stage, and makes it visible
	 * to the user.
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the User whose roles will be updated
	 *
	 */
	public static void displayCreatePostReply(Stage ps, User user) {
		
		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;
		
		// If not yet established, populate the static aspects of the GUI by creating the 
		// singleton instance of this class
		if (theView == null) theView = new ViewCreatePostReply();
		
		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.  This page is different from the others.  Since there are two 
		// modes (1: user has not been selected, and 2: user has been selected) there are two
		// lists of widgets to be displayed.  For this reason, we have implemented the following 
		// two controller methods to deal with this dynamic aspect.
		ControllerCreatePostReply.repaintTheWindow();
	}

	
	/**********
	 * <p> Method: GUIAddRemoveRolesPage() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object. 
	 * 
	 * This is a singleton, so this is performed just one.  Subsequent uses fill in the changeable
	 * fields using the displayAddRempoveRoles method.</p>
	 * 
	 */
	public ViewCreatePostReply() {
		
		// This page is used by all roles, so we do not specify the role being used		
			
		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theCreatePostReplyScene = new Scene(theRootPane, width, height);
		
		// Populate the window with the title and other common widgets and set their static state
		
		// GUI Area 1
		label_PageTitle.setText("Create Post/Reply Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
		
		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((event) -> 
			{guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser); });
		
		// GUI Area 2a
		setupLabelUI(label_SelectPostorReply, "Arial", 20, 300, Pos.BASELINE_LEFT, 20, 130);
		
		setupComboBoxUI(combobox_SelectPostorReply, "Dialog", 16, 250, 400, 125);
		String[] optionList = {"Post", "Reply"};	
		combobox_SelectPostorReply.setItems(FXCollections.observableArrayList(optionList));
		combobox_SelectPostorReply.getSelectionModel().select(0);
		combobox_SelectPostorReply.getSelectionModel().selectedItemProperty()
    	.addListener((ObservableValue<? extends String> observable, 
    		String oldvalue, String newValue) -> {ControllerCreatePostReply.repaintTheWindow();});
		
		// GUI Area 2b
		// Post widgets
		setupLabelUI(label_SelectThread, "Arial", 16, 300, Pos.BASELINE_LEFT, 20, 170);
		
		setupComboBoxUI(combobox_SelectThread, "Dialog", 16, 150, 280, 170);
		List<String> threadList = theDatabase.getThreadList();
		
		combobox_SelectThread.setItems(FXCollections.observableArrayList(threadList));
		combobox_SelectThread.getSelectionModel().select(0);
		combobox_SelectThread.getSelectionModel().selectedItemProperty()
    	.addListener((ObservableValue<? extends String> observable, 
    		String oldvalue, String newValue) -> {ControllerCreatePostReply.performCreateReply();});
		
		setupLabelUI(label_PostTitle, "Arial", 16, 300, Pos.BASELINE_LEFT, 20, 210);
		setupTextUI(text_PostTitle, "Arial", 18, 300, Pos.BASELINE_LEFT, 280, 210, true);
		setupLabelUI(label_PostBody, "Arial", 16, 300, Pos.BASELINE_LEFT, 20, 250);
		setupTextUI(text_PostBody, "Arial", 18, 300, Pos.BASELINE_LEFT, 20, 290, true);
		setupButtonUI(button_CreatePost, "Dialog", 18, 210, Pos.CENTER, 300, 450);
		ViewCreatePostReply.button_CreatePost.setOnAction((event) -> 
		{ControllerCreatePostReply.performCreatePost(); });
		
		
		// Reply widgets
		setupLabelUI(label_PostID, "Arial", 16, 300, Pos.BASELINE_LEFT, 20, 170);
		setupTextUI(text_PostID, "Arial", 18, 300, Pos.BASELINE_LEFT, 280, 170, true);
		setupLabelUI(label_ReplyBody, "Arial", 16, 300, Pos.BASELINE_LEFT, 20, 210);
		setupTextUI(text_ReplyBody, "Arial", 18, 300, Pos.BASELINE_LEFT, 20, 250, true);
		setupButtonUI(button_CreateReply, "Dialog", 18, 210, Pos.CENTER, 300, 450);
		ViewCreatePostReply.button_CreateReply.setOnAction((event) -> 
		{ControllerCreatePostReply.performCreateReply(); });
		
		// GUI Area 3		
		setupButtonUI(button_Return, "Dialog", 18, 210, Pos.CENTER, 20, 540);
		button_Return.setOnAction((event) -> {ControllerCreatePostReply.performReturn(); });

		setupButtonUI(button_Logout, "Dialog", 18, 210, Pos.CENTER, 300, 540);
		button_Logout.setOnAction((event) -> {ControllerCreatePostReply.performLogout(); });
    
		setupButtonUI(button_Quit, "Dialog", 18, 210, Pos.CENTER, 570, 540);
		button_Quit.setOnAction((event) -> {ControllerCreatePostReply.performQuit(); });
		
		// This is the end of the GUI Widgets for the page
		
		// Due to the very dynamic nature of this page, setting the widget into the Root Pane has 
		// has been delegated to the repaintTheWindow and doSelectUser controller methods.
		// Don't follow this pattern if formatting of the page does not change dynamically.
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
		
		//added for testing purposes 
		label_TestStatus.setText(message);
		
		alert.showAndWait();
	}
}