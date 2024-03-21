package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Random;

import static ui.EscapeSequences.*;

import ui.Client;

import java.util.Scanner;

public class Repl {
    private final Client client;
    private final String url;

    public Repl(String url) {
        client = new Client(url);
        this.url = url;
    }

    public void run() {
        System.out.println(SET_TEXT_COLOR_BLUE + "Welcome to Chess!");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
                if (Objects.equals(result, "Go get 'em, WHITE!\n")) {
                    printBoard("white");
                    printBoard("black");
                } else if (Objects.equals(result, "You got this, BLACK!\n")) {
                    printBoard("black");
                    printBoard("white");
                } else if (Objects.equals(result, "Enjoy the show!\n")) {
                    printBoard("white");
                    printBoard("black");
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
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
