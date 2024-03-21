package ui;

import java.io.PrintStream;
import java.util.Objects;
import java.util.Random;

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

        if (Objects.equals(color, "white")) {

            for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
                out.print(SET_BG_COLOR_LIGHT_GREY);
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(" " + (boardRow + 1) + " ");
                out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);

                drawRowOfSquares(out, boardRow, color);

                out.print(SET_BG_COLOR_LIGHT_GREY);
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(" " + (boardRow + 1) + " ");
                out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
                out.println();
            }
        } else if (Objects.equals(color, "black")) {
            int reverseRow = 8;

            for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
                out.print(SET_BG_COLOR_LIGHT_GREY);
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(" " + reverseRow + " ");
                out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);

                drawRowOfSquares(out, boardRow, color);

                out.print(SET_BG_COLOR_LIGHT_GREY);
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(" " + reverseRow + " ");
                out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
                out.println();

                reverseRow--;
            }
        }
    }

    private static void drawRowOfSquares(PrintStream out, int rowN, String color) {

        String[] pieces = {" R ", " N ", " B ", " K ", " Q ", " B ", " N ", " R "};

        if (rowN == 0) {
            if (Objects.equals(color, "white")) {
                out.print(SET_BG_COLOR_WHITE);
                out.print(SET_TEXT_COLOR_RED);
                out.print(pieces[0]);
                out.print(SET_BG_COLOR_BLACK);
                out.print(pieces[1]);
                out.print(SET_BG_COLOR_WHITE);
                out.print(pieces[2]);
                out.print(SET_BG_COLOR_BLACK);
                out.print(pieces[3]);
                out.print(SET_BG_COLOR_WHITE);
                out.print(pieces[4]);
                out.print(SET_BG_COLOR_BLACK);
                out.print(pieces[5]);
                out.print(SET_BG_COLOR_WHITE);
                out.print(pieces[6]);
                out.print(SET_BG_COLOR_BLACK);
                out.print(pieces[7]);
            } else if (Objects.equals(color, "black")) {
                out.print(SET_BG_COLOR_WHITE);
                out.print(SET_TEXT_COLOR_BLUE);
                out.print(pieces[0]);
                out.print(SET_BG_COLOR_BLACK);
                out.print(pieces[1]);
                out.print(SET_BG_COLOR_WHITE);
                out.print(pieces[2]);
                out.print(SET_BG_COLOR_BLACK);
                out.print(pieces[4]);
                out.print(SET_BG_COLOR_WHITE);
                out.print(pieces[3]);
                out.print(SET_BG_COLOR_BLACK);
                out.print(pieces[5]);
                out.print(SET_BG_COLOR_WHITE);
                out.print(pieces[6]);
                out.print(SET_BG_COLOR_BLACK);
                out.print(pieces[7]);
            }
            out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
        } else if (rowN == 1) {
            out.print(SET_BG_COLOR_BLACK);
            if (Objects.equals(color, "white")) {
                out.print(SET_TEXT_COLOR_RED);
            } else if (Objects.equals(color, "black")) {
                out.print(SET_TEXT_COLOR_BLUE);
            }
            out.print(" P ");
            out.print(SET_BG_COLOR_WHITE);
            out.print(" P ");
            out.print(SET_BG_COLOR_BLACK);
            out.print(" P ");
            out.print(SET_BG_COLOR_WHITE);
            out.print(" P ");
            out.print(SET_BG_COLOR_BLACK);
            out.print(" P ");
            out.print(SET_BG_COLOR_WHITE);
            out.print(" P ");
            out.print(SET_BG_COLOR_BLACK);
            out.print(" P ");
            out.print(SET_BG_COLOR_WHITE);
            out.print(" P ");
            out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
        } else if (rowN == 2 || rowN == 4) {
            setWhite(out);
            out.print("   ");
            setBlack(out);
            out.print("   ");
            setWhite(out);
            out.print("   ");
            setBlack(out);
            out.print("   ");
            setWhite(out);
            out.print("   ");
            setBlack(out);
            out.print("   ");
            setWhite(out);
            out.print("   ");
            setBlack(out);
            out.print("   ");
            out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
        } else if (rowN == 3 || rowN == 5) {
            setBlack(out);
            out.print("   ");
            setWhite(out);
            out.print("   ");
            setBlack(out);
            out.print("   ");
            setWhite(out);
            out.print("   ");
            setBlack(out);
            out.print("   ");
            setWhite(out);
            out.print("   ");
            setBlack(out);
            out.print("   ");
            setWhite(out);
            out.print("   ");
            out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
        } else if (rowN == 6) {
            out.print(SET_BG_COLOR_WHITE);
            if (Objects.equals(color, "white")) {
                out.print(SET_TEXT_COLOR_BLUE);
            } else if (Objects.equals(color, "black")) {
                out.print(SET_TEXT_COLOR_RED);
            }
            out.print(" P ");
            out.print(SET_BG_COLOR_BLACK);
            out.print(" P ");
            out.print(SET_BG_COLOR_WHITE);
            out.print(" P ");
            out.print(SET_BG_COLOR_BLACK);
            out.print(" P ");
            out.print(SET_BG_COLOR_WHITE);
            out.print(" P ");
            out.print(SET_BG_COLOR_BLACK);
            out.print(" P ");
            out.print(SET_BG_COLOR_WHITE);
            out.print(" P ");
            out.print(SET_BG_COLOR_BLACK);
            out.print(" P ");
            out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
        } else if (rowN == 7) {
            if (Objects.equals(color, "white")) {
                out.print(SET_BG_COLOR_BLACK);
                out.print(SET_TEXT_COLOR_BLUE);
                out.print(pieces[0]);
                out.print(SET_BG_COLOR_WHITE);
                out.print(pieces[1]);
                out.print(SET_BG_COLOR_BLACK);
                out.print(pieces[2]);
                out.print(SET_BG_COLOR_WHITE);
                out.print(pieces[3]);
                out.print(SET_BG_COLOR_BLACK);
                out.print(pieces[4]);
                out.print(SET_BG_COLOR_WHITE);
                out.print(pieces[5]);
                out.print(SET_BG_COLOR_BLACK);
                out.print(pieces[6]);
                out.print(SET_BG_COLOR_WHITE);
                out.print(pieces[7]);
            } else if (Objects.equals(color, "black")) {
                out.print(SET_BG_COLOR_BLACK);
                out.print(SET_TEXT_COLOR_RED);
                out.print(pieces[0]);
                out.print(SET_BG_COLOR_WHITE);
                out.print(pieces[1]);
                out.print(SET_BG_COLOR_BLACK);
                out.print(pieces[2]);
                out.print(SET_BG_COLOR_WHITE);
                out.print(pieces[4]);
                out.print(SET_BG_COLOR_BLACK);
                out.print(pieces[3]);
                out.print(SET_BG_COLOR_WHITE);
                out.print(pieces[5]);
                out.print(SET_BG_COLOR_BLACK);
                out.print(pieces[6]);
                out.print(SET_BG_COLOR_WHITE);
                out.print(pieces[7]);
            }
            out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
        }
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setRed(PrintStream out) {
        out.print(SET_BG_COLOR_RED);
        out.print(SET_TEXT_COLOR_RED);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }
}
