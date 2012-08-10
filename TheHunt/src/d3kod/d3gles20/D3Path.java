package d3kod.d3gles20;

import java.util.ArrayList;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

public class D3Path extends D3Shape {

	private static final String TAG = "D3Path";
	private static int beingBuiltType = GLES20.GL_LINE_STRIP;
	private static int isBuiltType = GLES20.GL_LINE_LOOP;
	private float mCenterX;
	private float mCenterY;
	private float mRadius;
	private boolean mIsClosed;
	private float[] mVertexData;
	private float[] mModelMatrix;
	private float mShrinkSpeed = 0.05f;
	private float mScale;
	
	public D3Path(float[] beingBuiltColor) {
		super(0, null, beingBuiltColor, beingBuiltType, true);
		mCenterX = -100; mCenterY = -100; mRadius = 0;
		mIsClosed = false;
	}

	public void makeVertexBuffer(ArrayList<Float> pVertexData) {
		if (pVertexData.isEmpty()) return;
		mVertexData = new float[pVertexData.size() + pVertexData.size()/2];
//		pVertexData.toArray(mVertexData);
		int ind = 0, vertexNum = pVertexData.size()/2;
		float sumX = 0, sumY = 0;
		for (Float f:pVertexData) {
			mVertexData[ind++] = f;
//			if (ind%3==1) sumX += f;
			if (ind%3==2) {
//				sumY += f;
				mVertexData[ind++] = 0;
			}
		}
		super.setVertexBuffer(D3GLES20.newFloatBuffer(mVertexData));
	}

	public void shrink() {
		if (!mIsClosed) return;
//		Matrix.mModelMatrix
		mScale = mScale*(1-mShrinkSpeed);
		Matrix.scaleM(mModelMatrix, 0, (1-mShrinkSpeed), (1-mShrinkSpeed), 0);
	}
	
	public void isFinished() {
		mIsClosed = true;
		transformToClosedShape();
		super.setDrawType(isBuiltType);
	}
	
	private void transformToClosedShape() {
		int verticesNum = getVerticesNum();
		float sumX = 0, sumY = 0;
		int vertexInd;
		for (int i = 0; i < verticesNum; ++i) {
			vertexInd = i*D3GLES20.COORDS_PER_VERTEX;
			sumX += mVertexData[vertexInd];
			sumY += mVertexData[vertexInd+1];
		}
		mCenterX = sumX/verticesNum;
		mCenterY = sumY/verticesNum;
		
		float minRadius;
		mRadius = 100;
		
		for (int i = 0; i < verticesNum; ++i) {
			vertexInd = i*D3GLES20.COORDS_PER_VERTEX;
			mVertexData[vertexInd] = mVertexData[vertexInd] - mCenterX;
			mVertexData[vertexInd+1] = mVertexData[vertexInd+1] - mCenterY;
			minRadius = D3GLES20.distance(0, 0, mVertexData[vertexInd], mVertexData[vertexInd+1]);
			if (minRadius < mRadius) {
				mRadius = minRadius;
			}
		}
		
		super.setVertexBuffer(D3GLES20.newFloatBuffer(mVertexData));
		
		mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, mCenterX, mCenterY, 0);
		setModelMatrix(mModelMatrix);
		
		mScale = 1f;
		
//		mRadius = mRadius/2;
		
		//TODO: calculate radius better
//		mRadius = D3GLES20.distance(0, 0, mVertexData[0], mVertexData[1]);
		
	}

	public void isBeingBuilt() {
		super.setDrawType(beingBuiltType);
	}

	@Override
	public float getCenterX() {
//		if (mIsClosed) super.getCenterX();
		return mCenterX;
	}

	@Override
	public float getCenterY() {
//		if (mIsClosed) super.getCenterY();
		return mCenterY;
	}
	
	@Override
	public float getRadius() {
		return mRadius*mScale;
	}
	
	@Override
	public void draw(float[] mVMatrix, float[] mProjMatrix) {
		super.draw(mVMatrix, mProjMatrix);
	}
}
