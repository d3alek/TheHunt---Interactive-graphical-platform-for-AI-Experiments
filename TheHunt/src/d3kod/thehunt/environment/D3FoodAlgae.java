package d3kod.thehunt.environment;

import java.nio.FloatBuffer;

import android.opengl.GLES20;
import d3kod.d3gles20.D3Maths;
import d3kod.d3gles20.ShaderManager;
import d3kod.d3gles20.Utilities;
import d3kod.d3gles20.shapes.D3Shape;

public class D3FoodAlgae extends D3Shape {

	private static float[] foodColor = {0.5f, 0.5f, 0.5f, 1.0f};
	private static int drawType = GLES20.GL_LINE_LOOP;
	
	protected D3FoodAlgae(ShaderManager sm) {
//		super(foodColor, drawType, sm.getDefaultProgram());
		super();
		super.setColor(foodColor);
		super.setDrawType(GLES20.GL_LINE_LOOP);
		setVertexBuffer(makeVertexBuffer());
	}

	private FloatBuffer makeVertexBuffer() {
		return Utilities.newFloatBuffer(D3Maths.circleVerticesData(0.005f, 10));
	}

	@Override
	public float getRadius() {
		throw new UnsupportedOperationException();
	}
	
//	@Override
//	public void draw(float[] mVMatrix, float[] mProjMatrix, float interpolation) {
//		super.draw(mVMatrix, mProjMatrix, interpolation);
//	}
}
