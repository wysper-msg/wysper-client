import org.json.simple.JSONObject;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class MessageTest {

  /** Test the standard Message constructor */
  @Test
  public void constructorTest() {
    Message m = new Message("Corey", "Hello!");
    String expected = "Corey";
    String actual = m.getUsername();
    assertEquals(expected, actual);

    expected = "Hello!";
    actual = m.getBody();
    assertEquals(expected, actual);
  }

  /** Test creating a Message object from a JSON object */
  @Test
  public void fromJSONTest() {
    JSONObject j = new JSONObject();
    j.put("username", "Corey");
    j.put("body", "Hello!");
    Message m = new Message(j);

    String expected = "Corey";
    String actual = m.getUsername();
    assertEquals(expected, actual);

    expected = "Hello!";
    actual = m.getBody();
    assertEquals(expected, actual);
  }

  /** Test converting a message object to JSON format */
  @Test
  public void toJSONTest() {
    Message m = new Message("Alex", "Moe Monday");

    JSONObject j = m.toJSON();

    String expected = "Alex";
    String actual = (String) j.get("username");
    assertEquals(expected, actual);

    expected = "Moe Monday";
    actual = (String) j.get("body");
    assertEquals(expected, actual);
  }
}
