package com.primalpond.hunt.world.tools;

import android.graphics.PointF;
import android.view.MotionEvent;

import com.primalpond.hunt.world.environment.Environment;
import com.primalpond.hunt.world.floating_text.PlokText;

import d3kod.graphics.sprite.D3Sprite;
import d3kod.graphics.sprite.SpriteManager;
import d3kod.graphics.sprite.shapes.D3Shape;

public class Gestures extends D3Sprite implements Tool {
	
	private Environment mEnv;

	public Gestures(Environment env, SpriteManager d3gles20) {
		super(new PointF(0, 0), d3gles20);
		mEnv = env;
		// TODO Auto-generated constructor stub
	}

	private boolean mStarted;
	private boolean notShown;
	private boolean showSnatchText;
	private D3GesturePath mPathGraphic;
	private float firstY;
	private float firstX;
	private D3GestureAction mActionGraphic;

	private static final float MAX_LENGTH = 2f;
	private static final float MAX_FOOD_PLACEMENT_LENGTH = 0.1f;

	public void start(float x, float y) {
		if (mPathGraphic != null) {
			return;
		}
		
		mStarted = true;
		notShown = true;
		showSnatchText = false;
		
		mPathGraphic = new D3GesturePath();
		setPosition(new PointF(0, 0));
		initGraphic(mPathGraphic);
		
		firstX = x; firstY = y;
		mPathGraphic.addVertex(x, y);
	}

	public void next(float x, float y) {
		if (!mStarted) {
			//TODO: not sure it is needed
			start(x, y);
			return;
		}		
		if (mPathGraphic == null || mPathGraphic.isInvalid() || mPathGraphic.isFinished()) {
			return;
		}
		if (!mPathGraphic.isFarEnoughFromLast(x, y)) {
			return;
		}
		if (mPathGraphic.getLength() > MAX_LENGTH) {
			mPathGraphic.setInvalid();
		}
		mPathGraphic.addVertex(x, y);
		notShown = false;
	}

	public void finish(float x, float y) {
		// TODO: remove restrictions on net placement, handle in game logic
		mStarted = false;
		if (mPathGraphic == null) {
			return;
		}
		if (mPathGraphic.isInvalid() || mPathGraphic.isFinished()) {
			// TODO: is mPathGraphic null now? ...
			return;
		}
		if (mPathGraphic.getLength() < MAX_FOOD_PLACEMENT_LENGTH) {
			// assume food placement was meant
			mPathGraphic = null;
//			mD3GLES20.removeShape(mPathGraphicIndex);
//			Log.v(TAG, "Setting notShown to true because of 2");
			notShown = true;
			return;
		}
		if (mPathGraphic.canFinishNetWith(x, y) || mPathGraphic.canFinishCut()) {
			mPathGraphic.setFinished();
//			initActionGraphic();
			mActionGraphic = mPathGraphic.getAction(mEnv, getSpriteManager());
			if (mActionGraphic == null) {
				mPathGraphic.setInvalid();
				return;
			}
			setPosition(((D3Shape)mActionGraphic).getCenter());
			initGraphic((D3Shape)mActionGraphic);
			getSpriteManager().putText(new PlokText(firstX, firstY));
			mEnv.putNoise(x, y, Environment.LOUDNESS_PLOK); //TODO: put noise in the center
		}
		else {
			mPathGraphic.setInvalid();
		}
	}

	public boolean handleTouch(int action, PointF location) {
		if (action == MotionEvent.ACTION_DOWN) {
			start(location.x, location.y);
			return true;
			
		}
		if (action == MotionEvent.ACTION_MOVE) {
			next(location.x, location.y);
			return true;
			
		}
		if (action == MotionEvent.ACTION_UP) {
			finish(location.x, location.y);
			return !notShown;
		}
		return false;
	}

	public void update() {
		if (mPathGraphic == null) return;
		if (mPathGraphic.fadeDone()) {
			mPathGraphic = null;
		}
		else if (mPathGraphic.isFinished() && !mPathGraphic.isInvalid() && mActionGraphic == null) {
			mActionGraphic = mPathGraphic.getAction(mEnv, getSpriteManager());
			setPosition(((D3Shape)mActionGraphic).getCenter());
			initGraphic((D3Shape)mActionGraphic);
			getSpriteManager().putText(new PlokText(mPathGraphic.getCenterX(), mPathGraphic.getCenterY()));
			mEnv.putNoise(mPathGraphic.getCenterX(), mPathGraphic.getCenterY(), Environment.LOUDNESS_PLOK); //TODO noise in center
		}
		if (mActionGraphic == null) {
			return;
		}
		if (mActionGraphic.isFinished()) {
//			((D3Shape)mActionGraphic).setFaded();
			getSpriteManager().removeShape((D3Shape)mActionGraphic);
			initGraphic(null);
			mActionGraphic = null;
			mPathGraphic = null;
			getSpriteManager().putSprite(this);
		}
		else {
			mActionGraphic.update();
		}
	}

	public void stop(PointF location) {
		if (mPathGraphic != null) {
			getSpriteManager().removeShape(mPathGraphic);
			initGraphic(null);
			mPathGraphic = null;
			mActionGraphic = null;
			getSpriteManager().putSprite(this);
		}
		if (mActionGraphic != null) {
			getSpriteManager().removeShape((D3Shape)mActionGraphic);
			initGraphic(null);
			mPathGraphic = null;
			mActionGraphic = null;
			getSpriteManager().putSprite(this);
		}

	}

	public boolean isActive() {
		return mActionGraphic != null;
	}

	public boolean didAction() {
		return false;
	}

	@Override
	public void initGraphic() {
		// TODO Auto-generated method stub
		
	}

}
