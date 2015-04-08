Jiawei He, jxh602
Selena Pigoni, scp44

Our .zip contains the following files:
GameState.java
PEAgent.java
PlannerAgent.java
Position.java
BuildPeasant.java
DepositAction.java
HarvestAction.java
This README

GameState.java Notes:
* UnitState class to keep track of the different peasants and their states
* generateChildren() returns a list of all possible states resulting from actions possible from the current states
	* calls getSubChildren() if multiple peasants
* getSubChildren() returns possible states that include multiple peasants.
	* Basically same as generateChildren(), but accounts for multiple peasants
* heuristic() returns function of how close we are to the goal.
	* If the action gives more gold/wood than the goal, not a good move, so high cost estimated
	* Otherwise, function of gold/wood and required gold/wood
* getCost() gets cost of moving from one place to another +1 for the actual action

PEAgent.java Notes:
* middleStep() executes the actions
	
PlannerAgent.java Notes:
* AStar() runs an A* search through the possible actions
	* returns a stack of actions leading from start to goal
* reconstructPath() reconstructs the path found by A* in order from start to goal

Position.java Notes:
* added resourceID, amountLeft, and type to help keep track of resources

BuildPeasant.java Notes:
* extends StripsAction.java
* handles BuildPeasant action

DepositAction.java Notes:
* extends StripsAction.java
* handles DepositAction action

HarvestAction.java Notes:
* extends StripsAction.java
* handles HarvestAction action