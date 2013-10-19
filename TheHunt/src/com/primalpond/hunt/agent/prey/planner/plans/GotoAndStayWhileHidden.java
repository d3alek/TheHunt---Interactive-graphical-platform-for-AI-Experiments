package com.primalpond.hunt.agent.prey.planner.plans;

import com.primalpond.hunt.agent.prey.memory.WorldModel;
import com.primalpond.hunt.world.events.Event;

import d3kod.graphics.extra.D3Maths;

public class GotoAndStayWhileHidden extends GoToAndStayPlan {

	public GotoAndStayWhileHidden(float hX, float hY, float bX, float bY,
			Event target) {
		super(hX, hY, bX, bY, target);
	}
	
	@Override
	public void update(WorldModel mWorldModel) {
		super.update(mWorldModel);
		float hX = mWorldModel.getHeadX(), hY = mWorldModel.getHeadY();
		float headFromTarget = D3Maths.distance(hX, hY, getTX(), getTY());
		if (super.arrived(headFromTarget)) {
			if (mWorldModel.getLightLevel() != 0) {
				mWorldModel.noAlgaeHere();
			}
		}
	}
}
