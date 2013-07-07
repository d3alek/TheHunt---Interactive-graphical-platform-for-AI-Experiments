package d3kod.graphics.sprite.shapes;

import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.util.Log;
import d3kod.graphics.extra.Utilities;
import d3kod.graphics.shader.ShaderProgramManager;
import d3kod.graphics.shader.programs.AttribVariable;
import d3kod.graphics.texture.TextureInfo;

public class D3Image extends D3Shape {

	private FloatBuffer mTextureCoordinates;
	private int mTextureCoordinateHandle;
	private int mTextureUniformHandle;
	private int mTextureDataHandle;
//	private int mMVPIndexHandle;
	
	private static final float[] colorData = {
		0.0f, 0.0f, 0.0f, 1.0f
	};
	private static final int drawType = GLES20.GL_TRIANGLE_STRIP;
	private static final String TAG = "D3Image";

	private final int mTextureCoordinateDataSize = 2;
	
	final float[] rectTextureCoordinateData = {
	        // Front face
	        0.0f, 0.0f,
	        0.0f, 1.0f,
	        1.0f, 0.0f,
	        1.0f, 1.0f
	};
	final float[] rectPositionDataDefault = {
			-0.5f, 0.5f, 0f,				
			-0.5f, -0.5f, 0f,
			0.5f, 0.5f, 0f,
			0.5f, -0.5f, 0f
	};
	float[] rectPositionData = new float[12];
	private float mWidth;
	private float mHeight;
	private ShaderProgramManager mShaderManager;
	
	public D3Image(TextureInfo texture, float size, ShaderProgramManager sm) {
//		super(colorData.clone(), drawType, sm.getTextProgram());
		super();
		super.setColor(colorData); //clone?
		super.setDrawType(drawType);
		super.setProgram(sm.getTextureProgram());
		mShaderManager = sm;
//		mWidth = texture.width * 0.015f;
//		mHeight = texture.height * 0.015f;
		float ratio = texture.height/(float)texture.width;
		if (ratio<1) {
			mWidth = size;
			mHeight = size*(texture.height/(float)texture.width);
		}
		else {
			mWidth = size/ratio;
			mHeight = size;
		}
		Log.v(TAG, "width, height: " + mWidth + " " + mHeight);
		super.setVertexBuffer(makeVertexBuffer(mWidth, mHeight));
		mTextureDataHandle = texture.handle;
		mTextureCoordinates = Utilities.newFloatBuffer(rectTextureCoordinateData);
        mTextureCoordinateHandle = AttribVariable.A_TexCoordinate.getHandle();//GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");
//        mMVPIndexHandle = AttribVariable.A_MVPMatrixIndex.getHandle();
        mTextureUniformHandle = GLES20.glGetUniformLocation(super.getProgram().getHandle(), "u_Texture");
	}

	private FloatBuffer makeVertexBuffer(float width, float height) {
		for (int i = 0; i < rectPositionData.length; i+=3) {
			rectPositionData[i] = rectPositionDataDefault[i]*width;
			rectPositionData[i+1] = rectPositionDataDefault[i+1]*height;
		}
		return Utilities.newFloatBuffer(rectPositionData);
	}
	
	@Override
	public float getRadius() {
//		throw (new UnsupportedOperationException());
		return mWidth/2;
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
	
	public void setInverted(boolean inverted) {
		if (inverted) {
			setProgram(mShaderManager.getInvertedTextureProgram());
		}
		else {
			setProgram(mShaderManager.getTextureProgram());
		}
	}
}
