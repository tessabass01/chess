package ui;

import chess.*;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLACK;

public class JoinBoard {

    private static HashMap<ChessPiece, String> reference = new HashMap<>();

    public static HashMap<ChessPiece, String> init() {
        reference.put(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK), SET_TEXT_COLOR_BLUE + " R ");
        reference.put(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT), SET_TEXT_COLOR_BLUE + " N ");
        reference.put(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP), SET_TEXT_COLOR_BLUE + " B ");
        reference.put(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN), SET_TEXT_COLOR_BLUE + " Q ");
        reference.put(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING), SET_TEXT_COLOR_BLUE + " K ");
        reference.put(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN), SET_TEXT_COLOR_BLUE + " P ");
        reference.put(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK), SET_TEXT_COLOR_MAGENTA + " R ");
        reference.put(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT), SET_TEXT_COLOR_MAGENTA + " N ");
        reference.put(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP), SET_TEXT_COLOR_MAGENTA + " B ");
        reference.put(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN), SET_TEXT_COLOR_MAGENTA + " Q ");
        reference.put(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING), SET_TEXT_COLOR_MAGENTA + " K ");
        reference.put(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN), SET_TEXT_COLOR_MAGENTA + " P ");
        return reference;
    }

    public static void printHeader(PrintStream out, ChessGame.TeamColor playerColor) {
        String color = null;
        if (playerColor.equals(ChessGame.TeamColor.WHITE)) {
            color = "white";
        } else if (playerColor.equals(ChessGame.TeamColor.BLACK)) {
            color = "black";
        }

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

    static void drawChessBoard(PrintStream out, ChessGame.TeamColor playerColor, ChessBoard board) {

        String color = null;
        if (playerColor.equals(ChessGame.TeamColor.WHITE)) {
            color = "white";
        } else if (playerColor.equals(ChessGame.TeamColor.BLACK)) {
            color = "black";
        }

        if (Objects.equals(color, "black")) {

//            for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
//                out.print(SET_BG_COLOR_LIGHT_GREY);
//                out.print(SET_TEXT_COLOR_BLACK);
//                out.print(" " + (boardRow + 1) + " ");
//                out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
            for (int i = 0; i < board.board.length; i++) {
                out.print(SET_BG_COLOR_LIGHT_GREY);
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(" " + (i + 1) + " ");
                out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);

                printRow(out, i, board, "black");

                out.print(SET_BG_COLOR_LIGHT_GREY);
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(" " + (i + 1) + " ");
                out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
                out.println();
            }
        } else if (Objects.equals(color, "white")) {
            for (int i = 7; i >= 0; i--) {
                out.print(SET_BG_COLOR_LIGHT_GREY);
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(" " + (i + 1) + " ");
                out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);

                printRow(out, i, board, "white");

                out.print(SET_BG_COLOR_LIGHT_GREY);
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(" " + (i + 1) + " ");
                out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
                out.println();
            }
        }
    }

    private static void printRow(PrintStream out, int rowIndex, ChessBoard board, String perspective) {
        reference = init();
        var pieces = board.board[rowIndex];
        if (perspective.equals("black")) {
            boolean bgIsWhite = rowIndex % 2 == 0;
            for (var j = 0; j < board.board.length; j++) {
                var piece = pieces[j];
                if (piece == null) {
                    if (bgIsWhite) {
                        out.print(SET_BG_COLOR_WHITE + "   ");
                        bgIsWhite = false;
                    } else {
                        out.print(SET_BG_COLOR_BLACK + "   ");
                        bgIsWhite = true;
                    }
                } else {
                    if (bgIsWhite) {
                        out.print(SET_BG_COLOR_WHITE + reference.get(piece));
                        bgIsWhite = false;
                    } else {
                        out.print(SET_BG_COLOR_BLACK + reference.get(piece));
                        bgIsWhite = true;
                    }
                }
            }
        } else if (perspective.equals("white")) {
            boolean bgIsWhite = rowIndex % 2 != 0;
            for (var j = 7; j >= 0; j--) {
                var piece = pieces[j];
                if (piece == null) {
                    if (bgIsWhite) {
                        out.print(SET_BG_COLOR_WHITE + "   ");
                        bgIsWhite = false;
                    } else {
                        out.print(SET_BG_COLOR_BLACK + "   ");
                        bgIsWhite = true;
                    }
                } else {
                    if (bgIsWhite) {
                        out.print(SET_BG_COLOR_WHITE + reference.get(piece));
                        bgIsWhite = false;
                    } else {
                        out.print(SET_BG_COLOR_BLACK + reference.get(piece));
                        bgIsWhite = true;
                    }
                }
            }
        }
    }

    static void highlightChessBoard(PrintStream out, ChessGame.TeamColor playerColor, ChessBoard board, ChessPosition startPosition, Collection<ChessMove> legalMoves) {

        String color = null;
        if (playerColor.equals(ChessGame.TeamColor.WHITE)) {
            color = "white";
        } else if (playerColor.equals(ChessGame.TeamColor.BLACK)) {
            color = "black";
        }

        if (Objects.equals(color, "black")) {

//            for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
//                out.print(SET_BG_COLOR_LIGHT_GREY);
//                out.print(SET_TEXT_COLOR_BLACK);
//                out.print(" " + (boardRow + 1) + " ");
//                out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
            for (int i = 0; i < board.board.length; i++) {
                out.print(SET_BG_COLOR_LIGHT_GREY);
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(" " + (i + 1) + " ");
                out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);

                highlightRow(out, i, board, "black", startPosition, legalMoves);

                out.print(SET_BG_COLOR_LIGHT_GREY);
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(" " + (i + 1) + " ");
                out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
                out.println();
            }
        } else if (Objects.equals(color, "white")) {
            for (int i = 7; i >= 0; i--) {
                out.print(SET_BG_COLOR_LIGHT_GREY);
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(" " + (i + 1) + " ");
                out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);

                highlightRow(out, i, board, "white", startPosition, legalMoves);

                out.print(SET_BG_COLOR_LIGHT_GREY);
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(" " + (i + 1) + " ");
                out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
                out.println();
            }
        }
    }

    private static void highlightRow(PrintStream out, int rowIndex, ChessBoard board, String perspective, ChessPosition startPosition, Collection<ChessMove> legalMoves) {
        reference = init();
        var pieces = board.board[rowIndex];
        var validEndPositions = new ArrayList<ChessPosition>();
        for (var move : legalMoves) {
            validEndPositions.add(move.getEndPosition());
        }
        if (perspective.equals("black")) {
            boolean bgIsWhite = rowIndex % 2 == 0;
            for (var j = 0; j < board.board.length; j++) {
                var piece = pieces[j];
                var currPosition = new ChessPosition(rowIndex+1, j+1);
                if (currPosition.equals(startPosition)) {
                    out.print(SET_BG_COLOR_YELLOW + SET_TEXT_COLOR_BLACK + reference.get(piece));
                    bgIsWhite = !bgIsWhite;
                } else if (validEndPositions.contains(currPosition)) {
                    if (piece == null) {
                        if (bgIsWhite) {
                            out.print(SET_BG_COLOR_GREEN + "   ");
                            bgIsWhite = false;
                        } else {
                            out.print(SET_BG_COLOR_DARK_GREEN + "   ");
                            bgIsWhite = true;
                        }
                    } else {
                        if (bgIsWhite) {
                            out.print(SET_BG_COLOR_GREEN + reference.get(piece));
                            bgIsWhite = false;
                        } else {
                            out.print(SET_BG_COLOR_DARK_GREEN + reference.get(piece));
                            bgIsWhite = true;
                        }
                    }
                } else {
                    if (piece == null) {
                        if (bgIsWhite) {
                            out.print(SET_BG_COLOR_WHITE + "   ");
                            bgIsWhite = false;
                        } else {
                            out.print(SET_BG_COLOR_BLACK + "   ");
                            bgIsWhite = true;
                        }
                    } else {
                        if (bgIsWhite) {
                            out.print(SET_BG_COLOR_WHITE + reference.get(piece));
                            bgIsWhite = false;
                        } else {
                            out.print(SET_BG_COLOR_BLACK + reference.get(piece));
                            bgIsWhite = true;
                        }
                    }
                }
            }
        } else if (perspective.equals("white")) {
            boolean bgIsWhite = rowIndex % 2 != 0;
            for (var j = 7; j >= 0; j--) {
                var piece = pieces[j];
                var currPosition = new ChessPosition(rowIndex+1, j+1);
                if (currPosition.equals(startPosition)) {
                    out.print(SET_BG_COLOR_YELLOW + SET_TEXT_COLOR_BLACK + reference.get(piece));
                    bgIsWhite = !bgIsWhite;
                } else if (validEndPositions.contains(currPosition)) {
                    if (piece == null) {
                        if (bgIsWhite) {
                            out.print(SET_BG_COLOR_GREEN + "   ");
                            bgIsWhite = false;
                        } else {
                            out.print(SET_BG_COLOR_DARK_GREEN + "   ");
                            bgIsWhite = true;
                        }
                    } else {
                        if (bgIsWhite) {
                            out.print(SET_BG_COLOR_GREEN + reference.get(piece));
                            bgIsWhite = false;
                        } else {
                            out.print(SET_BG_COLOR_DARK_GREEN + reference.get(piece));
                            bgIsWhite = true;
                        }
                    }
                } else {
                    if (piece == null) {
                        if (bgIsWhite) {
                            out.print(SET_BG_COLOR_WHITE + "   ");
                            bgIsWhite = false;
                        } else {
                            out.print(SET_BG_COLOR_BLACK + "   ");
                            bgIsWhite = true;
                        }
                    } else {
                        if (bgIsWhite) {
                            out.print(SET_BG_COLOR_WHITE + reference.get(piece));
                            bgIsWhite = false;
                        } else {
                            out.print(SET_BG_COLOR_BLACK + reference.get(piece));
                            bgIsWhite = true;
                        }
                    }
                }
            }
        }
    }
}
