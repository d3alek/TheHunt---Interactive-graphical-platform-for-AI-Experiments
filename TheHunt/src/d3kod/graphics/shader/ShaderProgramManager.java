package d3kod.graphics.shader;

import d3kod.graphics.shader.programs.DefaultProgram;
import d3kod.graphics.shader.programs.Program;
import d3kod.graphics.shader.programs.TextProgram;

public class ShaderProgramManager {
	
	private DefaultProgram defaultProgram;
	private TextProgram textProgram;
	
	public ShaderProgramManager() {
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
