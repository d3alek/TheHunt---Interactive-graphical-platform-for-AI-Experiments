package d3kod.thehunt;

import android.util.Log;
import d3kod.d3gles20.shapes.D3Circle;

public class D3CatchNet extends D3Circle {

	private static final float FADE_SPEED = 0.05f;
	private static float[] isBuiltColor = {
		0.0f, 0.0f, 0.0f, 1.0f
	};
	private static final int VERTICES_NUM = 100;

	private static final float mGrowSpeed = 0.05f;
	private static final String TAG = "D3CatchNet";
	private static final float START_RADIUS = 0.03f;

	public D3CatchNet(float x, float y, float radius) {
		super(radius, isBuiltColor, VERTICES_NUM);
		setPosition(x, y);
		setScale(START_RADIUS/radius);
	}


	public void grow() {
		super.grow(mGrowSpeed);
	}
	
	@Override
	public void draw(float[] mVMatrix, float[] mProjMatrix) {
		if (getScale() >= 1) {
//			Log.v(TAG, "Fading " + FADE_SPEED + " " + getColor()[3]);
			fade(FADE_SPEED);
		}
		super.draw(mVMatrix, mProjMatrix);
	}
}
