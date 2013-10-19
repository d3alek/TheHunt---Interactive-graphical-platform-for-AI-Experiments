package com.primalpond.hunt.agent.prey.planner.plans;

import java.util.Random;

import com.primalpond.hunt.agent.prey.Action;
import com.primalpond.hunt.agent.prey.memory.StressLevel;
import com.primalpond.hunt.agent.prey.memory.WorldModel;
import com.primalpond.hunt.world.environment.Dir;

import android.graphics.PointF;
import d3kod.graphics.extra.D3Maths;

public class ExplorePlan extends Plan {
	// Doc: isFinished() always returns true
	private static final String TAG = "Explore plan";
	private Random mRandom;

	Action[] wanderActions = {
			Action.TURN_LEFT_SMALL, Action.TURN_RIGHT_SMALL
	};
	private WorldModel mWorldModel;

	public ExplorePlan(WorldModel worldModel) {
		super(0, 0, 0, null);
		mWorldModel = worldModel;
		mRandom = new Random();
	}

	@Override
	public void update(WorldModel worldModel) {
		Dir curDir = worldModel.getCurrentDir();
		if (curDir == Dir.UNDEFINED) {
			super.addNextAction(chooseRandomWanderAction());
		}
		else {
			super.addNextAction(
					getFollowDirAction(
							curDir, worldModel.getBodyX(), worldModel.getBodyY(), 
							worldModel.getHeadX(), worldModel.getHeadY()));
		}
	}

	private Action getFollowDirAction(Dir curDir, float bodyX, float bodyY, float headX, float headY) {
		PointF currendDirDelta = curDir.getDelta();
		float det = D3Maths.det(bodyX, bodyY, 
				headX, headY, 
				headX + currendDirDelta.x, headY + currendDirDelta.y);
		if (mWorldModel.getStressLevel() == StressLevel.CALM) {
			if (det < 0) {
				return Action.TURN_RIGHT_SMALL;
			}
			else return Action.TURN_LEFT_SMALL;
		}
		else {
			if (det < 0) {
				return Action.TURN_RIGHT_LARGE;
			}
			else return Action.TURN_LEFT_LARGE;
		}
	}

	protected Action chooseRandomWanderAction() {
		int actionInd = Math.round((wanderActions.length-1) * mRandom.nextFloat());
		return wanderActions[actionInd];
	}

	@Override
	public boolean isFinished() {
		return true;
	}

	public Random getRandom() {
		return mRandom;
	}

	public boolean isRapid() {
		return false;
	}

}
