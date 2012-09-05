package d3kod.thehunt.prey.planner.plans;

import android.util.Log;
import d3kod.d3gles20.D3Maths;
import d3kod.thehunt.prey.Action;
import d3kod.thehunt.prey.memory.WorldModel;

public class GoToAndStayPlan extends MoveTowardsPlan {
	//private static final float SLOW_DOWN_DISTANCE = 0.5f;
	private static final float DISTANCE_ENOUGH = 0.1f;
//	private static final float DISTANCE_MEDIUM = 1f;
	private static final float DISTANCE_SMALL = 0.3f; // same as algae radius
//	private static final int KEEP_DISTANCE_FOR = 1;
	private static final String TAG = "GoToAndStayPlan";
	
	private int slowdown;
	private int keepDistanceCounter;
	private int slowdownCounter;
	
	public GoToAndStayPlan(float hX, float hY, float bX, float bY, float tX,
			float tY) {
		super(hX, hY, bX, bY, tX, tY);
		keepDistanceCounter = 0;
		slowdownCounter = 0;
	}
	
	@Override
	public void update(WorldModel mWorldModel) {
		
		float hX = mWorldModel.getHeadX(), hY = mWorldModel.getHeadY();
		float headFromTarget = D3Maths.distance(hX, hY, getTX(), getTY());
		
		if (headFromTarget <= DISTANCE_ENOUGH) {
//			keepDistanceCounter++;
			if (super.isEmpty()) {
				Log.v(TAG, "Close enough to target but plan empty " + getTX() + " " + getTY());
//				super.update(mWorldModel)
			}
			else {
				finish();
				return;
			}
		}
//		else keepDistanceCounter = 0;
//		
//		if (keepDistanceCounter >= KEEP_DISTANCE_FOR) {
//			finish();
//			return;
//		}
		
		super.update(mWorldModel);
		
//		Action nextAction = getNextAction();
//		if (headFromTarget < DISTANCE_SMALL) {
//			switch(nextAction) {
//			case FORWARD_LARGE: changeNextAction(Action.FORWARD_SMALL); break;
//			case TURN_LEFT_LARGE: changeNextAction(Action.TURN_LEFT_SMALL); break;
//			case TURN_RIGHT_LARGE: changeNextAction(Action.TURN_RIGHT_SMALL); break;
//			}
//		}
//		else if (headFromTarget < DISTANCE_MEDIUM) {
//			switch(nextAction) {
//			case FORWARD_LARGE: changeNextAction(Action.FORWARD_MEDIUM); break;
//			case TURN_LEFT_LARGE: changeNextAction(Action.TURN_LEFT_MEDIUM); break;
//			case TURN_RIGHT_LARGE: changeNextAction(Action.TURN_RIGHT_MEDIUM); break;
//			}
//		}
		if (getNextAction() == Action.FORWARD_LARGE) {
			if (headFromTarget < DISTANCE_SMALL) {
				changeNextAction(Action.FORWARD_SMALL);
			}
//			else if (headFromTarget < DISTANCE_MEDIUM) {
//				changeNextAction(Action.FORWARD_MEDIUM);
//			}
		}
	}
}
