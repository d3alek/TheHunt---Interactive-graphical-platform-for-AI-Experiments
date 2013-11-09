package com.primalpond.hunt;

import java.util.ArrayList;

import android.graphics.PointF;
import android.util.Log;

import com.primalpond.hunt.world.environment.FloatingObject;
import com.primalpond.hunt.world.environment.FloatingObject.Type;
import com.primalpond.hunt.world.environment.NAlgae;

import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.sprite.SpriteManager;

public class FoodTrainingDemonstrator extends Demonstrator {

	private static final float PREY_FROM_FOOD_TARGET = 0.2f;
	private static final String TAG = "FoodTrainingDemonstrator";
	private NAlgae mAlgae;
	private boolean mPutFood;
	private boolean mWaitingForPrey;
	private PointF mPlaceFoodAt;
	private boolean initialWait;
	private FloatingObject mFoodFo;
	private boolean mIsFinished;
	private boolean mIsSatisfied;
	private boolean mPutFoodTouchWait;
	private boolean mWaitingForFoodToAppear;

	public FoodTrainingDemonstrator(TutorialRenderer renderer,
			SpriteManager spriteManager) {
		super(renderer, spriteManager);
	}

	@Override
	public void update() {
		if (mIsFinished) {
			if (mRenderer.mPrey.getCaught()) {
				mIsSatisfied = true;
			}
		}
		else if (!initialWait) {
			if (countFinished()) {
				initialWait = true;
				clearCount();
			}
			else if (!counting()) {
				countForSec(2);
			}
		}
		else if (mAlgae != null) {
			if (!mPutFood) {
				if (mPutFoodTouchWait) {
					if (countFinished()) {
						releaseTouch(mPlaceFoodAt);
						mPutFoodTouchWait = false;
						mWaitingForFoodToAppear = true;
						clearCount();
						
					}
				}
				else if (mWaitingForFoodToAppear) {
					ArrayList<FloatingObject> floatingObjects = mRenderer.mEnv.seeObjects(0, 0, 2);
					for (FloatingObject fo: floatingObjects) {
						if (fo.getType() == Type.FOOD_GM) {
							Log.i(TAG, "Found food!");
							mFoodFo = fo;
							mWaitingForPrey = true;
							mPlaceFoodAt = fo.getPosition();
							mPutFood = true;
							break;
						}
					}
				}
				else {
					Log.i(TAG, "Placing food");
					mPlaceFoodAt = new PointF(0, 0);
					float offset = mAlgae.getRadius()+D3Maths.pxToScreen(NetTrainingDemonstrator.RADIUS_PX);
					mPlaceFoodAt.offset(offset, offset);
					touchDown(mPlaceFoodAt);
					mPutFoodTouchWait = true;
					countForSec(1);
				}

			}
			else {
				if (mWaitingForPrey) {
					mRenderer.mPrey.setTargetFood(mFoodFo);
					PointF preyPosition = mRenderer.mPrey.getPosition();
					if (D3Maths.distance(preyPosition.x, preyPosition.y, mPlaceFoodAt.x, mPlaceFoodAt.y) < PREY_FROM_FOOD_TARGET) {
						Log.i(TAG, "No longer waiting for prey, release net now!");
						mWaitingForPrey = false;
						startNet(mPlaceFoodAt);
					}
				}
				else if (netFinished()) {
					if (countFinished()) {
						Log.i(TAG, "Count finished");
						mIsFinished = true;
					}
					else if (!counting()) {
						Log.i(TAG, "Start count");
						countForSec(2);
					}
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

	public void setAlgae(NAlgae algae) {
		mAlgae = algae;
		mPutFood = false;
	}

}
