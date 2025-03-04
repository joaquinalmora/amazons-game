
package ubc.cosc322;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sfs2x.client.entities.Room;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;
import ygraph.ai.smartfox.games.amazons.HumanPlayer;

/**
 * An example illustrating how to implement a GamePlayer
 * 
 * @author Yong Gao (yong.gao@ubc.ca) Jan 5, 2021
 *
 */
public class COSC322Test extends GamePlayer {

	private GameClient gameClient = null;
	private BaseGameGUI gamegui = null;

	private String userName = null;
	private String passwd = null;

	MonteCarlo mc;
	private int whiteQueen = 1;
	private int blackQueen = 2;
	private int arrow = 3; // could be any number other than 1, 2
	private int myQueen = -1;
	private int opponentQueen = -1;

	/**
	 * The main method
	 * 
	 * @param args for name and passwd (current, any string would work)
	 */
	public static void main(String[] args) {
		String uname = "cosc322";
		if(args.length > 0) {
			uname = args[0];
		}
		COSC322Test player = new COSC322Test(uname, "cosc322");
		//HumanPlayer player = new HumanPlayer();

		if (player.getGameGUI() == null) {
			player.Go();
		} else {
			BaseGameGUI.sys_setup();
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					player.Go();
				}
			});
		}
	}

	/**
	 * Any name and passwd
	 * 
	 * @param userName
	 * @param passwd
	 */
	public COSC322Test(String userName, String passwd) {
		this.userName = userName;
		this.passwd = passwd;
		
		// To make a GUI-based player, create an instance of BaseGameGUI
		// and implement the method getGameGUI() accordingly
		this.gamegui = new BaseGameGUI(this);
	}

	@Override
	public void onLogin() {
		/*userName = gameClient.getUserName();
		if (gamegui != null) {
			gamegui.setRoomInformation(gameClient.getRoomList());
		}*/
		// Get the username from the client
		userName = gameClient.getUserName();

		// Retrieve the list of available rooms
		List<Room> rooms = gameClient.getRoomList();
	
		// Update the GUI with the available room list
		if (gamegui != null) {
			gamegui.setRoomInformation(rooms);
		}
	
		// Check if there is at least one room available
		if (!rooms.isEmpty()) {
			// Get the name of the first room in the list
			String roomName = rooms.get(0).getName();
			System.out.println("Joining room: " + roomName);
			
			// Join the room using the GameClient API
			gameClient.joinRoom(roomName);
		} else {
			System.out.println("No available room found to join!");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
		// This method will be called by the GameClient when it receives a game-related
		// message
		// from the server.

		// For a detailed description of the message types and format,
		// see the method GamePlayer.handleGameMessage() in the game-client-api
		// document.
		switch (messageType) {
		case GameMessage.GAME_STATE_BOARD:					
			ArrayList<Integer> stateArr = (ArrayList<Integer>) (msgDetails.get(AmazonsGameMessage.GAME_STATE));
			this.getGameGUI().setGameState(stateArr);
			this.mc = null;
			
			System.out.println("GAME_STATE_BOARD");
			
			
			break; /* THIS GAME STATE BOARD IS A MESSAGE CONTAINING THE CURRENT STATE */
		case GameMessage.GAME_ACTION_MOVE:
			ArrayList<Integer> queenPosCurr = (ArrayList<Integer>) (msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR));
			ArrayList<Integer> queenPosNext = (ArrayList<Integer>) (msgDetails.get(AmazonsGameMessage.QUEEN_POS_NEXT));
			ArrayList<Integer> arrowPos = (ArrayList<Integer>) (msgDetails.get(AmazonsGameMessage.ARROW_POS));
			
			ApplyOpponentMove(queenPosCurr, queenPosNext, arrowPos);
			if(this.mc != null) {
				MakeMove();
			}
			break;

		case GameMessage.GAME_ACTION_START:
			String playingWhiteQueens = (String) msgDetails.get(AmazonsGameMessage.PLAYER_WHITE);
			String playingBlackQueens = (String) msgDetails.get(AmazonsGameMessage.PLAYER_BLACK);
			SetMyQueen(playingWhiteQueens, playingBlackQueens);
			
			InitalizeBoard();
			
			if(this.myQueen == this.blackQueen) {
				MakeMove();
			}
			break;
		case "cosc322.game-state.userlost":
            // Handle the user lost message
            System.out.println("Received game-state.userlost. Game over.");
            // Optionally update the GUI or trigger cleanup logic here.
            break;
			
		default:
			System.out.println("Unhandled message type: " + messageType);
			assert (false);
			break;
		}
		return true;
	}
	
	public void SetMyQueen(String playingWhiteQueens, String playingBlackQueens) {
		assert (!playingWhiteQueens.equals(playingBlackQueens));
    	if(this.userName().equals(playingWhiteQueens)) {
    		 this.myQueen = this.whiteQueen;
    		 this.opponentQueen = this.blackQueen;
    	} else if(this.userName().equals(playingBlackQueens)) {
    		 this.myQueen = this.blackQueen;
    		 this.opponentQueen = this.whiteQueen;
    	} else {
    		System.out.println("Fatal error, invalid queen value received " + this.myQueen + ", please restart");
    		assert(false);
    	}
    	
    	System.out.println("SetMyQueen " + this.myQueen);
	}
	
	public void MakeMove() {
		// assert that monte carlo is tracking turns properly as we can only make moves on our turn
		assert(this.mc.root.getColor() == this.myQueen);
		
		AmazonsAction action = this.mc.MCTS();
		if(action != null) {
			ArrayList<Integer> aiQueenPosCurr = new ArrayList<Integer>();
			aiQueenPosCurr.add(action.queenSrcY + 1);
			aiQueenPosCurr.add(action.queenSrcX + 1);
			
			ArrayList<Integer> aiQueenPosNext = new ArrayList<Integer>();
			aiQueenPosNext.add(action.queenDestY + 1);
			aiQueenPosNext.add(action.queenDestX + 1);
			
			ArrayList<Integer> aiArrowPos = new ArrayList<Integer>();
			aiArrowPos.add(action.arrowDestY + 1);
			aiArrowPos.add(action.arrowDestX + 1);
			
			this.getGameGUI().updateGameState(aiQueenPosCurr, aiQueenPosNext, aiArrowPos);
			this.getGameClient().sendMoveMessage(aiQueenPosCurr, aiQueenPosNext, aiArrowPos);
			this.mc.rootFromAction(action);
		} else { // action is only null when you lose, as you have no actions available
			System.out.println("You lose");
			// TODO: is there a server message you send once the game is over?
		}
	}
	
	public void ApplyOpponentMove(ArrayList<Integer> queenPosCurr, ArrayList<Integer> queenPosNext, ArrayList<Integer> arrowPos) {
		AmazonsAction action = new AmazonsAction(queenPosCurr.get(1)-1, queenPosCurr.get(0)-1, queenPosNext.get(1)-1, queenPosNext.get(0)-1, arrowPos.get(1)-1, arrowPos.get(0)-1);
		this.getGameGUI().updateGameState(queenPosCurr, queenPosNext, arrowPos);
		if(this.mc != null) {
			this.mc.rootFromAction(action);
		}
	}
	
	public void InitalizeBoard() {
		System.out.println("Initializing board");
		
		int[][][] state = new int[2][10][10];

		// hard coded but ideally set using stateArr
		state[0][0][3] = this.whiteQueen;
		state[0][0][6] = this.whiteQueen;

		state[0][3][0] = this.whiteQueen;
		state[0][3][9] = this.whiteQueen;

		state[0][6][0] = this.blackQueen;
		state[0][6][9] = this.blackQueen;

		state[0][9][3] = this.blackQueen;
		state[0][9][6] = this.blackQueen;
		
		state[1] = AmazonsUtility.getMobilityMap(state[0]);
		AmazonsUtility.printBoard(state[0]);
		
		this.mc = new MonteCarlo(new TreeNode(state, this.blackQueen), 29000, 1.4);
	}

	@Override
	public String userName() {
		return userName;
	}

	@Override
	public GameClient getGameClient() {
		return this.gameClient;
	}

	@Override
	public BaseGameGUI getGameGUI() {
		return this.gamegui;
	}

	@Override
	public void connect() {
		gameClient = new GameClient(userName, passwd, this);
	}

}// end of class
