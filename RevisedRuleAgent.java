package com.kmichaelfox.agents.rule;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;

public class RevisedRuleAgent extends BasicMarioAIAgent implements Agent {
	private int trueJumpCounter = 0;
	private int trueBacktrackCounter = 0;
	// private int trueSpeedCounter = 0;
	private enum Direction {
		FORWARD, REVERSE
	}
	private enum Action {
		NONE, SPRINT, FIREBALL, JUMP, BACKTRACK
	}
	private Direction currentDirection;
	private Action currentAction;
	private Action nextAction;
	private boolean delay;

	public RevisedRuleAgent() {
	    super("RevisedRuleAgent");
	    reset();
	}

	public void reset() {
	    action = new boolean[Environment.numberOfKeys];
	    action[Mario.KEY_RIGHT] = true;
	    // action[Mario.KEY_SPEED] = true;
	    trueJumpCounter = 0;
	    delay = false;
	    
	    currentDirection = Direction.FORWARD;
	    currentAction = Action.NONE;
	    nextAction = Action.NONE;
	}
	
	private void updateCurrentAction() {
		switch(currentAction) {
		case SPRINT:
			break;
			
		case FIREBALL:
		{
			currentAction = nextAction;
			action[Mario.KEY_SPEED] = false;
			delay = true;
			break;
		}
			
		case JUMP: 
		{
			if (isMarioOnGround ||  trueJumpCounter > 16) {
				currentAction = nextAction;
				action[Mario.KEY_JUMP] = false;
				trueJumpCounter = 0;
				delay = true;
			} else {
				trueJumpCounter++;
			}
			break;
		}
			
		case BACKTRACK:
		{
			if (!isObstacleAbove() || trueBacktrackCounter > 25) {
				currentDirection = Direction.FORWARD;
				currentAction = Action.JUMP;
				nextAction = Action.NONE;
				
				changeDirection(Direction.FORWARD);
				action[Mario.KEY_JUMP] = true;
				trueBacktrackCounter = 0;
				delay = true;
			} else {
				trueBacktrackCounter++;
			}
			break;
		}
			
		case NONE:
			break;
		}
	}
	
	private void respondToLevel() {
		switch(currentAction) {
		case NONE: {
			 if (isObstacleAhead()) {
				// path is blocked, go back until jump
				if (isObstacleAbove()) {
					//System.out.println("two obstacles!");
					//System.out.println(getReceptiveFieldCellValue(marioEgoRow, marioEgoCol + 1));
					currentAction = Action.BACKTRACK;
					nextAction = Action.NONE;
					
					changeDirection(Direction.REVERSE);
					break;
				}
				
				// path is not blocked so just jump
				currentAction = Action.JUMP;
				nextAction = Action.NONE;
				action[Mario.KEY_JUMP] = true;
				break;
			} else if (isGapAhead()) {
				//System.out.println("jumping over gap");
				currentAction = Action.JUMP;
				nextAction = Action.NONE;
				
				action[Mario.KEY_JUMP] = true;
				break;
			} else if (isEnemyAhead(1) || isEnemyAhead(2) ) { // || (marioMode !=2 && isEnemyAhead(3))) {
				if (isObstacleAbove()) {
					currentAction = Action.BACKTRACK;
					nextAction = Action.NONE;
					
					changeDirection(Direction.REVERSE);
					break;
				}
				
				if (marioMode == 2) { // if mario has flower power
					currentAction = Action.FIREBALL;
					nextAction = Action.NONE;
					
					action[Mario.KEY_SPEED] = true;
					break;
				} else {
					currentAction = Action.JUMP;
					nextAction = Action.NONE;
					
					action[Mario.KEY_JUMP] = true;
					break;
				}
			}
		}
		
		case BACKTRACK: {
			if ((isEnemyAhead(-2) || isEnemyAhead(-1)) && (isEnemyAhead(1) || isEnemyAhead(2))) {
				nextAction = Action.BACKTRACK;
				currentAction = Action.JUMP;
				
				action[Mario.KEY_JUMP] = true;
				break;
			}
		}
		
		case JUMP:
		case FIREBALL:
		case SPRINT:
			break;
		}
	}
	
	private boolean isEnemyAhead(int stepsAhead) {
		//if (getEnemiesCellValue(marioEgoRow, marioEgoCol + stepsAhead) != 0) {
			//System.out.println(getEnemiesCellValue(marioEgoRow, marioEgoCol + 1));
		//}
		return getEnemiesCellValue(marioEgoRow, marioEgoCol + stepsAhead) != 0 ||
				getEnemiesCellValue(marioEgoRow - 1, marioEgoCol + stepsAhead) != 0;
//		if (getEnemiesCellValue(marioEgoRow, marioEgoCol + 1) != 2) {
//			System.out.println(getEnemiesCellValue(marioEgoRow, marioEgoCol + 1));
//		}
//		return (getEnemiesCellValue(marioEgoRow, marioEgoCol + stepsAhead) != 0 &&
//				getEnemiesCellValue(marioEgoRow, marioEgoCol + stepsAhead) != 20) ||
//				(getEnemiesCellValue(marioEgoRow - 1, marioEgoCol + stepsAhead) != 0 &&
//				getEnemiesCellValue(marioEgoRow - 1, marioEgoCol + stepsAhead) != 20) ||
//				(getEnemiesCellValue(marioEgoRow + 1, marioEgoCol + stepsAhead) != 0 &&
//				getEnemiesCellValue(marioEgoRow + 1, marioEgoCol + stepsAhead) != 20);
	}
	
	private boolean isObstacleAbove() {
		return getReceptiveFieldCellValue(marioEgoRow - 2, marioEgoCol) != 0 &&
				getReceptiveFieldCellValue(marioEgoRow - 2, marioEgoCol) != -24 ||
				getReceptiveFieldCellValue(marioEgoRow - 1, marioEgoCol) != 0 &&
				getReceptiveFieldCellValue(marioEgoRow - 1, marioEgoCol) != -24;
	}
	
	private boolean isObstacleAhead() {
//		return ((getReceptiveFieldCellValue(marioEgoRow - 2, marioEgoCol + 1) == 0 &&
//	            getReceptiveFieldCellValue(marioEgoRow - 1, marioEgoCol + 1) == 0) ||
//	            (getReceptiveFieldCellValue(marioEgoRow, marioEgoCol + 2) != 0) &&
//	            getReceptiveFieldCellValue(marioEgoRow, marioEgoCol + 2) != -24 &&
//	            getReceptiveFieldCellValue(marioEgoRow, marioEgoCol + 1) != 0 &&
//	            getReceptiveFieldCellValue(marioEgoRow, marioEgoCol + 1) != -24);
//		return getReceptiveFieldCellValue(marioEgoRow, marioEgoCol + 1) != 0;
		return (getReceptiveFieldCellValue(marioEgoRow, marioEgoCol + 1) != 0 ||
				getReceptiveFieldCellValue(marioEgoRow - 1, marioEgoCol + 1) != 0);// &&
				//getReceptiveFieldCellValue(marioEgoRow - 1, marioEgoCol + 1) != -24) &&
				//(getReceptiveFieldCellValue(marioEgoRow + 1, marioEgoCol + 1) != 0 &&
				//getReceptiveFieldCellValue(marioEgoRow + 1, marioEgoCol + 1) != -24);
	}
	
	private boolean isGapAhead() {
		return getReceptiveFieldCellValue(marioEgoRow + 1, marioEgoCol + 1) == 0;
	}
	
	private void changeDirection(Direction dir) {
		action[Mario.KEY_RIGHT] = false;
		action[Mario.KEY_LEFT] = false;
		
		if (dir == Direction.FORWARD) {
			action[Mario.KEY_RIGHT] = true;
		} else {
			action[Mario.KEY_LEFT] = true;
		}
	}

	public boolean[] getAction() {
		// updateCurrentAction();
		
		// respondToLevel();
		
		
//	    if (currentDirection == Direction.FORWARD) {
//	    	action[Mario.KEY_RIGHT] = true;
//	    }
		
		updateCurrentAction();
	    
	    if (!delay) {
	    	respondToLevel();
	    } else {
	    	delay = false;
	    }
	    
	    return action;
	}
}