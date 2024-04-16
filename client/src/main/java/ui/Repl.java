package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Objects;

import static ui.EscapeSequences.*;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import ui.Client;
import webSocketMessages.serverMessages.*;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.userCommands.UserGameCommand;

import java.util.Scanner;

public class Repl implements NotificationHandler {
    private final Client client;
    private final String url;

    public Repl(String url) {
        client = new Client(url, this);
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

    public void notify(String message) {
        var serverMessage = new Gson().fromJson(message, ServerMessage.class);
        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME -> loadGame(message);
            case ERROR -> error(message);
            case NOTIFICATION -> notification(message);
            case HIGHLIGHT -> highlight(message);
        }
    }

    private void loadGame(String message) {
        var deserialized = new Gson().fromJson(message, LoadGame.class);
        var board = deserialized.game.getBoard();
        var color = deserialized.playerColor;
        printBoard(color, board);
//        System.out.println(SET_TEXT_COLOR_WHITE + deserialized.game.toString());
    }

    private void error(String message) {
        var deserialized = new Gson().fromJson(message, Error.class);
        System.out.println(SET_TEXT_COLOR_WHITE + deserialized.errorMessage);
    }

    private void notification(String message) {
        var deserialized = new Gson().fromJson(message, Notification.class);
        System.out.println(SET_TEXT_COLOR_WHITE + deserialized.message);
    }

    private void highlight(String message) {
        var deserialized = new Gson().fromJson(message, Highlight.class);
        var board = deserialized.game.getBoard();
        var color = deserialized.playerColor;
        var startPosition = deserialized.startPosition;
        var moves = deserialized.legalMoves;
        printHighlight(color, board, startPosition, moves);
//        System.out.println(SET_TEXT_COLOR_WHITE + deserialized.game.toString());
    }

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_WHITE);
    }

    private void printBoard(ChessGame.TeamColor playerColor, ChessBoard board) {
            var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

            out.print(ERASE_SCREEN);

            JoinBoard.printHeader(out, playerColor);

            JoinBoard.drawChessBoard(out, playerColor, board);

            JoinBoard.printHeader(out, playerColor);

            out.print(SET_BG_COLOR_BLACK);
            out.print(SET_TEXT_COLOR_WHITE);
            out.println();
        }

    private void printHighlight(ChessGame.TeamColor playerColor, ChessBoard board, ChessPosition startPosition, Collection<ChessMove> legalMoves) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        JoinBoard.printHeader(out, playerColor);

        JoinBoard.highlightChessBoard(out, playerColor, board, startPosition, legalMoves);

        JoinBoard.printHeader(out, playerColor);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
        out.println();
    }
    }
