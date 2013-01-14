package d3kod.d3gles20.shapes;

import java.nio.FloatBuffer;

import android.opengl.GLES20;
import d3kod.d3gles20.D3Maths;
import d3kod.d3gles20.Utilities;
import d3kod.d3gles20.programs.Program;

public class D3Circle extends D3Shape {

	private float mRadius;

	public D3Circle(float r, float[] color, int vertices) {
//		super(makeCircleVerticesBuffer(r, vertices), color, GLES20.GL_LINE_LOOP);
		super();
		super.setVertexBuffer(makeCircleVerticesBuffer(r, vertices));
		super.setColor(color);
		super.setDrawType(GLES20.GL_LINE_LOOP);
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
