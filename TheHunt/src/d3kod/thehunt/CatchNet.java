package d3kod.thehunt;

import java.util.ArrayList;

import android.util.Log;
import d3kod.d3gles20.D3GLES20;
import d3kod.d3gles20.D3Path;
import d3kod.thehunt.environment.Environment;
import d3kod.thehunt.prey.Prey;

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
	
	private static final float DISTANCE_FAR_ENOUGH = 0.01f;
	private static final float DISTANCE_FINISH_ENOUGH = 0.1f;

	private static final String TAG = "CatchNet";

	private static final float MIN_RADIUS = 0.1f;
	
	private boolean beingBuilt;
	private ArrayList<Float> mVertexData = new ArrayList<Float>();
	private boolean isBuilt;

	private boolean isShown;

	private Prey mCaughtPrey;

	private Environment mEnv;

	private boolean isInvalid;

	public CatchNet(Environment env) {
		mEnv = env;
	}
	
	public void start(float x, float y) {
		if (isShown) {
//			Log.v(TAG, "Still showing previous net");
			return;
		}
		if (mEnv.netObstacle(x, y)) {
			return;
		}
//		Log.v(TAG, "Starting");
		isShown = true;
		beingBuilt = true;
		isBuilt = false;
		isInvalid = false;
		
		mGraphic = new D3Path(beingBuiltColor);
		mGraphicIndex = D3GLES20.putShape(mGraphic);
		mCaughtPrey = null;
		
		mGraphic.setColor(beingBuiltColor);
		mGraphic.isBeingBuilt();
		mVertexData.clear(); 
		mVertexData.add(x); mVertexData.add(y);
	}

	public void next(float x, float y) {
		if (!beingBuilt || isInvalid) return;
		if (!isFarEnoughFromLast(x, y)) return;
		if (mEnv.netObstacle(x, y)) {
			invalidNet();
		}
		mVertexData.add(x); mVertexData.add(y);		
	}

	private void invalidNet() {
		mGraphic.setColor(isNotBuiltColor);
		isInvalid = true;
		beingBuilt = false;
	}

	public void finish(float x, float y) {
		if (!beingBuilt || isInvalid) return;
//		Log.v(TAG, "Finishing " + x + " " + y);
		beingBuilt = false;
		if (isCloseEnoughToStart(x, y)) {
			isBuilt = true;
			mGraphic.setColor(isBuiltColor);
			mGraphic.isFinished();
		}
		else {
			//mGraphic.setColor(isNotBuiltColor);
			invalidNet();
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
		//TODO: move this shit to a new update method
		if (isBuilt) {
			if (mGraphic.fadeDone()) {
//				Log.v(TAG, "Fade done!");
				isBuilt = false;
				isShown = false;
				mGraphic = null;
				D3GLES20.removeShape(mGraphicIndex);
				
				if (mCaughtPrey != null) {
					Log.v(TAG, "Prey is in net!!! YAY");
					Log.v(TAG, "Let it go now...");
					//mCaughtPrey.setCaught(false);
					mCaughtPrey.release();
				}
				
				return;
				//TODO clear vertex data?
			}
			if (mGraphic.getRadius() < MIN_RADIUS) {
				mGraphic.fade();
			}
			else mGraphic.shrink();
		}
		else if (isInvalid) {
			if (mGraphic.fadeDone()) {
				isShown = false;
				mGraphic = null;
				D3GLES20.removeShape(mGraphicIndex);
				return;
			}
			mGraphic.fade();
		}
		else mGraphic.makeVertexBuffer(mVertexData);
		
		mGraphic.draw(mVMatrix, mProjMatrix);
	}

	public boolean show() {
		return isShown;
	}

	public int getGraphicIndex() {
		return mGraphicIndex;
	}

	public boolean isBuilt() {
		return isBuilt;
	}

	public void caughtPrey(Prey mPrey) {
		mCaughtPrey = mPrey;
	}

}
