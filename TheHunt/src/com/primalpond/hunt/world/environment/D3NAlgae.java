package com.primalpond.hunt.world.environment;

import java.nio.FloatBuffer;

import android.util.Log;
import d3kod.graphics.shader.ShaderProgramManager;
import d3kod.graphics.sprite.shapes.D3FilledCircle;

public class D3NAlgae extends D3FilledCircle {

//	private static int drawType = GLES20.GL_TRIANGLES;
	private static final float[] algaeColor = 
		{ 0.4f, 0.4f, 0.4f, 0.8f};
	private static final float algaeRad = 0.3f;
	private static final int verticesNum = 50;
	private static final float defaultSize = 25f;
	public static final float algaeStartSize = 0.3f/25;
	private static final String TAG = "D3NAlgae";
	private int mTimeHandle;
	float mTime;
	
	protected D3NAlgae(ShaderProgramManager shaderProgramManager) {
		super(algaeRad, algaeColor, verticesNum);
//		super.setProgram(shaderProgramManager.get());
//		mTimeHandle = GLES20.glGetUniformLocation(getProgram().getHandle(), "time");
		mTime = (float)Math.random();
	}
	
	@Override
		public void drawBuffer(FloatBuffer vertexBuffer, float[] modelMatrix) {
//			GLES20.glUniform1f(mTimeHandle, mTime);
			mTime += 0.01f;
			// TODO Auto-generated method stub
			super.drawBuffer(vertexBuffer, modelMatrix);
		}

	
	@Override
	public float getRadius() {
		return super.getRadius()*super.getScale();
	}

	public void setSizeCategory(int size) {
		setScale(size/defaultSize);
	}

}
