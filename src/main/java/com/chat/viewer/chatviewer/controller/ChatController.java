package com.chat.viewer.chatviewer.controller;

import com.chat.viewer.chatviewer.model.Message;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.*;
import java.util.*;

/**
 * The {@code ChatController} class is responsible for managing the user interface and
 * handling file operations in the chat viewer application. It provides functionality
 * to open and display message files, as well as parse message content.
 */
public class ChatController {

    @FXML
    private Label filePathLbl;

    @FXML
    private Label noFileOpenedtxt;

    @FXML
    private Button openFileBtn;

    @FXML
    private Button openFileBtn1;

    @FXML
    private VBox openFileWindow;

    private Alert alert;

    private File lastDirectory;

    private String previousNickname = null;

    /**
     * Opens a file dialog to select a conversation file and displays the messages
     * from the selected file. Displays an alert if the file is invalid or cannot be read.
     * Also stores the last visited directory in a variable for easy access.
     */
    public void openFile() {
        FileChooser fileChooser = new FileChooser();
        if (lastDirectory != null) {
            fileChooser.setInitialDirectory(lastDirectory);
        }
        fileChooser.setTitle("Open Conversation File");
        Window window = openFileWindow.getScene().getWindow();
        File file = fileChooser.showOpenDialog(window);

        if (file == null) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);;
            alert.setContentText("No file was selected");
            alert.showAndWait();
        } else if (!file.getPath().endsWith("msg")){
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);;
                alert.setContentText("The file has an incorrect format");
                alert.showAndWait();
        } else{
            lastDirectory = file.getParentFile();
            displayMessages(file.getPath());
        }
    }

    /**
     * Creates a {@code VBox} containing the visual representation of a message.
     *
     * @param message a map containing the message details: timestamp, nickname, and content
     * @return a {@code VBox} representing the message
     */
    public VBox createMessageBox(Map<String, String> message) {
        VBox vbox = new VBox(5);
        vbox.setStyle("-fx-padding: 10; -fx-background-color: #ECECEC; -fx-background-radius: 10;");

        // Label nicknameLabel = new Label(message.get("nickname") + ":");
        String currentNickname = message.get("nickname");
        Label nicknameLabel;

        // Check if the current nickname is the same as the previous one
        if (previousNickname != null && currentNickname.equals(previousNickname)) {
            nicknameLabel = new Label("...");
        } else {
            nicknameLabel = new Label(currentNickname + ":");
            previousNickname = currentNickname; // Update the previous nickname
        }
        nicknameLabel.setStyle("-fx-text-fill: blue;");

        TextFlow contentFlow = parseContentWithEmoticons(message.get("content"));

        Label timestampLabel = new Label("[" + message.get("timestamp") + "] ");
        timestampLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #888888;");

        vbox.getChildren().addAll(nicknameLabel, contentFlow, timestampLabel);

        if (message.get("nickname").equals("Me")) {
            vbox.setStyle("-fx-background-color: #E6EEFF; -fx-padding: 10; -fx-background-radius: 10;");
        } else {
            vbox.setStyle("-fx-background-color: #F0F0F0; -fx-padding: 10; -fx-background-radius: 10;");
        }

        return vbox;
    }

    /**
     * Displays messages from the specified file path in the user interface.
     * Shows a loading label while messages are being loaded and transitions to the
     * message display once loading is complete.
     *
     * @param filePath the path to the file containing the messages
     */
    public void displayMessages(String filePath) {
        noFileOpenedtxt.setVisible(false);
        openFileBtn.setVisible(false);

        // Create a loading label and add it to the openFileWindow
        Label loadingLabel = new Label("Loading messages...");
        openFileWindow.getChildren().add(loadingLabel);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPannable(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        Task<VBox> loadMessagesTask = new Task<>() {
            @Override
            protected VBox call() throws Exception {
                VBox messageContainer = new VBox(10);
                messageContainer.setStyle("-fx-padding: 10; -fx-background-color: #FFFFFF;");

                List<Map<String, String>> messages = msgFileReader(filePath);
                for (Map<String, String> message : messages) {
                    VBox messageBox = createMessageBox(message);
                    messageContainer.getChildren().add(messageBox);
                }

                return messageContainer;
            }
        };

        loadMessagesTask.setOnSucceeded(event -> {
            VBox messageContainer = loadMessagesTask.getValue();
            scrollPane.setContent(messageContainer);

            // Remove loading label
            openFileWindow.getChildren().remove(loadingLabel);

            FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), openFileWindow);
            fadeTransition.setFromValue(1.0);
            fadeTransition.setToValue(0.0);
            fadeTransition.setOnFinished(evt -> {
                openFileWindow.getChildren().clear();
                openFileWindow.getChildren().setAll(scrollPane, openFileBtn1, filePathLbl);
                openFileWindow.setOpacity(1.0); // Reset opacity after transition

                openFileBtn1.setVisible(true);
                filePathLbl.setText(filePath);
                filePathLbl.setVisible(true);
            });
            fadeTransition.play();
        });

        loadMessagesTask.setOnFailed(event -> {
            Throwable throwable = loadMessagesTask.getException();
            throwable.printStackTrace();
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);;
            alert.setContentText("An error occurred while reading the MSG file: incorrect format.");
            alert.showAndWait().ifPresent(response -> {
                // After the alert is closed, go back to the open file scene
                Platform.runLater(() -> {
                    openFileWindow.getChildren().clear();
                    openFileWindow.getChildren().setAll(noFileOpenedtxt, openFileBtn);
                    noFileOpenedtxt.setVisible(true);
                    openFileBtn.setVisible(true);
                });
            });
        });

        new Thread(loadMessagesTask).start();
    }

    /**
     * Parses message content and replaces emoticon text with corresponding images.
     *
     * @param content the message content to parse
     * @return a {@code TextFlow} containing the parsed content with emoticons
     */
    public TextFlow parseContentWithEmoticons(String content) {
        TextFlow textFlow = new TextFlow();
        String[] parts = content.split(" ");
        Image happyImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/chat/viewer/chatviewer/pictures/smile_happy.gif")));
        Image sadImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/chat/viewer/chatviewer/pictures/smile_happy.gif")));
        for (String part : parts) {
            if (part.equals(":)")) {
                ImageView happyImageView = new ImageView(happyImage);
                happyImageView.setFitWidth(20);
                happyImageView.setFitHeight(20);
                textFlow.getChildren().add(happyImageView);
            } else if (part.equals(":(")) {
                ImageView sadImageView = new ImageView(sadImage);
                sadImageView.setFitWidth(20);
                sadImageView.setFitHeight(20);
                textFlow.getChildren().add(sadImageView);
            } else {
                textFlow.getChildren().add(new Text(part + " "));
            }
        }
        return textFlow;
    }

    /**
     * Reads messages from a CSV file and returns them as a list of maps.
     *
     * @param filePath the path to the CSV file
     * @return a list of maps containing message details
     * @throws FileNotFoundException if the file is not found
     */
    public List<Map<String, String>> csvFileReader(String filePath) throws FileNotFoundException {
        List<Map<String, String>> messages = new ArrayList<>();

        try (Scanner sc = new Scanner(new File(filePath))) {
            int lineIndex = 1;
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] properties = line.split(",", -1);
                try {
                    String timeStamp = properties[0].trim();
                    String nickname = properties[1].trim();
                    String content = properties[2].trim();

                    Map<String, String> message = new HashMap<>();
                    message.put("timestamp", timeStamp);
                    message.put("nickname", nickname);
                    message.put("content", content);
                    messages.add(message);
                } catch (NumberFormatException e) {
                    // Handle the exception if necessary
                    e.printStackTrace();
                }
                lineIndex++;
            }
        } catch (FileNotFoundException e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Reading CSV File");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while reading the CSV file.");
            alert.showAndWait();
        }

        return messages;
    }

    /**
     * Reads messages from a JSON file and returns them as a list of maps.
     *
     * @param filePath the path to the JSON file
     * @return a list of maps containing message details
     */
    public List<Map<String, String>> jsonFileReader(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, String>> messages = new ArrayList<>();

        try {
            List<Message> chatMessages = objectMapper.readValue(new File(filePath), new TypeReference<List<Message>>() {});
            for (Message message : chatMessages) {
                Map<String, String> messageMap = new HashMap<>();
                messageMap.put("timestamp", message.getTimestamp());
                messageMap.put("nickname", message.getNickname());
                messageMap.put("content", message.getContent());
                messages.add(messageMap);
            }
        } catch (IOException e) {
            e.printStackTrace();
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Reading JSON File");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while reading the JSON file.");
            alert.showAndWait();
        }

        return messages;
    }

    /**
     * Reads messages from a .msg file and returns them as a list of maps.
     *
     * @param filePath the path to the .msg file
     * @return a list of maps containing message details
     * @throws FileNotFoundException if the file is not found
     */

    public List<Map<String, String>> msgFileReader(String filePath) throws FileNotFoundException {
        List<Map<String, String>> messages = new ArrayList<>();
        try (Scanner sc = new Scanner(new File(filePath))) {
            String timestamp = "";
            String nickname = "";
            String content = "";
            boolean isNewGroup = true;

            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();

                if (line.isEmpty()) {
                    isNewGroup = true; // Empty line indicates a new group starts next
                    continue;
                }

                if (isNewGroup) {
                    if (!line.startsWith("Time:")) {
                        showAlert("An error occurred while reading the MSG file: incorrect format.");
                        return Collections.emptyList(); // Return empty list on error
                    }
                    isNewGroup = false;
                }

                if (line.startsWith("Time:")) {
                    timestamp = line.substring(5).trim();
                } else if (line.startsWith("Name:")) {
                    nickname = line.substring(5).trim();
                } else if (line.startsWith("Message:")) {
                    content = line.substring(8).trim();

                    Map<String, String> message = new HashMap<>();
                    message.put("timestamp", timestamp);
                    message.put("nickname", nickname);
                    message.put("content", content);

                    messages.add(message);

                    isNewGroup = true;
                } else {
                    showAlert("An error occurred while reading the MSG file: incorrect format.");
                    return Collections.emptyList(); // Return empty list on error
                }
            }
            
            if (!isNewGroup) {
                showAlert("An error occurred while reading the MSG file: incorrect format.");
                return Collections.emptyList();
            }
        }

        return messages;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Reading MSG File");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
