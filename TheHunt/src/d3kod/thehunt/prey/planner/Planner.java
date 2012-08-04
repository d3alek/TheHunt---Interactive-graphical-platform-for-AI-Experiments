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
	public static PlanState mState;
	private Action[] allActions;
	private int numActions; 
	private Random mRandom;
	private Plan mPlan;
	private int mPlanTarget;

	public Planner() {
		mPlan = new NoPlan(0, 0);
		mState = PlanState.DONOTHING;
		allActions = Action.values();
		numActions = allActions.length;
		mRandom = new Random();
	}

	public Action nextAction(WorldModel mWorldModel) {
		mPlan.update(mWorldModel);
		if (!mPlan.isFinished()) {
			return mPlan.nextAction();
		}
		else if (SHOW_TARGET) D3GLES20.removeShape(mPlanTarget);
		
		mState = checkForSomethingInteresting(mWorldModel);
		switch(mState) {
		case EXPLORE: return chooseRandomAction();
		case FORAGE: mPlan = makeScavagePlan(mWorldModel); break;
		case HIDE: 
			if (mWorldModel.getLightLevel() > 0) mPlan = makeHidePlan(mWorldModel); 
			else mPlan = new NoPlan(mWorldModel.getHeadX(), mWorldModel.getHeadY());
			break;
		case DONOTHING: mPlan = new NoPlan(mWorldModel.getHeadX(), mWorldModel.getHeadY()); break;
		}
		makeTarget();
		mPlan.update(mWorldModel);
		return mPlan.nextAction();
	}

	private PlanState checkForSomethingInteresting(WorldModel mWorldModel) {
		if (mWorldModel.knowFoodLocation()) {
//			Log.v(TAG, "Found an interesting food location!");
			return PlanState.FORAGE;
		}
		if (mWorldModel.knowAlgaeLocation()) {
//			Log.v(TAG, "Found an interesting algae location!");
			return PlanState.HIDE;
		}
		return PlanState.DONOTHING;
	}

	private Plan makeScavagePlan(WorldModel mWorldModel) {
		Log.v(TAG, "Making scavage plan");
		Plan scvgPlan = new GoToAndEatPlan(
				mWorldModel.getHeadX(), mWorldModel.getHeadY(), 
				mWorldModel.getBodyX(), mWorldModel.getBodyY(), 
				mWorldModel.getFoodX(), mWorldModel.getFoodY());
		return scvgPlan;
	}

	private Plan makeHidePlan(WorldModel mWorldModel) {
		Log.v(TAG, "Making hide plan");
		Plan hidePlan = new GoToAndStayPlan(
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
		D3GLES20.draw(mPlanTarget, mPlan.getTargetMMatrix(), mVMatrix, mProjMatrix);
	}

	public float[] getTargetMMatrix() {
		return mPlan.getTargetMMatrix();
	}

	public void makeTarget() {
		mPlanTarget = D3GLES20.newDefaultCircle(mPlan.getTargetSize(), mPlan.getTargetColor(), 10);
	}

	public int getTarget() {
		return mPlanTarget;
	}
	
}
