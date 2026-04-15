package client;

import chess.*;
import server.ServerFacade;
import ui.UI;




public class ClientMain {
    static ServerFacade facade;

    public static void main(String[] args) {
        facade = new ServerFacade("http://localhost:8080");
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        UI uI = new UI(facade);
        uI.run();
    }
}
