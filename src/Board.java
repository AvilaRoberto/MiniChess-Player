import java.io.IOException;
import java.util.Random;
import java.util.Vector;

public class Board {
    private char[][] board;
    private int rows;
    private int cols;
    private int onMove;
    private int moveNumber;
    private int maxMoves;
    private Vector<Move> lastMove;
    private boolean original;

    //Inits a board with pieces in their initial positions.
    Board(){
        this.rows = 6;
        this.cols = 5;
        this.onMove = 1;
        this.moveNumber = 0;
        this.maxMoves = 40;
        this.lastMove = new Vector<>();
        this.original = true;
        initPieces();
    }

    //Inits a board in the same position as the board passed in.
    Board(Board b){
        this.rows = b.rows;
        this.cols = b.cols;
        this.onMove = b.onMove;
        this.moveNumber = b.moveNumber;
        this.maxMoves = 40;
        this.lastMove = new Vector<>(b.lastMove);
        this.board = new char[this.rows][this.cols];
        this.original = false;

        for(int r = 0; r < rows; ++r){
            for(int c = 0; c < cols; ++c){
                this.board[r][c] = b.board[r][c];
            }
        }
    }

    /*
        Copies the state of board b, with on move player om, on move number mn.
     */
    Board(char[][] b, int om, int mn){
        this.onMove = om;
        this.moveNumber = mn;
        this.rows = 6;
        this.cols = 5;
        this.maxMoves = 40;
        this.lastMove = new Vector<>();
        this.board = new char[this.rows][this.cols];

        for(int r = 0; r < this.rows; ++r){
            for(int c = 0; c < this.cols; ++c){
                this.board[r][c] = b[r][c];
            }
        }
    }

    boolean kingIsCaptured(){
        boolean wExists = false;
        boolean bExists = false;

        int numKings = 0;
        for(int r = 0; r < this.rows; ++r){
            for(int c = 0; c < this.cols; ++c){
                if(this.board[r][c] == 'K'){
                    wExists = true;
                }
                if(this.board[r][c] == 'k'){
                    bExists = true;
                }
            }
        }
        if(wExists && bExists){
            return false;
        }
        return true;
    }

    String getIDMove(int depth) throws IOException{
        long start = System.currentTimeMillis();
        return getIDMove(depth, start);
    }

    String getIDMove(int depth, long startTime) throws IOException{
        if(this.moveNumber < 1){
            return openingMove();
        }
        Board b = new Board(this);
        int currentDepth = 1;
        long maxTime = 5000;

        String move = b.getABNegamaxMove(currentDepth);
        b.move(move);
        currentDepth += 1;

        while(currentDepth < depth){
            Board bb = new Board(this);
            String m = bb.getABNegamaxMove(currentDepth);
            bb.move(m);
            if(System.currentTimeMillis() - startTime > maxTime){
                return move;
            }

            if(bb.kingIsCaptured()){
                return move;
            }

            if(b.kingIsCaptured()){
                return m;
            }

            move = m;
            currentDepth += 1;
        }

        return move;
    }

    String openingMove(){
        if(this.moveNumber > 1){
            return null;
        }
        Random r = new Random();
        int i = r.nextInt(2) + 1;
        if(this.moveNumber == 0 || this.moveNumber == 1){
            if(this.onMove == 1){
                if(i == 1) {
                    return "b2-b3";
                }
                else{
                    return "d2-d3";
                }
            }
            else{
                if(i == 1) {
                    return "b5-b4";
                }
                else{
                    return "d5-d4";
                }
            }
        }
        return null;

    }

    boolean whiteKingCaptured(){
        for(int r = 0; r < this.rows; ++r){
            for(int c = 0; c < this.cols; ++c){
                if(this.board[r][c] == 'K'){
                    return false;
                }
            }
        }
        return true;
    }

    boolean blackKingCaptured(){
        for(int r = 0; r < this.rows; ++r){
            for(int c = 0; c < this.cols; ++c){
                if(this.board[r][c] == 'k'){
                    return false;
                }
            }
        }
        return true;
    }

    String getABNegamaxMove(int depth) throws IOException{
        int num = 900000;
        int alpha = -num;
        int beta = num;

        Random rand = new Random();
        Vector<Move> ms = this.getMoves();
        int sz = ms.size();
        Vector<Move> bestMoves = new Vector<>();

        int bestEval = 0;
        for(int i = 0; i < sz; ++i){
            Board b = new Board(this);
            b.move(ms.elementAt(i));
            if(i == 0){
                bestMoves.add(ms.elementAt(i));
                bestEval = -ABNegamax(b, depth, -alpha, -beta);
                continue;
            }
            int eval = -ABNegamax(b, depth, -alpha, -beta);
            if(eval > bestEval){
                bestMoves.removeAllElements();
                bestMoves.add(ms.elementAt(i));
                bestEval = eval;
            }
            else if(eval == bestEval){
                bestMoves.add(ms.elementAt(i));
            }
        }

        int numBestMoves = bestMoves.size();
        int r = rand.nextInt(numBestMoves);

        return bestMoves.elementAt(r).moveToString();
    }

    int ABNegamax(Board b, int depth, int alpha, int beta) throws IOException{
        if(depth <= 0 || b.kingIsCaptured()){
            return b.evalState();
        }

        Vector<Move> ms = b.getMoves();
        int sz = ms.size();

        Board s = new Board(b);
        s.move(ms.elementAt(0));

        int val = - ABNegamax(s, depth-1, -alpha, -beta);

        if(val > beta){
            return val;
        }

        alpha = Math.max(alpha, val);

        for(int i = 1; i < sz; ++i){
            Board ss = new Board(b);
            ss.move(ms.elementAt(i));
            int v = -ABNegamax(ss, depth-1, -alpha, -beta);
            if(v >= beta){
                return v;
            }
            val = Math.max(val, v);
            alpha = Math.max(alpha, v);
        }
        return val;
    }

    int negamax(Board b, int depth) throws IOException{
        if(depth <= 0 || b.kingIsCaptured()){
            return b.evalState();
        }

        Vector<Move> ms = b.getMoves();
        int sz = ms.size();

        Board s = new Board(b);
        s.move(ms.elementAt(0));

        int val = - negamax(s, depth-1);

        for(int i = 1; i < sz; ++i){
            Board ss = new Board(b);
            ss.move(ms.elementAt(i));
            val = Math.max(val, - negamax(ss, depth-1));
        }
        return val;
    }

    String getNegamaxMove(int depth) throws IOException{
        Random rand = new Random();
        Vector<Move> ms = this.getMoves();
        int sz = ms.size();
        Vector<Move> bestMoves = new Vector<>();

        int bestEval = 0;
        for(int i = 0; i < sz; ++i){
            Board b = new Board(this);
            b.move(ms.elementAt(i));
            if(i == 0){
                bestMoves.add(ms.elementAt(i));
                bestEval = -negamax(b, depth);
                continue;
            }
            int eval = -negamax(b, depth);
            if(eval > bestEval){
                bestMoves.removeAllElements();
                bestMoves.add(ms.elementAt(i));
                bestEval = eval;
            }
            else if(eval == bestEval){
                bestMoves.add(ms.elementAt(i));
            }
        }

        int numBestMoves = bestMoves.size();
        int r = rand.nextInt(numBestMoves);

        return bestMoves.elementAt(r).moveToString();
    }

    String getBestEvalMove() throws IOException{
        Random rand = new Random();
        Vector<Move> ms = this.getMoves();
        int sz = ms.size();

        Vector<Move> bestMoves = new Vector<>();

        int moveEvals[] = new int[sz];
        int bestEval = 0;

        for(int i = 0; i < sz; ++i){
            Board b = new Board(this);
            b.move(ms.elementAt(i).moveToString());
            if(i == 0){
                bestMoves.add(ms.elementAt(i));
                bestEval = -b.evalState();
                continue;
            }
            int be = -b.evalState();
            if(be > bestEval){
                bestMoves.removeAllElements();
                bestMoves.add(ms.elementAt(i));
                bestEval = be;
            }
            else if(be == bestEval){
                bestMoves.add(ms.elementAt(i));
            }
        }

        int numBestMoves = bestMoves.size();
        int r = rand.nextInt(numBestMoves);

        return bestMoves.elementAt(r).moveToString();

    }

    String getRandomMove() throws IOException{
        Random rand = new Random();

        Vector<Move> ms = this.getMoves();
        int sz = ms.size();

        int rn = rand.nextInt(sz);

        return ms.elementAt(rn).moveToString();
    }

    int evalState(){
        int w = 0;
        int b = 0;
        for(int r = 0; r < this.rows; ++r){
            for(int c = 0; c < this.cols; ++c){
                char currentPiece = this.board[r][c];
                if(isWhitePiece(currentPiece)){
                    if(currentPiece == 'P'){
                        w += 100;
                        if(r < 4){
                            w += (-5*(r-4));
                            if(c < 2){
                                w += 0;
                            }
                        }
                    }
                    else if(currentPiece == 'B' || currentPiece == 'N'){
                        w += 700;
                    }
                    else if(currentPiece == 'R'){
                        w += 500;
                    }
                    else if(currentPiece == 'Q'){
                        w += 1100;
                    }
                    else if(currentPiece == 'K'){
                        w += 5000;
                    }
                }
                else if(isBlackPiece(currentPiece)){
                    if(currentPiece == 'p'){
                        b += 100;
                        if(r > 1){
                            b += (5*(r-1));
                        }
                            if(c > 1){
                                b += 0;
                            }
                    }
                    else if(currentPiece == 'b' || currentPiece == 'n'){
                        b += 700;
                    }
                    else if(currentPiece == 'r'){
                        b += 500;
                    }
                    else if(currentPiece == 'q'){
                        b += 1100;
                    }
                    else if(currentPiece == 'k'){
                        b += 5000;
                    }
                }
            }
        }
        if(this.onMove == 1){
            return w - b;
        }
        else{
            return b - w;
        }
    }

    /*
        Checks that c is a white piece.
     */
    boolean isWhitePiece(char c){
        if(c == 'P' || c == 'R' || c == 'N' || c == 'B' || c == 'Q' || c == 'K'){
            return true;
        }
        return false;
    }

    boolean isBlackPiece(char c){
        if(c == 'p' || c == 'r' || c == 'n' || c == 'b' || c == 'q' || c == 'k'){
            return true;
        }
        return false;
    }

    Vector<Move> getMoves() throws IOException{
        return getMoves(this.board);
    }

    /*
        Finds all possible moves on a board state for player on move.
     */
    Vector<Move> getMoves(char[][] brd) throws  IOException{
        Vector<Move> ms = new Vector<>();

        for(int r = 0; r < this.rows; ++r){
            for(int c = 0; c < this.cols; ++c){
                if(this.board[r][c] == '.'){
                    continue;
                }
                if(this.onMove == 1) {
                    if (isWhitePiece(brd[r][c])){
                        Vector<Move> toAdd = moveScan(brd, r, c);
                        ms.addAll(toAdd);
                    }
                }
                else{
                    if(isBlackPiece(brd[r][c])){
                        Vector<Move> toAdd = moveScan(brd, r, c);
                        ms.addAll(toAdd);
                    }
                }
            }
        }
        if(ms.size() == 0){
            System.exit(-this.onMove);
        }
        return ms;
    }

    void getAndPrintMoves() throws IOException{
        Vector<Move> ms = this.getMoves();
        printMoves(ms);
    }

    void printMoves(Vector<Move> ms) throws IOException{
        int s = ms.size();
        for(int i = 0; i < s; ++i){
            ms.elementAt(i).printMove();
        }
    }

    Vector<Move> moveScan(char[][] brd, int r, int c) throws IOException{
        Vector<Move> toAdd = new Vector<Move>();

        switch(Character.toLowerCase(brd[r][c])){
            case 'p':   toAdd = getPawnMoves(brd, r, c);
                break;
            case 'k':   toAdd = getKingMoves(brd, r, c);
                break;
            case 'q':   toAdd = getQueenMoves(brd, r, c);
                break;
            case 'r':   toAdd = getRookMoves(brd, r, c);
                break;
            case 'b':   toAdd = getBishopMoves(brd, r, c);
                break;
            case 'n':   toAdd = getKnightMoves(brd, r, c);
                break;
            default:    throw new IOException("Move scan error");
        }

        return toAdd;
    }


    /*
        Check if taget piece exists and belongs to
        side on move.
        @param m the move to be made.
        @return a game board with the new move, if possible.
            Throw an exeption if not.
     */
    char[][] move(Move move) throws IOException{
        if(this.moveNumber != 0){
            if(this.original) {
                this.lastMove.elementAt(0).printMove();
            }
            this.lastMove.add(move);
            this.lastMove.removeElementAt(0);
        }
        else{
            this.lastMove.add(move);
        }

        if(onMove == 1){
            this.moveNumber += 1;
        }

        if(this.original) {
            this.printBoard();
        }

        if(isInBoundsOnSide(move)){
            int[] coords = move.getToFrom();        //Get coordinates of the move.
            /*
                coords = [from.row][from.col][to.row][to.col]
             */
            char fromPiece = move.getFromPiece(this.board); //Get the type of piece being moved.
            char toPiece = move.getToPiece(this.board);     //Get the type of piece being replaced.

            //Move piece
            this.board[coords[2]][coords[3]] = this.board[coords[0]][coords[1]];
            //Make 'from" piece blank
            this.board[coords[0]][coords[1]] = '.';
            pawnPromotion();

            this.onMove = this.onMove * -1;

            if(this.original && (toPiece == 'K' || toPiece == 'k')){
                char winner;
                if(this.onMove == -1){
                    winner = 'W';
                }
                else{
                    winner = 'B';
                }
                move.printMove();
                this.printBoard();
                System.out.println("= " + winner + " wins");
                System.exit(-this.onMove);
            }
            if(this.original && (this.moveNumber > this.maxMoves)){
                System.out.println("= draw");
                System.exit(0);
            }
            return this.board;
        }
        throw new IOException("Move is not legal.");
    }

    void pawnPromotion(){
        for(int c = 0; c < this.cols; ++c){
            if(this.board[0][c] == 'P'){
                this.board[0][c] = 'Q';
            }
            if(this.board[this.rows-1][c] == 'p'){
                this.board[this.rows-1][c] = 'q';
            }
        }
    }


    /*
        Get a move in the form 'a1-b2'.
        @param move a string with information for a move.
     */
    int move(String move) throws IOException{


        if(move.length() != 5 || move.charAt(2) != '-') {
            throw new IOException("Coordinates in wrong format. " + move.length() + " " + move.charAt(2));
        }

        //col
        int fromX;
        //row
        int fromY = -Character.getNumericValue(move.charAt(1)) + 6;

        if(move.charAt(0) == 'a'){
            fromX = 0;
        }
        else if(move.charAt(0) == 'b'){
            fromX = 1;
        }
        else if(move.charAt(0) == 'c'){
            fromX = 2;
        }
        else if(move.charAt(0) == 'd'){
            fromX = 3;
        }
        else if(move.charAt(0) == 'e'){
            fromX = 4;
        }
        else{
            throw new IOException("First coordinate is wrong.");
        }

        int toX;
        int toY = -Character.getNumericValue(move.charAt(4)) + 6;

        if(move.charAt(3) == 'a'){
            toX = 0;
        }
        else if(move.charAt(3) == 'b'){
            toX = 1;
        }
        else if(move.charAt(3) == 'c'){
            toX = 2;
        }
        else if(move.charAt(3) == 'd'){
            toX = 3;
        }
        else if(move.charAt(3) == 'e'){
            toX = 4;
        }
        else{
            throw new IOException("Second coordinate is wrong.");
        }
        this.board = move(new Move(fromX, fromY, toX, toY));

        return 0;
    }

    private Vector<Move> getRookMoves(char[][] brd, int r, int c){
        Vector<Move> moveList = new Vector<>();

        for(int i = 0; i < 4; ++i){
            //check up
            if(i == 0){
                for(int cr = r-1; cr >= 0; --cr){
                    Square s = new Square(c, cr);
                    if(s.isOnBoard()){
                        if(s.getPiece(brd) == '.'){
                            moveList.add(new Move(c, r, c, cr));
                        }
                        else if(s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                            moveList.add(new Move(c, r, c, cr));
                            break;
                        }
                        else if(!s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                            break;
                        }
                    }
                }
            }
            //down
            else if(i == 1){
                for(int cr = r+1; cr < this.rows; ++cr){
                    Square s = new Square(c, cr);
                    if(s.isOnBoard()){
                        if(s.getPiece(brd) == '.'){
                            moveList.add(new Move(c, r, c, cr));
                        }
                        else if(s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                            moveList.add(new Move(c, r, c, cr));
                            break;
                        }
                        else if(!s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                            break;
                        }
                    }
                }
            }
            //left
            else if(i == 2){
                for(int cc = c-1; cc >= 0; --cc){
                    Square s = new Square(cc, r);
                    if(s.isOnBoard()){
                        if(s.getPiece(brd) == '.'){
                            moveList.add(new Move(c, r, cc, r));
                        }
                        else if(s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                            moveList.add(new Move(c, r, cc, r));
                            break;
                        }
                        else if(!s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                            break;
                        }
                    }
                }
            }
            //right
            else if(i == 3){
                for(int cc = c+1; cc < this.cols; ++cc){
                    Square s = new Square(cc, r);
                    if(s.isOnBoard()){
                        if(s.getPiece(brd) == '.'){
                            moveList.add(new Move(c, r, cc, r));
                        }
                        else if(s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                            moveList.add(new Move(c, r, cc, r));
                            break;
                        }
                        else if(!s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                            break;
                        }
                    }
                }
            }
        }

        return moveList;
    }


    private Vector<Move> getKnightMoves(char[][] brd, int r, int c){
        Vector<Move> moveList = new Vector<>();


        for(int i = 0; i < 8; ++i){

            if(i == 0){
                Square s = new Square(c+1, r-2);
                if(s.isOnBoard()){
                    if(s.getPiece(brd) == '.' || s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)) {
                        moveList.add(new Move(c, r, c + 1, r - 2));
                    }
                }

            }
            else if(i == 1){
                Square s = new Square(c+2, r-1);
                if(s.isOnBoard()){
                    if(s.getPiece(brd) == '.' || s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)) {
                        moveList.add(new Move(c, r, c + 2, r - 1));
                    }
                }
            }
            else if(i == 2){
                Square s = new Square(c+2, r+1);
                if(s.isOnBoard()){
                    if(s.getPiece(brd) == '.' || s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)) {
                        moveList.add(new Move(c, r, c + 2, r + 1));
                    }
                }
            }
            else if(i == 3){
                Square s = new Square(c+1, r+2);
                if(s.isOnBoard()){
                    if(s.getPiece(brd) == '.' || s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)) {
                        moveList.add(new Move(c, r, c + 1, r + 2));
                    }
                }
            }
            else if(i == 4){
                Square s = new Square(c-1, r+2);
                if(s.isOnBoard()){
                    if(s.getPiece(brd) == '.' || s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)) {
                        moveList.add(new Move(c, r, c - 1, r + 2));
                    }
                }
            }
            else if(i == 5){
                Square s = new Square(c-2, r+1);
                if(s.isOnBoard()){
                    if(s.getPiece(brd) == '.' || s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)) {
                        moveList.add(new Move(c, r, c - 2, r + 1));
                    }
                }
            }
            else if(i == 6){
                Square s = new Square(c-2, r-1);
                if(s.isOnBoard()){
                    if(s.getPiece(brd) == '.' || s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)) {
                        moveList.add(new Move(c, r, c - 2, r - 1));
                    }
                }
            }
            else if(i == 7){
                Square s = new Square(c-1, r-2);
                if(s.isOnBoard()){
                    if(s.getPiece(brd) == '.' || s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)) {
                        moveList.add(new Move(c, r, c - 1, r - 2));
                    }
                }
            }

        }

        return moveList;
    }


    private Vector<Move> getPawnMoves(char[][] brd, int r, int c){
        Vector<Move> moveList = new Vector<Move>();
        int direction;

        if(this.onMove == 1){
            //negative direction == up the board
            direction = -1;
        }
        else{
            direction = 1;
        }

        for(int i = 0; i < 3; ++i){
            //check forward move
            if( i == 0){
                Square s = new Square(c, r+direction);
                if(s.isOnBoard() && s.getPiece(brd) == '.'){
                    moveList.add(new Move(c, r, c, r+direction));
                }
            }
            //check right move
            if(i == 1){
                Square s = new Square(c+1, r+direction);
                if(s.isOnBoard() && s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                    moveList.add(new Move(c, r, c+1, r+direction));
                }
            }
            //check left move
            if(i == 2){
                Square s = new Square(c-1, r+direction);
                if(s.isOnBoard() && s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                    moveList.add(new Move(c, r, c-1, r+direction));
                }
            }
        }

        return moveList;
    }


    private Vector<Move> getBishopMoves(char[][] brd, int r, int c) throws IOException{
        Vector<Move> moveList = new Vector<>();


        for(int i = 0; i < 8; ++i){

            //check up
            if(i == 0){
                Square s = new Square(c, r-1);
                if(s.isOnBoard()){
                    if(s.getPiece(brd) == '.'){
                        moveList.add(new Move(c, r, c, r-1));
                    }
                }
            }
            //check NE
            else if(i == 1){
                for(int cc = c+1, cr = r-1; cc < this.cols && cr >= 0; ++cc, --cr){
                    Square s = new Square(cc, cr);
                    if(s.isOnBoard()){
                        if(s.getPiece(brd) == '.'){
                            moveList.add(new Move(c, r, cc, cr));
                        }
                        else if(s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                            moveList.add(new Move(c, r, cc, cr));
                            break;
                        }
                        else if(!s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                            break;
                        }
                    }
                }
            }
            //right
            else if(i == 2){
                Square s = new Square(c+1, r);
                if(s.isOnBoard()){
                    if(s.getPiece(brd) == '.'){
                        moveList.add(new Move(c, r, c+1, r));
                    }
                }
            }
            //SE
            else if(i == 3){
                for(int cc = c+1, cr = r+1; cc < this.cols && cr < this.rows; ++cc, ++cr){
                    Square s = new Square(cc, cr);
                    if(s.isOnBoard()){
                        if(s.getPiece(brd) == '.'){
                            moveList.add(new Move(c, r, cc, cr));
                        }
                        else if(s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                            moveList.add(new Move(c, r, cc, cr));
                            break;
                        }
                        else if(!s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                            break;
                        }
                    }
                }
            }
            //down
            else if(i == 4){
                Square s = new Square(c, r+1);
                if(s.isOnBoard()){
                    if(s.getPiece(brd) == '.'){
                        moveList.add(new Move(c, r, c, r+1));
                    }
                }
            }
            //SW
            else if(i == 5){
                for(int cc = c-1, cr = r+1; cc >= 0 && cr < this.rows; --cc, ++cr){
                    Square s = new Square(cc, cr);
                    if(s.isOnBoard()){
                        if(s.getPiece(brd) == '.'){
                            moveList.add(new Move(c, r, cc, cr));
                        }
                        else if(s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                            moveList.add(new Move(c, r, cc, cr));
                            break;
                        }
                        else if(!s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                            break;
                        }
                    }
                }
            }
            //left
            else if(i == 6){
                Square s = new Square(c-1, r);
                if(s.isOnBoard()){
                    if(s.getPiece(brd) == '.'){
                        moveList.add(new Move(c, r, c-1, r));
                    }
                }
            }
            //NW
            else if(i == 7){
                for(int cc = c-1, cr = r-1; cc >= 0 && cr >= 0; --cc, --cr){
                    Square s = new Square(cc, cr);
                    if(s.isOnBoard()){
                        if(s.getPiece(brd) == '.'){
                            moveList.add(new Move(c, r, cc, cr));
                        }
                        else if(s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                            moveList.add(new Move(c, r, cc, cr));
                            break;
                        }
                        else if(!s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                            break;
                        }
                    }
                }
            }
        }
        return moveList;
    }

    private Vector<Move> getQueenMoves(char[][] brd, int r, int c){
        Vector<Move> moveList = new Vector<>();


        for(int i = 0; i < 8; ++i){

            //check up
            if(i == 0){
                for(int cr = r-1; cr >= 0; --cr){
                    Square s = new Square(c, cr);
                    if(s.isOnBoard()){
                        if(s.getPiece(brd) == '.'){
                            moveList.add(new Move(c, r, c, cr));
                        }
                        else if(s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                            moveList.add(new Move(c, r, c, cr));
                            break;
                        }
                        else if(!s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                            break;
                        }
                    }
                }
            }
            //check NE
            else if(i == 1){
                for(int cc = c+1, cr = r-1; cc < this.cols && cr >= 0; ++cc, --cr){
                    Square s = new Square(cc, cr);
                    if(s.isOnBoard()){
                        if(s.getPiece(brd) == '.'){
                            moveList.add(new Move(c, r, cc, cr));
                        }
                        else if(s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                            moveList.add(new Move(c, r, cc, cr));
                            break;
                        }
                        else if(!s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                            break;
                        }
                    }
                }
            }
            //right
            else if(i == 2){
                for(int cc = c+1; cc < this.cols; ++cc){
                    Square s = new Square(cc, r);
                    if(s.isOnBoard()){
                        if(s.getPiece(brd) == '.'){
                            moveList.add(new Move(c, r, cc, r));
                        }
                        else if(s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                            moveList.add(new Move(c, r, cc, r));
                            break;
                        }
                        else if(!s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                            break;
                        }
                    }
                }
            }
            //SE
            else if(i == 3){
                for(int cc = c+1, cr = r+1; cc < this.cols && cr < this.rows; ++cc, ++cr){
                    Square s = new Square(cc, cr);
                    if(s.isOnBoard()){
                        if(s.getPiece(brd) == '.'){
                            moveList.add(new Move(c, r, cc, cr));
                        }
                        else if(s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                            moveList.add(new Move(c, r, cc, cr));
                            break;
                        }
                        else if(!s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                            break;
                        }
                    }
                }
            }
            //down
            else if(i == 4){
                for(int cr = r+1; cr < this.rows; ++cr){
                    Square s = new Square(c, cr);
                    if(s.isOnBoard()){
                        if(s.getPiece(brd) == '.'){
                            moveList.add(new Move(c, r, c, cr));
                        }
                        else if(s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                            moveList.add(new Move(c, r, c, cr));
                            break;
                        }
                        else if(!s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                            break;
                        }
                    }
                }
            }
            //SW
            else if(i == 5){
                for(int cc = c-1, cr = r+1; cc >= 0 && cr < this.rows; --cc, ++cr){
                    Square s = new Square(cc, cr);
                    if(s.isOnBoard()){
                        if(s.getPiece(brd) == '.'){
                            moveList.add(new Move(c, r, cc, cr));
                        }
                        else if(s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                            moveList.add(new Move(c, r, cc, cr));
                            break;
                        }
                        else if(!s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                            break;
                        }
                    }
                }
            }
            //left
            else if(i == 6){
                for(int cc = c-1; cc >= 0; --cc){
                    Square s = new Square(cc, r);
                    if(s.isOnBoard()){
                        if(s.getPiece(brd) == '.'){
                            moveList.add(new Move(c, r, cc, r));
                        }
                        else if(s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                            moveList.add(new Move(c, r, cc, r));
                            break;
                        }
                        else if(!s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                            break;
                        }
                    }
                }
            }
            //NW
            else if(i == 7){
                for(int cc = c-1, cr = r-1; cc >= 0 && cr >= 0; --cc, --cr){
                    Square s = new Square(cc, cr);
                    if(s.isOnBoard()){
                        if(s.getPiece(brd) == '.'){
                            moveList.add(new Move(c, r, cc, cr));
                        }
                        else if(s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                            moveList.add(new Move(c, r, cc, cr));
                            break;
                        }
                        else if(!s.isOpponentPiece(this.onMove, s.getPiece(brd), brd)){
                            break;
                        }
                    }
                }
            }
        }
        return moveList;
    }

    private Vector<Move> getKingMoves(char[][] brd, int r, int c){
        Vector<Move> moveList = new Vector<Move>();

        for(int i = 0; i < 8; ++i) {
            if (i == 0) {
                Square s = new Square(c, r-1);
                if(s.isOnBoard()){
                    if(s.getPiece(brd) == '.' || s.isOpponentPiece(this.onMove, s.getPiece(brd),brd)){
                        moveList.add(new Move(c, r, c, r-1));
                    }
                }

            }
            else if(i == 1){
                Square s = new Square(c, r+1);
                if(s.isOnBoard()){
                    if(s.getPiece(brd) == '.' || s.isOpponentPiece(this.onMove, s.getPiece(brd),brd)){
                        moveList.add(new Move(c, r, c, r+1));
                    }
                }
            }
            else if(i == 2){
                Square s = new Square(c-1, r);
                if(s.isOnBoard()){
                    if(s.getPiece(brd) == '.' || s.isOpponentPiece(this.onMove, s.getPiece(brd),brd)){
                        moveList.add(new Move(c, r, c-1, r));
                    }
                }
            }
            else if(i == 3){
                Square s = new Square(c+1, r);
                if(s.isOnBoard()){
                    if(s.getPiece(brd) == '.' || s.isOpponentPiece(this.onMove, s.getPiece(brd),brd)){
                        moveList.add(new Move(c, r, c+1, r));
                    }
                }
            }
            else if(i == 4){
                Square s = new Square(c-1, r-1);
                if(s.isOnBoard()){
                    if(s.getPiece(brd) == '.' || s.isOpponentPiece(this.onMove, s.getPiece(brd),brd)){
                        moveList.add(new Move(c, r, c-1, r-1));
                    }
                }
            }
            else if(i == 5){
                Square s = new Square(c+1, r-1);
                if(s.isOnBoard()){
                    if(s.getPiece(brd) == '.' || s.isOpponentPiece(this.onMove, s.getPiece(brd),brd)){
                        moveList.add(new Move(c, r, c+1, r-1));
                    }
                }
            }
            else if(i == 6){
                Square s = new Square(c-1, r+1);
                if(s.isOnBoard()){
                    if(s.getPiece(brd) == '.' || s.isOpponentPiece(this.onMove, s.getPiece(brd),brd)){
                        moveList.add(new Move(c, r, c-1, r+1));
                    }
                }
            }
            else if(i == 7){
                Square s = new Square(c+1, r+1);
                if(s.isOnBoard()){
                    if(s.getPiece(brd) == '.' || s.isOpponentPiece(this.onMove, s.getPiece(brd),brd)){
                        moveList.add(new Move(c, r, c+1, r+1));
                    }
                }
            }
        }

        return moveList;
    }

    /*  Check if a move is in bounds, and
        if the move belongs to the player on move.
        @param move a specific move to make.
        @return true if move is legal.
     */
    boolean isInBoundsOnSide(Move move){
        if(!move.isOnBoard()){
            return false;
        }

        if(onMove == 1){
            return move.isWhiteMove(this.board);
        }
        else{
            return move.isBlackMove(this.board);
        }
    }

    /*
        Initialize a board and its pieces.
     */
    private void initPieces(){
        board = new char[rows][cols];

        //Set all blank pieces.
        for(int r = 0; r < rows; ++r){
            for(int c = 0; c < cols; ++c){
                board[r][c] = '.';
            }
        }

        //Set black pieces.
        board[0][0] = 'k'; board[0][1] = 'q'; board[0][2] = 'b'; board[0][3] = 'n'; board[0][4] = 'r';
        board[1][0] = 'p'; board[1][1] = 'p'; board[1][2] = 'p'; board[1][3] = 'p'; board[1][4] = 'p';

        //Set white pieces.
        board[4][0] = 'P'; board[4][1] = 'P'; board[4][2] = 'P'; board[4][3] = 'P'; board[4][4] = 'P';
        board[5][0] = 'R'; board[5][1] = 'N'; board[5][2] = 'B'; board[5][3] = 'Q'; board[5][4] = 'K';
    }

    void printBoard(){
        char p;
        if(onMove == 1){
            p = 'W';
        }
        else{
            p = 'B';
        }

        System.out.println(moveNumber + " " + p);
        for(int r = 0; r < rows; ++r){
            for(int c = 0; c < cols; ++c){
                System.out.print(board[r][c]);
            }
            System.out.println();
        }
        System.out.println();
    }

}
