package chess;

public class KingFinder {
    ChessBoard board;
    ChessGame.TeamColor color;
    public KingFinder(ChessBoard board, ChessGame.TeamColor color) {
        this.board = board;
        this.color = color;

    }
    ChessPosition findIt(){
        int row = 1;
        int col = 1;
        ChessPosition pos = null;
        while(row < 9){
            col = 1;
            while(col < 9){
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if(piece == null){
                    col++;
                    col--;
                }
                else if(piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == color){
                    return  position;
                }
                col++;
            }
            row++;
        }
        return pos;
    }
}
