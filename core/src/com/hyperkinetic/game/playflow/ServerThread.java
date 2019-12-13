package com.hyperkinetic.game.playflow;

import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread {
    private Socket s;
    private GameServer gs;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String userName;
    private GameRoom room;
    private boolean loggedIn;

    public ServerThread(Socket s, GameServer gs){
        this.s = s;
        this.gs = gs;
        loggedIn = false;
        try
        {
            out = new ObjectOutputStream(this.s.getOutputStream());
            out.flush();

            in = new ObjectInputStream(this.s.getInputStream());
            this.start();
        }
        catch(IOException ioe) {
            System.out.println("ioe in ServerThread constructor: ");
            ioe.printStackTrace();
        }
    }

    /**
     * Send the message object to output
     * @param message the message to be sent
     */
    public void sendMessage(GameMessage message)
    {
        try
        {
            out.writeObject(message);
            out.flush();
        }
        catch(IOException ioe)
        {
            System.out.println("ioe in sendMessage() of ServerThread: ");
            ioe.printStackTrace();
        }
    }

    /**
     * Set the gameRoom that this thread runs
     * @param gm
     */
    public void enterGame(GameRoom gm)
    {
        if(this.room != null) return;
        this.room = gm;
    }

    public void leaveGame(){
        this.room = null;
    }
    /**
     * Getter of the playerID
     * @return
     */
    public String getUserName()
    {
        return userName;
    }

    /**
     * Constantly read message from the GameRoom/GameServer
     */
    @Override
    public void run() {
        try {
            while(true){ // loop forever

                while (!loggedIn) { // before login
                    try {
                        GameMessage message = (GameMessage) in.readObject();
                        gs.logMessage(message);
                        if (message.getMessageType() == GameMessage.messageType.LOGIN_ATTEMPT) {
                            GameMessage loginResponse = gs.queryDatabase(message);
                            if (loginResponse.getMessageType() == GameMessage.messageType.LOGIN_SUCCESS) {
                                loggedIn = true;
                                userName = loginResponse.userName;
                                gs.loginServerThread(this);
                            }
                            sendMessage(loginResponse);
                        } else if (message.getMessageType() == GameMessage.messageType.REGISTER_ATTEMPT) {
                            GameMessage registerResponse = gs.queryDatabase(message);
                            if (registerResponse.getMessageType() == GameMessage.messageType.REGISTER_SUCCESS) {
                                loggedIn = true;
                                userName = registerResponse.userName;
                                gs.loginServerThread(this);
                            }
                            sendMessage(registerResponse);
                        }
                    } catch (ClassNotFoundException cnfe) {
                        System.out.println("cnfe in ServerThread run(): ");
                        cnfe.printStackTrace();
                    }
                }

                while (loggedIn && room==null) { // logged in & not in game
                    try {
                        GameMessage message = (GameMessage) in.readObject();
                        gs.logMessage(message);
                        if (message.getMessageType() == GameMessage.messageType.MATCHMAKING_REQUEST) {
                            gs.addToMatchmaking(message.userName);
                        } else if(message.getMessageType()==GameMessage.messageType.STATS_REQUEST) {
                            GameMessage response = gs.queryDatabase(message);
                            gs.logMessage(response);
                            sendMessage(response);
                        }
                    } catch (ClassNotFoundException cnfe) {
                        System.out.println("cnfe in run() of ServerThread " + userName);
                        cnfe.printStackTrace();
                    }
                }

                while (loggedIn && room!=null && !room.isOver) { // logged in & in game
                    try {
                        GameMessage message = (GameMessage) in.readObject();
                        gs.logMessage(message);
                        room.readMessage(message);
                    } catch (ClassNotFoundException cnfe) {
                        System.out.println("cnfe in run() of ServerThread " + userName);
                        cnfe.printStackTrace();
                    }
                }
            }
        } catch(IOException ioe) {
            System.out.println("ioe in ServerThread run(): "+ioe.getMessage());
            if(loggedIn && room!=null){
                System.out.println("Terminating connection with client...");
                room.disconnect(this);
            }
        }
    }
}
