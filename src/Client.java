package src;// A Java program for a Client
import org.json.simple.JSONObject;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client
{
    // initialize socket and input output streams 
    private Socket socket            = null;
    private DataInputStream  input   = null;
    private DataOutputStream out     = null;

    // constructor to put ip address and port 
    public Client(String address, int port, String username) {
        // establish a connection 
        try {
            socket = new Socket(address, port);
            System.out.println("Connected");

            // takes input from terminal 
            input = new DataInputStream(System.in);

            // sends output to the socket 
            out = new DataOutputStream(socket.getOutputStream());
        }
        catch(UnknownHostException u) {
            System.out.println(u);
        }
        catch(IOException i) {
            System.out.println(i);
        }

        // string to read message from input 
        String line = "";

        // keep reading until "Shh" is input
        while (!line.equals("Shh")) {
            try {
                line = input.readLine();
                out.writeUTF(line);
            }
            catch(IOException i) {
                System.out.println(i);
            }
        }

        // close the connection 
        try {
            input.close();
            out.close();
            socket.close();
        }
        catch(IOException i) {
            System.out.println(i);
        }
    }

    /**
     *
     * @return a JSONObject containing the relevant user information
     * username, etc.
     */
    private void makeJson() {

    }


    public static void main(String args[]) {
        System.out.print("Enter your username: ");
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();

        Client client = new Client("129.161.136.236", 5000, username);
        /*
        try {
            System.out.println("My ip is: " + InetAddress.getLocalHost().getHostAddress());
        }
        catch (Exception e) {
            System.out.println(e);
        }
        */
    }
} 