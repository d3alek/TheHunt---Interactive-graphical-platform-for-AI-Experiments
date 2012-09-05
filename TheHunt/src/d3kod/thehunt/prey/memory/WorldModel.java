package d3kod.thehunt.prey.memory;

import java.util.ArrayList;

import android.util.Log;
import d3kod.d3gles20.D3Maths;
import d3kod.thehunt.TheHuntRenderer;
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
	private int hiddenForSafe = 0;
	private static final int HIDDEN_FOR_SAFE_ADJ = 30;
	private static final int HIDDEN_FOR_SAFE_MAX = 150;
	private static final int SECONDS_FOR_ENERGY_LOSS = 1;
	private static final int ENERGY_DEPLETE_TICKS = TheHuntRenderer.TICKS_PER_SECOND*SECONDS_FOR_ENERGY_LOSS;
	private static final int ENERGY_DEPLETE_SPEED = 1;
	private static final int ONE_FOOD_ENERGY = 30;
	private static final int PANIC_ENERGY = 30;
	private static final int MAX_ENERGY = 100;
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
	private int mEnergy;
	private int energyDepleteCounter;
	
	
	public WorldModel(float screenWidth, float screenHeight) {
//		mNodes = new MemoryGraph(screenWidth, screenHeight);
//		mNearestFoodX = mNearestFoodY = -1;
		mNearestFood = null;
		mNearestAlgae = null;
		mStressLevel = StressLevel.CALM;
		mHiddenFor = 0;
		mEnergy = MAX_ENERGY;
		energyDepleteCounter = 0;
	}
	public void update(ArrayList<Event> sensorEvents) {
//		mLoudNoiseHeard = false;
//		Log.v(TAG, "Updating world model");
		for (Event e: sensorEvents) {
			processEvent(e);
		}
		mNearestAlgae = recallNearestAlgae();
		if (energyDepleteCounter >= ENERGY_DEPLETE_TICKS) {
			energyDepleteCounter = 0;
			mEnergy -= ENERGY_DEPLETE_SPEED;
			if (mEnergy < 0) mEnergy = 0;
			if (mEnergy <= PANIC_ENERGY) {
				hiddenForSafe -= HIDDEN_FOR_SAFE_ADJ;
				if (hiddenForSafe < 0) hiddenForSafe = 0;
				Log.v(TAG, "decr hiddenForSafe is now " + hiddenForSafe);
			}
		}
		else {
			energyDepleteCounter++;
		}
	}
	private void processEvent(Event e) {
		if (mEventMemory.contains(e)) return;
		switch(e.type()) { 
		case AT: 
			EventAt eAt = (EventAt) e;
			mHeadX = eAt.getHeadX(); mHeadY = eAt.getHeadY();
			mBodyX = eAt.getBodyX(); mBodyY = eAt.getBodyY();
			mHeadAngle = eAt.getHeadAngle();
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
			mLightLevel = ((EventLight) e).getLightLevel();
			if (mLightLevel == 0) {
				mHiddenFor++;
			}
			else {
				mHiddenFor = 0;
			}
			if (mStressLevel == StressLevel.PLOK_CLOSE && mHiddenFor > hiddenForSafe) {
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
				Log.v(TAG, "Loud noise heard, panic!");
				mStressLevel = StressLevel.PLOK_CLOSE;
				mHiddenFor = 0;
				if (hiddenForSafe < HIDDEN_FOR_SAFE_MAX) hiddenForSafe += HIDDEN_FOR_SAFE_ADJ;
				Log.v(TAG, "incr hiddenForSafe is now " + hiddenForSafe);
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
		mEnergy += ONE_FOOD_ENERGY;
		if (mEnergy > MAX_ENERGY) {
			mEnergy = MAX_ENERGY;
		}
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
	
	public int getEnergy() {
		return mEnergy;
	}
}
