package entityClasses;

/*******
 * <p> Title: Post Class </p>
 * 
 * <p> Description: This Post class represents a post entity in the system.  It creates the post's
 *  details such as postID, author, title, body, and controls details such as parentThread, markedRead,
 *  numReplies, replyList </p>
 * 
 */ 

public class Post {
	
	/*
	 * These are the private attributes for this entity object
	 */
	protected int postID;
    protected String author;
    protected String title;
    protected String body;
    protected String parentThread;
    protected int numReplies;
    
    
    /*****
     * <p> Method: Post() </p>
     * 
     * <p> Description: This default constructor is not used in this system. </p>
     */
    public Post() {
    	
    }

    
    /*****
     * <p> Method: Post(String author, String title, String body, String parentThread) </p>
     * 
     * <p> Description: This constructor is used to establish post entity objects. </p>
     * 
     * @param author specifies the author who made this post
     * 
     * @param title specifies the the title for this post
     * 
     * @param body specifies the body text of this post
     * 
     * @param parentThread specifies the thread this post is in
     * 
     */
    // Constructor to initialize a new Post object
    public Post(String author, String title, String body, String parentThread) {
    	this.author = author;
    	this.title = title;
    	this.body = body;
    	this.parentThread = parentThread;
    	this.numReplies = 0;
    }

    
    /*****
     * <p> Method: int getPostID() </p>
     * 
     * <p> Description: This getter gets the postID attribute. </p>
     * 
     * @return the value of postID
     * 
     */
    public int getPostID() {
    	return postID;
    }
    
    /*****
     * <p> Method: String getAuthor() </p>
     * 
     * <p> Description: This getter gets the author attribute. </p>
     * 
     * @return the value of author
     * 
     */
    public String getAuthor() {
    	return author;
    }
    
    /*****
     * <p> Method: String getTitle() </p>
     * 
     * <p> Description: This getter gets the title attribute. </p>
     * 
     * @return the value of title
     * 
     */
    public String getTitle() {
    	return title;
    }
    
    /*****
     * <p> Method: String getBody() </p>
     * 
     * <p> Description: This getter gets the abody attribute. </p>
     * 
     * @return the value of body
     * 
     */
    public String getBody() {
    	return body;
    }
    
    /*****
     * <p> Method: void setBody() </p>
     * 
     * <p> Description: This setter sets the body attribute. Used when the author edits the post. </p>
     * 
     * @param body the new body text
     * 
     */
    public void setBody(String body) {
    	this.body = body;
    }
    
    /*****
     * <p> Method: String getParentThread() </p>
     * 
     * <p> Description: This getter gets the parentThread attribute. </p>
     * 
     * @return the value of parentThread
     * 
     */
    public String getParentThread() {
    	return parentThread;
    }
    
    /*****
     * <p> Method: int getNumReplies() </p>
     * 
     * <p> Description: This getter gets the numReplies attribute. </p>
     * 
     * @return the value of numReplies
     * 
     */
    public int getNumReplies() {
    	return numReplies;
    }



}