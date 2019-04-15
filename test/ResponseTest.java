package test;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import src.Message;
import src.Response;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class ResponseTest {

    ArrayList<Message> msgArr;

    /**
     * Initialize an array of messages to test against
     */
    @Before
    public void setup() {
        msgArr = new ArrayList<Message>();

        Message m1 = new Message("Corey", "Hello");
        Message m2 = new Message("Alex", "Hi");
        Message m3 = new Message("Aayushi", "Shh");

        msgArr.add(m1);
        msgArr.add(m2);
        msgArr.add(m3);
    }

    /**
     * Test the standard Response constructor that
     * constructs a list of messages from a JSON
     * object
     */
    @Test
    public void constructorTest() {
        // First mimic a client response
        JSONObject j = new JSONObject();
        JSONArray arr = new JSONArray();

        // Create the JSON object using Message.toJSON
        // since we test that separately
        for (int i = 0; i < msgArr.size(); i++) {
            arr.add(msgArr.get(i).toJSON());
        }
        j.put("messages", arr);
        Response r = new Response(j);

        // Make sure the list of messages we get is the same as the expected
        for (int i = 0; i < r.getMessages().size(); i++) {
            assertEquals(r.getMessages().get(i).getBody(), msgArr.get(i).getBody());
            assertEquals(r.getMessages().get(i).getUsername(), msgArr.get(i).getUsername());
            assertEquals(r.getMessages().get(i).getTimestamp(), msgArr.get(i).getTimestamp());
        }
    }
}
