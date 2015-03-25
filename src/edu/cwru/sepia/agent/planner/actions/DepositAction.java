package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Position;

public class DepositAction implements StripsAction {

	public String actionType = "Move";
	public Position townHallLoc;
	public int mapX, mapY;
	public int carriedAmount;
	
	public DepositAction(Position dest, int mapX, int mapY, int unitCarriedAmount) {
		this.mapX = mapX;
		this.mapY = mapY;
		this.townHallLoc = dest;
		this.carriedAmount = unitCarriedAmount;
	}

	@Override
	public GameState apply(GameState state) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean preconditionsMet(GameState state) {
		//Precondition: the destination must be within the boundaries of the map, and the unit must be carrying
		//resources
		return (townHallLoc.x >= 0 && townHallLoc.x <= mapX && townHallLoc.y >= 0 && townHallLoc.y <= mapY
				&& carriedAmount > 0);
	}

}
