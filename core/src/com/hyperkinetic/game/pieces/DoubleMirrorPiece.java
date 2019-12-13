package com.hyperkinetic.game.pieces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.hyperkinetic.game.core.LaserGame;
import com.hyperkinetic.game.util.Directions;

public class DoubleMirrorPiece extends AbstractMirrorPiece {
    public DoubleMirrorPiece(int x, int y, boolean c, Directions.MirrorDirection orientation) {
        super(x, y, c, orientation);
    }

    public DoubleMirrorPiece () {}

    @Override
    public Array<Directions.Direction> acceptLaser(Directions.Direction laserDirection) {
        Directions.MirrorDirection oppositeSide = null;

        if(orientation == Directions.MirrorDirection.NORTHWEST)
            oppositeSide = Directions.MirrorDirection.SOUTHEAST;
        else if(orientation == Directions.MirrorDirection.NORTHEAST)
            oppositeSide = Directions.MirrorDirection.SOUTHWEST;
        else if (orientation == Directions.MirrorDirection.SOUTHWEST)
            oppositeSide = Directions.MirrorDirection.NORTHEAST;
        else if(orientation == Directions.MirrorDirection.SOUTHEAST)
            oppositeSide = Directions.MirrorDirection.NORTHWEST;

        Directions.Direction reflectionDir = Directions.reflect(orientation, laserDirection);
        Directions.Direction oppositeReflectionDir = Directions.reflect(oppositeSide, laserDirection);
        Array<Directions.Direction> reflectedLaserDirection = new Array<>();

        if(reflectionDir == null){
            reflectedLaserDirection.add(oppositeReflectionDir);
        }
        else if (oppositeReflectionDir == null) {
            reflectedLaserDirection.add(reflectionDir);
        }
        reflectedLaserDirection.add(laserDirection);
        return reflectedLaserDirection;
    }

    @Override
    public void loadRegion()
    {
        textures[0] = textures[2] = LaserGame.loadTexture("pieces/doublemirror" + (!color ? "Red2.png" : "2.png"));
        textures[1] = textures[3] = LaserGame.loadTexture("pieces/doublemirror" + (!color ? "Red1.png" : "1.png"));
    }
}
