package d3kod.thehunt.prey.memory;

import java.util.ArrayList;

import android.util.Log;
import d3kod.d3gles20.D3Maths;
import d3kod.thehunt.environment.Dir;
import d3kod.thehunt.events.Event;
import d3kod.thehunt.events.Event.EventType;
import d3kod.thehunt.events.EventAlgae;
import d3kod.thehunt.events.EventAt;
import d3kod.thehunt.events.EventCurrent;
import d3kod.thehunt.events.EventFood;
import d3kod.thehunt.events.EventLight;
import d3kod.thehunt.events.EventNoise;

public class WorldModel {
private static final String TAG = "WorldModel";
private static final float INF = 100;
private static final float LOUD_NOISE = 1f;
private static final int HIDDEN_FOR_SAFE = 30;
	//MemoryGraph mNodes;
	private float mHeadX;
	private float mHeadY;
	private float mBodyY;
	private float mBodyX;
	private int mLightLevel;
	
	private ArrayList<Event> mEventMemory = new ArrayList<Event>();
	private EventFood mNearestFood;
	private EventAlgae mNearestAlgae;
	private Dir mCurrentDir;
	private float mHeadAngle;
	private StressLevel mStressLevel;
	private int mHiddenFor;
//	private boolean mLoudNoiseHeard;
//	public void updateNode(float posX, float posY, float currentX, float currentY) {
//		mNodes.getNode(posX, posY).setCurrent(currentX, currentY);
//	}
	public WorldModel(float screenWidth, float screenHeight) {
//		mNodes = new MemoryGraph(screenWidth, screenHeight);
//		mNearestFoodX = mNearestFoodY = -1;
		mNearestFood = null;
		mNearestAlgae = null;
		mStressLevel = StressLevel.CALM;
		mHiddenFor = 0;
	}
	public void update(ArrayList<Event> sensorEvents) {
//		mLoudNoiseHeard = false;
//		Log.v(TAG, "Updating world model");
		for (Event e: sensorEvents) {
			processEvent(e);
		}
		mNearestAlgae = recallNearestAlgae();
	}
	private void processEvent(Event e) {
		if (mEventMemory.contains(e)) return;
		switch(e.type()) { 
		case AT: 
			EventAt eAt = (EventAt) e;
			float newBodyX = eAt.getBodyX();
			float newBodyY = eAt.getBodyY();
			float newHeadX = eAt.getHeadX();
			float newHeadY = eAt.getHeadY();
			if (newHeadX != mHeadX || newHeadY != mHeadY) {
				// TODO: There is a movement! Improve detection?
				mHeadX = newHeadX; mHeadY = newHeadY;
				mBodyX = newBodyX; mBodyY = newBodyY;
				mHeadAngle = eAt.getHeadAngle();
//				Log.v(TAG, "mHeadAngle is " + mHeadAngle);
			}
			break;
		case FOOD: 
			EventFood food = (EventFood)e;
			float foodX = food.getFoodX();
			float foodY = food.getFoodY();
			if (!knowFoodLocation() ||
					D3Maths.distance(mHeadX, mHeadY, mNearestFood.getFoodX(), mNearestFood.getFoodY()) > 
					D3Maths.distance(mHeadX, mHeadY, foodX, foodY)) {
				mNearestFood = food;
				//TODO cache current food distance
			}
			break;
		case ALGAE:
			break;
		case LIGHT:
//			EventLight light = (EventLight) e;
			mLightLevel = ((EventLight) e).getLightLevel();
			if (mLightLevel == 0) {
				mHiddenFor++;
//				Log.v(TAG, "mLight level is " + mLightLevel);
			}
			else {
				mHiddenFor = 0;
			}
			if (mStressLevel == StressLevel.PLOK_CLOSE && mHiddenFor > HIDDEN_FOR_SAFE) {
				Log.v(TAG, "Feeling safe, be cautios now");
				mStressLevel = StressLevel.CAUTIOS;
			}
			break;
		case CURRENT:
			mCurrentDir = ((EventCurrent) e).getDir();
			break;
		case NOISE:
			EventNoise noise = (EventNoise) e;
			if (noise.getLoudness() >= LOUD_NOISE) {
//				mLoudNoiseHeard = true;
				Log.v(TAG, "Loud noise heard, panic!");
				mStressLevel = StressLevel.PLOK_CLOSE;
				mHiddenFor = 0;
			}
		}
		if (e.type() == EventType.FOOD || e.type() == EventType.ALGAE) {
			rememberEvent(e);
		}
	}
	private void rememberEvent(Event e) {
		mEventMemory.add(e);
	}
	public int getLightLevel() {
		return mLightLevel;
	}
	public boolean knowFoodLocation() {
		return (mNearestFood != null);
	}
	public float getNearestFoodX() {
		return mNearestFood.getFoodX();
	}
	public float getNearestFoodY() {
		return mNearestFood.getFoodY();
	}
	public float getNearestAlgaeX() {
		return mNearestAlgae.getAlgaeX();
	}
	public float getNearestAlgaeY() {
		return mNearestAlgae.getAlgaeY();
	}
	public float getHeadX() {
		return mHeadX;
	}
	public float getHeadY() {
		return mHeadY;
	}
	public void eatFood(float mPosHeadX, float mPosHeadY) {
		//TODO: the food removed is not always the nearest food #BUG
		mEventMemory.remove(mNearestFood);
		mNearestFood = recallNearestFood();
	}
	private EventFood recallNearestFood() {
		float closestX = INF, closestY = INF;
		EventFood closestFood = null;
		
		for (Event e: mEventMemory) {
			if (e.type() == EventType.FOOD) {
				EventFood ef = (EventFood)e;
				float foodX = ef.getFoodX();
				float foodY = ef.getFoodY();
				if (D3Maths.distance(mHeadX, mHeadY, foodX, foodY) <
						D3Maths.distance(mHeadX, mHeadY, closestX, closestY)) {
					closestFood = ef;
					closestX = foodX; 
					closestY = foodY;
				}
			}
		}
		
		return closestFood;
	}
	
	private EventAlgae recallNearestAlgae() {
		float closestX = INF, closestY = INF;
		EventAlgae closestAlgae = null;
		
		for (Event e: mEventMemory) {
			if (e.type() == EventType.ALGAE) {
				EventAlgae ea = (EventAlgae)e;
				float foodX = ea.getAlgaeX();
				float foodY = ea.getAlgaeY();
				if (D3Maths.distance(mHeadX, mHeadY, foodX, foodY) <
						D3Maths.distance(mHeadX, mHeadY, closestX, closestY)) {
					closestAlgae = ea;
					closestX = foodX; 
					closestY = foodY;
				}
			}
		}
		
		return closestAlgae;	
	}
	
	public float getBodyX() {
		return mBodyX;
	}
	public float getBodyY() {
		return mBodyY;
	}
	public boolean knowAlgaeLocation() {
		return mNearestAlgae != null;
	}
	public void recalcNearestFood() {
		mNearestFood = recallNearestFood();
	}
	public Dir getCurrentDir() {
		return mCurrentDir;
	}
	
	public float getHeadAngle() {
		return mHeadAngle;
	}
	
	public StressLevel getStressLevel() {
		return mStressLevel;
	}
}
