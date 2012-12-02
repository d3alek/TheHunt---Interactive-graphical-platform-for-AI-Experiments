package d3kod.thehunt.prey.planner.plans;

import android.util.Log;
import d3kod.d3gles20.D3Maths;
import d3kod.thehunt.events.Event;
import d3kod.thehunt.prey.memory.WorldModel;

public class GoToPlan extends MoveTowardsPlan {

	private static final String TAG = "GoToPlan";
	protected boolean arrived;
//	private static final float DISTANCE_ENOUGH = 0.1f;
	private float targetRadius;
	
	public GoToPlan(float hX, float hY, float bX, float bY, Event target) {
		super(hX, hY, bX, bY, target);
		targetRadius = target.getRadius();
		//Log.v(TAG, "New GoToPlan to radius " + targetRadius);
		arrived = false;
	}

	@Override
	public void update(WorldModel mWorldModel) {
		if (arrived) return;
		float hX = mWorldModel.getHeadX(), hY = mWorldModel.getHeadY();
		float headFromTarget = D3Maths.distance(hX, hY, getTX(), getTY());
		//Log.v(TAG, "Updating, headFromTarget is now " + headFromTarget); 
		if (closeEnough(headFromTarget)) {
			arrived = true;
//			if (isEmpty()) addNextAction(Action.none);
			return;
		}
		super.update(mWorldModel);
	}

	protected boolean closeEnough(float headFromTarget) {
		return headFromTarget <= targetRadius;
	}
	
}
