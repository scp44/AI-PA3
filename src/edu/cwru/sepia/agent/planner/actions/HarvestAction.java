package edu.cwru.sepia.agent.planner.actions;

import java.util.HashSet;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Position;

public class HarvestAction implements StripsAction {

	public String actionType = "Harvest";
	public Position resLoc;
	public int mapX, mapY;
	public int carriedAmount;
	
	public HarvestAction(Position resource, int mapX, int mapY, int unitCarriedAmount) {
		this.mapX = mapX;
		this.mapY = mapY;
		this.resLoc = resource;
		this.carriedAmount = unitCarriedAmount;
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
		// TODO Auto-generated method stub
		return null;
	}

}
