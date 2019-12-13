package com.hyperkinetic.game.pieces;

import com.badlogic.gdx.utils.Array;
import com.hyperkinetic.game.core.LaserGame;
import com.hyperkinetic.game.util.Directions;

public class GuardianPiece extends AbstractBlockPiece {

    public GuardianPiece(int x, int y, boolean c, Directions.Direction o) {
        super(x, y, c, o);
    }
    public GuardianPiece() {}

    @Override
    public Array<Directions.Direction> acceptLaser(Directions.Direction laserDirection) {
        Array<Directions.Direction> retval = new Array<>();
        
        if (this.orientation ==  Directions.Direction.EAST && laserDirection != Directions.Direction.WEST)
            return null;

        else if (this.orientation ==  Directions.Direction.WEST && laserDirection != Directions.Direction.EAST)
            return null;

        else if (this.orientation ==  Directions.Direction.NORTH && laserDirection != Directions.Direction.SOUTH)
            return null;

        else if (this.orientation ==  Directions.Direction.SOUTH && laserDirection != Directions.Direction.NORTH)
            return null;

        return retval;
    }

    @Override
    public void loadRegion()
    {
        textures[0] = LaserGame.loadTexture("pieces/guardianback" + (!color ? "Red.png" : ".png"));
        textures[1] = LaserGame.loadTexture("pieces/guardian" + (!color ? "Red1.png" : "1.png"));
        textures[2] = LaserGame.loadTexture("pieces/guardianfront" + (!color ? "Red.png" : ".png"));
        textures[3] = LaserGame.loadTexture("pieces/guardian" + (!color ? "Red2.png" : "2.png"));
    }
}
