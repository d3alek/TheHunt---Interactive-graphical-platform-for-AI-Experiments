package d3kod.d3gles20;

import java.util.ArrayList;

import android.opengl.GLES20;
import android.util.Log;

public class D3Path extends D3Shape {

	private static final String TAG = "D3Path";
	private static int beingBuiltType = GLES20.GL_LINE_STRIP;
	private static int isBuiltType = GLES20.GL_LINE_LOOP;
	private float mCenterX;
	private float mCenterY;
	private float mRadius;
	
	public D3Path(float[] beingBuiltColor) {
		super(0, null, beingBuiltColor, beingBuiltType, true);
	}

	public void makeVertexBuffer(ArrayList<Float> pVertexData) {
		if (pVertexData.isEmpty()) return;
		float[] mVertexData = new float[pVertexData.size() + pVertexData.size()/2];
//		pVertexData.toArray(mVertexData);
		int ind = 0, vertexNum = pVertexData.size()/2;
		float sumX = 0, sumY = 0;
		for (Float f:pVertexData) {
			mVertexData[ind++] = f;
			if (ind%3==1) sumX += f;
			else if (ind%3==2) {
				sumY += f;
				mVertexData[ind++] = 0;
			}
		}
		//TODO: calculate these only if path is closed;
		mCenterX = sumX/vertexNum;
		mCenterY = sumY/vertexNum;
		//TODO: calculate radius better
		mRadius = D3GLES20.distance(mCenterX, mCenterY, mVertexData[0], mVertexData[1]);
//		Log.v(TAG, "mCenter " + mCenterX + " " + mCenterY + " mRadius " + mRadius);
		super.setVertexBuffer(D3GLES20.newFloatBuffer(mVertexData));
	}

	public void isFinished() {
		super.setDrawType(isBuiltType);
	}
	
	public void isBeingBuilt() {
		super.setDrawType(beingBuiltType);
	}

	@Override
	public float getCenterX() {
		return mCenterX;
	}

	@Override
	public float getCenterY() {
		return mCenterY;
	}
	
	@Override
	public float getRadius() {
		return mRadius;
	}
	
}
