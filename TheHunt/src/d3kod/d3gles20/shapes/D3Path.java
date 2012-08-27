package d3kod.d3gles20.shapes;

import java.util.ArrayList;

import android.opengl.GLES20;
import d3kod.d3gles20.D3GLES20;
import d3kod.d3gles20.D3Maths;
import d3kod.d3gles20.Program;
import d3kod.d3gles20.Utilities;

public class D3Path extends D3Shape {

	private static final String TAG = "D3Path";
	private static final int drawType = GLES20.GL_LINE_STRIP;
	
	protected ArrayList<Float> mVertexData = new ArrayList<Float>();
	private float mLength;
	
	public D3Path(float[] beingBuiltColor, Program program) {
		super(null, beingBuiltColor, drawType, program);
		mLength = 0;
	}

	public void makeVertexBuffer() {
		if (mVertexData.isEmpty()) return;
		float[] vertexData = new float[mVertexData.size()];
		for (int i = 0; i < mVertexData.size(); ++i) {
			vertexData[i] = mVertexData.get(i);
		}
//		Log.v(TAG, "Setting vertex buffer");
		super.setVertexBuffer(Utilities.newFloatBuffer(vertexData));
	}

	@Override
	public float getCenterX() {
		throw new UnsupportedOperationException();
	}

	@Override
	public float getCenterY() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public float getRadius() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void draw(float[] mVMatrix, float[] mProjMatrix) {
		if (mVertexData.size()/D3GLES20.COORDS_PER_VERTEX > getVerticesNum()) makeVertexBuffer();
		super.draw(mVMatrix, mProjMatrix);
	}
	
	public void addVertex(float x, float y) {
		int lastXIndex = mVertexData.size()-3, lastYIndex = lastXIndex+1;
		if (lastYIndex > 0) {
			mLength += D3Maths.distance(x, y, mVertexData.get(lastXIndex), mVertexData.get(lastYIndex));
		}
		
		mVertexData.add(x);
		mVertexData.add(y);
		mVertexData.add(0f);
	}
	public float getLength() {		
		return mLength;
	}
}
