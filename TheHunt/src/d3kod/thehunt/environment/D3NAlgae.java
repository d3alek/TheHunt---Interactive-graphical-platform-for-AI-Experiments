package d3kod.thehunt.environment;

import android.opengl.GLES20;
import d3kod.d3gles20.ShaderManager;
import d3kod.d3gles20.programs.Program;
import d3kod.d3gles20.shapes.D3Circle;
import d3kod.d3gles20.shapes.D3Shape;

public class D3NAlgae extends D3Circle {

	private static int drawType = GLES20.GL_LINE_LOOP;
	private static final float[] algaeColor = 
		{ 0.4f, 0.4f, 0.4f, 1.0f};
	private static final float algaeRad = 0.3f;
	private static final int verticesNum = 50;
	private static final float defaultSize = 25f;
	
	protected D3NAlgae(ShaderManager sm) {
		super(algaeRad, algaeColor, verticesNum, sm.getDefaultProgram());
	}

	@Override
	public float getRadius() {
		return super.getRadius()*super.getScale();
	}

	public void setSizeCategory(int size) {
		setScale(size/defaultSize);
	}

}
