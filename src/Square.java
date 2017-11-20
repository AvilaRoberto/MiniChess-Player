import java.io.IOException;

public class Square {
    private int x;  //(0-4)(col) x position on board.
    private int y;  //(0-5)(row) y position on board.

    /*
        Init a square at coordinate 0, 0.
     */
    Square(){
        this.x = 0;
        this.y = 0;
    }

    Square(int x, int y){
        this.x = x;
        this.y = y;
    }

    /*
        Init a square at a specific coordinate.
     */
    Square(Square s){
        this.x = s.x;
        this.y = s.x;
    }

    String squareToString() throws IOException{

        char first = 'x';
        char second = 'x';

        if(x == 0){
            first = 'a';
        }
        else if(x == 1){
            first = 'b';
        }
        else if(x == 2){
            first = 'c';
        }
        else if(x == 3){
            first = 'd';
        }
        else if(x == 4){
            first = 'e';
        }
        else{
            throw new IOException("first coord not found: = x");
        }


        if(y == 0){
            second = '6';
        }
        else if(y == 1){
            second = '5';
        }
        else if(y == 2){
            second = '4';
        }
        else if(y == 3){
            second = '3';
        }
        else if(y == 4){
            second = '2';
        }
        else if(y == 5){
            second = '1';
        }
        else{
            throw new IOException("second coord not found: = x");
        }

        return String.valueOf(first) + String.valueOf(second);
    }

    void printSquare() throws IOException{
        System.out.print(this.squareToString());

    }

    /*
        @return the character at y, x.
     */
    char getPiece(char[][] board){
        return board[y][x];
    }

    /*
        Check if coordinates belong to a white piece.
        @return true if so.
     */
    boolean isBlack(char[][] board){
        if(board[y][x] == 'k'
                || board[y][x] == 'q'
                || board[y][x] == 'b'
                || board[y][x] == 'n'
                || board[y][x] == 'r'
                || board [y][x] == 'p'){
            return true;
        }
        return false;
    }

    /*
        Check if coordinates belong to a black piece.
        @return true if so.
     */
    boolean isWhite(char[][] board){
        if(board[y][x] == 'K'
                || board[y][x] == 'Q'
                || board[y][x] == 'B'
                || board[y][x] == 'N'
                || board[y][x] == 'R'
                || board [y][x] == 'P'){
            return true;
        }
        return false;
    }

    boolean isOpponentPiece(int onMove, char c, char[][] brd){
        if(onMove == 1){
            return this.isBlack(brd);
        }
        else{
            return this.isWhite(brd);
        }
    }

    /*
        Check if coordinates are on the game board.
        @return true if so.
     */
    boolean isOnBoard(){
        int numberOfCols = 5;
        int numberOfRows = 6;

        if(x >= 0 && x < numberOfCols){
            if(y >= 0 && y < numberOfRows){
                return true;
            }
        }
        return false;
    }



    int getX(){
        return this.x;
    }
     int getY(){
        return this.y;
     }
}
