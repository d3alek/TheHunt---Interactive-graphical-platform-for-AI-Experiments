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
//	private int target;
	private float mTargetX;
	private float mTargetY;
	private float[] mTargetColor;
	private float mTargetSize;
	private int mCurrentActionTicks;
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
		mTargetX = targetX;
		mTargetY = targetY;
		mTargetSize = targetSize;
		mTargetColor = targetColor.clone();
		if (Planner.SHOW_TARGET) {
//			target = D3GLES20.newDefaultCircle(targetSize, targetColor, 10);
			modelMatrix = new float[16];
			Matrix.setIdentityM(modelMatrix , 0);
			Matrix.translateM(modelMatrix, 0, targetX, targetY, 0);
		}
	}
	public float getTargetX() {
		return mTargetX;
	}
	public float getTargetY() {
		return mTargetY;
	}
	public float getTargetSize() {
		return mTargetSize;
	}
	public float[] getTargetColor() {
		return mTargetColor;
	}
	public float[] getTargetMMatrix() {
		return modelMatrix;
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
		Action nextAction = mActions.get(mCurrentAction++);
		mCurrentActionTicks = nextAction.getTicks()-1; // we tick once now
		return nextAction;
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
	
	public Action getNextAction() {
		return mActions.get(mCurrentAction);
	}
	
	public void changeNextAction(Action action) {
		mActions.remove(mCurrentAction);
		mActions.add(mCurrentAction, action);
	}
	
	public void clearActions() {
		mActions.clear();
		mCurrentAction = 0;
	}

//	public int getTarget() {
//		return target;
//	}
//
//	public void done() {
//		D3GLES20.removeShape(target);
//	}

	public boolean isEmpty() {
		return mActions.isEmpty();
	}
	public void addAfterNextAction(Action action) {
		mActions.add(mCurrentAction+1, action);
	}
//	public void changeNextAction(Action action) {
//		mActions.
//	}
	public void logActions() {
		String planStr = "Actions log ";
		for (Action action: mActions) {
			planStr += action + " ";
		}
		Log.v(TAG, planStr);
	}

	public boolean finishedCurrentAction() {
		return (mCurrentActionTicks <= 0);
	}

	public void tickCurrentAction() {
		mCurrentActionTicks--;
	}
}
