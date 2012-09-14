package d3kod.thehunt;

import android.opengl.GLES20;
import d3kod.d3gles20.ShaderManager;
import d3kod.d3gles20.Utilities;
import d3kod.d3gles20.shapes.D3Shape;

public class PreyPointer extends D3Shape {

	private static final float[] pointerColor = {
		0.0f, 0.0f, 0.0f, 1.0f };
	private static final int drawType = GLES20.GL_LINE_LOOP;

	private static final float pointerSize = 0.1f;
	private static final float[] pointerVertexData = {
		0.0f, pointerSize/2, 0.0f,
		-pointerSize/2, -pointerSize/2, 0.0f,
		pointerSize/2, -pointerSize/2, 0.0f };
	
	protected PreyPointer(ShaderManager sm) {
		super(pointerColor, drawType , sm.getDefaultProgram());
		super.setVertexBuffer(Utilities.newFloatBuffer(pointerVertexData));
	}

	@Override
	public void setPosition(float x, float y, float angleDeg) {
		switch((int)angleDeg) {
		case 0: y -= pointerSize/2; break; // dir is N
		case 90: x += pointerSize/2; break; // dir is W
		case -90: x -= pointerSize/2; break; // dir is E
		case 180: y += pointerSize/2; break; // dir is S;
		}
		
		super.setPosition(x, y, angleDeg);
	}
	
	@Override
	public float getRadius() {
		throw new UnsupportedOperationException();
	}

}
