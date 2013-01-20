package d3kod.thehunt.world.environment;

import android.opengl.GLES20;
import d3kod.graphics.shader.ShaderProgramManager;
import d3kod.graphics.shader.programs.Program;
import d3kod.graphics.sprite.shapes.D3Circle;
import d3kod.graphics.sprite.shapes.D3Shape;

public class D3NAlgae extends D3Circle {

	private static int drawType = GLES20.GL_LINE_LOOP;
	private static final float[] algaeColor = 
		{ 0.4f, 0.4f, 0.4f, 1.0f};
	private static final float algaeRad = 0.3f;
	private static final int verticesNum = 50;
	private static final float defaultSize = 25f;
	
	protected D3NAlgae() {
		super(algaeRad, algaeColor, verticesNum);
	}

	@Override
	public float getRadius() {
		return super.getRadius()*super.getScale();
	}

	public void setSizeCategory(int size) {
		setScale(size/defaultSize);
	}

}
