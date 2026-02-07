package chess;

import java.util.Collection;
import java.util.List;
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
        this.type = type;
        this.pieceColor = pieceColor;
    }

    /**
     * Creates a clone of a Chess Piece**/
    public ChessPiece(ChessPiece old) {
        this.pieceColor = old.pieceColor;
        this.type = old.type;
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
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        /** Getting the piece at the location in question  */
        PieceMovesCalculator moveSet = new PieceMovesCalculator(board, myPosition);
        return moveSet.getMoves();

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        if(pieceColor == ChessGame.TeamColor.WHITE) {
            if (type == PieceType.KING) {
                return "K";
            }
            if (type == PieceType.QUEEN) {
                return "Q";
            }
            if (type == PieceType.BISHOP) {
                return "B";
            }
            if (type == PieceType.KNIGHT) {
                return "N";
            }
            if (type == PieceType.ROOK) {
                return "R";
            }
            if (type == PieceType.PAWN) {
                return "P";
            } else {
                return "Ya dun messed up.  This piece ain't got no type.";
            }
        }
        if(pieceColor == ChessGame.TeamColor.BLACK) {
            if (type == PieceType.KING) {
                return "k";
            }
            if (type == PieceType.QUEEN) {
                return "q";
            }
            if (type == PieceType.BISHOP) {
                return "b";
            }
            if (type == PieceType.KNIGHT) {
                return "n";
            }
            if (type == PieceType.ROOK) {
                return "r";
            }
            if (type == PieceType.PAWN) {
                return "p";
            } else {
                return "Ya dun messed up.  This piece ain't got no type.";
            }
        }
        else {
            return "Ya dun messed up.  This piece ain't got no Color.";
        }
    }
}
