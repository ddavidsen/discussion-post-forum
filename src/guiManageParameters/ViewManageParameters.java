package guiManageParameters;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
* <p> Title: ViewManageParameters Class. </p>
*
* <p> Description: JavaFX page for managing application parameters where it allows for create, edit, delete. Those view exposes static GUI widgets and helper methods used by
* the view exposes static GUI widgets and helper methods used by the Controller Manage Parameters class to implement user interactions for creating, editing and deleting parameters.</p>
*
* @author Diana Davidsen
* 
*/


public class ViewManageParameters {
	
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
	protected static Label label_SelectAction = new Label("Select an action to perform:");
	protected static ComboBox <String> combobox_SelectAction = new ComboBox <String>();
	
	// Area 2b: When a user has been selected these widgets are shown and can be used
	// Create widgets
	protected static Label label_EnterParameter = new Label("Enter new parameter:");
	protected static TextField text_EnterParameter = new TextField();
	protected static Button button_CreateParameter = new Button("Create Parameter");
	
	// Edit widgets
	protected static Label label_SelectEditParameter = new Label("Select parameter number to edit:");
	protected static TextField text_SelectParameter = new TextField();
	protected static Label label_EnterNewParameter = new Label("Enter new parameter:");
	protected static TextField text_EnterNewParameter = new TextField();
	protected static Button button_EditParameter = new Button("Edit Parameter");
	
	// Delete widgets
	protected static Label label_SelectDeleteParameter = new Label("Select parameter to delete:");
	protected static Button button_DeleteParameter = new Button("Delete Parameter");
		
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator4 = new Line(20, 525, width-20,525);
	
	// Alert used when the Admin tries to remove their own admin role
	protected static Alert alert = new Alert(AlertType.INFORMATION);
	protected static Alert confirm = new Alert(AlertType.CONFIRMATION);
	
	// GUI Area 3: This is last of the GUI areas.  It is used for quitting the application, logging
	// out, and on other pages a return is provided so the user can return to a previous page when
	// the actions on that page are complete.  Be advised that in most cases in this code, the 
	// return is to a fixed page as opposed to the actual page that invoked the pages.
	protected static Button button_Return = new Button("Return");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

	// This is the end of the GUI objects for the page.
	
	// These attributes are used to configure the page and populate it with this user's information
	private static ViewManageParameters theView;	// Used to determine if instantiation of the class
												// is needed	

	protected static Stage theStage;			// The Stage that JavaFX has established for us
	protected static Pane theRootPane;			// The Pane that holds all the GUI widgets 
	protected static User theUser;				// The current user of the application
	
	public static Scene theAddRemoveRolesScene = null;	// The Scene each invocation populates
	protected static String theSelectedOption = "";	// The user whose roles are being updated
	protected static int theSelectedParameter;



	/*-*******************************************************************************************

	Constructors
	
	*/
	
	/**
	 * <p> Method: displayManageParameters(Stage ps, User user) </p>
	 *
	 * <p> Description: This method is the single entry point from outside this package to
	 * display the manage parameters page. <p>
	 * 
	 *  Saves the provided JavaFX stage and the current User.
	 *  Lazily constructs the singleton view instance (initializes widgets and event handlers)if needed,
	 *  updates the dynamic GUI content by invoking Controller ManageParameter#repaintTheWindow()}.
	 *  which places the correct widgets into the root pane based on the selected action.
	 * 
	 *  @param ps JavaFX stage used for this GUI
	 *  @param user the current User whose Information populates the page
	 *
	 */

	public static void displayManageParameters(Stage ps, User user) {
		
		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;
		
		// If not yet established, populate the static aspects of the GUI by creating the 
		// singleton instance of this class
		if (theView == null) theView = new ViewManageParameters();
		
		// Update the current users username, since it may change between views of this page
		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
		
		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.  This page is different from the others.  Since there are two 
		// modes (1: user has not been selected, and 2: user has been selected) there are two
		// lists of widgets to be displayed.  For this reason, we have implemented the following 
		// two controller methods to deal with this dynamic aspect.
		ControllerManageParameters.repaintTheWindow();
	}

	
	/**
	 * <p> Method: ViewManageParameters() </p>
	 *
	 * <p> Description: Initialize all static GUI widgets, layouts, and event handlers for the Manage parameters page. 
	 * This constructor performs the the following actions.
	 *
	 * This is a singleton, so this is performed just one.  Subsequent uses fill in the changeable
	 * fields using the ViewManageParameters method.
	 * Configure labels, separators, buttons, combo boxes, and text fields, register event handlers for the action ComboBox.</p>
	 *
	 *
	 */

	public ViewManageParameters() {
		
		// This page is used by all roles, so we do not specify the role being used		
			
		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theAddRemoveRolesScene = new Scene(theRootPane, width, height);
		
		// Populate the window with the title and other common widgets and set their static state
		
		// GUI Area 1
		label_PageTitle.setText("Manage Parameters Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
		
		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((event) -> 
			{guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser); });
		
		// GUI Area 2a
		setupLabelUI(label_SelectAction, "Arial", 20, 300, Pos.BASELINE_LEFT, 20, 130);
		
		setupComboBoxUI(combobox_SelectAction, "Dialog", 16, 250, 280, 125);
		String[] optionList = {"Create", "Edit", "Delete"};
		combobox_SelectAction.setItems(FXCollections.observableArrayList(optionList));
		combobox_SelectAction.getSelectionModel().select(0);
		combobox_SelectAction.getSelectionModel().selectedItemProperty()
    	.addListener((ObservableValue<? extends String> observable, 
    		String oldvalue, String newValue) -> {ControllerManageParameters.repaintTheWindow();});
		
		// GUI Area 2b
		// Create widgets
		setupLabelUI(label_EnterParameter, "Arial", 16, 300, Pos.BASELINE_LEFT, 20, 170);
		setupTextUI(text_EnterParameter, "Arial", 16, 300, Pos.BASELINE_LEFT, 280, 170, true);	
		setupButtonUI(button_CreateParameter, "Dialog", 16, 210, Pos.CENTER, 300, 450);
		button_CreateParameter.setOnAction((event) -> 
		{ControllerManageParameters.performCreateParameter(); });
		
		// Edit widgets
		setupLabelUI(label_SelectEditParameter, "Arial", 16, 300, Pos.BASELINE_LEFT, 20, 170);
		setupTextUI(text_SelectParameter, "Arial", 16, 300, Pos.BASELINE_LEFT, 280, 170, true);	
		
		setupLabelUI(label_EnterNewParameter, "Arial", 16, 300, Pos.BASELINE_LEFT, 20, 210);
		setupTextUI(text_EnterNewParameter, "Arial", 16, 300, Pos.BASELINE_LEFT, 280, 210, true);	
		setupButtonUI(button_EditParameter, "Dialog", 16, 210, Pos.CENTER, 300, 450);
		button_EditParameter.setOnAction((event) -> 
		{ControllerManageParameters.performEditParameter(); });
		
		// Delete widgets
		setupLabelUI(label_SelectDeleteParameter, "Arial", 16, 300, Pos.BASELINE_LEFT, 20, 170);	
		setupButtonUI(button_DeleteParameter, "Dialog", 16, 210, Pos.CENTER, 300, 450);
		button_DeleteParameter.setOnAction((event) -> 
		{ControllerManageParameters.performDeleteParameter(); });
		
		// GUI Area 3		
		setupButtonUI(button_Return, "Dialog", 18, 210, Pos.CENTER, 20, 540);
		button_Return.setOnAction((event) -> {ControllerManageParameters.performReturn(); });

		setupButtonUI(button_Logout, "Dialog", 18, 210, Pos.CENTER, 300, 540);
		button_Logout.setOnAction((event) -> {ControllerManageParameters.performLogout(); });
    
		setupButtonUI(button_Quit, "Dialog", 18, 210, Pos.CENTER, 570, 540);
		button_Quit.setOnAction((event) -> {ControllerManageParameters.performQuit(); });
		
		// Setup alert
		confirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
		
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
	 * Private local method to initialize the standard fields for a ComboBox
	 * 
	 * @param c		The ComboBox object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the ComboBox
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	protected static void setupIntComboBoxUI(ComboBox <Integer> c, String ff, double f, double w,
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
}