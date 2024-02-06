package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {

        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {

        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        var moves = new ArrayList<ChessMove>();
        if (this.getPieceType() == PieceType.BISHOP) {
            moveDiagonal(myPosition, moves, board);
        } else if (this.getPieceType() == PieceType.ROOK) {
            moveLinear(myPosition, moves, board);
        } else if (this.getPieceType() == PieceType.QUEEN) {
            moveDiagonal(myPosition, moves, board);
            moveLinear(myPosition, moves, board);
        } else if (this.getPieceType() == PieceType.KING) {
            moveDiagonal(myPosition, moves, board);
            moveLinear(myPosition, moves, board);
        } else if (this.getPieceType() == PieceType.KNIGHT) {
            moveL(myPosition, moves, board);
        } else if (this.getPieceType() == PieceType.PAWN) {
            pawnMoveHelper(myPosition, moves, board);
        }
            return moves;
    }

    private void moveHelper(ChessPosition startPosition, Collection<ChessMove> moves, ChessBoard board, int x, int y) {
        ChessPosition currentPosition = startPosition;
        while ((startPosition.getRow()) <= 8 && (startPosition.getRow()) >= 1 && (startPosition.getColumn()) <= 8 && (startPosition.getColumn()) >= 1) {
            ChessPosition endPosition = new ChessPosition(startPosition.getRow() - y, startPosition.getColumn() + x);
            if (endPosition.getColumn() > 8 || endPosition.getColumn() < 1 || endPosition.getRow() > 8 || endPosition.getRow() < 1) {
                break;
            }
            if (board.getPiece(endPosition) == null) {
                moves.add(new ChessMove(currentPosition, endPosition, null));
                startPosition = endPosition;
                if (this.getPieceType() == PieceType.KING || this.getPieceType() == PieceType.KNIGHT) {
                    break;
                }
            } else if (board.getPiece(endPosition).getTeamColor() != this.getTeamColor()) {
                moves.add(new ChessMove(currentPosition, endPosition, null));
                break;
            } else {
                break;
            }
        }
    }
    private void moveDiagonal(ChessPosition myPosition, Collection<ChessMove> moves, ChessBoard board) {
        moveHelper(myPosition, moves, board, 1, -1);
        moveHelper(myPosition, moves, board, 1, 1);
        moveHelper(myPosition, moves, board, -1, 1);
        moveHelper(myPosition, moves, board, -1, -1);
    }

    private void moveLinear(ChessPosition myPosition, Collection<ChessMove> moves, ChessBoard board) {
        moveHelper(myPosition, moves, board, 1, 0);
        moveHelper(myPosition, moves, board, -1, 0);
        moveHelper(myPosition, moves, board, 0, 1);
        moveHelper(myPosition, moves, board, 0, -1);
    }

    private void moveL(ChessPosition myPosition, Collection<ChessMove> moves, ChessBoard board) {
        moveHelper(myPosition, moves, board, 2, 1);
        moveHelper(myPosition, moves, board, 2, -1);
        moveHelper(myPosition, moves, board, -2, 1);
        moveHelper(myPosition, moves, board, -2, -1);
        moveHelper(myPosition, moves, board, 1, 2);
        moveHelper(myPosition, moves, board, -1, 2);
        moveHelper(myPosition, moves, board, 1, -2);
        moveHelper(myPosition, moves, board, -1, -2);
    }

    private void pawnMoveHelper(ChessPosition startPosition, Collection<ChessMove> moves, ChessBoard board) {
        if (board.getPiece(startPosition).getTeamColor() == ChessGame.TeamColor.WHITE) {
            ChessPosition whiteFirstPosition = new ChessPosition(startPosition.getRow() + 1, startPosition.getColumn());
            ChessPosition whiteCaptureLeft = new ChessPosition(startPosition.getRow() + 1, startPosition.getColumn() - 1);
            ChessPosition whiteCaptureRight = new ChessPosition(startPosition.getRow() + 1, startPosition.getColumn() + 1);
            if (whiteFirstPosition.getColumn() <= 8 && whiteFirstPosition.getColumn() >= 1 && whiteFirstPosition.getRow() <= 8 && whiteFirstPosition.getRow() >= 1) {
                if (startPosition.getRow() <= 6) {
                    if (whiteCaptureLeft.getColumn() <= 8 && whiteCaptureLeft.getColumn() >= 1 && whiteCaptureLeft.getRow() <= 8 && whiteCaptureLeft.getRow() >= 1) {
                        if (board.getPiece(whiteCaptureLeft) != null && board.getPiece(whiteCaptureLeft).getTeamColor() == ChessGame.TeamColor.BLACK) {
                            moves.add(new ChessMove(startPosition, whiteCaptureLeft, null));
                        }
                    }
                    if (whiteCaptureRight.getColumn() <= 8 && whiteCaptureRight.getColumn() >= 1 && whiteCaptureRight.getRow() <= 8 && whiteCaptureRight.getRow() >= 1) {
                        if (board.getPiece(whiteCaptureRight) != null && board.getPiece(whiteCaptureRight).getTeamColor() == ChessGame.TeamColor.BLACK) {
                            moves.add(new ChessMove(startPosition, whiteCaptureRight, null));
                        }
                    }
                    if (board.getPiece(whiteFirstPosition) == null) {
                        moves.add(new ChessMove(startPosition, whiteFirstPosition, null));
                        if (startPosition.getRow() == 2) {
                            ChessPosition whiteSecondPosition = new ChessPosition(startPosition.getRow() + 2, startPosition.getColumn());
                            if (board.getPiece(whiteSecondPosition) == null) {
                                moves.add(new ChessMove(startPosition, whiteSecondPosition, null));
                            }
                        }
                    }
                } else if (startPosition.getRow() == 7) {
                    if (board.getPiece(whiteFirstPosition) == null) {
                        moves.add(new ChessMove(startPosition, whiteFirstPosition, PieceType.KNIGHT));
                        moves.add(new ChessMove(startPosition, whiteFirstPosition, PieceType.ROOK));
                        moves.add(new ChessMove(startPosition, whiteFirstPosition, PieceType.QUEEN));
                        moves.add(new ChessMove(startPosition, whiteFirstPosition, PieceType.BISHOP));
                    }
                    if (board.getPiece(whiteCaptureLeft) != null && board.getPiece(whiteCaptureLeft).getTeamColor() == ChessGame.TeamColor.BLACK) {
                        moves.add(new ChessMove(startPosition, whiteCaptureLeft, PieceType.KNIGHT));
                        moves.add(new ChessMove(startPosition, whiteCaptureLeft, PieceType.ROOK));
                        moves.add(new ChessMove(startPosition, whiteCaptureLeft, PieceType.QUEEN));
                        moves.add(new ChessMove(startPosition, whiteCaptureLeft, PieceType.BISHOP));
                    }
                    if (board.getPiece(whiteCaptureRight) != null && board.getPiece(whiteCaptureRight).getTeamColor() == ChessGame.TeamColor.BLACK) {
                        moves.add(new ChessMove(startPosition, whiteCaptureRight, PieceType.KNIGHT));
                        moves.add(new ChessMove(startPosition, whiteCaptureRight, PieceType.ROOK));
                        moves.add(new ChessMove(startPosition, whiteCaptureRight, PieceType.QUEEN));
                        moves.add(new ChessMove(startPosition, whiteCaptureRight, PieceType.BISHOP));
                    }

                }
            }
        } else {
            ChessPosition firstBlackPosition = new ChessPosition(startPosition.getRow() - 1, startPosition.getColumn());
            ChessPosition blackCaptureLeft = new ChessPosition(startPosition.getRow() - 1, startPosition.getColumn() - 1);
            ChessPosition blackCaptureRight = new ChessPosition(startPosition.getRow() - 1, startPosition.getColumn() + 1);
            if (firstBlackPosition.getColumn() <= 8 && firstBlackPosition.getColumn() >= 1 && firstBlackPosition.getRow() <= 8 && firstBlackPosition.getRow() >= 1) {
                if (startPosition.getRow() >= 3) {
                    if (blackCaptureLeft.getColumn() <= 8 && blackCaptureLeft.getColumn() >= 1 && blackCaptureLeft.getRow() <= 8 && blackCaptureLeft.getRow() >= 1) {
                        if (board.getPiece(blackCaptureLeft) != null && board.getPiece(blackCaptureLeft).getTeamColor() == ChessGame.TeamColor.WHITE) {
                            moves.add(new ChessMove(startPosition, blackCaptureLeft, null));
                        }
                    }
                    if (blackCaptureRight.getColumn() <= 8 && blackCaptureRight.getColumn() >= 1 && blackCaptureRight.getRow() <= 8 && blackCaptureRight.getRow() >= 1) {
                        if (board.getPiece(blackCaptureRight) != null && board.getPiece(blackCaptureRight).getTeamColor() == ChessGame.TeamColor.WHITE) {
                            moves.add(new ChessMove(startPosition, blackCaptureRight, null));
                        }
                    }
                    if (board.getPiece(firstBlackPosition) == null) {
                        moves.add(new ChessMove(startPosition, firstBlackPosition, null));
                        if (startPosition.getRow() == 7) {
                            ChessPosition blackSecondPosition = new ChessPosition(startPosition.getRow() - 2, startPosition.getColumn());
                            if (board.getPiece(blackSecondPosition) == null) {
                                moves.add(new ChessMove(startPosition, blackSecondPosition, null));
                            }
                        }
                    }
                } else if (startPosition.getRow() == 2) {
                    if (board.getPiece(firstBlackPosition) == null) {
                        moves.add(new ChessMove(startPosition, firstBlackPosition, PieceType.KNIGHT));
                        moves.add(new ChessMove(startPosition, firstBlackPosition, PieceType.ROOK));
                        moves.add(new ChessMove(startPosition, firstBlackPosition, PieceType.QUEEN));
                        moves.add(new ChessMove(startPosition, firstBlackPosition, PieceType.BISHOP));
                    }
                    if (board.getPiece(blackCaptureLeft) != null && board.getPiece(blackCaptureLeft).getTeamColor() == ChessGame.TeamColor.WHITE) {
                        moves.add(new ChessMove(startPosition, blackCaptureLeft, PieceType.KNIGHT));
                        moves.add(new ChessMove(startPosition, blackCaptureLeft, PieceType.ROOK));
                        moves.add(new ChessMove(startPosition, blackCaptureLeft, PieceType.QUEEN));
                        moves.add(new ChessMove(startPosition, blackCaptureLeft, PieceType.BISHOP));
                    }
                    if (board.getPiece(blackCaptureRight) != null && board.getPiece(blackCaptureRight).getTeamColor() == ChessGame.TeamColor.WHITE) {
                        moves.add(new ChessMove(startPosition, blackCaptureRight, PieceType.KNIGHT));
                        moves.add(new ChessMove(startPosition, blackCaptureRight, PieceType.ROOK));
                        moves.add(new ChessMove(startPosition, blackCaptureRight, PieceType.QUEEN));
                        moves.add(new ChessMove(startPosition, blackCaptureRight, PieceType.BISHOP));
                    }
                }
            }
        }
    }


    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
