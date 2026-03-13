package movecalculators;

import chess.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;



public class KingMovesCalculator {
    protected final ChessBoard board;
    protected final ChessPosition startPosition;
    protected final ChessGame.TeamColor color;
    private final int row;
    private final int col;


    public KingMovesCalculator(ChessBoard board, ChessPosition startPosition){
        this.board = board;
        this.startPosition = startPosition;
        this.color = board.getPiece(startPosition).getTeamColor();
        this.row = startPosition.getRow();
        this.col = startPosition.getColumn();
    }

    public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new HashSet<ChessMove>();
        int row1 = row;
        int col1 = col;

        row1++;
        col1++;



        if (row1 >= 1 && row1 <= 8 && col1 >= 1 && col1 <= 8){
            ChessPosition newPos = new ChessPosition(row1, col1);
            if (board.getPiece(newPos) != null) {
                if (board.getPiece(newPos).getTeamColor() != color) {
                    ChessMove newMove = new ChessMove(startPosition, newPos, null);
                    newMove.capMove = true;
                    moves.add(newMove);
                }
            } else {
                moves.add(new ChessMove(startPosition, newPos, null));
            }
        }
        row1 = row;
        col1 = col;

        row1--;
        col1++;

        if (row1 >= 1 && row1 <= 8 && col1 >= 1 && col1 <= 8) {
            ChessPosition newPos = new ChessPosition(row1, col1);
            if (board.getPiece(newPos) != null) {
                if (board.getPiece(newPos).getTeamColor() != color) {
                    ChessMove newMove = new ChessMove(startPosition, newPos, null);
                    newMove.capMove = true;
                    moves.add(newMove);
                }
            } else {
                moves.add(new ChessMove(startPosition, newPos, null));
            }
        }

        row1 = row;
        col1 = col;

        row1--;
        col1--;

        if (row1 >= 1 && row1 <= 8 && col1 >= 1 && col1 <= 8) {
            ChessPosition newPos = new ChessPosition(row1, col1);
            if (board.getPiece(newPos) != null) {
                if (board.getPiece(newPos).getTeamColor() != color) {
                    ChessMove newMove = new ChessMove(startPosition, newPos, null);
                    newMove.capMove = true;
                    moves.add(newMove);
                }
            } else {
                moves.add(new ChessMove(startPosition, newPos, null));
            }
        }
        row1 = row;
        col1 = col;

        row1++;
        col1--;

        if (row1 >= 1 && row1 <= 8 && col1 >= 1 && col1 <= 8) {
            ChessPosition newPos = new ChessPosition(row1, col1);
            if (board.getPiece(newPos) != null) {
                if (board.getPiece(newPos).getTeamColor() != color) {
                    ChessMove newMove = new ChessMove(startPosition, newPos, null);
                    newMove.capMove = true;
                    moves.add(newMove);
                }
            } else {
                moves.add(new ChessMove(startPosition, newPos, null));
            }
        }

        row1 = row;
        col1 = col;
        row1++;


        if (row1 >= 1 && row1 <= 8 && col1 >= 1 && col1 <= 8) {
            ChessPosition newPos = new ChessPosition(row1, col1);
            if (board.getPiece(newPos) != null) {
                if (board.getPiece(newPos).getTeamColor() != color) {
                    ChessMove newMove = new ChessMove(startPosition, newPos, null);
                    newMove.capMove = true;
                    moves.add(newMove);
                }
            } else {
                moves.add(new ChessMove(startPosition, newPos, null));
            }
        }
        row1 = row;
        col1 = col;

        col1++;

        if (row1 >= 1 && row1 <= 8 && col1 >= 1 && col1 <= 8) {
            ChessPosition newPos = new ChessPosition(row1, col1);
            if (board.getPiece(newPos) != null) {
                if (board.getPiece(newPos).getTeamColor() != color) {
                    ChessMove newMove = new ChessMove(startPosition, newPos, null);
                    newMove.capMove = true;
                    moves.add(newMove);
                }
            } else {
                moves.add(new ChessMove(startPosition, newPos, null));
            }
        }

        row1 = row;
        col1 = col;

        row1--;


        if (row1 >= 1 && row1 <= 8 && col1 >= 1 && col1 <= 8) {
            ChessPosition newPos = new ChessPosition(row1, col1);
            if (board.getPiece(newPos) != null) {
                if (board.getPiece(newPos).getTeamColor() != color) {
                    ChessMove newMove = new ChessMove(startPosition, newPos, null);
                    newMove.capMove = true;
                    moves.add(newMove);
                }
            } else {
                moves.add(new ChessMove(startPosition, newPos, null));
            }
        }
        row1 = row;
        col1 = col;

        col1--;

        if (row1 >= 1 && row1 <= 8 && col1 >= 1 && col1 <= 8) {
            ChessPosition newPos = new ChessPosition(row1, col1);
            if (board.getPiece(newPos) != null) {
                if (board.getPiece(newPos).getTeamColor() != color) {
                    ChessMove newMove = new ChessMove(startPosition, newPos, null);
                    newMove.capMove = true;
                    moves.add(newMove);
                }
            } else {
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
        KingMovesCalculator that = (KingMovesCalculator) o;
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
