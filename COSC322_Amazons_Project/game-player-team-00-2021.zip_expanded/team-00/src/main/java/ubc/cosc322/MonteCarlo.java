package ubc.cosc322;

import java.util.ArrayList;

public class MonteCarlo {	
	long allowedTimeMs;
	TreeNode root;
	double explorationCoefficient;
	
//	public static void main(String[] args) {
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
//		int[][] mobilityMap = AmazonsUtility.getMobilityMap(defaultBoard);
//		int[][][] state = new int[][][] {defaultBoard, mobilityMap};
//		int currentPlayer = 1;
//		
//		TreeNode currentState = new TreeNode(state, currentPlayer);
//		MonteCarlo mc = null;
//		while(!currentState.isTerminal()) {
//			
//			System.out.println("Current heuristic value: " + HeuristicEvaluator.getHeuristicEval(currentState.boardState, currentState.getColor()));
//			//System.out.println("Current heuristic value 2: " + HeuristicEvaluator.getHeuristicEval2(currentState.boardState, currentState.getColor()));
//			//double w = HeuristicEvaluator.queenMinDistance(currentState.boardState[0], currentState.getColor())[2];
//			//System.out.println("Current w value: " + w);
//			System.out.println(currentState.getColor() + " to move");
//			currentState.printBoard();
//			TreeNode.maxDepth = 0;
//			mc = new MonteCarlo(currentState, 30000, 1.4);
//			AmazonsAction action = mc.MCTS();
//			state = AmazonsAction.applyAction(action, state);
//			if(currentPlayer == 1) currentPlayer = 2;
//			else currentPlayer = 1;
//			currentState = new TreeNode(state, currentPlayer);
//		}
//		AmazonsUtility.printBoard(state[0]);
//		System.out.println("Player " + (3-currentPlayer) + " wins!");
//	}
	
	MonteCarlo(TreeNode root, long allowedTimeMs, double explorationCoefficient){
		this.root = root;
		this.allowedTimeMs = allowedTimeMs;
		this.explorationCoefficient = explorationCoefficient;
	}
	
	// performs an MCTS from the current root and returns the best action
	public AmazonsAction MCTS() {
		TreeNode.maxDepth = 0;
		long currentTime = System.currentTimeMillis();
		int iterations = 0;
		for(long startTime = System.currentTimeMillis(); currentTime - startTime < allowedTimeMs; currentTime = System.currentTimeMillis()) {
			TreeNode leaf = traverse(root);
			double result;
			if(leaf.isTerminal()) {
				result = 1;
			} else {
				leaf = leaf.expandAtRandom();
				result = heuristicRollout(leaf);
			}
			iterations++;
			backpropogate(leaf, result);
		}
		System.out.println(iterations + " iterations were run");

		// returns an action based on child with highest winrate
		AmazonsAction bestAction = null;
		double bestWinrate = -10000;
		for (TreeNode t : root.children) {
			double winrate = 0;
			double Q = t.Q;
			double N = t.N;
			if (N != 0) {
				winrate = Q / N;
			}
			if (winrate > bestWinrate) {
				bestWinrate = winrate;
				bestAction = t.action;
			}
		}
		return bestAction;
	}

	public TreeNode traverse(TreeNode node) {
		// if the node is not a leaf node, traverse to its best child
		if (!node.hasUnexpandedChildren() && node.hasExpandedChildren()) {
			// get the child with the highest UCB score
			double maxUCB = -1; // all UCB values will be >=0
			TreeNode bestChild = null;
			for (TreeNode n : node.children) {
				double currentUCB = n.getUCB(explorationCoefficient);
				if (currentUCB > maxUCB) {
					maxUCB = currentUCB;
					bestChild = n;
				}
			}
			// traverse recursively
			return traverse(bestChild);
		}
		return node;
	}
	
	//playerColor is the color of the player we desire to win, so the root node's color will be passed in
	public int rollout(TreeNode start) {
		TreeNode currentNode = new TreeNode(start); //copy start node
		while(true) {	
			if(currentNode.isTerminal()) { //if a node is terminal, the current color loses
				if(currentNode.getColor() == start.getColor()) return 0;
				else return 1;
			}
			
			//expand that child and continue looping
			currentNode = currentNode.expandAtRandom();
		}
	}
	
	public double heuristicRollout(TreeNode node) {
		double heuristicResult = HeuristicEvaluator.getHeuristicEval(node.boardState, node.getColor());
		double result = AmazonsUtility.sigmoid(heuristicResult);
		
		if(node.getColor() == 1) {
			return 1 - result;
		} else {
			return result;
		}
	}
	
	public void backpropogate(TreeNode leaf, double result) {
		leaf.N++; leaf.Q += result;
		if(leaf.parent != null) {
			backpropogate(leaf.parent, 1 - result);
		}
	}
	
	public void rootFromAction(AmazonsAction a) {
		this.root.expand();
		boolean found = false;
		for(TreeNode n: root.children) {
			if(n.action.isEqual(a)) {
				root = n;
				root.parent = null;
				found = true;
				break;
			}
		}
		if(!found) {
			//RECORD THE ILLEGAL MOVE STUFF
			int newColor;
			if(root.color == 2) {
				newColor = 1;
			} else {
				newColor = 2;
			}
			int[][][] postCheatState = AmazonsAction.applyAction(a, root.boardState);
			root = new TreeNode(postCheatState, newColor);
		}

	}
	
}
