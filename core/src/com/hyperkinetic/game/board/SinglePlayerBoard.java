package com.hyperkinetic.game.board;

public abstract class SinglePlayerBoard extends AbstractGameBoard
{
    private boolean hasTurn;

    public SinglePlayerBoard(int x, int y, boolean hasTurn)
    {
        super(x, y, hasTurn);
    }

    @Override
    public void createTiles() {}

    @Override
    public void createPieces() {}

    public static SinglePlayerBoard getInstance(AbstractGameBoard board, boolean turn)
    {
        SinglePlayerBoard retval = new SinglePlayerBoard(board.x, board.y, turn)
        {
            @Override
            public String getGameState() {
                if(bPharaoh==null) {
                    return "AWin";
                } else if (aPharaoh==null) {
                    return "BWin";
                }
                return "NoWin";
            }
        };
        retval.pieces = board.pieces;
        retval.tiles = board.tiles;
        retval.aPharaoh = board.aPharaoh;
        retval.bPharaoh = board.bPharaoh;
        retval.aLaser = board.aLaser;
        retval.bLaser = board.bLaser;

        return retval;
    }
}
