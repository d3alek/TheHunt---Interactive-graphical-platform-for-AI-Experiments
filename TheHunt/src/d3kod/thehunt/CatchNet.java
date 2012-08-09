package d3kod.thehunt;

import java.util.ArrayList;

import android.util.Log;
import d3kod.d3gles20.D3GLES20;
import d3kod.d3gles20.D3Path;

public class CatchNet {
	
	D3Path mGraphic;

	private int mGraphicIndex;
	
	private static float[] beingBuiltColor = {
		0.4f, 0.4f, 0.4f, 0.0f
	};

	private static float[] isBuiltColor = {
		0.0f, 0.0f, 0.0f, 0.0f
	};
	
	private static float[] isNotBuiltColor = {
		1.0f, 0.0f, 0.0f, 0.0f
	};

	public void initGraphics() {
		mGraphic = new D3Path(beingBuiltColor);
		mGraphicIndex = D3GLES20.putShape(mGraphic);
	}
	
	private static final float DISTANCE_FAR_ENOUGH = 0.01f;
	private static final float DISTANCE_FINISH_ENOUGH = 0.1f;

	private static final String TAG = "CatchNet";
	private boolean beingBuilt;
	private ArrayList<Float> mVertexData = new ArrayList<Float>();
	private boolean isBuilt;

	private boolean isShown;

	public void start(float x, float y) {
		Log.v(TAG, "Starting");
		isShown = true;
		beingBuilt = true;
		isBuilt = false;
		mGraphic.setColor(beingBuiltColor);
		mGraphic.isBeingBuilt();
		mVertexData.clear(); 
		mVertexData.add(x); mVertexData.add(y);
	}

	public void next(float x, float y) {
		Log.v(TAG, "Adding next " + x + " " + y);
		mVertexData.add(x); mVertexData.add(y);		
	}

	public void finish(float x, float y) {
		Log.v(TAG, "Finishing " + x + " " + y);
		beingBuilt = false;
		if (isCloseEnoughToStart(x, y)) {
			isBuilt = true;
			mGraphic.setColor(isBuiltColor);
			mGraphic.isFinished();
		}
		else {
			mGraphic.setColor(isNotBuiltColor);
		}
	}
	
	private boolean isCloseEnoughToStart(float x, float y) {
		return (D3GLES20.distance(
				mVertexData.get(0), mVertexData.get(1), x, y)
					< DISTANCE_FINISH_ENOUGH);
	}

	public boolean isFarEnoughFromLast(float x, float y) {
		int lastYIndex = mVertexData.size() - 1, lastXIndex = lastYIndex - 1;
		return (D3GLES20.distance(
				mVertexData.get(lastXIndex), mVertexData.get(lastYIndex), x, y) 
					> DISTANCE_FAR_ENOUGH);
	}
	
	public void draw(float[] mVMatrix, float[] mProjMatrix) {
		mGraphic.makeVertexBuffer(mVertexData);
		mGraphic.draw(mVMatrix, mProjMatrix);
	}

	public boolean show() {
		return isShown;
	}

	public int getGraphicIndex() {
		return mGraphicIndex;
	}

}
