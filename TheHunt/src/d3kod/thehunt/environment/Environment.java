package d3kod.thehunt.environment;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;
import d3kod.d3gles20.D3GLES20;
import d3kod.d3gles20.D3Maths;
import d3kod.d3gles20.TextureHelper;
import d3kod.thehunt.R;
import d3kod.thehunt.environment.FloatingObject.Type;
import d3kod.thehunt.events.Event;
import d3kod.thehunt.events.EventCurrent;
import d3kod.thehunt.events.EventLight;
import d3kod.thehunt.events.EventNoise;
public class Environment {
	private static final String TAG = "Environment";
	public static final float LOUDNESS_PLOK = 1f;
	private static final int NOISE_EVENTS_CLEAR_FREQUENCY = 2;
	public EnvironmentData data;
	private float[] foodColor = {0.5f, 0.5f, 0.5f, 1.0f};
	public int mTextureDataHandle;
	private D3GLES20 mD3GLES20;
	private ArrayList<EventNoise> mNoiseEvents;
	
	public Environment(int width, int height) {
		data = new EnvironmentData(width, height);
		mNoiseEvents = new ArrayList<EventNoise>();
	}
	
	public void update() {
		data.updateFloatingObjects();
	}
	
	public void initGraphics(Context context, D3GLES20 d3GLES20) {
		mD3GLES20 = d3GLES20;
		mTextureDataHandle = TextureHelper.loadTexture(context, R.drawable.hatching_cross);
		data.makeAlgae(mTextureDataHandle, mD3GLES20);
//		Tile.initBuffers();
		
	}
	public void recalculateCurrents() {
	}
	
	public Tile[][] getTiles() {
		return data.mTiles;
	}
	public void putFood(float x, float y) {
//		Log.v(TAG, "Putting food at " + x +  " " + y);
		data.addFloatingObject(new FloatingObject(x, y, Type.FOOD, mD3GLES20), mD3GLES20.newDefaultCircle(0.01f, foodColor , 20));
	}
	
	public Event senseCurrent(float x, float y) {
		Dir tileDir = data.getTileFromPos(new PointF(x, y)).getDir();
		return new EventCurrent(tileDir);
	}
	
	public void eatFood(float mPosHeadX, float mPosHeadY) {
		data.removeFood(mPosHeadX, mPosHeadY);
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
		for (FloatingObject fo: data.getFloatingObjects()) {
			if (fo.getType() == Type.ALGAE && mD3GLES20.contains(fo.getKey(), hX, hY)) {
				return new EventLight(0);
			}
		}
		return new EventLight(1);
	}
	public boolean netObstacle(float x, float y) {
		for (FloatingObject fo: data.getFloatingObjects()) {
			if (fo.getType() == Type.ALGAE && mD3GLES20.contains(fo.getKey(), x, y)) 
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

}
