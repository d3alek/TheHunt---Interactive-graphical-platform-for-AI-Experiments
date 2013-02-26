package d3kod.graphics.shader;

import d3kod.graphics.shader.programs.BatchTextProgram;
import d3kod.graphics.shader.programs.DefaultProgram;
import d3kod.graphics.shader.programs.Program;
import d3kod.graphics.shader.programs.TextProgram;

public class ShaderProgramManager {
	
	private DefaultProgram defaultProgram;
	private TextProgram textProgram;
	private BatchTextProgram batchTextProgram;
	
	public ShaderProgramManager() {
		defaultProgram = new DefaultProgram();
		textProgram = new TextProgram();
		batchTextProgram = new BatchTextProgram();
	}

	public Program getDefaultProgram() {
		if (!defaultProgram.initialized()) {
			defaultProgram.init();
		}
		return defaultProgram;
	}

	public void clear() {
		defaultProgram.delete(); textProgram.delete(); batchTextProgram.delete();
	}

	public Program getTextProgram() {
		if (!textProgram.initialized()) {
			textProgram.init();
		}
		return textProgram;
	}

	public Program getBatchTextProgram() {
		if (!batchTextProgram.initialized()) {
			batchTextProgram.init();
		}
		return batchTextProgram;
	}
}
