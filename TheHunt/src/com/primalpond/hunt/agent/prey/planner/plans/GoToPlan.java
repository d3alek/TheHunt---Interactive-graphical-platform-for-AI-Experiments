package com.primalpond.hunt.agent.prey.planner.plans;

import com.primalpond.hunt.agent.prey.memory.WorldModel;
import com.primalpond.hunt.world.events.Event;

import d3kod.graphics.extra.D3Maths;

public class GoToPlan extends MoveTowardsPlan {

	private static final String TAG = "GoToPlan";
	protected boolean arrived;
	private float targetRadius;
	
	public GoToPlan(float hX, float hY, float bX, float bY, Event target) {
		super(hX, hY, bX, bY, target);
		targetRadius = target.getRadius();
		arrived = false;
	}

	@Override
	public void update(WorldModel mWorldModel) {
		if (arrived) return;
		float hX = mWorldModel.getHeadX(), hY = mWorldModel.getHeadY();
		float headFromTarget = D3Maths.distance(hX, hY, getTX(), getTY());
		if (closeEnough(headFromTarget)) {
			arrived = true;
			return;
		}
		super.update(mWorldModel);
	}

	protected boolean closeEnough(float headFromTarget) {
		return headFromTarget <= targetRadius;
	}
	
}
