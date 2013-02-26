package d3kod.graphics.sprite.shapes;

import java.nio.FloatBuffer;

import android.opengl.GLES20;
import d3kod.graphics.extra.Utilities;
import d3kod.graphics.shader.ShaderProgramManager;
import d3kod.graphics.shader.programs.AttribVariable;

public class D3Text extends D3Shape {

	private FloatBuffer mTextureCoordinates;
	private int mTextureCoordinateHandle;
	private int mTextureUniformHandle;
	private int mTextureDataHandle;
	private int mMVPIndexHandle;
	
	private static final float[] colorData = {
		0.0f, 0.0f, 0.0f, 1.0f
	};
	private static final int drawType = GLES20.GL_TRIANGLE_STRIP;

	private final int mTextureCoordinateDataSize = 2;
	
	final float[] squareTextureCoordinateData = {
	        // Front face
	        0.0f, 0.0f,
	        0.0f, 1.0f,
	        1.0f, 0.0f,
	        1.0f, 1.0f
	};
	final float[] squarePositionDataDefault = {
			-0.5f, 0.5f, 0f,				
			-0.5f, -0.5f, 0f,
			0.5f, 0.5f, 0f,
			0.5f, -0.5f, 0f
	};
	float[] squarePositionData = new float[12];
	
	protected D3Text(float textSize, int textureHandle, ShaderProgramManager sm, float maxFade) {
//		super(colorData.clone(), drawType, sm.getTextProgram());
		super();
		super.setColor(colorData); //clone?
		super.setDrawType(drawType);
		super.setProgram(sm.getBatchTextProgram());
		super.setVertexBuffer(makeVertexBuffer(textSize));
		mTextureDataHandle = textureHandle;
		mTextureCoordinates = Utilities.newFloatBuffer(squareTextureCoordinateData);
        mTextureCoordinateHandle = AttribVariable.A_TexCoordinate.getHandle();//GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");
        mMVPIndexHandle = AttribVariable.A_MVPMatrixIndex.getHandle();
//        mTextureUniformHandle = GLES20.glGetUniformLocation(super.getProgram().getHandle(), "u_Texture");
	}

	private FloatBuffer makeVertexBuffer(float size) {
		for (int i = 0; i < squarePositionData.length; ++i) {
			squarePositionData[i] = squarePositionDataDefault[i]*size;
		}
		return Utilities.newFloatBuffer(squarePositionData);
	}
	
	@Override
	public float getRadius() {
		throw (new UnsupportedOperationException());
	}

	@Override
	public void draw(float[] mVMatrix, float[] mProjMatrix) {
		super.useProgram();
		
	     // Set the active texture unit to texture unit 0.
	    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		 
	    // Bind the texture to this unit.
	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
	 
	    // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
	    GLES20.glUniform1i(mTextureUniformHandle, 0);

	    mTextureCoordinates.position(0);
	    GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false, 
       		0, mTextureCoordinates);
       
	    GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
//	    mTextureCoordinates.position(0);
//	    GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false, 
//       		0, mTextureCoordinates);
//       
//	    GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
		super.draw(mVMatrix, mProjMatrix);
	}
	
}
