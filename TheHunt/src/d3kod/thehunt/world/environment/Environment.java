package d3kod.thehunt.world.environment;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.PointF;
import android.util.Log;
import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.sprite.SpriteManager;
import d3kod.thehunt.agent.Agent;
import d3kod.thehunt.world.environment.FloatingObject.Type;
import d3kod.thehunt.world.events.EatableEvent;
import d3kod.thehunt.world.events.Event;
import d3kod.thehunt.world.events.EventCurrent;
import d3kod.thehunt.world.events.EventLight;
import d3kod.thehunt.world.events.EventNoise;
public class Environment {
	private static final String TAG = "Environment";
	public static final float LOUDNESS_PLOK = 1f;
	private static final int NOISE_EVENTS_CLEAR_FREQUENCY = 2;
	public EnvironmentData data;
//	public int mTextureDataHandle;
	transient private SpriteManager mD3GLES20;
	private ArrayList<EventNoise> mNoiseEvents;
	private Random mRandom;
	private Agent mPrey;
	
	
	public static final int ALGAE_NUM = 300;
	private static final float NET_INTERSECT_RAD_ADJ = 0.2f;
	
	public Environment(int width, int height, SpriteManager d3gles20) {
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
		if (mD3GLES20 == null) {
			Log.e(TAG, "D3gles20 is null on env update!");
		}
		data.updateFloatingObjects();

	}
	public void recalculateCurrents() {
	}
	
	public Tile[][] getTiles() {
		return data.mTiles;
	}
	public void putFoodGM(float x, float y) {
		data.addFloatingObject(new FoodGM(x, y, mD3GLES20));
	}
	
	public Event senseCurrent(float x, float y) {
		Dir tileDir = data.getTileFromPos(new PointF(x, y)).getDir();
		return new EventCurrent(tileDir);
	}
	
	public int eatFood(float x, float y) {
		for (FloatingObject fo: data.getFloatingObjects()) {
//			Log.v(TAG, "Eat food check fo type is " + fo.getType());
			if (fo.getType().compareTo(Type.ALGAE) != 0 
					&& fo.getType().compareTo(Type.FOOD_GM) != 0) {
//				Log.v(TAG, "Eat food check fo type is " + fo.getType());
//				Log.v(TAG, "Eat food check fo type is ")
//				Log.v(TAG, "" + fo.getType().compareTo(Type.ALGAE));
				continue;
			}
			float foX = fo.getX(), foY = fo.getY();
//			Log.v(TAG, "Eat food circle contains check!");
			if (D3Maths.circleContains(x, y, Math.max(fo.getRadius(), EatableEvent.MIN_RADIUS), foX, foY)) {
//				Log.v(TAG, "Eat food check passed!");
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
			if (fo.getType().compareTo(Type.ALGAE) == 0 && fo.contains(at)) {
				return new EventLight(0);
			}
		}
		return new EventLight(1);
	}
	public boolean netObstacle(float x, float y) {
		PointF at = new PointF(x, y);
		for (FloatingObject fo: data.getFloatingObjects()) {
			if (fo.getType().compareTo(Type.ALGAE) == 0 && fo.contains(at)) 
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
//		Log.v(TAG, "Net intersects test started with " + data.getFloatingObjects().size() + " fo");
		for (FloatingObject fo: data.getFloatingObjects()) {
//			Log.v(TAG, "Net intersects test fo radius is " + fo.getRadius());
			if (fo.getType().compareTo(Type.ALGAE) == 0 &&
					D3Maths.circlesIntersect(centerX, centerY, radius - NET_INTERSECT_RAD_ADJ, fo.getX(), fo.getY(), fo.getRadius())) {
				return true;
			}
		}
		return false;
	}

	public void addPrey(Agent prey) {
		mPrey = prey;
	}
	public Agent getPrey() {
		if (mPrey.getEnvironment() != this) {
			Log.v(TAG, "Adjusting prey environment");
			mPrey.setEnvironment(this);
		}
		return mPrey;
	}

	public void initGraphics(SpriteManager mD3GLES202) {
		mD3GLES20 = mD3GLES202;
		data.setGraphics(mD3GLES20);
		Log.v(TAG, "Environment graphics initialized! " + mD3GLES20);
	}

	public SpriteManager getSpriteManager() {
		return mD3GLES20;
	}

	
	
	// STORABLE_D3Sprite
	
	
//	public void initSprites(ArrayList<D3Sprite> sprites) {
//		for (D3Sprite sprite: sprites) {
//			if (sprite instanceof FloatingObject) {
//				sprite.
//				data.addFloatingObject((FloatingObject)sprite);
//			}
//		}
//	}
//
//	public ArrayList<D3Sprite> getSprites() {
//		ArrayList<D3Sprite> sprites = new ArrayList<D3Sprite>();
//		for (FloatingObject fo: data.getFloatingObjects()) {
//			fo.clearGraphic();
//			sprites.add((D3Sprite)fo);
//		}
////		return data.getFloatingObjects();
//		return sprites;
//	}
}
