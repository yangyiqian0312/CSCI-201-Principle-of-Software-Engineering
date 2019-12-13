package com.hyperkinetic.game.pieces;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.hyperkinetic.game.util.Directions;

import static com.hyperkinetic.game.util.Directions.Direction.*;

/**
 * Abstract superclass describing the behaviour of a block piece.
 *
 * @author cqwillia briannlz
 */
public abstract class AbstractBlockPiece extends AbstractGamePiece
{
    protected Directions.Direction orientation;

    public AbstractBlockPiece(int x, int y, boolean c, Directions.Direction o) {
        super(x, y, c);
        this.orientation = o;
    }

    public AbstractBlockPiece()
    {
        orientation = NORTH;
    }

    public Directions.Direction getOrientation() {
        return orientation;
    }

    /**
     * Transforms the orientation of this piece appropriately for one quarter turn clockwise.
     */
    public void rotateRight()
    {
        if(orientation == null) return;

        if(orientation == NORTH)
            orientation = Directions.Direction.EAST;
        else if(orientation == Directions.Direction.EAST)
            orientation = SOUTH;
        else if(orientation == SOUTH)
            orientation = WEST;
        else
            orientation = NORTH;
    }

    /**
     * Transforms the orientation of this piece appropriately for one quarter turn counterclockwise.
     */
    public void rotateLeft()
    {
        if(orientation == null) return;

        if(orientation == NORTH)
            orientation = WEST;
        else if(orientation == WEST)
            orientation = SOUTH;
        else if(orientation == SOUTH)
            orientation = Directions.Direction.EAST;
        else
            orientation = NORTH;
    }

    /**
     * Defines the behaviour of a laser when it encounters this game piece.
     *
     * @param laserDirection the direction of the incoming laser
     * @return an {@link Array} of Directions representing the outgoing lasers from this piece,
     * or null if the laser is not reflected.
     */
    public abstract Array<Directions.Direction> acceptLaser(Directions.Direction laserDirection);

    @Override
    protected Texture getTexture()
    {
        switch(orientation)
        {
            case NORTH:
                return textures[0];
            case EAST:
                return textures[1];
            case SOUTH:
                return textures[2];
            case WEST:
                return textures[3];
        }

        return null;
    }
    
    @Override
    protected void flipOrientation()
    {
        switch(orientation)
        {
            case NORTH:
                orientation = SOUTH;
                break;
            case EAST:
                orientation = WEST;
                break;
            case SOUTH:
                orientation = NORTH;
                break;
            case WEST:
                orientation = EAST;
        }
    }
}
