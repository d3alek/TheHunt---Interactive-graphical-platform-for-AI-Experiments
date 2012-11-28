package d3kod.thehunt.prey.planner.plans;

import java.util.ArrayList;
import java.util.Stack;

import android.util.Log;
import d3kod.thehunt.prey.Action;
import d3kod.thehunt.prey.memory.WorldModel;

public class Plan {
	private static final String TAG = "Plan";
	private static final int MAX_ACTIONS = 1000;
	ArrayList<Action> mActions;
//	ArrayList<Action> mParallelActions;
	Stack<ParallelAction> mParallelActions;
	int mCurrentAction;
	private int mLastAction;
	private float mTargetX;
	private float mTargetY;
	private float[] mTargetColor;
	private float mTargetSize;
	private int mCurrentActionTicks;
	private final static float[] targetColorDefault = {1.0f, 0.0f, 0.0f, 1.0f};
	private final static float targetSizeDefault = 0.005f;
	private static float[] modelMatrix;
	
	public Plan(float headX, float headY) {
		this(headX, headY, targetSizeDefault, targetColorDefault);
	}
	
	public Plan(float targetX, float targetY, float targetSize, float[] targetColor) {
		mActions = new ArrayList<Action>();
		mParallelActions = new Stack<ParallelAction>(); 
		mCurrentAction = 0;
		mLastAction = MAX_ACTIONS;
		mTargetX = targetX;
		mTargetY = targetY;
		mTargetSize = targetSize;
		mTargetColor = targetColor;
	}
	public float getTargetX() {
		return mTargetX;
	}
	public float getTargetY() {
		return mTargetY;
	}
	public void setTarget(float tX, float tY) {
		mTargetX = tX; mTargetY = tY;
	}
	public float getTargetSize() {
		return mTargetSize;
	}
	public float[] getTargetColor() {
		return mTargetColor;
	}
	public boolean isFinished() {
		return mCurrentAction == -1 || mActions.size() <= mCurrentAction || mCurrentAction >= mLastAction;
	}
	public Action nextAction() {
		if (mCurrentAction == -1 || mCurrentAction >= mActions.size()) {
			Log.v(TAG, "Next action is null!");
			return null;
		}
		Action nextAction = mActions.get(mCurrentAction++);
		mCurrentActionTicks = nextAction.getTicks();
//		tickCurrentAction();
		return nextAction;
	}
	public void addLastAction(Action action) {
		mActions.add(action);
	}
	public void addAction(int index, Action action) {
		mActions.add(index, action);
	}
	public void update(WorldModel mWorldModel) {
		
	}
	public void finish() {
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

	public boolean isEmpty() {
		return mActions.isEmpty();
	}
	public void addAfterNextAction(Action action) {
		mActions.add(mCurrentAction+1, action);
	}
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
	
	public void addParallelAction(Action action) {
		ParallelAction newAction = new ParallelAction(action);
		if (mParallelActions.contains(newAction)) {
			return;
		}
		mParallelActions.push(newAction);
	}
	//TODO: Use recursion to not wait for top to finish before starting second 
	public Action getParallelAction() {
		if (mParallelActions.empty()) {
			return Action.none;
		}
		ParallelAction top = mParallelActions.peek();
		if (!top.started()) {
			top.start();
			return top.getAction(); 
		}
		if (top.getTicks() > 1) {
			top.tickOnce();
		}
		else mParallelActions.pop();
		return Action.none;
	}
	
	public Stack<ParallelAction> getParallelActions() {
		return mParallelActions;
	}
	public void setParallelActions(Stack<ParallelAction> setTo) {
		mParallelActions = (Stack<ParallelAction>)setTo.clone();
	}
}
