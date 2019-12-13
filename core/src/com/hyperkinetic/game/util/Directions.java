package com.hyperkinetic.game.util;

/**
 * Wrapper class containing public variables to describe the directions of lasers
 * and mirrors. Contains common utility methods for calculating laser reflection.
 *
 * @author cqwillia
 */
public class Directions
{
    /**
     * Describes the direction of lasers based on the direction of their movement.
     */
    public enum Direction
    {
        NORTH,
        WEST,
        SOUTH,
        EAST
    }

    /**
     * Describes the direction of mirrors based on the trajectory of a vector
     * drawn tangent to their surface.
     */
    public enum MirrorDirection
    {
        NORTHWEST,
        NORTHEAST,
        SOUTHEAST,
        SOUTHWEST
    }

    /*public static boolean singleMirrorReflected(MirrorDirection mirror, Direction laser)
    {
        if(mirror == MirrorDirection.NORTHWEST)
            return laser == Direction.SOUTH || laser == Direction.EAST;
        if(mirror == MirrorDirection.NORTHEAST)
            return laser == Direction.SOUTH || laser == Direction.WEST;
        if(mirror == MirrorDirection.SOUTHWEST)
            return laser == Direction.NORTH || laser == Direction.EAST;
        if(mirror == MirrorDirection.SOUTHEAST)
            return laser == Direction.NORTH || laser == Direction.WEST;

        return false;
    }*/

    /**
     * Calculates the resultant direction of a laser bouncing off of a mirror.
     *
     * @param mirror the {@link MirrorDirection} describing the orientation of the mirror.
     * @param laser the {@link Direction} describing the direction of the laser.
     *              note: if laser = south, it means coming from north, going to south
     * @return the direction in which the laser will travel after incidence with the mirror,
     * or <code>null</code> if the mirror does not reflect the laser.
     */
    public static Direction reflect(MirrorDirection mirror, Direction laser)
    {
        if(mirror == null || laser == null) return null;

        if(mirror == MirrorDirection.NORTHWEST)
        {
            if(laser == Direction.SOUTH) return Direction.WEST;
            if(laser == Direction.EAST) return Direction.NORTH;
        }
        else if(mirror == MirrorDirection.NORTHEAST)
        {
            if(laser == Direction.SOUTH) return Direction.EAST;
            if(laser == Direction.WEST) return Direction.NORTH;
        }
        else if(mirror == MirrorDirection.SOUTHEAST)
        {
            if(laser == Direction.NORTH) return Direction.EAST;
            if(laser == Direction.WEST) return Direction.SOUTH;
        }
        else
        {
            if(laser == Direction.NORTH) return Direction.WEST;
            if(laser == Direction.EAST) return Direction.SOUTH;
        }

        return null;
    }
}
