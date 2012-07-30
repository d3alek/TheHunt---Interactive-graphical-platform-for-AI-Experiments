package d3kod.thehunt.environment;

import android.graphics.PointF;
import android.opengl.Matrix;
import android.util.Log;
import d3kod.d3gles20.D3GLES20;
import d3kod.thehunt.environment.FloatingObject.Type;
import d3kod.thehunt.prey.sensor.Event;
import d3kod.thehunt.prey.sensor.Event.EventType;
public class Environment {
	private static final String TAG = "Environment";
	public EnvironmentData data;
	private float[] foodColor = {0.5f, 0.5f, 0.0f};
	
	public Environment() {
		data = new EnvironmentData();
//		data.createTiles();
////		data.currents.initialize();
//		data.mFloatingObjects = new Algae(EnvironmentData.tWidth, EnvironmentData.tHeight);
//		data.mFloatingObjects.setPosition(data.mFrame.centerX(), data.mFrame.centerY());
	}
//	public Drawable getObjects() {
//		return data.mFloatingObjects.getDrawable();
//	}
	public void recalculateCurrents() {
		data.createTiles();
		data.currents.initialize();
	}
	
//	public void update() {
//		Tile objTile = getTileFromPos(data.mFloatingObjects.getPosition());
//		data.mFloatingObjects.update(objTile.getDir().getDelta());
//	}
	public void setSize(int width, int height) {
		data.setSize(width, height);
		data.createTiles();
	}
	public Tile[][] getTiles() {
		return data.mTiles;
	}
	public void putFood(float x, float y) {
		Log.v(TAG, "Putting food at " + x +  " " + y);
		//D3GLES20.circleVerticesData(new float[] {x, y}, 0.5f, 20);
//		float[] modelMatrix = new float[16];
//		Matrix.setIdentityM(modelMatrix, 0);
//		Matrix.translateM(modelMatrix, 0, x, y, 0);
		data.addFloatingObject(new FloatingObject(D3GLES20.newDefaultCircle(0.01f, foodColor , 20), x, y, Type.FOOD));
//		data.newFood(x, y);
	}
	public void draw(float[] mVMatrix, float[] mProjMatrix, float interpolation) {
		for (FloatingObject fo: data.getFloatingObjects()) {
			D3GLES20.draw(fo.getIndex(), fo.getModelMatrix(), mVMatrix, mProjMatrix);
		}
	}
	public Event senseCurrent(float x, float y) {
		return data.senseCurrent(x, y);
	}
	public Event senseFood(float x, float y) {
		return data.senseFood(x, y);
	}
	public void eatFood(float mPosHeadX, float mPosHeadY) {
		data.removeFood(mPosHeadX, mPosHeadY);
	}
}
