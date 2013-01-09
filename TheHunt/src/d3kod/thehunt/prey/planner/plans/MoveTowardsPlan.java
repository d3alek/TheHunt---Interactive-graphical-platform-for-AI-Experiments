package d3kod.thehunt.prey.planner.plans;

import d3kod.d3gles20.D3Maths;
import d3kod.thehunt.events.Event;
import d3kod.thehunt.prey.Action;
import d3kod.thehunt.prey.Prey;
import d3kod.thehunt.prey.TurnAngle;
import d3kod.thehunt.prey.memory.WorldModel;

public class MoveTowardsPlan extends Plan {
	private static final String TAG = "GoToPlan";
	private static final float TOLERANCE = 0.005f;
	private static final float GOING_TOWARDS_TARGET_ANGLE_MAX = TurnAngle.LEFT_SMALL.getValue()/2;
	private static final int TURN_LARGE_COOLDOWN = 3;
//	private float tX;
//	private float tY;
	private Event mTarget;
	private int turnLeftLargeCooldown;
	private int turnRightLargeCooldown;
	
	public MoveTowardsPlan(float hX, float hY, float bX, float bY, Event target) {
		super(target.getX(), target.getY());
		mTarget = target;
		turnLeftLargeCooldown = 0;
		turnRightLargeCooldown = 0;
//		this.tX = tX; this.tY = tY;
	}
	public void update(WorldModel mWorldModel) {
		float hX = mWorldModel.getHeadX(), hY = mWorldModel.getHeadY(),
				bX = mWorldModel.getBodyX(), bY = mWorldModel.getBodyY();
		float tX = mTarget.getX(), tY = mTarget.getY();
		super.setTarget(tX, tY);
		float headFromTarget = D3Maths.distance(hX, hY, tX, tY),
				bodyFromTarget = D3Maths.distance(bX, bY, tX, tY);
//				headFromBody = D3GLES20.distance(bX, bY, hX, hY);
		float bht = D3Maths.det(bX, bY, hX, hY, tX, tY);
		float angleToTarget = D3Maths.angleBetweenVectors(
				bX - hX, bY - hY, //bodyToHead
				bX - tX, bY - tY, //bodyToTarget
				1, bodyFromTarget); // Prey.bodyToHeadLength
//		Log.v(TAG, "angleToTarget is " + angleToTarget + " bht is " + bht);
		if (bht > 0) {
			//Target is to the left
			if (angleToTarget < GOING_TOWARDS_TARGET_ANGLE_MAX) {
				addNextAction(Action.FORWARD_LARGE);
			}
			else if (angleToTarget < TurnAngle.LEFT_SMALL.getValue()) {
				addNextAction(Action.TURN_LEFT_SMALL);
			}
			else if (angleToTarget <= TurnAngle.LEFT_MEDIUM.getValue()) {
				addNextAction(Action.TURN_LEFT_MEDIUM);
			}
			else {
				//TODO: don't do two large in a row
				if (turnLeftLargeCooldown > 0) {
					addNextAction(Action.TURN_LEFT_MEDIUM);
				}
				else {
					addNextAction(Action.TURN_LEFT_LARGE);
					turnLeftLargeCooldown = TURN_LARGE_COOLDOWN;
				}
			}
			turnLeftLargeCooldown--;
//			else {
//				
//				
//			}
		}
		else {
			//Target is to the right
//			}
			if (angleToTarget < GOING_TOWARDS_TARGET_ANGLE_MAX) {
				addNextAction(Action.FORWARD_LARGE);
			}
			else if (angleToTarget < TurnAngle.LEFT_SMALL.getValue()) {
				addNextAction(Action.TURN_RIGHT_SMALL);
//				Log.v(TAG, "Adding next action to be " + Action.FORWARD_LARGE);
			}
			else if (angleToTarget <= TurnAngle.LEFT_MEDIUM.getValue()) {
				addNextAction(Action.TURN_RIGHT_MEDIUM);
			}
			else {
				if (turnRightLargeCooldown > 0) {
					addNextAction(Action.TURN_RIGHT_MEDIUM);
				}
				else {
					addNextAction(Action.TURN_RIGHT_LARGE);
					turnRightLargeCooldown = TURN_LARGE_COOLDOWN;
				}
			}
			turnRightLargeCooldown--;
		}
	}
	
	public float getTX() {
		return mTarget.getX();
	}
	public float getTY() {
		return mTarget.getY();
	}
}
