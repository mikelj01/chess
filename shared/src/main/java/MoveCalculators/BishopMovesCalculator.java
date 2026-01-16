package MoveCalculators;

import chess.*;

import java.util.Collection;
import java.util.HashSet;

public class BishopMovesCalculator {
    protected final ChessBoard board;
    protected final ChessPosition startPosition;
    protected final ChessGame.TeamColor Color;
    private final int row;
    private final int col;
    public BishopMovesCalculator(ChessBoard board, ChessPosition startPosition){
        this.board = board;
        this.startPosition = startPosition;
        this.Color = board.getPiece(startPosition).getTeamColor();
        this.row = startPosition.getRow();
        this.col = startPosition.getColumn();
    }
    public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition position){
        Collection<ChessMove> moves = new HashSet<ChessMove>();
        int row1 = row;
        int col1 = col;
        while (row1 < 8 && col1 < 8) {
            row1++;
            col1++;
        moves.add(new ChessMove(startPosition, new ChessPosition(row1, col1), null));
        }
        while (row1 > 1 && col1 < 8) {
            row1--;
            col1++;
            moves.add(new ChessMove(startPosition, new ChessPosition(row1, col1), null));
        }
        while (row1 > 1 && col1 > 1) {
            row1--;
            col1--;
            moves.add(new ChessMove(startPosition, new ChessPosition(row1, col1), null));
        }
        while (row1 < 0 && col1 > 1) {
            row1++;
            col1--;
            moves.add(new ChessMove(startPosition, new ChessPosition(row1, col1), null));
        }
        return moves;
    }

}
