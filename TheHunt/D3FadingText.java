package d3kod.thehunt.prey;

import java.nio.FloatBuffer;
import java.util.Arrays;

import android.opengl.GLES20;
import d3kod.d3gles20.Utilities;
import d3kod.d3gles20.TextureManager.Texture;
import d3kod.d3gles20.shapes.D3FadingShape;

public class D3FadingText extends D3FadingShape {

	private int mProgram;
	private FloatBuffer mTextureCoordinates;
	private int mTextureCoordinateHandle;
	private int mTextureUniformHandle;
	private int mTextureDataHandle;
	
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
//	float squareSize = 0.01f;
	float[] squarePositionData = new float[12];
	private static final String vertexShaderCode =
			"uniform mat4 u_MVPMatrix;      \n"     // A constant representing the combined model/view/projection matrix.
		 
		  + "attribute vec4 a_Position;     \n"     // Per-vertex position information we will pass in.
		  
		  + "attribute vec2 a_TexCoordinate;\n"     // Per-vertex texture coordinate information we will pass in
		  
		  + "varying vec2 v_TexCoordinate;  \n"   // This will be passed into the fragment shader.
		  
		  + "void main()                    \n"     // The entry point for our vertex shader.
		  + "{                              \n"
		  + "   v_TexCoordinate = a_TexCoordinate; \n"
		  + "   gl_Position = u_MVPMatrix   \n"     // gl_Position is a special variable used to store the final position.
		  + "               * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in
		  											// normalized screen coordinates.
		  + "}                              \n";    


	private static final String fragmentShaderCode =
			"uniform sampler2D u_Texture;       \n"    // The input texture.
			+	"precision mediump float;       \n"     // Set the default precision to medium. We don't need as high of a
	        // precision in the fragment shader.
			+ "uniform vec4 u_Color;          \n"
	        // triangle per fragment.
			
			+ "varying vec2 v_TexCoordinate;  \n" // Interpolated texture coordinate per fragment.
			
			+ "void main()                    \n"     // The entry point for our fragment shader.
			+ "{                              \n"
			+ "   gl_FragColor = texture2D(u_Texture, v_TexCoordinate);\n"     // Pass the color directly through the pipeline.
			+ "   gl_FragColor.a *= u_Color.a;\n"
			+ "}                             \n";
	
	protected D3FadingText(float textSize, float fadeSpeed, int textureHandle, float maxFade) {
//		super(colorData, drType, useDefaultShaders, fadeSpeed);
		super(colorData.clone(), drawType, false, fadeSpeed, maxFade);
		super.setVertexBuffer(makeVertexBuffer(textSize));
		mTextureDataHandle = textureHandle;
		mTextureCoordinates = Utilities.newFloatBuffer(squareTextureCoordinateData);
		mProgram = Utilities.createProgram(Utilities.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode), 
				Utilities.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode));
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");
        super.setProgram(mProgram);
	}

	private FloatBuffer makeVertexBuffer(float size) {
		for (int i = 0; i < squarePositionData.length; ++i) {
			squarePositionData[i] = squarePositionDataDefault[i]*size;
		}
		return Utilities.newFloatBuffer(squarePositionData);
	}

//	public void setTextureDataHandle(int handle) {
//		mTextureDataHandle = handle;
//	}
	
	@Override
	public float getRadius() {
		throw (new UnsupportedOperationException());
	}

	@Override
	public void draw(float[] mVMatrix, float[] mProjMatrix) {
		GLES20.glUseProgram(mProgram);
		
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
	    mTextureCoordinates.position(0);
	    GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false, 
       		0, mTextureCoordinates);
       
	    GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
		super.draw(mVMatrix, mProjMatrix);
	}
	
}
