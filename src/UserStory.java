import java.io.Serializable;
import java.util.ArrayList;

public class UserStory  implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private int points;
	private String author;
	private String status;
	
	public  UserStory(String n, int points, String a, String s) {
		this.name = n;
		this.author = a;
		//this.status = s;
	}
			
	public int getStoryPoints() {
		return this.points;
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
