import java.io.Serializable;
import java.util.ArrayList;

public class UserStory  implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private String author;
	private String status;
	private ArrayList<String> storyPoints = new ArrayList<String>(); 
	
	public  UserStory(String n, String a, String s) {
		this.name = n;
		this.author = a;
		//this.status = s;
	}
	
	public ArrayList<String> getStoryPoints() {
		return this.storyPoints;
	}
	
	public void addStoryPoint(String s) {
		// TO DO
	}
	
	public void removeStoryPoint(int index) {
		// TO DO
	}
	
	public String getAuthor() {
		return this.author;
	}
	
	public String getStatus() {
		return this.status;
	}
	
	public void setStatus(String s) {
		this.status = s;
	}
	
	public String getName() {
		return this.name;
	}

}
