package com.hyperkinetic.game.playflow;

import com.badlogic.gdx.utils.Json;
import com.hyperkinetic.game.board.AbstractGameBoard;
import com.hyperkinetic.game.core.LaserGame;
import com.hyperkinetic.game.core.LogInScreen;
import com.hyperkinetic.game.pieces.LaserPiece;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Vector;

public class ClientThread extends Thread {
    /**
     * Input from the server
     */
    private ObjectInputStream in;
    /**
     * A reference to the game board
     */
    private AbstractGameBoard board;

    public String userName;
    public boolean isGuest;
    public boolean isAI;

    private boolean loggedIn;

    /**
     * The player thread that is hold by the client
     */
    private Player player;
    /**
     * The socket of the player
     */
    private Socket s;

    private LaserGame game;

    public ClientThread(String hostname, int port, boolean isGuest, boolean isAI, LaserGame game)
    {
        board = null;
        this.isGuest = isGuest;
        this.isAI = isAI;
        this.userName = null;
        this.game = game;
        loggedIn = false;

        try
        {
            System.out.println("Trying to connect to "+hostname+":"+port);
            s = new Socket(hostname, port);
            System.out.println("Connected to "+hostname+":"+port);
            player = new Player(s, this);
            in = new ObjectInputStream(s.getInputStream());
            this.start();
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public Player getPlayer(){
        return player;
    }

    public void resetPlayerID() {
        userName = null;
        player.setPlayerID(null);
    }

    /**
     * Receive the start-of-game message and constantly check for server packets and process
     */
    @Override
    public void run()
    {
        try{
            while(true) {

                while(!loggedIn){ // before login
                    try {
                        GameMessage message = (GameMessage) in.readObject();
                        System.out.println("Message received: "+message.getMessage());

                        if (message.getMessageType() == GameMessage.messageType.LOGIN_SUCCESS || message.getMessageType() == GameMessage.messageType.REGISTER_SUCCESS) {
                            this.player.setPlayerID(message.userName);
                            this.userName = message.userName;
                            loggedIn = true;
                        } else if (message.getMessageType() == GameMessage.messageType.LOGIN_FAILURE || message.getMessageType() == GameMessage.messageType.REGISTER_FAILURE) {
                            System.out.println(message.errorMessage);
                            this.player.setPlayerID(LogInScreen.LOGIN_FAILURE_FLAG);
                            this.userName = LogInScreen.LOGIN_FAILURE_FLAG;
                        }

                    } catch (ClassNotFoundException cnfe) {
                        System.out.println("cnfe in ClientThread run(): ");
                        cnfe.printStackTrace();
                    }
                }

                while(loggedIn && board==null){ // logged in & not in game
                    try {
                        GameMessage message = (GameMessage) in.readObject();
                        System.out.println("Message received: "+message.getMessage());

                        if(message.getMessageType()==GameMessage.messageType.ROOM_CREATE){
                            Json json = new Json();
                            AbstractGameBoard start = message.boardClass.cast(json.fromJson(message.boardClass, message.startBoard));
                            this.board = start;
                            player.setBoard(start);

                            // CORE CODE GOES HERE!
                            GameMessage g = new GameMessage(GameMessage.messageType.STATS_RESPONSE);
                            g.errorMessage = "hello";
                            player.sendMessage(g);
                        }

                    } catch (ClassNotFoundException cnfe) {
                        System.out.println("cnfe in ClientThread run(): ");
                        cnfe.printStackTrace();
                    }
                }

                while(loggedIn && board!=null && !board.isOver){ // logged in & in game
                    try {
                        GameMessage message = (GameMessage) in.readObject();
                        System.out.println("Message received: "+message.getMessage());

                        if(message.getMessageType()==GameMessage.messageType.MOVE_SUCCESS){
                            if(!message.userName.equals(userName)){
                                board.update(message.x,message.y,message.moveType,message.moveX,message.moveY);
                                LaserPiece laser = board.getActiveLaser();
                                board.fireLaser(laser.getX(),laser.getY(),laser.getOrientation());
                            }
                        }  else if(message.getMessageType()==GameMessage.messageType.GAME_OVER){
                            // code below never reached
                            Vector<GameMessage> attached = message.attached;
                            for(GameMessage gm : attached){
                                if(gm.getMessageType()==GameMessage.messageType.STATS_RESPONSE && gm.userName.equals(userName))
                                {
                                    int numPlayed = gm.numPlayed;
                                    int numWin = gm.numWin;
                                    int numLoss = gm.numLoss;
                                    player.updateRecord(numPlayed,numWin,numLoss);
                                    break;
                                }
                            }
                            if(message.userName.equals(userName)){
                                System.out.println("You have won! "+userName);
                                player.won();
                            } else {
                                System.out.println("You have lost! "+userName);
                                player.lost();
                            }
                        }

                    } catch (ClassNotFoundException cnfe) {
                        System.out.println("cnfe in ClientThread run(): ");
                        cnfe.printStackTrace();
                    }
                }
            }
        } catch (IOException ioe) {
            System.out.println("ioe in ClientThread run(): ");
            ioe.printStackTrace();
        }
    }
}