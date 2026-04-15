package movecalculators;

import chess.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;



public class KnightMovesCalculator {
    protected final ChessBoard board;
    protected final ChessPosition startPosition;
    protected final ChessGame.TeamColor color;
    private final int row;
    private final int col;
    private final Collection<ChessMove> moves;


    public KnightMovesCalculator(ChessBoard board, ChessPosition startPosition){
        this.board = board;
        this.startPosition = startPosition;
        this.color = board.getPiece(startPosition).getTeamColor();
        this.row = startPosition.getRow();
        this.col = startPosition.getColumn();
        this.moves = new HashSet<ChessMove>();
    }

    public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition position) {
        int row1 = row;
        int col1 = col;

        row1 += 2;
        col1 ++;

        if(row1 < 9 && row1 > 0 && col1 < 9 && col1 > 0){
            ChessPosition newPos = new ChessPosition(row1, col1);
            addMove(newPos);
        }
        int row2 = row;
        int col2 = col;

        row2 += 2;
        col2 --;

        if(row2 < 9 && row2 > 0 && col2 < 9 && col2 > 0){
            ChessPosition newPos = new ChessPosition(row2, col2);
            addMove(newPos);
        }

        int row3 = row;
        int col3 = col;

        row3 -= 2;
        col3 --;

        if(row3 < 9 && row3 > 0 && col3 < 9 && col3 > 0){
            ChessPosition newPos = new ChessPosition(row3, col3);
            addMove(newPos);
        }
        int row4 = row;
        int col4 = col;

        row4 -= 2;
        col4 ++;

        if(row4 < 9 && row4 > 0 && col4 < 9 && col4 > 0){
            ChessPosition newPos = new ChessPosition(row4, col4);
            addMove(newPos);
        }

        int row5 = row;
        int col5 = col;

        col5 += 2;
        row5 --;

        if(row5 < 9 && row5 > 0 && col5 < 9 && col5 > 0){
            ChessPosition newPos = new ChessPosition(row5, col5);
            addMove(newPos);
        }
        int row6 = row;
        int col6 = col;

        col6 -= 2;
        row6 --;

        if(row6 < 9 && row6 > 0 && col6 < 9 && col6 > 0){
            ChessPosition newPos = new ChessPosition(row6, col6);
            addMove(newPos);
        }
        int row7 = row;
        int col7 = col;

        col7 += 2;
        row7 ++;

        if(row7 < 9 && row7 > 0 && col7 < 9 && col7 > 0){
            ChessPosition newPos = new ChessPosition(row7, col7);
            addMove(newPos);
        }

        int row8 = row;
        int col8 = col;

        col8 -= 2;
        row8 ++;

        if(row8 < 9 && row8 > 0 && col8 < 9 && col8 > 0){
            ChessPosition newPos = new ChessPosition(row8, col8);
            addMove(newPos);
        }
        return moves;
    }

    public void addMove(ChessPosition newPos){
        if (board.getPiece(newPos) == null) {
            moves.add(new ChessMove(startPosition, newPos, null));
        }
        else{
            if (board.getPiece(newPos).getTeamColor() != color) {
                ChessMove newMove = new ChessMove(startPosition, newPos, null);
                newMove.capMove = true;
                moves.add(newMove);

            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KnightMovesCalculator that = (KnightMovesCalculator) o;
        if(row == that.row && col == that.col && Objects.equals(board, that.board)) {
            return Objects.equals(startPosition, that.startPosition) && color == that.color;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, startPosition, color, row, col);
    }

}