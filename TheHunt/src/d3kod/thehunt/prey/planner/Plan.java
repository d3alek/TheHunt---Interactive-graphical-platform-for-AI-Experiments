package d3kod.thehunt.prey.planner;

import java.util.ArrayList;

import android.opengl.Matrix;
import android.util.Log;
import d3kod.d3gles20.D3GLES20;
import d3kod.thehunt.prey.Action;
import d3kod.thehunt.prey.memory.WorldModel;

public class Plan {
	private static final String TAG = "Plan";
	private static final int MAX_ACTIONS = 1000;
	ArrayList<Action> mActions;
	int mCurrentAction;
	private int mLastAction;
	private int target;
	private final static float[] targetColorDefault = {1.0f, 0.0f, 0.0f};
	private final static float targetSizeDefault = 0.005f;
	private static float[] modelMatrix;
	
	public Plan(float headX, float headY) {
		this(headX, headY, targetSizeDefault, targetColorDefault);
	}
	
	public Plan(float targetX, float targetY, float targetSize, float[] targetColor) {
		mActions = new ArrayList<Action>();
		mCurrentAction = 0;
		mLastAction = MAX_ACTIONS;
		if (Planner.SHOW_TARGET) {
			target = D3GLES20.newDefaultCircle(targetSize, targetColor, 10);
			modelMatrix = new float[16];
			Matrix.setIdentityM(modelMatrix , 0);
			Matrix.translateM(modelMatrix, 0, targetX, targetY, 0);
		}
	}
	public boolean isFinished() {
//		Log.v(TAG, mActions.size() + " " + mCurrentAction + " " + mLastAction);
		return mCurrentAction == -1 || mActions.size() <= mCurrentAction || mCurrentAction >= mLastAction;
	}
	public Action nextAction() {
		if (mCurrentAction == -1 || mCurrentAction >= mActions.size()) {
			Log.v(TAG, "Next action is null!");
			return null;
		}
		return mActions.get(mCurrentAction++);
	}
	public void addLastAction(Action action) {
		mActions.add(action); 
//		Log.v(TAG, "Added action " + action);
//		mCurrentAction++;
//		return mCurrentAction;
	}
	public void addAction(int index, Action action) {
		mActions.add(index, action);
//		Log.v(TAG, "Added action " + action + " to index " + index);
	}
	public void update(WorldModel mWorldModel) {
		
	}
	public void finish() {
		//mCurrentAction = mActions.size();
		mActions.subList(mCurrentAction, mActions.size()).clear();
	}
	public Action getPrevAction() {
		return mActions.get(mCurrentAction - 1);
	}
	public void finishAfterNext() {
		mLastAction = mCurrentAction + 1;
	}
	public void addNextAction(Action action) {
		mActions.add(mCurrentAction, action);
	}
	public void clearActions() {
		mActions.clear();
		mCurrentAction = 0;
	}

	public int getTarget() {
		return target;
	}

	public float[] getTargetMMatrix() {
		return modelMatrix;
	}

	public void done() {
		D3GLES20.removeShape(target);
	}

	public boolean isEmpty() {
		return mActions.isEmpty();
	}
	
	
}
