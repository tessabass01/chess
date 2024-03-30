package ui;

import java.io.PrintStream;
import java.util.Objects;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLACK;

public class JoinBoard {

    private static final int BOARD_SIZE_IN_SQUARES = 8;

    public static void printHeader(PrintStream out, String color) {

        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);

        if (Objects.equals(color, "white")) {
            out.print("    a  b  c  d  e  f  g  h    ");
        } else if (Objects.equals(color, "black")) {
            out.print("    h  g  f  e  d  c  b  a    ");
        }
            out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
            out.println();
    }

    static void drawChessBoard(PrintStream out, String color) {

        if (Objects.equals(color, "black")) {

            for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
                out.print(SET_BG_COLOR_LIGHT_GREY);
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(" " + (boardRow + 1) + " ");
                out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);

                printRow(out, "black", boardRow);

                out.print(SET_BG_COLOR_LIGHT_GREY);
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(" " + (boardRow + 1) + " ");
                out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
                out.println();
            }
        } else if (Objects.equals(color, "white")) {
            int reverseRow = 8;

            for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
                out.print(SET_BG_COLOR_LIGHT_GREY);
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(" " + reverseRow + " ");
                out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);

                printRow(out, "white", boardRow);

                out.print(SET_BG_COLOR_LIGHT_GREY);
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(" " + reverseRow + " ");
                out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
                out.println();

                reverseRow--;
            }
        }
    }
    private static void printRow(PrintStream out, String playerColor, int rowIndex) {
        boolean bgIsWhite = true;
        String[] pieces = {};
        if (playerColor == "white") {
            out.print(SET_TEXT_COLOR_BLUE);
            if (rowIndex == 0 || rowIndex == 7) {
                pieces = new String[]{" R ", " N ", " B ", " Q ", " K ", " B ", " N ", " R "};
            }
            if (rowIndex == 6 || rowIndex == 7) {
                out.print(SET_TEXT_COLOR_MAGENTA);
            }
        } else if (playerColor == "black"){
            out.print(SET_TEXT_COLOR_MAGENTA);
            if (rowIndex == 0 || rowIndex == 7) {
                pieces = new String[]{" R ", " N ", " B ", " K ", " Q ", " B ", " N ", " R "};
            }
            if (rowIndex == 6 || rowIndex == 7) {
                out.print(SET_TEXT_COLOR_BLUE);
            }
        }
        if (rowIndex == 1 || rowIndex == 6) {
            pieces = new String[]{" P ", " P ", " P ", " P ", " P ", " P ", " P ", " P "};
        } else if (rowIndex > 1 && rowIndex < 6) {
            pieces = new String[]{"   ", "   ", "   ", "   ", "   ", "   ", "   ", "   "};
        }
        if (rowIndex % 2 == 0) {
            out.print(SET_BG_COLOR_WHITE);
        } else {
            out.print(SET_BG_COLOR_BLACK);
            bgIsWhite = false;
        }
        for (String piece : pieces) {
            out.print(piece);
            if (bgIsWhite) {
                out.print(SET_BG_COLOR_BLACK);
                bgIsWhite = false;
            } else {
                out.print(SET_BG_COLOR_WHITE);
                bgIsWhite = true;
            }
        }
    }
}
