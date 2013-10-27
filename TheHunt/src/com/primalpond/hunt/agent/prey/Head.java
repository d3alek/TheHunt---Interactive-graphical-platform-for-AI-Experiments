package com.primalpond.hunt.agent.prey;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.primalpond.hunt.agent.prey.sensor.Sensor.Sensors;

import android.graphics.PointF;
import android.opengl.Matrix;


public class Head extends BodyPart {

	private static final String TAG = "Head";
	private float[] mHeadPosMatrix = new float[16];
	
	public static final float bodyToHeadLength = 0.07f;
	private static final String KEY_EATING_STEP = "eating_step";
	private static final String KEY_EATING_MOTION = "eating_motion";
	private static final String KEY_MOUTH_OPEN = "mouth_open";
	ArrayList<Sensors> mSensors;
//	private int independentMutationsNum = 2;
	private transient HeadGraphic mGraphic;
//	private boolean haveEars;
//	private boolean haveEyes;
	private int mEatingStep = -1;
	private int mEatingMotionStep = 0;
	private boolean mMouthOpen = false;

	public Head(JSONObject savedHead) {
		mSensors = new ArrayList<Sensors>();
		mSensors.add(Sensors.TOUCH_SENSOR);
		
		mSensors.add(Sensors.SIGHT_SENSOR);
		
		mSensors.add(Sensors.HEARING_SENSOR);
		if (savedHead != null) {
			try {
				mEatingStep = savedHead.getInt(KEY_EATING_STEP);
				mEatingMotionStep = savedHead.getInt(KEY_EATING_MOTION);
				mMouthOpen = savedHead.getBoolean(KEY_MOUTH_OPEN);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
//		haveEars = haveEyes = false;
	}
	
	@Override
	public HeadGraphic getGraphic(D3Prey graphic, float size) {
		if (mGraphic == null) {
			mGraphic = new HeadGraphic(graphic, this, size);
			mGraphic.init(mEatingStep, mEatingMotionStep, mMouthOpen);
			mGraphic.putEyes();
			mGraphic.putEars();
		}
		return mGraphic;
	}

	

	public void updatePos(float posX, float posY, float bodyStartAngle) {
		float[] posTemp = { 0.0f, bodyToHeadLength, 0.0f, 1.0f };
		
		Matrix.setIdentityM(mHeadPosMatrix, 0);
		Matrix.translateM(mHeadPosMatrix, 0, posX, posY, 0);
		Matrix.rotateM(mHeadPosMatrix, 0, bodyStartAngle, 0, 0, 1);
		Matrix.multiplyMV(posTemp, 0, mHeadPosMatrix, 0, posTemp, 0);

		setPos(new PointF(posTemp[0], posTemp[1]));
	}
	
//	@Override
//	public void mutate() {
//		int rand = (int)(Math.random()*1000);
//		if (!haveEars && rand % independentMutationsNum == 0) {
//			mSensors.add(Sensors.HEARING_SENSOR);
//			mGraphic.putEars();
//			
//		}
//		else if (!haveEyes && rand % independentMutationsNum == 1) {
//			mSensors.add(Sensors.SIGHT_SENSOR);
//			mGraphic.putEyes();
//		}
//	}

	public ArrayList<Sensors> getSensors() {
		return mSensors;
	}

	public float getSize() {
		return mGraphic.getSize();
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject savedHead = new JSONObject();
		if (mGraphic != null) {
			savedHead.put(KEY_EATING_STEP, mGraphic.eatingStep);
			savedHead.put(KEY_EATING_MOTION, mGraphic.eatingMotionStepCounter);
			savedHead.put(KEY_MOUTH_OPEN, mGraphic.mMouthOpen);
		}
		else {
			savedHead.put(KEY_EATING_STEP, mEatingStep);
			savedHead.put(KEY_EATING_MOTION, mEatingMotionStep);
			savedHead.put(KEY_MOUTH_OPEN, mMouthOpen);
		}
		return savedHead;
	}
	
}

