package d3kod.graphics.shader.programs;

import com.primalpond.hunt.MyApplication;

import d3kod.graphics.extra.Utilities;

public class DefaultProgramFromAssets extends Program {
	
	private static final AttribVariable[] programVariables = {
		AttribVariable.A_Position
	};
	
	public DefaultProgramFromAssets() {

	}
	
	@Override
	public void init() {
		super.init(Utilities.getShaderCode("default_vertex_program", MyApplication.APPLICATION), 
				Utilities.getShaderCode("default_fragment_program", MyApplication.APPLICATION),
				programVariables);
	}

}
