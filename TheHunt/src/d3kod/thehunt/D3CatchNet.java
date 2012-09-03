package d3kod.thehunt;

import android.opengl.Matrix;
import android.util.Log;
import d3kod.d3gles20.programs.Program;
import d3kod.d3gles20.shapes.D3Circle;

public class D3CatchNet extends D3Circle {

	private static final float FADE_SPEED = 0.05f;
	private static float[] isBuiltColor = {
		0.0f, 0.0f, 0.0f, 1.0f
	};
	private static final int VERTICES_NUM = 100;

	private static final float mGrowSpeed = 0.05f;
	private static final String TAG = "D3CatchNet";
	private static final float START_RADIOUS = 0.03f;
	private float mScale;

	public D3CatchNet(float x, float y, float radius, Program program) {
		super(radius, isBuiltColor, VERTICES_NUM, program);
		setPosition(x, y);
		mScale = START_RADIOUS/radius;
		Matrix.scaleM(getMMatrix(), 0, mScale, mScale, 0);
	}

	public void grow() {
		mScale = mScale*(1+mGrowSpeed);
		Matrix.scaleM(getMMatrix(), 0, (1+mGrowSpeed), (1+mGrowSpeed), 0);
//		setModelMatrix(mModelMatrix);
	}
	
	public float getScale() {
//		Log.v(TAG, "Scale is " + mScale);
		return mScale;
	}
	@Override
	public void draw(float[] mVMatrix, float[] mProjMatrix) {
		if (mScale >= 1) {
			fade(FADE_SPEED);
		}
		super.draw(mVMatrix, mProjMatrix);
	}
}
