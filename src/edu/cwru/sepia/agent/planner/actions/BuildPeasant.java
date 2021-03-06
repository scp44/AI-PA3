package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;

public class BuildPeasant implements StripsAction {

	public int townhallID;
	public String actionType = "BuildPeasant";
	
	public BuildPeasant(int townhall) {
		this.townhallID = townhall;
	}
	@Override
	public boolean preconditionsMet(GameState state) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public GameState apply(GameState state) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String actionType() {
		return this.actionType;
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getUnitIndex() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public String toString() {
		return "BuildPeasant()";
	}
	
	//Returns a deep copy of this action object
	public BuildPeasant clone() {
		BuildPeasant dummy = new BuildPeasant(this.townhallID);
		dummy.actionType = "BuildPeasant";
		return dummy;
	}

}
