package chess;
import MoveCalculators.BishopMovesCalculator;

import java.util.Collection;

public class PieceMovesCalculator {
        protected final ChessBoard board;
        protected final ChessPosition position;
        protected final ChessPiece.PieceType pieceTYPE;
    public PieceMovesCalculator(ChessBoard board, ChessPosition position){
        this.board = board;
        this.position = position;
        ChessPiece piece = board.getPiece(position);
        ChessPiece.PieceType type = piece.getPieceType();
        this.pieceTYPE = type;

    }
    /**-------------ADD A RETURN VALUE TO THIS
     * _________________________________
     * _____________________________*/
    public Collection<ChessMove> getMoves(){
        if(pieceTYPE.equals(ChessPiece.PieceType.BISHOP)){
            BishopMovesCalculator moveCalc = new BishopMovesCalculator(board, position);
            Collection<ChessMove> allMoves = moveCalc.getMoves(board, position);
            return allMoves;
        }
        throw new RuntimeException("Ya dun Goofed see (PieceMovesCalculator)");
    }
}
