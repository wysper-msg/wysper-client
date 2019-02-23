package src;

import java.net.Socket;
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

    }

    public boolean sendMessage(String message) {
        //Construct a Message object from the client input message string

        //Connect to server to
    }
}
