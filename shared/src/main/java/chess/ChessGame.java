package chess;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board = new ChessBoard();
    private ChessGame.TeamColor turn = ChessGame.TeamColor.WHITE;

    public ChessGame() {
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        if (this.turn == TeamColor.BLACK) {
            this.turn = TeamColor.WHITE;
        } else {
            this.turn = TeamColor.BLACK;
        }
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        return board.getPiece(startPosition).pieceMoves(board, startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        var isValid = false;
        Collection<ChessMove> validMovesCollection = this.validMoves(move.getStartPosition());
        for (ChessMove element : validMovesCollection) {
            if (element == move) {
                isValid = true;
                break;
            }
        }
        if (isValid) {
            board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
            board.addPiece(move.getStartPosition(), null);
        } else {
            throw new InvalidMoveException("This is not a valid move");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessMove> otherTeamValidMoves = new ArrayList<ChessMove>();
        var kingPosition = new ChessPosition(0, 0);
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                var iterPosition = new ChessPosition(i, j);
                if (Objects.equals(this.board.getPiece(iterPosition), new ChessPiece(teamColor, ChessPiece.PieceType.KING))) {
                    kingPosition = iterPosition;
                } else if (this.board.getPiece(iterPosition) != null && this.board.getPiece(iterPosition).getTeamColor() != teamColor) {
                    var someValidMoves = this.validMoves(iterPosition);
                    otherTeamValidMoves.addAll(someValidMoves);
                }
            }
        }
        for (var move : otherTeamValidMoves) {
            if (Objects.equals(move.getEndPosition(), kingPosition)) {
                return true;
            }
        }
        return false;
    }

    private Collection<ChessPosition> getEndPositions(Collection<ChessMove> movesArray) {
        var endPositions = new ArrayList<ChessPosition>();
        for (var move : movesArray) {
            endPositions.add(move.getEndPosition());
        }
        return endPositions;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        var otherTeamValidMoves = new ArrayList<ChessMove>();
        Collection<ChessMove> kingValidMoves = new ArrayList<ChessMove>();
        var kingPosition = new ChessPosition(0, 0);
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                var iterPosition = new ChessPosition(i, j);
                if (Objects.equals(board.getPiece(iterPosition), new ChessPiece(teamColor, ChessPiece.PieceType.KING))) {
                    kingValidMoves = this.validMoves(iterPosition);
                    kingPosition = iterPosition;
                } else if (board.getPiece(iterPosition) != null && board.getPiece(iterPosition).getTeamColor() != teamColor) {
                    var someValidMoves = this.validMoves(iterPosition);
                    otherTeamValidMoves.addAll(someValidMoves);
                }
            }
        }
        var kingEndPositions = this.getEndPositions(kingValidMoves);
        kingEndPositions.add(kingPosition);
        var otherTeamEndPositions = this.getEndPositions(otherTeamValidMoves);
        if (otherTeamEndPositions.containsAll(kingEndPositions)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }



}


