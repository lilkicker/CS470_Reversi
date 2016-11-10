import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Michael Sharp on 11/9/2016.
 */
public class ABThread implements Callable<Integer> {
    private int me;
    private int opp;
    private int[][] state;
    private int moveDet;
    private int depth;
    private int alpha;
    private int beta;
    private boolean mazPlayer;
    private boolean forwardPrune;

    public ABThread(int me, int opp, int[][] state, int moveDet, int depth, int alpha, int beta, boolean mazPlayer, boolean forwardPrune) {
        this.me = me;
        this.opp = opp;
        this.state = state;
        this.moveDet = moveDet;
        this.depth = depth;
        this.alpha = alpha;
        this.beta = beta;
        this.mazPlayer = mazPlayer;
        this.forwardPrune = forwardPrune;
    }

    @Override
    public Integer call() throws Exception {
        return alphaBeta(state,moveDet,depth,alpha,beta,mazPlayer,forwardPrune );
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

                return Heuristic.getHeuristicValue(moveDet,state,opp);
            }
            for (Integer i : vMoves){
                copy = copyState(state);
                int score = Heuristic.getPlayerScore(copy,me);
                copy[i/8][i%8] = me;
                copy = changeColors(i/8,i%8,me-1,copy);
                if(forwardPrune && Heuristic.getPlayerScore(copy,me) - score < 2){
                    continue;
                }
                v = Math.max(v, alphaBeta(copy,i,depth-1,alpha,beta,false,forwardPrune));
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

                return Heuristic.getHeuristicValue(moveDet,state,me);
            }

            for (Integer i : vMoves){
                copy = copyState(state);
                int score = Heuristic.getPlayerScore(copy,opp);
                copy[i/8][i%8] = opp;
                copy = changeColors(i/8,i%8,opp-1,copy);
                if(forwardPrune && Heuristic.getPlayerScore(copy,opp) - score > 4){
                    continue;
                }
                v = Math.min(v, alphaBeta(copy,i,depth-1,alpha,beta,true,forwardPrune));
                beta = Math.min(beta,v);
                if (beta <= alpha){
                    break;
                }

            }
            return v;
        }
    }

    private List<Integer> getValidMovesAB(int state[][], int me) {
        int i, j;

        List<Integer> validMoves = new ArrayList<>();
        for (i = 0; i < 8; i++) {
            for (j = 0; j < 8; j++) {
                if (state[i][j] == 0) {
                    if (couldBe(state, i, j, me)) {
                        validMoves.add(i * 8 + j);
                    }
                }
            }
        }
        return validMoves;
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
}


