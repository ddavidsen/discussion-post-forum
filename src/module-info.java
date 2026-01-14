/**
 * Java module definition for the FoundationsF25 JavaFX application.
 * 
 */
module FoundationsF25 {
	requires javafx.controls;
	requires java.sql;
	
	opens applicationMain to javafx.graphics, javafx.fxml;
}
