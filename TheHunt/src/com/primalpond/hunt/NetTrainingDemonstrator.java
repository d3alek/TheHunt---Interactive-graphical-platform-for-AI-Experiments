package com.primalpond.hunt;

import android.graphics.PointF;
import android.util.Log;
import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.sprite.SpriteManager;

public class NetTrainingDemonstrator extends Demonstrator {

	private static final String TAG = NetTrainingDemonstrator.class.getName();
	private boolean mFinished;
	private boolean mIsSatisfied;

	public NetTrainingDemonstrator(TutorialRenderer renderer,
			SpriteManager spriteManager) {
		super(renderer, spriteManager);
		startNet(new PointF(0, 0));
	}

	@Override
	public void update() {

		if (mFinished && mMonitoringGoalProgress) {
			if (countFinished()) {
				Log.i(TAG, "Am satisfied");
				mIsSatisfied = true;
			}
			if (mRenderer.mPrey.getCaught()) {
				Log.i(TAG, "Start counting");
				countForSec(1);
			}
		}
		else if (!mFinished && netFinished()) {
			if (countFinished()) {
				Log.i(TAG, "net count finished");
				mFinished = true;
				clearCount();
			}
			else if (!counting()) {
				Log.i(TAG, "net count ");
				countForSec(3);
			}
		}

		super.update();
	}

	@Override
	public boolean isFinished() {
		return mFinished;
	}

	@Override
	public boolean isSatisfied() {
		return mIsSatisfied;
	}

}
