package d3kod.thehunt.environment;

import android.opengl.Matrix;

class FloatingObject {

	public enum Type {
		FOOD;
	}
	
	private int mIndex;
	private float[] mModelMatrix;
	Type mType;
	private float mX;
	private float mY;

	public FloatingObject(int index, float x, float y, Type type) {
		mIndex = index; 
		mType = type;
		mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, x, y, 0);
		mX = x; mY = y;
	}

	public int getIndex() {
		return mIndex;
	}

	public float[] getModelMatrix() {
		return mModelMatrix;
	}
	
	public Type getType() {
		return mType;
	}
	
	public float getX() {
		return mX;
	}
	public float getY() {
		return mY;
	}
	
}