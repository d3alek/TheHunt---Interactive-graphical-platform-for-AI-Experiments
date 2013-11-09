package com.primalpond.hunt;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

import com.primalpond.hunt.agent.prey.Prey;
import com.primalpond.hunt.world.environment.Environment;
import com.primalpond.hunt.world.environment.NAlgae;
import com.primalpond.hunt.world.logic.TheHuntRenderer;
import com.primalpond.hunt.world.tools.CatchNet;

import d3kod.graphics.shader.ShaderProgramManager;
import d3kod.graphics.sprite.SpriteManager;
import d3kod.graphics.texture.TextureManager;

public class TutorialRenderer extends TheHuntRenderer {

	private static final String TAG = "TutorialRenderer";
	private static final int SHOULD_FEED = 200;
	private static final int INCR_ENERGY_AMOUNT = 20;
	private Demonstrator mDemonstrator;
	private int mUpdated;
	private boolean mToShowDemonstrator;
	private TutorialStep mTutorialStep;

	public enum TutorialStep {
		NET_TRAINING, FOOD_TRAINING, CUT_TRAINING;
	}

	public TutorialRenderer(Context context) {
		super(context, TAG);
		mToShowDemonstrator = true;
		mTutorialStep = TutorialStep.NET_TRAINING;
	}

	@Override
	protected SpriteManager loadSavedState(ShaderProgramManager shaderManager,
			TextureManager textureManager) {
		Log.v(TAG, "Loading saved state");

		mD3GLES20 = new SpriteManager(shaderManager, textureManager, mContext);
		mEnv = new Environment(null, mD3GLES20, 0);
		//		mEnv.addNewAlgae(10, new PointF(0, 0), 0);
		mPrey = new Prey(mEnv, null, mD3GLES20);

		return mD3GLES20;
	}

	@Override
	public void updateWorld() {
		super.updateWorld();
		if (mTutorialStep == null) {
			return;
		}
		if (mTutorialStep.equals(TutorialStep.NET_TRAINING)) {
			mPrey.setPosition(new PointF(0, 0));
			mUpdated ++;
			if (mUpdated > SHOULD_FEED) {
//				mEnv.putFoodGM(mPrey.getRadius(), 0);
				mPrey.increaseEnergy(INCR_ENERGY_AMOUNT);
				mUpdated = 0;
			}
			
		}
		if (mTutorialStep.equals(TutorialStep.FOOD_TRAINING)) {
			mPrey.reduceEnergy(100);
		}
		
		
//		if (mTutorialStep.equals(TutorialStep.FOOD_TRAINING)) {
//			mPrey.setStressLevel(StressLevel.PLOK_CLOSE);
//		}
		if (mDemonstrator != null) {
			mDemonstrator.update();
		}

		if (mDemonstrator != null && mDemonstrator.isSatisfied()) {
//			mOnPauseListener.onToShowNavigation();
			int nextTutStep = mTutorialStep.ordinal() + 1;
			mOnPauseListener.onToShowTutorialText(nextTutStep);
			if (nextTutStep < TutorialStep.values().length) {
				mTutorialStep = TutorialStep.values()[nextTutStep];
			}
			else {
				mOnPauseListener.notifyTutorialFinished();
				mTutorialStep = null;
			}
//			if (mTutorialStep.equals(TutorialStep.FOOD_TRAINING)) {
//
//			}
			mToShowDemonstrator = true;
			if (mDemonstrator.getGraphic() != null) {
				mDemonstrator.clearGraphic();
			}
			mDemonstrator = null;
		}

		else if (mDemonstrator != null && mDemonstrator.isFinished()) {
			mOnPauseListener.onToShowTutTryText();
			mDemonstrator.monitorGoalProgress();
		}

	}


	@Override
	protected void saveState() {
		Log.i(TAG, "Ignoring saveState call");
	}


//	@Override
//	public void handleTouch(MotionEvent event, boolean doubleTouch) {
//		Log.i(TAG, "IMHERE");
//		handleTouch(event, doubleTouch, false);
//	}
	@Override
	public void handleTouch(MotionEvent event, boolean doubleTouch, boolean nonDemonstratorTouch) {
		if (mTutorialStep == null) {
			return;
		}
		if (nonDemonstratorTouch) {
			if (mDemonstrator != null && !mDemonstrator.isFinished()) {
				Log.i(TAG, "Ignoring touch");
				return;
			}

			if (mToShowDemonstrator) {
//				mOnPauseListener.onToHideNavigation();
//				mOnPauseListener.onToHideTutorialText();
				showDemonstrator();
				mOnPauseListener.onToShowTutorialDescriptiveText();
				mToShowDemonstrator = false;
				switch (mTutorialStep) {
				case NET_TRAINING:
					mDemonstrator = new NetTrainingDemonstrator(this, mD3GLES20);
					break;
				case FOOD_TRAINING:
					mDemonstrator = new FoodTrainingDemonstrator(this, mD3GLES20);
					NAlgae algae = mEnv.addNewAlgae(15, new PointF(0, 0), 0);
					mPrey.setPosition(new PointF(0, 0));
					((FoodTrainingDemonstrator)mDemonstrator).setAlgae(algae);
					break;
				case CUT_TRAINING:
					mDemonstrator = new CutDemonstrator(this, mD3GLES20);
					mTool = new CatchNet(mEnv, mD3GLES20);
					break;
				}
				//mDemonstrator.initGraphic();
				return;
			}
		}
		super.handleTouch(event, doubleTouch, nonDemonstratorTouch);
	}
	
	public void showDemonstrator() {
		if (mTutorialStep == null) {
			return;
		}
		Log.i(TAG, "showDemonstrator");
//		mOnPauseListener.onToHideNavigation();
		if (mDemonstrator != null) {
			mDemonstrator.clearGraphic();
		}
		mOnPauseListener.onToShowTutorialDescriptiveText();
		mToShowDemonstrator = false;
		switch (mTutorialStep) {
		case NET_TRAINING:
			mDemonstrator = new NetTrainingDemonstrator(this, mD3GLES20);
			break;
		case FOOD_TRAINING:
			mDemonstrator = new FoodTrainingDemonstrator(this, mD3GLES20);
			NAlgae algae = mEnv.addNewAlgae(15, new PointF(0, 0), 0);
			mPrey.setPosition(new PointF(0, 0));
			((FoodTrainingDemonstrator)mDemonstrator).setAlgae(algae);
			break;
		case CUT_TRAINING:
			mDemonstrator = new CutDemonstrator(this, mD3GLES20);
			mTool = new CatchNet(mEnv, mD3GLES20);
			break;
		}
		//mDemonstrator.initGraphic();
		return;
	}

}
