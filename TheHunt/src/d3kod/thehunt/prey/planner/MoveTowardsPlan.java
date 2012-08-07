package d3kod.thehunt.prey.planner;

import android.util.Log;
import d3kod.d3gles20.D3GLES20;
import d3kod.thehunt.prey.Action;
import d3kod.thehunt.prey.memory.WorldModel;

public class MoveTowardsPlan extends Plan {
	private static final String TAG = "GoToPlan";
	protected float tX;
	protected float tY;
	
//	private boolean arrived;
	public MoveTowardsPlan(float hX, float hY, float bX, float bY, float tX, float tY) {
		super(tX, tY);
		
		this.tX = tX; this.tY = tY;
//		if (fX == -1 || fY == -1) {
//			Log.v(TAG, "No food location known!");
//			return;
//		}
//		arrived = false;
	}
	public void update(WorldModel mWorldModel) {
//		if (arrived) return;
		float hX = mWorldModel.getHeadX(), hY = mWorldModel.getHeadY(),
				bX = mWorldModel.getBodyX(), bY = mWorldModel.getBodyY();
		float headFromTarget = D3GLES20.distance(hX, hY, tX, tY),
				bodyFromTarget = D3GLES20.distance(bX, bY, tX, tY);
//		if (headFromTarget <= DISTANCE_ENOUGH) {
//			arrived = true;
//			if (isEmpty()) addNextAction(Action.none);
//			return;
//		}
		float bhf = D3GLES20.det(bX, bY, hX, hY, tX, tY);
		if (bhf > 0) {
			addNextAction(Action.TURN_LEFT_SMALL);
		}
		else if (bhf < 0) {
			addNextAction(Action.TURN_RIGHT_SMALL);
		}
		else if (headFromTarget > bodyFromTarget) {
			addNextAction(Action.TURN_LEFT_SMALL);
		}
		else {
			addNextAction(Action.TURN_LEFT_SMALL);
			addNextAction(Action.TURN_RIGHT_SMALL);
		}
	}
}
