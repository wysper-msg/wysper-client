package src;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sun.applet.Main;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;


public class ClientApplication extends Application {
    double NEW_MESSAGE_BOX_WIDTH = 600;
    double NEW_MESSAGE_BOX_HEIGHT = 40;
    double SEND_BUTTON_WIDTH = 200;
    double SEND_BUTTON_HEIGHT = 40;
    double MESSAGE_PANE_WIDTH = 800;
    double MESSAGE_PANE_HEIGHT = 400;
    double POPUP_LABEL_WIDTH = 200;
    double POPUP_LABEL_HEIGHT = 40;
    double POPUP_INPUT_WIDTH = 200;
    double POPUP_INPUT_HEIGHT = 40;
    double LOGIN_BUTTON_WIDTH = 200;
    double LOGIN_BUTTON_HEIGHT = 40;
    private TextArea newMessage;
    private Button sendButton;
    private TextArea messageBox;
    private Text ipLabelBox;
    private String ipLabelText = "IP address of server:";
    private TextArea ipText;
    private Text portLabelBox;
    private String portLableText = "Port number:";
    private TextArea portText;
    private Text usernameLabelBox;
    private String usernameLabelText = "Username:";
    private TextArea usernameText;
    private String serverIPAddress;
    private int serverPortNumber;
    private String username;
    private Client client;

    @Override
    public void start(Stage primaryStage) throws Exception {

        //Popup to gather the client and server information
        final Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initOwner(primaryStage);

        ipLabelBox = new Text(labelMaker(ipLabelText));
        ipText = new TextArea();
        ipText.setMinWidth(POPUP_INPUT_WIDTH);
        ipText.setMaxWidth(POPUP_INPUT_WIDTH);
        ipText.setMinHeight(POPUP_INPUT_HEIGHT);
        ipText.setMaxHeight(POPUP_INPUT_HEIGHT);
        HBox ipBox = new HBox(ipLabelBox, ipText);
        ipBox.setAlignment(Pos.CENTER_RIGHT);



        portLabelBox = new Text(labelMaker(portLableText));
        portText = new TextArea();
        portText.setMinWidth(POPUP_INPUT_WIDTH);
        portText.setMaxWidth(POPUP_INPUT_WIDTH);
        portText.setMinHeight(POPUP_INPUT_HEIGHT);
        portText.setMaxHeight(POPUP_INPUT_HEIGHT);
        HBox portBox = new HBox(portLabelBox, portText);
        portBox.setAlignment(Pos.CENTER_RIGHT);

        usernameLabelBox = new Text(labelMaker(usernameLabelText));
        usernameText = new TextArea();
        usernameText.setMinWidth(POPUP_INPUT_WIDTH);
        usernameText.setMaxWidth(POPUP_INPUT_WIDTH);
        usernameText.setMinHeight(POPUP_INPUT_HEIGHT);
        usernameText.setMaxHeight(POPUP_INPUT_HEIGHT);
        HBox usernameBox = new HBox(usernameLabelBox, usernameText);
        usernameBox.setAlignment(Pos.CENTER_RIGHT);


        Button loginButton = new Button();
        loginButton.setText("Login");
        loginButton.setMinHeight(LOGIN_BUTTON_HEIGHT);
        loginButton.setMaxHeight(LOGIN_BUTTON_HEIGHT);
        loginButton.setMinWidth(LOGIN_BUTTON_WIDTH);
        loginButton.setMaxWidth(LOGIN_BUTTON_WIDTH);
        loginButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                soundClip();
                serverIPAddress = ipText.getText();
                serverPortNumber = Integer.valueOf(portText.getText());
                username = usernameText.getText();
                ipText.setText("");
                portText.setText("");
                usernameText.setText("");
                popup.hide();
                client = new Client(username);
                client.setServer(serverIPAddress, serverPortNumber);
                try {
                    launchChatroom();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        HBox loginBox = new HBox(loginButton);
        loginBox.setAlignment(Pos.CENTER);

        VBox popupBox = new VBox(ipBox, portBox, usernameBox, loginBox);
        Scene popupScene = new Scene(popupBox);

        popupScene.getStylesheets().add("src/darktheme.css");

        popup.setScene(popupScene);
        popup.setMinHeight(POPUP_INPUT_HEIGHT*3+LOGIN_BUTTON_HEIGHT+28);
        popup.setMaxHeight(POPUP_INPUT_HEIGHT*3+LOGIN_BUTTON_HEIGHT+28);
        popup.setMinWidth(POPUP_LABEL_WIDTH*6.4 + POPUP_INPUT_WIDTH);
        popup.setMaxWidth(POPUP_LABEL_WIDTH*6.4 + POPUP_INPUT_WIDTH);

        popup.show();
    }

    public void launchChatroom() throws InterruptedException {
        Stage chatRoomStage = new Stage();
        chatRoomStage.setTitle("Wysper");
        sendButton = new Button();
        sendButton.setText("Send Message");
        sendButton.setMinWidth(SEND_BUTTON_WIDTH);
        sendButton.setMinHeight(SEND_BUTTON_HEIGHT);
        sendButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(client!=null){
                    client.sendMessage(newMessage.getText());
                } else {
                    System.out.println("Client is null!");
                }
                newMessage.setText("");
            }
        });

        messageBox = new TextArea();
        messageBox.setMinWidth(MESSAGE_PANE_WIDTH);
        messageBox.setMinHeight(MESSAGE_PANE_HEIGHT);
        messageBox.setEditable(false);
        messageBox.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                messageBox.setScrollTop(Double.MAX_VALUE);
            }
        });
        List<Message> messageHistory = client.getMessageHistory();
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < messageHistory.size(); i++){
            builder.append("\n"+messageHistory.get(i));
        }
        messageBox.appendText(builder.toString());

        newMessage = new TextArea();
        newMessage.setMinWidth(NEW_MESSAGE_BOX_WIDTH);
        newMessage.setMinHeight(NEW_MESSAGE_BOX_HEIGHT);
        HBox hbox = new HBox(newMessage, sendButton);
        hbox.setMaxHeight(NEW_MESSAGE_BOX_HEIGHT);


        VBox vbox = new VBox(messageBox, hbox);
        Scene scene = new Scene(vbox, MESSAGE_PANE_WIDTH, MESSAGE_PANE_HEIGHT+NEW_MESSAGE_BOX_HEIGHT);



        scene.getStylesheets().add("src/darktheme.css");
        chatRoomStage.setScene(scene);
        chatRoomStage.show();
        ApplicationGetMessages getMessages = new ApplicationGetMessages(client, messageBox);
        Thread t = new Thread(getMessages);
        t.start();
    }

    private String labelMaker(String input) {
        POPUP_LABEL_WIDTH = Integer.max(ipLabelText.length(), Integer.max(portLableText.length(), usernameLabelText.length()));
        StringBuilder builder = new StringBuilder();
        builder.append(input);
        int padding = (int)POPUP_LABEL_WIDTH - input.length();
        for(int i = 0; i < padding; i++){
            builder.append(" ");
        }
        return builder.toString();
    }

    private void soundClip() {
        try {
            File soundFile = new File("/home/alexander/Downloads/r2d2_scream_converted.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            // Get a sound clip resource.
            Clip clip = AudioSystem.getClip();
            // Open audio clip and load samples from the audio input stream.
            clip.open(audioIn);
            clip.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

    private static synchronized void playSound() {
        new Thread(new Runnable() {
            // The wrapper thread is unnecessary, unless it blocks on the
            // Clip finishing; see comments.
            public void run() {
                try {
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                            Main.class.getResourceAsStream("./src/r2d2_scream_converted.wav"));
                    clip.open(inputStream);
                    clip.start();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }
}

class ApplicationGetMessages implements Runnable{
    private Client client;
    private TextArea messageBox;
    public ApplicationGetMessages(Client client, TextArea messageBox) {
        this.client = client;
        this.messageBox = messageBox;
    }

    public void run() {
        while(true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            if(messageBox == null){
                System.out.println("MessageBox is NULL");
                continue;
            }
            List<Message> newMessages;
            newMessages = client.getNewMessages();
            if(newMessages != null){
                StringBuilder builder = new StringBuilder();

                for(int i = 0; i < newMessages.size(); i++){
                    builder.append("\n"+newMessages.get(i));
                }
                messageBox.appendText(builder.toString());
            }
        }
    }
}


