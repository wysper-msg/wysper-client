package src;// A Java program for a Client

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The Client class allows a client to connect to a server and send messages
 */
public class Client
{
    private String username;            // Unique username identifying a user
    private Communicator communicator;  // Connects to server to send and receive messages

    /**
     * Standard Client constructor
     * @param username uniquely identifies this user
     */
    public Client(String username) {
        this.username = username;
    }

    /**
     * Initializes this.communicator to communicate with the specified server
     * @param serverIPAddress - IP address of server
     * @param portNumber - Port number open on server
     */
    public void setServer(String serverIPAddress, int portNumber) {
        this.communicator = new Communicator(serverIPAddress, portNumber);
    }

    /**
     * Sends a message to the server
     * @requires server is running and communicator was created properly
     * @param messageBody - Body text message will contain
     * @return - true if message sends, false otherwise
     */

    public boolean sendMessage(String messageBody) {
        if(this.communicator == null) {
            throw new ExceptionInInitializerError("No server information set. Call Client.setServer method.");
        }
        Message message = new Message(username, messageBody);
        return communicator.sendMessage(message);
    }

    /**
     * Calls the server and returns messages that have been sent since we last called
     * Requires server is set
     * @return - List of Messages vended by server
     */
    public ArrayList<Message> getNewMessages() {
        if(this.communicator == null) {
            throw new ExceptionInInitializerError("No server information set. Call Client.setServer method.");
        }
        return communicator.getNewMessages(username);
    }

    /**
     * Called on Client initialization only, loads most recent n messages from server,
     * where n is configured in server repo
     * Requires server is set
     * @return - List of Messages vended by server
     */
    public ArrayList<Message> getMessageHistory() {
        if(this.communicator == null) {
            throw new ExceptionInInitializerError("No server information set. Call Client.setServer method.");
        }
        return communicator.loadHistory(username);
    }

    /**
     * Prompts the client for information then connects to a running server, if possible
     * Also begins GetMessages routine to consistently pull for new messages from the server
     * @param args
     */
    public static void main(String args[]) {
        // First we prompt the user for information
        System.out.print("Enter your username: ");
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();
        System.out.println("Enter your server IP address: ");
        String serverIP = scanner.nextLine();
        System.out.println("Enter your server port number: ");
        String portString = scanner.nextLine();
        int portNumber = Integer.valueOf(portString);

        // Initialize client and set server
        Client client = new Client(username);
        client.setServer(serverIP, portNumber);

        // Get message history on initial startup
        String message;
        List<Message> messageHistory = client.getMessageHistory();
        for(int i = 0; i < messageHistory.size(); i++) {
            System.out.println(messageHistory.get(i));
        }

        // run getMessages in a thread to return new messages
        GetMessages getMessages = new GetMessages(client);
        Thread t = new Thread(getMessages);
        t.start();

        // Checks for a new message entered by the user
        while (true) {
             message = scanner.nextLine();

             // exit if the user enters "Exit"
             if(message.equals("'Exit'")) {
                 break;
             } else if(message.equals("")) {
                 continue;

             // Send a message if the user has typed one
             } else {
                 boolean sentMessage = client.sendMessage(message);
                 if(!sentMessage) {
                     // If the message fails to send, try 5 times
                     for(int i = 0; i < 5; i++) {
                         System.err.println(String.format("Message didn't send. Will attempt to send %d more times", 5-i));
                         try {
                         Thread.sleep(2000);
                         } catch (InterruptedException e) {

                         }
                         sentMessage = client.sendMessage(message);
                         if(sentMessage) {
                             break;
                         }
                     }
                 }
             }
        }
    }
}

/**
 * GetMessages continuously polls the server for new messages
 */
class GetMessages implements Runnable{
    private Client client;
    public GetMessages(Client client) {
        this.client = client;
    }

    // Loops and calls getMessages consistently
    public void run() {
        while(true) {
            List<Message> newMessages;
            newMessages = client.getNewMessages();
            for(int i = 0; i < newMessages.size(); i++) {
                System.out.println(newMessages.get(i));
            }
        }
    }

}