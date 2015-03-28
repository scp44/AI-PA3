package edu.cwru.sepia.agent.planner.actions;

import java.util.HashSet;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Position;
import edu.cwru.sepia.environment.model.state.ResourceNode;

public class HarvestAction implements StripsAction {

	public String actionType = "Harvest";
	public Position resLoc;
	public int mapX, mapY;
	public int carriedAmount;
	public int unitIndex;
	
	public HarvestAction(Position resource, int unitIndex, int mapX, int mapY, int unitCarriedAmount) {
		this.mapX = mapX;
		this.mapY = mapY;
		this.resLoc = resource;
		this.unitIndex = unitIndex;
		this.carriedAmount = unitCarriedAmount;
	}
	
	public String toString() {
		return "Harvest(" + this.unitIndex + ", " + resLoc.x + ", " + resLoc.y + ", " + resLoc.type.toString() + ")";
	}
	
	public int getID() {
		return this.resLoc.resourceID;
	}
	
	public String actionType() {
		return this.actionType;
	}
	
	public int getUnitIndex() {
		return this.unitIndex;
	}
	
	@Override
	public boolean preconditionsMet(GameState state) {
		//Check to make sure that the resource location is within the boundaries of the map and that the
		//amount of resources left is not 0, and the unit is emptyhanded
		return (resLoc.x >= 0 && resLoc.x <= mapX && resLoc.y >= 0 && resLoc.y <= mapY &&
				resLoc.amountLeft > 0 && carriedAmount == 0);
	}

	@Override
	public GameState apply(GameState state) {
		//I am not going to use this method, because in the generateChildren() method of GameState I'm already keeping track
		//of the resulting state of applying this action 
		return null;
	}

}
