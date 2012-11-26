package d3kod.thehunt.environment;

import java.nio.FloatBuffer;

import android.opengl.GLES20;
import d3kod.d3gles20.D3Maths;
import d3kod.d3gles20.ShaderManager;
import d3kod.d3gles20.Utilities;
import d3kod.d3gles20.shapes.D3Shape;

public class D3FoodGM extends D3Shape {

	private static float[] foodColor = {0.2f, 0.2f, 0.2f, 1.0f};
	private static int drawType = GLES20.GL_LINE_LOOP;
	private static final float radius = 0.01f;
	
	protected D3FoodGM(ShaderManager sm) {
		super(foodColor, drawType, sm.getDefaultProgram());
		setVertexBuffer(makeVertexBuffer());
	}

	private FloatBuffer makeVertexBuffer() {
		return Utilities.newFloatBuffer(D3Maths.circleVerticesData(radius, 20));
	}

	@Override
	public float getRadius() {
		return radius;
	}

}
