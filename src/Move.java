import java.io.IOException;

public class Move {
    Square to;
    Square from;

    /*
        Init a move with zeroed coordinates.
     */
    Move(){
        this.from = new Square();
        this.to = new Square();
    }

    Move(int fromX, int fromY, int toX, int toY){
        this.from = new Square(fromX, fromY);
        this.to = new Square(toX, toY);
    }

    String moveToString() throws IOException{
        return this.from.squareToString() + '-' + this.to.squareToString();
    }

    void printMove() throws IOException{
        from.printSquare();
        System.out.print("-");
        to.printSquare();
        System.out.println();
        System.out.println();
    }

    /*
        Init a move by copying another move object.
     */
    Move(Move m){
        this.to = new Square(m.to);
        this.from = new Square(m.from);
    }

    char getFromPiece(char[][] board){
        return from.getPiece(board);
    }
    char getToPiece(char[][] board) { return to.getPiece(board); }

    /*
        Returns an array holding the coordinates of a move.
     */
    int[] getToFrom(){
        int[] i = new int[4];
        i[0] = from.getY();
        i[1] = from.getX();
        i[2] = to.getY();
        i[3] = to.getX();
        return i;
    }

    /*
        Check if a square belongs to a white piece.
     */
    boolean isWhiteMove(char[][] board){
        return from.isWhite(board);
    }

    /*
        Check if a square belongs to a black piece.
     */
    boolean isBlackMove(char[][] board){
        return from.isBlack(board);
    }

    /*
        Check if a move is from the board, and to the board.
     */
    boolean isOnBoard(){
        return to.isOnBoard() && from.isOnBoard();
    }
}
