package src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.Vector;

public class Communicator {
    private String _serverIP;
    private int _portNumber;
    private Socket socke = null;
    public Communicator(String serverIP, int portNumber) {
        _serverIP = serverIP;
        _portNumber = portNumber;
    }

    public void getNewMessages() {
        try {
            URL url = new URL(String.format("http://%s:%d/poll", _serverIP, _portNumber));
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            //connection.setRequestProperty
            int responseCode = connection.getResponseCode();
            System.out.println("Response code: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                System.out.println(response.toString());
            } else {
                System.out.println("Shit fam, didn't work");
            }
        } catch (Exception e) {

        }
    }

    public boolean sendMessage(String message) {
        //Construct a Message object from the client input message string

        //Connect to server to send message
        try {
            URL url = new URL(String.format("http://%s:%d/send", _serverIP, _portNumber));
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            OutputStream output = connection.getOutputStream();
            output.write(message.getBytes());
            output.flush();
            output.close();
            int responseCode = connection.getResponseCode();
            System.out.println("Response code: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                System.out.println(response.toString());
            } else {
                System.out.println("POST request did not work");
                return false;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws Exception{
        Communicator comms = new Communicator("192.168.1.14", 8000);
        comms.sendMessage("Hi, Trevor");
    }
}



