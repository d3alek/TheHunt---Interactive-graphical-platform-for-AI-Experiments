package com.primalpond.hunt.world.environment;

import java.nio.FloatBuffer;

import android.opengl.GLES20;
import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.extra.Utilities;
import d3kod.graphics.shader.ShaderProgramManager;
import d3kod.graphics.sprite.shapes.D3Shape;

public class D3FoodGM extends D3Shape {

	private static float[] foodColor = {0.0f, 0.8f, 0.0f, 1.0f};
	private static int drawType = GLES20.GL_LINE_LOOP;
	private static final float radius = 0.01f;
	
	protected D3FoodGM() {
		super();
		super.setColor(foodColor);
		super.setDrawType(drawType);
		super.setVertexBuffer(makeVertexBuffer());
	}

	private FloatBuffer makeVertexBuffer() {
		return Utilities.newFloatBuffer(D3Maths.circleVerticesData(radius, 20));
	}

	@Override
	public float getRadius() {
		return radius;
	}

}
