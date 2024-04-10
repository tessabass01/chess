package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static ui.EscapeSequences.*;

import ui.Client;
import webSocketMessages.serverMessages.ServerMessage;

import java.util.Scanner;

public class Repl implements NotificationHandler {
    private final Client client;
    private final String url;

    public Repl(String url) {
        client = new Client(url);
        this.url = url;
    }

    public void run() {
        System.out.println(SET_TEXT_COLOR_WHITE + "Welcome to Chess!");
        System.out.print(SET_TEXT_COLOR_BLUE + client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                if (Objects.equals(result, "Go get 'em, WHITE!\n")) {
                    System.out.print(SET_TEXT_COLOR_BLUE + SET_TEXT_BLINKING + result);
//                    printBoard("white");
//                    printBoard("black");
                } else if (Objects.equals(result, "You got this, BLACK!\n")) {
                    System.out.print(SET_TEXT_COLOR_MAGENTA + SET_TEXT_BLINKING + result);
//                    printBoard("black");
//                    printBoard("white");
                } else if (Objects.equals(result, "Enjoy the show!\n")) {
                    System.out.print(SET_TEXT_COLOR_WHITE + SET_TEXT_BLINKING + result);
//                    printBoard("white");
//                    printBoard("black");
                } else {
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> loadGame();
            case ERROR -> error();
            case NOTIFICATION -> notification();
        }
    }

    private void loadGame() {
        System.out.print("loaded game");
    }

    private void error() {
        System.out.print("error");
    }

    private void notification() {
        System.out.print("notification");
    }

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_WHITE + ">>> " + RESET_TEXT_COLOR);
    }

    private void printBoard(String color) {
            var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

            out.print(ERASE_SCREEN);

            JoinBoard.printHeader(out, color);

            JoinBoard.drawChessBoard(out, color);

            JoinBoard.printHeader(out, color);

            out.print(SET_BG_COLOR_BLACK);
            out.print(SET_TEXT_COLOR_WHITE);
            out.println();
        }
    }
