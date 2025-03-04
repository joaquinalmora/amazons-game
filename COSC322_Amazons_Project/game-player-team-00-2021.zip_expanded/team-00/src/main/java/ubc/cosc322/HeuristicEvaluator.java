package ubc.cosc322;

import java.util.LinkedList;
import java.util.Queue;

public class HeuristicEvaluator {
	private static int whiteQueen = 1;
    private static int blackQueen = 2;
    
    private static double turnAdvantage = 0.15;
    
//    public static void main(String[] args) {
//    	int[][] board = new int[][] {
//    		{0,0,0,2,0,0,2,0,0,0},
//    		{0,0,0,0,0,0,0,0,0,0},
//    		{0,0,0,0,0,0,0,0,0,0},
//    		{2,0,0,0,0,0,0,0,0,2},
//    		{0,0,0,0,0,0,0,0,0,0},
//    		{0,0,0,0,0,0,0,0,0,0},
//    		{1,0,0,0,0,0,0,0,0,1},
//    		{0,0,0,0,0,0,0,0,0,0},
//    		{0,0,0,0,0,0,0,0,0,0},
//    		{0,0,0,1,0,0,1,0,0,0}};
//    		
//    	int[][] mobilityMap = AmazonsUtility.getMobilityMap(board);
//    	System.out.println(getHeuristicEval(board, mobilityMap, whiteQueen));
//    		
//		//final long startTime = System.nanoTime();
//		//System.out.println("Heuristic value is: " + kingMinDistance(board, 1));
//		//final long endTime = System.nanoTime();
//		//System.out.println("Total execution time: " + (endTime - startTime)/1000000.0);
//		//System.out.println(getHeuristicEval(board, HeuristicType.MIN_DISTANCE, 1));
//    }
	
	// returns a double representing the advantage of one player
	// positive values are white advantage
	// negative values are black advantage
	public static double getHeuristicEval(int state[][][], int playerTurn) {
		int[][] board = state[0];
		int[][] mobilityMap = state[1];
		
		double[] queenMinDistance = queenMinDistance(board, playerTurn);
		double t1 = queenMinDistance[0];
		double t2 = 0.0;
		double c1 = queenMinDistance[1];
		double c2 = 0.0;
		double w = queenMinDistance[2];
		
		double mobilityEval = 0.0;
		
		if(w > 10.0) {
			double[] kingMinDistance = kingMinDistance(board, playerTurn);
			t2 = kingMinDistance[0];
			c2 = kingMinDistance[1];
			mobilityEval = getMobilityEval(board, mobilityMap, w);
		} else {
			w = 0.0;
		}
		
		double territoryEval = f1(w)*t1 + f2(w)*c1 + f3(w)*t2 + f4(w)*c2;

		return territoryEval + mobilityEval;
	}
	
	private static double f1(double w) {
		return (100.0 - w) / 100.0;
	}
	
	private static double f2(double w) {
		return (1.0 - f1(w)) / 4.0;
	}
	
	private static double f3(double w) {
		return (1.0 - f1(w)) / 4.0;
	}
	
	private static double f4(double w) {
		return (1.0 - f1(w)) / 4.0;
	}
	
	private static double f5(double w, double mobility) {
		return  w * Math.pow(1.2, -mobility) / 45.0;
	}
	
	// returns an integer representing the number of squares controlled by each player
	// return array has 2 values
	// the first is in the form (squares controlled by white) - (squares controlled by black) and represents t1
	// the second represents c1
	private static double[] queenMinDistance(int board[][], int playerTurn) {
		int whiteMinDistance[][] = new int[10][10];
		int blackMinDistance[][] = new int[10][10];
		
		// must initialize above arrays to infinity
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				whiteMinDistance[i][j] = Integer.MAX_VALUE;
				blackMinDistance[i][j] = Integer.MAX_VALUE;
			}
		}
		
		Queue<int[]> whiteQueue = new LinkedList<>();
		Queue<int[]> blackQueue = new LinkedList<>();
		
		// get initial positions of all queens and add them  to respective queues
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				if(board[i][j] == whiteQueen) {
					whiteQueue.add(new int[]{i, j});
					whiteMinDistance[i][j] = 0;
				}
				else if(board[i][j] == blackQueen) {
					blackQueue.add(new int[]{i, j});
					blackMinDistance[i][j] = 0;
				}
			}
		}
		// for each item in the queue we want to loop in all directions
		while(!whiteQueue.isEmpty()) {
			int[] whitePosition = whiteQueue.poll();
			int posY = whitePosition[0];
			int posX = whitePosition[1];
			int minDistance = whiteMinDistance[posY][posX] + 1;
			
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
				if(upValid && AmazonsUtility.isSpotValid(board, posY-i, posX)) {
					if(whiteMinDistance[posY-i][posX] > minDistance) {
						whiteMinDistance[posY-i][posX] = minDistance;
						whiteQueue.add(new int[]{posY-i, posX});
					}
				} else upValid = false;
				
				// up left
				if(upLeftValid && AmazonsUtility.isSpotValid(board, posY-i, posX-i)) {
					if(whiteMinDistance[posY-i][posX-i] > minDistance) {
						whiteMinDistance[posY-i][posX-i] = minDistance;
						whiteQueue.add(new int[]{posY-i, posX-i});
					}
				} else upLeftValid = false;
				
				// left
				if(leftValid && AmazonsUtility.isSpotValid(board, posY, posX-i)) {
					if(whiteMinDistance[posY][posX-i] > minDistance) {
						whiteMinDistance[posY][posX-i] = minDistance;
						whiteQueue.add(new int[]{posY, posX-i});
					}
				} else leftValid = false;
				
				// down left
				if(downLeftValid && AmazonsUtility.isSpotValid(board, posY+i, posX-i)) {
					if(whiteMinDistance[posY+i][posX-i] > minDistance) {
						whiteMinDistance[posY+i][posX-i] = minDistance;
						whiteQueue.add(new int[]{posY+i, posX-i});
					}
				} else downLeftValid = false;
				
				// down
				if(downValid && AmazonsUtility.isSpotValid(board, posY+i, posX)) {
					if(whiteMinDistance[posY+i][posX] > minDistance) {
						whiteMinDistance[posY+i][posX] = minDistance;
						whiteQueue.add(new int[]{posY+i, posX});
					}
				} else downValid = false;
				
				// down right
				if(downRightValid && AmazonsUtility.isSpotValid(board, posY+i, posX+i)) {
					if(whiteMinDistance[posY+i][posX+i] > minDistance) {
						whiteMinDistance[posY+i][posX+i] = minDistance;
						whiteQueue.add(new int[]{posY+i, posX+i});
					}
				} else downRightValid = false;
				
				// right
				if(rightValid && AmazonsUtility.isSpotValid(board, posY, posX+i)) {
					if(whiteMinDistance[posY][posX+i] > minDistance) {
						whiteMinDistance[posY][posX+i] = minDistance;
						whiteQueue.add(new int[]{posY, posX+i});
					}
				} else rightValid = false;
				
				// up right
				if(upRightValid && AmazonsUtility.isSpotValid(board, posY-i, posX+i)) {
					if(whiteMinDistance[posY-i][posX+i] > minDistance) {
						whiteMinDistance[posY-i][posX+i] = minDistance;
						whiteQueue.add(new int[]{posY-i, posX+i});
					}
				} else upRightValid = false;
			}
		}
		
		while(!blackQueue.isEmpty()) {
			int[] whitePosition = blackQueue.poll();
			int posY = whitePosition[0];
			int posX = whitePosition[1];
			int minDistance = blackMinDistance[posY][posX] + 1;
			
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
				if(upValid && AmazonsUtility.isSpotValid(board, posY-i, posX)) {
					if(blackMinDistance[posY-i][posX] > minDistance) {
						blackMinDistance[posY-i][posX] = minDistance;
						blackQueue.add(new int[]{posY-i, posX});
					}
				} else upValid = false;
				
				// up left
				if(upLeftValid && AmazonsUtility.isSpotValid(board, posY-i, posX-i)) {
					if(blackMinDistance[posY-i][posX-i] > minDistance) {
						blackMinDistance[posY-i][posX-i] = minDistance;
						blackQueue.add(new int[]{posY-i, posX-i});
					}
				} else upLeftValid = false;
				
				// left
				if(leftValid && AmazonsUtility.isSpotValid(board, posY, posX-i)) {
					if(blackMinDistance[posY][posX-i] > minDistance) {
						blackMinDistance[posY][posX-i] = minDistance;
						blackQueue.add(new int[]{posY, posX-i});
					}
				} else leftValid = false;
				
				// down left
				if(downLeftValid && AmazonsUtility.isSpotValid(board, posY+i, posX-i)) {
					if(blackMinDistance[posY+i][posX-i] > minDistance) {
						blackMinDistance[posY+i][posX-i] = minDistance;
						blackQueue.add(new int[]{posY+i, posX-i});
					}
				} else downLeftValid = false;
				
				// down
				if(downValid && AmazonsUtility.isSpotValid(board, posY+i, posX)) {
					if(blackMinDistance[posY+i][posX] > minDistance) {
						blackMinDistance[posY+i][posX] = minDistance;
						blackQueue.add(new int[]{posY+i, posX});
					}
				} else downValid = false;
				
				// down right
				if(downRightValid && AmazonsUtility.isSpotValid(board, posY+i, posX+i)) {
					if(blackMinDistance[posY+i][posX+i] > minDistance) {
						blackMinDistance[posY+i][posX+i] = minDistance;
						blackQueue.add(new int[]{posY+i, posX+i});
					}
				} else downRightValid = false;
				
				// right
				if(rightValid && AmazonsUtility.isSpotValid(board, posY, posX+i)) {
					if(blackMinDistance[posY][posX+i] > minDistance) {
						blackMinDistance[posY][posX+i] = minDistance;
						blackQueue.add(new int[]{posY, posX+i});
					}
				} else rightValid = false;
				
				// up right
				if(upRightValid && AmazonsUtility.isSpotValid(board, posY-i, posX+i)) {
					if(blackMinDistance[posY-i][posX+i] > minDistance) {
						blackMinDistance[posY-i][posX+i] = minDistance;
						blackQueue.add(new int[]{posY-i, posX+i});
					}
				} else upRightValid = false;
			}
		}
		
		/*
		int boardControl[][] = new int[10][10];
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				boardControl[i][j] = 0;
			}
		}
		*/
		
		// add up a score from computed minDistance values
		double score = 0.0;
		double c1 = 0.0;
		double w = 0.0;
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				c1 += Math.pow(2, -whiteMinDistance[i][j]) - Math.pow(2, -blackMinDistance[i][j]);
				
				if(whiteMinDistance[i][j] != Integer.MAX_VALUE && blackMinDistance[i][j] != Integer.MAX_VALUE) {
					w += Math.pow(2, -Math.abs(whiteMinDistance[i][j] - blackMinDistance[i][j]));
				}
				
				// if white advantage
				if(whiteMinDistance[i][j] < blackMinDistance[i][j]) {
					score += 1.0;
					//boardControl[i][j] = 1;
				}
				// if black advantage
				else if(whiteMinDistance[i][j] > blackMinDistance[i][j]) {
					score -= 1.0;
					//boardControl[i][j] = 2;
				}
				// if the square can be reached, but equal distance
				else if(whiteMinDistance[i][j] != Integer.MAX_VALUE) {
					if(playerTurn == 1) {
						score += turnAdvantage;
					} else {
						score -= turnAdvantage;
					}
				}
			}
		}
		//AmazonsUtility.printBoard(boardControl);
		return new double[]{score, 2.0*c1, w};
	}
	
	// return array has 2 values
	// the first represents t2
	// the second represents c2
	private static double[] kingMinDistance(int board[][], int playerTurn) {
		int whiteMinDistance[][] = new int[10][10];
		int blackMinDistance[][] = new int[10][10];
		
		// must initialize above arrays to infinity
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				whiteMinDistance[i][j] = Integer.MAX_VALUE;
				blackMinDistance[i][j] = Integer.MAX_VALUE;
			}
		}
		
		Queue<int[]> whiteQueue = new LinkedList<>();
		Queue<int[]> blackQueue = new LinkedList<>();
		
		// get initial positions of all queens and add them  to respective queues
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				if(board[i][j] == whiteQueen) {
					whiteQueue.add(new int[]{i, j});
					whiteMinDistance[i][j] = 0;
				}
				else if(board[i][j] == blackQueen) {
					blackQueue.add(new int[]{i, j});
					blackMinDistance[i][j] = 0;
				}
			}
		}
		
		while(!whiteQueue.isEmpty()) {
			int[] whitePosition = whiteQueue.poll();
			int posY = whitePosition[0];
			int posX = whitePosition[1];
			int minDistance = whiteMinDistance[posY][posX] + 1;
			
			// up
			if(AmazonsUtility.isSpotValid(board, posY-1, posX)) {
				if(whiteMinDistance[posY-1][posX] > minDistance) {
					whiteMinDistance[posY-1][posX] = minDistance;
					whiteQueue.add(new int[]{posY-1, posX});
				}
			}
			
			// up left
			if(AmazonsUtility.isSpotValid(board, posY-1, posX-1)) {
				if(whiteMinDistance[posY-1][posX-1] > minDistance) {
					whiteMinDistance[posY-1][posX-1] = minDistance;
					whiteQueue.add(new int[]{posY-1, posX-1});
				}
			}
			
			// left
			if(AmazonsUtility.isSpotValid(board, posY, posX-1)) {
				if(whiteMinDistance[posY][posX-1] > minDistance) {
					whiteMinDistance[posY][posX-1] = minDistance;
					whiteQueue.add(new int[]{posY, posX-1});
				}
			}
			
			// down left
			if(AmazonsUtility.isSpotValid(board, posY+1, posX-1)) {
				if(whiteMinDistance[posY+1][posX-1] > minDistance) {
					whiteMinDistance[posY+1][posX-1] = minDistance;
					whiteQueue.add(new int[]{posY+1, posX-1});
				}
			}
			
			// down
			if(AmazonsUtility.isSpotValid(board, posY+1, posX)) {
				if(whiteMinDistance[posY+1][posX] > minDistance) {
					whiteMinDistance[posY+1][posX] = minDistance;
					whiteQueue.add(new int[]{posY+1, posX});
				}
			}
			
			// down right
			if(AmazonsUtility.isSpotValid(board, posY+1, posX+1)) {
				if(whiteMinDistance[posY+1][posX+1] > minDistance) {
					whiteMinDistance[posY+1][posX+1] = minDistance;
					whiteQueue.add(new int[]{posY+1, posX+1});
				}
			}
			
			// right
			if(AmazonsUtility.isSpotValid(board, posY, posX+1)) {
				if(whiteMinDistance[posY][posX+1] > minDistance) {
					whiteMinDistance[posY][posX+1] = minDistance;
					whiteQueue.add(new int[]{posY, posX+1});
				}
			}
			
			// up right
			if(AmazonsUtility.isSpotValid(board, posY-1, posX+1)) {
				if(whiteMinDistance[posY-1][posX+1] > minDistance) {
					whiteMinDistance[posY-1][posX+1] = minDistance;
					whiteQueue.add(new int[]{posY-1, posX+1});
				}
			}
		}
		
		while(!blackQueue.isEmpty()) {
			int[] blackPosition = blackQueue.poll();
			int posY = blackPosition[0];
			int posX = blackPosition[1];
			int minDistance = blackMinDistance[posY][posX] + 1;
			
			// up
			if(AmazonsUtility.isSpotValid(board, posY-1, posX)) {
				if(blackMinDistance[posY-1][posX] > minDistance) {
					blackMinDistance[posY-1][posX] = minDistance;
					blackQueue.add(new int[]{posY-1, posX});
				}
			}
			
			// up left
			if(AmazonsUtility.isSpotValid(board, posY-1, posX-1)) {
				if(blackMinDistance[posY-1][posX-1] > minDistance) {
					blackMinDistance[posY-1][posX-1] = minDistance;
					blackQueue.add(new int[]{posY-1, posX-1});
				}
			}
			
			// left
			if(AmazonsUtility.isSpotValid(board, posY, posX-1)) {
				if(blackMinDistance[posY][posX-1] > minDistance) {
					blackMinDistance[posY][posX-1] = minDistance;
					blackQueue.add(new int[]{posY, posX-1});
				}
			}
			
			// down left
			if(AmazonsUtility.isSpotValid(board, posY+1, posX-1)) {
				if(blackMinDistance[posY+1][posX-1] > minDistance) {
					blackMinDistance[posY+1][posX-1] = minDistance;
					blackQueue.add(new int[]{posY+1, posX-1});
				}
			}
			
			// down
			if(AmazonsUtility.isSpotValid(board, posY+1, posX)) {
				if(blackMinDistance[posY+1][posX] > minDistance) {
					blackMinDistance[posY+1][posX] = minDistance;
					blackQueue.add(new int[]{posY+1, posX});
				}
			}
			
			// down right
			if(AmazonsUtility.isSpotValid(board, posY+1, posX+1)) {
				if(blackMinDistance[posY+1][posX+1] > minDistance) {
					blackMinDistance[posY+1][posX+1] = minDistance;
					blackQueue.add(new int[]{posY+1, posX+1});
				}
			}
			
			// right
			if(AmazonsUtility.isSpotValid(board, posY, posX+1)) {
				if(blackMinDistance[posY][posX+1] > minDistance) {
					blackMinDistance[posY][posX+1] = minDistance;
					blackQueue.add(new int[]{posY, posX+1});
				}
			}
			
			// up right
			if(AmazonsUtility.isSpotValid(board, posY-1, posX+1)) {
				if(blackMinDistance[posY-1][posX+1] > minDistance) {
					blackMinDistance[posY-1][posX+1] = minDistance;
					blackQueue.add(new int[]{posY-1, posX+1});
				}
			}
		}
		
		// add up a score from computed minDistance values
		double score = 0.0;
		double c2 = 0.0;
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				c2 += Math.min(1, Math.max(-1, (blackMinDistance[i][j] - whiteMinDistance[i][j])/6.0));

				// if white advantage
				if(whiteMinDistance[i][j] < blackMinDistance[i][j]) {
					score += 1.0;
					//boardControl[i][j] = 1;
				}
				// if black advantage
				else if(whiteMinDistance[i][j] > blackMinDistance[i][j]) {
					score -= 1.0;
					//boardControl[i][j] = 2;
				}
				// if the square can be reached, but equal distance
				else if(whiteMinDistance[i][j] != Integer.MAX_VALUE) {
					if(playerTurn == 1) {
						score += turnAdvantage;
						//boardControl[i][j] = 3;
					} else {
						score -= turnAdvantage;
						//boardControl[i][j] = 4;
					}
				}
			}
		}
		return new double[]{score, c2};
	}
	
	private static double getMobilityEval(int[][] board, int[][] mobilityMap, double w) {
		double whiteScore = 0.0;
		double blackScore = 0.0;
		
		Queue<int[]> whiteQueue = new LinkedList<>();
		Queue<int[]> blackQueue = new LinkedList<>();
		
		// get initial positions of all queens and add them  to respective queues
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				if(board[i][j] == whiteQueen) {
					whiteQueue.add(new int[]{i, j});
				}
				else if(board[i][j] == blackQueen) {
					blackQueue.add(new int[]{i, j});
				}
			}
		}
		
		while(!whiteQueue.isEmpty()) {
			int[] whitePosition = whiteQueue.poll();
			int posY = whitePosition[0];
			int posX = whitePosition[1];
			
			double queenEval = 0.0;
			
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
				if(upValid && AmazonsUtility.isSpotValid(board, posY-i, posX)) {
					queenEval += Math.pow(2, -(i-1)) * mobilityMap[posY-i][posX];
				} else upValid = false;
				
				// up left
				if(upLeftValid && AmazonsUtility.isSpotValid(board, posY-i, posX-i)) {
					queenEval += Math.pow(2, -(i-1)) * mobilityMap[posY-i][posX-i];
				} else upLeftValid = false;
				
				// left
				if(leftValid && AmazonsUtility.isSpotValid(board, posY, posX-i)) {
					queenEval += Math.pow(2, -(i-1)) * mobilityMap[posY][posX-i];
				} else leftValid = false;
				
				// down left
				if(downLeftValid && AmazonsUtility.isSpotValid(board, posY+i, posX-i)) {
					queenEval += Math.pow(2, -(i-1)) * mobilityMap[posY+i][posX-i];
				} else downLeftValid = false;
				
				// down
				if(downValid && AmazonsUtility.isSpotValid(board, posY+i, posX)) {
					queenEval += Math.pow(2, -(i-1)) * mobilityMap[posY+i][posX];
				} else downValid = false;
				
				// down right
				if(downRightValid && AmazonsUtility.isSpotValid(board, posY+i, posX+i)) {
					queenEval += Math.pow(2, -(i-1)) * mobilityMap[posY+i][posX+i];
				} else downRightValid = false;
				
				// right
				if(rightValid && AmazonsUtility.isSpotValid(board, posY, posX+i)) {
					queenEval += Math.pow(2, -(i-1)) * mobilityMap[posY][posX+i];
				} else rightValid = false;
				
				// up right
				if(upRightValid && AmazonsUtility.isSpotValid(board, posY-i, posX+i)) {
					queenEval += Math.pow(2, -(i-1)) * mobilityMap[posY-i][posX+i];
				} else upRightValid = false;
				
				whiteScore += f5(w, queenEval);
			}
		}
		
		while(!blackQueue.isEmpty()) {
			int[] blackPosition = blackQueue.poll();
			int posY = blackPosition[0];
			int posX = blackPosition[1];
			
			double queenEval = 0.0;
			
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
				if(upValid && AmazonsUtility.isSpotValid(board, posY-i, posX)) {
					queenEval += Math.pow(2, -(i-1)) * mobilityMap[posY-i][posX];
				} else upValid = false;
				
				// up left
				if(upLeftValid && AmazonsUtility.isSpotValid(board, posY-i, posX-i)) {
					queenEval += Math.pow(2, -(i-1)) * mobilityMap[posY-i][posX-i];
				} else upLeftValid = false;
				
				// left
				if(leftValid && AmazonsUtility.isSpotValid(board, posY, posX-i)) {
					queenEval += Math.pow(2, -(i-1)) * mobilityMap[posY][posX-i];
				} else leftValid = false;
				
				// down left
				if(downLeftValid && AmazonsUtility.isSpotValid(board, posY+i, posX-i)) {
					queenEval += Math.pow(2, -(i-1)) * mobilityMap[posY+i][posX-i];
				} else downLeftValid = false;
				
				// down
				if(downValid && AmazonsUtility.isSpotValid(board, posY+i, posX)) {
					queenEval += Math.pow(2, -(i-1)) * mobilityMap[posY+i][posX];
				} else downValid = false;
				
				// down right
				if(downRightValid && AmazonsUtility.isSpotValid(board, posY+i, posX+i)) {
					queenEval += Math.pow(2, -(i-1)) * mobilityMap[posY+i][posX+i];
				} else downRightValid = false;
				
				// right
				if(rightValid && AmazonsUtility.isSpotValid(board, posY, posX+i)) {
					queenEval += Math.pow(2, -(i-1)) * mobilityMap[posY][posX+i];
				} else rightValid = false;
				
				// up right
				if(upRightValid && AmazonsUtility.isSpotValid(board, posY-i, posX+i)) {
					queenEval += Math.pow(2, -(i-1)) * mobilityMap[posY-i][posX+i];
				} else upRightValid = false;
				
				blackScore += f5(w, queenEval);
			}
		}
		
		return blackScore - whiteScore;
	}
}
