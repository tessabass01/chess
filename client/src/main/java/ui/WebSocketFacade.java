package ui;

import webSocketMessages.serverMessages.ServerMessage;

import javax.websocket.*;
import java.net.URI;
import java.util.Scanner;

public class WebSocketFacade extends Endpoint {

    public static void main(String[] args) throws Exception {
        var ws = new WebSocketFacade();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter a message you want to echo");
        while (true) ws.send(scanner.nextLine());
    }

    public Session session;

    public WebSocketFacade(NotificationHandler notificationHandler) throws Exception {
        URI uri = new URI("ws://localhost:8080/connect");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<ServerMessage>() {
            public void onMessage(ServerMessage message) {
                notificationHandler.notify(message);
                System.out.println(message.toString());
            }
        });
    }

    // joinGame
    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
