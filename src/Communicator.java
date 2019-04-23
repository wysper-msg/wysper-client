import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/** Connects to server and has methods for getting and sending messages */
public class Communicator {

  // Identify the server we are trying to connect to
  private String _serverIP;
  private int _portNumber;

  /**
   * Constructor of Communicator
   *
   * @param serverIP - IP address of the server to communicate to
   * @param portNumber - Port number of the server to communicate to
   */
  public Communicator(String serverIP, int portNumber) {
    _serverIP = serverIP;
    _portNumber = portNumber;
  }

  /**
   * Sends a request to the server at /poll to get new messages that have not yet been read by this
   * user.
   *
   * @return List of new Messages vended by the server
   */
  public ArrayList<Message> getNewMessages(String username) {
    ArrayList<Message> newMessages = new ArrayList<>();
    try {
      // Attempt to make a connection to the server
      URL url = new URL(String.format("http://%s:%d/poll", _serverIP, _portNumber));
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setDoOutput(true);
      OutputStream output = connection.getOutputStream();
      output.write(username.getBytes());
      output.flush();
      output.close();

      // If a connection is made, we parse the response into message objects
      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer responseBuffer = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
          responseBuffer.append(inputLine);
        }
        in.close();
        JSONParser parser = new JSONParser();

        JSONObject json = (JSONObject) parser.parse(responseBuffer.toString());
        Response response = new Response(json);
        newMessages = response.getMessages();
      }
    } catch (ParseException e) {
      System.err.println("Error JSON parsing response message!");
    } catch (java.net.ConnectException e) {

    } catch (IOException e) {
      System.err.println("Error getting new Messages: " + e.getMessage());
      e.printStackTrace();
    }
    return newMessages;
  }

  /**
   * Sends a request to the server at /init to get n messages on users first startup
   *
   * @return List of new Messages vended by the server
   */
  public ArrayList<Message> getNMessages(int numMessages) {
    ArrayList<Message> newMessages = new ArrayList<>();
    try {
      URL url = new URL(String.format("http://%s:%d/getn", _serverIP, _portNumber));
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setDoOutput(true);
      OutputStream output = connection.getOutputStream();
      output.write(String.valueOf(numMessages).getBytes());
      output.flush();
      output.close();
      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer responseBuffer = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
          responseBuffer.append(inputLine);
        }
        in.close();
        JSONParser parser = new JSONParser();

        JSONObject json = (JSONObject) parser.parse(responseBuffer.toString());
        Response response = new Response(json);
        newMessages = response.getMessages();
      }
    } catch (ParseException e) {
      System.err.println("Error JSON parsing response message!");
    } catch (java.net.ConnectException e) {

    } catch (IOException e) {
      System.err.println("Error getting new Messages: " + e.getMessage());
      e.printStackTrace();
    }
    return newMessages;
  }

  /** @return new messages vended by the server */
  public ArrayList<Message> loadHistory(String username) {
    ArrayList<Message> messageHistory = new ArrayList<>();
    try {
      // Attempt to make a connection to the server
      URL url = new URL(String.format("http://%s:%d/init", _serverIP, _portNumber));
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setDoOutput(true);
      OutputStream output = connection.getOutputStream();
      output.write(username.getBytes());
      output.flush();
      output.close();

      // If a connection is made, parse Message objects from the response
      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer responseBuffer = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
          responseBuffer.append(inputLine);
        }
        in.close();
        JSONParser parser = new JSONParser();

        JSONObject json = (JSONObject) parser.parse(responseBuffer.toString());
        Response response = new Response(json);
        messageHistory = response.getMessages();
      }
    } catch (ParseException e) {
      System.err.println("Error JSON parsing response message!");
    } catch (java.net.ConnectException e) {

    } catch (IOException e) {
      System.err.println("Error getting new Messages: " + e.getMessage());
    }
    if (messageHistory == null) {
      return new ArrayList<Message>();
    }
    return messageHistory;
  }

  /**
   * Sends a message object to the server
   *
   * @param message - Message to send to the server
   * @return - true on send success, false on send failure
   */
  public boolean sendMessage(Message message) {
    // Construct a JSON object from the message
    JSONObject json = message.toJSON();

    try {
      // Attempt to connect to server to send message
      URL url = new URL(String.format("http://%s:%d/send", _serverIP, _portNumber));
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setDoOutput(true);
      OutputStream output = connection.getOutputStream();

      // Write the message as JSON to the server
      output.write(json.toJSONString().getBytes());
      output.flush();
      output.close();

      int responseCode = connection.getResponseCode();

      // If we did not send the message properly
      if (responseCode != HttpURLConnection.HTTP_OK) {
        // POST Request did not work
        System.out.println("Response Code: " + responseCode);
        System.out.println("Error sending message.");
        return false;
      }
    } catch (MalformedURLException e) {
      // Connection failed
      return false;
    } catch (IOException e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
      return false;
    }
    return true;
  }
}
