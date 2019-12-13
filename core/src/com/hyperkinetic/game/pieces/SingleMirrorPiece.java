package com.hyperkinetic.game.pieces;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.hyperkinetic.game.core.LaserGame;
import com.hyperkinetic.game.util.Directions;

/**
 * A piece with only one mirror and three unreflective sides.
 *
 * @author cqwillia briannlz
 */
public class SingleMirrorPiece extends AbstractMirrorPiece
{
    public SingleMirrorPiece(int x, int y, boolean c)
    {
        super(x, y, c, Directions.MirrorDirection.NORTHWEST);
        System.out.println();
    }

    public SingleMirrorPiece(int x, int y, boolean c, Directions.MirrorDirection o)
    {
        super(x, y, c, o);
    }
    public SingleMirrorPiece() {}

    @Override
    public Array<Directions.Direction> acceptLaser(Directions.Direction laserDirection)
    {
        Directions.Direction dir = Directions.reflect(this.orientation, laserDirection);
        if(dir == null) return null;

        Array<Directions.Direction> reflectedLaserDirection = new Array<>();
        reflectedLaserDirection.add(dir);
        return reflectedLaserDirection;
    }
    
    @Override
    public void loadRegion()
    {
        textures[0] = LaserGame.loadTexture("pieces/singlemirrorback" + (!color ? "Red1.png" : "1.png"));
        textures[1] = LaserGame.loadTexture("pieces/singlemirrorback" + (!color ? "Red2.png" : "2.png"));
        textures[2] = LaserGame.loadTexture("pieces/singlemirror" + (!color ? "Red2.png" : "2.png"));
        textures[3] = LaserGame.loadTexture("pieces/singlemirror" + (!color ? "Red1.png" : "1.png"));
    }
}
