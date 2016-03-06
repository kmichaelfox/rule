package com.kmichaelfox.agents.rule;

//public class RuleAgent {
//
//}

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;

/**
 * Created by Eclipse
 * User: Kelly Michael Fox
 * Date: Feb 5, 2016
 * Package: ch.idsia.agents.controllers;
 */

public class RuleAgent extends BasicMarioAIAgent implements Agent {
	private int trueJumpCounter = 0;
	private int trueRetreatCounter = 0;
	// private int trueSpeedCounter = 0;
	private enum Action {
		FORWARD, FIREBALL, JUMP, RETREAT
	}
	private Action currentAction;
	private boolean delay;

	public RuleAgent() {
	    super("RuleAgent");
	    reset();
	}

	public void reset() {
	    action = new boolean[Environment.numberOfKeys];
	    action[Mario.KEY_RIGHT] = true;
	    // action[Mario.KEY_SPEED] = true;
	    trueJumpCounter = 0;
	    delay = false;
	    
	    currentAction = Action.FORWARD;
	}
	
	private void updateAction() {
		switch(currentAction) {
		case JUMP: 
		{
			trueJumpCounter++;
			if (isMarioOnGround || trueJumpCounter > 16) {
				currentAction = Action.FORWARD;
				action[Mario.KEY_JUMP] = false;
				trueJumpCounter = 0;
				delay = true;
			}
			break;
		}
		
		case RETREAT: 
		{
			trueRetreatCounter++;
			if (!isObstacleAbove() || trueRetreatCounter > 10) {
				trueRetreatCounter = 0;
				currentAction = Action.FORWARD;
				action[Mario.KEY_LEFT] = false;
				action[Mario.KEY_RIGHT] = true;
				delay = true;
				System.out.println("Cancelling retreat");
			}
			break;
		}
		
		case FIREBALL:
		{
			currentAction = Action.FORWARD;
			action[Mario.KEY_SPEED] = false;
			delay = true;
			break;
		}
		
		default: 
			break;
		}
	}

	private boolean DangerOfAny() {

//	        if ((getReceptiveFieldCellValue(marioEgoRow + 2, marioEgoCol + 1) == 0 &&
//	            getReceptiveFieldCellValue(marioEgoRow + 1, marioEgoCol + 1) == 0) ||
//	            getReceptiveFieldCellValue(marioEgoRow, marioEgoCol + 1) != 0 ||
//	            getReceptiveFieldCellValue(marioEgoRow, marioEgoCol + 2) != 0 ||
//	            getEnemiesCellValue(marioEgoRow, marioEgoCol + 1) != 0 ||
//	            getEnemiesCellValue(marioEgoRow, marioEgoCol + 2) != 0)
//	            return true;
//	        else
//	            return false;
		
		if ((isEnemyAhead(1) || isEnemyAhead(2)) && isObstacleAbove()) {
			return true;
		}
		
		return false;
	}
	
	private boolean isEnemyAhead(int stepsAhead) {
		return getEnemiesCellValue(marioEgoRow, marioEgoCol + stepsAhead) != 0 ||
				getEnemiesCellValue(marioEgoRow - 1, marioEgoCol + stepsAhead) != 0 ||
				getEnemiesCellValue(marioEgoRow + 1, marioEgoCol + stepsAhead) != 0;
	}
	
	private boolean isObstacleAbove() {
		return getReceptiveFieldCellValue(marioEgoRow - 2, marioEgoCol) != 0 ||
				getReceptiveFieldCellValue(marioEgoRow - 1, marioEgoCol) != 0;
	}
	
	private boolean isObstacleAhead() {
		return ((getReceptiveFieldCellValue(marioEgoRow + 2, marioEgoCol + 1) == 0 &&
	            getReceptiveFieldCellValue(marioEgoRow + 1, marioEgoCol + 1) == 0) ||
	            getReceptiveFieldCellValue(marioEgoRow, marioEgoCol + 1) != 0 ||
	            getReceptiveFieldCellValue(marioEgoRow, marioEgoCol + 2) != 0) && 
	            getReceptiveFieldCellValue(marioEgoRow, marioEgoCol + 1) != 0;
//		return getReceptiveFieldCellValue(marioEgoRow, marioEgoCol + 1) != 0;
	}

	public boolean[] getAction() {
	    // this Agent requires observation integrated in advance.

//	    if (DangerOfAny() && getReceptiveFieldCellValue(marioEgoRow, marioEgoCol + 1) != 1)  // a coin
//	    {
//	        if (isMarioAbleToJump || (!isMarioOnGround && action[Mario.KEY_JUMP]))
//	        {
//	            action[Mario.KEY_JUMP] = true;
//	        }
//	        ++trueJumpCounter;
//	    }
//	    else
//	    {
//	        action[Mario.KEY_JUMP] = false;
//	        trueJumpCounter = 0;
//	    }
//
//	    if (trueJumpCounter > 16)
//	    {
//	        trueJumpCounter = 0;
//	        action[Mario.KEY_JUMP] = false;
//	    }
		
		updateAction();
		
		if (!delay) {
			if ((isEnemyAhead(-2) || isEnemyAhead(-1)) && (isEnemyAhead(1) || isEnemyAhead(2))) {
				if (currentAction == Action.RETREAT) {
					action[Mario.KEY_RIGHT] = true;
					action[Mario.KEY_LEFT] = false;
				}
					
				currentAction = Action.JUMP;
				action[Mario.KEY_JUMP] = true;
			} else if ((isEnemyAhead(1) || isEnemyAhead(2))) {
				if (isObstacleAbove()) {
					currentAction = Action.RETREAT;
					action[Mario.KEY_RIGHT] = false;
					action[Mario.KEY_LEFT] = true;
					System.out.println("Retreating");
				} else {
					//System.out.println(marioMode);
					switch(marioMode) {
					case 2:
						currentAction = Action.FIREBALL;
						action[Mario.KEY_SPEED] = true;
						System.out.println("Shooting fireball");
						break;
						
					case 1:
					case 0:
						currentAction = Action.JUMP;
						action[Mario.KEY_JUMP] = true;
						System.out.println("Jumping over enemy");
						break;
					
					default:
						break;
					}
				}
				
			} else if (isObstacleAhead()) {
				currentAction = Action.JUMP;
				action[Mario.KEY_JUMP] = true;
				System.out.println("Jumping over obstacle");
			}
		} else {
			delay = false;
		}
	    
	    return action;
	}
	
//	private int jumpCounter = 0;
//	private boolean alert = false;
//	
//public RuleAgent()
//{
//    super("RuleAgent");
//    reset();
//}
//
//private boolean dangerOfAny() {
//	if (getEnemiesCellValue(marioEgoRow, marioEgoCol + 1) != 0) {
//		return true;
//	}
//	return false;
//}
//
//private boolean alerted() {
//	if ((getReceptiveFieldCellValue(marioEgoRow + 1, marioEgoCol) != 0 ||
//			getReceptiveFieldCellValue(marioEgoRow + 2, marioEgoCol) != 0) &&
//			(getEnemiesCellValue(marioEgoRow, marioEgoCol + 1) != 0 ||
//			getEnemiesCellValue(marioEgoRow, marioEgoCol + 2) != 0)) {
//		if ()
//	}
//}
//
//public boolean[] getAction()
//{
//    //action[Mario.KEY_SPEED] = action[Mario.KEY_JUMP] = isMarioAbleToJump || !isMarioOnGround;
//	if (!alert) {
//		if (dangerOfAny() && isMarioOnGround) {
//			action[Mario.KEY_JUMP] = true;
//		} else {
//			action[Mario.KEY_JUMP] = false;
//		}
//	} else {
//		
//	}
//	
//	//action[Mario.KEY_RIGHT] = (counter < 5) ? true : false;
//	//counter = (counter + 1) % 10;
//	//action[Mario.KEY_RIGHT] = true;
//    return action;
//}
//
//public void reset()
//{
//    action = new boolean[Environment.numberOfKeys];
//    action[Mario.KEY_RIGHT] = true;
//    //action[Mario.KEY_SPEED] = true;
//}
}