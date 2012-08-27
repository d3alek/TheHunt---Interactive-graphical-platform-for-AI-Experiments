package d3kod.d3gles20;

public class ShaderManager {
	
	private DefaultProgram defaultProgram;
	private TextProgram textProgram;
	
	public ShaderManager() {
//		defaultProgramHandle = defVertexShaderHandle = defFragmentShaderHandle = 0;
		defaultProgram = new DefaultProgram();
		textProgram = new TextProgram();
	}

	public Program getDefaultProgram() {
		if (!defaultProgram.initialized()) {
			defaultProgram.init();
		}
		return defaultProgram;
	}

	public void clear() {
		defaultProgram.delete(); textProgram.delete();
	}

	public Program getTextProgram() {
		if (!textProgram.initialized()) {
			textProgram.init();
		}
		return textProgram;
	}
}
