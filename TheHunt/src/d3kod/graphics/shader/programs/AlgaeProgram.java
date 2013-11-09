package d3kod.graphics.shader.programs;

import com.primalpond.hunt.MyApplication;

import d3kod.graphics.extra.Utilities;

public class AlgaeProgram extends Program {
	
	private static final AttribVariable[] programVariables = {
		AttribVariable.A_Position
	};
	
	public AlgaeProgram() {

	}
	
	@Override
	public void init() {
		super.init(Utilities.getShaderCode("algae_vertex_program", MyApplication.APPLICATION), 
				Utilities.getShaderCode("algae_fragment_program", MyApplication.APPLICATION),
				programVariables);
	}

}
