package d3kod.thehunt.prey.planner;

import d3kod.d3gles20.D3GLES20;
import d3kod.thehunt.prey.Action;
import d3kod.thehunt.prey.memory.MoodLevel;
import d3kod.thehunt.prey.memory.WorldModel;
import d3kod.thehunt.prey.planner.plans.ExplorePlan;
import d3kod.thehunt.prey.planner.plans.GoToAndEatPlan;
import d3kod.thehunt.prey.planner.plans.GoToAndStayPlan;
import d3kod.thehunt.prey.planner.plans.GotoAndStayWhileHidden;
import d3kod.thehunt.prey.planner.plans.NoPlan;
import d3kod.thehunt.prey.planner.plans.Plan;
import d3kod.thehunt.prey.planner.plans.RapidExplorePlan;

public class Planner {
	private static final String TAG = "Planner";
	public static final boolean SHOW_TARGET = true;
	private PlanState mState;
	private Plan mPlan;
	private int mPlanTarget;
	private D3GLES20 mD3GLES20;
	private ExplorePlan mExplorePlan;

	public Planner(D3GLES20 d3GLES20) {
		mD3GLES20 = d3GLES20;
		clear();
	}

	public Action nextAction(WorldModel mWorldModel) {
		if (!mPlan.finishedCurrentAction()) {
//			Log.v(TAG, "Ticking current action")
			mPlan.tickCurrentAction();
			//TODO: this looks like a good place for parallel actions
			if (mWorldModel.amOverweight()) return Action.poop;
			return Action.none;
		}
		mPlan.update(mWorldModel);
		if (!mPlan.isFinished()) {
			if (SHOW_TARGET && !(mPlan instanceof ExplorePlan)) mD3GLES20.setShapePosition(mPlanTarget, mPlan.getTargetX(), mPlan.getTargetY());
			return mPlan.nextAction();
		}
		else if (SHOW_TARGET && !(mPlan instanceof ExplorePlan)) mD3GLES20.removeShape(mPlanTarget);
		
		mState = checkForSomethingInteresting(mWorldModel);
//		Log.v(TAG, "mState is " + mState);
		switch(mState) {
		case EXPLORE: mPlan = makeExplorePlan(mWorldModel); break;
		case FORAGE: mPlan = makeScavagePlan(mWorldModel); break;
		case HIDE: mPlan = makeHidePlan(mWorldModel); break;
		case DONOTHING: mPlan = new NoPlan(mWorldModel.getHeadX(), mWorldModel.getHeadY()); break;
		}
		if (SHOW_TARGET && !(mPlan instanceof ExplorePlan)) makeTarget();
		mPlan.update(mWorldModel);
		if (SHOW_TARGET && !(mPlan instanceof ExplorePlan)) mD3GLES20.setShapePosition(mPlanTarget, mPlan.getTargetX(), mPlan.getTargetY());
		return mPlan.nextAction();
	}
	
	private PlanState checkForSomethingInteresting(WorldModel mWorldModel) {
		if (mWorldModel.getMoodLevel() == MoodLevel.DESPAIR) {
			if (mWorldModel.knowFoodLocation()) {
				return PlanState.FORAGE;
			}
			return PlanState.EXPLORE;
		}
		switch (mWorldModel.getStressLevel()) {
		case CALM: 
			if (mWorldModel.knowFoodLocation()) {
				return PlanState.FORAGE;
			}
			return PlanState.EXPLORE;
		case CAUTIOS:
			//TODO try to hide under algae while exploring
			if (mWorldModel.knowFoodLocation()) {
				return PlanState.FORAGE;
			}
			return PlanState.EXPLORE;
//		case 2:
//			//TODO  (plok recently) - същото като 1, този път се движи под водорасли докато може на път за храна
		case PLOK_CLOSE:
			//TODO (plok very close) - прави измъкващи маневри, цел да се скрие под водорасло
			return PlanState.HIDE;
		default: return PlanState.DONOTHING;	
		}
	}
	private Plan makeExplorePlan(WorldModel mWorldModel) {
//		Log.v(TAG, "Making explore plan");
		if (mWorldModel.getMoodLevel() == MoodLevel.DESPAIR) {
			if (mExplorePlan == null || !mExplorePlan.isRapid()) {
				mExplorePlan = new RapidExplorePlan(mWorldModel);
			}
		}
		else if (mExplorePlan == null || mExplorePlan.isRapid()) {
			mExplorePlan = new ExplorePlan(mWorldModel);
		}
		return mExplorePlan;
	}

	private Plan makeScavagePlan(WorldModel mWorldModel) {
//		Log.v(TAG, "Making scavage plan");
//		Plan scvgPlan = new GoToAndEatPlan(
//				mWorldModel.getHeadX(), mWorldModel.getHeadY(), 
//				mWorldModel.getBodyX(), mWorldModel.getBodyY(), 
//				mWorldModel.getNearestFoodX(), mWorldModel.getNearestFoodY());
		Plan scvgPlan = new GoToAndEatPlan(
				mWorldModel.getHeadX(), mWorldModel.getHeadY(), 
				mWorldModel.getBodyX(), mWorldModel.getBodyY(), 
				mWorldModel.getNearestFood());
		return scvgPlan;
	}

	private Plan makeHidePlan(WorldModel mWorldModel) {
//		Log.v(TAG, "Making hide plan");
		Plan hidePlan;
		if (mWorldModel.knowAlgaeLocation()) {
			hidePlan = new GotoAndStayWhileHidden(
					mWorldModel.getHeadX(), mWorldModel.getHeadY(), 
					mWorldModel.getBodyX(), mWorldModel.getBodyY(), 
					mWorldModel.getNearestAlgae());
		}
		else {
			hidePlan = new RapidExplorePlan(mWorldModel);
		}
		return hidePlan;
	}
	
//	private Action chooseRandomAction() {
//		int actionInd = Math.round((numActions-1) * mRandom.nextFloat());
//		return allActions[actionInd];
//	}

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

	public PlanState getState() {
		return mState;
	}
	
}
