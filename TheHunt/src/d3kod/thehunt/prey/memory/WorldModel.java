package d3kod.thehunt.prey.memory;

import java.util.ArrayList;

import android.util.Log;
import d3kod.d3gles20.D3Maths;
import d3kod.thehunt.events.Event;
import d3kod.thehunt.events.EventAlgae;
import d3kod.thehunt.events.EventAt;
import d3kod.thehunt.events.EventFood;
import d3kod.thehunt.events.EventLight;
import d3kod.thehunt.events.Event.EventType;

public class WorldModel {
private static final String TAG = "WorldModel";
private static final float INF = 100;
	//MemoryGraph mNodes;
	private float mHeadX;
	private float mHeadY;
	private float mBodyY;
	private float mBodyX;
	private int mLightLevel;
	
	private ArrayList<Event> mEventMemory = new ArrayList<Event>();
	private EventFood mNearestFood;
	private EventAlgae mNearestAlgae;
//	public void updateNode(float posX, float posY, float currentX, float currentY) {
//		mNodes.getNode(posX, posY).setCurrent(currentX, currentY);
//	}
	public WorldModel(float screenWidth, float screenHeight) {
//		mNodes = new MemoryGraph(screenWidth, screenHeight);
//		mNearestFoodX = mNearestFoodY = -1;
		mNearestFood = null;
		mNearestAlgae = null;
	}
	public void update(ArrayList<Event> sensorEvents) {
		for (Event e: sensorEvents) {
			processEvent(e);
		}
		mNearestAlgae = recallNearestAlgae();
	}
	private void processEvent(Event e) {
		if (mEventMemory.contains(e)) return;
		switch(e.type()) { 
		case AT: 
			float newBodyX = ((EventAt) e).getBodyX();
			float newBodyY = ((EventAt) e).getBodyY();
			float newHeadX = ((EventAt) e).getHeadX();
			float newHeadY = ((EventAt) e).getHeadY();
			if (newHeadX != mHeadX || newHeadY != mHeadY) {
				// TODO: There is a movement! Improve detection?
				mHeadX = newHeadX; mHeadY = newHeadY;
				mBodyX = newBodyX; mBodyY = newBodyY;
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
//			EventAlgae algae = (EventAlgae)e;
//			float algaeX = algae.getAlgaeX();
//			float algaeY = algae.getAlgaeY();
//			if (!knowAlgaeLocation() || 
//					D3GLES20.distance(mHeadX, mHeadY, mNearestAlgae.getAlgaeX(), mNearestAlgae.getAlgaeY()) > 
//					D3GLES20.distance(mHeadX, mHeadY, algaeX, algaeY)) {
//				mNearestAlgae = algae;
//			}
			break;
		case LIGHT:
			mLightLevel = ((EventLight) e).getLightLevel();
			break;
		}
		if (e.type() == EventType.FOOD || e.type() == EventType.ALGAE) {
			rememberEvent(e);
		}
	}
	private void rememberEvent(Event e) {
//		if (eventMemory.contains(e)) return;
//		Log.v(TAG, "Remembering event " + e);
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
}
