package org.oettel.ui.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.oettel.configuration.ClientConfigurationSingleton;
import org.oettel.model.message.ClientMessage;
import org.oettel.model.message.ClientMessageType;
import org.oettel.model.message.Message;
import org.oettel.model.vectorclock.VectorClockSingleton;
import org.oettel.sender.UnicastSender;
import org.oettel.ui.login.LoginController;

import java.io.IOException;

public class ChatController {


    @FXML
    private javafx.scene.control.Button closeButton;

    @FXML
    private Label labelUsernameChat;

    @FXML
    private TextField messageField;

    @FXML
    private TextArea chatArea;

    private UnicastSender unicastSender;


    @FXML
    private void initialize() {
        labelUsernameChat.setText("Username: " + ClientConfigurationSingleton.getInstance().getClientName());
        if (ClientConfigurationSingleton.getInstance().getLastReceivedChattMessage() != null) {
            System.out.println(ClientConfigurationSingleton.getInstance().getLastReceivedChattMessage());
            chatArea.appendText(ClientConfigurationSingleton.getInstance().getLastReceivedChattMessage());
        }
    }

    @FXML
    private void sendChatMessage() throws IOException {
        this.unicastSender = new UnicastSender(ClientConfigurationSingleton.getInstance().getLeader());
        String[] splittedLabelText = labelUsernameChat.getText().split(":");
        String username = splittedLabelText[1].replace(" ", "");
        VectorClockSingleton.getInstance().updateVectorclock();
        Message clientMessage = new ClientMessage(ClientMessageType.CHAT_MESSAGE, username + ": " + messageField.getText(), VectorClockSingleton.getInstance().getVectorClockEntryList());
        ObjectMapper objectMapper = new ObjectMapper();
        unicastSender.sendMessage(objectMapper.writeValueAsString(clientMessage));
        unicastSender.close();
    }

/*    public void logoutAction(ActionEvent actionEvent) throws IOException {
        //LoginController.shutdown();
        //Main.setRoot("/login");

    }*/


    @FXML
    private void closeButtonAction() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        LoginController.shutdown();
        stage.close();
    }


}
