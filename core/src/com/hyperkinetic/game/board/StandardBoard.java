package com.hyperkinetic.game.board;

import com.badlogic.gdx.utils.Array;
import com.hyperkinetic.game.pieces.*;
import com.hyperkinetic.game.util.Directions;

/**
 * Defines the standard 8x8 laser game board with laser tiles in the upper left
 * and lower right corners.
 *
 * @author cqwillia
 */
public class StandardBoard extends AbstractGameBoard
{
    public StandardBoard(boolean hasTurn)
    {
        super(10, 8, hasTurn);
        createTiles();
        createPieces();
    }
    public StandardBoard(boolean hasTurn, boolean local)
    {
        super(10, 8, hasTurn, local);
        createTiles();
        createPieces();
    }
    public StandardBoard()
    {
        this(true);
    }

    @Override
    public void createTiles()
    {
        for(int i = 0; i < y; i++)
        {
            for(int j = 0; j < x; j++)
            {
                if(j == 0)
                {
                    if(i == 0)
                        tiles.add(new StandardTile(AbstractBoardTile.TileType.LL_CORNER));
                    else if(i == y-1)
                        tiles.add(new LaserTile());
                    else
                        tiles.add(new StandardTile(AbstractBoardTile.TileType.LEFT_EDGE));
                }
                else if(i == 0)
                {
                    if(j == x-1)
                        tiles.add(new LaserTile());
                    else
                        tiles.add(new StandardTile(AbstractBoardTile.TileType.LOWER_EDGE));
                }
                else if(j == x-1)
                {
                    if(i == y-1)
                        tiles.add(new StandardTile(AbstractBoardTile.TileType.UR_CORNER));
                    else
                        tiles.add(new StandardTile(AbstractBoardTile.TileType.RIGHT_EDGE));
                }
                else if(i == y-1)
                {
                    tiles.add(new StandardTile(AbstractBoardTile.TileType.UPPER_EDGE));
                }
                else
                {
                    tiles.add(new StandardTile(AbstractBoardTile.TileType.CENTER));
                }
            }
        }
    }

    @Override
    public void createPieces() {
        pieces = new Array<>();
        for(int i = 0; i < x * y; i++)
            pieces.add(null);

        // populate pieces, aPieces, bPieces, place pieces on board, initiate aPharaoh, bPharaoh, aLaser, bLaser
        // i = row, j = column, starting at 0 in the lower left corner
        for(int i = 0 ; i < y; i++)
        {
            for(int j = 0; j < x; j++)
            {
                AbstractGamePiece newPiece = null;
                // add single mirror pieces - white
                if((i == 0 && j == 2) || (i == 3 && j == 2) || (i == 5 && j == 3) || (i == 4 && j == 9))
                   newPiece = new SingleMirrorPiece(j, i, true, Directions.MirrorDirection.NORTHWEST);
                else if(i == 1 && j == 7)
                    newPiece = new SingleMirrorPiece(j, i, true, Directions.MirrorDirection.NORTHEAST);
                else if((i == 4 && j == 2) || (i == 3 && j == 9))
                    newPiece = new SingleMirrorPiece(j, i, true, Directions.MirrorDirection.SOUTHWEST);

                // add single mirror pieces - black
                else if((i == 3 && j == 0) || (i == 2 && j == 6) || (i == 4 && j == 7) || (i == 7 && j == 7))
                    newPiece = new SingleMirrorPiece(j, i, false, Directions.MirrorDirection.SOUTHEAST);
                else if((i == 4 && j == 0) || (i == 3 && j == 7))
                    newPiece = new SingleMirrorPiece(j, i, false, Directions.MirrorDirection.NORTHEAST);
                else if(i == 6 && j == 2)
                    newPiece = new SingleMirrorPiece(j, i, false, Directions.MirrorDirection.SOUTHWEST);

                // add double mirror pieces - white
                else if(i == 3 && j == 4)
                    newPiece = new DoubleMirrorPiece(j, i, true, Directions.MirrorDirection.NORTHWEST);
                else if(i == 3 && j == 5)
                    newPiece = new DoubleMirrorPiece(j, i, true, Directions.MirrorDirection.NORTHEAST);

                // add double mirror pieces - black
                else if(i == 4 && j == 4)
                    newPiece = new DoubleMirrorPiece(j, i, false, Directions.MirrorDirection.NORTHEAST);
                else if(i == 4 && j == 5)
                    newPiece = new DoubleMirrorPiece(j, i, false, Directions.MirrorDirection.NORTHWEST);

                // add guardian pieces
                else if((i == 0 && j == 3) || (i == 0 && j == 5))
                    newPiece = new GuardianPiece(j, i, true, Directions.Direction.NORTH);
                else if((i == 7 && j == 4) || (i == 7 && j == 6))
                    newPiece = new GuardianPiece(j, i, false, Directions.Direction.SOUTH);

                // add king pieces
                else if(i == 0 && j == 4)
                {
                    newPiece = new KingPiece(j, i, true, Directions.Direction.NORTH);
                    this.aPharaoh = (KingPiece)newPiece;
                }
                else if(i == 7 && j == 5)
                {
                    newPiece = new KingPiece(j, i, false, Directions.Direction.SOUTH);
                    this.bPharaoh = (KingPiece)newPiece;
                }

                // add laser pieces
                else if(i == 0 && j == 9)
                {
                    newPiece = new LaserPiece(j, i, true, Directions.Direction.NORTH);
                    this.aLaser = (LaserPiece)newPiece;
                }
                else if(i == 7 && j == 0)
                {
                    newPiece = new LaserPiece(j, i, false, Directions.Direction.SOUTH);
                    this.bLaser = (LaserPiece)newPiece;
                }

                this.pieces.set(i * x + j, newPiece);
            }
        }
    }

    @Override
    public String getGameState() {
        if(bPharaoh==null) {
            return "AWin";
        } else if (aPharaoh==null) {
            return "BWin";
        }
        return "NoWin";
    }

}
