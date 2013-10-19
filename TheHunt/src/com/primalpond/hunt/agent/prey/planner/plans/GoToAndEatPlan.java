package com.primalpond.hunt.agent.prey.planner.plans;

import com.primalpond.hunt.agent.Agent;
import com.primalpond.hunt.agent.prey.Action;
import com.primalpond.hunt.agent.prey.memory.MoodLevel;
import com.primalpond.hunt.agent.prey.memory.StressLevel;
import com.primalpond.hunt.agent.prey.memory.WorldModel;
import com.primalpond.hunt.world.events.Event;

import android.util.Log;

public class GoToAndEatPlan extends GoToPlan {

	private static final String TAG = "GoToAndEatPlan";
	private boolean ate;
	
	public GoToAndEatPlan(float hX, float hY, float bX, float bY, Event target) {
		super(hX, hY, bX, bY, target);
		ate = false;
	}

	@Override
	public void update(WorldModel mWorldModel) {
		if (ate) return;
		if (mWorldModel.getStressLevel() == StressLevel.PLOK_CLOSE && mWorldModel.getMoodLevel() != MoodLevel.DESPAIR) {
			Log.v(TAG, "Plok close!");
			finish();
		}
		else super.update(mWorldModel);
		if (arrived) {
			finish();
			addParallelAction(Action.eat);
			ate = true;
			return;
		}
	}
}
