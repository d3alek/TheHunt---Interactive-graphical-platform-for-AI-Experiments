package d3kod.thehunt.prey.planner.plans;

import android.util.Log;
import d3kod.d3gles20.D3Maths;
import d3kod.thehunt.events.Event;
import d3kod.thehunt.prey.Action;
import d3kod.thehunt.prey.memory.WorldModel;

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
}
