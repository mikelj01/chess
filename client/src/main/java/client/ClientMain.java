package client;

import chess.*;
import server.ServerFacade;
import ui.UI;
import server.Server;



public class ClientMain {
    private static Server server;
    static ServerFacade facade;

    public static void main(String[] args) {

        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        server = new Server();
        var port = server.run(8000);
        facade = new ServerFacade("http://localhost:8000");
        UI uI = new UI(facade);
        uI.run();
        server.stop();
    }
}
