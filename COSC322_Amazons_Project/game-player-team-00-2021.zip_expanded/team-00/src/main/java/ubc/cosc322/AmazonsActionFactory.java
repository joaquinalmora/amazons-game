package ubc.cosc322;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class AmazonsActionFactory {
	
//	public static void main(String args[]) {
//		int[][] defaultBoard = new int[][] {
//			{0,0,0,2,0,0,2,0,0,0},
//			{0,0,0,0,0,0,0,0,0,0},
//			{0,0,0,0,0,0,0,0,0,0},
//			{2,0,0,0,0,0,0,0,0,2},
//			{0,0,0,0,0,0,0,0,0,0},
//			{0,0,0,0,0,0,0,0,0,0},
//			{1,0,0,0,0,0,0,0,0,1},
//			{0,0,0,0,0,0,0,0,0,0},
//			{0,0,0,0,0,0,0,0,0,0},
//			{0,0,0,1,0,0,1,0,0,0},
//		};
//		
//		int[][] board = new int[][] {
//			{0,0,0,2,0,0,2,0,0,0},
//			{0,0,0,0,0,0,0,0,0,0},
//			{0,0,0,0,0,0,0,0,0,0},
//			{2,0,0,0,0,0,0,0,0,2},
//			{0,0,3,0,0,0,0,0,0,0},
//			{3,3,0,0,3,0,0,0,3,3},
//			{1,3,0,0,0,0,0,0,3,1},
//			{3,3,0,0,0,0,0,0,3,3},
//			{0,0,3,3,3,3,3,3,0,0},
//			{0,0,3,1,3,3,1,3,0,0},
//		};
//		
//		int[][] mobilityMap = AmazonsUtility.getMobilityMap(defaultBoard);
//		int[][][] state = new int[][][] {defaultBoard, mobilityMap};
//		int[][][] newState = new int[2][10][10];
//		int currentPlayer = 1;
//		
//		while(!getActions(state, currentPlayer).isEmpty()) {
//			AmazonsUtility.printBoard(state[0]);
//			ArrayList<AmazonsAction> actions = getActions(state, currentPlayer);
//			double maxEval = Integer.MIN_VALUE;
//			double eval;
//			int[][][] bestState = new int[2][10][10];
//			for(AmazonsAction action : actions) {
//				newState = AmazonsAction.applyAction(action, state);
//				eval = HeuristicEvaluator.getHeuristicEval(newState, 2);
//				if(currentPlayer == 2) {
//					eval = -eval;
//				}
//				if(eval > maxEval) {
//					maxEval = eval;
//					bestState = newState;
//				}
//			}
//			if(currentPlayer == 1) {
//				currentPlayer = 2;
//			} else currentPlayer = 1;
//			state = bestState;
//		}
//		AmazonsUtility.printBoard(state[0]);
//		System.out.println("Player " + currentPlayer + " loses!");
//
//	}
	
	// takes in a board state and a color (1 is white, 2 is black)
	// returns an ArrayList of all actions
	public static ArrayList<AmazonsAction> getActions(int[][][] state, int color) {
		int[][] board = state[0];
		ArrayList<AmazonsAction> actions = new ArrayList<>();
		
		Queue<int[]> queenPositions = new LinkedList<>();
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				if(board[i][j] == color) {
					queenPositions.add(new int[]{i, j});
				}
			}
		}
		
		while(!queenPositions.isEmpty()) {
			int[] queenPosition = queenPositions.poll();
			int queenSrcY = queenPosition[0];
			int queenSrcX = queenPosition[1];
			
			boolean leftValid = true; 
			boolean upLeftValid = true;
			boolean upValid = true;
			boolean upRightValid = true;
			boolean rightValid = true;
			boolean downRightValid = true;
			boolean downValid = true;
			boolean downLeftValid = true;
			
			for(int i = 1; i < 10; i++) {
				// up
				if(upValid && AmazonsUtility.isSpotValid(board, queenSrcY-i, queenSrcX)) {
					//System.out.println(queenSrcY-i + ", " + queenSrcX);
					actions.addAll(getArrowMoves(queenSrcX, queenSrcY, queenSrcX, queenSrcY-i, state));
				} else upValid = false;

				// up left
				if(upLeftValid && AmazonsUtility.isSpotValid(board, queenSrcY-i, queenSrcX-i)) {
					actions.addAll(getArrowMoves(queenSrcX, queenSrcY, queenSrcX-i, queenSrcY-i, state));
				} else upLeftValid = false;
				
				// left
				if(leftValid && AmazonsUtility.isSpotValid(board, queenSrcY, queenSrcX-i)) {
					actions.addAll(getArrowMoves(queenSrcX, queenSrcY, queenSrcX-i, queenSrcY, state));
				} else leftValid = false;
				
				// down left
				if(downLeftValid && AmazonsUtility.isSpotValid(board, queenSrcY+i, queenSrcX-i)) {
					actions.addAll(getArrowMoves(queenSrcX, queenSrcY, queenSrcX-i, queenSrcY+i, state));
				} else downLeftValid = false;
				
				// down
				if(downValid && AmazonsUtility.isSpotValid(board, queenSrcY+i, queenSrcX)) {
					actions.addAll(getArrowMoves(queenSrcX, queenSrcY, queenSrcX, queenSrcY+i, state));
				} else downValid = false;
				
				// down right
				if(downRightValid && AmazonsUtility.isSpotValid(board, queenSrcY+i, queenSrcX+i)) {
					actions.addAll(getArrowMoves(queenSrcX, queenSrcY, queenSrcX+i, queenSrcY+i, state));
				} else downRightValid = false;
				
				// right
				if(rightValid && AmazonsUtility.isSpotValid(board, queenSrcY, queenSrcX+i)) {
					actions.addAll(getArrowMoves(queenSrcX, queenSrcY, queenSrcX+i, queenSrcY, state));
				} else rightValid = false;
				
				// up right
				if(upRightValid && AmazonsUtility.isSpotValid(board, queenSrcY-i, queenSrcX+i)) {
					actions.addAll(getArrowMoves(queenSrcX, queenSrcY, queenSrcX+i, queenSrcY-i, state));
				} else upRightValid = false;
			}
			
		}
		
		return actions;
	}
	
	private static ArrayList<AmazonsAction> getArrowMoves(int queenSrcX, int queenSrcY, int queenDestX, int queenDestY, int[][][] state) {
		int[][] board = state[0];
		ArrayList<AmazonsAction> actions = new ArrayList<>();
		
		int[][] newBoard = AmazonsAction.applyQueenMove(queenSrcX, queenSrcY, queenDestX, queenDestY, state);
		
		boolean leftValid = true; 
		boolean upLeftValid = true;
		boolean upValid = true;
		boolean upRightValid = true;
		boolean rightValid = true;
		boolean downRightValid = true;
		boolean downValid = true;
		boolean downLeftValid = true;
		
		for(int i = 1; i < 10; i++) {
			// up
			if(upValid && AmazonsUtility.isSpotValid(newBoard, queenDestY-i, queenDestX)) {
				actions.add(new AmazonsAction(queenSrcX, queenSrcY, queenDestX, queenDestY, queenDestX, queenDestY-i));
			} else upValid = false;

			// up left
			if(upLeftValid && AmazonsUtility.isSpotValid(newBoard, queenDestY-i, queenDestX-i)) {
				actions.add(new AmazonsAction(queenSrcX, queenSrcY, queenDestX, queenDestY, queenDestX-i, queenDestY-i));
			} else upLeftValid = false;
			
			// left
			if(leftValid && AmazonsUtility.isSpotValid(newBoard, queenDestY, queenDestX-i)) {
				actions.add(new AmazonsAction(queenSrcX, queenSrcY, queenDestX, queenDestY, queenDestX-i, queenDestY));
			} else leftValid = false;
			
			// down left
			if(downLeftValid && AmazonsUtility.isSpotValid(newBoard, queenDestY+i, queenDestX-i)) {
				actions.add(new AmazonsAction(queenSrcX, queenSrcY, queenDestX, queenDestY, queenDestX-i, queenDestY+i));
			} else downLeftValid = false;
			
			// down
			if(downValid && AmazonsUtility.isSpotValid(newBoard, queenDestY+i, queenDestX)) {
				actions.add(new AmazonsAction(queenSrcX, queenSrcY, queenDestX, queenDestY, queenDestX, queenDestY+i));
			} else downValid = false;
			
			// down right
			if(downRightValid && AmazonsUtility.isSpotValid(newBoard, queenDestY+i, queenDestX+i)) {
				actions.add(new AmazonsAction(queenSrcX, queenSrcY, queenDestX, queenDestY, queenDestX+i, queenDestY+i));
			} else downRightValid = false;
			
			// right
			if(rightValid && AmazonsUtility.isSpotValid(newBoard, queenDestY, queenDestX+i)) {
				actions.add(new AmazonsAction(queenSrcX, queenSrcY, queenDestX, queenDestY, queenDestX+i, queenDestY));
			} else rightValid = false;
			
			// up right
			if(upRightValid && AmazonsUtility.isSpotValid(newBoard, queenDestY-i, queenDestX+i)) {
				actions.add(new AmazonsAction(queenSrcX, queenSrcY, queenDestX, queenDestY, queenDestX+i, queenDestY-i));
			} else upRightValid = false;
		}
		
		return actions;
	}
}
