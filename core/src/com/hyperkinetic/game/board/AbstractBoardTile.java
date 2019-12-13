package com.hyperkinetic.game.board;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.hyperkinetic.game.core.LaserGame;
import com.hyperkinetic.game.pieces.AbstractGamePiece;

import java.io.Serializable;

/**
 * An abstract superclass describing the behaviour of tiles on the game board.
 *
 * @author cqwillia
 */
public abstract class AbstractBoardTile implements Serializable
{
    /**
     * Serial ID
     */
    private static final long serialVersionUID = 2L;

    /**
     * Dimension of each tile.
     */
    private static final int IMG_DIM = 600;

    /**
     * Defines where on the board this tile resides so it can be textured correctly.
     */
    public enum TileType
    {
        UL_CORNER,
        UPPER_EDGE,
        UR_CORNER,
        LEFT_EDGE,
        CENTER,
        RIGHT_EDGE,
        LL_CORNER,
        LOWER_EDGE,
        LR_CORNER
    }

    /**
     * A new tile instance for left rotation.
     */
    public static final AbstractBoardTile ROTATE_LEFT = new AbstractBoardTile(){};
    /**
     * A new tile instance for right rotation.
     */
    public static final AbstractBoardTile ROTATE_RIGHT = new AbstractBoardTile(){};

    /**
     * The texture of the board tile.
     */
    private transient Texture texture;
    /**
     * The piece that sits on the tile.
     */
    private AbstractGamePiece piece;

    public AbstractBoardTile()
    {
        piece = null;
    }
    public AbstractBoardTile(AbstractBoardTile.TileType type)
    {
        piece = null;
    }
    public AbstractBoardTile(String basePath, AbstractBoardTile.TileType type)
    {
        piece = null;
    }

    /**
     * Getter of the game piece
     *
     * @return the piece placed on this tile
     */
    public AbstractGamePiece getPiece() {
        return piece;
    }

    /**
     * Replace the current piece on this tile by a different piece.
     * @param p the piece that is to be placed on the tile
     */
    public void setPiece(AbstractGamePiece p)
    {
        piece = p;
    }

    /**
     * Trigger for when a game piece is placed on this tile.
     * @param piece the placed piece
     */
    public void onPiecePlaced(AbstractGamePiece piece) {}

    /**
     * Trigger for when the game piece placed on this tile is rotated.
     * @param piece the rotated piece
     */
    public void onPieceRotated(AbstractGamePiece piece) {}

    /**
     * Trigger for when the game piece is removed from this tile.
     * @param piece the rotated piece
     */
    public void onPieceDestroyed(AbstractGamePiece piece) {}

    /**
     * Trigger for when a left mouse button is clicked and released within this tile.
     */
    public void onLeftClick() {}

    /**
     * Trigger for when a right mouse button is clicked and released within this tile.
     */
    public void onRightClick()
    {
        if(piece != null)
        {
            piece.rotateRight();
            onPieceRotated(piece);
        }
    }

    /**
     * Render the board
     *
     * @param sb the board SpriteBatch
     * @param x the x-coordinate of tile
     * @param y the y-coordinate of tile
     * @param width the width(dimention) of the tile
     * @param height the height(dimension) of the tile
     */
    public void render(SpriteBatch sb, int x, int y, int width, int height)
    {
        render(sb, x, y, width, height, false);
    }

    /**
     * Render the tile, using the draw method of SpriteBatch class.
     *
     * @param sb the board SpriteBatch
     * @param x the x-coordinate of tile
     * @param y the y-coordinate of tile
     * @param width the width(dimention) of the tile
     * @param height the height(dimension) of the tile
     * @param flipped boolean of whether the board is flipped
     */
    public void render(SpriteBatch sb, int x, int y, int width, int height, boolean flipped)
    {
        sb.draw(texture, x, y, width/2F, height/2F, width, height, 1, 1, 0, 0, 0, IMG_DIM, IMG_DIM, flipped, flipped);
    }

    /**
     * Load the texture on the tile using the path of image.
     * @param image the path of image
     */
    protected void loadRegion(String image)
    {
        texture = LaserGame.loadTexture(image);
    }

    /**
     * Load the texture on the tile using the type of image.
     * @param type the type of the tile
     */
    protected void loadRegion(AbstractBoardTile.TileType type)
    {
        loadRegion(getPathFromTileType(type));
    }

    /**
     * Load the texture on the tile using a base path and the type of tile.
     * @param basePath the base path of image
     * @param type the type of the tile
     */
    protected void loadRegion(String basePath, AbstractBoardTile.TileType type)
    {
        loadRegion(basePath + getPathFromTileType(type));
    }

    public void loadRegion()
    {
        loadRegion(TileType.CENTER);
    }

    /**
     * Utility function which converts a tile type into a file path.
     *
     * @param type the tile type
     * @return path to the appropriate texture image
     */
    private String getPathFromTileType(AbstractBoardTile.TileType type)
    {
        switch(type)
        {
            case UL_CORNER:
                return "ulcorner.png";
            case UPPER_EDGE:
                return "upperedge.png";
            case UR_CORNER:
                return "urcorner.png";
            case LEFT_EDGE:
                return "leftedge.png";
            case CENTER:
                return "center.png";
            case RIGHT_EDGE:
                return "rightedge.png";
            case LL_CORNER:
                return "llcorner.png";
            case LOWER_EDGE:
                return "loweredge.png";
            case LR_CORNER:
                return "lrcorner.png";
            default:
                return "center.png";
        }
    }
}
