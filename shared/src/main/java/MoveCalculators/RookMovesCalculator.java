package MoveCalculators;

import chess.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;



public class RookMovesCalculator {
    protected final ChessBoard board;
    protected final ChessPosition startPosition;
    protected final ChessGame.TeamColor Color;
    private final int row;
    private final int col;


    public RookMovesCalculator(ChessBoard board, ChessPosition startPosition){
        this.board = board;
        this.startPosition = startPosition;
        this.Color = board.getPiece(startPosition).getTeamColor();
        this.row = startPosition.getRow();
        this.col = startPosition.getColumn();
    }

    public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new HashSet<ChessMove>();
        int row1 = row;
        int col1 = col;
        while (row1 < 8) {
            row1++;
            ChessPosition newPos = new ChessPosition(row1, col1);
            if (board.getPiece(newPos) != null) {
                if (board.getPiece(newPos).getTeamColor() != Color) {
                    moves.add(new ChessMove(startPosition, newPos, null));
                    row1 = 8;

                }
                else {
                    row1 = 8;

                }
            }

            else{
                moves.add(new ChessMove(startPosition, newPos, null));
            }
        }
        row1 = row;

        while (row1 > 1) {
            row1--;
            ChessPosition newPos = new ChessPosition(row1, col1);
            if (board.getPiece(newPos) != null) {
                if (board.getPiece(newPos).getTeamColor() != Color) {
                    moves.add(new ChessMove(startPosition, newPos, null));
                    row1 = 1;

                }
                else {
                    row1 = 1;

                }
            }

            else{
                moves.add(new ChessMove(startPosition, newPos, null));
            }
        }
        row1 = row;
        while (col1 > 1) {
            col1--;
            ChessPosition newPos = new ChessPosition(row1, col1);
            if (board.getPiece(newPos) != null) {
                if (board.getPiece(newPos).getTeamColor() != Color) {
                    moves.add(new ChessMove(startPosition, newPos, null));
                    col1 = 1;

                }
                else {
                    col1 = 1;

                }
            }

            else{
                moves.add(new ChessMove(startPosition, newPos, null));
            }
        }
        col1 = col;
        while (col1 < 8) {
            col1++;
            ChessPosition newPos = new ChessPosition(row1, col1);
            if (board.getPiece(newPos) != null) {
                if (board.getPiece(newPos).getTeamColor() != Color) {
                    moves.add(new ChessMove(startPosition, newPos, null));
                    col1 = 8;

                }
                else {
                    col1 = 8;

                }
            }

            else{
                moves.add(new ChessMove(startPosition, newPos, null));
            }
        }
        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RookMovesCalculator that = (RookMovesCalculator) o;
        return row == that.row && col == that.col && Objects.equals(board, that.board) && Objects.equals(startPosition, that.startPosition) && Color == that.Color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, startPosition, Color, row, col);
    }
}