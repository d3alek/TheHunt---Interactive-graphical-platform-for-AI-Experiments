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
import d3kod.thehunt.events.EventLight;
import d3kod.thehunt.events.EventNoise;
import d3kod.thehunt.events.MovingEvent;


//TODO: Refractor - make getNearest{Algae,Food}() method of the memory, abstract away the concept of nearest things as well
public class WorldModel {
	private static final String TAG = "WorldModel";
	private static final float INF = 100;
	private static final float LOUD_NOISE = 1f;
	private int hiddenForSafe = 0;
	private static final int HIDDEN_FOR_SAFE_ADJ = 20;
	private static final int HIDDEN_FOR_SAFE_MAX = 100;
	
	private static final int ENERGY_DEPLETE_SPEED = 3;
	
	private static final int DESPAIR_ENERGY = 30;
	private static final int RISK_ENERGY = 60;
	private static final int MAX_ENERGY = 100;
	
	private static final int SECONDS_FOR_ENERGY_LOSS = 1;
	private static final int ENERGY_DEPLETE_TICKS = TheHuntRenderer.TICKS_PER_SECOND*SECONDS_FOR_ENERGY_LOSS;
	
	private static final int SECONDS_FOR_INCR_RISK = 2;
	private static final int INCR_RISK_TICKS = TheHuntRenderer.TICKS_PER_SECOND*SECONDS_FOR_INCR_RISK;
	public static final int MINIMUM_HIDING_ALGAE_SIZE = 10;
	
	//MemoryGraph mNodes;
	private float mHeadX;
	private float mHeadY;
	private float mBodyY;
	private float mBodyX;
	private int mLightLevel;
	
	private ArrayList<MovingEvent> mEventMemory = new ArrayList<MovingEvent>();
	private MovingEvent mNearestFood;
	private EventAlgae mNearestAlgae;
	private Dir mCurrentDir;
	private float mHeadAngle;
	private StressLevel mStressLevel;
	private int mHiddenFor;
	private int mEnergy;
	private int energyDepleteCounter;
//	private boolean mPanic;
	private int incrRiskCounter;
	private MoodLevel mMoodLevel;
	private boolean mOverweight;
	
	
	public WorldModel(float screenWidth, float screenHeight) {
		mNearestFood = null;
		mNearestAlgae = null;
		mStressLevel = StressLevel.CALM;
		mHiddenFor = 0;
		mEnergy = MAX_ENERGY;
		energyDepleteCounter = 0;
		incrRiskCounter = 0;
		mOverweight = false;
	}
	public void update(ArrayList<Event> sensorEvents) {
		for (Event e: sensorEvents) {
			processEvent(e);
		}
		// TODO: WTF SO LAME.. EVERY FUCKING TIME?!
		mNearestAlgae = recallNearestAlgae();

		
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
	}
	private void processEvent(Event e) {
		if (mEventMemory.contains(e)) {
			MovingEvent me = ((MovingEvent) e);
			if (knowFoodLocation() && mNearestFood.equals(me)) {
				mNearestFood.set(me);
			}
			else if (knowAlgaeLocation() && mNearestAlgae.equals(me)) {
				mNearestAlgae.set((EventAlgae) me);
				if (mNearestAlgae.getSize() < MINIMUM_HIDING_ALGAE_SIZE) {
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
			if (!knowFoodLocation() ||
					D3Maths.distance(mHeadX, mHeadY, mNearestFood.getX(), mNearestFood.getY()) > 
					D3Maths.distance(mHeadX, mHeadY, foodX, foodY)) {
				mNearestFood = food; //TODO: make sure this updates the fish target as well
				//TODO cache current food distance
			}
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
			if (mMoodLevel.compareTo(MoodLevel.DESPAIR) < 0 && noise.getLoudness() >= LOUD_NOISE) {
				Log.v(TAG, "Loud noise heard, panic!");
				mStressLevel = StressLevel.PLOK_CLOSE;
				mHiddenFor = 0;
				decreaseRisk();
			}
		}
		if (e.type() == EventType.FOOD || e.type() == EventType.ALGAE) {
			if (e.type() == EventType.ALGAE && ((EventAlgae)e).getSize() < MINIMUM_HIDING_ALGAE_SIZE) {
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
	public EventAlgae getNearestAlgae() {
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
//		if (mEnergy > MAX_ENERGY) {
//			mEnergy = MAX_ENERGY;
//		}
		//TODO: Do we need this?
		mNearestFood = recallNearestFood();
	}
	private MovingEvent recallNearestFood() {
		float closestX = INF, closestY = INF;
		MovingEvent closestFood = null;
		
		for (MovingEvent e: mEventMemory) {
//			if (e.type() == EventType.FOOD) {
//				EventFood ef = (EventFood)e;
				float foodX = e.getX();
				float foodY = e.getY();
				if (D3Maths.distance(mHeadX, mHeadY, foodX, foodY) <
						D3Maths.distance(mHeadX, mHeadY, closestX, closestY)) {
					closestFood = e;
					closestX = foodX; 
					closestY = foodY;
				}
//			}
		}
		
		return closestFood;
	}
	
	private EventAlgae recallNearestAlgae() {
		float closestX = INF, closestY = INF;
		EventAlgae closestAlgae = null;
		
		for (Event e: mEventMemory) {
			if (e.type() == EventType.ALGAE) {
				EventAlgae ea = (EventAlgae)e;
				float foodX = ea.getX();
				float foodY = ea.getY();
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
	public boolean knowFoodLocation() {
		return mNearestFood != null;
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
	
	public MoodLevel getMoodLevel() {
		return mMoodLevel;
	}
	public void refillEnergy() {
		mEnergy = MAX_ENERGY;
	}
	public void noAlgaeHere() {
		mEventMemory.remove(mNearestAlgae);
		EventAlgae nearestAlgae = recallNearestAlgae();
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
}
