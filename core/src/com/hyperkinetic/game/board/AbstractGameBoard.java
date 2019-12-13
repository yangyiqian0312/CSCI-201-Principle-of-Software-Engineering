package com.hyperkinetic.game.board;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.hyperkinetic.game.core.LaserGame;
import com.hyperkinetic.game.pieces.AbstractGamePiece;
import com.hyperkinetic.game.pieces.KingPiece;
import com.hyperkinetic.game.pieces.LaserPiece;
import com.hyperkinetic.game.playflow.GameMessage;
import com.hyperkinetic.game.util.Directions;

/**
 * A superclass for all laser game boards. Contains code to render the game board as well as static
 * functionality to track the game state.
 *
 * @author cqwillia briannlz
 */

public abstract class AbstractGameBoard {
    /**
     * Tracks the game board currently being played on for centralized modification by various game objects.
     */
    private static AbstractGameBoard board = null;
    /**
     * Texture of the vertical laser.
     */
    private transient Texture verticalLaserTexture;
    /**
     * Texture of the horizontal laser.
     */
    private transient Texture horizontalLaserTexture;
    /**
     * Texture of the highlighted part of laser.
     */
    private transient Texture highlightTexture;

    /**
     * X-dimension of the board.
     */
    protected int x;
    /**
     * Y-dimension of the board.
     */
    protected int y;
    /**
     * X-display of the screen.
     */
    private int screenX;
    /**
     * Y-display of the screen.
     */
    private int screenY;
    /**
     * Dimension of each tile.
     */
    private int tileDim;
    /**
     * Dimension of each piece
     */
    private int pieceDim;

    /**
     * Array that includes all the tiles of the board.
     */
    protected Array<AbstractBoardTile> tiles;
    /**
     * Array that includes all of the pieces on th board.
     */
    protected Array<AbstractGamePiece> pieces;

    /**
     * The KingPiece of player A.
     */
    protected KingPiece aPharaoh;
    /**
     * The KingPiece of player B.
     */
    protected KingPiece bPharaoh;
    /**
     * The LaserPiece of player A.
     */
    protected LaserPiece aLaser;
    /**
     * The LaserPiece of player B.
     */
    protected LaserPiece bLaser;

    /**
     * The next move to be sent to the server
     */
    private GameMessage nextMove;
    /**
     * Variable that keeps track of whether the move is confirmed
     */
    private boolean moveConfirmed;

    /**
     * Indicates which player has the turn.
     * If local, true means white and false means black.
     * If online multiplayer, true means the local player has the turn.
     * flipBoard indicates that the player is black, so the board should be rendered backwards.
     */
    private boolean hasTurn;
    /**
     * Variable that keeps track of whether the board is flipped
     */
    public boolean flipBoard;

    /**
     * Determines if this game board describes a local game.
     */
    private boolean local;

    /**
     * The laser that is to be drawn.
     */
    private Array<Rectangle> lasersToDraw;
    /**
     * The duration time of laser on the board.
     */
    private long laserDuration;

    /**
     * The click sound for picking and dropping a piece
     */
    private Music pieceSound;
    /**
     * The laser sound for firing a laser
     */
    private Music laserSound;
    /**
     * The sound when a piece is destroyed
     */
    private Music pieceDestroyedSound;
    /**
     * The sound effect when a king is destroyed
     */
    private Music kingDestroyedSound;
    /**
     * Variable that stores the piece that is currently picked up
     */
    private AbstractGamePiece pickedUpPiece;
    /**
     * Variable that keeps track on whether the game is over
     */
    public boolean isOver;

    public AbstractGameBoard(int x, int y, boolean hasTurn) {
        tiles = new Array<>();
        pieces = new Array<>();
        aPharaoh = null;
        bPharaoh = null;
        aLaser = null;
        bLaser = null;
        laserDuration = System.currentTimeMillis();
        lasersToDraw = new Array<>();
        nextMove = null;
        moveConfirmed = false;
        pickedUpPiece = null;
        local = false;
        this.hasTurn = hasTurn;
        this.flipBoard = !hasTurn;
        isOver = false;

        this.x = x;
        this.y = y;

        screenX = -1;
        screenY = -1;
        tileDim = -1;
        pieceDim = -1;
    }

    public AbstractGameBoard(int x, int y, boolean hasTurn, boolean local)
    {
        this(x, y, hasTurn);
        this.local = local;
        if(local)
            this.flipBoard = false;
    }

    /**
     * Initializes the client side fields of the game board (screen dimension, static board variable)
     */
    public void initialize()
    {
        int xSpace = (int) (Gdx.graphics.getWidth() * .60);
        int ySpace = (int) (Gdx.graphics.getHeight() * .80);

        double boardRatio = ((double) x / y);
        double screenRatio = ((double) Gdx.graphics.getWidth() / Gdx.graphics.getHeight());

        if (boardRatio > screenRatio) {
            tileDim = xSpace / x;
            screenX = (int) (Gdx.graphics.getWidth() * .20);
            screenY = (Gdx.graphics.getHeight() - y * tileDim) / 2;
        } else {
            tileDim = ySpace / y;
            screenY = (int) (Gdx.graphics.getHeight() * .10);
            screenX = (Gdx.graphics.getWidth() - x * tileDim) / 2;
        }
        pieceDim = tileDim * 4 / 5;

        for(AbstractGamePiece piece : pieces)
            if(piece != null) piece.loadRegion();

        for(AbstractBoardTile tile : tiles)
            if(tile != null) tile.loadRegion();

        AbstractGameBoard.board = this;

        verticalLaserTexture = LaserGame.loadTexture("board/vertical_laser.png");
        horizontalLaserTexture = LaserGame.loadTexture("board/horizontal_laser.png");
        highlightTexture = LaserGame.loadTexture("board/highlight.png");
    
        pieceSound = Gdx.audio.newMusic(Gdx.files.internal("piece.mp3"));
        pieceSound.setVolume(2.0f);
    
        laserSound = Gdx.audio.newMusic(Gdx.files.internal("laser.mp3"));
    
        pieceDestroyedSound = Gdx.audio.newMusic(Gdx.files.internal("breakglass.mp3"));
        pieceDestroyedSound.setVolume(2.0f);
    
        kingDestroyedSound = Gdx.audio.newMusic(Gdx.files.internal("explosion.mp3"));
    }

    /**
     * Processes a right click on the current game board at the specified screen location.
     *
     * @return whether or not the right click was registered
     */
    public static boolean rightClick(int oldX, int oldY, int newX, int newY)
    {
        if(!checkClickBounds(oldX, oldY, newX, newY)) return false;
        // Open an informational piece / tile dialog?
        AbstractGamePiece piece = board.pieces.get(board.tiles.indexOf(getTileFromLocation(newX, newY), true));
        if(piece.equals(board.aLaser) || piece.equals(board.bLaser))
            board.handleLaserRotate((LaserPiece) piece);

        return false;
    }

    /**
     * Processes a left click on the current game board at the specified screen location.
     *
     * @return whether or not the left click was registered
     */
    public static boolean leftClick(int oldX, int oldY, int newX, int newY)
    {
        if(!checkClickBounds(oldX, oldY, newX, newY)) return false;

        // Get the piece from the clicked tile and check that it is non-null
        AbstractGamePiece piece = board.pieces.get(board.tiles.indexOf(getTileFromLocation(newX, newY), true));

        if(piece == null)
        {
            // Try to make a move using this click
            if(!board.makeMove(getTileFromLocation(newX, newY)))
                return false;
            else
            {
                board.pieceSound.play();
                board.pickedUpPiece = null;
                return true;
            }
        }

        // If the clicked piece is a laser, fire it
        if(piece.equals(board.aLaser) || piece.equals(board.bLaser))
        {
            return board.handleLaserClick((LaserPiece) piece);
        }

        // if the player clicks on the same square again, drop the piece
        if(piece == board.pickedUpPiece)
        {
            board.pieceSound.play();
            board.pickedUpPiece = null;
        }

        // Check if the piece can be picked up
        if(!board.canPickUpPiece(piece)) return false;

        // If the player clicked on the destination of the current move, revert the current move
        if(board.nextMove != null &&
                ((piece.getX() == board.nextMove.moveX && piece.getY() == board.nextMove.moveY) ||
                 (board.nextMove.moveType.contains("rotate")) && piece.getX() == board.nextMove.x && piece.getY() == board.nextMove.y))
        {
            board.pieceSound.play();
            board.undoMove();
            return true;
        }

        // If there is a current move, undo it, and pick up the piece
        board.undoMove();
        board.pickedUpPiece = piece;

        return true;
    }

    /**
     * Processes a key press on the current game board.
     *
     * @param key a string representing the key depressed
     * @return whether or not the key press is registered
     */
    public static boolean keyPressed(String key)
    {
        if(key.equals("Q"))
        {
            board.pieceSound.play();
            return board.makeMove(AbstractBoardTile.ROTATE_LEFT);
        }
        else if(key.equals("E"))
        {
            board.pieceSound.play();
            return board.makeMove(AbstractBoardTile.ROTATE_RIGHT);
        }

        return false;
    }

    /**
     * Attempts to make a piece move with the currently picked up piece onto the given tile.
     *
     * @param tile the tile to move the piece to
     * @return true if the piece is moved or if the click causes the player to drop the piece
     */
    private boolean makeMove(AbstractBoardTile tile)
    {
        if(pickedUpPiece == null) return false;

        // Check if the move is a rotation
        if(tile == AbstractBoardTile.ROTATE_LEFT) {
            update(pickedUpPiece.getX(), pickedUpPiece.getY(), "rotateL", -1, -1);
            pickedUpPiece = null;
        }
        else if(tile == AbstractBoardTile.ROTATE_RIGHT) {
            update(pickedUpPiece.getX(), pickedUpPiece.getY(), "rotateR", -1, -1);
            pickedUpPiece = null;
        }

        // If this exact tile is within the legal moves of the piece, move it
        else if(pickedUpPiece.getLegalMoves(this).contains(tile, true))
        {
            int pIndex = pieces.indexOf(pickedUpPiece, true);
            int tIndex = tiles.indexOf(tile, true);

            update(pIndex % x, pIndex / x, "move",tIndex % x, tIndex / x);
        }
        // Otherwise, drop the piece
        else {
            pickedUpPiece = null;
        }
        return true;
    }

    /**
     * Checks whether a piece could be picked up based on the turn and the owner of the piece
     * @param piece the piece to be checked
     * @return true if the piece could be picked up, false otherwise
     */
    private boolean canPickUpPiece(AbstractGamePiece piece)
    {
        // if the game is local, the piece can be picked up as long as it is the turn of the player who owns it
        if(local)
        {
            return piece.getColor() == hasTurn;
        }

        // otherwise, the piece can only be picked up if it is this player's turn and this player owns the piece
        return piece.getColor() == !flipBoard && hasTurn;
    }

    /**
     * Checks if a pair of click screen coordinates are legitimate (within bounds and corresponding to the same tile)
     *
     * @param oldX the x coordinate of mouse-down
     * @param oldY the y coordinate of mouse-down
     * @param newX the x coordinate of mouse-up
     * @param newY the y coordinate of mouse-up
     * @return true if the click is valid, false otherwise
     */
    private static boolean checkClickBounds(int oldX, int oldY, int newX, int newY)
    {
        // If any click location is off of the current board, return false
        if(oldX < board.screenX || oldY < board.screenY || newX < board.screenX || newY < board.screenY ||
                oldX > board.screenX + board.x * board.tileDim || oldY > board.screenY + board.y * board.tileDim ||
                newX > board.screenX + board.x * board.tileDim || newY > board.screenY + board.y * board.tileDim)
            return false;

        // If the clicked tile is different from the released tile, return false
        if(AbstractGameBoard.getTileFromLocation(oldX, oldY) != AbstractGameBoard.getTileFromLocation(newX, newY))
            return false;

        return true;
    }

    /**
     * Getter of the x-dimension of the board.
     *
     * @return the x-dimension of te current board
     */
    public int getX() {
        return x;
    }

    /**
     * Getter of the y-dimension of the board.
     *
     * @return the y-dimension of te current board
     */
    public int getY() {
        return y;
    }

    /**
     * Getter of the tiles array.
     *
     * @return the tiles array of te current board
     */
    public Array<AbstractBoardTile> getTiles() {
        return tiles;
    }

    /**
     * Getter of the pieces array.
     *
     * @return array of pieces
     */
    public Array<AbstractGamePiece> getPieces() {
        return pieces;
    }

    /**
     * Getter of aLaser piece.
     *
     * @return aLaser
     */
    public LaserPiece getALaser() {
        return this.aLaser;
    }

    /**
     * Getter of bLaser piece.
     *
     * @return bLaser
     */
    public LaserPiece getBLaser() {
        return this.bLaser;
    }

    /**
     * Getter of laser piece according to hasTurn.
     *
     * @return active laser
     */
    public LaserPiece getActiveLaser() {
        return (hasTurn ^ flipBoard) ? this.aLaser : this.bLaser;
    }

    /**
     * Get the confirmed next move.
     * @return the message of next move
     */
    public GameMessage getNextMove()
    {
        if(moveConfirmed)
        {
            moveConfirmed = false;
            return nextMove;
        }
        else return null;
    }

    /**
     * Abstract method which populates the board with tiles based on the board type.
     */
    public abstract void createTiles();

    /**
     * Abstract method which places pieces on the board.
     */
    public abstract void createPieces();

    /**
     * Renders the current game board. Called by the main game loop.
     *
     * @param sb the {@link SpriteBatch} responsible for drawing game objects.
     */
    public void render(SpriteBatch sb) {
        // determine tiles to highlight as legal moves
        Array<AbstractBoardTile> highlight = null;
        if(pickedUpPiece != null) highlight = pickedUpPiece.getLegalMoves(this);

        // starting from the bottom left
        for (int i = 0; i < y; i++) {
            for (int j = 0; j < x; j++) {
                if(flipBoard) {
                    if(highlight != null && highlight.contains(tiles.get(x * (y - i) - j - 1), true))
                        sb.draw(highlightTexture, screenX + j * tileDim, screenY + i * tileDim, tileDim, tileDim);
                    else
                        tiles.get(x * (y - i) - j - 1).render(sb, screenX + j * tileDim, screenY + i * tileDim, tileDim, tileDim, true);
                }
                else {
                    if(highlight != null && highlight.contains(tiles.get(j + i * x), true))
                        sb.draw(highlightTexture, screenX + j * tileDim, screenY + i * tileDim, tileDim, tileDim);
                    else
                        tiles.get(j + i * x).render(sb, screenX + j * tileDim, screenY + i * tileDim, tileDim, tileDim);
                }
            }
        }

        if(System.currentTimeMillis() > laserDuration + 1000)
            lasersToDraw.clear();

        for(Rectangle laser : lasersToDraw)
        {
            float drawX = laser.x;
            float drawY = laser.y;
            if(flipBoard)
            {
                drawX = Gdx.graphics.getWidth() - laser.x - laser.width;
                drawY = Gdx.graphics.getHeight() - laser.y - laser.height;
            }

            sb.draw(laser.width > laser.height ? horizontalLaserTexture : verticalLaserTexture,
                    drawX, drawY, laser.width, laser.height);
        }

        for(int i = 0; i < pieces.size; i++) {
            AbstractGamePiece piece = pieces.get(i);
            if(pickedUpPiece != null && piece == pickedUpPiece)
            {
                piece.render(sb, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), pieceDim, pieceDim, false, true);
            }
            else if(piece!=null) {
                if(flipBoard)
                    piece.render(sb, Gdx.graphics.getWidth() - screenX - (piece.getX()+1) * tileDim,
                            Gdx.graphics.getHeight() - screenY - (piece.getY()+1) * tileDim,
                            tileDim, tileDim, true);
                else
                    piece.render(sb, screenX + piece.getX() * tileDim,
                            screenY + piece.getY() * tileDim,
                            tileDim, tileDim, false);
            }
        }
    }

    /**
     * Converts a location on the virtual screen to the corresponding tile on the current game board.
     *
     * @param mouseX the x location on the virtual screen
     * @param mouseY the y location on the virtual screen
     * @return the board tile at the given location, or <code>null</code> if the mouse is outside the game board.
     */
    private static AbstractBoardTile getTileFromLocation(int mouseX, int mouseY) {
        if (mouseX < board.screenX || mouseY < board.screenY
                || mouseX > board.screenX + board.x * board.tileDim || mouseY > board.screenY + board.y * board.tileDim)
            return null;

        // int j = (mouseY - screenY) / tileDim;
        // int i = (mouseX - screenX) / tileDim;
        int yCoord = (mouseY - board.screenY) / board.tileDim;
        int xCoord = (mouseX - board.screenX) / board.tileDim;

        if(board.flipBoard)
        {
            yCoord = board.y - 1 - yCoord;
            xCoord = board.x - 1 - xCoord;
        }

        return board.tiles.get(yCoord * board.x + xCoord);
    }

    /**
     * Converts a coordinate to the corresponding tile on the current game board.
     *
     * @param x the x location on the board
     * @param y the y location on the board
     * @return the board tile at the given location,
     * or <code>null</code> if the coordinate is invalid.
     */
    public AbstractBoardTile getTileFromCoordinate(int x, int y) {
        if (x < 0 || y < 0 || x >= this.x || y >= this.y) {
            return null;
        }
        return (tiles.get(y * this.x + x));
    }

    /**
     * Converts a coordinate to the corresponding piece on the current game board.
     *
     * @param x the x location on the board
     * @param y the y location on the board
     * @return the game piece at the given location,
     * or <code>null</code> if either the coordinate is invalid or no piece is place on that tile.
     */
    public AbstractGamePiece getPieceFromCoordinate(int x, int y) {
        if (x < 0 || y < 0 || x >= this.x || y >= this.y) {
            return null;
        }
        return (pieces.get(y * this.x + x));
    }

    /**
     * Abstract method which returns whether game is over based on board type.
     *
     * @return "AWin" or "BWin" or "NoWin"
     */
    public abstract String getGameState();

    /**
     * Update the board configuration
     * @param x the x coordinate of the piece that is to be updated
     * @param y the y coordinate of the piece that is to be updated
     * @param moveType the type of the move, rotated or moved to a new location
     * @param nX the new x coordinate of the piece
     * @param nY the new y coordinate of the piece
     */
    public void update(int x,int y,String moveType,int nX,int nY){
        AbstractGamePiece piece = getPieceFromCoordinate(x,y);
        if(moveType.equals("rotateL")) {
            pieceRotateLeft(piece);
            if(!local && piece.equals(aLaser)) pieceRotateLeft(aLaser);
            if(!local && piece.equals(bLaser)) pieceRotateLeft(bLaser);
        } else if(moveType.equals("rotateR")) {
            pieceRotateRight(piece);
            if(!local && piece.equals(aLaser)) pieceRotateRight(aLaser);
            if(!local && piece.equals(bLaser)) pieceRotateRight(bLaser);
        } else {
            pieceMove(piece,nX,nY);
        }

        GameMessage move = new GameMessage(GameMessage.messageType.PLAYER_MOVE);
        move.x = x;
        move.y = y;
        move.moveType = moveType;
        move.moveX = nX;
        move.moveY = nY;
        this.nextMove = move;
    }

    /**
     * Undo the move that is not confirmed by the player.
     */
    private void undoMove()
    {
        if(nextMove == null) return;

        AbstractGamePiece piece;
        if(nextMove.moveType.contains("rotate"))
            piece = getPieceFromCoordinate(nextMove.x, nextMove.y);
        else
            piece = getPieceFromCoordinate(nextMove.moveX, nextMove.moveY);

        if(nextMove.moveType.equals("rotateL")) {
            piece.rotateRight();
        } else if(nextMove.moveType.equals("rotateR")) {
            piece.rotateLeft();
        } else {
            pieceMove(piece, nextMove.x, nextMove.y);
        }

        nextMove = null;
    }

    /**
     * Function dealing with clicking on the laser piece
     * @param laser the laser piece that is to be clicked
     * @return true if click is valid and laser is fired
     */
    private boolean handleLaserClick(LaserPiece laser)
    {
        if(nextMove == null)
        {
            return false;
        }

        if(laser.equals(aLaser))
        {
            if(hasTurn ^ flipBoard)
            {
                laserSound.play();
                if(!LaserGame.IS_SERVER && LaserGame.client != null && !local)
                {
                    nextMove.userName = LaserGame.client.userName;
                    LaserGame.client.getPlayer().sendMessage(nextMove);
                }
                fireLaser(laser.getX(), laser.getY(), laser.getOrientation());
                return true;
            }
        }
        else if(laser.equals(bLaser))
        {
            if(!hasTurn ^ flipBoard)
            {
                laserSound.play();
                if(!LaserGame.IS_SERVER && LaserGame.client != null && !local)
                {
                    nextMove.userName = LaserGame.client.userName;
                    LaserGame.client.getPlayer().sendMessage(nextMove);
                }
                fireLaser(laser.getX(), laser.getY(), laser.getOrientation());
                return true;
            }
        }

        return false;
    }

    /**
     * Function dealing with rotation of the laser source.
     * @param laser the laser source piece
     * @return true if laser piece has successfully rotated
     */
    private boolean handleLaserRotate(LaserPiece laser)
    {
        pickedUpPiece = null;
        
        if(local)
        {
            if(laser.equals(aLaser) && hasTurn)
            {
                undoMove();
                update(laser.getX(), laser.getY(), laser.toggleDirection(), -1, -1);
                return true;
            }
            else if(laser.equals(bLaser) && !hasTurn)
            {
                undoMove();
                update(laser.getX(), laser.getY(), laser.toggleDirection(), -1, -1);
                return true;
            }
        }
        else if(laser.equals(aLaser) && (hasTurn ^ flipBoard)) {
            undoMove();
            update(laser.getX(), laser.getY(), laser.toggleDirection(), -1, -1);
            return true;
        }
        else if(laser.equals(bLaser) && (hasTurn ^ flipBoard)) {
            undoMove();
            update(laser.getX(), laser.getY(), laser.toggleDirection(), -1, -1);
            return true;
        }
        
        return false;
    }

    /**
     * Left-rotate a selected piece on the board.
     *
     * @param piece chosen piece to ratate left
     * @return true if success
     */
    private boolean pieceRotateLeft(AbstractGamePiece piece) {
        if(!LaserGame.IS_SERVER) pieceSound.play();
        piece.rotateLeft();
        return true;
    }

    /**
     * Right-rotate a selected piece on the board.
     *
     * @param piece chosen piece to ratate right, matches pID
     * @return true if success
     */
    private boolean pieceRotateRight(AbstractGamePiece piece) {
        if(!LaserGame.IS_SERVER) pieceSound.play();
        piece.rotateRight();
        return true;
    }

    /**
     * Move a selected piece on the board.
     *
     * @param piece chosen piece to move, matches pID
     * @param x new x location
     * @param y new y location
     * @return true if success
     */
    private boolean pieceMove(AbstractGamePiece piece, int x, int y) {
        piece.pickUpPiece(this);
        piece.setX(x);
        piece.setY(y);
        piece.placePiece(this);
        return true;
    }

    /**
     * Check whether the movement of a player on a piece is valid.
     *
     * @param color the identifier of the player that has the turn
     * @param x the x position of the piece
     * @param y the y position of the piece
     * @param moveType the type of the move
     * @param nX the x position of the desination
     * @param nY the y position of the destination
     * @return whether the move is valid or not
     */
    public boolean isValidMove(boolean color, int x, int y, String moveType, int nX, int nY) {
        AbstractGamePiece piece = getPieceFromCoordinate(x,y);
        if(piece==null) return false;
        if(piece.getColor() != color) return false;

        if(moveType.equals("rotateL") || moveType.equals("rotateR")) {
            return true;
        } else if(moveType.equals("move")) {
            if (getTileFromCoordinate(nX, nY) != null && getPieceFromCoordinate(nX, nY) == null) {
                AbstractBoardTile tile = getTileFromCoordinate(nX, nY);
                Array<AbstractBoardTile> candidates = piece.getLegalMoves(this);
                for (AbstractBoardTile t : candidates) {
                    if (tile == t) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Handles the logic and rendering of a laser being fired into a tile.
     *
     * @param startX the X-coordinate of the tile into which the laser is being fired
     * @param startY the Y-coordinate of the tile into which the laser is being fired
     * @param d the direction in which the laser is being fired into the tile
     */
    public void fireLaser(int startX, int startY, Directions.Direction d) {
        laserHelper(startX, startY, d);
        nextMove = null;
        hasTurn = !hasTurn;
    }

    /**
     * A recursive fucntion for drawing the laser onto the board.
     * @param startX the x coordinate of the starting point of laser
     * @param startY the y coordinate of the starting point of laser
     * @param d the direction of the laser that is to be drawn
     */
    private void laserHelper(int startX, int startY, Directions.Direction d)
    {
        laserDuration = System.currentTimeMillis();
    
        if (startX < 0 || startY < 0 || startX >= x || startY >= y) return;
    
        AbstractGamePiece thisPiece = pieces.get(startY * x + startX);
        AbstractBoardTile thisTile = tiles.get(startY * x + startX);
    
        if(thisPiece == null)
        {
            drawLaser(startX, startY, d);
            startX += (d == Directions.Direction.EAST ? 1 : 0) + (d == Directions.Direction.WEST ? -1 : 0);
            startY += (d == Directions.Direction.NORTH ? 1 : 0) + (d == Directions.Direction.SOUTH ? -1 : 0);
            laserHelper(startX, startY, d);
            return;
        }
    
        Array<Directions.Direction> newDirections = thisPiece.acceptLaser(d);
    
        if(newDirections == null)
        {
            // destroy the piece
            if(aPharaoh.equals(pieces.get(startY * x + startX)))
            {
                if(!LaserGame.IS_SERVER) kingDestroyedSound.play();
                aPharaoh = null;
                isOver = true;
            }
            else if(bPharaoh.equals(pieces.get(startY * x + startX)))
            {
                if(!LaserGame.IS_SERVER) kingDestroyedSound.play();
                bPharaoh = null;
                isOver = true;
            }
            else
                if (!LaserGame.IS_SERVER) pieceDestroyedSound.play();
            
            pieces.set(startY * x + startX, null);
        
            thisTile.setPiece(null);
            thisTile.onPieceDestroyed(thisPiece);
            return;
        }
    
        for(Directions.Direction dir : newDirections)
        {
            //reflect the lasers
            drawLaser(startX, startY, dir);
            int newX = startX + (dir == Directions.Direction.EAST ? 1 : 0) + (dir == Directions.Direction.WEST ? -1 : 0);
            int newY = startY + (dir == Directions.Direction.NORTH ? 1 : 0) + (dir == Directions.Direction.SOUTH ? -1 : 0);
            laserHelper(newX, newY, dir);
        }
    }

    /**
     * Helper function for <code>fireLaser()</code>
     *
     * @param startX the X-coordinate of the tile into which the laser is being fired
     * @param startY the Y-coordinate of the tile into which the laser is being fired
     * @param d the direction in which the laser is being fired into the tile
     */
    private void drawLaser(int startX, int startY, Directions.Direction d)
    {
        if(d == Directions.Direction.NORTH)
            lasersToDraw.add(new Rectangle(screenX + startX * tileDim + tileDim / 2F - 5,
                                        screenY + startY * tileDim + tileDim / 2F,
                                    10, tileDim));
        else if(d == Directions.Direction.SOUTH)
            lasersToDraw.add(new Rectangle(screenX + startX * tileDim + tileDim / 2F - 5,
                                        screenY + startY * tileDim - tileDim / 2F,
                                    10, tileDim));
        else if(d == Directions.Direction.EAST)
            lasersToDraw.add(new Rectangle(screenX + startX * tileDim + tileDim / 2F,
                                        screenY + startY * tileDim + tileDim / 2F - 5,
                                        tileDim, 10));
        else if(d == Directions.Direction.WEST)
            lasersToDraw.add(new Rectangle(screenX + startX * tileDim - tileDim / 2F,
                                        screenY + startY * tileDim + tileDim / 2F - 5,
                                            tileDim, 10));
    }
}
