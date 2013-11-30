package com.primalpond.hunt.world.tools;

import android.graphics.PointF;
import android.util.Log;

import com.primalpond.hunt.agent.Agent;
import com.primalpond.hunt.world.environment.Environment;
import com.primalpond.hunt.world.environment.FloatingObject;

import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.sprite.shapes.D3Circle;

public class D3GestureActionNet extends D3Circle implements D3GestureAction {

	private static final float FADE_SPEED = 0.05f;
	private static float[] isBuiltColor = {
		0.0f, 0.0f, 0.0f, 1.0f
	};
	private static final int VERTICES_NUM = 100;

	private static final float mGrowSpeed = 0.05f;
	private static final String TAG = "D3CatchNet";
	private static final float START_RADIUS = 0.03f;
	private Environment mEnv;
	private boolean mFinished;

	public D3GestureActionNet(float x, float y, float radius, Environment env) {
		super(radius, isBuiltColor, VERTICES_NUM);
		mEnv = env;
		setPosition(x, y);
		setScale(START_RADIUS/radius);
	}

	public void grow() {
		super.grow(mGrowSpeed);
	}
	
	@Override
	public void draw(float[] mVMatrix, float[] mProjMatrix) {
		if (getScale() >= 1) {
			fade(FADE_SPEED);
		}
		super.draw(mVMatrix, mProjMatrix);
	}

	public boolean isFinished() {
		return mFinished;
	}

	public void update() {
		if (getScale() < 1) {
			grow();
		}
		else {
			Agent prey = mEnv.getPrey();
			if (prey != null && !prey.getCaught() && !prey.isHidden() && contains(prey.getPosition())) {
				Log.v(TAG, "I caught the prey!");
				prey.setCaught(true);
			}
			for (FloatingObject fo:mEnv.seeObjects(getCenterX(), getCenterY(), getRadius())) {
				if (fo.getRadius() < getRadius()) {
					Log.v(TAG, "catching floating object " + fo.getType());
					mEnv.playerRemoves(fo);
				}
				else {
					//TODO: shake animation, object too big
				}
			}
			setFaded();
			mFinished = true;
		}
	}
	
	public boolean contains(PointF point) {
		if (point == null) {
			Log.v(TAG, "Illegal contains call because point is null");
			return false;
		}
		return D3Maths.circleContains(getCenterX(), getCenterY(), getRadius(), point.x, point.y); //TODO: fix getRadius() call, keep seperate radiuses for graphic and logic
	}
}
