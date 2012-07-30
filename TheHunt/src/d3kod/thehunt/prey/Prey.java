package d3kod.thehunt.prey;

import java.nio.FloatBuffer;

import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.FloatMath;
import android.util.Log;
import d3kod.d3gles20.D3GLES20;
import d3kod.thehunt.TheHuntRenderer;
import d3kod.thehunt.environment.Environment;
import d3kod.thehunt.environment.EnvironmentData;
import d3kod.thehunt.prey.memory.WorldModel;
import d3kod.thehunt.prey.planner.Planner;
import d3kod.thehunt.prey.sensor.Sensor;

public class Prey {
	private static final String TAG = "Prey";
	private static final float MAX_SPEED = 0.1f;
	private static final int MAX_SPIN_SPEED = 10;
	private static final float DISTANCE_TO_ANGLE_RATIO = 0.001f;// MAX_SPEED/MAX_SPIN_SPEED;
	public static final float MAX_BODY_BEND_ANGLE = 0;
	public static final int angleSpeedIncrement = 2;
	private static final int angleSpeedHeadDefault = angleSpeedIncrement/2;
	private static int angleSpeedHead = angleSpeedIncrement;
	private static final int angleBackSpeed = 10;
	private static float moveStep = 0.005f;
	private static final int STRIDE_BYTES = D3GLES20.COORDS_PER_VERTEX * D3GLES20.BYTES_PER_FLOAT;
	private final float[] preyColor = {
		0.0f, 0.0f, 0.0f, 0.0f };
	private final float preySize = 1.0f;
	private final float bodyLength = 0.1f * preySize;
	private final float bodyWidth = 1.0f * bodyLength;
	private final float bodyHeight = 1.0f * bodyLength;
	
	//Head
	private final float headSize = 0.04f * preySize;
	
	private final float[] headPosition = {
			0, bodyHeight/2 + headSize*0.4f, 0
	};
	
	private final float[] eyePosition = { -0.40f * headSize, 0.25f * headSize, 0.0f };
	private final float eyeSize = 0.25f*headSize;
	private float[] eyeVertexData;
			
	private float[] headPart1Start = { 0.0f, 1.0f, 0.0f };
	private float[] headPart1B = { -0.5f, 0.75f, 0.0f };
	private float[] headPart1C = { -1.0f, 0.5f, 0.0f };
	private float[] headPart2Start = { -1.0f, -0.5f, 0.0f };
	private float[] headPart2B = { -0.2f, -0.3f, 0.0f };
	private float[] headPart2C = { 0.2f, -0.3f, 0.0f };
	private float[] headPart3Start = { 1.0f, -0.5f, 0.0f };
	private float[] headPart3B = { 1.0f, 0.5f, 0.0f };
	private float[] headPart3C = { 0.5f, 0.75f, 0.0f };
	private float detailsStep = 0.1f;
	
	private int headVerticesNum;
	private FloatBuffer headVertexBuffer;
	private float[] mHeadModelMatrix = new float[16];
	
	// Body

	private final float[] bodyStart = { 0, 0.5f, 0};
	private final float[] bodyB = {0, 0.2f, 0};
	private final float[] bodyC = {0, -0.2f, 0};
	private final float[] bodyEnd = { 0, -0.5f, 0};
	
	private int bodyVerticesNum;
	private FloatBuffer bodyVertexBuffer;
	
	// Fins
	private int maxAngle = 40;
	private int minAngle = 0;
	private final float finSize = 0.05f * preySize;
	
	private final float[] rightFinStart = { 0.0f, 0.0f, 0.0f };
	private final float[] rightFinB = { 0.55f, -0.4f, 0.0f };
	private final float[] rightFinC = { 0.7f, -0.6f, 0.0f };
	private final float[] rightFinEnd = { 0.8f, -1.0f, 0.0f };
	
	private final float[] leftFinStart = { rightFinStart[0], rightFinStart[1], 0.0f };
	private final float[] leftFinB = { -rightFinB[0], rightFinB[1], 0.0f };
	private final float[] leftFinC = { -rightFinC[0], rightFinC[1], 0.0f };
	private final float[] leftFinEnd = { -rightFinEnd[0], rightFinEnd[1], 0.0f };
	
	private int finVerticesNum;
	
	private float[] headVerticesData;
	private float[] leftFinVerticesData;
	private float[] rightFinVerticesData;
	
	private final float[] leftFootPosition = {
			0, -bodyHeight/2, 0
	};
	
	private FloatBuffer rightFinVertexBuffer;
	private FloatBuffer leftFinVertexBuffer;
	
	// Shaders
	
	private static final String vertexShaderCode =
			"uniform mat4 u_MVPMatrix;      \n"
		 
		  + "attribute vec4 a_Position;     \n"
		 
		  + "void main()                    \n"
		  + "{                              \n"
		  + "   gl_Position = u_MVPMatrix   \n"
		  + "               * a_Position;   \n"
		  + "}                              \n";


	private static final String fragmentShaderCode =
			  "precision mediump float;       \n"
			+ "uniform vec4 u_Color;          \n"
			+ "void main()                    \n"
			+ "{                              \n"
			+ "   gl_FragColor = u_Color;     \n"
			+ "}                              \n";
	
	private float mAngleFins; // 0 for up, 90 for right, kept in range 0..360
	private float mAngleHead;
	private int mProgram;
	private int mMVPMatrixHandle;
	private int mPositionHandle;
	private int mColorHandle;

	private float vx;
	private float vy;
	private float mPosY;
	private float mPosX;

	private float[] mModelMatrix = new float[16];
	private float[] mRModelMatrix = new float[16];
	private float[] mMVPMatrix = new float[16];
	private float mPredictedPosX;
	private float mPredictedPosY;
	private float[] mFeetModelMatrix;
	private int mLeftFootAngle = minAngle;
	private int mRightFootAngle = minAngle;
	private int vLeft;
	private int vRight;
	private float forwardAngleSpeed;
	private FloatBuffer eyeVertexBuffer;
	private int headDelay;
	private float[] bodyStart4 = new float[4];
	private float[] bodyB4 = new float[4];
	private float[] bodyStartRot = new float[4];
	private float[] bodyBRot = new float[4];
	private Planner mPlanner;
	private WorldModel mWorldModel;
	private Sensor mSensor;
	private float mPosHeadX;
	private float mPosHeadY;
	private Environment mEnv;

	public void update(float dx, float dy) {
		float[] posTemp = { 0.0f, 0.07f, 0.0f, 1.0f };
		Matrix.setIdentityM(mHeadModelMatrix, 0);
		Matrix.translateM(mHeadModelMatrix, 0, mPosX, mPosY, 0);
		Matrix.rotateM(mHeadModelMatrix, 0, mAngleHead, 0, 0, 1);
		Matrix.multiplyMV(posTemp, 0, mHeadModelMatrix, 0, posTemp, 0);
		
		mPosHeadX = posTemp[0]; 
		mPosHeadY = posTemp[1];
		
		mWorldModel.update(mSensor.sense(mPosHeadX, mPosHeadY, mPosX, mPosY));
		doAction(mPlanner.nextAction(mWorldModel)); 
		
		move(dx, dy);
	}
	
	public Prey(float screenWidth, float screenHeight, Environment env) {
		mWorldModel = new WorldModel(screenWidth, screenHeight);
		mPlanner = new Planner();
		mEnv = env;
		mSensor = new Sensor(mEnv);
		
		Matrix.setIdentityM(mModelMatrix, 0);
		mPosX = mPosY = 0;

		for (int i = 0; i < 3; ++i) {
			bodyStart4[i] = bodyStart[i];
		}
		for (int i = 0; i < 3; ++i) {
			bodyB4[i] = bodyB[i];
		}
		
		headVerticesData = calcHeadVerticesData();
		leftFinVerticesData = calcLeftFinVerticesData();
		rightFinVerticesData = calcRightFinVerticesData();
		eyeVertexData = D3GLES20.circleVerticesData(eyePosition, eyeSize, 10);
		
		finVerticesNum = rightFinVerticesData.length / D3GLES20.COORDS_PER_VERTEX;
//		bodyVerticesNum =
//		bodyVertexBuffer = D3GLES20.newFloatBuffer(bodyVerticesData);
		leftFinVertexBuffer = D3GLES20.newFloatBuffer(leftFinVerticesData);
		rightFinVertexBuffer = D3GLES20.newFloatBuffer(rightFinVerticesData);
		headVertexBuffer = D3GLES20.newFloatBuffer(headVerticesData);
		eyeVertexBuffer = D3GLES20.newFloatBuffer(eyeVertexData);
		
		int vertexShaderHandle = TheHuntRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShaderHandle = TheHuntRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        
        mProgram = D3GLES20.createProgram(vertexShaderHandle, fragmentShaderHandle);
        
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix"); 
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "u_Color");
	}
	
	private float[] calcRightFinVerticesData() {
		return D3GLES20.quadBezierCurveVertices(
				rightFinStart, rightFinB, rightFinC, rightFinEnd, detailsStep, finSize);
	}

	private float[] calcLeftFinVerticesData() {
		return D3GLES20.quadBezierCurveVertices(
				leftFinStart, leftFinB, leftFinC, leftFinEnd, detailsStep, finSize);
	}

	private float[] calcHeadVerticesData() {
		float[] part1 = D3GLES20.quadBezierCurveVertices(
				headPart1Start, headPart1B, headPart1C, headPart2Start, detailsStep, headSize);
		float[] part2 = D3GLES20.quadBezierCurveVertices(
				headPart2Start, headPart2B, headPart2C, headPart3Start, detailsStep, headSize);
		float[] part3 = D3GLES20.quadBezierCurveVertices(
				headPart3Start, headPart3B, headPart3C, headPart1Start, detailsStep, headSize);
		float[] headVerticesData = new float[part1.length + part2.length + part3.length];
		for (int i = 0; i < part1.length; ++i) {
			headVerticesData[i] = part1[i];
		}
		for (int i = 0; i < part2.length; ++i) {
			headVerticesData[part1.length + i] = part2[i];
		}
		for (int i = 0; i < part3.length; ++i) {
			headVerticesData[part1.length + part2.length + i] = part3[i];
		}
		headVerticesNum = headVerticesData.length / D3GLES20.COORDS_PER_VERTEX;
		return headVerticesData;
	}

	public void draw(float[] mVMatrix, float[] mProjMatrix, float interpolation) {
//		GLES20.glEnable(GLES20.GL_PO)
//		GLES20.glLineWidth(3f);
		GLES20.glUseProgram(mProgram);
		
        GLES20.glUniform4fv(mColorHandle, 1, preyColor , 0);
        GLES20.glEnableVertexAttribArray(mColorHandle);
        
        mPredictedPosX = mPosX + vx*interpolation; mPredictedPosY = mPosY + vy*interpolation;
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix , 0, mPredictedPosX, mPredictedPosY, 0);
//      TODO:  mPredictedAngle = mAngle + // depend on the deg, make a flag for rotating
        Matrix.rotateM(mRModelMatrix, 0, mModelMatrix, 0, mAngleFins, 0, 0, 1);
        Matrix.rotateM(mHeadModelMatrix, 0, mModelMatrix, 0, mAngleHead-mAngleFins, 0, 0, 1);
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mRModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        
		// Body
        updateBodyVertexBuffer();
        
        GLES20.glVertexAttribPointer(mPositionHandle, D3GLES20.COORDS_PER_VERTEX, 
        		GLES20.GL_FLOAT, false, STRIDE_BYTES, bodyVertexBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, bodyVerticesNum);
        
        // Feet
        mFeetModelMatrix = mRModelMatrix.clone();
        Matrix.translateM(mFeetModelMatrix, 0, 
        		leftFootPosition[0], leftFootPosition[1], 0);
        Matrix.rotateM(mFeetModelMatrix, 0, mLeftFootAngle, 0, 0, 1);
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mFeetModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        
        GLES20.glVertexAttribPointer(mPositionHandle, D3GLES20.COORDS_PER_VERTEX, 
        		GLES20.GL_FLOAT, false, STRIDE_BYTES, leftFinVertexBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, finVerticesNum);
        
        mFeetModelMatrix = mRModelMatrix.clone();
        Matrix.translateM(mFeetModelMatrix, 0, 
        		-leftFootPosition[0], leftFootPosition[1], 0);
        Matrix.rotateM(mFeetModelMatrix, 0, -mRightFootAngle, 0, 0, 1);
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mFeetModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        
        GLES20.glVertexAttribPointer(mPositionHandle, D3GLES20.COORDS_PER_VERTEX, 
        		GLES20.GL_FLOAT, false, STRIDE_BYTES, rightFinVertexBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, finVerticesNum);
        
        // Head
//        mHeadModelMatrix = mRModelMatrix.clone();
        Matrix.rotateM(mHeadModelMatrix, 0, mModelMatrix, 0, mAngleHead, 0, 0, 1);
        Matrix.translateM(mHeadModelMatrix , 0, 
        		headPosition[0], headPosition[1], 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mHeadModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        
        GLES20.glVertexAttribPointer(mPositionHandle, D3GLES20.COORDS_PER_VERTEX, 
        		GLES20.GL_FLOAT, false, STRIDE_BYTES, headVertexBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, headVerticesNum );
		
		//EYE
//        mHeadModelMatrix = mModelMatrix.clone();
       
        Matrix.translateM(mHeadModelMatrix , 0, 
        		eyePosition[0], eyePosition[1], 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mHeadModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        
		GLES20.glVertexAttribPointer(mPositionHandle, D3GLES20.COORDS_PER_VERTEX, 
        		GLES20.GL_FLOAT, false, STRIDE_BYTES, eyeVertexBuffer);
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, 10);
		
		if (Planner.SHOW_TARGET) mPlanner.draw(mVMatrix, mProjMatrix);
	}
	
	private void updateBodyVertexBuffer() {
		Matrix.multiplyMV(bodyStartRot, 0, mHeadModelMatrix, 0, bodyStart4, 0);
		Matrix.multiplyMV(bodyBRot, 0, mHeadModelMatrix, 0, bodyB4, 0);
		float[] bodyVerticesData = D3GLES20.quadBezierCurveVertices(bodyStartRot, bodyBRot, bodyC, bodyEnd, detailsStep, bodyLength);
		bodyVerticesNum = bodyVerticesData.length/D3GLES20.COORDS_PER_VERTEX;
		bodyVertexBuffer = D3GLES20.newFloatBuffer(bodyVerticesData);
	}

	public void moveForward(float distance) {
		distance *= DISTANCE_TO_ANGLE_RATIO;
		Log.v(TAG, "Moving the prey forward to a distance of " + distance);
		float radAngle = (float)Math.toRadians(mAngleFins);
		vx += -FloatMath.sin(radAngle)*distance;
		vy += FloatMath.cos(radAngle)*distance;  
	}
	
	public float getAngle() {
		return mAngleFins; 
	}
	
	public void updateSpeed(float dx, float dy) {
		vx += dx; vy += dy;
		
		vx -= EnvironmentData.frictionCoeff*vx;
		vy -= EnvironmentData.frictionCoeff*vy;
		vLeft -= EnvironmentData.frictionCoeff*vLeft;
		vRight -= EnvironmentData.frictionCoeff*vRight;
		
		
		float curSpeed = FloatMath.sqrt(vx*vx+vy*vy);
		if (curSpeed > MAX_SPEED) {
			float ratio = MAX_SPEED/curSpeed;
			vx *= ratio; vy *= ratio;
		}
	}
	
	public void move(float x, float y) {
		forwardAngleSpeed = 0;
		if (vLeft > 0 && vLeft <= vRight) {
			forwardAngleSpeed = vLeft;
			vRight -= vLeft;
			vLeft = 0;
		}
		else if (vRight > 0 && vRight < vLeft) {
			forwardAngleSpeed = vRight;
			vLeft -= vRight;
			vRight = 0;
		}
		mAngleFins += vLeft - vRight;
		
		if (forwardAngleSpeed > 0) {
			Log.v(TAG, vLeft + " " + vRight + " " + forwardAngleSpeed);
			moveForward(forwardAngleSpeed);
		}
		
		moveFins();
		moveHead();
		mPosX += vx ; mPosY += vy;
		updateSpeed(x, y);
	}

	private void moveFins() {
		if (mLeftFootAngle > minAngle) {
			mLeftFootAngle -= angleBackSpeed;
		}
		if (mRightFootAngle > minAngle) {
			mRightFootAngle -= angleBackSpeed;
		}
//		if (mAngleFins != mAngleHead && Math.abs(mAngleFins - mAngleHead) > MAX_BODY_BEND_ANGLE) {
//			angleSpeedHead = angleSpeedTail;
//		}
//		else angleSpeedHead = angleSpeedHeadDefault;
	}

	private void moveHead() {
//		Log.v(TAG, "Head and fins " + mAngleHead + " " + mAngleFins);
		if (mAngleHead + MAX_BODY_BEND_ANGLE < mAngleFins) {
			mAngleHead += vLeft - vRight;
			//if (mAngleHead > mAngleFins) mAngleHead = mAngleFins;
		}
		else if (mAngleHead - MAX_BODY_BEND_ANGLE > mAngleFins) {
			mAngleHead += vLeft - vRight;
			//if (mAngleHead < mAngleFins) mAngleHead = mAngleFins;
		}
		//TODO: // Straighten the body
	}
	
	public PointF getWorldPosition() {
		return new PointF(D3GLES20.toWorldWidth(mPosX), D3GLES20.toWorldHeight(mPosY));
	}
	
	public PointF getPosition() {
		return new PointF(mPosX, mPosY);
	}

	public void flopLeft() {
		if (vLeft < MAX_SPIN_SPEED) vLeft += angleSpeedIncrement;
		//mLeftFootAngle = maxAngle;
		mAngleHead = -angleSpeedHead;
	}
	
	public void flopRight() {
		if (vRight < MAX_SPIN_SPEED) vRight += angleSpeedIncrement;
//		mRightFootAngle = maxAngle;
		mAngleHead = angleSpeedHead;
	}

	private void doAction(Action nextAction) {
		if (nextAction == null) return;
		switch(nextAction) {
		case flopLeft: flopLeft(); break;
		case flopRight: flopRight(); break;
		case eat: eat(); break;
		default: Log.v(TAG, "Doing nothing.");
		}
	}

	private void eat() {
		Log.v(TAG, "Eating food at " + mPosHeadX + " " + mPosHeadY);
		mEnv.eatFood(mPosHeadX, mPosHeadY);
		mWorldModel.eatFood(mPosHeadX, mPosHeadY);
	}
}
