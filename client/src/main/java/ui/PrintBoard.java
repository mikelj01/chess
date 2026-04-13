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

    public void print() {
        boolean toggle = false;
        if (color == "black") {
            toggle = true;
        }
        if (color != "white" | color != "black") {
            System.out.print("Incorrect color");
        } else {
            for (ChessPiece[] inner : board.getSquares()) {
                for (ChessPiece piece : inner) {
                    String pColor;
                    if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        pColor = SET_TEXT_COLOR_GREEN;
                    } else {
                        pColor = SET_TEXT_COLOR_BLACK;
                    }
                    if (toggle == false) {
                        System.out.print(SET_BG_COLOR_WHITE + pColor + piece);
                    } else {
                        System.out.print(SET_BG_COLOR_RED + pColor + piece);
                    }
                    toggle = !toggle;
                }

            }
        }
    }
}
