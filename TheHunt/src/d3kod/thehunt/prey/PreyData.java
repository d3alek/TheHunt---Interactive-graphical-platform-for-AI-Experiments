package d3kod.thehunt.prey;

import java.nio.FloatBuffer;

import d3kod.d3gles20.D3GLES20;

public class PreyData {
	protected static final float MAX_SPEED = 0.1f;
	protected static final int MAX_SPIN_SPEED = 10;
	protected static final float DISTANCE_TO_ANGLE_RATIO = 0.0003f;// MAX_SPEED/MAX_SPIN_SPEED;
	protected static final float MAX_BODY_BEND_ANGLE = 30;
	protected static final int angleSpeedIncrement = 1;
	protected static final int angleSpeedHeadDefault = angleSpeedIncrement/2;
	protected float targetAngleFins;
	protected static int angleSpeedFins = 1;
	protected static int angleSpeedHead = 30;
	
	protected static int angleFlopHead = 30;
	public int rotateSpeed = 5;
	public float thrust;
	
	protected static final int angleBackSpeed = 3;
	protected static float moveStep = 0.005f;
	protected static final int STRIDE_BYTES = D3GLES20.COORDS_PER_VERTEX * D3GLES20.BYTES_PER_FLOAT;
	protected static final float[] preyColor = {
		0.0f, 0.0f, 0.0f, 0.0f };
	protected static final float preySize = 1.0f;
	protected static final float bodyLength = 0.1f * preySize;
	protected static final float bodyWidth = 1.0f * bodyLength;
	protected static final float bodyHeight = 1.0f * bodyLength;
	
	protected final int maxAngle = 60;
	protected final int minAngle = 0;
	protected int mLeftFootAngle = minAngle;
	protected int mRightFootAngle = minAngle;
	protected float vHeadLeft;
	protected float vHeadRight;
	protected float vTailLeft;
	protected float vTailRight;
	protected float forwardAngleSpeed;
	
	//Head
	protected static final float headSize = 0.04f * preySize;
	
	protected static final float[] headPosition = {
			0, bodyHeight/2 + headSize*0.4f, 0
	};
	
	protected static final float[] eyePosition = { -0.40f * headSize, 0.25f * headSize, 0.0f };
	protected static final float eyeSize = 0.25f*headSize;
			
	protected static final float[] headPart1Start = { 0.0f, 1.0f, 0.0f };
	protected static final float[] headPart1B = { -0.5f, 0.75f, 0.0f };
	protected static final float[] headPart1C = { -1.0f, 0.5f, 0.0f };
	protected static final float[] headPart2Start = { -1.0f, -0.5f, 0.0f };
	protected static final float[] headPart2B = { -0.2f, -0.3f, 0.0f };
	protected static final float[] headPart2C = { 0.2f, -0.3f, 0.0f };
	protected static final float[] headPart3Start = { 1.0f, -0.5f, 0.0f };
	protected static final float[] headPart3B = { 1.0f, 0.5f, 0.0f };
	protected static final float[] headPart3C = { 0.5f, 0.75f, 0.0f };
	protected static final float detailsStep = 0.1f;
	
	// Body

	protected static final float[] bodyStart = { 0, 0.5f, 0};
	protected static final float[] bodyB = {0, 0.2f, 0};
	protected static final float[] bodyC = {0, -0.2f, 0};
	protected static final float[] bodyEnd = { 0, -0.5f, 0};
	
	// Fins
	protected final float finSize = 0.05f * preySize;
	
	protected final float[] rightFinStart = { 0.0f, 0.0f, 0.0f };
	protected final float[] rightFinB = { 0.55f, -0.4f, 0.0f };
	protected final float[] rightFinC = { 0.7f, -0.6f, 0.0f };
	protected final float[] rightFinEnd = { 0.8f, -1.0f, 0.0f };
	
	protected final float[] leftFinStart = { rightFinStart[0], rightFinStart[1], 0.0f };
	protected final float[] leftFinB = { -rightFinB[0], rightFinB[1], 0.0f };
	protected final float[] leftFinC = { -rightFinC[0], rightFinC[1], 0.0f };
	protected final float[] leftFinEnd = { -rightFinEnd[0], rightFinEnd[1], 0.0f };
	
	protected int finVerticesNum;
	
	protected float[] headVerticesData;
	protected float[] leftFinVerticesData;
	protected float[] rightFinVerticesData;
	
	protected final float[] leftFootPosition = {
			0, -bodyHeight/2, 0
	};
	
	// Shaders
	
	protected static final String vertexShaderCode =
			"uniform mat4 u_MVPMatrix;      \n"
		 
		  + "attribute vec4 a_Position;     \n"
		 
		  + "void main()                    \n"
		  + "{                              \n"
		  + "   gl_Position = u_MVPMatrix   \n"
		  + "               * a_Position;   \n"
		  + "}                              \n";


	protected static final String fragmentShaderCode =
			  "precision mediump float;       \n"
			+ "uniform vec4 u_Color;          \n"
			+ "void main()                    \n"
			+ "{                              \n"
			+ "   gl_FragColor = u_Color;     \n"
			+ "}                              \n";
	public static final int delayVLength = 10;
	
	protected float mAngleFins; // 0 for up, 90 for right, kept in range 0..360
	protected float mAngleHead;
	protected int mProgram;
	protected int mMVPMatrixHandle;
	protected int mPositionHandle;
	protected int mColorHandle;

	protected float vx;
	protected float vy;
	protected float mPosY;
	protected float mPosX;

	protected float[] mModelMatrix = new float[16];
	protected float[] mRModelMatrix = new float[16];
	protected float[] mMVPMatrix = new float[16];
	protected float mPredictedPosX;
	protected float mPredictedPosY;
	protected float[] mFeetModelMatrix = new float[16];
	protected FloatBuffer eyeVertexBuffer;
	protected int headDelay;
	protected float[] bodyStart4 = new float[4];
	protected float[] bodyB4 = new float[4];
	protected float[] bodyC4 = new float[4];
	protected float[] bodyEnd4 = new float[4];
	protected float[] bodyStartRot = new float[4];
	protected float[] bodyBRot = new float[4];
	protected float[] bodyCRot = new float[4];
	protected float[] bodyEndRot = new float[4];
	
	protected float mPosHeadX;
	protected float mPosHeadY;
	
	protected float[] eyeVertexData;
	protected int headVerticesNum;
	protected FloatBuffer headVertexBuffer;
	protected float[] mHeadModelMatrix = new float[16];
	protected int bodyVerticesNum;
	protected FloatBuffer bodyVertexBuffer;
	protected FloatBuffer rightFinVertexBuffer;
	protected FloatBuffer leftFinVertexBuffer;
	protected final int eyeDetailsLevel = 10;
	public float[][] delayV;
	public float targetAngleHead;
	
	public int bodyStartAngle;
	public int bodyBAngle;
	public int bodyCAngle;
	public int bodyEndAngle;
	
	float[] mBodyStartRMatrix = new float[16];
	float[] mBodyBRMatrix = new float[16];
	float[] mBodyCRMatrix = new float[16];
	float[] mBodyEndRMatrix = new float[16];
	public int bodyEndAngleTarget;
	public int bodyCAngleTarget;
	public int bodyBAngleTarget;
	public int bodyStartAngleTarget;
}
