package d3kod.thehunt.prey;

import d3kod.d3gles20.shapes.D3Shape;

abstract public class D3ExpiringShape extends D3Shape {

    private static final float MIN_ALPHA = 0.6f;
	//	private int mTicks;
	private boolean mExpired;
	private float mFadeSpeed;

	protected D3ExpiringShape(float[] colorData, int drType,
			boolean useDefaultShaders, float fadeSpeed) {
		super(colorData, drType, useDefaultShaders);
//		mTicks = ticks;
		mExpired = false;
		mFadeSpeed = fadeSpeed;
	}

	@Override
	public void draw(float[] mVMatrix, float[] mProjMatrix) {
//		if (mTicks <= 0) {
//			mExpired = true;
//			return;
//		}
//		mTicks--;
		if (mExpired) {
			return;
		}
		super.draw(mVMatrix, mProjMatrix);
		super.fade(mFadeSpeed);
		if (super.fadeDone(MIN_ALPHA)) {
			mExpired = true;
		}
	}
	
	public boolean isExpired() {
		return mExpired;
	}

}
