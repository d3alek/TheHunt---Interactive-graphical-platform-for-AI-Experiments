package d3kod.graphics.shader;

import d3kod.graphics.shader.programs.AlgaeProgram;
import d3kod.graphics.shader.programs.BatchTextProgram;
import d3kod.graphics.shader.programs.DefaultProgramFromAssets;
import d3kod.graphics.shader.programs.GrayscaleInvertedTextureProgram;
import d3kod.graphics.shader.programs.Program;
import d3kod.graphics.shader.programs.TextureProgram;

public class ShaderProgramManager {
	
	private DefaultProgramFromAssets defaultProgram;
	private TextureProgram textureProgram;
	private BatchTextProgram batchTextProgram;
	private GrayscaleInvertedTextureProgram invTextureProgram;
	private AlgaeProgram algaeProgram;
	
	public ShaderProgramManager() {
		defaultProgram = new DefaultProgramFromAssets();
		textureProgram = new TextureProgram();
		batchTextProgram = new BatchTextProgram();
		invTextureProgram = new GrayscaleInvertedTextureProgram();
		algaeProgram = new AlgaeProgram();
	}

	public Program getDefaultProgram() {
		if (!defaultProgram.initialized()) {
			defaultProgram.init();
		}
		return defaultProgram;
	}

	public void clear() {
		defaultProgram.delete(); textureProgram.delete(); batchTextProgram.delete(); invTextureProgram.delete();
	}

	public Program getTextureProgram() {
		if (!textureProgram.initialized()) {
			textureProgram.init();
		}
		return textureProgram;
	}

	public Program getBatchTextProgram() {
		if (!batchTextProgram.initialized()) {
			batchTextProgram.init();
		}
		return batchTextProgram;
	}
	
	public Program getInvertedTextureProgram() {
		if (!invTextureProgram.initialized()) {
			invTextureProgram.init();
		}
		return invTextureProgram;
	}

	public Program getAlgaeProgram() {
		if (!algaeProgram.initialized()) {
			algaeProgram.init();
		}
		return algaeProgram;
	}
}
