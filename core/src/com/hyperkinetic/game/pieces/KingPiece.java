package com.hyperkinetic.game.pieces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.hyperkinetic.game.core.LaserGame;
import com.hyperkinetic.game.util.Directions;

public class KingPiece extends AbstractBlockPiece {
    public KingPiece(int x, int y, boolean c, Directions.Direction o) {
        super(x, y, c, o);
    }
    public KingPiece() {}

    @Override
    public Array<Directions.Direction> acceptLaser(Directions.Direction laserDirection) {
        return null;
    }

    @Override
    public void loadRegion()
    {
        textures[0] = textures[1] = textures[2] = textures[3] =
                LaserGame.loadTexture("pieces/King" + (!color ? "Red.png" : ".png"));
    }

    @Override
    public boolean equals(Object other)
    {
        if(!(other instanceof KingPiece)) return false;
        KingPiece otherPiece = (KingPiece) other;
        return otherPiece.color == color && otherPiece.orientation == orientation &&
                otherPiece.x == x && otherPiece.y == y;
    }
}
