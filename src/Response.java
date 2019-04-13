package src;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Response abstracts the server response and converts JSON to Message objects
 */
public class Response {

    JSONObject json;                // store server JSON response
    ArrayList<Message> messages;    // store list of messages from the response

    /**
     * Standard Response constructor
     * Gets messages from JSONObject and adds them to this.messages
     * @param json - the json response from the server
     */
    public Response(JSONObject json) {
        this.json = json;

        JSONArray mList = (JSONArray) json.get("messages");
        this.messages = new ArrayList<>();

        // pull messages from JSON response
        if(mList != null) {
            for ( int i = 0; i < mList.size(); i++) {
                Message m = new Message((JSONObject)mList.get(i));
                this.messages.add(m);
            }
        } else {
            System.err.println();
        }
    }

    /**
     * Gets the messages stored in this Response object
     * @return a list of Message objects from the json response
     */
    public ArrayList<Message> getMessages() {
        return this.messages;
    }

}
