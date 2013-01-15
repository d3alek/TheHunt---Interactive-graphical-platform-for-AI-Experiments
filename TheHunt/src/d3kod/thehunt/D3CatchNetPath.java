package d3kod.thehunt;

import android.opengl.GLES20;
import android.opengl.Matrix;
import d3kod.d3gles20.D3GLES20;
import d3kod.d3gles20.D3Maths;
import d3kod.d3gles20.TextureManager;
import d3kod.d3gles20.Utilities;
import d3kod.d3gles20.programs.Program;
import d3kod.d3gles20.shapes.D3Path;

public class D3CatchNetPath extends D3Path {
	
	private static final String TAG = "D3CatchNet";
	
	private static float[] beingBuiltColor = {
		0.6f, 0.6f, 0.6f, 1.0f
	};
	
	private static float[] isNotBuiltColor = {
		1.0f, 0.0f, 0.0f, 1.0f
	};
	
	private static final int CLOSED_SHAPE_VERTICES_NUM = 100;
	private static final float DISTANCE_FAR_ENOUGH = 0.01f;
	private static final float DISTANCE_FINISH_ENOUGH = 0.1f;

	private static int isBuiltType = GLES20.GL_LINE_LOOP;
	

	private static final float FADE_SPEED = 0.05f;

	private static final float MIN_LENGTH = 0.5f;

	private static final float MAX_RADIUS = 0.3f;
	
	private float mScale;
	
	private boolean mIsClosed;

	private float mCenterX;

	private float mRadius;

	private float mCenterY;

	private float[] mModelMatrix;

	private boolean mIsInvalid;

	public D3CatchNetPath() {
		super();
		setColor(beingBuiltColor.clone()); //TODO do we need clone?
		mIsClosed = mIsInvalid = false;
		mCenterX = -100; mCenterY = -100; mRadius = 0;
	}
	
	public void setInvalid() {
		mIsInvalid = true;
		setColor(isNotBuiltColor.clone());
	}
	
	public void setFinished() {
		mIsClosed = true;
		transformToClosedShape();
		super.setDrawType(isBuiltType);
	}
	
	boolean canFinishWith(float x, float y) {
		return (D3Maths.distance(
				mVertexData.get(0), mVertexData.get(1), x, y)
					< DISTANCE_FINISH_ENOUGH)
					&& getLength() >= MIN_LENGTH;
	}

	public boolean isFarEnoughFromLast(float x, float y) {
		int lastYIndex = mVertexData.size() - 2, lastXIndex = lastYIndex - 1;
		return (D3Maths.distance(
				mVertexData.get(lastXIndex), mVertexData.get(lastYIndex), x, y) 
					> DISTANCE_FAR_ENOUGH);
	}

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
			minRadius = D3Maths.distance(mCenterX, mCenterY, mVertexData.get(vertexInd), mVertexData.get(vertexInd+1));
			if (minRadius > mRadius) {
				mRadius = minRadius;
			}
		}
		if (mRadius > MAX_RADIUS) {
			mRadius = MAX_RADIUS;
		}
//		float[] center = {mCenterX, mCenterY, 0};
		super.setVertexBuffer(Utilities.newFloatBuffer(D3Maths.circleVerticesData(mRadius, CLOSED_SHAPE_VERTICES_NUM)));
		mVertexData.clear();
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
	
	@Override
	public void addVertex(float x, float y) {
		super.addVertex(x, y);
		if (canFinishWith(x, y)) {
			setFinished();
		}
	}
	
	@Override
	public void draw(float[] mVMatrix, float[] mProjMatrix) {
		if (mIsInvalid) {
			fade(FADE_SPEED);
		}
		
		super.draw(mVMatrix, mProjMatrix);
	}
}
