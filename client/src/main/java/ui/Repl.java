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
                    printBoardWhite();
                } else if (Objects.equals(result, "You got this, BLACK!\n")) {
                    printBoardBlack();
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

    private void printBoardWhite() {
            var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

            out.print(ERASE_SCREEN);

            JoinBoard.printHeader(out);

            JoinBoard.drawChessBoard(out);

            JoinBoard.printHeader(out);

            out.print(SET_BG_COLOR_BLACK);
            out.print(SET_TEXT_COLOR_WHITE);
        }

    private void printBoardBlack() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        JoinBoard.printHeader(out);

        JoinBoard.drawChessBoard(out);

        JoinBoard.printHeader(out);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    }
