package com.primalpond.hunt;

import android.graphics.PointF;
import android.util.FloatMath;
import android.util.Log;

import com.primalpond.hunt.world.environment.FloatingObject;
import com.primalpond.hunt.world.tools.Knife;

import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.sprite.SpriteManager;

public class CutDemonstrator extends Demonstrator {

	private static final float PREY_FROM_FOOD_TARGET = 0.1f;
	private static final String TAG = "FoodTrainingDemonstrator";
	private static final float DIST_FROM_KNIFE = 0.1f;
	private static final float SPEED_CUT = 0.01f;
	private static final float SPEED_SELECT_TOOL = 0.005f;
	private boolean mIsFinished;
	private boolean mIsSatisfied;
	private FloatingObject mTarget;
	private boolean mSwitchingTool;
	private boolean mStartedCutting;
	private double mCuttingAngle;
	private float mCuttingDist;
	private boolean mToSwitchToolAfterCount;
	private boolean mStartMovingAfterCount;

	public CutDemonstrator(TutorialRenderer renderer,
			SpriteManager spriteManager) {
		super(renderer, spriteManager);
	}

	@Override
	public void update() {
		
		if (mIsFinished && !mIsSatisfied) {
			if (mRenderer.mPrey.getCaught()) {
				mIsSatisfied = true;
			}
		}
		else if (!mStartedCutting && (mTarget == null || mTarget.getGraphic() == null)) {
//			ArrayList<FloatingObject> floatingObjects = mRenderer.mEnv.seeObjects(0, 0, 4);
//			for (FloatingObject fo: floatingObjects) {
//				if (fo.getType() == Type.ALGAE && fo.getGraphic() != null) {
//					mTarget = fo;
//					break;
//				}
//			}
			mTarget = mRenderer.mEnv.addNewAlgae(30, new PointF(0, 0), D3Maths.getRandAngle());
		}
		else {
			if (mSwitchingTool) {
				if (!mRenderer.mHUD.mPalette.isShown()) {
					if (isTouching()) {
						// wait for palette to show up
					}
					else {
						Log.i(TAG, "Touching");
						touchDown(getPosition());
						countForSec(1);
						mStartMovingAfterCount = true;
					}
				}
				else if (mToSwitchToolAfterCount) {
					if (countFinished()) {
						clearCount();
						releaseTouch(getPosition());
						mSwitchingTool = false;
						mToSwitchToolAfterCount = false;
					}
				}
				else if (mStartMovingAfterCount) {
					if (countFinished()) {
						clearCount();
						mStartMovingAfterCount = false;
					}
				}
				else {
					PointF pos = getPosition();
					PointF knifePos = mRenderer.mHUD.mPalette.knife.getPosition();
					float dist = D3Maths.distance(pos.x, pos.y, knifePos.x, knifePos.y);
					if (dist < DIST_FROM_KNIFE) {
						mToSwitchToolAfterCount = true;
						countForSec(1);
					}
					else {
						float dx = knifePos.x - pos.x;
						float dy = knifePos.y - pos.y;
						pos.offset(SPEED_SELECT_TOOL*dx/dist, SPEED_SELECT_TOOL*dy/dist);
						moveTouch(pos);
					}
				}

			}
			else if (mRenderer.mTool.getClass() != Knife.class) {
				mSwitchingTool = true;
			}
			else {
				if (!mStartedCutting) {
					Log.i(TAG, "Starting cutting");
					PointF pos = mTarget.getPosition();
					float r = mTarget.getRadius();
					float angl = D3Maths.getRandAngle();
					Log.i(TAG, "Angl is " + angl);
					touchDown(new PointF(pos.x + r*FloatMath.cos(angl), pos.y + r*FloatMath.sin(angl)));
					mCuttingAngle = angl - Math.PI;
					mCuttingDist = 2*r;
					Log.i(TAG, "Cutting dist is " + mCuttingDist);
					mStartedCutting = true;
				}
				else if (mCuttingDist <= 0) {
					Log.i(TAG, "Finished cutting!");
					releaseTouch(getPosition());
					mStartedCutting = false;
					mIsFinished = true;
				}
				else {
					Log.i(TAG, "Doing cut!");
					PointF pos = getPosition();
					float newX = (float)(pos.x + SPEED_CUT*Math.cos(mCuttingAngle));
					float newY = (float)(pos.y + SPEED_CUT*Math.sin(mCuttingAngle));
					Log.i(TAG, "newX, newY angle " + newX + " " + newY + " " + mCuttingAngle);
					mCuttingDist -= D3Maths.distance(pos.x, pos.y, newX, newY);
					moveTouch(new PointF(newX, newY));
				}
			}
		}
		super.update();
	}

	@Override
	public boolean isFinished() {
		return mIsFinished;
	}

	@Override
	public boolean isSatisfied() {
		return mIsSatisfied;
	}

}
