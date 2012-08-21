package d3kod.thehunt;

import android.opengl.Matrix;
import android.util.Log;
import d3kod.d3gles20.D3Maths;
import d3kod.thehunt.environment.EnvironmentData;

public class Camera {

	
	private static final String TAG = "Camera";
	private float mCenterX;
	private float mCenterY;
	private float mViewLeft;
	private float mViewBottom;
	private float[] mVMatrix;
	private boolean recalcViewMatrix;
	private float mWidth;
	private float mHeight;
	private float mWidthToHeightRatio;

	public Camera(float screenToWorldWidth, float screenToWorldHeight, float widthToHeightRatio) {
		mCenterX = mCenterY = 0;
		mVMatrix = new float[16];
		mWidth = 2*screenToWorldWidth;//*mScreenWidthPx/(float)mScreenHeightPx;
	    mHeight = 2*screenToWorldHeight;
	    mWidthToHeightRatio = widthToHeightRatio;
	    calcViewMatrix();
	}

	public void move(float dx, float dy) {
		Log.v(TAG, "Moving " + dx + " " + dy + " "
				+ EnvironmentData.mScreenWidth + " "
				+ mWidth + " "
				+ mWidth * mWidthToHeightRatio + " " 
				+ EnvironmentData.mScreenHeight + " "
				+ mHeight + " " + mCenterX + " " +
				mCenterY);
		recalcViewMatrix = false;
		if (D3Maths.rectContains(0, 0, 
				EnvironmentData.mScreenWidth - mWidth * mWidthToHeightRatio, 
				EnvironmentData.mScreenHeight - mHeight, 
				mCenterX + dx, mCenterY)) {
			mCenterX += dx;
			recalcViewMatrix = true;
		}
		if (D3Maths.rectContains(0, 0, 
				EnvironmentData.mScreenWidth - mWidth * mWidthToHeightRatio, 
				EnvironmentData.mScreenHeight - mHeight, 
				mCenterX, mCenterY+dy)) {
//			Log.v(TAG, "Moving camera with " + dx + " " + dy);
			mCenterY += dy;
			recalcViewMatrix = true;
		}
		if (recalcViewMatrix) {
			calcViewMatrix();
		}
	}
	
	private void calcViewMatrix() {
		mViewLeft = -mWidth/2 + mCenterX;
		mViewBottom = -mHeight/2 + mCenterY;
		Matrix.orthoM(mVMatrix, 0, 
				mViewLeft,
				mViewLeft+mWidth, 
				mViewBottom, 
				mViewBottom+mHeight, 0.1f, 100f);
	}

	public float[] toViewMatrix() {
		return mVMatrix;
	}
}
