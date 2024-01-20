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
                if (this.getPieceType() == PieceType.KING) {
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
