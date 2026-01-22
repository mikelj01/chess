package chess;
import MoveCalculators.*;

import java.util.Collection;
import java.util.Objects;

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
        } else if(pieceTYPE.equals(ChessPiece.PieceType.KING)){
            KingMovesCalculator moveCalc = new KingMovesCalculator(board, position);
            Collection<ChessMove> allMoves = moveCalc.getMoves(board, position);
            return allMoves;

        }
        else if(pieceTYPE.equals(ChessPiece.PieceType.KNIGHT)){
            KnightMovesCalculator moveCalc = new KnightMovesCalculator(board, position);
            Collection<ChessMove> allMoves = moveCalc.getMoves(board, position);
            return allMoves;

        }
        else if(pieceTYPE.equals(ChessPiece.PieceType.PAWN)){
            PawnMovesCalculator moveCalc = new PawnMovesCalculator(board, position);
            Collection<ChessMove> allMoves = moveCalc.getMoves(board, position);
            return allMoves;

        }
        else if(pieceTYPE.equals(ChessPiece.PieceType.QUEEN)){
            QueenMovesCalculator moveCalc = new QueenMovesCalculator(board, position);
            Collection<ChessMove> allMoves = moveCalc.getMoves(board, position);
            return allMoves;

        }
        else if(pieceTYPE.equals(ChessPiece.PieceType.ROOK)){
            RookMovesCalculator moveCalc = new RookMovesCalculator(board, position);
            Collection<ChessMove> allMoves = moveCalc.getMoves(board, position);
            return allMoves;

        }
        throw new RuntimeException("Ya dun Goofed see (PieceMovesCalculator)");
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PieceMovesCalculator that = (PieceMovesCalculator) o;
        return Objects.equals(board, that.board) && Objects.equals(position, that.position) && pieceTYPE == that.pieceTYPE;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, position, pieceTYPE);
    }
}
