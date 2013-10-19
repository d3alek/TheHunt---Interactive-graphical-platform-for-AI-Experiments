package com.primalpond.hunt.agent.prey.planner;

import java.util.Stack;

import com.primalpond.hunt.agent.prey.Action;
import com.primalpond.hunt.agent.prey.memory.MoodLevel;
import com.primalpond.hunt.agent.prey.memory.WorldModel;
import com.primalpond.hunt.agent.prey.planner.plans.ExplorePlan;
import com.primalpond.hunt.agent.prey.planner.plans.GoToAndEatPlan;
import com.primalpond.hunt.agent.prey.planner.plans.GotoAndStayWhileHidden;
import com.primalpond.hunt.agent.prey.planner.plans.NoPlan;
import com.primalpond.hunt.agent.prey.planner.plans.ParallelAction;
import com.primalpond.hunt.agent.prey.planner.plans.Plan;
import com.primalpond.hunt.agent.prey.planner.plans.RapidExplorePlan;

import d3kod.graphics.sprite.SpriteManager;

public class Planner {
	private static final String TAG = "Planner";
	public static final boolean SHOW_TARGET = true;
	private PlanState mState;
	private Plan mPlan;
	private ExplorePlan mExplorePlan;

	public Planner() {
		clear();
	}

	public Action nextAction(WorldModel mWorldModel) {
		if (!mPlan.finishedCurrentAction()) {
			mPlan.tickCurrentAction();
			//TODO: this looks like a good place for parallel actions
			return Action.none;
		}
		if (mWorldModel.amOverweight()) mPlan.addParallelAction(Action.poop);
		mPlan.update(mWorldModel);
		if (!mPlan.isFinished()) {
			return mPlan.nextAction();
		}
		
		mState = checkForSomethingInteresting(mWorldModel);
		Stack<ParallelAction> saveParallelActions = mPlan.getParallelActions();
		switch(mState) {
		case EXPLORE: mPlan = makeExplorePlan(mWorldModel); break;
		case FORAGE: mPlan = makeScavagePlan(mWorldModel); break;
		case HIDE: mPlan = makeHidePlan(mWorldModel); break;
		case DONOTHING: mPlan = new NoPlan(mWorldModel.getHeadX(), mWorldModel.getHeadY()); break;
		}
		mPlan.setParallelActions(saveParallelActions);
		mPlan.update(mWorldModel);
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

	public void clear() {
		mPlan = new NoPlan(0, 0);
		mState = PlanState.DONOTHING;
	}

	public PlanState getState() {
		return mState;
	}

	public Action nextParallelAction() {
		return mPlan.getParallelAction();
	}
}