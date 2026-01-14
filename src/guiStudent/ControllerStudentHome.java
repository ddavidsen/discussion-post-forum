package guiStudent;

import java.util.List;

import applicationMain.FoundationsMain;
import database.Database;
import guiCreatePostReply.ViewCreatePostReply;
import guiDeletePostReply.ViewDeletePostReply;
import guiEditPostReply.ViewEditPostReply;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

public class ControllerStudentHome {
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	**********************************************************************************************/
	
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	
	/**
	 * retrieves all posts from database and displays them.
	 * information alert is shown if no post exists.
	 */
	protected static void viewPosts() {
		boolean search = ViewStudentHome.showChoiceAlert();
		List<String> posts;
		
		// Choose between viewing all or search for specific posts
		if (search) {
			String keyword = ViewStudentHome.getKeyword();
			// Handle empty input
	        if (keyword == null) {
	            return;
	        }
	        posts = theDatabase.getPostListKeyword(keyword);
		}
		else {
			posts = theDatabase.getPostList();
		}
		
		if (posts.size() == 0) {
			System.out.println("No posts found.");
			
			Alert postsalert = new Alert(Alert.AlertType.INFORMATION);
			postsalert.setTitle("All Posts");
			postsalert.setHeaderText("No Posts");
			postsalert.setContentText("There are no posts in the system.");
			postsalert.showAndWait();
		} else {
			String list = "Post Format:\n" 
					+ "Thread, PostID, Original Post ID, # of Replies, Author, Title, Body Text, Read?\n"
					+ "***If Thread is null, the post is a reply.\n"
					+ "***If a reply's Parent Post ID is 0, the original post was deleted.\n";
			for (String u : posts) {
				System.out.println(u);
				list += u + "\n";
			}
			
			TextArea textArea = new TextArea(list.toString());
		    textArea.setEditable(false);
		    textArea.setWrapText(true);
		    textArea.setPrefWidth(600);
		    textArea.setPrefHeight(400);
		    textArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 13;");

		    // Use Alert with custom content
		    Alert postsAlert = new Alert(Alert.AlertType.INFORMATION);
		    postsAlert.setTitle("All Posts");
		    postsAlert.setHeaderText("List of Posts");
		    postsAlert.getDialogPane().setContent(textArea);
		    postsAlert.showAndWait();
		}
	}
	
	/**
	 * opens the interface for creating a new post reply
	 */
	protected static void createPostReply () {
		ViewCreatePostReply.displayCreatePostReply(ViewStudentHome.theStage, 
				ViewStudentHome.theUser);
	}
	
	/**
	 * opens the interface for editing a post reply
	 */
	protected static void editPostReply () {
		ViewEditPostReply.displayEditPostReply(ViewStudentHome.theStage, 
				ViewStudentHome.theUser);
	}
	
	/**
	 * opens the interface for deleting a post reply
	 */
	protected static void deletePostReply () {
		ViewDeletePostReply.displayDeletePostReply(ViewStudentHome.theStage, 
				ViewStudentHome.theUser);
	}
	
	protected static void viewGrades() {
		List<String> grades = theDatabase.getStudentGradesList(FoundationsMain.activeUser.getUserName());
		if (grades.size() == 0) {
			System.out.println("No posts found.");
			
			Alert postsalert = new Alert(Alert.AlertType.INFORMATION);
			postsalert.setTitle("My Grades");
			postsalert.setHeaderText("No Grades");
			postsalert.setContentText("You have no grades yet.");
			postsalert.showAndWait();
		} else {
			String list = "Grade Format:\n" 
					+ "GradeID, ID of the graded post, Student, Grader, Score (0-100), Optional Comments\n";
			for (String u :grades) {
				System.out.println(u);
				list += u + "\n";
			}
			
			TextArea textArea = new TextArea(list.toString());
		    textArea.setEditable(false);
		    textArea.setWrapText(true);
		    textArea.setPrefWidth(600);
		    textArea.setPrefHeight(400);
		    textArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 13;");

		    // Use Alert with custom content
		    Alert postsAlert = new Alert(Alert.AlertType.INFORMATION);
		    postsAlert.setTitle("My Grades");
		    postsAlert.setHeaderText("List of All Grades");
		    postsAlert.getDialogPane().setContent(textArea);
		    postsAlert.showAndWait();
		}
	}
	
	/**
	 * opens the interface for a student to update 
	 */
	protected static void performUpdate () {
		guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewStudentHome.theStage, ViewStudentHome.theUser);
	}	

	/**
	 * returns student to login screen
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewStudentHome.theStage);
	}
	
	/**
	 * exits program
	 */
	protected static void performQuit() {
		System.exit(0);
	}

}
