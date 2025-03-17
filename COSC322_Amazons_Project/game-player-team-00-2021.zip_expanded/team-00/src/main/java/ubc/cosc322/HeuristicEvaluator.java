package ubc.cosc322;

import java.util.LinkedList;
import java.util.Queue;

public class HeuristicEvaluator {
    // values for the two queen types
    private static int whiteQueen = 1;
    private static int blackQueen = 2;
    
    // small bonus/penalty for turn advantage
    private static double turnAdvantage = 0.15;
    
    // Returns double that represents the overall advantage (positive: white, negative: black)
    // state[0] is the board, state[1] is the mobility map
    public static double getHeuristicEval(int state[][][], int playerTurn) {
        int[][] board = state[0];
        int[][] mobilityMap = state[1];
        
        //Get metrics based on queen distances:
        double[] queenMetrics = calculateQueenDistanceMetrics(board, playerTurn);
        double t1 = queenMetrics[0];
        double c1 = queenMetrics[1];
        double diffWeight = queenMetrics[2];
        
        double t2 = 0.0, c2 = 0.0;
        double mobilityEval = 0.0;
        
        // Only use king distances if the difference measure is high enough
        if(diffWeight > 10.0) {
            double[] kingMetrics = calculateKingDistanceMetrics(board, playerTurn);
            t2 = kingMetrics[0];
            c2 = kingMetrics[1];
            mobilityEval = evaluateMobility(board, mobilityMap, diffWeight);
        } else {
            diffWeight = 0.0;
        }
        
        //combining different evaluation metrics
        double territoryEval = f1(diffWeight) * t1 + f2(diffWeight) * c1 
                              + f3(diffWeight) * t2 + f4(diffWeight) * c2;
        return territoryEval + mobilityEval;
    }
    
    //weighting functions fir evaluation – adjust contribution based on diffWeight
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
        return w * Math.pow(1.2, -mobility) / 45.0;
    }
    
    //computes distance-based metrics using queen positions
    // Returns an array with: [score (white minus black), 2*c1, diffWeight]
    private static double[] calculateQueenDistanceMetrics(int board[][], int playerTurn) {
        int[][] whiteDistances = new int[10][10];
        int[][] blackDistances = new int[10][10];
        
        // Setting all distances to infinity initially
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                whiteDistances[i][j] = Integer.MAX_VALUE;
                blackDistances[i][j] = Integer.MAX_VALUE;
            }
        }
        
        Queue<int[]> whiteQueue = new LinkedList<>();
        Queue<int[]> blackQueue = new LinkedList<>();
        
        // Finding the initial queen positions and initialize distances
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if(board[i][j] == whiteQueen) {
                    whiteQueue.add(new int[]{i, j});
                    whiteDistances[i][j] = 0;
                } else if(board[i][j] == blackQueen) {
                    blackQueue.add(new int[]{i, j});
                    blackDistances[i][j] = 0;
                }
            }
        }
        
        // Propagate distances for white queens
        while(!whiteQueue.isEmpty()) {
            int[] pos = whiteQueue.poll();
            int posY = pos[0], posX = pos[1];
            int nextDist = whiteDistances[posY][posX] + 1;
            
            boolean up = true, upLeft = true, left = true, downLeft = true;
            boolean down = true, downRight = true, right = true, upRight = true;
            for (int d = 1; d < 10; d++) {
                // up
                if(up && AmazonsUtility.isSpotValid(board, posY - d, posX)) {
                    if(whiteDistances[posY - d][posX] > nextDist) {
                        whiteDistances[posY - d][posX] = nextDist;
                        whiteQueue.add(new int[]{posY - d, posX});
                    }
                } else up = false;
                
                // up-left
                if(upLeft && AmazonsUtility.isSpotValid(board, posY - d, posX - d)) {
                    if(whiteDistances[posY - d][posX - d] > nextDist) {
                        whiteDistances[posY - d][posX - d] = nextDist;
                        whiteQueue.add(new int[]{posY - d, posX - d});
                    }
                } else upLeft = false;
                
                // left
                if(left && AmazonsUtility.isSpotValid(board, posY, posX - d)) {
                    if(whiteDistances[posY][posX - d] > nextDist) {
                        whiteDistances[posY][posX - d] = nextDist;
                        whiteQueue.add(new int[]{posY, posX - d});
                    }
                } else left = false;
                
                // down-left
                if(downLeft && AmazonsUtility.isSpotValid(board, posY + d, posX - d)) {
                    if(whiteDistances[posY + d][posX - d] > nextDist) {
                        whiteDistances[posY + d][posX - d] = nextDist;
                        whiteQueue.add(new int[]{posY + d, posX - d});
                    }
                } else downLeft = false;
                
                // down
                if(down && AmazonsUtility.isSpotValid(board, posY + d, posX)) {
                    if(whiteDistances[posY + d][posX] > nextDist) {
                        whiteDistances[posY + d][posX] = nextDist;
                        whiteQueue.add(new int[]{posY + d, posX});
                    }
                } else down = false;
                
                // down-right
                if(downRight && AmazonsUtility.isSpotValid(board, posY + d, posX + d)) {
                    if(whiteDistances[posY + d][posX + d] > nextDist) {
                        whiteDistances[posY + d][posX + d] = nextDist;
                        whiteQueue.add(new int[]{posY + d, posX + d});
                    }
                } else downRight = false;
                
                // righty
                if(right && AmazonsUtility.isSpotValid(board, posY, posX + d)) {
                    if(whiteDistances[posY][posX + d] > nextDist) {
                        whiteDistances[posY][posX + d] = nextDist;
                        whiteQueue.add(new int[]{posY, posX + d});
                    }
                } else right = false;
                
                // up-right
                if(upRight && AmazonsUtility.isSpotValid(board, posY - d, posX + d)) {
                    if(whiteDistances[posY - d][posX + d] > nextDist) {
                        whiteDistances[posY - d][posX + d] = nextDist;
                        whiteQueue.add(new int[]{posY - d, posX + d});
                    }
                } else upRight = false;
            }
        }
        
        // Propagate distances for black queens (logic is essentailly the same lol)
        while(!blackQueue.isEmpty()) {
            int[] pos = blackQueue.poll();
            int posY = pos[0], posX = pos[1];
            int nextDist = blackDistances[posY][posX] + 1;
            
            boolean up = true, upLeft = true, left = true, downLeft = true;
            boolean down = true, downRight = true, right = true, upRight = true;
            for (int d = 1; d < 10; d++) {
                if(up && AmazonsUtility.isSpotValid(board, posY - d, posX)) {
                    if(blackDistances[posY - d][posX] > nextDist) {
                        blackDistances[posY - d][posX] = nextDist;
                        blackQueue.add(new int[]{posY - d, posX});
                    }
                } else up = false;
                
                if(upLeft && AmazonsUtility.isSpotValid(board, posY - d, posX - d)) {
                    if(blackDistances[posY - d][posX - d] > nextDist) {
                        blackDistances[posY - d][posX - d] = nextDist;
                        blackQueue.add(new int[]{posY - d, posX - d});
                    }
                } else upLeft = false;
                
                if(left && AmazonsUtility.isSpotValid(board, posY, posX - d)) {
                    if(blackDistances[posY][posX - d] > nextDist) {
                        blackDistances[posY][posX - d] = nextDist;
                        blackQueue.add(new int[]{posY, posX - d});
                    }
                } else left = false;
                
                if(downLeft && AmazonsUtility.isSpotValid(board, posY + d, posX - d)) {
                    if(blackDistances[posY + d][posX - d] > nextDist) {
                        blackDistances[posY + d][posX - d] = nextDist;
                        blackQueue.add(new int[]{posY + d, posX - d});
                    }
                } else downLeft = false;
                
                if(down && AmazonsUtility.isSpotValid(board, posY + d, posX)) {
                    if(blackDistances[posY + d][posX] > nextDist) {
                        blackDistances[posY + d][posX] = nextDist;
                        blackQueue.add(new int[]{posY + d, posX});
                    }
                } else down = false;
                
                if(downRight && AmazonsUtility.isSpotValid(board, posY + d, posX + d)) {
                    if(blackDistances[posY + d][posX + d] > nextDist) {
                        blackDistances[posY + d][posX + d] = nextDist;
                        blackQueue.add(new int[]{posY + d, posX + d});
                    }
                } else downRight = false;
                
                if(right && AmazonsUtility.isSpotValid(board, posY, posX + d)) {
                    if(blackDistances[posY][posX + d] > nextDist) {
                        blackDistances[posY][posX + d] = nextDist;
                        blackQueue.add(new int[]{posY, posX + d});
                    }
                } else right = false;
                
                if(upRight && AmazonsUtility.isSpotValid(board, posY - d, posX + d)) {
                    if(blackDistances[posY - d][posX + d] > nextDist) {
                        blackDistances[posY - d][posX + d] = nextDist;
                        blackQueue.add(new int[]{posY - d, posX + d});
                    }
                } else upRight = false;
            }
        }
        
        // add up all the scores based on the distances computed.
        double score = 0.0;
        double controlScore = 0.0;
        double diffWeight = 0.0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                controlScore += Math.pow(2, -whiteDistances[i][j]) - Math.pow(2, -blackDistances[i][j]);
                if(whiteDistances[i][j] != Integer.MAX_VALUE && blackDistances[i][j] != Integer.MAX_VALUE) {
                    diffWeight += Math.pow(2, -Math.abs(whiteDistances[i][j] - blackDistances[i][j]));
                }
                if(whiteDistances[i][j] < blackDistances[i][j]) {
                    score += 1.0;
                } else if(whiteDistances[i][j] > blackDistances[i][j]) {
                    score -= 1.0;
                } else if(whiteDistances[i][j] != Integer.MAX_VALUE) {
                    score += (playerTurn == 1 ? turnAdvantage : -turnAdvantage);
                }
            }
        }
        return new double[]{score, 2.0 * controlScore, diffWeight};
    }
    
    // Computes distance metrics based on a simpler directional search for king evaluation
    // Returns an array with: [score (white minus black), c2]
    private static double[] calculateKingDistanceMetrics(int board[][], int playerTurn) {
        int[][] whiteDistances = new int[10][10];
        int[][] blackDistances = new int[10][10];
        
        // Initialize our distances to infinity
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                whiteDistances[i][j] = Integer.MAX_VALUE;
                blackDistances[i][j] = Integer.MAX_VALUE;
            }
        }
        
        Queue<int[]> whiteQueue = new LinkedList<>();
        Queue<int[]> blackQueue = new LinkedList<>();
        
        // Set initial positions for each queen
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if(board[i][j] == whiteQueen) {
                    whiteQueue.add(new int[]{i, j});
                    whiteDistances[i][j] = 0;
                } else if(board[i][j] == blackQueen) {
                    blackQueue.add(new int[]{i, j});
                    blackDistances[i][j] = 0;
                }
            }
        }
        
        // Propagate white distances using individual directional checks
        while(!whiteQueue.isEmpty()) {
            int[] pos = whiteQueue.poll();
            int posY = pos[0], posX = pos[1];
            int nextDist = whiteDistances[posY][posX] + 1;
            
            if(AmazonsUtility.isSpotValid(board, posY - 1, posX) && whiteDistances[posY - 1][posX] > nextDist) {
                whiteDistances[posY - 1][posX] = nextDist;
                whiteQueue.add(new int[]{posY - 1, posX});
            }
            if(AmazonsUtility.isSpotValid(board, posY - 1, posX - 1) && whiteDistances[posY - 1][posX - 1] > nextDist) {
                whiteDistances[posY - 1][posX - 1] = nextDist;
                whiteQueue.add(new int[]{posY - 1, posX - 1});
            }
            if(AmazonsUtility.isSpotValid(board, posY, posX - 1) && whiteDistances[posY][posX - 1] > nextDist) {
                whiteDistances[posY][posX - 1] = nextDist;
                whiteQueue.add(new int[]{posY, posX - 1});
            }
            if(AmazonsUtility.isSpotValid(board, posY + 1, posX - 1) && whiteDistances[posY + 1][posX - 1] > nextDist) {
                whiteDistances[posY + 1][posX - 1] = nextDist;
                whiteQueue.add(new int[]{posY + 1, posX - 1});
            }
            if(AmazonsUtility.isSpotValid(board, posY + 1, posX) && whiteDistances[posY + 1][posX] > nextDist) {
                whiteDistances[posY + 1][posX] = nextDist;
                whiteQueue.add(new int[]{posY + 1, posX});
            }
            if(AmazonsUtility.isSpotValid(board, posY + 1, posX + 1) && whiteDistances[posY + 1][posX + 1] > nextDist) {
                whiteDistances[posY + 1][posX + 1] = nextDist;
                whiteQueue.add(new int[]{posY + 1, posX + 1});
            }
            if(AmazonsUtility.isSpotValid(board, posY, posX + 1) && whiteDistances[posY][posX + 1] > nextDist) {
                whiteDistances[posY][posX + 1] = nextDist;
                whiteQueue.add(new int[]{posY, posX + 1});
            }
            if(AmazonsUtility.isSpotValid(board, posY - 1, posX + 1) && whiteDistances[posY - 1][posX + 1] > nextDist) {
                whiteDistances[posY - 1][posX + 1] = nextDist;
                whiteQueue.add(new int[]{posY - 1, posX + 1});
            }
        }
        
        // Propagate black distances, same as white essentially
        while(!blackQueue.isEmpty()) {
            int[] pos = blackQueue.poll();
            int posY = pos[0], posX = pos[1];
            int nextDist = blackDistances[posY][posX] + 1;
            
            if(AmazonsUtility.isSpotValid(board, posY - 1, posX) && blackDistances[posY - 1][posX] > nextDist) {
                blackDistances[posY - 1][posX] = nextDist;
                blackQueue.add(new int[]{posY - 1, posX});
            }
            if(AmazonsUtility.isSpotValid(board, posY - 1, posX - 1) && blackDistances[posY - 1][posX - 1] > nextDist) {
                blackDistances[posY - 1][posX - 1] = nextDist;
                blackQueue.add(new int[]{posY - 1, posX - 1});
            }
            if(AmazonsUtility.isSpotValid(board, posY, posX - 1) && blackDistances[posY][posX - 1] > nextDist) {
                blackDistances[posY][posX - 1] = nextDist;
                blackQueue.add(new int[]{posY, posX - 1});
            }
            if(AmazonsUtility.isSpotValid(board, posY + 1, posX - 1) && blackDistances[posY + 1][posX - 1] > nextDist) {
                blackDistances[posY + 1][posX - 1] = nextDist;
                blackQueue.add(new int[]{posY + 1, posX - 1});
            }
            if(AmazonsUtility.isSpotValid(board, posY + 1, posX) && blackDistances[posY + 1][posX] > nextDist) {
                blackDistances[posY + 1][posX] = nextDist;
                blackQueue.add(new int[]{posY + 1, posX});
            }
            if(AmazonsUtility.isSpotValid(board, posY + 1, posX + 1) && blackDistances[posY + 1][posX + 1] > nextDist) {
                blackDistances[posY + 1][posX + 1] = nextDist;
                blackQueue.add(new int[]{posY + 1, posX + 1});
            }
            if(AmazonsUtility.isSpotValid(board, posY, posX + 1) && blackDistances[posY][posX + 1] > nextDist) {
                blackDistances[posY][posX + 1] = nextDist;
                blackQueue.add(new int[]{posY, posX + 1});
            }
            if(AmazonsUtility.isSpotValid(board, posY - 1, posX + 1) && blackDistances[posY - 1][posX + 1] > nextDist) {
                blackDistances[posY - 1][posX + 1] = nextDist;
                blackQueue.add(new int[]{posY - 1, posX + 1});
            }
        }
        
        double score = 0.0;
        double controlMetric = 0.0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                controlMetric += Math.min(1, Math.max(-1, (blackDistances[i][j] - whiteDistances[i][j]) / 6.0));
                if(whiteDistances[i][j] < blackDistances[i][j]) {
                    score += 1.0;
                } else if(whiteDistances[i][j] > blackDistances[i][j]) {
                    score -= 1.0;
                } else if(whiteDistances[i][j] != Integer.MAX_VALUE) {
                    score += (playerTurn == 1 ? turnAdvantage : -turnAdvantage);
                }
            }
        }
        return new double[]{score, controlMetric};
    }
    
    // Evaluates queen mobility by summing weighted contributions along each direction
    private static double evaluateMobility(int[][] board, int[][] mobilityMap, double w) {
        double whiteScore = 0.0;
        double blackScore = 0.0;
        
        Queue<int[]> whiteQueue = new LinkedList<>();
        Queue<int[]> blackQueue = new LinkedList<>();
        
        //add queen positions to their respective queues
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if(board[i][j] == whiteQueen) {
                    whiteQueue.add(new int[]{i, j});
                } else if(board[i][j] == blackQueen) {
                    blackQueue.add(new int[]{i, j});
                }
            }
        }
        
        // evaluate mobility for the white queens
        while(!whiteQueue.isEmpty()) {
            int[] pos = whiteQueue.poll();
            int posY = pos[0], posX = pos[1];
            double queenEval = 0.0;
            
            boolean up = true, upLeft = true, left = true, downLeft = true;
            boolean down = true, downRight = true, right = true, upRight = true;
            for (int d = 1; d < 10; d++) {
                if(up && AmazonsUtility.isSpotValid(board, posY - d, posX)) {
                    queenEval += Math.pow(2, -(d - 1)) * mobilityMap[posY - d][posX];
                } else up = false;
                
                if(upLeft && AmazonsUtility.isSpotValid(board, posY - d, posX - d)) {
                    queenEval += Math.pow(2, -(d - 1)) * mobilityMap[posY - d][posX - d];
                } else upLeft = false;
                
                if(left && AmazonsUtility.isSpotValid(board, posY, posX - d)) {
                    queenEval += Math.pow(2, -(d - 1)) * mobilityMap[posY][posX - d];
                } else left = false;
                
                if(downLeft && AmazonsUtility.isSpotValid(board, posY + d, posX - d)) {
                    queenEval += Math.pow(2, -(d - 1)) * mobilityMap[posY + d][posX - d];
                } else downLeft = false;
                
                if(down && AmazonsUtility.isSpotValid(board, posY + d, posX)) {
                    queenEval += Math.pow(2, -(d - 1)) * mobilityMap[posY + d][posX];
                } else down = false;
                
                if(downRight && AmazonsUtility.isSpotValid(board, posY + d, posX + d)) {
                    queenEval += Math.pow(2, -(d - 1)) * mobilityMap[posY + d][posX + d];
                } else downRight = false;
                
                if(right && AmazonsUtility.isSpotValid(board, posY, posX + d)) {
                    queenEval += Math.pow(2, -(d - 1)) * mobilityMap[posY][posX + d];
                } else right = false;
                
                if(upRight && AmazonsUtility.isSpotValid(board, posY - d, posX + d)) {
                    queenEval += Math.pow(2, -(d - 1)) * mobilityMap[posY - d][posX + d];
                } else upRight = false;
            }
            whiteScore += f5(w, queenEval);
        }
        
        // and now mobility for black queens
        while(!blackQueue.isEmpty()) {
            int[] pos = blackQueue.poll();
            int posY = pos[0], posX = pos[1];
            double queenEval = 0.0;
            
            boolean up = true, upLeft = true, left = true, downLeft = true;
            boolean down = true, downRight = true, right = true, upRight = true;
            for (int d = 1; d < 10; d++) {
                if(up && AmazonsUtility.isSpotValid(board, posY - d, posX)) {
                    queenEval += Math.pow(2, -(d - 1)) * mobilityMap[posY - d][posX];
                } else up = false;
                
                if(upLeft && AmazonsUtility.isSpotValid(board, posY - d, posX - d)) {
                    queenEval += Math.pow(2, -(d - 1)) * mobilityMap[posY - d][posX - d];
                } else upLeft = false;
                
                if(left && AmazonsUtility.isSpotValid(board, posY, posX - d)) {
                    queenEval += Math.pow(2, -(d - 1)) * mobilityMap[posY][posX - d];
                } else left = false;
                
                if(downLeft && AmazonsUtility.isSpotValid(board, posY + d, posX - d)) {
                    queenEval += Math.pow(2, -(d - 1)) * mobilityMap[posY + d][posX - d];
                } else downLeft = false;
                
                if(down && AmazonsUtility.isSpotValid(board, posY + d, posX)) {
                    queenEval += Math.pow(2, -(d - 1)) * mobilityMap[posY + d][posX];
                } else down = false;
                
                if(downRight && AmazonsUtility.isSpotValid(board, posY + d, posX + d)) {
                    queenEval += Math.pow(2, -(d - 1)) * mobilityMap[posY + d][posX + d];
                } else downRight = false;
                
                if(right && AmazonsUtility.isSpotValid(board, posY, posX + d)) {
                    queenEval += Math.pow(2, -(d - 1)) * mobilityMap[posY][posX + d];
                } else right = false;
                
                if(upRight && AmazonsUtility.isSpotValid(board, posY - d, posX + d)) {
                    queenEval += Math.pow(2, -(d - 1)) * mobilityMap[posY - d][posX + d];
                } else upRight = false;
            }
            blackScore += f5(w, queenEval);
        }
        return blackScore - whiteScore;
    }
}