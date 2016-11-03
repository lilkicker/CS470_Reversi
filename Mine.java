import java.time.chrono.HijrahEra;
import java.util.*;
import java.lang.*;
import java.io.*;
import java.net.*;

class Mine {

    public Socket s;
	public BufferedReader sin;
	public PrintWriter sout;
    Random generator = new Random();

    double t1, t2;
    int me;
    int opp;
    int boardState;
    int state[][] = new int[8][8]; // state[0][0] is the bottom left corner of the board (on the GUI)
    int turn = -1;
    int round;
    
    int validMoves[] = new int[64];
    int numValidMoves;
    
    
    // main function that (1) establishes a connection with the server, and then plays whenever it is this player's turn
    public Mine(int _me, String host) {
        me = _me;
        if (me == 1){
            opp = 2;
        }else{
            opp = 1;
        }
        initClient(host);

        int myMove;
        
        while (true) {
            System.out.println("Read");
            readMessage();
            
            if (turn == me) {
                //System.out.println("Move");
                getValidMoves(round, state);
                
                myMove = move();
                //myMove = generator.nextInt(numValidMoves);        // select a move randomly
                
                String sel = validMoves[myMove] / 8 + "\n" + validMoves[myMove] % 8;
                
                System.out.println("Selection: " + validMoves[myMove] / 8 + ", " + validMoves[myMove] % 8);
                
                sout.println(sel);
            }
        }
        //while (turn == me) {
        //    System.out.println("My turn");
            
            //readMessage();
        //}
    }
    public static void printBoard(int[][] state){

        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                System.out.print(state[i][j]);
            }
            System.out.println();
        }
    }
    private int alphaBeta(int[][] state,int moveDet, int depth, int alpha, int beta, boolean maxPlayer, boolean forwardPrune){
        int copy[][];


        if (maxPlayer){
            if (depth == 0){
                return Heuristic.getHeuristicValue(moveDet,state,opp);
            }
            int v = -10;
            List<Integer> vMoves = getValidMovesAB(state,me);
            if (vMoves.size() == 0){

                return Heuristic.getHeuristicValue(moveDet,state,me);
            }
            for (Integer i : vMoves){
                copy = copyState(state);
                int score = Heuristic.getPlayerScore(copy,me);
                copy[i/8][i%8] = me;
                copy = changeColors(i/8,i%8,me-1,copy);
                if(forwardPrune && Heuristic.getPlayerScore(copy,me) - score < 2){
                	continue;
                }
                v = Math.max(v, alphaBeta(copy,i,depth-1,alpha,beta,false));
                alpha = Math.max(alpha,v);
                if (beta <= alpha){
                    break;
                }

            }
            return v;


        }else{
            if (depth == 0){
                return Heuristic.getHeuristicValue(moveDet,state,me);
            }
            int v = Integer.MAX_VALUE;
            List<Integer> vMoves = getValidMovesAB(state,opp);
            if (vMoves.size() == 0){

                return Heuristic.getHeuristicValue(moveDet,state,opp);
            }

            for (Integer i : vMoves){
                copy = copyState(state);
                int score = Heuristic.getPlayerScore(copy,opp);

                copy[i/8][i%8] = opp;
                copy = changeColors(i/8,i%8,opp-1,copy);
                
                if(forwardPrune && Heuristic.getPlayerScore(copy,opp) - score > 5){
                	continue;
                }
                v = Math.min(v, alphaBeta(copy,i,depth-1,alpha,beta,true));
                beta = Math.min(beta,v);
                if (beta <= alpha){
                    break;
                }

            }
            return v;
        }
    }

    public int[][] checkDirection(int row, int col, int incx, int incy, int player, int[][] state) {
        int sequence[] = new int[7];
        int seqLen;
        int i, r, c;

        seqLen = 0;
        for (i = 1; i < 8; i++) {
            r = row+incy*i;
            c = col+incx*i;

            if ((r < 0) || (r > 7) || (c < 0) || (c > 7))
                break;

            sequence[seqLen] = state[r][c];
            seqLen++;
        }

        int count = 0;
        for (i = 0; i < seqLen; i++) {
            if (player == 0) {
                if (sequence[i] == 2)
                    count ++;
                else {
                    if ((sequence[i] == 1) && (count > 0))
                        count = 20;
                    break;
                }
            }
            else {
                if (sequence[i] == 1)
                    count ++;
                else {
                    if ((sequence[i] == 2) && (count > 0))
                        count = 20;
                    break;
                }
            }
        }

        if (count > 10) {
            if (player == 0) {
                i = 1;
                r = row+incy*i;
                c = col+incx*i;
                while (state[r][c] == 2) {
                    state[r][c] = 1;
                    i++;
                    r = row+incy*i;
                    c = col+incx*i;
                }
            }
            else {
                i = 1;
                r = row+incy*i;
                c = col+incx*i;
                while (state[r][c] == 1) {
                    state[r][c] = 2;
                    i++;
                    r = row+incy*i;
                    c = col+incx*i;
                }
            }
        }
        return state;
    }

    public int[][] changeColors(int row, int col, int player, int[][] state) {
        int incx, incy;

        for (incx = -1; incx < 2; incx++) {
            for (incy = -1; incy < 2; incy++) {
                if ((incx == 0) && (incy == 0))
                    continue;

                state = checkDirection(row, col, incx, incy, player, state);
            }
        }
        return state;
    }

    private int[][] copyState(int[][] state){
        int[][] toReturn = new int[8][8];
        for(int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                toReturn[i][j] = state[i][j];
            }
        }
        return toReturn;
    }

    private List<Integer> getValidMovesAB(int state[][],int me) {
        int i, j;

        List<Integer> validMoves = new ArrayList<>();
        for (i = 0; i < 8; i++) {
            for (j = 0; j < 8; j++) {
                if (state[i][j] == 0) {
                    if (couldBe(state, i, j,me)) {
                        validMoves.add( i*8 + j);
                    }
                }
            }
        }
        return validMoves;
    }

    // You should modify this function
    // validMoves is a list of valid locations that you could place your "stone" on this turn
    // Note that "state" is a global variable 2D list that shows the state of the game
    private int move() {
        int myMove = 0;
        int index = 0;
        if (round <4) {
            myMove = generator.nextInt(numValidMoves);
        }else {

            int v = -10;
            List<Integer> vMoves = getValidMovesAB(state,me);

            for (Integer i : vMoves) {
                if (Heuristic.isCorner(i)){
                    return index;
                }

                int[][] copy = copyState(state);
                copy[i/8][i%8] = me;
                copy = changeColors(i/8,i%8,me-1,copy);
                int t = Math.max(v, alphaBeta(copy, i, 8, Integer.MIN_VALUE, Integer.MAX_VALUE, false,true));

                if (t > v) {
                    myMove = index;
                    v = t;
                }
                index ++;
            }
            /*if (Heuristic.isNextToCorner(vMoves.get(myMove))){
                System.out.println();
            }*/
        }

        return myMove;
    }
    
    // generates the set of valid moves for the player; returns a list of valid moves (validMoves)
    private void getValidMoves(int round, int state[][]) {
        int i, j;
        
        numValidMoves = 0;
        if (round < 4) {
            if (state[3][3] == 0) {
                validMoves[numValidMoves] = 3*8 + 3;
                numValidMoves ++;
            }
            if (state[3][4] == 0) {
                validMoves[numValidMoves] = 3*8 + 4;
                numValidMoves ++;
            }
            if (state[4][3] == 0) {
                validMoves[numValidMoves] = 4*8 + 3;
                numValidMoves ++;
            }
            if (state[4][4] == 0) {
                validMoves[numValidMoves] = 4*8 + 4;
                numValidMoves ++;
            }
            //System.out.println("Valid Moves:");
            //for (i = 0; i < numValidMoves; i++) {
                //System.out.println(validMoves[i] / 8 + ", " + validMoves[i] % 8);
            //}
        }
        else {
            //System.out.println("Valid Moves:");
            for (i = 0; i < 8; i++) {
                for (j = 0; j < 8; j++) {
                    if (state[i][j] == 0) {
                        if (couldBe(state, i, j,me)) {
                            validMoves[numValidMoves] = i*8 + j;
                            numValidMoves ++;
                            //System.out.println(i + ", " + j);
                        }
                    }
                }
            }
        }
    }
    
    private boolean checkDirection(int state[][], int row, int col, int incx, int incy, int me) {
        int sequence[] = new int[7];
        int seqLen;
        int i, r, c;
        
        seqLen = 0;
        for (i = 1; i < 8; i++) {
            r = row+incy*i;
            c = col+incx*i;
        
            if ((r < 0) || (r > 7) || (c < 0) || (c > 7))
                break;
        
            sequence[seqLen] = state[r][c];
            seqLen++;
        }
        
        int count = 0;
        for (i = 0; i < seqLen; i++) {
            if (me == 1) {
                if (sequence[i] == 2)
                    count ++;
                else {
                    if ((sequence[i] == 1) && (count > 0))
                        return true;
                    break;
                }
            }
            else {
                if (sequence[i] == 1)
                    count ++;
                else {
                    if ((sequence[i] == 2) && (count > 0))
                        return true;
                    break;
                }
            }
        }
        
        return false;
    }
    
    private boolean couldBe(int state[][], int row, int col, int me) {
        int incx, incy;
        
        for (incx = -1; incx < 2; incx++) {
            for (incy = -1; incy < 2; incy++) {
                if ((incx == 0) && (incy == 0))
                    continue;
            
                if (checkDirection(state, row, col, incx, incy, me))
                    return true;
            }
        }
        
        return false;
    }
    
    public void readMessage() {
        int i, j;
        String status;
        try {
            //System.out.println("Ready to read again");
            turn = Integer.parseInt(sin.readLine());
            
            if (turn == -999) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
                
                System.exit(1);
            }
            
            //System.out.println("Turn: " + turn);
            round = Integer.parseInt(sin.readLine());
            t1 = Double.parseDouble(sin.readLine());
            //System.out.println(t1);
            t2 = Double.parseDouble(sin.readLine());
            //System.out.println(t2);
            for (i = 0; i < 8; i++) {
                for (j = 0; j < 8; j++) {
                    state[i][j] = Integer.parseInt(sin.readLine());
                }
            }
            sin.readLine();
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
        
        /*System.out.println("Turn: " + turn);
        System.out.println("Round: " + round);
        for (i = 7; i >= 0; i--) {
            for (j = 0; j < 8; j++) {
                System.out.print(state[i][j]);
            }
            System.out.println();
        }
        System.out.println();*/
    }
    
    public void initClient(String host) {
        int portNumber = 3333+me;
        
        try {
			s = new Socket(host, portNumber);
            sout = new PrintWriter(s.getOutputStream(), true);
			sin = new BufferedReader(new InputStreamReader(s.getInputStream()));
            
            String info = sin.readLine();
            //System.out.println(info);
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
    }

    
    // compile on your machine: javac *.java
    // call: java RandomGuy [ipaddress] [player_number]
    //   ipaddress is the ipaddress on the computer the server was launched on.  Enter "localhost" if it is on the same computer
    //   player_number is 1 (for the black player) and 2 (for the white player)
    public static void main(String args[]) {
        new Mine(Integer.parseInt(args[1]), args[0]);
    }
    
}
