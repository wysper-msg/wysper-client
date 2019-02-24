package src;// A Java program for a Client

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client
{
    private String username;
    private Communicator communicator;

    public Client(String username) {
        this.username = username;
    }

    /**
     *
     * @param serverIPAddress - IP address of server
     * @param portNumber - Port number open on server
     */
    public void setServer(String serverIPAddress, int portNumber) {
        this.communicator = new Communicator(serverIPAddress, portNumber);
    }

    /**
     * Requires that server is set
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
     * Requires that server is set
     * @return - Unread messages vended by server
     */

    public ArrayList<Message> getNewMessages() {
        if(this.communicator == null) {
            throw new ExceptionInInitializerError("No server information set. Call Client.setServer method.");
        }
        return communicator.getNewMessages();
    }






    public static void main(String args[]) {
        System.out.print("Enter your username: ");
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();
        System.out.println("Enter your server IP address: ");
        String serverIP = scanner.nextLine();
        System.out.println("Enter your server port number: ");
        String portString = scanner.nextLine();
        int portNumber = Integer.valueOf(portString);
        Client client = new Client(username);
        client.setServer(serverIP, portNumber);
        String message;
        List<Message> newMessages;
        while (true) {
             message = scanner.nextLine();
             if(message.equals("'Exit'")) {
                 break;
             } else if(message.equals("'NewMessages'")) {
                 newMessages = client.getNewMessages();
                 for(int i = 0; i < newMessages.size(); i++) {
                     System.out.println(newMessages.get(i));
                 }
             } else {
                 if(!client.sendMessage(message)) {
                     System.err.println("Message didn't send");
                 }
             }
        }
    }
} 