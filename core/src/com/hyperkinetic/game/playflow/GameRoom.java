package com.hyperkinetic.game.playflow;

import com.badlogic.gdx.utils.Json;
import com.hyperkinetic.game.board.AbstractGameBoard;
import com.hyperkinetic.game.board.StandardBoard;
import com.hyperkinetic.game.pieces.LaserPiece;
import com.hyperkinetic.game.playflow.GameMessage.messageType;

import java.util.Vector;

/**
 * GameRoom class, implements basic game flow - this version only supports local mode
 */
public class GameRoom {
    public static final int PORT = 8000;

    /**
     * Thread of player a on the server
     */
    protected ServerThread aThread;
    /**
     * Thread of player b on the server
     */
    protected ServerThread bThread;
    /**
     * The game board that the two players are playing with
     */
    protected AbstractGameBoard board;
    /**
     * A reference of the game server
     */
    private GameServer gs;
    /**
     * Variable that keeps track of player's turn
     */
    protected boolean turn; //who's turn is it. Player A = White = True, Player B = Black = False
    /**
     * Variable that keeps track of whether the game is over
     */
    public boolean isOver = false;

    public GameRoom(GameServer gs, ServerThread a, ServerThread b) {
        this.gs = gs;
        turn = true; // white (a) plays first

        aThread = a;
        bThread = b;

        aThread.enterGame(this);
        bThread.enterGame(this);

        this.board = new StandardBoard(true);

        Json json = new Json();

        GameMessage gm1 = new GameMessage(messageType.ROOM_CREATE);
        StandardBoard board1 = new StandardBoard(true);
        gm1.startBoard = json.toJson(board1);
        gm1.boardClass = StandardBoard.class;
        gm1.userName = aThread.getUserName();
        gm1.userName2 = bThread.getUserName();
        aThread.sendMessage(gm1);

        GameMessage gm2 = new GameMessage(messageType.ROOM_CREATE);
        StandardBoard board2 = new StandardBoard(false);
        gm2.startBoard = json.toJson(board2);
        gm2.boardClass = StandardBoard.class;
        gm2.userName = aThread.getUserName();
        gm2.userName2 = bThread.getUserName();
        bThread.sendMessage(gm2);
    }

    protected GameRoom()
    {
        turn = true;
        aThread = null;
        bThread = null;
    }

    /**
     * Broadcast the message to both two clients.
     * @param message the message to be broadcast to each client
     */
    public void broadcast(GameMessage message) {
        gs.logMessage(message);
        aThread.sendMessage(message);
        bThread.sendMessage(message);
    }

    /**
     * Validate move, send move success/failure message to server.
     * @param move the move to be validated
     */
    public synchronized void handleMoveAttempt(GameMessage move){
        if(move.getMessageType() != GameMessage.messageType.PLAYER_MOVE) return;

        if(move.userName.equals(getActivePlayerID())) {
            if(board.isValidMove(turn, move.x, move.y, move.moveType, move.moveX, move.moveY)) {
                GameMessage success = new GameMessage(messageType.MOVE_SUCCESS);
                success.userName = getActivePlayerID();
                success.x = move.x;
                success.y = move.y;
                success.moveType = move.moveType;
                success.moveX = move.moveX;
                success.moveY = move.moveY;
                broadcast(success);

                updateBoard(move.x, move.y, move.moveType, move.moveX, move.moveY);

            } else {
                GameMessage fail = new GameMessage(messageType.MOVE_FAILURE);
                fail.userName = getActivePlayerID();
                fail.x = move.x;
                fail.y = move.y;
                fail.moveType = move.moveType;
                fail.moveX = move.moveX;
                fail.moveY = move.moveY;
                fail.errorMessage = "illegal move";

                endGame(turn ? "BWin" : "AWin", fail);
            }
        } else {
            GameMessage fail = new GameMessage(messageType.MOVE_FAILURE);
            fail.userName = move.userName;
            fail.x = move.x;
            fail.y = move.y;
            fail.moveType = move.moveType;
            fail.moveX = move.moveX;
            fail.moveY = move.moveY;
            fail.errorMessage = "wrong turn";

            endGame(turn ? "BWin" : "AWin", fail);
        }
    }

    /**
     * Update the board on the server
     * @param x the x coordinate of the piece that is to be updated
     * @param y the y coordinate of the piece that is to be updated
     * @param moveType the type of the move, rotated or moved to a new location
     * @param nX the new x coordinate of the piece
     * @param nY the new y coordinate of the piece
     */
    private void updateBoard(int x,int y,String moveType,int nX,int nY) {
        board.update(x,y,moveType,nX,nY);
        LaserPiece aLaser = board.getALaser();
        LaserPiece bLaser = board.getBLaser();
        if(turn) board.fireLaser(aLaser.getX(), aLaser.getY(), aLaser.getOrientation());
        else board.fireLaser(bLaser.getX(), bLaser.getY(), bLaser.getOrientation());
        turn = !turn;

        String res = board.getGameState();
        endGame(res,null);
    }
    
    public void disconnect(ServerThread st)
    {
        if(st == aThread)
            endGame("BWin",null);
        else if(st == bThread)
            endGame("AWin",null);
    }

    // if game over, send gameover message with updated stats (and reason) attached
    public void endGame(String res, GameMessage reason)
    {
        if(res.equals("AWin")){
            GameMessage gm = new GameMessage(messageType.GAME_OVER);
            gm.userName = aThread.getUserName();
            gm.userName2 = bThread.getUserName();
            gs.updateDatabase(gm);
            isOver = true;

            GameMessage statsRequest = new GameMessage(messageType.STATS_REQUEST);
            statsRequest.userName = aThread.getUserName();
            GameMessage statsResponseA = gs.queryDatabase(statsRequest);
            statsRequest.userName = bThread.getUserName();
            GameMessage statsResponseB = gs.queryDatabase(statsRequest);

            Vector<GameMessage> attached = new Vector<>();
            if(reason!=null) attached.add(reason);
            attached.add(statsResponseA);
            attached.add(statsResponseB);

            gm.attached = attached;
            broadcast(gm);

            aThread.leaveGame();
            bThread.leaveGame();

            clear();
        } else if(res.equals("BWin")){
            GameMessage gm = new GameMessage(messageType.GAME_OVER);
            gm.userName = bThread.getUserName();
            gm.userName2 = aThread.getUserName();
            gs.updateDatabase(gm);
            isOver = true;

            GameMessage statsRequest = new GameMessage(messageType.STATS_REQUEST);
            statsRequest.userName = aThread.getUserName();
            GameMessage statsResponseA = gs.queryDatabase(statsRequest);
            statsRequest.userName = bThread.getUserName();
            GameMessage statsResponseB = gs.queryDatabase(statsRequest);

            Vector<GameMessage> attached = new Vector<>();
            if(reason!=null) attached.add(reason);
            attached.add(statsResponseA);
            attached.add(statsResponseB);

            gm.attached = attached;
            broadcast(gm);

            aThread.leaveGame();
            bThread.leaveGame();

            clear();
        }
    }

    /**
     * Read the message from the server.
     * @param message the message from the server
     */
    public void readMessage(GameMessage message)
    {
        if(message.getMessageType()==messageType.PLAYER_MOVE){
            handleMoveAttempt(message);
        }
    }

    /**
     * Get the player ID of the active player.
     * @return the player ID of the active player
     */
    private String getActivePlayerID()
    {
        return turn ? aThread.getUserName() : bThread.getUserName();
    }

    public void clear(){
        gs.deleteRoom(this);
    }
}
