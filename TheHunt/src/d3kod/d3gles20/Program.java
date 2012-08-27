package d3kod.d3gles20;

import android.opengl.GLES20;
import d3kod.d3gles20.shapes.AttribVariable;


public abstract class Program {
	
	private int programHandle;
	private int vertexShaderHandle;
	private int fragmentShaderHandle;
	
	public Program(String vertexShaderCode, String fragmentShaderCode,
			AttribVariable[] programVariables) {
		if (programHandle == 0) {
			if (vertexShaderHandle == 0) {
				vertexShaderHandle = Utilities.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
			}
			if (fragmentShaderHandle == 0) {
				fragmentShaderHandle = Utilities.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
			}
			programHandle = Utilities.createProgram(
					vertexShaderHandle, fragmentShaderHandle, programVariables);
		}
	}
	
	public int getHandle() {
		return programHandle;
	}
}