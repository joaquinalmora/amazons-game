package ubc.cosc322;

import java.util.LinkedList;
import java.util.Queue;

public class HeuristicEvaluator {
    // Constants for queen types
    private static final int whiteQueen = 1;
    private static final int blackQueen = 2;

    // Small bonus/penalty for turn advantage
    private static final double turnAdvantage = 0.15;

    // Direction vectors for movement (up, down, left, right, diagonals)
    private static final int[][] DIRECTIONS = {
        {-1, 0}, {-1, -1}, {0, -1}, {1, -1},
        {1, 0}, {1, 1}, {0, 1}, {-1, 1}
    };

    
     // Main heuristic evaluation function.
    public static double getHeuristicEval(int[][][] state, int playerTurn) {
        // The game state: state[0] is the board, state[1] is the mobility map.
        int[][] board = state[0];
        int[][] mobilityMap = state[1];

        // Get metrics based on queen distances
        double[] queenMetrics = calculateDistanceMetrics(board, playerTurn, true); The current player's turn (1 for white, 2 for black).
        double t1 = queenMetrics[0];
        double c1 = queenMetrics[1];
        double diffWeight = queenMetrics[2];

        double t2 = 0.0, c2 = 0.0, mobilityEval = 0.0;

        // Only use king distances if the difference measure is high enough
        if (diffWeight > 10.0) {
            double[] kingMetrics = calculateDistanceMetrics(board, playerTurn, false);
            t2 = kingMetrics[0];
            c2 = kingMetrics[1];
            mobilityEval = evaluateMobility(board, mobilityMap, diffWeight);
        } else {
            diffWeight = 0.0;
        }

        // Combine different evaluation metrics
        double territoryEval = f1(diffWeight) * t1 + f2(diffWeight) * c1
                             + f3(diffWeight) * t2 + f4(diffWeight) * c2;
        return territoryEval + mobilityEval; // A double representing the heuristic evaluation (positive: white advantage, negative: black advantage).
    }

    // Weighting functions for evaluation – adjust contribution based on diffWeight
    private static double f1(double w) { return (100.0 - w) / 100.0; }
    private static double f2(double w) { return (1.0 - f1(w)) / 4.0; }
    private static double f3(double w) { return (1.0 - f1(w)) / 4.0; }
    private static double f4(double w) { return (1.0 - f1(w)) / 4.0; }
    private static double f5(double w, double mobility) {
        return w * Math.pow(1.2, -mobility) / 45.0;
    }


    private static double[] calculateDistanceMetrics(int[][] board, int playerTurn, boolean isQueen) {
        int[][] whiteDistances = initializeDistances();
        int[][] blackDistances = initializeDistances();

        propagateDistances(board, whiteDistances, whiteQueen, isQueen);
        propagateDistances(board, blackDistances, blackQueen, isQueen);

        double score = 0.0, controlScore = 0.0, diffWeight = 0.0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                controlScore += Math.pow(2, -whiteDistances[i][j]) - Math.pow(2, -blackDistances[i][j]);
                if (whiteDistances[i][j] != Integer.MAX_VALUE && blackDistances[i][j] != Integer.MAX_VALUE) {
                    diffWeight += Math.pow(2, -Math.abs(whiteDistances[i][j] - blackDistances[i][j]));
                }
                if (whiteDistances[i][j] < blackDistances[i][j]) {
                    score += 1.0;
                } else if (whiteDistances[i][j] > blackDistances[i][j]) {
                    score -= 1.0;
                } else if (whiteDistances[i][j] != Integer.MAX_VALUE) {
                    score += (playerTurn == 1 ? turnAdvantage : -turnAdvantage);
                }
            }
        }
        return new double[]{score, 2.0 * controlScore, diffWeight};
    }

     // Initializes a distance array with maximum values.
     * @return A 10x10 array filled with Integer.MAX_VALUE.
     */
    private static int[][] initializeDistances() {
        int[][] distances = new int[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                distances[i][j] = Integer.MAX_VALUE;
            }
        }
        return distances;
    }

    /**
     * Propagates distances from queens or kings across the board.
     * @param board The game board.
     * @param distances The distance array to update.
     * @param pieceType The type of piece (whiteQueen or blackQueen).
     * @param isQueen True if propagating for queens, false for kings.
     */
    private static void propagateDistances(int[][] board, int[][] distances, int pieceType, boolean isQueen) {
        Queue<int[]> queue = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (board[i][j] == pieceType) {
                    queue.add(new int[]{i, j});
                    distances[i][j] = 0;
                }
            }
        }

        while (!queue.isEmpty()) {
            int[] pos = queue.poll();
            int posY = pos[0], posX = pos[1];
            int nextDist = distances[posY][posX] + 1;

            for (int[] dir : DIRECTIONS) {
                for (int d = 1; d < 10; d++) {
                    int newY = posY + dir[0] * d, newX = posX + dir[1] * d;
                    if (AmazonsUtility.isSpotValid(board, newY, newX) && distances[newY][newX] > nextDist) {
                        distances[newY][newX] = nextDist;
                        queue.add(new int[]{newY, newX});
                    }
                    if (!isQueen) break; // Stop after one step for kings
                }
            }
        }
    }

    /**
     * Evaluates queen mobility by summing weighted contributions along each direction.
     * @param board The game board.
     * @param mobilityMap The mobility map.
     * @param w The weight factor.
     * @return The mobility evaluation score.
     */
    private static double evaluateMobility(int[][] board, int[][] mobilityMap, double w) {
        return evaluateMobilityForType(board, mobilityMap, whiteQueen, w)
             - evaluateMobilityForType(board, mobilityMap, blackQueen, w);
    }

    /**
     * Evaluates mobility for a specific queen type.
     * @param board The game board.
     * @param mobilityMap The mobility map.
     * @param pieceType The type of piece (whiteQueen or blackQueen).
     * @param w The weight factor.
     * @return The mobility score for the given piece type.
     */
    private static double evaluateMobilityForType(int[][] board, int[][] mobilityMap, int pieceType, double w) {
        Queue<int[]> queue = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (board[i][j] == pieceType) {
                    queue.add(new int[]{i, j});
                }
            }
        }

        double score = 0.0;
        while (!queue.isEmpty()) {
            int[] pos = queue.poll();
            int posY = pos[0], posX = pos[1];
            double queenEval = 0.0;

            for (int[] dir : DIRECTIONS) {
                for (int d = 1; d < 10; d++) {
                    int newY = posY + dir[0] * d, newX = posX + dir[1] * d;
                    if (AmazonsUtility.isSpotValid(board, newY, newX)) {
                        queenEval += Math.pow(2, -(d - 1)) * mobilityMap[newY][newX];
                    } else {
                        break;
                    }
                }
            }
            score += f5(w, queenEval);
        }
        return score;
    }
}
