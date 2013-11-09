package d3kod.graphics.sprite.shapes;

import java.nio.FloatBuffer;

import android.opengl.GLES20;
import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.extra.Utilities;
import d3kod.graphics.shader.programs.Program;

public class D3CoolShaderCircle extends D3Shape {

	private float mRadius;

	public D3CoolShaderCircle(float r, float[] color, int vertices) {
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
