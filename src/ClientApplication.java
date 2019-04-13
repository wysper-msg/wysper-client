package src;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
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
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.Semaphore;


public class ClientApplication extends Application {
    public static Semaphore semaphore = new Semaphore(1);

    int MESSAGE_LOADING_STEP = 20;
    double TOP_BUTTON_WIDTH = 400;
    double TOP_BUTTON_HEIGHT = 40;
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
    private Button logoutButton;
    private Button moreMessagesButton;
    private TextArea newMessage;
    private Button sendButton;
    private TextArea messageBox;
    private Scene chatRoomScene;
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
    private Integer numberOfMessagesLoaded = 0;
    private Stage chatRoomStage;
    private Stage loginStage;
    private Thread newMessagesThread;
    @Override
    public void start(Stage primaryStage) throws Exception {

        //Popup to gather the client and server information
        loginStage = new Stage();
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.initOwner(primaryStage);
        loginStage.setTitle("Wysper Login");

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
                serverIPAddress = ipText.getText();
                serverPortNumber = Integer.valueOf(portText.getText());
                username = usernameText.getText();
                ipText.setText("");
                portText.setText("");
                usernameText.setText("");
                loginStage.hide();
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

        loginStage.setScene(popupScene);
        loginStage.setMinHeight(POPUP_INPUT_HEIGHT*3+LOGIN_BUTTON_HEIGHT+28);
        loginStage.setMaxHeight(POPUP_INPUT_HEIGHT*3+LOGIN_BUTTON_HEIGHT+28);
        loginStage.setMinWidth(POPUP_LABEL_WIDTH*6.4 + POPUP_INPUT_WIDTH);
        loginStage.setMaxWidth(POPUP_LABEL_WIDTH*6.4 + POPUP_INPUT_WIDTH);

        loginStage.show();
    }

    public void launchChatroom() throws InterruptedException {
        chatRoomStage = new Stage();
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
        messageBox.autosize();
        messageBox.setMinWidth(MESSAGE_PANE_WIDTH);
        messageBox.setMinHeight(MESSAGE_PANE_HEIGHT);
        messageBox.setWrapText(true);
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
            builder.append(messageFormatter(messageHistory.get(i)) + "\n");
            numberOfMessagesLoaded ++;
        }
        messageBox.appendText(builder.toString());

        newMessage = new TextArea();
        newMessage.setMinWidth(NEW_MESSAGE_BOX_WIDTH);
        newMessage.setMinHeight(NEW_MESSAGE_BOX_HEIGHT);
        HBox hbox = new HBox(newMessage, sendButton);
        hbox.setMaxHeight(NEW_MESSAGE_BOX_HEIGHT);
        hbox.setMinHeight(NEW_MESSAGE_BOX_HEIGHT);

        logoutButton = new Button();
        logoutButton.setText("Logout");
        logoutButton.setMinWidth(TOP_BUTTON_WIDTH);
        logoutButton.setMinHeight(TOP_BUTTON_HEIGHT);
        logoutButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //Close current window
                chatRoomStage.hide();
                newMessagesThread.stop();
                //Open Login window
                loginStage.show();
            }
        });
        moreMessagesButton = new Button();
        moreMessagesButton.setText("Get more messages");
        moreMessagesButton.setMinWidth(TOP_BUTTON_WIDTH);
        moreMessagesButton.setMinHeight(TOP_BUTTON_HEIGHT);
        moreMessagesButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    numberOfMessagesLoaded += MESSAGE_LOADING_STEP;
                    semaphore.acquire();
                    List<Message> nMessages = client.getNMessages(numberOfMessagesLoaded);
                    StringBuilder builder = new StringBuilder();
                    for(int i = 0; i < nMessages.size(); i++){
                        builder.append(messageFormatter(nMessages.get(i)) + "\n");
                    }
                    messageBox.clear();
                    messageBox.appendText(builder.toString());
                    numberOfMessagesLoaded = nMessages.size();
                    semaphore.release();
                } catch (InterruptedException e) {
                    System.out.println("GetMoreMessageButton: Error with semaphore");
                }
            }
        });

        HBox topButtons = new HBox(logoutButton, moreMessagesButton);
        topButtons.setMaxHeight(TOP_BUTTON_HEIGHT);
        topButtons.setMinHeight(TOP_BUTTON_HEIGHT);


        VBox vbox = new VBox(topButtons, messageBox, hbox);
        vbox.setAlignment(Pos.CENTER);

        vbox.autosize();
        chatRoomScene  = new Scene(vbox, MESSAGE_PANE_WIDTH, TOP_BUTTON_HEIGHT+MESSAGE_PANE_HEIGHT+NEW_MESSAGE_BOX_HEIGHT);

        chatRoomScene.getStylesheets().add("src/darktheme.css");
        chatRoomStage.setScene(chatRoomScene);
        chatRoomStage.setMinWidth(MESSAGE_PANE_WIDTH);
        chatRoomStage.setMinHeight(TOP_BUTTON_HEIGHT + SEND_BUTTON_HEIGHT + 25);
        chatRoomStage.setHeight(TOP_BUTTON_HEIGHT + SEND_BUTTON_HEIGHT + MESSAGE_PANE_HEIGHT);
        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> System.out.println();
        chatRoomStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            double newChatRoomHeight = chatRoomScene.getHeight() - TOP_BUTTON_HEIGHT - SEND_BUTTON_HEIGHT;
            messageBox.setMinHeight(newChatRoomHeight);
        });
        chatRoomStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            double newTopButtonWidths = chatRoomScene.getWidth()/2;
            logoutButton.setMinWidth(newTopButtonWidths);
            moreMessagesButton.setMinWidth(newTopButtonWidths);
            newMessage.setMinWidth(chatRoomScene.getWidth()-SEND_BUTTON_WIDTH);
        });
        chatRoomStage.setFullScreen(true);
        chatRoomStage.show();
        ApplicationGetMessages getMessages = new ApplicationGetMessages(client, messageBox, numberOfMessagesLoaded);
        newMessagesThread = new Thread(getMessages);
        newMessagesThread.start();
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

    public static String messageFormatter(Message message) {
        String header = message.username.toString() + " " + new SimpleDateFormat("hh:mm:ss").format(message.timestamp);

        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < header.length(); i++) {
            builder.append("*");
        }
        builder.append("\n"+header+"|"+"\n");
        for(int i = 0; i < header.length(); i++){
            builder.append("*");
        }
        builder.append("\n"+message.body+"\n");
        return builder.toString();
    }
}

class ApplicationGetMessages implements Runnable{
    private Client client;
    private TextArea messageBox;
    private Integer numberOfMessagesInBox;
    public ApplicationGetMessages(Client client, TextArea messageBox, Integer currentNumMessages) {
        this.client = client;
        this.messageBox = messageBox;
        this.numberOfMessagesInBox = currentNumMessages;
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
            try {
                ClientApplication.semaphore.acquire();
                newMessages = client.getNewMessages();
                ClientApplication.semaphore.release();
                if(newMessages != null){
                    StringBuilder builder = new StringBuilder();

                    for(int i = 0; i < newMessages.size(); i++){
                        builder.append(ClientApplication.messageFormatter(newMessages.get(i)) + "\n");
                        numberOfMessagesInBox ++;
                    }
                    messageBox.appendText(builder.toString());
                }
            } catch (InterruptedException e) {
                System.out.println("ClientApplication: GetMessages interrupted");
            }
        }
    }
}


