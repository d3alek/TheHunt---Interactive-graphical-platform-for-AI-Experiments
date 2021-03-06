package d3kod.graphics.sprite.shapes;

import java.nio.FloatBuffer;

import android.opengl.GLES20;
import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.extra.Utilities;
import d3kod.graphics.shader.programs.Program;

public class D3FadingFilledCircle extends D3FadingShape {

	private float mRadius;

	public D3FadingFilledCircle(float r, float[] color, int vertices, float fadeSpeed, float maxFade) {
		super(fadeSpeed, maxFade);
		super.setVertexBuffer(makeFilledCircleVerticesBuffer(r, vertices));
		super.setColor(color);
		super.setDrawType(GLES20.GL_TRIANGLE_FAN);
		mRadius = r;
	}

	private static FloatBuffer makeFilledCircleVerticesBuffer(float r, int vertices) {
		FloatBuffer buffer = Utilities.newFloatBuffer(D3Maths.filledCircleVertiesData(r, vertices));
		return buffer;
	}

	@Override
	public float getRadius() {
		return mRadius;
	}
	
	public void setRadius(float radius) {
		this.mRadius = radius;
	}

}
