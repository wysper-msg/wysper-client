package src;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class Response {
    JSONObject json;
    ArrayList<Message> messages;


    /**
     * Standard Response constructor
     * @param json, the json response from the server
     */
    public Response(JSONObject json) {
        this.json = json;

        JSONArray mList = (JSONArray) json.get("messages");
        this.messages = new ArrayList<>();

        for (Object value : mList) {
            if (value instanceof Message) {
                Message m = (Message) value;
                this.messages.add(m);
            }
        }
    }

    /**
     * @return a list of Message objects from the json response
     */
    public ArrayList<Message> getMessages() {
        return this.messages;
    }

    public static void main(String[] args) {
        System.out.println("Response main");

        JSONObject j = new JSONObject();
        Message m1 = new Message("corey", "what's up");
        Message m2 = new Message("corey", "hello");

        JSONArray mList = new JSONArray();
        mList.add(m1);
        mList.add(m2);

        j.put("messages", mList);

        Response r = new Response(j);

        ArrayList<Message> l1 = r.getMessages();

        for (Message m : l1) {
            System.out.println(m);
        }
    }



}
