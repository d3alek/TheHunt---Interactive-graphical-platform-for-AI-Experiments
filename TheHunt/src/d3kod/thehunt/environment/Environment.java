package d3kod.thehunt.environment;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.PointF;
import d3kod.d3gles20.D3GLES20;
import d3kod.d3gles20.TextureHelper;
import d3kod.thehunt.R;
import d3kod.thehunt.environment.FloatingObject.Type;
import d3kod.thehunt.events.Event;
import d3kod.thehunt.events.EventAlgae;
import d3kod.thehunt.events.EventFood;
import d3kod.thehunt.events.EventLight;
import d3kod.thehunt.events.EventNone;
public class Environment {
	private static final String TAG = "Environment";
	public EnvironmentData data;
	private float[] foodColor = {0.5f, 0.5f, 0.5f};
	public int mTextureDataHandle;
	
	public Environment(int width, int height) {
		data = new EnvironmentData(width, height);
	}
	
	public void update() {
		data.updateFloatingObjects();
	}
	
	public void initGraphics(Context context) {
		mTextureDataHandle = TextureHelper.loadTexture(context, R.drawable.bumpy_bricks_public_domain);
		data.makeAlgae(mTextureDataHandle);
		Tile.initBuffers();
		
	}
	public void recalculateCurrents() {
	}
	
	public Tile[][] getTiles() {
		return data.mTiles;
	}
	public void putFood(float x, float y) {
//		Log.v(TAG, "Putting food at " + x +  " " + y);
		data.addFloatingObject(new FloatingObject(x, y, Type.FOOD), D3GLES20.newDefaultCircle(0.01f, foodColor , 20));
	}
//	public void draw(float[] mVMatrix, float[] mProjMatrix, float interpolation) {
////		data.logFloatingObjects();
//		for (FloatingObject fo: data.getFloatingObjects()) {
//			D3GLES20.draw(fo.getIndex(), fo.getModelMatrix(), mVMatrix, mProjMatrix);
//		}
//	}
	public Event senseCurrent(float x, float y) {
		Tile tile = data.getTileFromPos(new PointF(x, y));
		return new EventNone();
	}
	public ArrayList<Event> senseFood(float x, float y) {
		ArrayList<Event> sensedFood = new ArrayList<Event>();
		for (FloatingObject fo: data.getFloatingObjects()) {
			if (fo.getType() == Type.FOOD) sensedFood.add(new EventFood(fo.getX(), fo.getY()));
		}
		return sensedFood;
	}
	public void eatFood(float mPosHeadX, float mPosHeadY) {
		data.removeFood(mPosHeadX, mPosHeadY);
	}
	public ArrayList<Event> senseAlgae() {
		ArrayList<Event> sensedAlgae = new ArrayList<Event>();
		for (FloatingObject fo: data.getFloatingObjects()) {
			if (fo.getType() == Type.ALGAE) sensedAlgae.add(new EventAlgae(fo.getX(), fo.getY()));
		}
		return sensedAlgae;
	}
	public Event senseLight(float hX, float hY) {
		for (FloatingObject fo: data.getFloatingObjects()) {
			if (fo.getType() == Type.ALGAE && D3GLES20.contains(fo.getKey(), hX, hY)) 
				return new EventLight(0);
		}
		return new EventLight(1);
	}
	public boolean netObstacle(float x, float y) {
		for (FloatingObject fo: data.getFloatingObjects()) {
			if (fo.getType() == Type.ALGAE && D3GLES20.contains(fo.getKey(), x, y)) 
				return true;
		}
		return false;
	}

//	public void clean() {
//		data.clear();
//	}
}
