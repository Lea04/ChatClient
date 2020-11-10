package org.oettel.ui.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.oettel.businesslogic.UnicastClientService;
import org.oettel.configuration.ClientConfigurationSingleton;
import org.oettel.listener.MulticastListener;
import org.oettel.listener.UnicastListener;
import org.oettel.model.message.ClientMessage;
import org.oettel.model.message.ClientMessageType;
import org.oettel.model.message.Message;
import org.oettel.sender.BroadcastSender;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.oettel.configuration.Constants.SERVER_PORT;

public class LoginController {

    private static ExecutorService pool = Executors.newFixedThreadPool(20);

    @FXML
    private javafx.scene.control.Button closeButton;

    @FXML
    private TextField nameInputText;

    @FXML
    private void performLogin() throws IOException {
        initialConfigurationOfServer(SERVER_PORT);
        startingListener();
        ClientConfigurationSingleton.getInstance().setClientName(nameInputText.getText());
        sendInitialBroadcast();
    }

    /**
     * Method for configuring the Server startup.
     *
     * @param serverPort for server.
     */
    private static void initialConfigurationOfServer(final int serverPort) {
        System.out.println("#### initialization of server ####");
        ClientConfigurationSingleton.getInstance().setServerPort(serverPort);
        try {
            ClientConfigurationSingleton.getInstance().setServerAddress(InetAddress.getByAddress(InetAddress.getLocalHost().getAddress()));
            ClientConfigurationSingleton.getInstance().setSequenceNumber(1);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        System.out.println("#### initialization of server completed ####");
    }

    /**
     * starts the listeners for unicast and multicast messages.
     */
    private static void startingListener() {
        System.out.println("#### starting listener ####");
        //ExecutorService pool = Executors.newFixedThreadPool(20);

        try {
            UnicastListener unicastListener = new UnicastListener(new UnicastClientService());
            MulticastListener multicastListener = new MulticastListener();
            pool.execute(unicastListener);
            pool.execute(multicastListener);

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    /**
     * Sends the initial broadcast and checks for other servers.
     *
     * @throws SocketException
     * @throws UnknownHostException
     */
    private static void sendInitialBroadcast() throws SocketException, UnknownHostException {
        BroadcastSender broadCastSender = new BroadcastSender();
        try {
            ObjectMapper mapper = new ObjectMapper();
            Message message = new ClientMessage(ClientMessageType.CLIENT_BROADCAST, "client broadcast");
            broadCastSender.sendEcho(mapper.writeValueAsString(message));
            broadCastSender.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void shutdown() {
        pool.shutdown();
    }

    @FXML
    private void closeButtonAction() {
        // get a handle to the stage
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }


}
