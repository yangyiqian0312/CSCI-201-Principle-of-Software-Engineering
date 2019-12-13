package com.hyperkinetic.game.pieces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.hyperkinetic.game.core.LaserGame;
import com.hyperkinetic.game.util.Directions;

// can only rotate
public class LaserPiece extends AbstractBlockPiece{
    public LaserPiece(int x, int y, boolean c, Directions.Direction o) {
        super(x, y, c, o);
    }
    public LaserPiece() {}

    @Override
    public Array<Directions.Direction> acceptLaser(Directions.Direction laserDirection) {
        Array<Directions.Direction> retval = new Array<>();
        retval.add(laserDirection);
        return retval;
    }

    @Override
    public boolean equals(Object other)
    {
        if(!(other instanceof LaserPiece)) return false;
        LaserPiece o = (LaserPiece) other;
        return o.color == this.color && o.x == this.x && o.y == this.y;
    }
    
    @Override
    public void loadRegion()
    {
        textures[0] = LaserGame.loadTexture("pieces/laserpieceback" + (!color ? "Red.png" : ".png"));
        textures[1] = LaserGame.loadTexture("pieces/laserpiece" + (!color ? "Red1.png" : "1.png"));
        textures[2] = LaserGame.loadTexture("pieces/laserpiecefront" + (!color ? "Red.png" : ".png"));
        textures[3] = LaserGame.loadTexture("pieces/laserpiece" + (!color ? "Red2.png" : "2.png"));
    }
    
    public String toggleDirection()
    {
        if(color)
        {
            if(orientation == Directions.Direction.NORTH)
                return "rotateL";
            else
                return "rotateR";
        }
        else
        {
            if(orientation == Directions.Direction.SOUTH)
                return "rotateL";
            else
                return "rotateR";
        }
    }
}
