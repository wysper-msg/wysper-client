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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * The purpose of this class is to run the GUI for the client application. It uses the JavaFX
 * framework.
 */
public class ClientApplication extends Application {
  // semaphore needed for multithreading
  public static Semaphore semaphore = new Semaphore(1);

  // Starting dimension variables
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

  // UI elements
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

  // Thread to get new messages at regular intervals
  private Thread newMessagesThread;

  /**
   * This method is a callback from the JavaFX framework. It is the first method that will by called
   * when launching the application. It make a window to gather login information and call proper
   * methods when the user is logging in.
   *
   * @param primaryStage
   * @throws Exception
   */
  @Override
  public void start(Stage primaryStage) throws Exception {

    // Popup to gather the client and server information
    loginStage = new Stage();
    loginStage.initModality(Modality.APPLICATION_MODAL);
    loginStage.initOwner(primaryStage);
    loginStage.setTitle("Wysper Login");

    // Create the elements to gather IP address from user
    ipLabelBox = new Text(labelMaker(ipLabelText));
    ipText = new TextArea();
    ipText.setMinWidth(POPUP_INPUT_WIDTH);
    ipText.setMaxWidth(POPUP_INPUT_WIDTH);
    ipText.setMinHeight(POPUP_INPUT_HEIGHT);
    ipText.setMaxHeight(POPUP_INPUT_HEIGHT);
    HBox ipBox = new HBox(ipLabelBox, ipText);
    ipBox.setAlignment(Pos.CENTER_RIGHT);

    // Create the elements to gather port information from user
    portLabelBox = new Text(labelMaker(portLableText));
    portText = new TextArea();
    portText.setMinWidth(POPUP_INPUT_WIDTH);
    portText.setMaxWidth(POPUP_INPUT_WIDTH);
    portText.setMinHeight(POPUP_INPUT_HEIGHT);
    portText.setMaxHeight(POPUP_INPUT_HEIGHT);
    HBox portBox = new HBox(portLabelBox, portText);
    portBox.setAlignment(Pos.CENTER_RIGHT);

    // Create the elements to gather username information from user
    usernameLabelBox = new Text(labelMaker(usernameLabelText));
    usernameText = new TextArea();
    usernameText.setMinWidth(POPUP_INPUT_WIDTH);
    usernameText.setMaxWidth(POPUP_INPUT_WIDTH);
    usernameText.setMinHeight(POPUP_INPUT_HEIGHT);
    usernameText.setMaxHeight(POPUP_INPUT_HEIGHT);
    HBox usernameBox = new HBox(usernameLabelBox, usernameText);
    usernameBox.setAlignment(Pos.CENTER_RIGHT);

    // Create the Login button to log the user into chat room
    Button loginButton = new Button();
    loginButton.setText("Login");
    loginButton.setMinHeight(LOGIN_BUTTON_HEIGHT);
    loginButton.setMaxHeight(LOGIN_BUTTON_HEIGHT);
    loginButton.setMinWidth(LOGIN_BUTTON_WIDTH);
    loginButton.setMaxWidth(LOGIN_BUTTON_WIDTH);
    loginButton.setOnMouseClicked(
        new EventHandler<MouseEvent>() {
          @Override
          public void handle(MouseEvent mouseEvent) {
            // Gather the data from the UI fields
            serverIPAddress = ipText.getText();
            try {
              serverPortNumber = Integer.valueOf(portText.getText());
            } catch (NumberFormatException e) {
              System.out.println("Enter a valid integer to the port number box.");
              ipText.setText("");
              portText.setText("");
              usernameText.setText("");
              return;
            }
            username = usernameText.getText();
            ipText.setText("");
            portText.setText("");
            usernameText.setText("");
            if (serverIPAddress.length() == 0) {
              System.out.println("You must enter an IP address.");
              return;
            }
            if (username.length() > 0 && username.length() < 31) {
              // hide the login window
              loginStage.hide();

              // Create a new client
              client = new Client(username);
              client.setServer(serverIPAddress, serverPortNumber);

              // Launch a new chatroom
              try {
                launchChatroom();
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            } else {
              System.out.println("Username must be between 1 and 30 characters.");
              return;
            }
          }
        });
    HBox loginBox = new HBox(loginButton);
    loginBox.setAlignment(Pos.CENTER);

    // Add all components into the scene
    VBox popupBox = new VBox(ipBox, portBox, usernameBox, loginBox);
    Scene popupScene = new Scene(popupBox);

    // Set the theme
    popupScene.getStylesheets().add("darktheme.css");

    // Set the window dimensions and show it
    loginStage.setScene(popupScene);
    loginStage.setMinHeight(POPUP_INPUT_HEIGHT * 3 + LOGIN_BUTTON_HEIGHT + 28);
    loginStage.setMaxHeight(POPUP_INPUT_HEIGHT * 3 + LOGIN_BUTTON_HEIGHT + 28);
    loginStage.setMinWidth(POPUP_LABEL_WIDTH * 6.4 + POPUP_INPUT_WIDTH);
    loginStage.setMaxWidth(POPUP_LABEL_WIDTH * 6.4 + POPUP_INPUT_WIDTH);
    loginStage.show();
  }

  /**
   * This method is called when the user clicks on the login button.
   *
   * @throws InterruptedException
   */
  private void launchChatroom() throws InterruptedException {
    // Create the window for the chatroom
    chatRoomStage = new Stage();
    chatRoomStage.setTitle("Wysper");

    // Create the send button
    sendButton = new Button();
    sendButton.setText("Send Message");
    sendButton.setMinWidth(SEND_BUTTON_WIDTH);
    sendButton.setMinHeight(SEND_BUTTON_HEIGHT);

    // send the message on click of the send button
    sendButton.setOnAction(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            if (client != null) {
              client.sendMessage(newMessage.getText());
            } else {
              System.out.println("Client is null!");
            }
            newMessage.setText("");
          }
        });

    // Create the message box
    messageBox = new TextArea();
    messageBox.autosize();
    messageBox.setMinWidth(MESSAGE_PANE_WIDTH);
    messageBox.setMinHeight(MESSAGE_PANE_HEIGHT);
    messageBox.setWrapText(true);
    messageBox.setEditable(false);
    messageBox
        .textProperty()
        .addListener(
            new ChangeListener<String>() {
              @Override
              public void changed(
                  ObservableValue<? extends String> observableValue, String s, String t1) {
                messageBox.setScrollTop(Double.MAX_VALUE);
              }
            });

    // Populate existing chatroom messages
    List<Message> messageHistory = client.getMessageHistory();
    StringBuilder builder = new StringBuilder();

    for (int i = 0; i < messageHistory.size(); i++) {
      builder.append(messageFormatter(messageHistory.get(i)) + "\n");
      numberOfMessagesLoaded++;
    }
    messageBox.appendText(builder.toString());

    // Create the new message box
    newMessage = new TextArea();
    newMessage.setMinWidth(NEW_MESSAGE_BOX_WIDTH);
    newMessage.setMinHeight(NEW_MESSAGE_BOX_HEIGHT);
    HBox hbox = new HBox(newMessage, sendButton);
    hbox.setMaxHeight(NEW_MESSAGE_BOX_HEIGHT);
    hbox.setMinHeight(NEW_MESSAGE_BOX_HEIGHT);

    // Create the logout button
    logoutButton = new Button();
    logoutButton.setText("Logout");
    logoutButton.setMinWidth(TOP_BUTTON_WIDTH);
    logoutButton.setMinHeight(TOP_BUTTON_HEIGHT);
    // set on click behavior for logout button
    logoutButton.setOnAction(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            // Close current window
            chatRoomStage.hide();
            newMessagesThread.stop();
            // Open Login window
            loginStage.show();
          }
        });

    // Create the more messages button
    moreMessagesButton = new Button();
    moreMessagesButton.setText("Get more messages");
    moreMessagesButton.setMinWidth(TOP_BUTTON_WIDTH);
    moreMessagesButton.setMinHeight(TOP_BUTTON_HEIGHT);
    moreMessagesButton.setOnAction(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent actionEvent) {
            try {
              numberOfMessagesLoaded += MESSAGE_LOADING_STEP;
              semaphore.acquire();
              List<Message> nMessages = client.getNMessages(numberOfMessagesLoaded);
              StringBuilder builder = new StringBuilder();
              for (int i = 0; i < nMessages.size(); i++) {
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

    // Add all elements into layouts
    HBox topButtons = new HBox(logoutButton, moreMessagesButton);
    topButtons.setMaxHeight(TOP_BUTTON_HEIGHT);
    topButtons.setMinHeight(TOP_BUTTON_HEIGHT);

    VBox vbox = new VBox(topButtons, messageBox, hbox);
    vbox.setAlignment(Pos.CENTER);

    vbox.autosize();
    chatRoomScene =
        new Scene(
            vbox,
            MESSAGE_PANE_WIDTH,
            TOP_BUTTON_HEIGHT + MESSAGE_PANE_HEIGHT + NEW_MESSAGE_BOX_HEIGHT);

    // Set the styling, size, and resize behavior of the chatroom window
    chatRoomScene.getStylesheets().add("darktheme.css");
    chatRoomStage.setScene(chatRoomScene);
    chatRoomStage.setMinWidth(MESSAGE_PANE_WIDTH);
    chatRoomStage.setMinHeight(TOP_BUTTON_HEIGHT + SEND_BUTTON_HEIGHT + 25);
    chatRoomStage.setHeight(TOP_BUTTON_HEIGHT + SEND_BUTTON_HEIGHT + MESSAGE_PANE_HEIGHT);
    ChangeListener<Number> stageSizeListener =
        (observable, oldValue, newValue) -> System.out.println();
    chatRoomStage
        .heightProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              double newChatRoomHeight =
                  chatRoomScene.getHeight() - TOP_BUTTON_HEIGHT - SEND_BUTTON_HEIGHT;
              messageBox.setMinHeight(newChatRoomHeight);
            });
    chatRoomStage
        .widthProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              double newTopButtonWidths = chatRoomScene.getWidth() / 2;
              logoutButton.setMinWidth(newTopButtonWidths);
              moreMessagesButton.setMinWidth(newTopButtonWidths);
              newMessage.setMinWidth(chatRoomScene.getWidth() - SEND_BUTTON_WIDTH);
            });
    chatRoomStage.setFullScreen(true);
    // Show the chatroom window
    chatRoomStage.show();

    // Start the background polling of new messages
    ApplicationGetMessages getMessages =
        new ApplicationGetMessages(client, messageBox, numberOfMessagesLoaded);
    newMessagesThread = new Thread(getMessages);
    newMessagesThread.start();
  }

  /**
   * The method is used to create labels with the same length for the login screen
   *
   * @param input
   * @return
   */
  private String labelMaker(String input) {
    POPUP_LABEL_WIDTH =
        Integer.max(
            ipLabelText.length(), Integer.max(portLableText.length(), usernameLabelText.length()));
    StringBuilder builder = new StringBuilder();
    builder.append(input);
    int padding = (int) POPUP_LABEL_WIDTH - input.length();
    for (int i = 0; i < padding; i++) {
      builder.append(" ");
    }
    return builder.toString();
  }

  /**
   * String formatter of messages to go into the message box.
   *
   * @param message - Message to be formatted into String
   * @return
   */
  public static String messageFormatter(Message message) {
    String header =
        message.username.toString()
            + " "
            + new SimpleDateFormat("hh:mm:ss").format(message.timestamp);

    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < header.length(); i++) {
      builder.append("*");
    }
    builder.append("\n" + header + "|" + "\n");
    for (int i = 0; i < header.length(); i++) {
      builder.append("*");
    }
    builder.append("\n" + message.body + "\n");
    return builder.toString();
  }

  public static void main(String[] args) {
    launch(args);
  }
}

/**
 * The purpose of this class is to be a secondary thread running in the background to pull more
 * messages from the server.
 */
class ApplicationGetMessages implements Runnable {
  private Client client;
  private TextArea messageBox;
  private Integer numberOfMessagesInBox;

  public ApplicationGetMessages(Client client, TextArea messageBox, Integer currentNumMessages) {
    this.client = client;
    this.messageBox = messageBox;
    this.numberOfMessagesInBox = currentNumMessages;
  }

  public void run() {
    while (true) {
      // Wait half a second between checking for new messages
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      if (messageBox == null) {
        System.out.println("MessageBox is NULL");
        continue;
      }

      // Make the client check for any new messages
      List<Message> newMessages;
      try {
        ClientApplication.semaphore.acquire();
        newMessages = client.getNewMessages();
        ClientApplication.semaphore.release();

        // Output the new messages the client has received from the server
        if (newMessages != null) {
          StringBuilder builder = new StringBuilder();

          for (int i = 0; i < newMessages.size(); i++) {
            builder.append(ClientApplication.messageFormatter(newMessages.get(i)) + "\n");
            numberOfMessagesInBox++;
          }
          messageBox.appendText(builder.toString());
        }
      } catch (InterruptedException e) {
        System.out.println("ClientApplication: GetMessages interrupted");
      }
    }
  }
}
