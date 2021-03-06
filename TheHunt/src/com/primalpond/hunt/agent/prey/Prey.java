package com.primalpond.hunt.agent.prey;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.PointF;
import android.util.FloatMath;
import android.util.Log;

import com.primalpond.hunt.agent.Agent;
import com.primalpond.hunt.agent.prey.memory.StressLevel;
import com.primalpond.hunt.agent.prey.memory.WorldModel;
import com.primalpond.hunt.agent.prey.planner.PlanState;
import com.primalpond.hunt.agent.prey.planner.Planner;
import com.primalpond.hunt.agent.prey.sensor.Sensor;
import com.primalpond.hunt.world.environment.Environment;
import com.primalpond.hunt.world.environment.EnvironmentData;
import com.primalpond.hunt.world.environment.FloatingObject;
import com.primalpond.hunt.world.environment.FoodGM;
import com.primalpond.hunt.world.environment.NAlgae;
import com.primalpond.hunt.world.events.EatableEvent;
import com.primalpond.hunt.world.events.MovingEvent;
import com.primalpond.hunt.world.events.Event.EventType;
import com.primalpond.hunt.world.floating_text.CrunchText;
import com.primalpond.hunt.world.floating_text.FlopText;
import com.primalpond.hunt.world.floating_text.PanicText;
import com.primalpond.hunt.world.floating_text.PlokText;
import com.primalpond.hunt.world.logic.TheHuntRenderer;

import d3kod.graphics.extra.D3Color;
import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.sprite.SpriteManager;
import d3kod.graphics.sprite.shapes.D3Shape;

public class Prey extends Agent {

	private static final String TAG = "Prey";
	public static final float EAT_FOOD_RADIUS = 0.1f;

	private Planner mPlanner;
	private WorldModel mWorldModel;
	private Sensor mSensor;
	private PreyData mD;


	private static final float FORCE_TO_DISTANCE = 0.00002f;
	private static final double MUTATE_CHANCE_ON_CAUGHT = 1;
	private static final double MUTATE_CHANCE_ON_EAT_PER_NUTRITION = 0.001;
	private static final String KEY_HEAD = "head";
	private static final String KEY_TAIL = "tail";
	private static final String KEY_BODY = "body";

	public static int flopBacksPerSecond = 2;
	public static int flopBackTicks = TheHuntRenderer.TICKS_PER_SECOND/flopBacksPerSecond;

	transient private D3Prey mGraphic;

	transient private SpriteManager mD3GLES20;
	private Head mHead;
	private Tail mTail;
	private Body mBody;
	private boolean mHidden;

	public void update() {
		if (mEnv == null) {
			Log.e(TAG, "Prey env is null! Prey needs env!");
		}
		if (mD.mIsCaught) return;

		calcPosHeadandTail();
		//TODO: instead loop through bodyParts array and pass the prev to the next...

		if (PreyData.AI) {
			updateWorldModel();
			doAction(mPlanner.nextAction(mWorldModel));
			doAction(mPlanner.nextParallelAction());
		}
		move();
		if (mGraphic == null) return;
		if (mEnv.algaeCovers(this)) {
			mHidden = true;
			mGraphic.setHiddenColor();
		}
		else {
			mHidden = false;
			mGraphic.resetColor();
		}

		if (mPlanner.getState() == PlanState.FORAGE) {
			mGraphic.openMouth();
		}
		else {
			mGraphic.closeMouth();
		}
	}
	
	public boolean isHidden() {
		return mHidden;
	}

	private void updateWorldModel() {
		mWorldModel.update(mSensor.sense(mHead.getX(), mHead.getY(), mHead.getSize(), mBody.getX(), mBody.getY(), mBody.getTopAngle()));
		expressEmotion();
		if (mD.emotionText != null) {
			if (mD.emotionText.faded()) {
				mD.emotionText = null;
			}
		}
	}


	private void expressEmotion() {
		if (mWorldModel.getStressLevel().compareTo(StressLevel.CAUTIOS) > 0) {
			if (mD.emotionText != null) {
				mD.emotionText.noFade();
			}

			else {
				mD.emotionText = new PanicText(mHead.getX(), mHead.getY(), mBody.getTopAngle());
				mD3GLES20.putText(mD.emotionText);
			}
		}
	}

	private void calcPosHeadandTail() {
		mTail.updatePos(mBody.getX(), mBody.getY());
		mHead.updatePos(mBody.getX(), mBody.getY(), mBody.getTopAngle());
	}

	public void move() {
		if (mD.mIsCaught) return;
		mBody.update();
		moveForward(mBody.getForce());

		applyFriction();
		PointF curPos = mBody.getPos();
		mBody.setPos(new PointF(curPos.x+mD.vx, curPos.y+mD.vy));
	}
	private void putFlopText(float angle) {
		float radAngle = (float)Math.toRadians(angle);
//		Log.v(TAG, "Put flop at " + D.mPosX + FloatMath.sin(radAngle)*D3Prey.finSize*2 + " " + (mD.mPosY - FloatMath.cos(radAngle)*D3Prey.finSize*2, angle));
		mD3GLES20.putText(new FlopText(mBody.getX() + FloatMath.sin(radAngle)*D3Prey.tailSize*2, 
				mBody.getY() - FloatMath.cos(radAngle)*D3Prey.tailSize*2, angle));
	}

	public void turn(TurnAngle angle) {

		if (!mBody.bend(angle)) {
//			Log.v(TAG, "Can't bend that much!");
			return;
		}
	}

	public void updateSpeed(float dx, float dy) {
		mD.vx += dx; mD.vy += dy;
	}

	public void moveForward(float force) {
		if (force < D3Maths.EPSILON) return;
		putFlopText(mBody.getBottomAngle());
		float distance = force * FORCE_TO_DISTANCE;
		//		Log.v(TAG, "Moving the prey forward to a distance of " + distance + " thrust is " + mD.thrust);
		float radAngle = (float)Math.toRadians(mBody.getFacingAngle());
		mD.vx += -FloatMath.sin(radAngle)*distance;
		mD.vy += FloatMath.cos(radAngle)*distance;  
	}

	public void applyFriction() {
		mD.vx -= EnvironmentData.frictionCoeff*mD.vx;
		mD.vy -= EnvironmentData.frictionCoeff*mD.vy;
	}

	public Prey(Environment env, JSONObject savedPrey, SpriteManager d3gles20) {
		super(env, d3gles20);
		mD3GLES20 = d3gles20;
		mD = new PreyData();
		
		JSONObject savedHead = null, savedTail = null, savedBody = null;
		Log.i(TAG, "SavedPrey is " + savedPrey);
		if (savedPrey != null) {
			try {
				savedHead = savedPrey.getJSONObject(KEY_HEAD);
				savedTail = savedPrey.getJSONObject(KEY_TAIL);
				savedBody = savedPrey.getJSONObject(KEY_BODY);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		mHead = new Head(savedHead);
		mTail = new Tail(savedTail);
		mBody = new Body(savedBody);
		
		setTextureManager(d3gles20.getTextureManager());
		mWorldModel = new WorldModel();
		mEnv = env;
		mPlanner = new Planner();
		mSensor = new Sensor(mEnv, mHead);

		mD.mIsCaught = false;
		mD.emotionText = null;
		if (savedBody == null) {
			PointF newPos = mEnv.randomPosInEnv();
			mBody.setPos(newPos);
		}
	}

	public PointF getPosition() {
		return mBody.getPos();
	}
	
	public void setPosition(PointF pointF) {
		mBody.setPos(pointF);
	}
	
	public PointF getHeadPositon() {
		return new PointF(mD.mPosHeadX, mD.mPosHeadY);
	}
	private void doAction(Action nextAction) {
		if (nextAction == null) {
			return;
		}
		switch(nextAction) {
		case TURN_LEFT_SMALL: turn(TurnAngle.LEFT_SMALL);break;//flopLeft(); break;
		case TURN_LEFT_MEDIUM: turn(TurnAngle.LEFT_MEDIUM);break;
		case TURN_LEFT_LARGE: turn(TurnAngle.LEFT_LARGE);break;
		case TURN_RIGHT_SMALL: turn(TurnAngle.RIGHT_SMALL);break;//flopRight(); break;
		case TURN_RIGHT_MEDIUM: turn(TurnAngle.RIGHT_MEDIUM);break;
		case TURN_RIGHT_LARGE: turn(TurnAngle.RIGHT_LARGE);break;
		case FORWARD_SMALL: mBody.backFinMotion(TurnAngle.BACK_SMALL); break;
		case FORWARD_MEDIUM: mBody.backFinMotion(TurnAngle.BACK_MEDIUM); break;
		case FORWARD_LARGE: mBody.backFinMotion(TurnAngle.BACK_LARGE); break;
		case eat: eat(); break; 
		case poop: poop(); break;
		case none: break;
		default: Log.v(TAG, "Could not process action!");
		}
	}

	private void poop() {
		//TODO D3Prey does not use getCenter... !!! 
		if (mEnv.getSpriteManager() == null) {
			Log.e(TAG, "Prey's env sprite manager is null!");
		}
		mEnv.addNewAlgae(1, new PointF(mTail.getX(), mTail.getY()), D3Maths.getRandAngle());
		mWorldModel.reduceEnergy(NAlgae.FOOD_ALGAE_BITE_NUTRITION);
	}

	private void eat() {
		int nutrition = mEnv.eatFood(mHead.getX(), mHead.getY());
		if (nutrition == 0) {
			Log.v(TAG, "I thought I ate something, but it felt like thin air :?");
			mWorldModel.eatFood(0);
		}
		else {
			mWorldModel.eatFood(nutrition);
			mGraphic.initEatingMotion();
			mD3GLES20.putText(new CrunchText(mHead.getX(), mHead.getY()));
			
//			if (Math.random() < MUTATE_CHANCE_ON_EAT_PER_NUTRITION*nutrition) {
//				mutate();
//			}
			
		}
	}

	public void setCaught(boolean caught) {
		mD.mIsCaught = caught;
		if (caught) {
			mD.vx = mD.vy = 0;
			mBody.noRot();
		}
	}

	public boolean getCaught() {
		return mD.mIsCaught;
	}

	public void release() {
		mD.mIsCaught = false;
		PointF newPos = mEnv.randomPosInEnv();
		mBody.setPos(newPos);
		mPlanner.clear();
		calcPosHeadandTail();
		updateWorldModel();
		mWorldModel.refillEnergy();
		mWorldModel.recalcNearestFood();
		mD3GLES20.putText(new PlokText(mBody.getX(), mBody.getY()));
		mGraphic.resetColor();
//		if (Math.random() < MUTATE_CHANCE_ON_CAUGHT) {
//			mutate();
//		}
	}
//	
//	private void mutate() {
//		
//			Log.v(TAG, "Mutating!");
//			int rand = (int)(Math.random()*1000);
//			if (rand % 3 == 0) {
//				//mutateHead
//				mHead.mutate();
//				mD3GLES20.putText(new MutateText("Mutate head!", mHead.getX(), mHead.getY()));
//			}
//			if (rand % 3 == 1) {
//				//mutateBody
//				mBody.mutate();
//				mD3GLES20.putText(new MutateText("Mutate body!", mBody.getX(), mBody.getY()));
//			}
//			if (rand % 3 == 2) {
//				//mutateTail
//				mTail.mutate();
//				mD3GLES20.putText(new MutateText("Mutate tail!", mTail.getX(), mTail.getY()));
//			}
//	}

	public void initGraphic() {
		mGraphic = new D3Prey(mD, mHead, mBody, mTail);
		initGraphic(mGraphic);
		mD.emotionText = null; // fix for disappearing ! after db save while panicked
	}

	@Override
	public int getEnergy() {
		return mWorldModel.getEnergy();
	}

	@Override
	public String getStateString() {
		return mPlanner.getState().toString();
	}

	public PointF getPredictedPosition() {
		return new PointF(mGraphic.getPredictedX(), mGraphic.getPredictedY());
	}

	public D3Shape getGraphic() {
		return mGraphic;
	}
	@Override
	public void setSpriteManager(SpriteManager d3gles20) {
		super.setSpriteManager(d3gles20);
		mD3GLES20 = d3gles20;
	}
	@Override
	public D3Color getMoodColor() {
		switch (mWorldModel.getMoodLevel()) {
			case DESPAIR: return D3Color.RED;
			case NEUTRAL: return D3Color.DARK_GREEN;
			case RISK: return D3Color.ORANGE;
			default:
				return D3Color.BLACK;
		}
	}
	
	public JSONObject toJSON() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(KEY_BODY, mBody.toJSON());
		jsonObject.put(KEY_HEAD, mHead.toJSON());
		jsonObject.put(KEY_TAIL, mTail.toJSON());
		return jsonObject;
	}

	@Override
	public void setStressLevel(StressLevel stressLevel) {
		mWorldModel.setStressLevel(stressLevel);
	}

	@Override
	public void setTargetFood(FloatingObject fo) {
		mWorldModel.setTargetFood(
				new EatableEvent(fo.getX(), fo.getY(),
						EventType.FOOD, ((FoodGM)fo).getNutrition(), fo.getRadius()));
	}

	@Override
	public void reduceEnergy(int i) {
		mWorldModel.reduceEnergy(i);
	}

	@Override
	public void increaseEnergy(int i) {
		int energy = mWorldModel.getEnergy();
		mWorldModel.setEnergy(energy+i);
	}
	
}

