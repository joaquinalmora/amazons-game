package ubc.cosc322;

public class AmazonsUtility {
	
	public static int[][] getMobilityMap(int[][] board) {
		int[][] mobilityMap = new int[10][10];
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				mobilityMap[i][j] = 0;
				
				// up
				if(isSpotValid(board, i-1, j)) {
					mobilityMap[i][j]++;
				}
				
				// up left
				if(isSpotValid(board, i-1, j-1)) {
					mobilityMap[i][j]++;
				}
				
				// left
				if(isSpotValid(board, i, j-1)) {
					mobilityMap[i][j]++;
				}
				
				// down left
				if(isSpotValid(board, i+1, j-1)) {
					mobilityMap[i][j]++;
				}
				
				// down
				if(isSpotValid(board, i+1, j)) {
					mobilityMap[i][j]++;
				}
				
				// down right
				if(isSpotValid(board, i+1, j+1)) {
					mobilityMap[i][j]++;
				}
				
				// right
				if(isSpotValid(board, i, j+1)) {
					mobilityMap[i][j]++;
				}
				
				// up right
				if(isSpotValid(board, i-1, j+1)) {
					mobilityMap[i][j]++;
				}
			}
		}
		return mobilityMap;
	}
	
	public static void printBoard(int[][] board) {
		System.out.println("-----------------------------------------");
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				if(j == 0) {
					System.out.print("| ");
				}
				if(board[i][j] == 0) {
					System.out.print(' ');
				} else if(board[i][j] == 3) {
					System.out.print('X');
				} else {
					System.out.print(board[i][j]);
				}
				System.out.print(" | ");
			}
			System.out.println("\n-----------------------------------------");
		}
		System.out.println();
	}
	
	public static boolean isSpotValid(int y, int x) {
		return x >= 0 && x <= 9 && y >= 0 && y <= 9;
	}
	
	public static boolean isSpotValid(int board[][], int y, int x) {
		return x >= 0 && x <= 9 && y >= 0 && y <= 9 && board[y][x] == 0;
	}
	
	public static double sigmoid(double x) {
		return 1 / (1 + Math.exp(-x/5));
	}
}
