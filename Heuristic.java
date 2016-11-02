public class Heuristic {
	
	//** Assumption of grid **//
	// 56 57 58 59 60 61 62 63
	// 48 49 50 51 52 53 54 55
	// 40 41 42 43 44 45 46 47
	// 32 33 34 35 36 37 38 39
	// 24 25 26 27 28 29 30 31
	// 16 17 18 19 20 21 22 23
	//  8  9 10 11 12 13 14 15
	//  0  1  2  3  4  5  6  7
	
	public static int getHeuristicValue(int alreadyPlacedMove, int[][] newState, int myColor) {
		int score = getPlayerScore(newState, myColor);
		if(isCorner(alreadyPlacedMove)) {
			score += 15;
		}
		else if(isNextToCorner(alreadyPlacedMove)) {
			score -= 10;
		}
		else if(isTwoAwayFromCorner(alreadyPlacedMove)) {
			score += 3;
		}
		else if(isEdge(alreadyPlacedMove)) {
			score += 1;
		}
		


		
		return score;
	}
	
	public static int getPlayerScore(int[][] state, int myColor) {
		
		//** find opposing color **//
		//int otherColor = 2;
		//if(myColor == 2) {
		//	otherColor = 1; 
		//}
		int result = 0;
		for(int i=0; i<8; i++) {
			for(int j=0; j<8; j++) {
				if(state[i][j] == myColor) {
					result++;
				}
				//** if we want to subtract opposing players score **//
				//else if(state[i][j] == otherColor) {
				//	result--;
				//}
			}
		}
		return result;
	}
	
	// if move == 1
	// 1 0 0 0 0 0 0 1
	// 0 0 0 0 0 0 0 0
	// 0 0 0 0 0 0 0 0
	// 0 0 0 0 0 0 0 0
	// 0 0 0 0 0 0 0 0
	// 0 0 0 0 0 0 0 0
	// 0 0 0 0 0 0 0 0
	// 1 0 0 0 0 0 0 1
	public static boolean isCorner(int move) {
		if(move == 0) {
			return true;
		}
		else if(move == 7) {
			return true;
		}
		else if(move == 56) {
			return true;
		}
		else if(move == 63) {
			return true;
		}
		return false;
	}
	
	// if move == 1
	// 1 1 1 1 1 1 1 1
	// 1 0 0 0 0 0 0 1
	// 1 0 0 0 0 0 0 1
	// 1 0 0 0 0 0 0 1
	// 1 0 0 0 0 0 0 1
	// 1 0 0 0 0 0 0 1
	// 1 0 0 0 0 0 0 1
	// 1 1 1 1 1 1 1 1
	public static boolean isEdge(int move) {
		if(move < 8) { // bottom edge
			return true;
		}
		else if(move % 8 == 0) { // left edge
			return true;
		}
		else if(move % 8 == 7) { // right edge
			return true;
		}
		else if(move > 55) { // top edge
			return true;
		}
		return false;
	}
	
	// if move == 1
	// 0 1 0 0 0 0 1 0
	// 1 1 0 0 0 0 1 1
	// 0 0 0 0 0 0 0 0
	// 0 0 0 0 0 0 0 0
	// 0 0 0 0 0 0 0 0
	// 0 0 0 0 0 0 0 0
	// 1 1 0 0 0 0 1 1
	// 0 1 0 0 0 0 1 0
	public static boolean isNextToCorner(int move) {
		if(move == 1 || move == 8 || move == 9) { // bottom left
			return true;
		}
		else if(move == 6 || move == 14 || move == 15) { // bottom right 
			return true;
		}
		else if(move == 48 || move == 49 || move == 57) { // top left 
			return true;
		}
		else if(move == 62 || move == 54 || move == 55) { // top right 
			return true;
		}
		return false;
	}
	
	// if move == 1
	// 0 0 1 0 0 1 0 0
	// 0 0 1 0 0 1 0 0
	// 1 1 1 0 0 1 1 1
	// 0 0 0 0 0 0 0 0
	// 0 0 0 0 0 0 0 0
	// 1 1 1 0 0 1 1 1
	// 0 0 1 0 0 1 0 0
	// 0 0 1 0 0 1 0 0
	public static boolean isTwoAwayFromCorner(int move) {
		if(move == 2 || move == 10 || move == 16 || move == 17 || move == 18) { // bottom left
			return true;
		}
		else if(move == 5 || move == 13 || move == 21 || move == 22 || move == 23) { // bottom right 
			return true;
		}
		else if(move == 40 || move == 41 || move == 42 || move == 50 || move == 58) { // top left 
			return true;
		}
		else if(move == 45 || move == 46 || move == 47 || move == 53 || move == 61) { // top right 
			return true;
		}
		return false;
	}
	
}
