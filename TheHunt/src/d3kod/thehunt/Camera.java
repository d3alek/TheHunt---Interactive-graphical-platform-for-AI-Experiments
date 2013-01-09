package d3kod.thehunt;

import android.graphics.PointF;
import android.opengl.Matrix;
import android.util.Log;
import d3kod.d3gles20.D3GLES20;
import d3kod.d3gles20.D3Maths;
import d3kod.thehunt.environment.Dir;
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
	private PreyPointer mPreyPointer;
	private boolean mPointerShown;

	public Camera(float screenToWorldWidth, float screenToWorldHeight, float widthToHeightRatio, D3GLES20 d3gles20) {
		mCenterX = mCenterY = 0;
		mVMatrix = new float[16];
		mWidth = 2*screenToWorldWidth;//*mScreenWidthPx/(float)mScreenHeightPx;
	    mHeight = 2*screenToWorldHeight;
	    mWidthToHeightRatio = widthToHeightRatio;
	    
	    mPreyPointer = new PreyPointer(d3gles20.getShaderManager());
	    d3gles20.putShape(mPreyPointer);
//	    mPointerShown = true;
	    hidePreyPointer();
	    
	    calcViewMatrix();
	}

	public void move(float dx, float dy) {
//		Log.v(TAG, "Moving " + dx + " " + dy + " "
//				+ EnvironmentData.mScreenWidth + " "
//				+ mWidth + " "
//				+ mWidth * mWidthToHeightRatio + " " 
//				+ EnvironmentData.mScreenHeight + " "
//				+ mHeight + " " + mCenterX + " " +
//				mCenterY);
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

	public boolean contains(PointF point) {
		if (point == null) {
			//Log.w(TAG, "Point is null in contains check!");
			return true;
		}
		return D3Maths.rectContains(mCenterX, mCenterY, mWidth * mWidthToHeightRatio, mHeight, point.x, point.y);
	}

	public void showPreyPointer() {
		mPointerShown = true;
		mPreyPointer.noFade();
	}

	public void hidePreyPointer() {
		mPointerShown = false;
		mPreyPointer.fade(1);
	}
	
	public void setPreyPointerPosition(PointF preyPosition) {
		if (mPointerShown == false || preyPosition == null) return;
		float mLeftX = mCenterX - mWidth * mWidthToHeightRatio/2;
		float mRightX = mLeftX + mWidth * mWidthToHeightRatio;
		float mBottomY = mCenterY - mHeight/2;
		float mTopY = mBottomY + mHeight;
		float mPreyPointerX, mPreyPointerY;
		Dir facingDir = Dir.UNDEFINED;
		if (preyPosition.x < mLeftX) {
			 mPreyPointerX = mLeftX;
			 facingDir = Dir.W;
		}
		else if (preyPosition.x > mRightX) {
			mPreyPointerX = mRightX;
			facingDir = Dir.E;
		}
		else {
			mPreyPointerX = preyPosition.x;
		}
		if (preyPosition.y < mBottomY) {
			mPreyPointerY = mBottomY;
			facingDir = Dir.S;
		}
		else if (preyPosition.y > mTopY) {
			mPreyPointerY = mTopY;
			facingDir = Dir.N;
		}
		else {
			mPreyPointerY = preyPosition.y;
		}

		mPreyPointer.setPosition(mPreyPointerX, mPreyPointerY, facingDir.getAngle());
	}
}
