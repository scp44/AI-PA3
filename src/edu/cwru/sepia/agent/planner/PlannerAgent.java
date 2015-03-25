package edu.cwru.sepia.agent.planner;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.agent.planner.actions.StripsAction;
import edu.cwru.sepia.environment.model.history.History;
import edu.cwru.sepia.environment.model.state.State;

import java.io.*;
import java.util.*;

/**
 * Created by Devin on 3/15/15.
 */
public class PlannerAgent extends Agent {

    final int requiredWood;
    final int requiredGold;
    final boolean buildPeasants;

    // Your PEAgent implementation. This prevents you from having to parse the text file representation of your plan.
    PEAgent peAgent;

    public PlannerAgent(int playernum, String[] params) {
        super(playernum);

        if(params.length < 3) {
            System.err.println("You must specify the required wood and gold amounts and whether peasants should be built");
        }

        requiredWood = Integer.parseInt(params[0]);
        requiredGold = Integer.parseInt(params[1]);
        buildPeasants = Boolean.parseBoolean(params[2]);


        System.out.println("required wood: " + requiredWood + " required gold: " + requiredGold + " build Peasants: " + buildPeasants);
    }

    @Override
    public Map<Integer, Action> initialStep(State.StateView stateView, History.HistoryView historyView) {

        Stack<StripsAction> plan = Astar(new GameState(stateView, playernum, requiredGold, requiredWood, buildPeasants));

        if(plan == null) {
            System.err.println("No plan was found");
            System.exit(1);
            return null;
        }

        // write the plan to a text file
        savePlan(plan);


        // Instantiates the PEAgent with the specified plan.
        peAgent = new PEAgent(playernum, plan);

        return peAgent.initialStep(stateView, historyView);
    }

    @Override
    public Map<Integer, Action> middleStep(State.StateView stateView, History.HistoryView historyView) {
        if(peAgent == null) {
            System.err.println("Planning failed. No PEAgent initialized.");
            return null;
        }

        return peAgent.middleStep(stateView, historyView);
    }

    @Override
    public void terminalStep(State.StateView stateView, History.HistoryView historyView) {

    }

    @Override
    public void savePlayerData(OutputStream outputStream) {

    }

    @Override
    public void loadPlayerData(InputStream inputStream) {

    }
    
    //stuff for the AStar 
    class OpenListCompare implements Comparator<GameState>
    {
    	public int compare(GameState loc1, GameState loc2) {
    		if (loc1.estTotalCost < loc2.estTotalCost)
    			return -1;
    		else if (loc1.estTotalCost == loc2.estTotalCost)
    			return 0;
    		else
    			return 1;
    	}
    }
    
    class PriorityQueueList extends PriorityQueue<GameState>
    {
    	public PriorityQueueList(int initLength)
    	{
    		super(initLength, new OpenListCompare());
    	}
    	
    	public float nodeCost(GameState state) {
    		/*if (this.isEmpty()) 
        		return 0;
        	Iterator<GameState> it = this.iterator();
        	while (it.hasNext()) {
        		GameState location = it.next();
    			if(location.x == loc.x && location.y == loc.y) {
    				return location.cost;
    			}
    	    }*/
        	return 0;
    	}
    	
    	public boolean contains(GameState state) {
        	
        	/*if (this.isEmpty()) 
        		return false;
        	Iterator<GameState> it = this.iterator();
        	while (it.hasNext()) {
        		GameState location = it.next();
    			if(location.x == loc.x && location.y == loc.y) {
    				return true;
    			}
    	    }*/
        	return false;
        }
    	
    	/**
    	 * Removes the previous entry for the specified location and replaces
    	 * it with the new one to the queue.
    	 * 
    	 * @param loc
    	 */
    	public void update(GameState loc)
    	{
    		/*if (this.isEmpty()) 
        		return;
        	Iterator<GameState> it = this.iterator();
        	while (it.hasNext()) {
        		GameState location = it.next();
    			if(location.x == loc.x && location.y == loc.y) {
    				this.remove(location);
    				this.add(loc);
    				return;
    			}
    	    }*/
        	return;
    	}
    }

    /**
     * Perform an A*  of the game graph. This should return your plan as a stack of actions. This is essentially
     * the same as your first assignment. The implementations should be very similar. The difference being that your
     * nodes are now GameState objects not MapLocation objects.
     *
     * @param startState The state which is being planned from
     * @return The plan or null if no plan is found.
     */
    private Stack<StripsAction> Astar(GameState startState) {
        // TODO: Implement me!
    	PriorityQueueList openList = new PriorityQueueList(1);
        Set<GameState> closedList = new HashSet<GameState>();
        	
        startState.cost = 0;
        startState.estTotalCost = startState.cost + startState.heuristic();
        openList.add(startState);
        	
        GameState current = null;
        GameState nextState = null;
        double temp_g = 0;
        
        while(!openList.isEmpty())
        {
        	//remove invalid nodes from the list until first valid one is found
        	//may not need this function if all goes according to plan...
        	while(openList.peek().estTotalCost == -1)
        	{
        		openList.remove();
        	}
        		
        	current = openList.poll();
	       	closedList.add(current);
	        	
        	if (current.isGoal())
        	{
        		//reconstruct path & return it
        		Stack<StripsAction> foundPath = reconstructPath(current, startState);

               	return foundPath;
       		}
        		
        	List<GameState> nextStateList;	
       		nextStateList = current.generateChildren();

       		for(int x = 0; x < nextStateList.size() && nextStateList.get(x) != null; x++)
       		{
       			nextState = nextStateList.get(x);
        			
       			if (closedList.contains(nextState))
       			{
       				continue;
       			}
        			
       			temp_g = current.cost + actionCost(nextState);
        			
       			if(!(openList.contains(nextState)) || temp_g < openList.nodeCost(nextState))
       			{
       				nextState.parent = current;
       				nextState.cost = temp_g;
       				nextState.estTotalCost = nextState.cost + nextState.heuristic();
        				
       				if(!(openList.contains(nextState)))
       				{
       					openList.add(nextState);
       				}
       				else
       				{
       					openList.update(nextState);
       				}
       			}
       		}
       	}    	
    	//no path. Return empty path
    	System.out.println("No path found using Astar!");
        return null;
    }
    
    private Stack<StripsAction> reconstructPath(GameState current, GameState start)
    {
    	Stack<StripsAction> path = new Stack<StripsAction>();

    	if (current.parent == null) {
    		return path;
    	}

    	while(!(current.parent.equals(start)))
    	{
    		current = current.parent;
    		path.add(current.prevAction);
    	}
    	
    	return path;
    }
    
    //gets cost to perform a certain action from the resulting state
    private double actionCost(GameState startState) {
    	 
    	return 0;
    }



    /**
     * This has been provided for you. Each strips action is converted to a string with the toString method. This means
     * each class implementing the StripsAction interface should override toString. Your strips actions should have a
     * form matching your included Strips definition writeup. That is <action name>(<param1>, ...). So for instance the
     * move action might have the form of Move(peasantID, X, Y) and when grounded and written to the file
     * Move(1, 10, 15).
     *
     * @param plan Stack of Strips Actions that are written to the text file.
     */
    private void savePlan(Stack<StripsAction> plan) {
        if (plan == null) {
            System.err.println("Cannot save null plan");
            return;
        }

        File outputDir = new File("saves");
        outputDir.mkdirs();

        File outputFile = new File(outputDir, "plan.txt");

        PrintWriter outputWriter = null;
        try {
            outputFile.createNewFile();

            outputWriter = new PrintWriter(outputFile.getAbsolutePath());

            Stack<StripsAction> tempPlan = (Stack<StripsAction>) plan.clone();
            while(!tempPlan.isEmpty()) {
                outputWriter.println(tempPlan.pop().toString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputWriter != null)
                outputWriter.close();
        }
    }
}
