import org.json.simple.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;


/**
 * Stores Message data
 */
public class Message{
    String username;        // Username of the sender
    String body;            // Message body
    Timestamp timestamp;    // Time the message was sent

    /**
     * Standard message constructor
     * Adds timestamp on creation
     * @param username username of sender
     * @param text message body
     */
    public Message(String username, String text) {
        this.username = username;
        this.body = text;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    /**
     * JSON Message constructor
     * Creates a Message from its JSON representation
     * @param j a json representation of a message
     */
    public Message(JSONObject j) {
        if (j.containsKey("username")) {
            this.username = j.get("username").toString();
        }
        if (j.containsKey("timestamp")) {
            this.timestamp = Timestamp.valueOf((String)j.get("timestamp")) ;
        }
        else {
            this.timestamp = new Timestamp(System.currentTimeMillis());
        }
        if (j.containsKey("body")) {
            this.body = j.get("body").toString();
        }
    }

    /**
     * Helper function to format messages in a String
     * @param size size of the space string to make
     * @return a string of size size of all spaces
     */
    private String makeSpaceString(int size) {
        String ret = new String();
        for (int i = 0; i < size; i++) {
            ret += " ";
        }
        return ret;
    }

    /**
     * Displays this Message object in a String
     * @return a String representation of the message containing sender,
     * timestamp, and message text
     */
    @Override
    public String toString() {
        int wrapLen = 50;
        int userPad = 30;
        String[] bodyArray = this.body.split("\\s+");

        String ret = "";
        ret += this.username;
        ret += makeSpaceString(userPad - this.username.length());

        int lineCount = 0;
        for (int i = 0; i < bodyArray.length; i++) {
            // if this word fits on the current line
            if (bodyArray[i].length() + lineCount < wrapLen - 1) {
                ret += bodyArray[i] + " ";
                lineCount += bodyArray[i].length() + 1;
            }
            // if we can fit this word on a newline
            else if (bodyArray[i].length() < wrapLen - 1) {
                ret += "\n" + makeSpaceString(userPad) + bodyArray[i] + " ";
                lineCount = bodyArray[i].length() + 1;
            }
            // if this word is larger than wrapLen
            else {
                int ind = wrapLen - lineCount - 1;
                ret += bodyArray[i].substring(0, wrapLen - lineCount - 1) + "-\n";
                ret += makeSpaceString(userPad);
                while (bodyArray[i].substring(ind).length() > wrapLen - 1) {
                    ret += bodyArray[i].substring(ind, ind+wrapLen-1) + "-\n";
                    ret += makeSpaceString(userPad);
                    ind += wrapLen;
                }
                ret += bodyArray[i].substring(ind) + " ";
                lineCount = bodyArray[i].substring(ind).length() + 1;
            }
        }
        ret += makeSpaceString(wrapLen - lineCount + 2);
        ret += new SimpleDateFormat("hh:mm:ss").format(timestamp);
        return ret;
    }

    /**
     * Converts this Message to JSON
     * @return a json representation of the message
     */
    public JSONObject toJSON() {
        JSONObject j = new JSONObject();
        j.put("username", this.username);
        j.put("body", this.body);
        j.put("timestamp", this.timestamp.toString());

        return j;
    }

    /**
     * @return the username who created the message
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * @return the body of the message
     */
    public String getBody() {
        return this.body;
    }

    /**
     * @return a LocalDateTime object of when the message was created
     */
    public Timestamp getTimestamp() {
        return this.timestamp;
    }
}
