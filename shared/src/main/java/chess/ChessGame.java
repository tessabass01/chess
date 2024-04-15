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
        board.resetBoard();
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
     * @param params the team whose turn it is
     */
    public void setTeamTurn(TeamColor... params) {
        if (params.length == 1) {
            if (this.turn == TeamColor.BLACK) {
                this.turn = TeamColor.WHITE;
            } else {
                this.turn = TeamColor.BLACK;
            }
        } else if (params.length == 0){
            this.turn = TeamColor.GAME_OVER;
        }
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK,
        GAME_OVER
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        var copyBoard = new ChessBoard(board);
        var piece = copyBoard.getPiece(startPosition);
        var moves = piece.pieceMoves(copyBoard, startPosition);
        var iter = moves.iterator();
        var validMoveList = new ArrayList<ChessMove>();
        for (int i = 0; i < moves.size(); i++) {
            var move = iter.next();
            var previousBoard = new ChessBoard(copyBoard);
            copyBoard.copyAddPiece(move.getEndPosition(), copyBoard.getPiece(move.getStartPosition()), copyBoard);
            copyBoard.copyAddPiece(move.getStartPosition(), null, copyBoard);
            if (!this.copyIsInCheck(piece.getTeamColor(), copyBoard)) {
                validMoveList.add(move);
            }
            copyBoard = previousBoard;
        }
        return validMoveList;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        var isValid = false;
        var startPosition = move.getStartPosition();
        var pieceColor = board.getPiece(startPosition).getTeamColor();
        var promotion = move.getPromotionPiece();
        var isRightColor = false;
        if (this.turn == pieceColor) {
            isRightColor = true;
        }
        Collection<ChessMove> movesCollection = this.validMoves(move.getStartPosition()); // replace with piece moves
        for (ChessMove element : movesCollection) {
            if (Objects.equals(element, move)) {
                isValid = true;
                break;
            }
        }
        if (isValid && isRightColor) {
            if (promotion == null) {
                board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
                board.addPiece(move.getStartPosition(), null);
            } else {
                board.addPiece(move.getEndPosition(), new ChessPiece(turn, promotion));
                board.addPiece(move.getStartPosition(), null);
            }
        } else {
            throw new InvalidMoveException("This is not a valid move");
        }
        setTeamTurn(this.turn);
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
                if (Objects.equals(board.getPiece(iterPosition), new ChessPiece(teamColor, ChessPiece.PieceType.KING))) {
                    kingPosition = iterPosition;
                } else if (board.getPiece(iterPosition) != null && board.getPiece(iterPosition).getTeamColor() != teamColor) {
                    var someValidMoves = board.getPiece(iterPosition).pieceMoves(board, iterPosition);
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

    public boolean copyIsInCheck(TeamColor teamColor, ChessBoard copyBoard) {
        Collection<ChessMove> otherTeamValidMoves = new ArrayList<ChessMove>();
        var kingPosition = new ChessPosition(0, 0);
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                var iterPosition = new ChessPosition(i, j);
                if (Objects.equals(copyBoard.getPiece(iterPosition), new ChessPiece(teamColor, ChessPiece.PieceType.KING))) {
                    kingPosition = iterPosition;
                } else if (copyBoard.getPiece(iterPosition) != null && copyBoard.getPiece(iterPosition).getTeamColor() != teamColor) {
                    var someValidMoves = copyBoard.getPiece(iterPosition).pieceMoves(copyBoard, iterPosition);
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
        if (this.isInCheck(teamColor)) {
            if (this.isInStalemate(teamColor)) {
                turn = null;
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        var otherTeamValidMoves = new ArrayList<ChessMove>();
        Collection<ChessMove> kingValidMoves = new ArrayList<ChessMove>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                var iterPosition = new ChessPosition(i, j);
                if (Objects.equals(board.getPiece(iterPosition), new ChessPiece(teamColor, ChessPiece.PieceType.KING))) {
                    kingValidMoves = this.validMoves(iterPosition);
                }
//                else if (board.getPiece(iterPosition) != null && board.getPiece(iterPosition).getTeamColor() != teamColor) {
//                    var someValidMoves = this.validMoves(iterPosition);
//                    otherTeamValidMoves.addAll(someValidMoves);
//                }
            }
        }
//        var kingEndPositions = this.getEndPositions(kingValidMoves);
//        var otherTeamEndPositions = this.getEndPositions(otherTeamValidMoves);
//        if (otherTeamEndPositions.containsAll(kingEndPositions)) {
        if (kingValidMoves.isEmpty()) {
            turn = null;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "board=" + board +
                ", turn=" + turn +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && turn == chessGame.turn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, turn);
    }
}


