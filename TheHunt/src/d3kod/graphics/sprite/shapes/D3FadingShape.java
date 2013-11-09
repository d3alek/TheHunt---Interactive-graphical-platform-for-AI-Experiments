package d3kod.graphics.sprite.shapes;

import d3kod.graphics.shader.programs.Program;


abstract public class D3FadingShape extends D3Shape {

	private boolean mExpired;
	private float mFadeSpeed;
	private float mMaxFade;

	protected D3FadingShape(float[] colorData, int drType,
			Program program, float fadeSpeed, float maxFade) {
		this(fadeSpeed, maxFade);
		setColor(colorData);
		setDrawType(drType);
		setProgram(program);
	}

	public D3FadingShape(float fadeSpeed, float maxFade) {
		mExpired = false;
		mFadeSpeed = fadeSpeed;
		mMaxFade = maxFade;
	}

	public void setFadeSpeed(float fadeSpeed) {
		mFadeSpeed = fadeSpeed;
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
