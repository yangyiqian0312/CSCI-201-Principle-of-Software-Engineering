package com.hyperkinetic.game.board;

/**
 * A tile which, when clicked, emits a laser and ends the current player's turn.
 *
 * @author cqwillia
 */
public class LaserTile extends AbstractBoardTile
{
    public LaserTile()
    {
        super();
    }

    @Override
    public void loadRegion()
    {
        super.loadRegion("board/tileRed.png");
    }
}
