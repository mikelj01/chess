package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;

import java.util.Objects;

import static ui.EscapeSequences.*;

public class PrintBoard {
    ChessBoard board;
    String color;

    public PrintBoard(ChessBoard board, String colr) {
        this.board = board;
        this.color = colr.toLowerCase();
    }

    public String print() {
        boolean toggle = true;
        ChessPiece[][] squares = board.getSquares();
        if (Objects.equals(color, "white")) {
            toggle = false;
            for (int i = 0; i < squares.length; i++) {
                for (int j = 0; j < squares[i].length / 2; j++) {
                    ChessPiece temp = squares[i][j];
                    squares[i][j] = squares[squares.length - 1 - i][squares[i].length - 1 - j];
                    squares[squares.length - 1 - i][squares[i].length - 1 - j] = temp;
                }
            }
        }

        String boardText = "";
        for (ChessPiece[] inner : squares) {
            for (ChessPiece piece : inner) {
                //boardText += SET_TEXT_COLOR_BLACK + "|";
                if (piece == null) {
                    if (toggle == false) {
                        boardText += SET_BG_COLOR_WHITE;
                    } else {
                        boardText +=SET_BG_COLOR_DARK_GREEN;
                    }
                    boardText += "   ";
                } else {
                    String pColor;
                    if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        pColor = SET_TEXT_COLOR_LIGHT_GREY;
                    } else {
                        pColor = SET_TEXT_COLOR_BLACK;
                    }
                    if (toggle == false) {
                        boardText += SET_BG_COLOR_WHITE + pColor + " " + piece.toString() + " ";
                    } else {
                        boardText +=SET_BG_COLOR_DARK_GREEN + pColor + " " + piece.toString() + " ";
                    }
                }
                toggle = !toggle;
            }
            toggle = !toggle;
            //boardText += "|";
            boardText +=SET_BG_COLOR_BLACK;
            boardText += "\n";

        }
        boardText += RESET_TEXT_COLOR + RESET_BG_COLOR;
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
