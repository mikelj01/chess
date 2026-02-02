package MoveCalculators;

import chess.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;



public class PawnMovesCalculator {
    protected final ChessBoard board;
    protected final ChessPosition startPosition;
    protected final ChessGame.TeamColor Color;
    private final int row;
    private final int col;
    private final boolean moved;



    public PawnMovesCalculator(ChessBoard board, ChessPosition startPosition){
        this.board = board;
        this.startPosition = startPosition;
        this.Color = board.getPiece(startPosition).getTeamColor();
        this.row = startPosition.getRow();
        this.col = startPosition.getColumn();
        if (Color == ChessGame.TeamColor.WHITE){
            this.moved = row != 2;
        }
        else if (Color == ChessGame.TeamColor.BLACK){
            this.moved = row != 7;
        }
        else{
            this.moved = true;
        }
    }

    public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new HashSet<ChessMove>();
        int row1 = row;
        int col1 = col;

        /* Initial Move Logic*/
        if(!moved){
            if(Color == ChessGame.TeamColor.WHITE) {
                row1++;
                ChessPosition newPos1 = new ChessPosition(row1, col1);
                ChessPosition newPos = new ChessPosition(row1, col1);
                if(board.getPiece(newPos1)  == null){
                    moves.add(new ChessMove(startPosition, newPos, null));
                }
                row1++;
                newPos = new ChessPosition(row1, col1);
                if(board.getPiece(newPos)  == null && board.getPiece(newPos1)  == null){
                    moves.add(new ChessMove(startPosition, newPos, null));
                }

            }
            else{
                row1--;
                ChessPosition newPos1 = new ChessPosition(row1, col1);
                ChessPosition newPos = new ChessPosition(row1, col1);
                if(board.getPiece(newPos1)  == null){
                    moves.add(new ChessMove(startPosition, newPos, null));
                }
                row1--;
                newPos = new ChessPosition(row1, col1);
                if(board.getPiece(newPos)  == null && board.getPiece(newPos1)  == null){
                    moves.add(new ChessMove(startPosition, newPos, null));
                }
            }
        }
        row1 = row;
        col1 = col;
        /*Normal Move Logic*/
        if(row1 < 7 && row1 > 2){
            if(Color == ChessGame.TeamColor.WHITE && board.getPiece(new ChessPosition(row1 + 1, col1)) == null){
                row1++;
                ChessPosition newPos = new ChessPosition(row1, col1);
                moves.add(new ChessMove(startPosition, newPos, null));


            }
            else if (Color == ChessGame.TeamColor.BLACK && board.getPiece(new ChessPosition(row1 - 1, col1)) == null) {
                row1--;
                ChessPosition newPos = new ChessPosition(row1, col1);
                moves.add(new ChessMove(startPosition, newPos, null));
            }

        }
        row1 = row;
        col1 = col;

        /*Capture Logic*/
        if(Color == ChessGame.TeamColor.WHITE && row1 < 7 || Color == ChessGame.TeamColor.BLACK && row1 > 2){
            if(Color == ChessGame.TeamColor.WHITE){
                row1++;
                if(board.getPiece(new ChessPosition(row1, col1 + 1)) != null && board.getPiece(new ChessPosition(row1, col1 + 1)).getTeamColor() == ChessGame.TeamColor.BLACK){
                    col1++;
                    ChessPosition newPos = new ChessPosition(row1, col1);
                    ChessMove newMove = new ChessMove(startPosition, newPos, null);
                    newMove.capMove = true;
                    moves.add(newMove);
                    col1 = col;
                }
                if(board.getPiece(new ChessPosition(row1, col1 - 1)) != null && board.getPiece(new ChessPosition(row1, col1 - 1)).getTeamColor() == ChessGame.TeamColor.BLACK){
                    col1--;
                    ChessPosition newPos = new ChessPosition(row1, col1);
                    ChessMove newMove = new ChessMove(startPosition, newPos, null);
                    newMove.capMove = true;
                    moves.add(newMove);

                }


            }
            else if (Color == ChessGame.TeamColor.BLACK ) {
                row1--;
                if(board.getPiece(new ChessPosition(row1, col1 + 1)) != null && board.getPiece(new ChessPosition(row1, col1 + 1)).getTeamColor() == ChessGame.TeamColor.WHITE){
                    col1++;
                    ChessPosition newPos = new ChessPosition(row1, col1);
                    ChessMove newMove = new ChessMove(startPosition, newPos, null);
                    newMove.capMove = true;
                    moves.add(newMove);
                    col1 = col;
                }
                if(board.getPiece(new ChessPosition(row1, col1 - 1)) != null && board.getPiece(new ChessPosition(row1, col1 - 1)).getTeamColor() == ChessGame.TeamColor.WHITE){
                    col1--;
                    ChessPosition newPos = new ChessPosition(row1, col1);
                    ChessMove newMove = new ChessMove(startPosition, newPos, null);
                    newMove.capMove = true;
                    moves.add(newMove);

                }
            }

        }

        row1 = row;
        col1 = col;

        /*Promotion Logic*/
        if(row1 == 7 && Color == ChessGame.TeamColor.WHITE || Color == ChessGame.TeamColor.BLACK &&  row1 == 2 ){
            int counter = 1;

            while(counter < 5) {
                ChessPiece.PieceType pp = null;
                if(counter == 1){
                    pp = ChessPiece.PieceType.BISHOP;
                }
                if(counter == 2){
                    pp = ChessPiece.PieceType.KNIGHT;
                }
                if(counter == 3){
                    pp = ChessPiece.PieceType.ROOK;
                }
                if(counter == 4){
                    pp = ChessPiece.PieceType.QUEEN;
                }

                if (Color == ChessGame.TeamColor.WHITE && board.getPiece(new ChessPosition(row1 + 1, col1)) == null) {
                    row1++;
                    ChessPosition newPos = new ChessPosition(row1, col1);
                    moves.add(new ChessMove(startPosition, newPos, pp));
                }
                else if (Color == ChessGame.TeamColor.BLACK && board.getPiece(new ChessPosition(row1 - 1, col1)) == null) {
                    row1--;
                    ChessPosition newPos = new ChessPosition(row1, col1);
                    moves.add(new ChessMove(startPosition, newPos, pp));
                }
                row1 = row;

                if(Color == ChessGame.TeamColor.WHITE){
                    row1++;
                    if(board.getPiece(new ChessPosition(row1, col1 + 1)) != null && board.getPiece(new ChessPosition(row1, col1 + 1)).getTeamColor() == ChessGame.TeamColor.BLACK){
                        col1++;
                        ChessPosition newPos = new ChessPosition(row1, col1);
                        ChessMove newMove = new ChessMove(startPosition, newPos, pp);
                        newMove.capMove = true;
                        moves.add(newMove);
                        col1 = col;
                    }
                    if(board.getPiece(new ChessPosition(row1, col1 - 1)) != null && board.getPiece(new ChessPosition(row1, col1 - 1)).getTeamColor() == ChessGame.TeamColor.BLACK){
                        col1--;
                        ChessPosition newPos = new ChessPosition(row1, col1);
                        ChessMove newMove = new ChessMove(startPosition, newPos, pp);
                        newMove.capMove = true;
                        moves.add(newMove);

                    }


                }
                else if (Color == ChessGame.TeamColor.BLACK ) {
                    row1--;
                    if(board.getPiece(new ChessPosition(row1, col1 + 1)) != null && board.getPiece(new ChessPosition(row1, col1 + 1)).getTeamColor() == ChessGame.TeamColor.WHITE){
                        col1++;
                        ChessPosition newPos = new ChessPosition(row1, col1);
                        ChessMove newMove = new ChessMove(startPosition, newPos, pp);
                        newMove.capMove = true;
                        moves.add(newMove);
                        col1 = col;
                    }
                    if(board.getPiece(new ChessPosition(row1, col1 - 1)) != null && board.getPiece(new ChessPosition(row1, col1 - 1)).getTeamColor() == ChessGame.TeamColor.WHITE){
                        col1--;
                        ChessPosition newPos = new ChessPosition(row1, col1);
                        ChessMove newMove = new ChessMove(startPosition, newPos, pp);
                        newMove.capMove = true;
                        moves.add(newMove);

                    }
                }
                row1 = row;
                col1 = col;
                counter ++;
            }
        }
        return moves;
    }
}