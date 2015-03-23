package edu.cwru.sepia.agent.planner;



import edu.cwru.sepia.environment.model.state.ResourceNode;
import edu.cwru.sepia.environment.model.state.State;

import java.util.ArrayList;
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
	private int collectedGold;
	private int collectedWood;
	private boolean buildPeasant;
	private int playerNum;
	private List<Integer> unitIDs = new ArrayList<Integer>();
	private HashSet<Position> resourceLocations = new HashSet<Position>();
	private UnitState units[];
	private int mapXExtent;
	private int mapYExtent;
	public GameState parent;
	public float cost, estTotalCost;
	
	//An inner class to keep track of the states of the resources
	/*class ResourcePile {
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
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
			if (!(obj instanceof ResourcePile)) {
				return false;
			}
			ResourcePile other = (ResourcePile) obj;
			if (!getOuterType().equals(other.getOuterType())) {
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
		int x, y;
		ResourceNode.Type resourceType;
		int amountLeft;
		public ResourcePile(int x, int y, ResourceNode.Type type, int ammount) {
			this.x = x;
			this.y = y;
			this.resourceType = type;
			amountLeft = ammount;
		}
		private GameState getOuterType() {
			return GameState.this;
		}
	}*/
	
	//An inner class to keep track of the state of the peasant units
	class UnitState {
		int x, y;
		int carriedResAmount;
		public UnitState(int x, int y, int resAmount) {
			this.x = x;
			this.y = y;
			this.carriedResAmount = resAmount;
		}
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
        //Get the peasant units of the player
        unitIDs = state.getUnitIds(playernum);
        units = new UnitState[unitIDs.size()];
        int i = 0;
        for (Integer unitID : unitIDs) {
        	units[i++] = new UnitState(state.getUnit(unitID).getXPosition(), state.getUnit(unitID).getXPosition(),
        			state.getUnit(unitID).getCargoAmount());
        }
        //Iterate over all the resources and store their locations, type, and amount into a hashset
        List<Integer> resourceIDs = state.getAllResourceIds();
        for (Integer resourceID : resourceIDs) {
			ResourceNode.ResourceView resource = state
					.getResourceNode(resourceID);
			resourceLocations.add(new Position(resource.getXPosition(),
					resource.getYPosition(), resource.getType(), resource.getAmountRemaining()));
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
        // TODO: Implement me!
        return false;
    }

    /**
     * The branching factor of this search graph are much higher than the planning. Generate all of the possible
     * successor states and their associated actions in this method.
     *
     * @return A list of the possible successor states and their associated actions
     */
    public List<GameState> generateChildren() {
        // TODO: Implement me!
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
        return 0.0;
    }

    /**
     *
     * Write the function that computes the current cost to get to this node. This is combined with your heuristic to
     * determine which actions/states are better to explore.
     *
     * @return The current cost to reach this goal
     */
    public double getCost() {
        // TODO: Implement me!
        return 0.0;
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

    /**
     * This will be necessary to use the GameState as a key in a Set or Map.
     *
     * @param o The game state to compare
     * @return True if this state equals the other state, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        // TODO: Implement me!
        return false;
    }

    /**
     * This is necessary to use the GameState as a key in a HashSet or HashMap. Remember that if two objects are
     * equal they should hash to the same value.
     *
     * @return An integer hashcode that is equal for equal states.
     */
    @Override
    public int hashCode() {
        // TODO: Implement me!
        return 0;
    }
}
