package edu.cwru.sepia.agent.planner;

import edu.cwru.sepia.environment.model.state.ResourceNode;
import edu.cwru.sepia.util.Direction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Devin on 3/15/15.
 *
 * I've provided you with a simple Position class with some helper methods. Use this for any place you need to track
 * a location. If you need modify the methods and add new ones. If you make changes add a note here about what was
 * changed and why.
 *
 * This class is immutable, meaning any changes creates an entirely separate copy.
 */
public class Position {

    public final int x;
    public final int y;
    
    //Added in these two fields to help keep track of the resources
    public ResourceNode.Type type;
    public int amountLeft;
    
    //A constructor for the peasant position
    public Position(int x, int y) {
    	this.x = x;
    	this.y = y;
    	type = null;
    	amountLeft = 0;
    }
    //Another constructor for the resource location
    public Position(int x, int y, ResourceNode.Type type, int amount) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.amountLeft = amount;
    }

    /**
     * Deep copy of specified position.
     *
     * @param pos Position to copy
     */
    public Position(Position pos) {
        x = pos.x;
        y = pos.y;
        this.type = pos.type;
        this.amountLeft = pos.amountLeft;
    }

    /**
     * Gives the position one step in the specified direciton.
     *
     * @param direction North, south, east, etc.
     * @return Position one step away
     */
    public Position move(Direction direction) {
        return new Position(direction.xComponent() + x, direction.yComponent() + y, type, amountLeft);
    }

    /**
     * Returns a list of adjacent positions. This method does not check
     * if the positions are valid. So it may return locations outside of the
     * map bounds or positions that are occupied by other objects.
     *
     * @return List of adjacent positions
     */
    public List<Position> getAdjacentPositions() {
        List<Position> positions = new ArrayList<Position>();

        for (Direction direction : Direction.values()) {
            positions.add(move(direction));
        }

        return positions;
    }

    /**
     * Check if the position is within the map. Does not check if the position is occupied
     *
     * @param xExtent X dimension size of the map (get this from the StateView object)
     * @param yExtent Y dimension size of the map (get this from the StateView object)
     * @return True if in bounds, false otherwise.
     */
    public boolean inBounds(int xExtent, int yExtent) {
        return (x >= 0 && y >= 0 && x < xExtent && y < yExtent);
    }

    /**
     * Calculates the Euclidean distance between this position and another.
     * May be useful for your heuristic.
     *
     * @param position Other position to get distance to
     * @return Euclidean distance between two positions
     */
    public double euclideanDistance(Position position) {
        return Math.sqrt(Math.pow(x - position.x, 2) + Math.pow(y - position.y, 2));
    }

    /**
     * Calculates the Chebyshev distance between this position and another.
     * May be useful for your heuristic.
     *
     * @param position Other position to get distance to
     * @return Chebyshev distance between two positions
     */
    public int chebyshevDistance(Position position) {
        return Math.max(Math.abs(x - position.x), Math.abs(y - position.y));
    }

    /**
     * True if the specified position can be reached in one step. Does not check if the position
     * is in bounds.
     *
     * @param position Position to check for adjacency
     * @return true if adjacent, false otherwise
     */
    public boolean isAdjacent(Position position) {
        return Math.abs(x - position.x) <= 1 && Math.abs(y - position.y) <= 1;
    }

    /**
     * Get the direction for an adjacent position.
     *
     * @param position Adjacent position
     * @return Direction to specified adjacent position
     */
    public Direction getDirection(Position position) {
        int xDiff = position.x - x;
        int yDiff = position.y - y;

        // figure out the direction the footman needs to move in
        if(xDiff == 1 && yDiff == 1)
        {
            return Direction.SOUTHEAST;
        }
        else if(xDiff == 1 && yDiff == 0)
        {
            return Direction.EAST;
        }
        else if(xDiff == 1 && yDiff == -1)
        {
            return Direction.NORTHEAST;
        }
        else if(xDiff == 0 && yDiff == 1)
        {
            return Direction.SOUTH;
        }
        else if(xDiff == 0 && yDiff == -1)
        {
            return Direction.NORTH;
        }
        else if(xDiff == -1 && yDiff == 1)
        {
            return Direction.SOUTHWEST;
        }
        else if(xDiff == -1 && yDiff == 0)
        {
            return Direction.WEST;
        }
        else if(xDiff == -1 && yDiff == -1)
        {
            return Direction.NORTHWEST;
        }

        System.err.println("Position not adjacent. Could not determine direction");
        return null;
    }

    @Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Position)) {
			return false;
		}
		Position other = (Position) obj;
		if (amountLeft != other.amountLeft) {
			return false;
		}
		if (x != other.x) {
			return false;
		}
		if (y != other.y) {
			return false;
		}
		return true;
	}

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + amountLeft;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

    /**
     * @return human readable string representation.
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
