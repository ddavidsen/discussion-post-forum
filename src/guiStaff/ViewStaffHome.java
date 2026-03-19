package guiStaff;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Optional;

import database.Database;
import entityClasses.User;
import guiUserUpdate.ViewUserUpdate;


/*******
 * <p> Title: GUIReviewerHomePage Class. </p>
 * 
 * <p> Description: The Java/FX-based Staff Home Page.  The page is a stub for some role needed for
 * the application.  The widgets on this page are likely the minimum number and kind for other role
 * pages that may be needed.</p>
 * 
 */

public class ViewStaffHome {
	
	/*-*******************************************************************************************

	Attributes
	
	 */
	
	// These are the application values required by the user interface
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;


	// These are the widget attributes for the GUI. There are 3 areas for this GUI.
	
	// GUI Area 1: It informs the user about the purpose of this page, whose account is being used,
	// and a button to allow this user to update the account settings
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Account Update");
	
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);

	// GUI ARea 2: This is a stub, so there are no widgets here.  For an actual role page, this are
	// would contain the widgets needed for the user to play the assigned role.
	// Added buttons for discussion post system
	protected static Button button_ViewPosts = new Button("View Posts");
	protected static Button button_CreatePostReply = new Button("Create Post/Reply");
	protected static Button button_EditPostReply = new Button("Edit Post/Reply");
	protected static Button button_DeletePostReply = new Button("Delete Post/Reply");
	protected static Button button_ManageThreads = new Button("Manage Threads");
	
	// Added buttons for parameter CRUD functions
	protected static Button button_ViewParameters = new Button("View Parameters");
	protected static Button button_ManageParameters = new Button("Manage Parameters");
	
	// Added buttons for grading system
	protected static Button button_ViewGrades = new Button("View All Grades");
	protected static Button button_ManageGrades = new Button("Manage Grades");
	
	// Added buttons for admin requests system
	protected static Button button_ViewRequests = new Button("View Admin Requests");
	protected static Button button_OpenRequest = new Button("Open Request");
	
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator4 = new Line(20, 525, width-20,525);
	
	// GUI Area 3: This is last of the GUI areas.  It is used for quitting the application and for
	// logging out.
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

	// This is the end of the GUI objects for the page.
	
	// These attributes are used to configure the page and populate it with this user's information
	private static ViewStaffHome theView;		// Used to determine if instantiation of the class
												// is needed

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;			// The Stage that JavaFX has established for us	
	protected static Pane theRootPane;			// The Pane that holds all the GUI widgets
	protected static User theUser;				// The current logged in User
	

	private static Scene theViewStaffHomeScene;	// The shared Scene each invocation populates
	protected static final int theRole = 2;		// Admin: 1; Staff: 2; Student: 3

	/*-*******************************************************************************************

	Constructors
	
	 */


	/**********
	 * <p> Method: displayStaffHome(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the Staff Home page to be displayed.
	 * 
	 * It first sets up every shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup.  If not, it instantiates the class, 
	 * initializes all the static aspects of the GIUI widgets (e.g., location on the page, font,
	 * size, and any methods to be performed).
	 * 
	 * After the instantiation, the code then populates the elements that change based on the user
	 * and the system's current state.  It then sets the Scene onto the stage, and makes it visible
	 * to the user.
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the User for this GUI and it's methods
	 * 
	 */
	public static void displayStaffHome(Stage ps, User user) {
		
		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;
		
		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewStaffHome();		// Instantiate singleton if needed
		
		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.
		theDatabase.getUserAccountDetails(user.getUserName());
		applicationMain.FoundationsMain.activeHomePage = theRole;
		applicationMain.FoundationsMain.activeUser = theUser;
		
		label_UserDetails.setText("User: " + theUser.getUserName());
				
		// Set the title for the window, display the page, and wait for the Admin to do something
		theStage.setTitle("Staff Home Page");
		theStage.setScene(theViewStaffHomeScene);
		theStage.show();
	}
	
	/**********
	 * <p> Method: ViewStaffHome() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object.</p>
	 * 
	 * This is a singleton and is only performed once.  Subsequent uses fill in the changeable
	 * fields using the displayStaffHomeHome method.</p>
	 * 
	 */
	private ViewStaffHome() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theViewStaffHomeScene = new Scene(theRootPane, width, height);	// Create the scene
		
		// Set the title for the window
		
		// Populate the window with the title and other common widgets and set their static state
		
		// GUI Area 1
		label_PageTitle.setText("Staff Home Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
		
		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((event) ->
			{ViewUserUpdate.displayUserUpdate(theStage, theUser); });
		
		setupButtonUI(button_ViewRequests, "Dialog", 16, 210, Pos.CENTER, 570, 100);
		button_ViewRequests.setOnAction((event) -> 
			{ControllerStaffHome.viewAdminRequests(); });
		
		setupButtonUI(button_OpenRequest, "Dialog", 16, 210, Pos.CENTER, 570, 130);
		button_OpenRequest.setOnAction((event) -> 
			{ControllerStaffHome.openRequest(); });
		
		// GUI Area 2
		
		setupButtonUI(button_ViewPosts, "Dialog", 16, 210, Pos.CENTER, 20, 270);
		button_ViewPosts.setOnAction((event) -> {ControllerStaffHome.viewPosts(); });
		
		setupButtonUI(button_CreatePostReply, "Dialog", 16, 210, Pos.CENTER, 20, 320);
		button_CreatePostReply.setOnAction((event) -> {ControllerStaffHome.createPostReply(); });
		
		setupButtonUI(button_EditPostReply, "Dialog", 16, 210, Pos.CENTER, 20, 370);
		button_EditPostReply.setOnAction((event) -> {ControllerStaffHome.editPostReply(); });
		
		setupButtonUI(button_DeletePostReply, "Dialog", 16, 210, Pos.CENTER, 20, 420);
		button_DeletePostReply.setOnAction((event) -> {ControllerStaffHome.deletePostReply(); });
		
		setupButtonUI(button_ManageThreads, "Dialog", 16, 210, Pos.CENTER, 20, 470);
		button_ManageThreads.setOnAction((event) -> {ControllerStaffHome.manageThreads(); });
		
		setupButtonUI(button_ViewParameters, "Dialog", 16, 210, Pos.CENTER, 300, 270);
		button_ViewParameters.setOnAction((event) -> {ControllerStaffHome.viewParameters(); });
		
		setupButtonUI(button_ManageParameters, "Dialog", 16, 210, Pos.CENTER, 300, 320);
		button_ManageParameters.setOnAction((event) -> {ControllerStaffHome.manageParameters(); });
		
		setupButtonUI(button_ViewGrades, "Dialog", 16, 210, Pos.CENTER, 570, 270);
		button_ViewGrades.setOnAction((event) -> {ControllerStaffHome.viewGrades(); });
		
		setupButtonUI(button_ManageGrades, "Dialog", 16, 210, Pos.CENTER, 570, 320);
		button_ManageGrades.setOnAction((event) -> {ControllerStaffHome.manageGrades(); });
		
		// GUI Area 3
        setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
        button_Logout.setOnAction((event) -> {ControllerStaffHome.performLogout(); });
        
        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);
        button_Quit.setOnAction((event) -> {ControllerStaffHome.performQuit(); });

		// This is the end of the GUI initialization code
		
		// Place all of the widget items into the Root Pane's list of children
         theRootPane.getChildren().addAll(
			label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
	        line_Separator4, button_Logout, button_Quit,
	        button_ViewPosts, button_CreatePostReply, button_EditPostReply, button_DeletePostReply,
	        button_ManageThreads, button_ViewParameters, button_ManageParameters, 
	        button_ViewGrades, button_ManageGrades, button_ViewRequests, button_OpenRequest);
}
	
	
	/*-********************************************************************************************

	Helper methods to reduce code length

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
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, 
			double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}
	
	/**
	 * Helper method for getting the option chosen by the user.
	 * 
	 * @return the option chosen between view all posts or search by keyword.
	 */
	public static boolean showChoiceAlert() {
	    Alert choice = new Alert(Alert.AlertType.CONFIRMATION);
	    choice.setTitle("View Posts");
	    choice.setHeaderText("Would you like to search for specific posts?");
	    
	    ButtonType searchButton = new ButtonType("Keyword Search");
	    ButtonType allButton = new ButtonType("View All");

	    choice.getButtonTypes().setAll(searchButton, allButton);

	    Optional<ButtonType> result = choice.showAndWait();

	    return result.isPresent() && result.get() == searchButton;
	}
	
	/**
	 * Helper method to get keyword if user wants to search specific posts
	 * 
	 * @return the keyword
	 */
	public static String getKeyword() {
		TextInputDialog dialog = new TextInputDialog();
	    dialog.setTitle("Search");
	    dialog.setHeaderText("Enter a keyword, thread, author, unread, etc");
	    dialog.setContentText("Search for::");

	    Optional<String> result = dialog.showAndWait();
	    if (result.isPresent()) {
	        String keyword = result.get().trim();
	        if (!keyword.isEmpty()) {
	            return keyword;
	        }
	    }
	    return null;
	}
}
