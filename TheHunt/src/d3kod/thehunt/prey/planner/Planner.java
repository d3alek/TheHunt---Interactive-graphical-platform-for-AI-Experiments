package d3kod.thehunt.prey.planner;

import java.util.Random;

import d3kod.d3gles20.D3GLES20;
import d3kod.thehunt.prey.Action;
import d3kod.thehunt.prey.memory.WorldModel;

import android.opengl.Matrix;
import android.util.Log;

public class Planner {
	private static final String TAG = "Planner";
	public static final boolean SHOW_TARGET = true;
	private static final float TOLERANCE = 0.001f; 
	private PlanState mState;
	private Action[] allActions;
	private int numActions; 
	private Random mRandom;
	private Plan mPlan;
	private float distanceFromFoodPrev;
	private Action actionPrev;
//	private boolean targetNotShown;
//	private float[] targetColor = {1.0f, 0.0f, 0.0f};
//	private float targetSize = 0.005f;
//	private int target;
//	private static float[] modelMatrix = new float[16];
	private boolean firstMistake;
	private boolean stop;

	public Planner() {
		mPlan = new NoPlan(0, 0);
		mState = PlanState.DONOTHING;
		allActions = Action.values();
		numActions = allActions.length;
		mRandom = new Random();
		distanceFromFoodPrev = 50;
		actionPrev = Action.flopLeft;
//		if (SHOW_TARGET) target = D3GLES20.newDefaultCircle(targetSize, targetColor, 10);
		stop = false;
	}

	public Action nextAction(WorldModel mWorldModel) {
		mPlan.update(mWorldModel);
		if (!mPlan.isFinished()) {
//			Log.v(TAG, "Plan not finished yet, go on doing it");
			return mPlan.nextAction();
		}
		else mPlan.done();
		mState = checkForSomethingInteresting(mWorldModel);
		switch(mState) {
		case EXPLORE: return chooseRandomAction();
		case SCAVAGE: mPlan = makeScavagePlan(mWorldModel); break;
		case HIDE: 
			if (mWorldModel.getLightLevel() > 0) mPlan = makeHidePlan(mWorldModel); 
			else mPlan = new NoPlan(mWorldModel.getHeadX(), mWorldModel.getHeadY());
			break;
		case DONOTHING: mPlan = new NoPlan(mWorldModel.getHeadX(), mWorldModel.getHeadY()); break;
		}
		mPlan.update(mWorldModel);
		return mPlan.nextAction();
	}

	private PlanState checkForSomethingInteresting(WorldModel mWorldModel) {
		if (mWorldModel.knowFoodLocation()) {
			Log.v(TAG, "Found an interesting food location!");
			return PlanState.SCAVAGE;
		}
		if (mWorldModel.knowAlgaeLocation()) {
			Log.v(TAG, "Found an interesting algae location!");
			return PlanState.HIDE;
		}
		return PlanState.DONOTHING;
	}

	private Plan makeScavagePlan(WorldModel mWorldModel) {
		Log.v(TAG, "Making scavage plan");
		Plan scvgPlan = new GoToPlan(
				mWorldModel.getHeadX(), mWorldModel.getHeadY(), 
				mWorldModel.getBodyX(), mWorldModel.getBodyY(), 
				mWorldModel.getFoodX(), mWorldModel.getFoodY());
		scvgPlan.addLastAction(Action.eat);
		return scvgPlan;
	}

	private Plan makeHidePlan(WorldModel mWorldModel) {
		Log.v(TAG, "Making hide plan");
		Plan hidePlan = new GoToPlan(
				mWorldModel.getHeadX(), mWorldModel.getHeadY(), 
				mWorldModel.getBodyX(), mWorldModel.getBodyY(), 
				mWorldModel.getAlgaeX(), mWorldModel.getAlgaeY());
		return hidePlan;
	}
	
	private Action chooseRandomAction() {
		int actionInd = Math.round((numActions-1) * mRandom.nextFloat());
		return allActions[actionInd];
	}
	
	public void draw(float[] mVMatrix, float[] mProjMatrix) {
		D3GLES20.draw(mPlan.getTarget(), mPlan.getTargetMMatrix(), mVMatrix, mProjMatrix);
	}
	
}
