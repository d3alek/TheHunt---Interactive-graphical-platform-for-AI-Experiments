package com.primalpond.hunt.world.tools;

import android.graphics.PointF;
import android.util.Log;

import com.primalpond.hunt.world.environment.Environment;
import com.primalpond.hunt.world.environment.NAlgae;

import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.shader.ShaderProgramManager;
import d3kod.graphics.sprite.shapes.D3Image;
import d3kod.graphics.sprite.shapes.D3TriangleFill;
import d3kod.graphics.texture.TextureInfo;

public class D3GestureActionKnife extends D3TriangleFill implements D3GestureAction {

	private static final float SIZE = 0.2f;
	private static final float VELOCITY = 0.01f;
	private static final float DISTANCE_TO_FINISHED = 0.1f;
	private static final String TAG = "D3GestureActionKnife";
	private static float[] color = {0.0f, 0.0f, 0.0f, 1.0f};
	private PointF mDrawTo;
	private PointF mDir;
	private boolean mStarted;
	private PointF start;
	private Environment mEnv;
	private NAlgae newCuttingAlgae;
	private NAlgae cuttingAlgae;
	private boolean didCut;
	private PointF mPosition;
	private float mLength;
	private PointF mStartPos;
	private float mAngle;
	static float[] rectPositionData = {
			0.0f, 0.0f, 0f,
			0.0f, 1.0f, 0f,
			1.0f, 0.0f, 0f,				
			1.0f, 1.0f, 0f,
	};
	//	private boolean mIsFinished;

	public D3GestureActionKnife(PointF pos, PointF dir, PointF to, Environment env, TextureInfo textureInfo, ShaderProgramManager sm) {
//		super(textureInfo, SIZE, sm, rectPositionData);
		super(SIZE/2, SIZE);
		setColor(new float[]{0.0f, 0.0f, 0.0f, 1.0f});
		setVelocity(VELOCITY*dir.x, VELOCITY*dir.y);
		mDir = dir;
		mAngle = -D3Maths.angleBetweenVectors(mDir.x, mDir.y, 0, 1, 1, 1);
		Log.i(TAG, "mAngle is " + mAngle);
		setPosition(pos.x - VELOCITY*dir.x, pos.y - VELOCITY*dir.y, mAngle);
		mPosition = getCenter();
		mDrawTo = to;
		mStarted = false;
		mEnv = env;
		mLength = D3Maths.distance(pos.x, pos.y, to.x, to.y);
		mStartPos = pos;
	}

	public boolean isFinished() {
		float d = D3Maths.distance(getCenterX(), getCenterY(), mDrawTo.x, mDrawTo.y);
		Log.i(TAG, "DIST TO FINISHED IS " + d + " direction is is " + mDir.x + " " + mDir.y);
		return (d < DISTANCE_TO_FINISHED);
	}

	public void update() {
		float distSoFar = D3Maths.distance(mPosition.x, mPosition.y, mStartPos.x, mStartPos.y);
		float vx = VELOCITY*mDir.x*(1 + distSoFar/mLength);
		float vy = VELOCITY*mDir.y*(1 + distSoFar/mLength);
		mPosition.offset(vx, vy);
		setPosition(mPosition.x, mPosition.y, mAngle);
		PointF location = new PointF();
		location.set(mPosition);
//		PointF location = getCenter();
		setVelocity(vx, vy);
		//		Log.i(TAG, "Location is " + location);
		//		if (!mStarted) {
		newCuttingAlgae = mEnv.knifeIntersectsWithAlgae(location.x, location.y);
		if (newCuttingAlgae == null) {
			cuttingAlgae = null;
		}
		if (newCuttingAlgae != cuttingAlgae) {
			start = location;
			//				mStarted = true;
			didCut = false;
			cuttingAlgae = newCuttingAlgae;
		}
		//		}
		else if (cuttingAlgae == null) {
			return;
		}
		else if (!didCut &&  D3Maths.distance(location.x, location.y, start.x, start.y) > cuttingAlgae.getRadius()) {
			//			PointF cuttingDir = new PointF(location.x - start.x, location.y - start.y);
			//			float cuttingDirLength = cuttingDir.length();
			//			cuttingDir.set(cuttingDir.x/cuttingDirLength, cuttingDir.y/cuttingDirLength);
			//			mIsFinished = true;
			cuttingAlgae.cut(mDir);
			didCut = true;
		}

	}

}
