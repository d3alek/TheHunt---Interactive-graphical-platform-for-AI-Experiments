package com.primalpond.hunt.agent.prey.planner.plans;

import com.primalpond.hunt.agent.prey.Action;
import com.primalpond.hunt.agent.prey.memory.WorldModel;
import com.primalpond.hunt.world.events.Event;

import android.util.Log;
import d3kod.graphics.extra.D3Maths;

public class GoToAndStayPlan extends MoveTowardsPlan {
	private static final float DISTANCE_ENOUGH = 0.1f;
	private static final float DISTANCE_SMALL = 0.3f; // same as algae radius
	private static final String TAG = "GoToAndStayPlan";
	
	public GoToAndStayPlan(float hX, float hY, float bX, float bY, Event target) {
		super(hX, hY, bX, bY, target);
	}
	
	@Override
	public void update(WorldModel mWorldModel) {
		
		float hX = mWorldModel.getHeadX(), hY = mWorldModel.getHeadY();
		float headFromTarget = D3Maths.distance(hX, hY, getTX(), getTY());
		
		if (arrived(headFromTarget)) {
			if (super.isEmpty()) {
				Log.v(TAG, "Close enough to target but plan empty " + getTX() + " " + getTY());
			}
			else {
				finish();
				return;
			}
		}
		
		super.update(mWorldModel);
	
		if (getNextAction() == Action.FORWARD_LARGE) {
			if (headFromTarget < DISTANCE_SMALL) {
				changeNextAction(Action.FORWARD_SMALL);
			}
		}
	}

	protected boolean arrived(float headFromTarget) {
		return headFromTarget <= DISTANCE_ENOUGH;
	}

	public boolean nearby(float headFromTarget) {
		return headFromTarget <= DISTANCE_SMALL;
	}
}
