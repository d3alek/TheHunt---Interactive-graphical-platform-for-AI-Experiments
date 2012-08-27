package d3kod.d3gles20;

import d3kod.d3gles20.programs.DefaultProgram;
import d3kod.d3gles20.programs.Program;
import d3kod.d3gles20.programs.TextProgram;

public class ShaderManager {
	
	private DefaultProgram defaultProgram;
	private TextProgram textProgram;
	
	public ShaderManager() {
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
