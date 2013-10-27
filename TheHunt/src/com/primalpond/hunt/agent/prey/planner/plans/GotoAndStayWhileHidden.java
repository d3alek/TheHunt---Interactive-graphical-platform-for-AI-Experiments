package com.primalpond.hunt.agent.prey.planner.plans;

import android.util.Log;

import com.primalpond.hunt.agent.prey.memory.WorldModel;
import com.primalpond.hunt.world.events.Event;

import d3kod.graphics.extra.D3Maths;

public class GotoAndStayWhileHidden extends GoToAndStayPlan {

	private static final String TAG = "GotoAndStayWhileHidden";
	private Event mInitialTarget;

	public GotoAndStayWhileHidden(float hX, float hY, float bX, float bY,
			Event target) {
		super(hX, hY, bX, bY, target);
		mInitialTarget = target;
	}
	
	@Override
	public void update(WorldModel mWorldModel) {
		if (mWorldModel.getNearestAlgae() != null) {
			setTarget(mWorldModel.getNearestAlgaeX(), mWorldModel.getNearestAlgaeY());
		}
		else {
			setTarget(mInitialTarget.getX(), mInitialTarget.getY());
		}
		super.update(mWorldModel);
		float hX = mWorldModel.getHeadX(), hY = mWorldModel.getHeadY();
		float headFromTarget = D3Maths.distance(hX, hY, getTX(), getTY());
		if (super.nearby(headFromTarget)) {
			if (mWorldModel.getLightLevel() != 0) {
				mWorldModel.noAlgaeHere();
				Log.i(TAG, "No Alagae here!!!");
			}
		}
	}
}
