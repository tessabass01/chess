package ui;

import java.io.PrintStream;
import java.util.Random;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLACK;

public class JoinBoard {

    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_CHARS = 1;
    private static boolean isBlack = false;
    private static final String EMPTY = "   ";
    private static final String X = " X ";
    private static final String O = " O ";
    private static Random rand = new Random();

    public static void printHeader(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);

        out.print("    a  b  c  d  e  f  g  h    ");
        out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
        out.println();
    }

    static void drawChessBoard(PrintStream out) {

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
            out.print(SET_BG_COLOR_LIGHT_GREY);
            out.print(SET_TEXT_COLOR_BLACK);
            out.print(" " + (boardRow+1) + " ");
            out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);

            drawRowOfSquares(out, boardRow);

            out.print(SET_BG_COLOR_LIGHT_GREY);
            out.print(SET_TEXT_COLOR_BLACK);
            out.print(" " + (boardRow+1) + " ");
            out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
            out.println();
        }
    }

    private static void drawRowOfSquares(PrintStream out, int rowN) {

        String[] pieces = {" R ", " N ", " B ", " K ", " Q ", " B ", " N ", " R "};

        if (rowN == 0) {
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
            out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
        } else if (rowN == 1) {
            out.print(SET_BG_COLOR_BLACK);
            out.print(SET_TEXT_COLOR_RED);
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
            out.print(SET_TEXT_COLOR_BLUE);
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

    private static void printPlayer(PrintStream out, String player) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(player);

        setWhite(out);
    }
}
