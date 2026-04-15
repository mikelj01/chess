package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;

import static ui.EscapeSequences.*;

public class PrintBoard {
    ChessBoard board;
    String color;

    public PrintBoard(ChessBoard board, String colr) {
        this.board = board;
        this.color = colr.toLowerCase();
    }

    public String print() {
        boolean toggle = false;
        if (color == "black") {
            toggle = true;
        }

        ChessPiece[][] squares = board.getSquares();
        String boardText = "";
        for (ChessPiece[] inner : squares) {
            for (ChessPiece piece : inner) {
                //boardText += SET_TEXT_COLOR_BLACK + "|";
                if (piece == null) {
                    if (toggle == false) {
                        boardText += SET_BG_COLOR_WHITE;
                    } else {
                        boardText +=SET_BG_COLOR_RED;
                    }
                    boardText += " ";
                } else {
                    String pColor;
                    if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        pColor = SET_TEXT_COLOR_GREEN;
                    } else {
                        pColor = SET_TEXT_COLOR_BLUE;
                    }
                    if (toggle == false) {
                        boardText += SET_BG_COLOR_WHITE + pColor + piece.toString();
                    } else {
                        boardText +=SET_BG_COLOR_RED + pColor + piece.toString();
                    }
                }
                toggle = !toggle;
            }
            toggle = !toggle;
            //boardText += "|";
            boardText +=SET_BG_COLOR_BLACK;
            boardText += "\n";

        }
        return boardText;


//
//
//        for (ChessPiece[] inner : board.getSquares()) {
//            for (ChessPiece piece : inner) {
//
//
//
//            }
//            System.out.print("\n");
//
//        }
//    }
    }
}
