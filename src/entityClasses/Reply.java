package entityClasses;

/*******
 * <p> Title: Reply Class </p>
 * 
 * <p> Description: This Reply class represents a reply entity in the system.  It creates the reply's
 *  details such as postID, author, title, body, and controls details such as parentThread, markedRead,
 *  numReplies, replyList </p>
 * 
 * @author Diana Davidsen
 * 
 * 
 */ 

public class Reply extends Post {
	
	/*
	 * These are the private attributes for this entity object
	 */
	private int parentPostID;
    
    
    /*****
     * <p> Method: Reply() </p>
     * 
     * <p> Description: This default constructor is not used in this system. </p>
     */
    public Reply() {
    	
    }

    
    /*****
     * <p> Method: Reply(int postID, String author, String title, String body,
     *  String parentThread, int parentPost) </p>
     * 
     * <p> Description: This constructor is used to establish post entity objects. </p>
     * 
     * @param author specifies the author who made this post
     * 
     * @param body specifies the body text of this post
     *  
     * @param parentPostID specifies the post this reply is under
     * 
     */
    // Constructor to initialize a new Reply object
    public Reply(String author, String body, int parentPostID) {
    	this.author = author;
    	this.body = body;
    	this.numReplies = 0;
    	this.parentPostID = parentPostID;
    }

    /*****
     * <p> Method: String getParentPost() </p>
     * 
     * <p> Description: This getter gets the parentPost attribute. </p>
     * 
     * @return the value of parentPost
     * 
     */
    public int getParentPostID() {
    	return this.parentPostID;
    }


}