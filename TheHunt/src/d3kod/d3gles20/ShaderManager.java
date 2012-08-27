package d3kod.d3gles20;

import d3kod.d3gles20.shapes.AttribVariable;
import android.opengl.GLES20;

public class ShaderManager {
	
	private DefaultProgram defaultProgram;
	private TextProgram textProgram;
	
	public ShaderManager() {
//		defaultProgramHandle = defVertexShaderHandle = defFragmentShaderHandle = 0;
	}

	public int getDefaultProgramHandle() {
		if (defaultProgram == null) {
			defaultProgram = new DefaultProgram();
		}
		return defaultProgram.getHandle();
	}

	public void clear() {
//		GLES20.glDeleteShader(defVertexShaderHandle);
//		GLES20.glDeleteShader(defFragmentShaderHandle);
//		defaultProgramHandle = defVertexShaderHandle = defFragmentShaderHandle = 0;
	}

	public int getTextProgramHandle() {
		if (textProgram == null) {
			textProgram = new TextProgram();
		}
		return textProgram.getHandle();
	}
}
