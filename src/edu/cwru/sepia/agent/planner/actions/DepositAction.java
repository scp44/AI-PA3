package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Position;

public class DepositAction implements StripsAction {

	public String actionType = "Deposit";
	public Position townHallLoc;
	public String resType;
	public int mapX, mapY;
	public int carriedAmount;
	public int unitIndex;
	
	public DepositAction(Position dest, int unitIndex, String type, int mapX, int mapY, int unitCarriedAmount) {
		this.mapX = mapX;
		this.mapY = mapY;
		this.resType = type;
		this.townHallLoc = dest;
		this.carriedAmount = unitCarriedAmount;
		this.unitIndex = unitIndex;
	}
	
	public String toString() {
		return "Deposit(" + this.unitIndex + ", " + carriedAmount + ")";
	}

	public String actionType() {
		return this.actionType;
	}
	
	
	//Note: for deposit actions, getID() is defunct, because we don't care about the resource ID here
	public int getID() {
		return -1;
	}
	@Override
	public GameState apply(GameState state) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean preconditionsMet(GameState state) {
		//Precondition: the unit must be carrying the right resource, and must carry > 0 of that resource
		//and that the destination (town hall) is within the boundaries of the map
		
		return state.units[0].carriedResAmount > 0 && state.units[0].resType == this.resType &&
				townHallLoc.x >= 0 && townHallLoc.x <= mapX && townHallLoc.y >= 0 && townHallLoc.y <= mapY;
		//return (townHallLoc.x >= 0 && townHallLoc.x <= mapX && townHallLoc.y >= 0 && townHallLoc.y <= mapY
		//		&& carriedAmount > 0);
	}

}
