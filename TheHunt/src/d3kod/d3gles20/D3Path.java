package d3kod.d3gles20;

import java.util.ArrayList;

import android.opengl.GLES20;
import android.util.Log;

public class D3Path extends D3Shape {

	private static final String TAG = "D3Path";
	private static int beingBuiltType = GLES20.GL_LINE_STRIP;
	private static int isBuiltType = GLES20.GL_LINE_LOOP;
	
	public D3Path(float[] beingBuiltColor) {
		super(0, null, beingBuiltColor, beingBuiltType, true);
	}
	
	@Override
	public float getRadius() {
		Log.v(TAG, "Unimplemented getRadius call!");
		return 0;
	}

	public void makeVertexBuffer(ArrayList<Float> pVertexData) {
		float[] mVertexData = new float[pVertexData.size() + pVertexData.size()/2];
//		pVertexData.toArray(mVertexData);
		int ind = 0;
		for (Float f:pVertexData) {
			mVertexData[ind++] = f;
			if (ind%3==1) mVertexData[ind++] = 0;
		}
		super.setVertexBuffer(D3GLES20.newFloatBuffer(mVertexData));
	}

	public void isFinished() {
		super.setDrawType(isBuiltType);
	}

}
