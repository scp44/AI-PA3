package edu.cwru.sepia.agent.planner;




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
	private boolean buildPeasant;
	private int playerNum;
	private List<Integer> unitIDs = new ArrayList<Integer>();
	private HashSet<Position> goldLocations = new HashSet<Position>();
	private HashSet<Position> woodLocations = new HashSet<Position>();
	public UnitState units[];
	private int mapXExtent;
	private int mapYExtent;
	public GameState parent;
	public double cost, estTotalCost;
	public StripsAction prevAction;
	private Position townhallPos;

	
	//An inner class to keep track of the state of the peasant units
	class UnitState {
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + carriedResAmount;
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
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (carriedResAmount != other.carriedResAmount) {
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
		int x, y;
		int carriedResAmount;
		String resType;
		public UnitState(int x, int y, int resAmount, String resType) {
			this.x = x;
			this.y = y;
			this.carriedResAmount = resAmount;
			this.resType = new String(resType);
		}
		//A separate constructor to perform deep copy
		public UnitState(UnitState unitState) {
			this.x = unitState.x;
			this.y = unitState.y;
			this.carriedResAmount = unitState.carriedResAmount;
		}
		private GameState getOuterType() {
			return GameState.this;
		}
	}
	
	//A constructor that performs a deep copy of a GameState
	public GameState(GameState state) {
		this.requiredGold = state.requiredGold;
		this.requiredWood = state.requiredWood;
		this.buildPeasant = state.buildPeasant;
		this.collectedGold = state.collectedGold;
		this.collectedWood = state.collectedWood;
		this.mapXExtent = state.mapXExtent;
		this.mapYExtent = state.mapYExtent;
		this.unitIDs.addAll(0, state.unitIDs);
		this.goldLocations = new HashSet<Position>(state.goldLocations);
		this.woodLocations = new HashSet<Position>(state.woodLocations);
		for (int i = 0; i < state.units.length; i++) {
			this.units[i] = new UnitState(state.units[i]);
		}
		//Are you sure you want to copy the GameState's parent pointer as well???
		this.parent = new GameState(state);
		this.cost = getCost();
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
        this.buildPeasant = buildPeasant;
        this.playerNum = playerNum;
        this.cost = 0.0;
        this.estTotalCost = 0.0;
        this.prevAction = null;
        this.parent = null;
        
        //Get the peasant units of the player
        unitIDs = state.getUnitIds(playernum);
        units = new UnitState[unitIDs.size()];
        int i = 0;
        for (Integer unitID : unitIDs) {
        	if(state.getUnit(unitID).getTemplateView().getName().equals("Peasant")) {
        		units[i++] = new UnitState(state.getUnit(unitID).getXPosition(), state.getUnit(unitID).getYPosition(),
            			0, null);
        	}
        	else if(state.getUnit(unitID).getTemplateView().getName().toLowerCase().equals("townhall")) {
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
						resource.getYPosition(), resource.getType(), resource.getAmountRemaining()));
			}
			else if (resource.getType() == ResourceNode.Type.TREE) {
				woodLocations.add(new Position(resource.getXPosition(),
						resource.getYPosition(), resource.getType(), resource.getAmountRemaining()));
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
    	Position resPosition = null;
    	List<GameState> childStates = new ArrayList<GameState>();
    	for (int i = 0; i < units.length; i++) {
    		//Check if the peasant is not next to a resource and is not carrying anything, 
    		//then add the move to harvest that resource
            for (Position p : goldLocations) {
            	if (!p.isAdjacent(new Position(units[i].x, units[i].y)) && units[i].carriedResAmount == 0) {
            		GameState tempState = new GameState(this);
            		tempState.units[i].x = p.x;
            		tempState.units[i].y = p.y;
            		tempState.collectedGold += 100;
            		tempState.prevAction = new HarvestAction(p, mapXExtent, mapYExtent, units[i].carriedResAmount);
            		childStates.add(tempState);
            	}
            }
            //Same thing for wood
            for (Position p : woodLocations) {
            	if (!p.isAdjacent(new Position(units[i].x, units[i].y)) && units[i].carriedResAmount == 0) {
            		GameState tempState = new GameState(this);
            		tempState.units[i].x = p.x;
            		tempState.units[i].y = p.y;
            		tempState.units[i].carriedResAmount = 100;
            		tempState.units[i].resType = "Wood";
            		tempState.prevAction = new HarvestAction(p, mapXExtent, mapYExtent, units[i].carriedResAmount);
            		childStates.add(tempState);
            	}
            }
            //If the unit have 100 resources, then it can deposit
            if (units[i].carriedResAmount == 100) {
            	if (units[i].resType == "Gold") {
            		GameState tempState = new GameState(this);
            		tempState.units[i].x = this.townhallPos.x;
            		tempState.units[i].y = this.townhallPos.y;
            		tempState.collectedGold += units[i].carriedResAmount;
            		tempState.prevAction = new DepositAction(townhallPos, mapXExtent, mapYExtent, units[i].carriedResAmount);
            		tempState.units[i].carriedResAmount = 0;
            		childStates.add(tempState);
            	}
            	else if(units[i].resType == "Wood") {
            		GameState tempState = new GameState(this);
            		tempState.units[i].x = this.townhallPos.x;
            		tempState.units[i].y = this.townhallPos.y;
            		tempState.collectedWood += units[i].carriedResAmount;
            		tempState.prevAction = new DepositAction(townhallPos, mapXExtent, mapYExtent, units[i].carriedResAmount);
            		tempState.units[i].carriedResAmount = 0;
            		childStates.add(tempState);
            	}
            }
    	}
    	
        
        return null;
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
        // TODO: Implement me!
    	int heuristic = 0;
    	//if(this.prevAction = Move(peasant, loc))
    		//heuristic = Math.max(Math.abs(loc.x-peasant.x), Math.abs(loc.y-peasant.y));
    	//else if(this.prevAction = gather(resource)){
    		//if(resource == gold && collectedGold >= requiredGold)
    			//heuristic = 999999999;
    		//else if(resource == wood && collectedWood >= requiredWood)
    			//heuristic = 999999999;
    		//else
    			//heuristic = 1;	//good thing to do. Yay
    	//}
    	//else //depositing
    		//heuristic = 1;
    	return heuristic;
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
        for (int j = 0; j < units.length; j++) {
        	Position unitPos = new Position(units[j].x, units[j].y);
        	//Note, we add 1 to the cost to account for the harvest/deposit action, which takes 1 step
        	totalStepsMoved += 1 + unitPos.euclideanDistance(new Position(this.parent.units[j].x, this.parent.units[j].y));
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
        return 0;
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
		if (!Arrays.equals(units, other.units)) {
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
		result = prime * result + Arrays.hashCode(units);
		return result;
	}
}
