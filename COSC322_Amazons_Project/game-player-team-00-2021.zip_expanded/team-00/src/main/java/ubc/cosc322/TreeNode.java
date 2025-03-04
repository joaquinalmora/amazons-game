package ubc.cosc322;

import java.util.ArrayList;

public class TreeNode {	
		public static int maxDepth = 0;
		int depth;
	
		int color;
		double Q;
		int N;
		int[][][] boardState;
		AmazonsAction action; 	//action taken to reach node (used to return best action after MCTS)
		TreeNode parent;
		ArrayList<TreeNode> children;
		ArrayList<AmazonsAction> possibleActions;
		boolean expanded;
		boolean actionsGenerated;
		
		//this constructor will be used to create the root node of the Monte-Carlo tree search 
		//2 will be passed in for int N, because we can't have an unvisited root node without 
		//creating an error with the UCB formula (we would divide by 0) and we don't want the second term of UCB to be 0
		TreeNode(int[][][] boardState, int color){
			this.children = new ArrayList<>();
			this.action = null;
			this.boardState = boardState;
			this.parent = null;
			this.color = color;
			this.N = 0;
			this.Q = 0;
			this.expanded = false;
			this.actionsGenerated = false;
			this.depth = 0;
		}
		
		//this constructor will be used for creating child nodes
		TreeNode(int[][][] boardState, TreeNode parent, AmazonsAction action){
			this.boardState = boardState;
			this.parent = parent;
			if(parent.color == 2) {
				this.color = 1;
			} else {
				this.color = 2;
			}
			this.N = 0;
			this.Q = 0;
			this.expanded = false;
			this.actionsGenerated = false;
			this.children = new ArrayList<>();
			this.action = action;
			this.depth = parent.depth + 1;
			if(this.depth > maxDepth) {
				maxDepth = this.depth;
				System.out.println("Depth: " + maxDepth);
			}
		}
		
		//this constructor to be used to make copies of nodes for roll out purposes
		TreeNode(TreeNode copyNode) {
			this.boardState = copyNode.boardState;
			this.possibleActions = copyNode.possibleActions;
			this.color = copyNode.color;
			this.expanded = false;
			this.actionsGenerated = copyNode.actionsGenerated;
			this.children = new ArrayList<>();
		}
		
		public double getUCB(double eC) {
			//if this node hasn't been visited yet, N=0, and the second term of the UCB will be infinity, so we just return Double.MAX_VALUE to avoid errors
			if(N==0) return 10000.0;
			//otherwise return the UCB formula
			return Q/N + eC*Math.sqrt(Math.log(parent.N)/N);
		}
		
		//generate a child node given a subState of the current node's state and the action taken to get there
		public TreeNode generateChild(AmazonsAction a) {
			//int[][][] subState = AmazonsAction.applyAction(a, this.boardState);
			//TreeNode child = new TreeNode(AmazonsAction.applyAction(a, this.boardState), this, a);
			TreeNode child = new TreeNode(AmazonsAction.applyAction(a, this.boardState), this, a);
			this.children.add(child);
			return child;
		}
		
		private void generateActions() {
			this.possibleActions = AmazonsActionFactory.getActions(this.boardState, this.color);
			this.actionsGenerated = true;
		}
		
		//expands the current node
		public void expand() {
			if(!this.actionsGenerated) {
				this.generateActions();
			}
			//create a list of all possible subStates from the current node's state
			for(AmazonsAction a: this.possibleActions) {
				this.children.add(new TreeNode(AmazonsAction.applyAction(a, this.boardState), this, a));
				//generateChild(a);
			}
			this.possibleActions.clear();
			this.expanded = true;
		}
		
		//expands a specific action for roll out traversal
		public TreeNode expandAtRandom() {
			if(!this.actionsGenerated) {
				this.generateActions();
			}
			int index = (int)(Math.random() * this.getNumPossibleActions());
			AmazonsAction action = this.possibleActions.get(index);
			this.possibleActions.remove(index);
			if(this.possibleActions.isEmpty()) {
				this.expanded = true;
			}
			return generateChild(action);
		}
		
		//checks if a node is terminal
		public boolean isTerminal() {
			if(!this.actionsGenerated) {
				this.generateActions();
			}
			return this.possibleActions.isEmpty() && this.children.isEmpty();
		}
		
		public boolean hasUnexpandedChildren() {
			if(!this.actionsGenerated) {
				this.generateActions();
			}
			return !this.possibleActions.isEmpty();
		}
		
		public boolean hasExpandedChildren() {
			return !this.children.isEmpty();
		}
		
		public int getColor() {
			return this.color;
		}
		
		public int getNumPossibleActions() {
			if(!this.actionsGenerated) {
				this.generateActions();
			}
			return this.possibleActions.size();
		}
		
		public void printBoard() {
			AmazonsUtility.printBoard(this.boardState[0]);
		}
}
