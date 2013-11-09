package com.primalpond.hunt;

import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

import com.primalpond.hunt.world.logic.TheHuntRenderer;

import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.sprite.D3Sprite;
import d3kod.graphics.sprite.SpriteManager;
import d3kod.graphics.sprite.shapes.D3FilledCircle;

public abstract class Demonstrator extends D3Sprite {
//	private static final float DRAWABLE_RAD_DP = 48;
	private static final int DETAILS = 50;
	private static final String TAG = "Demonstrator";

	private static final int SPEED = 1;
//	public static final float RADIUS_DP = 120;
	private static final int PRECISION = 35;
	private static final int COUNTDOWN = 60;
	private static final float RELEASE_TOUCH_SPEED = 0.1f                                                                                                                                                                                                                                                                                                   ;
	private static final float FADING_SPEED = 0.01f;
	private static final float FADE_TO = 0.4f;
	private static final float[] COLOR = {0, 153, 204};
	
	protected TutorialRenderer mRenderer;
	private boolean mTouchDown;
	protected boolean mMonitoringGoalProgress;
	private float[] vertices;
	private int currentVertex;
	private int mUpdateCounter;
	private boolean mReleasingNet;
	private boolean mStartedCountdownCounter;
	private int countdownCounter;
	private boolean mCountDownFinished;
	private boolean mNetFinished;
	private float[] mCenteredNetVertices;
	private int mCountdownTarget;
	private static float DRAWABLE_RAD_DP;
//	private boolean mReleasingTouch;
	public static float RADIUS_PX;

	public Demonstrator(TutorialRenderer renderer, SpriteManager spriteManager) {
		super(new PointF(0, 0), spriteManager);
		mRenderer = renderer;
		RADIUS_PX = MyApplication.APPLICATION.getResources().getDimension(R.dimen.tutorialNetRadius);
		DRAWABLE_RAD_DP = MyApplication.APPLICATION.getResources().getDimension(R.dimen.tutorialDrawableRadius);
		mCenteredNetVertices = D3Maths.circleVerticesData(D3Maths.pxToScreen(RADIUS_PX),
				PRECISION);
	}
	
	protected void touchDown(PointF at) {
		if (mGraphic == null) {
			Log.i(TAG, "Initializing graphic");
			initGraphic();
		}
		else {
			mGraphic.setScale(1);
			mGraphic.noFade();
		}
		MotionEvent down = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, at.x, at.y, 0);
		mRenderer.handleTouch(down, false, false);
		setPosition(at);
		down.recycle();
		mTouchDown = true;
	}
	
	protected void releaseTouch(PointF at) {
		MotionEvent up = MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, at.x, at.y, 0);
		mRenderer.handleTouch(up, false, false);
		setPosition(at);
		setVelocity(0, 0);
		up.recycle();
		if (mGraphic != null) {
			mGraphic.setScale(0);
//			((D3FadingShape)mGraphic).setFadeSpeed(FADING_SPEED);
			
		}
//		mReleasingTouch = true;
//			mGraphic.setScale(0);
//		}
		mTouchDown = false;
	}
	
	public boolean isTouching() {
		return mTouchDown;
	}
	
	@Override
	public void update() {
//		if (mReleasingTouch) {
//			if (mGraphic == null || mGraphic.getScale() <= 0) {
//				mReleasingTouch = false;
//			}
//			else {
//				mGraphic.setScale(mGraphic.getScale() - RELEASE_TOUCH_SPEED);
//			}
//		}
		
		if (mStartedCountdownCounter) {
			countdownCounter++;
			if (countdownCounter >= mCountdownTarget) {
				mCountDownFinished = true;
				mStartedCountdownCounter = false;
			}
		}

		else if (mReleasingNet) {
			if (currentVertex >= vertices.length / 3) {
				mReleasingNet = false;
				mNetFinished = true;
				currentVertex = vertices.length / 3 - 1;
				releaseTouch(new PointF(vertices[currentVertex * 3],
						vertices[currentVertex * 3 + 1]));
			}
			else if (mUpdateCounter >= SPEED) {
				mUpdateCounter = 0;
				float x = vertices[currentVertex * 3];
				float y = vertices[currentVertex * 3 + 1];
				float z = vertices[currentVertex * 3 + 2];

				moveTouch(new PointF(x, y));

				currentVertex += 1;
			}
			else {
				mUpdateCounter++;
			}
		}
		super.update();
	}
	
	protected boolean netFinished() {
		return mNetFinished;
	}
	
	protected void startNet(PointF center) {
		currentVertex = 0;
		vertices = new float[mCenteredNetVertices.length];
		for (int i = 0; i < mCenteredNetVertices.length; ++i) {
			if (i%3 == 0) {
				vertices[i] = mCenteredNetVertices[i] + center.x;
			}
			else if (i%3 == 1) {
				vertices[i] = mCenteredNetVertices[i] + center.y;
			}
			else  if (i%3 == 2) {
				vertices[i] = mCenteredNetVertices[i];
			}
		}
		mReleasingNet = true;
		
	}
	
	protected boolean releasingNet() {
		return mReleasingNet;
	}
	
	protected void clearCount() {
		mCountDownFinished = false;
		mStartedCountdownCounter = false;
	}
	protected void countForSec(int sec) {
		countdownCounter = 0;
		mCountdownTarget = sec * TheHuntRenderer.TICKS_PER_SECOND;
		mStartedCountdownCounter = true;
		mCountDownFinished = false;
	}
	
	public boolean counting() {
		return mStartedCountdownCounter;
	}
	
	protected boolean countFinished() {
		return mCountDownFinished;
	}
	
	protected void moveTouch(PointF at) {
		if (!mTouchDown) {
			touchDown(at);
			return;
		}
		MotionEvent move = MotionEvent.obtain(0, 0, MotionEvent.ACTION_MOVE, at.x, at.y, 0);
		mRenderer.handleTouch(move, false, false);
		PointF prev = getPosition();
		setPosition(at);
		setVelocity(at.x - prev.x, at.y - prev.y);
		
		move.recycle();
	}

	public abstract boolean isFinished();

	@Override
	public void initGraphic() {
		float radius = D3Maths.dpToScreen(DRAWABLE_RAD_DP);
		mGraphic = new D3FilledCircle(radius, COLOR, DETAILS);
		mGraphic.noFade();
		super.initGraphic(mGraphic);
	}

	public void monitorGoalProgress() {
		mMonitoringGoalProgress = true;
	}

	public abstract boolean isSatisfied();
}
