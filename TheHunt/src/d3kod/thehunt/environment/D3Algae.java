package d3kod.thehunt.environment;

import java.nio.FloatBuffer;
import java.util.Random;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import d3kod.d3gles20.D3GLES20;
import d3kod.d3gles20.D3Shape;
import d3kod.d3gles20.TextureHelper;
import d3kod.thehunt.R;

public class D3Algae extends D3Shape {
	
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
			+ "   gl_FragColor = u_Color  \n"   
			+  "      * texture2D(u_Texture, v_TexCoordinate) + 0.3;\n"     // Pass the color directly through the pipeline.
			+ "}                             \n";
	
	private static final String TAG = "D3Algae";

//	private static int drawType = GLES20.GL_LINE_LOOP;
	private static final float[] algaeColor = 
		{ 0.4f, 0.4f, 0.4f, 0.0f};

	private static final int ALGAE_DETAILS_PER_PART = 10;
	private static final float ALGAE_DETAILS_STEP = 1f/ALGAE_DETAILS_PER_PART;
	private static final float ALGAE_SIZE = 0.3f;
	
	private static final int curvePartsNum = 8;
	private static final int controlPointsPerPart = 4;
	private static final int controlPointsNum = curvePartsNum * (controlPointsPerPart-1);

	private static final float WIGGLE_SPEED = 0.001f;

	private static final int MAX_WIGGLES = 50;

	private static final float GENERATOR_MAX_DISPLACEMENT = ALGAE_SIZE/3;

	private static final float GENERATOR_MAX_DELTA = GENERATOR_MAX_DISPLACEMENT/3;

	private static final int GENERATOR_NO_FLIP_NEXT = 3;
	private float[][] controPointsData;
	
	// wiggle data for each controlPoint; 
	// first component is wiggle direction(1 or -1), second is wiggles done so far in this direction
	private float[][] wiggleData = new float[controlPointsNum][2];

	private int noFlip; 
	
//	/** Store our model data in a float buffer. */
//	private final FloatBuffer mCubeTextureCoordinates;
	 
	/** This will be used to pass in the texture. */
	private int mTextureUniformHandle;
	 
	/** This will be used to pass in model texture coordinate information. */
	private int mTextureCoordinateHandle;
	 
	/** Size of the texture coordinate data in elements. */
	private final int mTextureCoordinateDataSize = 2;
	 
	/** This is a handle to our texture data. */
	private int mTextureDataHandle;

	// S, T (or X, Y)
	// Texture coordinate data.
	// Because images have a Y axis pointing downward (values increase as you move down the image) while
	// OpenGL has a Y axis pointing upward, we adjust for that here by flipping the Y axis.
	// What's more is that the texture coordinates are the same for every face.
	final float[] cubeTextureCoordinateData =
	{
	        // Front face
	        0.0f, 0.0f,
	        0.0f, 1.0f,
	        1.0f, 0.0f,
	        0.0f, 1.0f,
	        1.0f, 1.0f,
	        1.0f, 0.0f
	};
	/** Store our model data in a float buffer. */
	private final FloatBuffer mCubeTextureCoordinates;

	private int mProgram;


	private int mColorHandle;
	
	
	final float[] cubePositionData =
		{
				// In OpenGL counter-clockwise winding is default. This means that when we look at a triangle, 
				// if the points are counter-clockwise we are looking at the "front". If not we are looking at
				// the back. OpenGL has an optimization where all back-facing triangles are culled, since they
				// usually represent the backside of an object and aren't visible anyways.

				// Front face
				-1.0f, 1.0f, 0f,				
				-1.0f, -1.0f, 0f,
				1.0f, 1.0f, 0f, 
				-1.0f, -1.0f, 0f, 				
				1.0f, -1.0f, 0f,
				1.0f, 1.0f, 0f
		};
	
	protected D3Algae(int textureDataHandle) {
		super(algaeColor, GLES20.GL_TRIANGLES, false);
		controPointsData = null;
		setVertexBuffer(makeVerticesBuffer());
//		Log.v(TAG, "Custom program is " + customProgram);
		mProgram = D3GLES20.createProgram(D3GLES20.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode), 
				D3GLES20.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode));
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix"); 
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "u_Color");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");
        // Load the texture
        mTextureDataHandle = textureDataHandle;
        
        mCubeTextureCoordinates = D3GLES20.newFloatBuffer(cubeTextureCoordinateData);
		
	}

	private FloatBuffer makeVerticesBuffer() {
		return D3GLES20.newFloatBuffer(cubePositionData);
	}

	@Override
	public void draw(float[] mVMatrix, float[] mProjMatrix) {
//		super.setProgram(mProgram);
		GLES20.glUseProgram(mProgram);
		
	     // Set the active texture unit to texture unit 0.
		    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		 
		    // Bind the texture to this unit.
		    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
		 
		    // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
		    GLES20.glUniform1i(mTextureUniformHandle, 0);
//		    
//		    mCubeTextureCoordinates.position(0);
//	        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false, 
//	        		0, mCubeTextureCoordinates);
//	        
//	        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
	        
//        super.draw(mVMatrix, mProjMatrix);
//        
        getVertexBuffer().position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, D3GLES20.COORDS_PER_VERTEX, 
        		GLES20.GL_FLOAT, false, STRIDE_BYTES, getVertexBuffer());
        
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        
        // Pass in color information
        GLES20.glUniform4fv(mColorHandle, 1, algaeColor , 0);
        
        GLES20.glEnableVertexAttribArray(mColorHandle);
        
        Matrix.multiplyMM(mMVPMatrix  , 0, mVMatrix, 0, getMMatrix(), 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);


	    
	    mCubeTextureCoordinates.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false, 
        		0, mCubeTextureCoordinates);
        
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
	}
	
//	private FloatBuffer makeVerticesBuffer() {
//		if (controPointsData == null) {
//			controPointsData = algaeControlPointsGenerator();
//			
//		}
//		
//		int coordsPerPart = ALGAE_DETAILS_PER_PART*D3GLES20.COORDS_PER_VERTEX;
//		float[] verticesData = new float[coordsPerPart*curvePartsNum];
//		float[] curPart;
//		int controlPointStart = 0;
//		for (int i = 0; i < curvePartsNum; ++i) {
//			curPart = D3GLES20.quadBezierCurveVertices(
//					controPointsData[controlPointStart], 
//					controPointsData[controlPointStart+1], 
//					controPointsData[controlPointStart+2], 
//					controPointsData[(controlPointStart+3)%controlPointsNum], ALGAE_DETAILS_STEP, 1f);
//			controlPointStart = controlPointStart+3;
//			for (int j = 0; j < coordsPerPart; ++j) {
//				verticesData[i*coordsPerPart + j] = curPart[j];
//			}
//		}
////		Log.v(TAG, "verticesData is: " + Arrays.toString(verticesData));
//		return D3GLES20.newFloatBuffer(verticesData);
//	}

	
	
	private float[][] algaeControlPointsGenerator() {
		Random rand = new Random();
		float[][] controlPoints = new float[controlPointsNum][D3GLES20.COORDS_PER_VERTEX];
		controlPoints = toTwoDimCoordinateArray(D3GLES20.circleVerticesData(ALGAE_SIZE, controlPointsNum));
		float displacementX = (1-2*rand.nextFloat())*GENERATOR_MAX_DISPLACEMENT;
		float displacementY = (1-2*rand.nextFloat())*GENERATOR_MAX_DISPLACEMENT;
//		float displacementXDelta;
//		if (displacementX == 0) displacementXDelta = GENERATOR_MAX_DELTA;
		float displacementXDelta = GENERATOR_MAX_DELTA*((GENERATOR_MAX_DISPLACEMENT-displacementX)/GENERATOR_MAX_DISPLACEMENT);
//		float displacementYDelta;
//		if (displacementY == 0) displacementYDelta = GENERATOR_MAX_DELTA;
		float displacementYDelta = GENERATOR_MAX_DELTA*((GENERATOR_MAX_DISPLACEMENT-displacementY)/GENERATOR_MAX_DISPLACEMENT);
		float flipProb;
		float signX, signY;
		int countFlips = 0;
		noFlip = 0;
		for (int i = 1; i < controlPointsNum-1; ++i) {
			controlPoints[i][0] += displacementX;
			controlPoints[i][1] += displacementY;
			displacementX += displacementXDelta;
			displacementY += displacementYDelta;
			flipProb = Math.abs(D3GLES20.distance(0, 0, displacementX, displacementY)-GENERATOR_MAX_DISPLACEMENT)/GENERATOR_MAX_DISPLACEMENT;
//			Log.v(TAG, "FlipProb is "  + flipProb + " distance is " + D3GLES20.distance(0, 0, displacementX, displacementY) + " GENERATOR_MAX_DISPLACEMENT is " + GENERATOR_MAX_DISPLACEMENT);
			if (noFlip <= 0 && rand.nextFloat() < flipProb) {
//				Log.v(TAG, "Do flip!");
				countFlips ++;
				signX = Math.signum(displacementXDelta);
				signY = Math.signum(displacementYDelta);
				displacementXDelta = -signX*GENERATOR_MAX_DELTA*rand.nextFloat();
				displacementYDelta = -signY*GENERATOR_MAX_DELTA*rand.nextFloat();
				noFlip = GENERATOR_NO_FLIP_NEXT;
			}
			else noFlip--;
		}
//		Log.v(TAG, "Control points are " + controlPointsNum + " Flips are " + countFlips);
		return controlPoints;
	}

	private float[][] toTwoDimCoordinateArray(float[] oneDimVerticesData) {
		int coordNum = oneDimVerticesData.length/D3GLES20.COORDS_PER_VERTEX;
		float[][] twoDimVerticesData = new float[coordNum][D3GLES20.COORDS_PER_VERTEX];
		for (int i = 0; i < coordNum; ++i) {
			twoDimVerticesData[i][0] = oneDimVerticesData[i*D3GLES20.COORDS_PER_VERTEX];
			twoDimVerticesData[i][1] = oneDimVerticesData[i*D3GLES20.COORDS_PER_VERTEX+1];
			twoDimVerticesData[i][2] = oneDimVerticesData[i*D3GLES20.COORDS_PER_VERTEX+2];
		}
		return twoDimVerticesData;
	}

	@Override
	public float getRadius() {
		return ALGAE_SIZE;
	}

	public void wiggle() {
		Random rand = new Random();
		float randSpeedX, randSpeedY;
		
		for (int i = 0; i < controlPointsNum; ++i) {
//			randSpeedX = WIGGLE_SPEED * (1-2*rand.nextFloat());
//			randSpeedY = WIGGLE_SPEED * (1-2*rand.nextFloat());
			if (wiggleData[i][0] == 0) {
				// decide for wiggle direction
				wiggleData[i][0] = rand.nextFloat() > 0.5 ? 1 : -1;
			}
			if (wiggleData[i][1] >= MAX_WIGGLES) {
				wiggleData[i][0] = -wiggleData[i][0];
				wiggleData[i][1] = 0;
			}
			controPointsData[i][0] += WIGGLE_SPEED*wiggleData[i][0];
			controPointsData[i][1] += WIGGLE_SPEED*wiggleData[i][0];
			wiggleData[i][1]++;
		}
		setVertexBuffer(makeVerticesBuffer());
	}
}
