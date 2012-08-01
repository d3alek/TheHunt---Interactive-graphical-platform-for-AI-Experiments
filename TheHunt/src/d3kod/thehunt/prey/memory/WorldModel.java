package d3kod.thehunt.prey.memory;

import java.util.ArrayList;

import d3kod.thehunt.environment.EnvironmentData;
import d3kod.thehunt.environment.LightEvent;
import d3kod.thehunt.prey.sensor.Event;
import d3kod.thehunt.prey.sensor.EventAlgae;
import d3kod.thehunt.prey.sensor.EventAt;
import d3kod.thehunt.prey.sensor.EventFood;

import android.util.Log;

public class WorldModel {
private static final String TAG = "WorldModel";
	MemoryGraph mNodes;
	private float mHeadX;
	private float mHeadY;
	private float mBodyY;
	private float mBodyX;
	private float mFoodX;
	private float mFoodY;
	private float mAlgaeX;
	private float mAlgaeY;
	private int mLightLevel;
	
	public void updateNode(float posX, float posY, float currentX, float currentY) {
		mNodes.getNode(posX, posY).setCurrent(currentX, currentY);
	}
	public WorldModel(float screenWidth, float screenHeight) {
		mNodes = new MemoryGraph(screenWidth, screenHeight);
		mFoodX = mFoodY = -1;
		mAlgaeX = mAlgaeY = -1;
	}
	public void update(ArrayList<Event> sensorEvents) {
		for (Event e: sensorEvents) {
			processEvent(e);
		}
	}
	private void processEvent(Event e) {
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
			mFoodX = ((EventFood) e).getFoodX();
			mFoodY = ((EventFood) e).getFoodY(); 
			break;
		case ALGAE:
			mAlgaeX = ((EventAlgae) e).getAlgaeX();
			mAlgaeY = ((EventAlgae) e).getAlgaeY();
			break;
		case LIGHT:
			mLightLevel = ((LightEvent) e).getLightLevel();
			break;
		}
	}
	public int getLightLevel() {
		return mLightLevel;
	}
	public boolean knowFoodLocation() {
		return (mFoodX != -1 && mFoodY != -1);
	}
	public float getFoodX() {
		return mFoodX;
	}
	public float getFoodY() {
		return mFoodY;
	}
	public float getAlgaeX() {
		return mAlgaeX;
	}
	public float getAlgaeY() {
		return mAlgaeY;
	}
	public float getHeadX() {
		return mHeadX;
	}
	public float getHeadY() {
		return mHeadY;
	}
	public void eatFood(float mPosHeadX, float mPosHeadY) {
		//TODO: improve functionality
		mFoodX = mFoodY = -1;
	}
	public float getBodyX() {
		return mBodyX;
	}
	public float getBodyY() {
		return mBodyY;
	}
	public boolean knowAlgaeLocation() {
		return (mAlgaeX != -1 && mAlgaeY != -1);
	}
}
