package d3kod.thehunt.environment;

import java.nio.FloatBuffer;

import android.opengl.GLES20;
import d3kod.d3gles20.D3GLES20;
import d3kod.d3gles20.D3Shape;

public class D3Algae extends D3Shape {

	private static int drawType = GLES20.GL_LINE_LOOP;
	private static final float[] algaeColor = 
		{ 0.4f, 0.4f, 0.4f, 0.0f};

	private static final int ALGAE_DETAILS = 100;

	private static final float ALGAE_SIZE = 0.3f;
	
	protected D3Algae() {
		super(makeVerticesBuffer(), algaeColor, drawType, true);
	}

	private static FloatBuffer makeVerticesBuffer() {
		return D3GLES20.newFloatBuffer(D3GLES20.circleVerticesData(ALGAE_SIZE, ALGAE_DETAILS));
	}

	@Override
	public float getRadius() {
		return ALGAE_SIZE;
	}

}
