package edu.cwru.sepia.agent.planner;




import edu.cwru.sepia.agent.planner.actions.BuildPeasant;
import edu.cwru.sepia.agent.planner.actions.DepositAction;
import edu.cwru.sepia.agent.planner.actions.HarvestAction;
import edu.cwru.sepia.agent.planner.actions.StripsAction;
import edu.cwru.sepia.environment.model.state.ResourceNode;
import edu.cwru.sepia.environment.model.state.ResourceType;
import edu.cwru.sepia.environment.model.state.State;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * This class is used to represent the state of the game after applying one of the avaiable actions. It will also
 * track the A* specific information such as the parent pointer and the cost and heuristic function. Remember that
 * unlike the path planning A* from the first assignment the cost of an action may be more than 1. Specifically the cost
 * of executing a compound action such as move can be more than 1. You will need to account for this in your heuristic
 * and your cost function.
 *
 * The first instance is constructed from the StateView object (like in PA2). Implement the methods provided and
 * add any other methods and member variables you need.
 *
 * Some useful API calls for the state view are
 *
 * state.getXExtent() and state.getYExtent() to get the map size
 *
 * I recommend storing the actions that generated the instance of the GameState in this class using whatever
 * class/structure you use to represent actions.
 */
public class GameState implements Comparable<GameState> {

	
	
	private int requiredGold;
	private int requiredWood;
	public int collectedGold;
	public int collectedWood;
	public boolean buildPeasant;
	private int playerNum;
	private List<Integer> unitIDs = new ArrayList<Integer>();
	public HashSet<Position> goldLocations = new HashSet<Position>();
	public HashSet<Position> woodLocations = new HashSet<Position>();
	public ArrayList<UnitState> units = new ArrayList<UnitState>();
	private int mapXExtent;
	private int mapYExtent;
	public GameState parent;
	public double cost, estTotalCost;
	public ArrayList<StripsAction> prevActions = new ArrayList<StripsAction>();
	private Position townhallPos;
	public int foodAmount;
	private int townhallID;

	
	//An inner class to keep track of the state of the peasant units
	public class UnitState {
		
		
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			//result = prime * result + getOuterType().hashCode();
			result = prime * result + carriedResAmount;
			result = prime * result
					+ ((resType == null) ? 0 : resType.hashCode());
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof UnitState)) {
				return false;
			}
			UnitState other = (UnitState) obj;
			/*if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}*/
			if (carriedResAmount != other.carriedResAmount) {
				return false;
			}
			if (resType == null) {
				if (other.resType != null) {
					return false;
				}
			} else if (!resType.equals(other.resType)) {
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
		//Fields to keep track of an individual peasant
		public int x, y;
		public int carriedResAmount;
		public String resType;
		public UnitState(int x, int y, int resAmount, String resType) {
			this.x = x;
			this.y = y;
			this.carriedResAmount = resAmount;
			if(resType == null) {
				this.resType = null;
			}
			else 
				this.resType = new String(resType);
		}
		//A separate constructor to perform deep copy
		public UnitState(UnitState unitState) {
			this.x = unitState.x;
			this.y = unitState.y;
			this.carriedResAmount = unitState.carriedResAmount;
			this.resType = new String(unitState.resType);
		}
		/*private GameState getOuterType() {
			return GameState.this;
		}*/
	}
	
	//A constructor that performs a deep copy of a GameState
	public GameState(GameState state) {
		this.requiredGold = state.requiredGold;
		this.requiredWood = state.requiredWood;
		this.buildPeasant = state.buildPeasant;
		this.playerNum = state.playerNum;
		this.collectedGold = state.collectedGold;
		this.collectedWood = state.collectedWood;
		this.mapXExtent = state.mapXExtent;
		this.mapYExtent = state.mapYExtent;
		this.unitIDs.addAll(0, state.unitIDs);
		this.goldLocations = new HashSet<Position>();
		for(Position p : state.goldLocations) {
			this.goldLocations.add(new Position(p.x, p.y, p.type, p.resourceID, p.amountLeft));
		}
		this.woodLocations = new HashSet<Position>();
		for(Position p : state.woodLocations) {
			this.woodLocations.add(new Position(p.x, p.y, p.type, p.resourceID, p.amountLeft));
		}
		this.units = new ArrayList<UnitState>();
		for (int i = 0; i < state.units.size(); i++) {
			this.units.add(i, new UnitState(state.units.get(i)));
		}
		//Copy over the prevActions as well, we'll need this for handling multiple units (and their actions)
		this.prevActions = new ArrayList<StripsAction>();
		for(int i = 0; i < state.prevActions.size(); i++) {
			this.prevActions.add(i, state.prevActions.get(i).clone());
		}
		//Are you sure you want to copy the GameState's parent pointer as well???
		this.parent = state;
		//this.cost = this.getCost() + this.parent.cost;
		//this.estTotalCost = this.cost + this.heuristic();
		//This is the only case where we can directly shallow copy the parent pointer
		//because the townhall location is constant for all game states
		this.townhallPos = state.townhallPos;
		this.foodAmount = state.foodAmount;
		this.townhallID = state.townhallID;
		//StripsAction prevAction;
	}
    /**
     * Construct a GameState from a stateview object. This is used to construct the initial search node. All other
     * nodes should be constructed from the another constructor you create or by factory functions that you create.
     *
     * @param state The current stateview at the time the plan is being created
     * @param playernum The player number of agent that is planning
     * @param requiredGold The goal amount of gold (e.g. 200 for the small scenario)
     * @param requiredWood The goal amount of wood (e.g. 200 for the small scenario)
     * @param buildPeasants True if the BuildPeasant action should be considered
     */
    public GameState(State.StateView state, int playernum, int requiredGold, int requiredWood, boolean buildPeasants) {
        this.requiredGold = requiredGold;
        this.requiredWood = requiredWood;
        this.collectedGold = 0;
        this.collectedWood = 0;
        this.buildPeasant = buildPeasants;
        this.playerNum = playernum;
        this.cost = 0.0;
        this.estTotalCost = this.cost + this.heuristic();
        //this.prevActions = new ArrayList<StripsAction>();
        this.parent = null;
        //Initially the townhall has 3 food, and there is only 1 unit on the map that consumes 1 food,
        //so there is 2 food left at the townhall
        this.foodAmount = 2;
        
        //Get the peasant units of the player
        unitIDs = state.getUnitIds(playernum);
        System.out.println(unitIDs.get(0));
        units = new ArrayList<UnitState>();
        int i = 0;
        //In the units array we only store the peasants, the townhall we will store in a Position object, rather
        //than a UnitState
        for (Integer unitID : unitIDs) {
        	if(state.getUnit(unitID).getTemplateView().getName().equals("Peasant")) {
        		units.add(i++, new UnitState(state.getUnit(unitID).getXPosition(), state.getUnit(unitID).getYPosition(),
            			0, "None"));
        	}
        	else if(state.getUnit(unitID).getTemplateView().getName().toLowerCase().equals("townhall")) {
        		this.townhallID = unitID;
        		this.townhallPos = new Position(state.getUnit(unitID).getXPosition(), 
        				state.getUnit(unitID).getYPosition());
        	}
        	
        	
        }
        //Iterate over all the resources and store their locations, type, and amount into a hashset
        List<Integer> resourceIDs = state.getAllResourceIds();
        for (Integer resourceID : resourceIDs) {
			ResourceNode.ResourceView resource = state
					.getResourceNode(resourceID);
			if (resource.getType() == ResourceNode.Type.GOLD_MINE) {
				goldLocations.add(new Position(resource.getXPosition(),
						resource.getYPosition(), resource.getType(), resource.getID(), resource.getAmountRemaining()));
			}
			else if (resource.getType() == ResourceNode.Type.TREE) {
				woodLocations.add(new Position(resource.getXPosition(),
						resource.getYPosition(), resource.getType(), resource.getID(), resource.getAmountRemaining()));
			}
			
		}
		mapXExtent = state.getXExtent();
		mapYExtent = state.getYExtent();
    }

    /**
     * Unlike in the first A* assignment there are many possible goal states. As long as the wood and gold requirements
     * are met the peasants can be at any location and the capacities of the resource locations can be anything. Use
     * this function to check if the goal conditions are met and return true if they are.
     *
     * @return true if the goal conditions are met in this instance of game state.
     */
    public boolean isGoal() {
        return (this.collectedGold >= this.requiredGold && this.collectedWood >= this.requiredWood);
    }

    /**
     * The branching factor of this search graph are much higher than the planning. Generate all of the possible
     * successor states and their associated actions in this method.
     *
     * @return A list of the possible successor states and their associated actions
     */
    public List<GameState> generateChildren() {
    	List<GameState> childStates = new ArrayList<GameState>();
    	//for (int i = 0; i < units.size(); i++) {
    	int i = 0;

    		//Check if the peasant is not next to a resource and is not carrying anything, 
    		//then add the move to harvest that resource
            for (Position p : goldLocations) {
            	if (!p.isAdjacent(new Position(units.get(i).x, units.get(i).y)) && units.get(i).carriedResAmount == 0 && p.amountLeft > 0) {
            		GameState tempState = new GameState(this);
            		if(!tempState.prevActions.isEmpty()) {
            			tempState.prevActions.clear();
            		}
            		if(this.collectedGold >= 400 && this.foodAmount > 0 && buildPeasant == true) {
                		tempState.collectedGold -= 400;
                		tempState.foodAmount -= 1;
                		tempState.units.add(this.units.size(), new UnitState(this.townhallPos.x, this.townhallPos.y, 0, "None"));
                		tempState.prevActions.add(new BuildPeasant(this.townhallID));
                	}
            		tempState.units.get(i).x = p.x;
            		tempState.units.get(i).y = p.y;
            		//Iterate over the child's goldLocations, find the gold mine we are harvesting from, and decrement the amount
            		//of gold left
            		for(Position tempPos : tempState.goldLocations) {
            			if (tempPos.equals(p)) {
            				tempPos.amountLeft -= 100;
            				//System.out.println("Subtracting 100 from position: " + p.x + " " + p.y);
            			}
            		}
            		tempState.units.get(i).carriedResAmount = 100;
            		tempState.units.get(i).resType = "Gold";
            		//tempState.collectedGold += 100;
            		tempState.prevActions.add(i, new HarvestAction(p, i, mapXExtent, mapYExtent, units.get(i).carriedResAmount));
            		if(i + 1 < units.size()) {
            			childStates = getSubChildren(i + 1, tempState, childStates);
            		}
            		else
            		{
            			tempState.cost = tempState.getCost() + tempState.heuristic();
            			//childStates.add(new GameState(tempState));
            			childStates.add(tempState);
            		}
            	}
            }
            //Same thing for wood
            for (Position p : woodLocations) {
            	if (!p.isAdjacent(new Position(units.get(i).x, units.get(i).y)) && units.get(i).carriedResAmount == 0 && p.amountLeft > 0) {
            		GameState tempState = new GameState(this);
            		if(!tempState.prevActions.isEmpty()) {
            			tempState.prevActions.clear();
            		}
            		if(this.collectedGold >= 400 && this.foodAmount > 0 && buildPeasant == true) {
                		tempState.collectedGold -= 400;
                		tempState.foodAmount -= 1;
                		tempState.units.add(this.units.size(), new UnitState(this.townhallPos.x, this.townhallPos.y, 0, "None"));
                		tempState.prevActions.add(new BuildPeasant(this.townhallID));
                	}
            		tempState.units.get(i).x = p.x;
            		tempState.units.get(i).y = p.y;
            		//Iterate over the child's woodLocations, find the tree we are harvesting from, and decrement the amount
            		//of wood left
            		for(Position tempPos : tempState.woodLocations) {
            			if (tempPos.equals(p)) {
            				tempPos.amountLeft -= 100;
            			}
            		}
            		tempState.units.get(i).carriedResAmount = 100;
            		tempState.units.get(i).resType = "Wood";
            		//tempState.collectedWood += 100;
            		tempState.prevActions.add(i, new HarvestAction(p, i, mapXExtent, mapYExtent, units.get(i).carriedResAmount));
            		if(i + 1 < units.size()) {
            			childStates = getSubChildren(i + 1, tempState, childStates);
            		}
            		else
            		{
            			tempState.cost = tempState.getCost() + tempState.heuristic();
            			//childStates.add(new GameState(tempState));
            			childStates.add(tempState);
            		}
            	}
            }
            //If the unit has 100 resources, then it can deposit
            if (units.get(i).carriedResAmount == 100) {
            	if (units.get(i).resType.equals("Gold")) {
            		GameState tempState = new GameState(this);
            		if(!tempState.prevActions.isEmpty()) {
            			tempState.prevActions.clear();
            		}
            		if(this.collectedGold >= 400 && this.foodAmount > 0 && buildPeasant == true) {
                		tempState.collectedGold -= 400;
                		tempState.foodAmount -= 1;
                		tempState.units.add(this.units.size(), new UnitState(this.townhallPos.x, this.townhallPos.y, 0, "None"));
                		tempState.prevActions.add(new BuildPeasant(this.townhallID));
                	}
            		tempState.units.get(i).x = this.townhallPos.x;
            		tempState.units.get(i).y = this.townhallPos.y;
            		tempState.collectedGold += 100;
            		tempState.prevActions.add(i, new DepositAction(townhallPos, i, "Gold", mapXExtent, mapYExtent, units.get(i).carriedResAmount));
            		tempState.units.get(i).carriedResAmount = 0;
            		if(i + 1 < units.size()) {
            			childStates = getSubChildren(i + 1, tempState, childStates);
            		}
            		else
            		{
            			tempState.cost = tempState.getCost() + tempState.heuristic();
            			//childStates.add(new GameState(tempState));
            			childStates.add(tempState);
            		}
            	}
            	else if(units.get(i).resType.equals("Wood") ) {
            		GameState tempState = new GameState(this);
            		if(!tempState.prevActions.isEmpty()) {
            			tempState.prevActions.clear();
            		}
            		//Check if can make a new peasant
            		if(this.collectedGold >= 400 && this.foodAmount > 0 && buildPeasant == true) {
                		tempState.collectedGold -= 400;
                		tempState.foodAmount -= 1;
                		tempState.units.add(this.units.size(), new UnitState(this.townhallPos.x, this.townhallPos.y, 0, "None"));
                		tempState.prevActions.add(new BuildPeasant(this.townhallID));
                	}
            		tempState.units.get(i).x = this.townhallPos.x;
            		tempState.units.get(i).y = this.townhallPos.y;
            		tempState.collectedWood += 100;
            		tempState.prevActions.add(i, new DepositAction(townhallPos, i, "Wood", mapXExtent, mapYExtent, units.get(i).carriedResAmount));
            		tempState.units.get(i).carriedResAmount = 0;
            		if(i + 1 < units.size()) {
            			childStates = getSubChildren(i + 1, tempState, childStates);
            		}
            		else
            		{
            			tempState.cost = tempState.getCost() + tempState.heuristic();
            			//childStates.add(new GameState(tempState));
            			childStates.add(tempState);
            		}
            	}
            }


        return childStates;
    }

    
    //This is a recursive function that gets called to iterate over multiple peasants
    public List<GameState> getSubChildren(int unitIndex, GameState tempState, List<GameState> childStates) {
    	boolean isBuildingPeasant = false;
		for(StripsAction action : tempState.prevActions) {
			if(action.actionType().equals("BuildPeasant")) {
				isBuildingPeasant = true;
			}
		}


    	/*HashSet<Position> currentGoldLocations = new HashSet<Position>();
    	for(Position tempPos : tempState.goldLocations) {
    		currentGoldLocations.add(new Position(tempPos.x, tempPos.y, tempPos.type, tempPos.resourceID, tempPos.amountLeft));
		}*/
    	for (Position p : goldLocations) {
    		//reset the tempState's goldLocations to their original amounts
        	/*for(Position tempPos : tempState.goldLocations) {
        		for(Position curPos : currentGoldLocations) {
        			if (tempPos.x == curPos.x && tempPos.y == curPos.y) {
        				tempPos.amountLeft = curPos.amountLeft;
        			}
        		}
    		}*/
        	
    		/*boolean isBuildingPeasant = false;
    		for(StripsAction action : tempState.prevActions) {
    			if(action.getUnitIndex() == 0) {
    				isBuildingPeasant = true;
    			}
    		}*/
    		if(tempState.prevActions.size() > unitIndex && !isBuildingPeasant) {
    			tempState.prevActions.remove(unitIndex);
    		}
        	if (!p.isAdjacent(new Position(units.get(unitIndex).x, units.get(unitIndex).y)) && units.get(unitIndex).carriedResAmount == 0 && p.amountLeft > 0) {
        		tempState.units.get(unitIndex).x = p.x;
        		tempState.units.get(unitIndex).y = p.y;
        		//Iterate over the child's goldLocations, find the gold mine we are harvesting from, and decrement the amount
        		//of gold left
        		for(Position tempPos : tempState.goldLocations) {
        			if (tempPos.equals(p)) {
        				tempPos.amountLeft -= 100;
        				//System.out.println("Subtracting 100 from position: " + p.x + " " + p.y);
        			}
        		}
        		tempState.units.get(unitIndex).carriedResAmount = 100;
        		tempState.units.get(unitIndex).resType = "Gold";
        		//tempState.collectedGold += 100;
        		tempState.prevActions.add(unitIndex, new HarvestAction(p, unitIndex, mapXExtent, mapYExtent, units.get(unitIndex).carriedResAmount));
        		//Recursively call getSubChildren on any additional units
        		if(unitIndex + 1 < units.size()) {
        			childStates = getSubChildren(unitIndex + 1, tempState, childStates);
        		}
        		//If there are no more units, add this state to the list
        		else
        		{
        			tempState.cost = tempState.getCost() + tempState.heuristic();
        			childStates.add(new GameState(tempState));
        		}
        	}
        }
    	
    	/*HashSet<Position> currentWoodLocations = new HashSet<Position>();
    	for(Position tempPos : tempState.woodLocations) {
    		currentWoodLocations.add(new Position(tempPos.x, tempPos.y, tempPos.type, tempPos.resourceID, tempPos.amountLeft));
		}*/
    	
        //Same thing for wood
        for (Position p : woodLocations) {
        	//reset the tempState's woodLocations to their original amounts
        	/*for(Position tempPos : tempState.woodLocations) {
        		for(Position curPos : currentWoodLocations) {
        			if (tempPos.x == curPos.x && tempPos.y == curPos.y) {
        				tempPos.amountLeft = curPos.amountLeft;
        			}
        		}
    		}*/
        	/*boolean isBuildingPeasant = false;
    		for(StripsAction action : tempState.prevActions) {
    			if(action.getUnitIndex() == 0) {
    				isBuildingPeasant = true;
    			}
    		}*/
        	if(tempState.prevActions.size() > unitIndex && !isBuildingPeasant) {
    			tempState.prevActions.remove(unitIndex);
    		}
        	if (!p.isAdjacent(new Position(units.get(unitIndex).x, units.get(unitIndex).y)) && units.get(unitIndex).carriedResAmount == 0 && p.amountLeft > 0) {
        		tempState.units.get(unitIndex).x = p.x;
        		tempState.units.get(unitIndex).y = p.y;
        		//Iterate over the child's woodLocations, find the tree we are harvesting from, and decrement the amount
        		//of wood left
        		for(Position tempPos : tempState.woodLocations) {
        			if (tempPos.equals(p)) {
        				tempPos.amountLeft -= 100;
        			}
        		}
        		tempState.units.get(unitIndex).carriedResAmount = 100;
        		tempState.units.get(unitIndex).resType = "Wood";
        		//tempState.collectedWood += 100;
        		tempState.prevActions.add(unitIndex, new HarvestAction(p, unitIndex, mapXExtent, mapYExtent, units.get(unitIndex).carriedResAmount));
        		//Recursively call getSubChildren on any additional units
        		if(unitIndex + 1 < units.size()) {
        			childStates = getSubChildren(unitIndex + 1, tempState, childStates);
        		}
        		//If there are no more units, add this state to the list
        		else
        		{
        			tempState.cost = tempState.getCost() + tempState.heuristic();

        			childStates.add(new GameState(tempState));
        		}
        		
        	}
        }
        /*if(tempState.prevActions.size() > unitIndex) {
			tempState.prevActions.remove(unitIndex);
		}*/
        //If the unit has 100 resources, then it can deposit
        if (this.units.get(unitIndex).carriedResAmount == 100) {
        	if (this.units.get(unitIndex).resType.equals("Gold")) {
        		tempState.units.get(unitIndex).x = this.townhallPos.x;
        		tempState.units.get(unitIndex).y = this.townhallPos.y;
        		tempState.collectedGold += 100;
        		tempState.prevActions.add(unitIndex, new DepositAction(townhallPos, unitIndex, "Gold", mapXExtent, mapYExtent, units.get(unitIndex).carriedResAmount));
        		tempState.units.get(unitIndex).carriedResAmount = 0;
        		//Recursively call getSubChildren on any additional units
        		if(unitIndex + 1 < units.size()) {
        			childStates = getSubChildren(unitIndex + 1, tempState, childStates);
        		}
        		//If there are no more units, add this state to the list
        		else
        		{
        			tempState.cost = tempState.getCost() + tempState.heuristic();
        			childStates.add(new GameState(tempState));
        		}
        	}
        	else if(this.units.get(unitIndex).resType.equals("Wood")) {
        		tempState.units.get(unitIndex).x = this.townhallPos.x;
        		tempState.units.get(unitIndex).y = this.townhallPos.y;
        		tempState.collectedWood += 100;
        		tempState.prevActions.add(unitIndex, new DepositAction(townhallPos, unitIndex, "Wood", mapXExtent, mapYExtent, units.get(unitIndex).carriedResAmount));
        		tempState.units.get(unitIndex).carriedResAmount = 0;
        		//Recursively call getSubChildren on any additional units
        		if(unitIndex + 1 < units.size()) {
        			childStates = getSubChildren(unitIndex + 1, tempState, childStates);
        		}
        		//If there are no more units, add this state to the list
        		else
        		{
        			tempState.cost = tempState.getCost() + tempState.heuristic();
        			childStates.add(new GameState(tempState));
        		}
        	}
        }

        return childStates;
    }
    /**
     * Write your heuristic function here. Remember this must be admissible for the properties of A* to hold. If you
     * can come up with an easy way of computing a consistent heuristic that is even better, but not strictly necessary.
     *
     * Add a description here in your submission explaining your heuristic.
     *
     * @return The value estimated remaining cost to reach a goal state from this state.
     */
    public double heuristic() {
    	double heuristic = 0;
    	if(collectedGold >= requiredGold)
    		heuristic = 9999999;
    	else if (collectedWood >= requiredWood)
    		heuristic = 9999999;
    	else
    		heuristic = requiredGold - collectedGold + requiredWood - collectedWood;
    	
    	//return heuristic;
    	return 100 * this.foodAmount + 0.2 * requiredGold - 0.2 * collectedGold + 0.2 * requiredWood - 0.2 * collectedWood;
    }

    /**
     *
     * Write the function that computes the current cost to get to this node. This is combined with your heuristic to
     * determine which actions/states are better to explore.
     *
     * @return The current cost to reach this goal
     */
    public double getCost() {
    	double totalStepsMoved = 0.0;
    	//Iterate over the peasants and compute how far they moved from their parent state (using euclidean distance)
        for (int j = 0; j < units.size(); j++) {
        	Position unitPos = new Position(units.get(j).x, units.get(j).y);
        	//Note, we add 1 to the cost to account for the harvest/deposit action, which takes 1 step
        	if(this.parent.units.size() > j) {
        		totalStepsMoved += 1 + unitPos.euclideanDistance(new Position(this.parent.units.get(j).x, this.parent.units.get(j).y));
        	}
        }
        return totalStepsMoved;
    }

    /**
     * This is necessary to use your state in the Java priority queue. See the official priority queue and Comparable
     * interface documentation to learn how this function should work.
     *
     * @param o The other game state to compare
     * @return 1 if this state costs more than the other, 0 if equal, -1 otherwise
     */
    @Override
    public int compareTo(GameState o) {
        // TODO: Implement me!
    	if (this.estTotalCost < o.estTotalCost)
			return -1;
		else if (this.estTotalCost == o.estTotalCost)
			return 0;
		else
			return 1;
    }

    @Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof GameState)) {
			return false;
		}
		GameState other = (GameState) obj;
		if (collectedGold != other.collectedGold) {
			return false;
		}
		if (collectedWood != other.collectedWood) {
			return false;
		}
		if (units == null) {
			if (other.units != null) {
				return false;
			}
		} else if (!units.equals(other.units)) {
			return false;
		}
		return true;
	}

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + collectedGold;
		result = prime * result + collectedWood;
		result = prime * result + ((units == null) ? 0 : units.hashCode());
		return result;
	}
}
