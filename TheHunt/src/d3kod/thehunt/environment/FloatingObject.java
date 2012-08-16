package d3kod.thehunt.environment;

import d3kod.d3gles20.D3GLES20;
import android.opengl.Matrix;

class FloatingObject {

	public enum Type {
		FOOD, ALGAE;
	}
	
	private int mKey;
//	private float[] mModelMatrix;
	Type mType;
	private float mX;
	private float mY;

	public FloatingObject(int key, float x, float y, Type type) {
		mKey = key; 
		mType = type;
//		mModelMatrix = new float[16];
//		Matrix.setIdentityM(mModelMatrix, 0);
//		Matrix.translateM(mModelMatrix, 0, x, y, 0);
		D3GLES20.setShapePosition(key, x, y);
		mX = x; mY = y;
	}

	public int getKey() {
		return mKey;
	}
//
//	public float[] getModelMatrix() {
//		return mModelMatrix;
//	}
//	
	public Type getType() {
		return mType;
	}
	
	public float getX() {
		return mX;
	}
	public float getY() {
		return mY;
	}

	public void clearGraphic() {
		D3GLES20.removeShape(mKey);		
	}
	
}