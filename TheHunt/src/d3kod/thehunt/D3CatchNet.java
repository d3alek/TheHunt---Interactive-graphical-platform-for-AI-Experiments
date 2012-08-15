package d3kod.thehunt;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import d3kod.d3gles20.D3GLES20;
import d3kod.d3gles20.D3Path;

public class D3CatchNet extends D3Path {
	
	private static final String TAG = "D3CatchNet";
	
//	private static int closedShapeVerticesNum = 100;
	
	private static float[] beingBuiltColor = {
		0.6f, 0.6f, 0.6f, 0.0f
	};

	private static float[] isBuiltColor = {
		0.0f, 0.0f, 0.0f, 0.0f
	};
	
	private static float[] isNotBuiltColor = {
		1.0f, 0.0f, 0.0f, 0.0f
	};
	
	private static final float DISTANCE_FAR_ENOUGH = 0.01f;
	private static final float DISTANCE_FINISH_ENOUGH = 0.2f;
	
	private static final float MIN_RADIUS = 0.15f;

	private static int isBuiltType = GLES20.GL_LINE_LOOP;
	
	private static final float mShrinkSpeed = 0.05f;

	private static final int CLOSED_SHAPE_VERTICES_NUM = 100;
	
	private float mScale;
	
	private boolean mIsClosed;

	private float mCenterX;

	private float mRadius;

	private float mCenterY;

	private float[] mModelMatrix;

	private boolean mIsInvalid;
	
	public D3CatchNet() {
		super(beingBuiltColor);
		mIsClosed = mIsInvalid = false;
		mCenterX = -100; mCenterY = -100; mRadius = 0;
	}

	public void shrink() {
		if (!mIsClosed) return;
		mScale = mScale*(1-mShrinkSpeed);
		Matrix.scaleM(mModelMatrix, 0, (1-mShrinkSpeed), (1-mShrinkSpeed), 0);
	}
	
	public void setInvalid() {
		mIsInvalid = true;
		setColor(isNotBuiltColor);
	}
	
	public void setFinished() {
		mIsClosed = true;
		transformToClosedShape();
		setColor(isBuiltColor);
		super.setDrawType(isBuiltType);
	}
	
	boolean isCloseEnoughToStart(float x, float y) {
		return (D3GLES20.distance(
				mVertexData.get(0), mVertexData.get(1), x, y)
					< DISTANCE_FINISH_ENOUGH);
	}

	public boolean isFarEnoughFromLast(float x, float y) {
		int lastYIndex = mVertexData.size() - 2, lastXIndex = lastYIndex - 1;
		return (D3GLES20.distance(
				mVertexData.get(lastXIndex), mVertexData.get(lastYIndex), x, y) 
					> DISTANCE_FAR_ENOUGH);
	}
	
//	private void transformToClosedShape() {
////		super.makeVertexBuffer();
//		int verticesNum = mVertexData.size()/D3GLES20.COORDS_PER_VERTEX;
//		float sumX = 0, sumY = 0;
//		int vertexInd;
//		for (int i = 0; i < verticesNum; ++i) {
//			vertexInd = i*D3GLES20.COORDS_PER_VERTEX;
//			sumX += mVertexData.get(vertexInd);
//			sumY += mVertexData.get(vertexInd+1);
//		}
//		mCenterX = sumX/verticesNum;
//		mCenterY = sumY/verticesNum;
//		
//		float minRadius;
//		mRadius = 100;
//		
//		for (int i = 0; i < verticesNum; ++i) {
//			vertexInd = i*D3GLES20.COORDS_PER_VERTEX;
//			mVertexData.set(vertexInd, mVertexData.get(vertexInd) - mCenterX);
//			mVertexData.set(vertexInd+1, mVertexData.get(vertexInd+1) - mCenterY);
//			minRadius = D3GLES20.distance(0, 0, mVertexData.get(vertexInd), mVertexData.get(vertexInd+1));
//			if (minRadius < mRadius) {
//				mRadius = minRadius;
//			}
//		}
//		
//		super.makeVertexBuffer();
//		
//		mModelMatrix = new float[16];
//		Matrix.setIdentityM(mModelMatrix, 0);
//		Matrix.translateM(mModelMatrix, 0, mCenterX, mCenterY, 0);
//		setModelMatrix(mModelMatrix);
//		
//		mScale = 1f;
//	}

	private void transformToClosedShape() {
		int verticesNum = mVertexData.size()/D3GLES20.COORDS_PER_VERTEX;
		float sumX = 0, sumY = 0;
		int vertexInd;
		for (int i = 0; i < verticesNum; ++i) {
			vertexInd = i*D3GLES20.COORDS_PER_VERTEX;
			sumX += mVertexData.get(vertexInd);
			sumY += mVertexData.get(vertexInd+1);
		}
		mCenterX = sumX/verticesNum;
		mCenterY = sumY/verticesNum;
		
		float minRadius;
		mRadius = 0;
		
		for (int i = 0; i < verticesNum; ++i) {
			vertexInd = i*D3GLES20.COORDS_PER_VERTEX;
			minRadius = D3GLES20.distance(mCenterX, mCenterY, mVertexData.get(vertexInd), mVertexData.get(vertexInd+1));
			if (minRadius > mRadius) {
				mRadius = minRadius;
			}
		}
		
		float[] center = {mCenterX, mCenterY, 0};
		super.setVertexBuffer(D3GLES20.newFloatBuffer(D3GLES20.circleVerticesData(center, mRadius, CLOSED_SHAPE_VERTICES_NUM)));
		
		mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, mCenterX, mCenterY, 0);
		setModelMatrix(mModelMatrix);
		
		mScale = 1f;
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
		return mRadius*mScale;
	}
	
	public boolean isInvalid() {
		return mIsInvalid;
	}
	
	public boolean isFinished() {
		return mIsClosed;
	}
	
	public void draw(float[] mVMatrix, float[] mProjMatrix) {
		if (mIsClosed) {
			if (getRadius() < MIN_RADIUS) {
				fade();
			}
			else shrink();
		}
		
		else if (mIsInvalid) {
			fade();
		}
		
		super.draw(mVMatrix, mProjMatrix);
	}

}
