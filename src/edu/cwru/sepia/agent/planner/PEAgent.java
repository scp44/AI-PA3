package edu.cwru.sepia.agent.planner;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.action.ActionFeedback;
import edu.cwru.sepia.action.ActionResult;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.agent.planner.actions.*;
import edu.cwru.sepia.environment.model.history.History;
import edu.cwru.sepia.environment.model.state.ResourceType;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.environment.model.state.Template;
import edu.cwru.sepia.environment.model.state.Unit;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * This is an outline of the PEAgent. Implement the provided methods. You may add your own methods and members.
 */
public class PEAgent extends Agent {

    // The plan being executed
    private Stack<StripsAction> plan = null;

    
    // maps the real unit Ids to the plan's unit ids
    // when you're planning you won't know the true unit IDs that sepia assigns. So you'll use placeholders (1, 2, 3).
    // this maps those placeholders to the actual unit IDs.
    private Map<Integer, Integer> peasantIdMap;
    private int townhallId;
    private int peasantTemplateId;

    public PEAgent(int playernum, Stack<StripsAction> plan) {
        super(playernum);
        peasantIdMap = new HashMap<Integer, Integer>();
        this.plan = plan;

    }

    @Override
    public Map<Integer, Action> initialStep(State.StateView stateView, History.HistoryView historyView) {
        // gets the townhall ID and the peasant ID
        for(int unitId : stateView.getUnitIds(playernum)) {
            Unit.UnitView unit = stateView.getUnit(unitId);
            String unitType = unit.getTemplateView().getName().toLowerCase();
            if(unitType.equals("townhall")) {
                townhallId = unitId;
            } else if(unitType.equals("peasant")) {
                peasantIdMap.put(unitId, unitId);
            }
        }

        // Gets the peasant template ID. This is used when building a new peasant with the townhall
        for(Template.TemplateView templateView : stateView.getTemplates(playernum)) {
            if(templateView.getName().toLowerCase().equals("peasant")) {
                peasantTemplateId = templateView.getID();
                break;
            }
        }

        return middleStep(stateView, historyView);
    }

    /**
     * This is where you will read the provided plan and execute it. If your plan is correct then when the plan is empty
     * the scenario should end with a victory. If the scenario keeps running after you run out of actions to execute
     * then either your plan is incorrect or your execution of the plan has a bug.
     *
     * You can create a SEPIA deposit action with the following method
     * Action.createPrimitiveDeposit(int peasantId, Direction townhallDirection)
     *
     * You can create a SEPIA harvest action with the following method
     * Action.createPrimitiveGather(int peasantId, Direction resourceDirection)
     *
     * You can create a SEPIA build action with the following method
     * Action.createPrimitiveProduction(int townhallId, int peasantTemplateId)
     *
     * You can create a SEPIA move action with the following method
     * Action.createCompoundMove(int peasantId, int x, int y)
     *
     * these actions are stored in a mapping between the peasant unit ID executing the action and the action you created.
     *
     * For the compound actions you will need to check their progress and wait until they are complete before issuing
     * another action for that unit. If you issue an action before the compound action is complete then the peasant
     * will stop what it was doing and begin executing the new action.
     *
     * To check an action's progress you can call getCurrentDurativeAction on each UnitView. If the Action is null nothing
     * is being executed. If the action is not null then you should also call getCurrentDurativeProgress. If the value is less than
     * 1 then the action is still in progress.
     *
     * Also remember to check your plan's preconditions before executing!
     */
    @Override
    public Map<Integer, Action> middleStep(State.StateView stateView, History.HistoryView historyView) {
        HashMap<Integer, Action> actions = new HashMap<Integer, Action>();
        int temp = 0;
        if(stateView.getUnitIds(playernum).size() > 2) {
        	int j = 0;
        }
        for(int unitId : stateView.getUnitIds(playernum)) {
        	Unit.UnitView unit = stateView.getUnit(unitId);
            String unitType = unit.getTemplateView().getName().toLowerCase();
            if(peasantIdMap.get(unitId) == null){
            	peasantIdMap.put(unitId, unitId);
            }
            //Check if the turn is not the first turn, and the plan is not empty
            if (stateView.getTurnNumber() != 0 && !plan.isEmpty()) {
            	//Store the results of the previous turn's action in a Map
                Map<Integer, ActionResult> actionResults = historyView.getCommandFeedback(playernum, stateView.getTurnNumber()-1);
                boolean isNewUnit = true;
                for (ActionResult result : actionResults.values()) {
                	if(unitType.equals("peasant") && (plan.peek().actionType() == "Deposit" || plan.peek().actionType() == "Harvest") 
                			&& result.getAction().getUnitId() == unitId) {
                		isNewUnit = false;
                	}
                }
                //Iterate over the results
                for (ActionResult result : actionResults.values()) {
                	
                	//If the last action completed successfully, then check what the next action is and pop it off
                	//the stack
                	if(unitType.equals("peasant") && (plan.peek().actionType() == "Deposit" || plan.peek().actionType() == "Harvest")
                			&& (isNewUnit || (result.getAction().getUnitId() == unitId && result.getFeedback().equals(ActionFeedback.COMPLETED)))) {
                    	StripsAction act = plan.pop();
                    	actions.put(peasantIdMap.get(unitId), createSepiaAction(act));
                    	isNewUnit = false;
                    }

                	if (unitType.equals("townhall") && plan.peek().actionType() == "BuildPeasant" 
                			&& result.getFeedback().equals(ActionFeedback.COMPLETED)) {
                    	StripsAction act = plan.pop();
                    	actions.put(townhallId, createSepiaAction(act));
                    }
                }
            }
            //A separate case for the first turn, since there is no previous action, just pop the first move of the
            //plan off the stack
            else if (!plan.isEmpty()) {
            	if(unitType.equals("peasant") && (plan.peek().actionType() == "Deposit" || plan.peek().actionType() == "Harvest")) {
                	StripsAction act = plan.pop();
                	actions.put(peasantIdMap.get(unitId), createSepiaAction(act));
                }
            	else if (unitType.equals("townhall") && plan.peek().actionType() == "BuildPeasant") {
                	StripsAction act = plan.pop();
                	actions.put(townhallId, createSepiaAction(act));
                }
            }
            
        	//For each peasant, create a harvest or deposit action
            /*if(unitType.equals("peasant") && (plan.peek().actionType() == "Deposit" || plan.peek().actionType() == "Harvest")) {
            	//Check if it has completed an action or if is not performing an action
            	if(unit.getCurrentDurativeAction() == null || unit.getCurrentDurativeProgress() >= 1) {
            		StripsAction act = plan.pop();
            		actions.put(peasantIdMap.get(unitId), createSepiaAction(act));
            	}
            }
            //Else, for the townhall, create a buildpeasant action
            else if (unitType.equals("townhall") && plan.peek().actionType() == "BuildPeasant") {
            	StripsAction act = plan.pop();
            	actions.put(townhallId, createSepiaAction(act));
            }*/
        }
        return actions;
    }

    /**
     * Returns a SEPIA version of the specified Strips Action.
     * @param action StripsAction
     * @return SEPIA representation of same action
     */
    private Action createSepiaAction(StripsAction action) {
    	if (action.actionType() == "Deposit") {
    		int uID = 0;
    		if(action.getUnitIndex() == 1) {
    			uID = 10;
    		}
    		else if(action.getUnitIndex() == 0) {
    			uID = 1;
    		}
    		return Action.createCompoundDeposit(peasantIdMap.get(uID), townhallId);
    	}
    	else if (action.actionType() == "Harvest") {
    		int uID = 0;
    		if(action.getUnitIndex() == 1) {
    			uID = 10;
    		}
    		else if(action.getUnitIndex() == 0) {
    			uID = 1;
    		}
    		return Action.createCompoundGather(peasantIdMap.get(uID), action.getID());
    	}
    	else if (action.actionType() == "BuildPeasant") {
    		return Action.createCompoundProduction(townhallId, peasantTemplateId);
    	}
    		return null;
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
}
