package com.hyperkinetic.game.board;

/**
 * The normal blank tile with no added effects. There are 62 of these in the standard
 * laser game configuration.
 *
 * @author cqwillia
 */
public class StandardTile extends AbstractBoardTile
{
    private TileType type;

    public StandardTile(AbstractBoardTile.TileType type)
    {
        this.type = type;
    }

    public StandardTile()
    {
        this.type = TileType.CENTER;
    }

    @Override
    public void loadRegion()
    {
        //super.loadRegion("board/", type);
        super.loadRegion("board/tile.png");
    }
}
