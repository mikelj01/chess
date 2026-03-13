package movecalculators;

import chess.*;

import java.util.Collection;
import java.util.HashSet;


public class PawnMovesCalculator {
    protected final ChessBoard board;
    protected final ChessPosition startPosition;
    protected final ChessGame.TeamColor color;
    private final int row;
    private final int col;
    private final boolean moved;



    public PawnMovesCalculator(ChessBoard board, ChessPosition startPosition){
        this.board = board;
        this.startPosition = startPosition;
        this.color = board.getPiece(startPosition).getTeamColor();
        this.row = startPosition.getRow();
        this.col = startPosition.getColumn();
        if (color == ChessGame.TeamColor.WHITE){
            this.moved = row != 2;
        }
        else if (color == ChessGame.TeamColor.BLACK){
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
            if(color == ChessGame.TeamColor.WHITE) {
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
            if(color == ChessGame.TeamColor.WHITE && board.getPiece(new ChessPosition(row1 + 1, col1)) == null){
                row1++;
                ChessPosition newPos = new ChessPosition(row1, col1);
                moves.add(new ChessMove(startPosition, newPos, null));


            }
            else if (color == ChessGame.TeamColor.BLACK && board.getPiece(new ChessPosition(row1 - 1, col1)) == null) {
                row1--;
                ChessPosition newPos = new ChessPosition(row1, col1);
                moves.add(new ChessMove(startPosition, newPos, null));
            }

        }
        row1 = row;
        col1 = col;

        /*Capture Logic*/
        if(color == ChessGame.TeamColor.WHITE && row1 < 7 || color == ChessGame.TeamColor.BLACK && row1 > 2){
            if(color == ChessGame.TeamColor.WHITE){
                row1++;
                ChessPiece piece1 = board.getPiece(new ChessPosition(row1, col1 + 1));
                if(piece1 != null && piece1.getTeamColor() == ChessGame.TeamColor.BLACK){
                    col1++;
                    ChessPosition newPos = new ChessPosition(row1, col1);
                    ChessMove newMove = new ChessMove(startPosition, newPos, null);
                    newMove.capMove = true;
                    moves.add(newMove);
                    col1 = col;
                }
                if(piece1 != null && piece1.getTeamColor() == ChessGame.TeamColor.BLACK){
                    col1--;
                    ChessPosition newPos = new ChessPosition(row1, col1);
                    ChessMove newMove = new ChessMove(startPosition, newPos, null);
                    newMove.capMove = true;
                    moves.add(newMove);

                }


            }
            else if (color == ChessGame.TeamColor.BLACK ) {
                row1--;
                ChessPiece piece1 = board.getPiece(new ChessPosition(row1, col1 + 1));
                if( piece1 != null && piece1.getTeamColor() == ChessGame.TeamColor.WHITE){
                    col1++;
                    ChessPosition newPos = new ChessPosition(row1, col1);
                    ChessMove newMove = new ChessMove(startPosition, newPos, null);
                    newMove.capMove = true;
                    moves.add(newMove);
                    col1 = col;
                }
                ChessPiece piece2 = board.getPiece(new ChessPosition(row1, col1 - 1));
                if (piece2!= null && piece2.getTeamColor() == ChessGame.TeamColor.WHITE){
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
        if(row1 == 7 && color == ChessGame.TeamColor.WHITE || color == ChessGame.TeamColor.BLACK &&  row1 == 2 ){
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

                if (color == ChessGame.TeamColor.WHITE && board.getPiece(new ChessPosition(row1 + 1, col1)) == null) {
                    row1++;
                    ChessPosition newPos = new ChessPosition(row1, col1);
                    moves.add(new ChessMove(startPosition, newPos, pp));
                }
                else if (color == ChessGame.TeamColor.BLACK && board.getPiece(new ChessPosition(row1 - 1, col1)) == null) {
                    row1--;
                    ChessPosition newPos = new ChessPosition(row1, col1);
                    moves.add(new ChessMove(startPosition, newPos, pp));
                }
                row1 = row;

                if(color == ChessGame.TeamColor.WHITE){
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
                else if (color == ChessGame.TeamColor.BLACK ) {
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