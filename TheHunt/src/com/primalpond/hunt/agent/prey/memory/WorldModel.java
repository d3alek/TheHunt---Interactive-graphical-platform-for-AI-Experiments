package com.primalpond.hunt.agent.prey.memory;

import java.util.ArrayList;

import com.primalpond.hunt.agent.prey.D3Prey;
import com.primalpond.hunt.world.environment.Dir;
import com.primalpond.hunt.world.events.EatableEvent;
import com.primalpond.hunt.world.events.Event;
import com.primalpond.hunt.world.events.EventAlgae;
import com.primalpond.hunt.world.events.EventAt;
import com.primalpond.hunt.world.events.EventCurrent;
import com.primalpond.hunt.world.events.EventFood;
import com.primalpond.hunt.world.events.EventLight;
import com.primalpond.hunt.world.events.EventNoise;
import com.primalpond.hunt.world.events.MovingEvent;
import com.primalpond.hunt.world.events.Event.EventType;
import com.primalpond.hunt.world.logic.TheHuntRenderer;

import android.util.Log;
import d3kod.graphics.extra.D3Maths;

//TODO: Refractor - make getNearest{Algae,Food}() method of the memory, abstract away the concept of nearest things as well
public class WorldModel {
	private static final String TAG = "WorldModel";
	private static final float INF = 100;
	private static final float LOUD_NOISE = 1f;
	private int hiddenForSafe = 0;
	private static final int HIDDEN_FOR_SAFE_ADJ = 20;
	private static final int HIDDEN_FOR_SAFE_MAX = 100;
	private static final int safeForCalm = 20;
	private static int safeFor = 0;
	
	private static final int ENERGY_DEPLETE_SPEED = 3;
	
	private static final int DESPAIR_ENERGY = 30;
	private static final int RISK_ENERGY = 60;
	private static final int MAX_ENERGY = 100;
	
	private static final int SECONDS_FOR_ENERGY_LOSS = 1;
	private static final int ENERGY_DEPLETE_TICKS = TheHuntRenderer.TICKS_PER_SECOND*SECONDS_FOR_ENERGY_LOSS;
	
	private static final int SECONDS_FOR_INCR_RISK = 2;
	private static final int INCR_RISK_TICKS = TheHuntRenderer.TICKS_PER_SECOND*SECONDS_FOR_INCR_RISK;
	public static final float MINIMUM_HIDING_ALGAE_RADIUS = D3Prey.preyRadius;
	
	private float mHeadX;
	private float mHeadY;
	private float mBodyY;
	private float mBodyX;
	private int mLightLevel;
	
	private ArrayList<MovingEvent> mEventMemory = new ArrayList<MovingEvent>();
	private MovingEvent mNearestFood;
	private MovingEvent mNearestAlgae;
	private Dir mCurrentDir;
	private float mHeadAngle;
	private StressLevel mStressLevel;
	private int mHiddenFor;
	private int mEnergy;
	private int energyDepleteCounter;
	private int incrRiskCounter;
	private MoodLevel mMoodLevel;
	private boolean mOverweight;
	private EventType[] mAlgaeType = {EventType.ALGAE};
	private EventType[] mFoodType = {EventType.FOOD, EventType.ALGAE};
	
	
	public WorldModel() {
		mNearestFood = null;
		mNearestAlgae = null;
		mStressLevel = StressLevel.CALM;
		mHiddenFor = 0;
		mEnergy = MAX_ENERGY;
		energyDepleteCounter = 0;
		incrRiskCounter = 0;
		mOverweight = false;
		mMoodLevel = MoodLevel.NEUTRAL;
		mCurrentDir = Dir.UNDEFINED;
	}
	public void update(ArrayList<Event> sensorEvents) {
		if (sensorEvents != null) {
			for (Event e: sensorEvents) {
				processEvent(e);
			}
		}
		// TODO: WTF SO LAME.. EVERY FUCKING TIME?!
		mNearestAlgae = recallClosest(mAlgaeType);

		
		if (energyDepleteCounter >= ENERGY_DEPLETE_TICKS) {
			energyDepleteCounter = 0;
			reduceEnergy(ENERGY_DEPLETE_SPEED);
		}
		else {
			energyDepleteCounter++;
		}
		
		if (mEnergy <= DESPAIR_ENERGY) {
			mMoodLevel = MoodLevel.DESPAIR;
		}
		else if (mEnergy <= RISK_ENERGY) {
			if (mStressLevel == StressLevel.CALM) {
				mStressLevel = StressLevel.CAUTIOS;
			}
			mMoodLevel = MoodLevel.RISK;
			incrRiskCounter++;
			if (incrRiskCounter >= INCR_RISK_TICKS) {
				incrRiskCounter = 0;
				increaseRisk();
			}
		}
		else {
			mMoodLevel = MoodLevel.NEUTRAL;
		}
		if (mMoodLevel == MoodLevel.NEUTRAL) {
			if (mStressLevel == StressLevel.CAUTIOS) {
				safeFor++;
				if (safeForCalm < safeFor) {
					mStressLevel = StressLevel.CALM;
				}
			}
		}
	}
	private void processEvent(Event e) {
		if (mEventMemory.contains(e)) {
			MovingEvent me = ((MovingEvent) e);
			if (knowFoodLocation() && mNearestFood.equals(me)) {
				mNearestFood.set(me);
			}
			else if (knowAlgaeLocation() && mNearestAlgae.equals(me)) {
				mNearestAlgae.set((EventAlgae) me);
				if (mNearestAlgae.getRadius() < MINIMUM_HIDING_ALGAE_RADIUS) {
					noAlgaeHere();
					Log.v(TAG, "Forgetting the current nearest algae because too small!");
					return; // otherwise will remove it again
				}
			}
			//TODO: This is lame
			mEventMemory.remove(me);
			mEventMemory.add(me);
			return;
		}
		switch(e.type()) { 
		case AT: 
			EventAt eAt = (EventAt) e;
			mHeadX = eAt.getHeadX(); mHeadY = eAt.getHeadY();
			mBodyX = eAt.getBodyX(); mBodyY = eAt.getBodyY();
			mHeadAngle = eAt.getHeadAngle();
			break;
		case FOOD: 
		case ALGAE:
			MovingEvent food = (MovingEvent)e;
			float foodX = food.getX();
			float foodY = food.getY();
			float foodRadius = food.getRadius();
			if (!knowFoodLocation() || 
					((EatableEvent)mNearestFood).getNutri() < ((EatableEvent)food).getNutri() ||
					((EatableEvent)mNearestFood).getNutri() == ((EatableEvent)food).getNutri() &&
					(D3Maths.distanceToCircle(mHeadX, mHeadY, mNearestFood.getX(), 
							mNearestFood.getY(), mNearestFood.getRadius()) >
					D3Maths.distanceToCircle(mHeadX, mHeadY, foodX, foodY, foodRadius))) {
				Log.i(TAG, "Setting nearest food to " + food + " " + ((EatableEvent)food).getNutri());
				mNearestFood = food; //TODO: make sure this updates the fish target as well
			}
				//TODO cache current food distance
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
				//Log.v(TAG, "Feeling safe, be cautios now");
				mStressLevel = StressLevel.CAUTIOS;
			}
			break;
		case CURRENT:
			mCurrentDir = ((EventCurrent) e).getDir();
			break;
		case NOISE:
			EventNoise noise = (EventNoise) e;
			Log.v(TAG, "DEBUGGG " + mMoodLevel + " " + noise);
			if (mMoodLevel.compareTo(MoodLevel.DESPAIR) < 0 && noise.getLoudness() >= LOUD_NOISE) {
				Log.v(TAG, "Loud noise heard, panic!");
				mStressLevel = StressLevel.PLOK_CLOSE;
				mHiddenFor = 0;
				decreaseRisk();
			}
		}
		if (e.type() == EventType.FOOD || e.type() == EventType.ALGAE) {
			if (e.type() == EventType.ALGAE && ((EventAlgae)e).getRadius() < MINIMUM_HIDING_ALGAE_RADIUS) {
				return;
			}
			rememberEvent((MovingEvent) e);
		}
	}

	private void rememberEvent(MovingEvent e) {
		mEventMemory.add(e);
	}
	
	private void increaseRisk() {
		hiddenForSafe -= HIDDEN_FOR_SAFE_ADJ;
		if (hiddenForSafe < 0) hiddenForSafe = 0;
		Log.v(TAG, "decr hiddenForSafe is now " + hiddenForSafe);
	}
	private void decreaseRisk() {
		if (hiddenForSafe < HIDDEN_FOR_SAFE_MAX) hiddenForSafe += HIDDEN_FOR_SAFE_ADJ;
		Log.v(TAG, "incr hiddenForSafe is now " + hiddenForSafe);
	}
	public int getLightLevel() {
		return mLightLevel;
	}
	public float getNearestFoodX() {
		return mNearestFood.getX();
	}
	public float getNearestFoodY() {
		return mNearestFood.getY();
	}
	public MovingEvent getNearestFood() {
		return mNearestFood;
	}
	public MovingEvent getNearestAlgae() {
		return mNearestAlgae;
	}
	public float getNearestAlgaeX() {
		return mNearestAlgae.getX();
	}
	public float getNearestAlgaeY() {
		return mNearestAlgae.getY();
	}
	public float getHeadX() {
		return mHeadX;
	}
	public float getHeadY() {
		return mHeadY;
	}
	public void eatFood(int energy) {
		//TODO: the food removed is not always the nearest food #BUG
		if (energy == 0) {
			Log.v(TAG, "Didn't eat anything... forget about this food");
			mEventMemory.remove(mNearestFood);
		}
		mEnergy += energy;
		//TODO: Do we need this?
		mNearestFood = recallClosest(mFoodType);
	}
	
	// assumes EventType elements in types are comparable
	private MovingEvent recallClosest(EventType[] types) {
		float x, y, radius;
		MovingEvent closest = null;
		for (MovingEvent e: mEventMemory) {
				if (!e.isOfType(types)) continue;
				if (closest == null) closest = e;
				else if (e.compare(closest, mHeadX, mHeadY) < 0) {
					closest = e;
				}
		}
		
		return closest;
	}
	
	public float getBodyX() {
		return mBodyX;
	}
	public float getBodyY() {
		return mBodyY;
	}
	public boolean knowFoodLocation() {
		return mNearestFood != null;
	}
	public boolean knowAlgaeLocation() {
		return mNearestAlgae != null;
	}
	public void recalcNearestFood() {
		mNearestFood = recallClosest(mFoodType);
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
	
	public MoodLevel getMoodLevel() {
		return mMoodLevel;
	}
	public void refillEnergy() {
		mEnergy = MAX_ENERGY;
	}
	public void noAlgaeHere() {
		mEventMemory.remove(mNearestAlgae);
		MovingEvent nearestAlgae = recallClosest(mAlgaeType);
		if (nearestAlgae == null) mNearestAlgae = null;
		else mNearestAlgae.set(nearestAlgae);
	}
	public boolean amOverweight() {
		return mOverweight;
	}
	public void reduceEnergy(int amount) {
		mEnergy -= amount;
		if (mEnergy > MAX_ENERGY) mOverweight = true;
		else {
			mOverweight = false;
			if (mEnergy < 0) mEnergy = 0;
		}
	}
	public void setStressLevel(StressLevel stressLevel) {
		mStressLevel = stressLevel;
	}
	public void setTargetFood(EatableEvent eatableEvent) {
		mNearestFood = eatableEvent;
	}
	public void setEnergy(int i) {
		mEnergy = i;
	}
}
