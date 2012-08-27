package d3kod.d3gles20.shapes;


abstract public class D3FadingShape extends D3Shape {

//    private static final float MIN_ALPHA = 0.6f;
	private boolean mExpired;
	private float mFadeSpeed;
	private float mMaxFade;

	protected D3FadingShape(float[] colorData, int drType,
			int programHandle, float fadeSpeed, float maxFade) {
		super(colorData, drType, programHandle);
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

}
