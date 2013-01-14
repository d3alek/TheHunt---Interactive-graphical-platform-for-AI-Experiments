package d3kod.thehunt.environment;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.PointF;
import d3kod.d3gles20.D3GLES20;
import d3kod.d3gles20.D3Maths;
import d3kod.thehunt.environment.FloatingObject.Type;
import d3kod.thehunt.events.EatableEvent;
import d3kod.thehunt.events.Event;
import d3kod.thehunt.events.EventCurrent;
import d3kod.thehunt.events.EventLight;
import d3kod.thehunt.events.EventNoise;
import d3kod.thehunt.prey.D3Prey;
import d3kod.thehunt.prey.Prey;
public class Environment {
	private static final String TAG = "Environment";
	public static final float LOUDNESS_PLOK = 1f;
	private static final int NOISE_EVENTS_CLEAR_FREQUENCY = 2;
	public EnvironmentData data;
	public int mTextureDataHandle;
	private D3GLES20 mD3GLES20;
	private ArrayList<EventNoise> mNoiseEvents;
	private Random mRandom;
	
	
	public static final int ALGAE_NUM = 300;
//	private static final float ALGAE_DROP_FOOD_CHANCE = 0.015f;
	private static final float NET_INTERSECT_RAD_ADJ = 0.2f;
	
	public Environment(int width, int height, D3GLES20 d3gles20) {
		mD3GLES20 = d3gles20;
		data = new EnvironmentData(width, height);
		data.setGraphics(mD3GLES20);
		mNoiseEvents = new ArrayList<EventNoise>();
		mRandom = new Random();
		seedAlgae();
	}
	
	public void addNewAlgae(int n, PointF pos, float dirAngle) {
		data.addFloatingObject(new NAlgae(n, pos, dirAngle, this, mD3GLES20));
	}
	
	private void seedAlgae() {
		for (int i = 0; i < ALGAE_NUM; ++i) {
			addNewAlgae(1, randomPosInEnv(), D3Maths.getRandAngle());
		}
	}

	public PointF randomPosInEnv() {
		return new PointF(
				-data.mScreenWidth/2 + mRandom.nextFloat()*data.mScreenWidth, 
				-data.mScreenHeight/2 + mRandom.nextFloat()*data.mScreenHeight);
	}
	
	public void update() {
		data.updateFloatingObjects();

	}
	public void recalculateCurrents() {
	}
	
	public Tile[][] getTiles() {
		return data.mTiles;
	}
	public void putFoodGM(float x, float y) {
//		Log.v(TAG, "Putting food at " + x +  " " + y);
//		data.addFloatingObject(new FloatingObject(x, y, Type.FOOD, mD3GLES20), mD3GLES20.newDefaultCircle(0.01f, foodColor , 20));
		data.addFloatingObject(new FoodGM(x, y, mD3GLES20));
	}
	
//	public void putFoodAlgae(float x, float y) {
//		Log.v(TAG, "Putting food algae!");
//		data.addFloatingObject(new FoodAlgae(x, y, mD3GLES20));
//	}
	
	public Event senseCurrent(float x, float y) {
		Dir tileDir = data.getTileFromPos(new PointF(x, y)).getDir();
		return new EventCurrent(tileDir);
	}
	
	public int eatFood(float x, float y) {
		for (FloatingObject fo: data.getFloatingObjects()) {
			if (fo.getType() != Type.ALGAE && fo.getType() != Type.FOOD_GM) continue;
			float foX = fo.getX(), foY = fo.getY();
			if (D3Maths.circleContains(x, y, Math.max(fo.getRadius(), EatableEvent.MIN_RADIUS), foX, foY)) {
//			if (mD3GLES20.shapesCollide(fo.getGraphic(), prey.getGraphic())) {
				Eatable eatable = (Eatable) fo;
				eatable.processBite();
				return eatable.getNutrition();
			}
		}
		return 0;
	}
	
	public ArrayList<FloatingObject> seeObjects(float x, float y, float sightRadius) {
		ArrayList<FloatingObject> sensedObjects = new ArrayList<FloatingObject>();
		
		for (FloatingObject fo: data.getFloatingObjects()) {
			if (D3Maths.circleContains(x, y, sightRadius, fo.getX(), fo.getY())) {
				sensedObjects.add(fo);
			}
		}
		
		return sensedObjects;
	}
	
	public Event senseLight(float hX, float hY) {
		PointF at = new PointF(hX, hY);
		for (FloatingObject fo: data.getFloatingObjects()) {
			if (fo.getType() == Type.ALGAE && fo.contains(at)) {
				return new EventLight(0);
			}
		}
		return new EventLight(1);
	}
	public boolean netObstacle(float x, float y) {
		PointF at = new PointF(x, y);
		for (FloatingObject fo: data.getFloatingObjects()) {
			if (fo.getType() == Type.ALGAE && fo.contains(at)) 
				return true;
		}
		return false;
	}

	public ArrayList<Event> hearEvents(float hX, float hY, float hearRadius) {
		ArrayList<Event> events = new ArrayList<Event>();
		for (EventNoise noise: mNoiseEvents) {
			if (D3Maths.circlesIntersect(hX, hY, hearRadius, 
					noise.getX(), noise.getY(), noise.getLoudness())) {
				events.add(noise);
			}
		}
		mNoiseEvents.clear();
		return events;
	}

	public void putNoise(float x, float y, float loudness) {
		mNoiseEvents.add(new EventNoise(x, y, loudness));
	}

	public boolean netIntersectsWithAlgae(float centerX, float centerY,
			float radius) {
		for (FloatingObject fo: data.getFloatingObjects()) {
			if (fo.getType() == Type.ALGAE &&
					D3Maths.circlesIntersect(centerX, centerY, radius - NET_INTERSECT_RAD_ADJ, fo.getX(), fo.getY(), fo.getRadius())) {
				return true;
			}
		}
		return false;
	}

//	public void putNewAlgae(float x, float y) {
//		data.getFloatingObjectsToAdd().add(new Algae(x, y, mD3GLES20, this));
//	}
}
