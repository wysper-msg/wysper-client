package src;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Communicator {
    private String _serverIP;
    private int _portNumber;

    /**
     * Constructor of Communicator
     * @param serverIP - IP address of the server to communicate to
     * @param portNumber - Port number of the server to communicate to
     */
    public Communicator(String serverIP, int portNumber) {
        _serverIP = serverIP;
        _portNumber = portNumber;
    }

    /**
     *
     * @return new messages vended by the server
     */

    public ArrayList<Message> getNewMessages() {
        ArrayList<Message> newMessages = new ArrayList<>();
        try {
            URL url = new URL(String.format("http://%s:%d/poll", _serverIP, _portNumber));
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            System.out.println("Response code: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer responseBuffer = new StringBuffer();
                while((inputLine = in.readLine()) != null) {
                    responseBuffer.append(inputLine);
                }
                in.close();
                System.out.println(responseBuffer.toString());
                JSONParser parser = new JSONParser();

                JSONObject json = (JSONObject)parser.parse(responseBuffer.toString());
                Response response = new Response(json);
                newMessages = response.getMessages();
            }
        } catch (Exception e) {
            System.err.println("Error pulling messages: " + e.getMessage());
        }
        return newMessages;
    }

    /**
     *
     * @param message - Message to send to the server
     * @return - true on send success, false on send failure
     */

    public boolean sendMessage(Message message) {
        //Construct a JSON object from the message
        JSONObject json = message.toJSON();
        //Connect to server to send message
        try {
            URL url = new URL(String.format("http://%s:%d/send", _serverIP, _portNumber));
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            OutputStream output = connection.getOutputStream();
            output.write(json.toString().getBytes());
            output.flush();
            output.close();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                //POST Request did not work
                return false;
            }
        } catch (MalformedURLException e) {
            //Connection failed
            return false;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }
}



