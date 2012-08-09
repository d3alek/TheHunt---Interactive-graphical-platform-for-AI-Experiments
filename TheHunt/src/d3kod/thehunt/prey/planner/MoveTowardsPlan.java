package d3kod.thehunt.prey.planner;

import android.util.Log;
import d3kod.d3gles20.D3GLES20;
import d3kod.thehunt.prey.Action;
import d3kod.thehunt.prey.Prey;
import d3kod.thehunt.prey.PreyData;
import d3kod.thehunt.prey.TurnAngle;
import d3kod.thehunt.prey.memory.WorldModel;

public class MoveTowardsPlan extends Plan {
	private static final String TAG = "GoToPlan";
	private static final float TOLERANCE = 0.005f;
	protected float tX;
	protected float tY;
	
	public MoveTowardsPlan(float hX, float hY, float bX, float bY, float tX, float tY) {
		super(tX, tY);
		
		this.tX = tX; this.tY = tY;
	}
	public void update(WorldModel mWorldModel) {
		float hX = mWorldModel.getHeadX(), hY = mWorldModel.getHeadY(),
				bX = mWorldModel.getBodyX(), bY = mWorldModel.getBodyY();
		float headFromTarget = D3GLES20.distance(hX, hY, tX, tY),
				bodyFromTarget = D3GLES20.distance(bX, bY, tX, tY);
//				headFromBody = D3GLES20.distance(bX, bY, hX, hY);
		float bht = D3GLES20.det(bX, bY, hX, hY, tX, tY);
		float angleToTarget = D3GLES20.angleBetweenVectors(
				bX - hX, bY - hY, //bodyToHead
				bX - tX, bY - tY, //bodyToTarget
				Prey.bodyToHeadLength, bodyFromTarget);
//		Log.v(TAG, "angleToTarget is " + angleToTarget + " bht is " + bht);
		if (bht > 0) {
			//Target is to the left
//			Log.v(TAG, "Target is to the left");
//			if (angleToTarget < TurnAngle.LEFT_SMALL.getValue()) {
//				//addNextAction(Action.TURN_LEFT_SMALL);
//				addNextAction(Action.FORWARD_LARGE);
//			}
//			else if (angleToTarget < TurnAngle.LEFT_MEDIUM.getValue()) {
//				addNextAction(Action.TURN_LEFT_SMALL);
//			}
//			else if (angleToTarget < TurnAngle.LEFT_LARGE.getValue()) {
//				addNextAction(Action.TURN_LEFT_MEDIUM);
//			}
//			else {
//				addNextAction(Action.TURN_LEFT_LARGE);
//			}
//			if (angleToTarget < TurnAngle.LEFT_SMALL.getValue()/2) {
//				addNextAction(Action.FORWARD_LARGE);
//			}
//			else if (angleToTarget < TurnAngle.LEFT_SMALL.getValue()) {
//				addNextAction(Action.TURN_LEFT_SMALL);
////				addNextAction(Action.FORWARD_LARGE);
//			}
//			else if (angleToTarget < TurnAngle.LEFT_MEDIUM.getValue()) {
////				addNextAction(Action.TURN_RIGHT_SMALL);
//				addNextAction(Action.TURN_LEFT_MEDIUM);
//			}
//			else {
////				addNextAction(Action.TURN_RIGHT_MEDIUM);
//				addNextAction(Action.TURN_LEFT_LARGE);
//			}
			if (angleToTarget < TurnAngle.LEFT_SMALL.getValue()) {
				addNextAction(Action.FORWARD_LARGE);
			}
			else if (angleToTarget <= TurnAngle.LEFT_MEDIUM.getValue()) {
				addNextAction(Action.TURN_LEFT_SMALL);
			}
			else if (angleToTarget <= TurnAngle.LEFT_LARGE.getValue()) {
				addNextAction(Action.TURN_LEFT_MEDIUM);
			}
			else addNextAction(Action.TURN_LEFT_LARGE);
		}
		else {
			//Target is to the right
//			Log.v(TAG, "Target is to the right");
//			if (angleToTarget < TurnAngle.LEFT_SMALL.getValue()) {
//				//addNextAction(Action.TURN_LEFT_SMALL);
//				addNextAction(Action.FORWARD_LARGE);
//			}
//			else if (angleToTarget < TurnAngle.LEFT_MEDIUM.getValue()) {
//				addNextAction(Action.TURN_RIGHT_SMALL);
//			}
//			else if (angleToTarget < TurnAngle.LEFT_LARGE.getValue()) {
//				addNextAction(Action.TURN_RIGHT_MEDIUM);
//			}
//			else {
//				addNextAction(Action.TURN_RIGHT_LARGE);
//			}
//			if (angleToTarget < TurnAngle.LEFT_SMALL.getValue()/2) {
//				addNextAction(Action.FORWARD_LARGE);
//			}
//			else if (angleToTarget < TurnAngle.LEFT_SMALL.getValue()) {
//				addNextAction(Action.TURN_RIGHT_SMALL);
////				addNextAction(Action.FORWARD_LARGE);
//			}
//			else if (angleToTarget < TurnAngle.LEFT_MEDIUM.getValue()) {
////				addNextAction(Action.TURN_RIGHT_SMALL);
//				addNextAction(Action.TURN_RIGHT_MEDIUM);
//			}
//			else {
////				addNextAction(Action.TURN_RIGHT_MEDIUM);
//				addNextAction(Action.TURN_RIGHT_LARGE);
//			}
			if (angleToTarget < TurnAngle.LEFT_SMALL.getValue()) {
				addNextAction(Action.FORWARD_LARGE);
//				Log.v(TAG, "Adding next action to be " + Action.FORWARD_LARGE);
			}
			else if (angleToTarget <= TurnAngle.LEFT_MEDIUM.getValue()) {
				addNextAction(Action.TURN_RIGHT_SMALL);
			}
			else if (angleToTarget <= TurnAngle.LEFT_LARGE.getValue()) {
				addNextAction(Action.TURN_RIGHT_MEDIUM);
			}
			else addNextAction(Action.TURN_RIGHT_LARGE);
		}
//		if (angleToTarget > TOLERANCE) {
//			addNextAction(Action.TURN_LEFT_SMALL);
//		}
//		else if (angleToTarget < -TOLERANCE) {
//			addNextAction(Action.TURN_RIGHT_SMALL);
//		}
//		else if (headFromTarget > bodyFromTarget) {
//			addNextAction(Action.TURN_LEFT_LARGE);
//		}
//		else {
////			addNextAction(Action.TURN_LEFT_SMALL);
////			addNextAction(Action.TURN_RIGHT_SMALL);
//			addNextAction(Action.FORWARD_LARGE);
//		}
	}
}
