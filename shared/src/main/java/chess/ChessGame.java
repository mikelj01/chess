package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor tTurn;
    ChessBoard board;
    boolean inCheckW;
    boolean inCheckB;

    public ChessGame() {
        this.tTurn = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.inCheckW = false;
        this.inCheckB = false;


    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return tTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        tTurn = team;
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
        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        if(board.getPiece(startPosition)== null) {
            return null;
        }
        //For each move, make a Phantom move and check if it causes problems.  If it does, remove it.
            for (ChessMove move : moves){
                ChessBoard newBoard = board;
                ChessPosition start = move.getStartPosition();
                ChessPosition end = move.getEndPosition();
                newBoard.removePiece(start);
                newBoard.addPiece(end, piece);
                if (checkChecker(newBoard)){
                    moves.remove(move);
                }
            }
            return moves;
    }

    private boolean checkChecker(ChessBoard newBoard) {
        //Find the King
        ChessPosition kingPos;
        if (tTurn == TeamColor.WHITE ) {
            kingPos = newBoard.WKPos;
        }
        else if (tTurn == TeamColor.BLACK ) {
            kingPos = newBoard.BKPos;
        }
        else{
            KingFinder finder = new KingFinder(board, tTurn);
            kingPos = finder.findIt();
        }
        if(kingPos == null){
            throw new RuntimeException("You Lost Your King");
        }
        ChessPiece pp = null;
        int counter = 1;
        while(counter < 6) {
            if (counter == 1) {
                pp = new ChessPiece(tTurn, ChessPiece.PieceType.BISHOP);
            }
            if (counter == 2) {
                pp = new ChessPiece(tTurn, ChessPiece.PieceType.KNIGHT);
            }
            if (counter == 3) {
                pp = new ChessPiece(tTurn, ChessPiece.PieceType.ROOK);
            }
            if (counter == 4) {
                pp = new ChessPiece(tTurn, ChessPiece.PieceType.QUEEN);
            }
            if (counter == 5) {
                pp = new ChessPiece(tTurn, ChessPiece.PieceType.PAWN);
            }
            newBoard.addPiece(kingPos, pp);
            PieceMovesCalculator sChecker = new PieceMovesCalculator(newBoard, kingPos);
            Collection<ChessMove> spots = sChecker.getMoves();
            for(ChessMove spot : spots){
                if(newBoard.getPiece(spot.getEndPosition())!=null){
                    ChessPiece.PieceType type = newBoard.getPiece(spot.getEndPosition()).getPieceType();
                    if(type == pp.getPieceType() && spot.capMove){
                        return true;
                    }
                }

            }
        counter++;
        }

        return false;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        try {
            ChessPosition start = move.getStartPosition();
            ChessPosition end = move.getEndPosition();
            ChessPiece.PieceType pp = move.getPromotionPiece();
            ChessPiece piece = board.getPiece(start);
            if(piece == null){
                throw new InvalidMoveException("No Piece Selected");
            }
            if(piece.getTeamColor() != tTurn){
                throw new InvalidMoveException("Not Your Turn");
            }


            //Checking valid moves
            Collection<ChessMove> cheatChecker = validMoves(start);

            if (pp != null && cheatChecker.contains(move)) {
                piece = new ChessPiece(piece.getTeamColor(), pp);
            } else if (pp == null && cheatChecker.contains(move)) {
                piece = new ChessPiece(piece.getTeamColor(), piece.getPieceType());
            } else{
                throw new InvalidMoveException("Invalid Move");
            }

            if(piece != null) {
                board.removePiece(start);
                board.addPiece(end, piece);

                //updating king position variable
                if(piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == TeamColor.WHITE){
                    board.WKPos = end;
                }
                if(piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == TeamColor.BLACK){
                    board.BKPos = end;
                }

                //Setting Turn
                if(piece.getTeamColor() == TeamColor.BLACK){
                setTeamTurn(TeamColor.WHITE);
                } else if (piece.getTeamColor() == TeamColor.WHITE){
                    setTeamTurn(TeamColor.BLACK);
                }
            }
            else{
                throw new InvalidMoveException("Invalid Move");
            }

        } catch(InvalidMoveException cheat){
            System.out.print(cheat);
            throw new InvalidMoveException("Invalid Move");

        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(tTurn == teamColor && !isInCheck(teamColor)){
        throw new RuntimeException("Not implemented");
        }
        return false;
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
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return tTurn == chessGame.tTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tTurn, board);
    }
}
