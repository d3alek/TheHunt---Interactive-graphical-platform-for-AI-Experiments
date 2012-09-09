package d3kod.d3gles20.shapes;

import d3kod.d3gles20.programs.Program;


abstract public class D3FadingShape extends D3Shape {

//    private static final float MIN_ALPHA = 0.6f;
	private boolean mExpired;
	private float mFadeSpeed;
	private float mMaxFade;

	protected D3FadingShape(float[] colorData, int drType,
			Program program, float fadeSpeed, float maxFade) {
		super(colorData, drType, program);
		mExpired = false;
		mFadeSpeed = fadeSpeed;
		mMaxFade = maxFade;
	}

	@Override
	public void draw(float[] mVMatrix, float[] mProjMatrix) {
		if (mExpired) {
			return;
		}
		super.draw(mVMatrix, mProjMatrix);
		super.fade(mFadeSpeed);
		if (super.fadeDone(mMaxFade)) {
			mExpired = true;
		}
	}
	
	public boolean isExpired() {
		return mExpired;
	}
	
	public void setExpired() {
		mExpired = true;
	}

}
