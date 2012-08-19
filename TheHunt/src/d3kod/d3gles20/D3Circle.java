package d3kod.d3gles20;

import java.nio.FloatBuffer;

import android.opengl.GLES20;

public class D3Circle extends D3Shape {

	private float mRadius;

	public D3Circle(float r, float[] color, int vertices, boolean useDefaultShaders) {
		super(makeCircleVerticesBuffer(r, vertices), color, GLES20.GL_LINE_LOOP, useDefaultShaders);
		mRadius = r;
	}

	private static FloatBuffer makeCircleVerticesBuffer(float r, int vertices) {
		FloatBuffer buffer = Utilities.newFloatBuffer(D3Maths.circleVerticesData(r, vertices));
		return buffer;
	}

	@Override
	public float getRadius() {
		return mRadius;
	}

}
