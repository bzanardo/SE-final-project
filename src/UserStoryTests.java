import org.junit.Test;
import junit.framework.TestCase;

public class UserStoryTests extends TestCase {
	
	@Test
	public void test1() {
		UserStory story = new UserStory("test name", 2, "test author", "Backlog");
		int storyPoint = story.getStoryPoints();
		assertTrue(storyPoint == 2);
	}
	
	@Test
	public void test2() {
		UserStory story = new UserStory("test name", 2, "test author", "Backlog");
		String storyAuthor = story.getAuthor();
		assertTrue(storyAuthor == "test author");
	}
	
	@Test
	public void test3() {
		UserStory story = new UserStory("test name", 2, "test author", "Backlog");
		String storyStatus = story.getStatus();
		assertTrue(storyStatus == "Backlog");
	}
	
	@Test
	public void test4() {
		UserStory story = new UserStory("test name", 2, "test author", "Backlog");
		story.setStatus("In progress");
		String storyStatus = story.getStatus();
		assertTrue(storyStatus == "In progress");
	}
	
	@Test
	public void test5() {
		UserStory story = new UserStory("test name", 2, "test author", "Backlog");
		story.setNull();
		String storyName = story.getName();
		assertTrue(storyName == null);
	}
	
	@Test
	public void test6() {
		UserStory story = new UserStory("test name", 2, "test author", "Backlog");
		story.setAuthor("author");
		String storyAuthor = story.getAuthor();
		assertTrue(storyAuthor == "author");	
	}
	
	@Test
	public void test7() {
		UserStory story = new UserStory("test name", 2, "test author", "Backlog");
		story.setStoryPoints(3);
		int storyPoint = story.getStoryPoints();
		assertTrue(storyPoint == 3);
	}
	

}
