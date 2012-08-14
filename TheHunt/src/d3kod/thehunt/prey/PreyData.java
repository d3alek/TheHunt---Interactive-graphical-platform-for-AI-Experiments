package d3kod.thehunt.prey;

import java.nio.FloatBuffer;

import d3kod.d3gles20.D3GLES20;
import d3kod.thehunt.TheHuntRenderer;

public class PreyData {
	
	public static boolean AI = true;
	public static final int BODY_BENDS_PER_SECOND_MAX = 30;
	public static int BODY_BENDS_PER_SECOND = 12;
	public static int BODY_BEND_DELAY = TheHuntRenderer.TICKS_PER_SECOND/BODY_BENDS_PER_SECOND;
	public static final int ACTIONS_PER_SECOND_MAX = 30;
	public static int ACTIONS_PER_SECOND = 30;
	public static int ACTION_DELAY = TheHuntRenderer.TICKS_PER_SECOND/ACTIONS_PER_SECOND;
	
	protected final float MAX_BODY_BEND_ANGLE = 110;
	
	private static int SMALL_TURNS_PER_SECOND = 5; //TODO: Make it 4-5.
	private static int MEDIUM_TURNS_PER_SECOND = 4;
	private static int LARGE_TURNS_PER_SECOND = 3;
	public static int SMALL_TICKS_PER_TURN = TheHuntRenderer.TICKS_PER_SECOND/SMALL_TURNS_PER_SECOND;
	public static int MEDIUM_TICKS_PER_TURN = TheHuntRenderer.TICKS_PER_SECOND/MEDIUM_TURNS_PER_SECOND;
	public static int LARGE_TICKS_PER_TURN = TheHuntRenderer.TICKS_PER_SECOND/LARGE_TURNS_PER_SECOND;
	
	public static int angleFlopSmall = 20;
	public static int angleFlopMedium = 45;
	public static int angleFlopLarge = 90;
	public static int angleFlopBackSmall = 10;
	public static int angleFlopBackMedium = 15;
	public static int angleFlopBackLarge = 25;
	public int rotateSpeedSmall = angleFlopSmall*SMALL_TURNS_PER_SECOND/(TheHuntRenderer.TICKS_PER_SECOND);
	public int rotateSpeedMedium = angleFlopMedium*MEDIUM_TURNS_PER_SECOND/(TheHuntRenderer.TICKS_PER_SECOND);
	public int rotateSpeedLarge = angleFlopLarge*LARGE_TURNS_PER_SECOND/(TheHuntRenderer.TICKS_PER_SECOND);

	protected final int STRIDE_BYTES = D3GLES20.COORDS_PER_VERTEX * D3GLES20.BYTES_PER_FLOAT;
	protected float[] preyColor = {
		0.0f, 0.0f, 0.0f, 0.0f };
	protected final float[] preyColorDefault = {
		0.0f, 0.0f, 0.0f, 0.0f };
	protected static final float preySize = 1.0f;
	protected final float bodyLength = 0.1f * preySize;
	
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
	protected final float headSize = 0.04f * preySize;
//	protected float headScale = 0.04f * preySize;
	protected final float[] headPosition = {
			0, bodyLength/2 + headSize*0.4f, 0
	};
	
	protected final float[] eyePosition = { -0.40f * headSize, 0.25f * headSize, 0.0f };
	protected final float eyeSize = 0.25f*headSize;
			
	protected final float[] headPart1Start = { 0.0f, 1.0f, 0.0f };
	protected final float[] headPart1B = { -0.5f, 0.75f, 0.0f };
	protected final float[] headPart1C = { -1.0f, 0.5f, 0.0f };
	protected final float[] headPart2Start = { -1.0f, -0.5f, 0.0f };
	protected final float[] headPart2B = { -0.2f, -0.3f, 0.0f };
	protected final float[] headPart2C = { 0.2f, -0.3f, 0.0f };
	protected final float[] headPart3Start = { 1.0f, -0.5f, 0.0f };
	protected final float[] headPart3B = { 1.0f, 0.5f, 0.0f };
	protected final float[] headPart3C = { 0.5f, 0.75f, 0.0f };
	protected final float detailsStep = 0.1f;
	
	// Body

	protected final float[] bodyStart = { 0, 0.5f, 0};
	protected final float[] bodyB = {0, 0.2f, 0};
	protected final float[] bodyC = {0, -0.2f, 0};
	protected final float[] bodyEnd = { 0, -0.5f, 0};
	
	// Ribs
	
	protected final float[] ribA = { -0.5f, -0.2f , 0 };
	protected final float[] ribB = { -0.25f, 0 , 0 };
	protected final float[] ribC = { 0.25f, 0 , 0 };
	protected final float[] ribD = { 0.5f, -0.2f , 0 };
	
//	protected static final float[] rib1Pos = {
//		bodyB[0]*bodyLength, bodyB[1]*bodyLength, bodyB[2]*bodyLength
//	};
//	protected static final float[] rib2Pos = {
//		bodyC[0]*bodyLength, bodyC[1]*bodyLength, bodyC[2]*bodyLength
//	};
	
	protected final float ribSize = 0.07f * preySize;
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
			0, -bodyLength/2, 0
	};
	
	// Shaders
	
	protected final String vertexShaderCode =
			"uniform mat4 u_MVPMatrix;      \n"
		 
		  + "attribute vec4 a_Position;     \n"
		 
		  + "void main()                    \n"
		  + "{                              \n"
		  + "   gl_Position = u_MVPMatrix   \n"
		  + "               * a_Position;   \n"
		  + "}                              \n";


	protected final String fragmentShaderCode =
			  "precision mediump float;       \n"
			+ "uniform vec4 u_Color;          \n"
			+ "void main()                    \n"
			+ "{                              \n"
			+ "   gl_FragColor = u_Color;     \n"
			+ "}                              \n";
//	public static final int delayVLength = 10;
	
//	protected float mAngleFins; // 0 for up, 90 for right, kept in range 0..360
//	protected float mAngleHead;
	protected int mProgram;
	protected int mMVPMatrixHandle;
	protected int mPositionHandle;
	protected int mColorHandle;

	protected float vx;
	protected float vy;
	protected float mPosY;
	protected float mPosX;

	protected float[] mModelMatrix = new float[16];
//	protected float[] mRModelMatrix = new float[16];
	protected float[] mMVPMatrix = new float[16];
	protected float mPredictedPosX;
	protected float mPredictedPosY;
	protected float[] mFeetModelMatrix = new float[16];
	protected FloatBuffer eyeVertexBuffer;
//	protected int headDelay;
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
//	public float[][] delayV;
//	public float targetAngleHead;
	
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
	public float[] ribVerticesData;
	public FloatBuffer ribVertexBuffer;
	public float[] mRibsModelMatrix = new float[16];
	public int ribVerticesNum;
	public float[] bodyVerticesData;
	public int rotateSpeedHead;
	public int rotateSpeedBody;
}
