package com.hyperkinetic.game.playflow;

import com.hyperkinetic.game.core.LaserGame;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.sql.*;

/**
 * GameSocket class, server side of websocket, hosts all GameRooms and implements match making
 * Note: This class should be in a separate server project instead of this local game project (client)
 */

public class GameServer {
    /**
     * Hostname of the server
     */
    private static final String host = "localhost";
    /**
     * Port number of the server
     */
    public static final int port = 8000;
    /**
     * Database connection URL
     */
    private static final String url = "mysql://localhost:3306/finalproject";
    /**
     * Database connection
     */
    private static Connection conn;
    /**
     * Database username
     */
    private static final String user = "root";
    /**
     * Database password
     */
    private static final String pwd = "root";
    /**
     * Stores all player sockets in matchmaking
     */
    private static Vector<ServerThread> matchingQueue = new Vector<>();
    /**
     * Stores ServerThreads that are not logged in
     */
    private static Vector<ServerThread> loginQueue = new Vector<>();
    /**
     * Stores ServerThreads that are logged in
     */
    private static Vector<ServerThread> loggedInQueue = new Vector<>();
    /**
     * stores mapping from playerID to GameRooms
     */
    private static Vector<GameRoom> gameRooms = new Vector<>();

    public static void main(String[] args){
        LaserGame.IS_SERVER = true;
        GameServer gs = new GameServer();
    }

    public GameServer(){
        try {
            conn = getConnection();
            System.out.println("Connected to database!");
            System.out.println("Binding to port: "+port);
            ServerSocket ss = new ServerSocket(port);
            System.out.println("Connected!");

            while(true) {
                Socket s = ss.accept();
                ServerThread st = new ServerThread(s, this);
                loginQueue.add(st);
                System.out.println("Accepted one new connection: "+s.getInetAddress());

                // check for dead games
                // for(GameRoom room : gameRooms)
                // {
                //     if(room.isOver) gameRooms.remove(room);
                // }
            }
        } catch(IOException e) {
            System.out.println("Unable to create server: "+e.getMessage());
        } catch (SQLException e) {
            System.out.println("Unable to connect the database: ");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found: ");
            e.printStackTrace();
        }
    }

    /**
     * Remove the ServerThread from loginQueue and add that ServerThread to loggedInQueue.
     *
     * @param st the ServerThread that is to be removed and added
     */
    public void loginServerThread(ServerThread st) {
        for (int i = 0; i < loginQueue.size(); i++) {
            if (st == loginQueue.get(i)) {
                loginQueue.remove(i);
                loggedInQueue.add(st);
            }
        }
    }

    /**
     * Get a connection to the database.
     * @return the connection to the database
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:"+url+"?user="+user+"&password="+pwd);
        return conn;
    }

    /**
     * Update records for both players after one game is over.
     *
     * @param gm GameMessage
     * @return a STATS_RESPONSE GameMessage object
     */
    public boolean updateDatabase(GameMessage gm){
        if(gm.getMessageType()!=GameMessage.messageType.GAME_OVER){
            return false;
        }
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String winner = gm.userName;
        String loser = gm.userName2;
        try{
            conn = getConnection();
            // winner
            ps = conn.prepareStatement("SELECT * FROM USER u, RECORD r WHERE u.userID=r.userID AND u.userName=?");
            ps.setString(1,winner);
            rs = ps.executeQuery();
            if(rs.next()){
                ps = conn.prepareStatement("UPDATE RECORD SET numPlayed=?, numWin=? WHERE recordID=?");
                ps.setInt(1,rs.getInt("numPlayed")+1);
                ps.setInt(2,rs.getInt("numWin")+1);
                ps.setInt(3,rs.getInt("recordID"));
                ps.executeUpdate();
            }

            // loser
            ps = conn.prepareStatement("SELECT * FROM USER u, RECORD r WHERE u.userID=r.userID AND u.userName=?");
            ps.setString(1,loser);
            rs = ps.executeQuery();
            if(rs.next()){
                ps = conn.prepareStatement("UPDATE RECORD SET numPlayed=?, numLoss=? WHERE recordID=?");
                ps.setInt(1,rs.getInt("numPlayed")+1);
                ps.setInt(2,rs.getInt("numLoss")+1);
                ps.setInt(3,rs.getInt("recordID"));
                ps.executeUpdate();
            }
        } catch(ClassNotFoundException e){
            System.out.println("ClassNotFound error in updateDatabase(): "+e.getMessage());
        } catch(SQLException e){
            System.out.println("SQL error in updateDatabase(): "+e.getMessage());
        } finally {
            try {
                if(rs!=null) rs.close();
                if(ps!=null) ps.close();
                if(conn!=null) conn.close();
            } catch(SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return false;
    }

    /**
     * Query/update the database for login, register, stats_request
     *
     * @param gm GameMessage
     * @return a responsive GameMessage object
     */
    public GameMessage queryDatabase(GameMessage gm){
        PreparedStatement ps = null;
        ResultSet rs = null;
        GameMessage res = null;
        try{
            if(gm.getMessageType()==GameMessage.messageType.LOGIN_ATTEMPT){
                String userName = gm.userName;
                String password = gm.password;
                ps = conn.prepareStatement("SELECT * FROM USER WHERE userName=?");
                ps.setString(1, userName);
                rs = ps.executeQuery();
                if (rs.next()) {
                    if (password.equals(rs.getString("passWord"))) {
                        res = new GameMessage(GameMessage.messageType.LOGIN_SUCCESS);
                    }
                    else {
                        res = new GameMessage(GameMessage.messageType.LOGIN_FAILURE);
                        res.errorMessage = "The password is incorrect.";
                    }
                }
                else {
                    res = new GameMessage(GameMessage.messageType.LOGIN_FAILURE);
                    res.errorMessage = "The user does not exist.";
                }
                res.userName = userName;
            } else if(gm.getMessageType()==GameMessage.messageType.REGISTER_ATTEMPT){
                String userName = gm.userName;
                String password = gm.password;
                ps = conn.prepareStatement("SELECT * FROM USER WHERE userName=?");
                ps.setString(1, userName);
                rs = ps.executeQuery();
                if (rs.next()) {
                    res = new GameMessage(GameMessage.messageType.REGISTER_FAILURE);
                    res.errorMessage = "The user name is taken.";
                }
                else {
                    ps = conn.prepareStatement("INSERT INTO USER (userName,password) VALUES (?,?)");
                    ps.setString(1, userName);
                    ps.setString(2, password);
                    ps.executeUpdate();
                    
                    ps = conn.prepareStatement("SELECT userID FROM USER WHERE userName=?");
                    ps.setString(1, userName);
                    rs = ps.executeQuery();
                    int userID = 0;
                    if(rs.next()){
                        userID = rs.getInt("userID");
                    }
                    
                    ps = conn.prepareStatement("INSERT INTO RECORD (userID,numPlayed,numWin,numLoss) VALUES (?,?,?,?)");
                    ps.setInt(1, userID);
                    ps.setInt(2, 0);
                    ps.setInt(3, 0);
                    ps.setInt(4, 0);
                    ps.executeUpdate();
                    
                    res = new GameMessage(GameMessage.messageType.REGISTER_SUCCESS);
                }
                res.userName = userName;
            } else if(gm.getMessageType()==GameMessage.messageType.STATS_REQUEST){
                res = new GameMessage(GameMessage.messageType.STATS_RESPONSE);
                String userName = gm.userName;
                ps = conn.prepareStatement("SELECT * FROM USER u, RECORD r WHERE u.userID=r.userID AND u.userName=?");
                ps.setString(1, userName);
                rs = ps.executeQuery();
                if (rs.next()) {
                    res.numPlayed = rs.getInt("numPlayed");
                    res.numWin = rs.getInt("numWin");
                    res.numLoss = rs.getInt("numLoss");
                }
                res.userName = userName;
            }
            return res;
        } catch(SQLException e) {
            System.out.println("SQL error in queryDatabase(): "+e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch(SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return null;
    }

    /**
     * Remove the game room from the gameRooms vector.
     * @param gr the GameRoom to be removed
     */
    public void deleteRoom(GameRoom gr){
        gameRooms.remove(gr);
    }

    /**
     * Print the game message to the server console.
     * @param message the GameMessage to be logged
     */
    public void logMessage(GameMessage message){
        System.out.println(message.getMessage());
    }

    /**
     * Add the logged in user to the matchmaking queue
     * @param playerID the id of the player that is added to the matching queue
     */
    public void addToMatchmaking(String playerID)
    {
        for(ServerThread st : loggedInQueue)
        {
            if(st.getUserName().equals(playerID))
            {
                loggedInQueue.remove(st);
                matchingQueue.add(st);
                break;
            }
        }
        // DEBUG
        System.out.println(playerID+" has been put to matching queue.");

        // first come first served matchmaking
        for(int i = 0; i < matchingQueue.size() - 1; i+=2)
        {
            GameRoom gr = new GameRoom(this, matchingQueue.get(i), matchingQueue.get(i+1));
            matchingQueue.get(i).enterGame(gr);
            matchingQueue.get(i+1).enterGame(gr);
            gameRooms.add(gr);
            // DEBUG
            System.out.println(matchingQueue.get(i).getUserName()+" and "+matchingQueue.get(i+1).getUserName()+" have been put in match.");
        }
        if(matchingQueue.size() % 2 == 0)
        {
            matchingQueue.clear();
        }
        else
        {
            ServerThread lastUser = matchingQueue.lastElement();
            matchingQueue.clear();
            matchingQueue.add(lastUser);
        }
    }
}
