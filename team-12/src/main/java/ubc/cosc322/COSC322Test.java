package ubc.cosc322;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import sfs2x.client.entities.Room;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;
import ygraph.ai.smartfox.games.GameMessage;

/**
 * An example illustrating how to implement a GamePlayer
 * @author Yong Gao (yong.gao@ubc.ca)
 * Jan 5, 2021
 *
 */
public class COSC322Test extends GamePlayer{

    private GameClient gameClient = null; 
    private BaseGameGUI gamegui = null;
	
    private String userName = null;
    private String passwd = null;
 
	
    /**
     * The main method
     * @param args for name and passwd (current, any string would work)
     */
    public static void main(String[] args) {				 
    	COSC322Test player = new COSC322Test(args[0], args[1]);
    	
    	if(player.getGameGUI() == null) {
    		player.Go();
    	}
    	else {
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
     * @param userName
      * @param passwd
     */
    public COSC322Test(String userName, String passwd) {
    	this.userName = userName;
    	this.passwd = passwd;
    	
    	//To make a GUI-based player, create an instance of BaseGameGUI
    	//and implement the method getGameGUI() accordingly
    	this.gamegui = new BaseGameGUI(this);
    }
 


    @Override
    public void onLogin() {
    	System.out.println("Congratualations!!! "
    			+ "I am called because the server indicated that the login is successfully");
    	System.out.println("The next step is to find a room and join it: "
    			+ "the gameClient instance created in my constructor knows how!"); 

				List<Room> roomList = gameClient.getRoomList();
				System.out.println("\nAvailable Rooms:");
				
				for (Room room : roomList) {
					System.out.println("Room Name: " + room.getName() + 
									 ", Players: " + room.getUserCount() + "/" + room.getMaxUsers());
				}
				
				// Join the first available game room
				if (!roomList.isEmpty()) {
					Room room = roomList.get(0);
					System.out.println("\nJoining room: " + room.getName());
					gameClient.joinRoom(room.getName());
				} else {
					System.out.println("No rooms available!");
				}

                userName = gameClient.getUserName();
        if(gamegui != null) {
            gamegui.setRoomInformation(gameClient.getRoomList());
            gameClient.joinRoom(gameClient.getRoomList().get(0).getName());
        }
			}

    @SuppressWarnings("unchecked")
    @Override
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
        // Handle different types of game messages
        switch(messageType) {
            case GameMessage.GAME_STATE_BOARD:
                ArrayList<Integer> gameState = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.GAME_STATE);
                System.out.println("Game State: " + gameState);
                break;

            case GameMessage.GAME_ACTION_START:
                System.out.println("Game Started");
                System.out.println("Black Player: " + msgDetails.get(AmazonsGameMessage.PLAYER_BLACK));
                System.out.println("White Player: " + msgDetails.get(AmazonsGameMessage.PLAYER_WHITE));
                break;

            case GameMessage.GAME_ACTION_MOVE:
                ArrayList<Integer> queenCurrent = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR);
                ArrayList<Integer> queenNext = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_NEXT);
                ArrayList<Integer> arrow = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.ARROW_POS);
                
                System.out.println("Queen Move: " + queenCurrent + " -> " + queenNext);
                System.out.println("Arrow Position: " + arrow);
                break;
        }
        gamegui.setGameState((ArrayList<Integer>)msgDetails.get(AmazonsGameMessage.GAME_STATE));
        return true;

        
    }
    
    
    @Override
    public String userName() {
    	return userName;
    }

	@Override
	public GameClient getGameClient() {
		// TODO Auto-generated method stub
		return this.gameClient;
	}

	@Override
	public BaseGameGUI getGameGUI() {
		return this.gamegui;
	}

	@Override
	public void connect() {
		// TODO Auto-generated method stub
    	gameClient = new GameClient(userName, passwd, this);			
	}

 
}//end of class