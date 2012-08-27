package d3kod.d3gles20.shapes;

import android.opengl.Matrix;
import d3kod.d3gles20.Program;

public class D3TempCircle extends D3Circle {
	
	private static float[] colorData = {
		0.0f, 0.5f, 0.0f, 1.0f };
	private static int verticesNum = 100;
	private int mTicks;
	private boolean mExpired;

	public D3TempCircle(float x, float y, float r, int ticks, Program program) {
		super(r, colorData, verticesNum , program);
		float[] modelMatrix = new float[16];
		Matrix.setIdentityM(modelMatrix, 0);
		Matrix.translateM(modelMatrix, 0, x, y, 0);
		setModelMatrix(modelMatrix);
		mTicks = ticks;
		mExpired = false;
	}
	
	@Override
	public void draw(float[] mVMatrix, float[] mProjMatrix) {
		if (mTicks <= 0) {
			mExpired = true;
			return;
		}
		mTicks--;
		super.draw(mVMatrix, mProjMatrix);
	}
	
	public boolean isExpired() {
		return mExpired;
	}
	
}
