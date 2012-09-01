package d3kod.thehunt.prey.planner;

import java.util.Random;

import android.util.Log;
import d3kod.d3gles20.D3GLES20;
import d3kod.thehunt.prey.Action;
import d3kod.thehunt.prey.memory.WorldModel;
import d3kod.thehunt.prey.planner.plans.ExplorePlan;
import d3kod.thehunt.prey.planner.plans.GoToAndEatPlan;
import d3kod.thehunt.prey.planner.plans.GoToAndStayPlan;
import d3kod.thehunt.prey.planner.plans.NoPlan;
import d3kod.thehunt.prey.planner.plans.Plan;

public class Planner {
	private static final String TAG = "Planner";
	public static final boolean SHOW_TARGET = true;
	public static PlanState mState;
	private Action[] allActions;
	private int numActions; 
	private Random mRandom;
	private Plan mPlan;
	private int mPlanTarget;
	private D3GLES20 mD3GLES20;
	private ExplorePlan mExplorePlan;

	public Planner(D3GLES20 d3GLES20) {
		mD3GLES20 = d3GLES20;
		allActions = Action.values();
		numActions = allActions.length;
		mRandom = new Random();
		clear();
	}

	public Action nextAction(WorldModel mWorldModel) {
		if (!mPlan.finishedCurrentAction()) {
//			Log.v(TAG, "Ticking current action")
			mPlan.tickCurrentAction();
			return Action.none;
		}
		mPlan.update(mWorldModel);
		if (!mPlan.isFinished()) {
			if (SHOW_TARGET) mD3GLES20.setShapePosition(mPlanTarget, mPlan.getTargetX(), mPlan.getTargetY());
			return mPlan.nextAction();
		}
		else if (SHOW_TARGET) mD3GLES20.removeShape(mPlanTarget);
		
		mState = checkForSomethingInteresting(mWorldModel);
//		Log.v(TAG, "mState is " + mState);
		switch(mState) {
		case EXPLORE: mPlan = makeExplorePlan(mWorldModel); break;
		case FORAGE: mPlan = makeScavagePlan(mWorldModel); break;
		case HIDE: mPlan = makeHidePlan(mWorldModel); break;
		case DONOTHING: mPlan = new NoPlan(mWorldModel.getHeadX(), mWorldModel.getHeadY()); break;
		}
		if (SHOW_TARGET) makeTarget();
		mPlan.update(mWorldModel);
		if (SHOW_TARGET) mD3GLES20.setShapePosition(mPlanTarget, mPlan.getTargetX(), mPlan.getTargetY());
		return mPlan.nextAction();
	}

	private Plan makeExplorePlan(WorldModel mWorldModel) {
		if (mExplorePlan == null) mExplorePlan = new ExplorePlan(mWorldModel);
		return mExplorePlan;
	}

	private PlanState checkForSomethingInteresting(WorldModel mWorldModel) {
		if (mWorldModel.knowFoodLocation()) {
			return PlanState.FORAGE;
		}
//		if (mWorldModel.knowAlgaeLocation()) {
//			return PlanState.HIDE;
//		}
		return PlanState.EXPLORE;
	}

	private Plan makeScavagePlan(WorldModel mWorldModel) {
//		Log.v(TAG, "Making scavage plan");
		Plan scvgPlan = new GoToAndEatPlan(
				mWorldModel.getHeadX(), mWorldModel.getHeadY(), 
				mWorldModel.getBodyX(), mWorldModel.getBodyY(), 
				mWorldModel.getNearestFoodX(), mWorldModel.getNearestFoodY());
		return scvgPlan;
	}

	private Plan makeHidePlan(WorldModel mWorldModel) {

		if (mWorldModel.getLightLevel() == 0) return new NoPlan(mWorldModel.getHeadX(), mWorldModel.getHeadY());
//		Log.v(TAG, "Making hide plan because light level is " + mWorldModel.getLightLevel());
		Plan hidePlan = new GoToAndStayPlan(
				mWorldModel.getHeadX(), mWorldModel.getHeadY(), 
				mWorldModel.getBodyX(), mWorldModel.getBodyY(), 
				mWorldModel.getNearestAlgaeX(), mWorldModel.getNearestAlgaeY());
		return hidePlan;
	}
	
	private Action chooseRandomAction() {
		int actionInd = Math.round((numActions-1) * mRandom.nextFloat());
		return allActions[actionInd];
	}

	public void makeTarget() {
		mPlanTarget = mD3GLES20.newDefaultCircle(mPlan.getTargetSize(), mPlan.getTargetColor(), 10);
	}

	public int getTarget() {
		return mPlanTarget;
	}

	public void clear() {
		mPlan = new NoPlan(0, 0);
		mState = PlanState.DONOTHING;
	}
	
}
