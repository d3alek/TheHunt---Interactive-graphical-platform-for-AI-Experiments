package com.primalpond.hunt.agent.prey;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.PointF;
import android.util.Log;


public class Body extends BodyPart {
	private static final String KEY_POS_X = "posx";
	private static final String KEY_POS_Y = "posy";
	private static final String KEY_BODY_ANGLE_START = "angle_start";
	private static final String KEY_BODY_ANGLE_B = "angle_b";
	private static final String KEY_BODY_ANGLE_C = "angle_c";
	private static final String KEY_BODY_ANGLE_END = "angle_end";
	private static final String KEY_BODY_ANGLE_ROT_B = "angle_rot_b";
	private static final String KEY_BODY_ANGLE_ROT_START = "angle_rot_start";
	private static final String KEY_BODY_ANGLE_ROT_C = "angle_rot_c";
	private static final String KEY_BODY_ANGLE_ROT_END = "angle_rot_end";
	private static final String KEY_BODY_ANGLE_TARGET_START = "angle_target_start";
	private static final String KEY_BODY_ANGLE_TARGET_B = "angle_target_b";
	private static final String KEY_BODY_ANGLE_TARGET_C = "angle_target_c";
	private static final String KEY_BODY_ANGLE_TARGET_END = "angle_target_end";
	private static final String KEY_ROTATE_SPEED_HEAD = "speed_head";
	private static final String KEY_ROTATE_SPEED_C = "speed_c";
	private static final String KEY_ROTATE_SPEED_END = "speed_end";
	private static final String KEY_BODY_BEND_COUNTER = "body_bend_count";
	private static final String KEY_FLOP_BACK = "flop_back";
	private static final String KEY_FLOP_BACK_TARGET_FIRST = "flop_target_first";
	private static final String KEY_FLOP_BACK_TARGET_SECOND = "flop_target_second";
	private static final String KEY_FLOPPED_FIRST = "flopped_first";
	private static final String KEY_FLOPPED_SECOND = "flopped_second";
	private static final String KEY_FLOPPED_THIRD = "flopped_third";
	private static final String KEY_TURNING_BACK_FIN_MOTION = "turning_back_fin";
	private static final String KEY_FLOP_BACK_ANGLE = "flop_back_angle";
	private static final String KEY_FLOP_BACK_SPEED = "flop_back_speed";
	private static final String KEY_BACK_FIN_ANGLE = "back_fin_angle";
	private static final String KEY_ROTATE_SPEED_B = "speed_b";
	private static final String TAG = "Body";
	// angles
	public float bodyStartAngle;
	public float bodyBAngle;
	public float bodyCAngle;
	public float bodyEndAngle;
	
	// angleRots
	protected float bodyStartAngleRot;
	protected float bodyBAngleRot;
	protected float bodyCAngleRot;
	protected float bodyEndAngleRot;
	
	// angleTargets
	public int bodyEndAngleTarget;
	public int bodyCAngleTarget;
	public int bodyBAngleTarget;
	public int bodyStartAngleTarget;
	
	// speed
	public float rotateSpeedHead;
	public float bodyBSpeed;
	public float bodyCSpeed;
	public float bodyEndSpeed;
	
	// misc
	private int bodyBendCounter;
	private boolean flopBack;
	protected int flopBackTargetFirst;
	protected int flopBackTargetSecond;
	protected boolean floppedFirst;
	protected boolean floppedSecond;
	private boolean floppedThird;
	protected boolean turningBackFinMotion;
	protected float flopBackAngle;
	private float flopBackSpeed;
	protected int backFinAngle;
	
	
	private float mForce;
	protected final float MAX_BODY_BEND_ANGLE = 110;
	
	public Body(JSONObject savedBody) {
		rotateSpeedHead = PreyData.rotateSpeedSmall;//Math.abs(TurnAngle.LEFT_SMALL.getValue())/SMALL_TICKS_PER_TURN;
		Log.i(TAG, "Saved body is " + savedBody);
		if (savedBody != null) {
			try {
			setPos(new PointF(
					(float)savedBody.getDouble(KEY_POS_X),
					(float)savedBody.getDouble(KEY_POS_Y)));
			Log.i(TAG, "Set pos to " + getPos());
			bodyStartAngle = (float)savedBody.getDouble(KEY_BODY_ANGLE_START);
			bodyBAngle = (float)savedBody.getDouble(KEY_BODY_ANGLE_B);
			bodyCAngle = (float)savedBody.getDouble(KEY_BODY_ANGLE_C);
			bodyEndAngle = (float)savedBody.getDouble(KEY_BODY_ANGLE_END);
			
			bodyStartAngleRot = (float)savedBody.getDouble(KEY_BODY_ANGLE_ROT_START);
			bodyBAngleRot = (float)savedBody.getDouble(KEY_BODY_ANGLE_ROT_B);
			bodyCAngleRot = (float)savedBody.getDouble(KEY_BODY_ANGLE_ROT_C);
			bodyEndAngleRot = (float)savedBody.getDouble(KEY_BODY_ANGLE_ROT_END);
			
			bodyStartAngleTarget = savedBody.getInt(KEY_BODY_ANGLE_TARGET_START);
			bodyBAngleTarget = savedBody.getInt(KEY_BODY_ANGLE_TARGET_B);
			bodyCAngleTarget = savedBody.getInt(KEY_BODY_ANGLE_TARGET_C);
			bodyEndAngleTarget = savedBody.getInt(KEY_BODY_ANGLE_TARGET_END);
			
			rotateSpeedHead = (float)savedBody.getDouble(KEY_ROTATE_SPEED_HEAD);
			bodyBSpeed = (float)savedBody.getDouble(KEY_ROTATE_SPEED_B);
			bodyCSpeed = (float)savedBody.getDouble(KEY_ROTATE_SPEED_C);
			bodyEndSpeed = (float)savedBody.getDouble(KEY_ROTATE_SPEED_END);
			
			bodyBendCounter = savedBody.getInt(KEY_BODY_BEND_COUNTER);
			flopBack = savedBody.getBoolean(KEY_FLOP_BACK);
			flopBackTargetFirst = savedBody.getInt(KEY_FLOP_BACK_TARGET_FIRST);
			flopBackTargetSecond = savedBody.getInt(KEY_FLOP_BACK_TARGET_SECOND);
			floppedFirst = savedBody.getBoolean(KEY_FLOPPED_FIRST);
			floppedSecond = savedBody.getBoolean(KEY_FLOPPED_SECOND);
			floppedThird = savedBody.getBoolean(KEY_FLOPPED_THIRD);
			turningBackFinMotion = savedBody.getBoolean(KEY_TURNING_BACK_FIN_MOTION);
			flopBackAngle = (float)savedBody.getDouble(KEY_FLOP_BACK_ANGLE);
			flopBackSpeed = savedBody.getInt(KEY_FLOP_BACK_SPEED);
			backFinAngle = savedBody.getInt(KEY_BACK_FIN_ANGLE);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public BodyPartGraphic getGraphic(D3Prey graphic, float size) {
		return new BodyGraphic(graphic, this, size);
	}

	public float getTopAngle() {
		return bodyStartAngle;
	}

	public void update() {
		mForce = 0;
		if (bodyBendCounter == 0) {
			bodyEndAngleTarget = bodyCAngleTarget;
			bodyEndSpeed = bodyCSpeed;
			bodyCAngleTarget = bodyBAngleTarget;
			bodyCSpeed = bodyBSpeed;
			bodyBAngleTarget = bodyStartAngleTarget;
			bodyBSpeed = rotateSpeedHead;
			bodyBendCounter = PreyData.BODY_BEND_DELAY-1;
		}
		else {
			--bodyBendCounter;
		}

		if (bodyStartAngleTarget > bodyStartAngle + rotateSpeedHead) bodyStartAngleRot = rotateSpeedHead;
		else if (bodyStartAngleTarget < bodyStartAngle - rotateSpeedHead) bodyStartAngleRot = -rotateSpeedHead;
		else {
			bodyStartAngleRot = 0;
			bodyStartAngle = bodyStartAngleTarget;
		}

		if (bodyBAngleTarget > bodyBAngle + bodyBSpeed) bodyBAngleRot = +bodyBSpeed;
		else if (bodyBAngleTarget < bodyBAngle - bodyBSpeed) bodyBAngleRot = -bodyBSpeed;
		else {
			bodyBAngleRot = 0;
			bodyBAngle = bodyBAngleTarget;
		}

		if (bodyCAngleTarget > bodyCAngle + bodyCSpeed) bodyCAngleRot = +bodyCSpeed;
		else if (bodyCAngleTarget < bodyCAngle - bodyCSpeed) bodyCAngleRot = -bodyCSpeed;
		else {
			bodyCAngleRot = 0;
			bodyCAngle = bodyCAngleTarget;
		}	
		
		if (!flopBack) {
			if (bodyEndAngleTarget > bodyEndAngle + bodyEndSpeed) bodyEndAngleRot = bodyEndSpeed;
			else if (bodyEndAngleTarget < bodyEndAngle - bodyEndSpeed) bodyEndAngleRot = -bodyEndSpeed;
			else {
				bodyEndAngleRot = 0;
				bodyEndAngle = bodyEndAngleTarget;
			}
		}
		
		if (flopBack) doFlopBack();

		bodyStartAngle += bodyStartAngleRot;
		bodyBAngle += bodyBAngleRot;
		bodyCAngle += bodyCAngleRot;
		if (!flopBack) {
			bodyEndAngle += bodyEndAngleRot;
		}
	}
	
	private boolean stoppedTurning() {
		return (bodyStartAngleTarget == bodyBAngleTarget 
				&& bodyBAngleTarget == bodyCAngleTarget);
	}

	private void doFlopBack() {
		if (!floppedFirst) {
			if (flopBackTargetFirst > flopBackAngle + flopBackSpeed) flopBackAngle += flopBackSpeed;
			else if (flopBackTargetFirst < flopBackAngle - flopBackSpeed) flopBackAngle -= flopBackSpeed;
			else {
				flopBackAngle = flopBackTargetFirst;
				floppedFirst = true;
//				moveForward(Math.abs(backFinAngle*flopBackSpeed)); // F = ma
				mForce += Math.abs(backFinAngle*flopBackSpeed);
//				Log.v(TAG, "Flop back!");
//				putFlopText(flopBackAngle + bodyCAngle);
			}
			bodyEndAngleRot = bodyCAngle + flopBackAngle-bodyEndAngle;
			bodyEndAngle = bodyCAngle + flopBackAngle;
		}
		else if (!floppedSecond) {
			if (flopBackTargetSecond > flopBackAngle + flopBackSpeed) flopBackAngle += flopBackSpeed;
			else if (flopBackTargetSecond < flopBackAngle - flopBackSpeed) flopBackAngle -= flopBackSpeed;
			else {
				flopBackAngle = flopBackTargetSecond;
				floppedSecond = true;
//				moveForward(Math.abs(2*backFinAngle*flopBackSpeed)); // F = ma
				mForce += Math.abs(2*backFinAngle*flopBackSpeed);
//				Log.v(TAG, "Flop back!");
//				putFlopText(flopBackAngle + bodyCAngle);
			}
			bodyEndAngleRot = bodyCAngle + flopBackAngle-bodyEndAngle;
			bodyEndAngle = bodyCAngle + flopBackAngle;
		}
		else {
			//flopping third
			if (0 > flopBackAngle + flopBackSpeed) flopBackAngle += flopBackSpeed;
			else if (0 < flopBackAngle - flopBackSpeed) flopBackAngle -= flopBackSpeed;
			else {
				flopBackAngle = 0;
				floppedThird = true;
//				moveForward(Math.abs(backFinAngle*flopBackSpeed)); // F = ma
				mForce += Math.abs(backFinAngle*flopBackSpeed);
			}
			bodyEndAngleRot = bodyCAngle + flopBackAngle-bodyEndAngle;
			bodyEndAngle = bodyCAngle + flopBackAngle;
		}
		if (floppedFirst && floppedSecond && floppedThird) {
			if (turningBackFinMotion) {
				turningBackFinMotion = false;
				flopBack = false;
			}
			else {
				flopBack = false;
			}
		}
	}
	public void backFinMotion(TurnAngle angle) {
		flopBack = true;
		backFinAngle = angle.getValue();
		bodyEndAngle = bodyCAngle;
		flopBackTargetFirst = +backFinAngle;
		flopBackAngle = 0;
		flopBackTargetSecond = -backFinAngle;
		floppedFirst = false;
		floppedSecond = false;
		floppedThird = false;
		flopBackSpeed = angle.getRotateSpeed();
	}

	public boolean bend(TurnAngle angle) {
		int value = angle.getValue();
		
		if (bodyStartAngleTarget + value - bodyCAngle > MAX_BODY_BEND_ANGLE 
				|| bodyStartAngleTarget + value - bodyCAngle < -MAX_BODY_BEND_ANGLE) {
			return false;
		}
		rotateSpeedHead = angle.getRotateSpeed();
		bodyStartAngleTarget += value;

		if (!turningBackFinMotion) {
			turningBackFinMotion = true;
//			turningBackFinAngle = angle.getBackAngle();
			backFinMotion(angle.getBackAngle());
		}
		return false;
	}

	public float getForce() {
		return mForce;
	}

	public float getBottomAngle() {
		return bodyEndAngle;
	}

	public double getFacingAngle() {
		return bodyCAngle;
	}

	public void noRot() {
		bodyEndAngleRot = bodyStartAngleRot = bodyBAngleRot = bodyCAngleRot = 0;
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject savedBody = new JSONObject();

		PointF pos = getPos();
		savedBody.put(KEY_POS_X, pos.x);
		savedBody.put(KEY_POS_Y, pos.y);
		savedBody.put(KEY_BODY_ANGLE_START, bodyStartAngle);
		savedBody.put(KEY_BODY_ANGLE_B, bodyBAngle);
		savedBody.put(KEY_BODY_ANGLE_C, bodyCAngle);
		savedBody.put(KEY_BODY_ANGLE_END, bodyEndAngle);
		
		savedBody.put(KEY_BODY_ANGLE_ROT_START, bodyStartAngleRot);
		savedBody.put(KEY_BODY_ANGLE_ROT_B, bodyBAngleRot);
		savedBody.put(KEY_BODY_ANGLE_ROT_C, bodyCAngleRot);
		savedBody.put(KEY_BODY_ANGLE_ROT_END, bodyEndAngleRot);
		
		savedBody.put(KEY_BODY_ANGLE_TARGET_START, bodyStartAngleTarget);
		savedBody.put(KEY_BODY_ANGLE_TARGET_B, bodyBAngleTarget);
		savedBody.put(KEY_BODY_ANGLE_TARGET_C, bodyCAngleTarget);
		savedBody.put(KEY_BODY_ANGLE_TARGET_END, bodyEndAngleTarget);
		
		savedBody.put(KEY_ROTATE_SPEED_HEAD, rotateSpeedHead);
		savedBody.put(KEY_ROTATE_SPEED_B, bodyBSpeed);
		savedBody.put(KEY_ROTATE_SPEED_C, bodyCSpeed);
		savedBody.put(KEY_ROTATE_SPEED_END, bodyEndSpeed);
		
		savedBody.put(KEY_BODY_BEND_COUNTER, bodyBendCounter);
		savedBody.put(KEY_FLOP_BACK, flopBack);
		savedBody.put(KEY_FLOP_BACK_TARGET_FIRST, flopBackTargetFirst);
		savedBody.put(KEY_FLOP_BACK_TARGET_SECOND, flopBackTargetSecond);
		savedBody.put(KEY_FLOPPED_FIRST, floppedFirst);
		savedBody.put(KEY_FLOPPED_SECOND, floppedSecond);
		savedBody.put(KEY_FLOPPED_THIRD, floppedThird);
		savedBody.put(KEY_TURNING_BACK_FIN_MOTION, turningBackFinMotion);
		savedBody.put(KEY_FLOP_BACK_ANGLE, flopBackAngle);
		savedBody.put(KEY_FLOP_BACK_SPEED, flopBackSpeed);
		savedBody.put(KEY_BACK_FIN_ANGLE, backFinAngle);
		return savedBody;
	}

}
