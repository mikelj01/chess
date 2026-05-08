package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;

import java.util.Objects;

import static ui.EscapeSequences.*;

public class PrintBoard {
    ChessBoard board;
    String color;
    int rowNum;

    public PrintBoard(ChessBoard board, String colr) {
        this.board = board;
        this.color = colr.toLowerCase();
    }

    public String print() {
        boolean toggle = false;
        ChessPiece[][] squares = board.getSquares();
        if (Objects.equals(color, "black")) {
            for (int i = 0; i < squares.length ; i++) {
                for (int j = 0; j < squares[i].length / 2; j++) {
                    ChessPiece temp = squares[i][j];
                    squares[i][j] = squares[squares.length - 1 - i][squares[i].length - 1 - j];
                    squares[squares.length - 1 - i][squares[i].length - 1 - j] = temp;
                }
            }
        }

        if(Objects.equals(color, "black")){
            rowNum = 1;
        }else{
            rowNum = 8;
        }

        String boardText = "";
        for (int i = squares.length - 1; i >= 0; i--) {

            boardText += RESET_TEXT_COLOR;
            boardText += rowNum + " ";
            if(Objects.equals(color, "black")){
                rowNum ++;
            }else {
                rowNum--;
            }

            for (ChessPiece piece : squares[i]) {
                if (piece == null) {
                    if (!toggle) {
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
        boardText += RESET_TEXT_COLOR;
        if(Objects.equals(color, "black")){
            boardText += "   8  7  6  5  4  3  2  1  \n";
        }else{
            boardText += "   1  2  3  4  5  6  7  8  \n";
        }
        boardText += RESET_BG_COLOR;

        return boardText;
    }
}
