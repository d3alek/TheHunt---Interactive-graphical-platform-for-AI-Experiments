package d3kod.thehunt.prey;

import java.text.Bidi;

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
	public static boolean AI = false;
	public static int BODY_BEND_DELAY = 1;
	public static int BODY_BEND_DELAY_MAX = 10;
	public static int ACTION_DELAY = 5;
	public static int ACTION_DELAY_MAX = 20;
	private Planner mPlanner;
	private WorldModel mWorldModel;
	private Sensor mSensor;
	private Environment mEnv;
	private PreyData mD;
	private int bodyBendCounter;
	private int actionDelayCounter;
	private float bodyStartAnglePredicted;
	private float bodyBAnglePredicted;
	private float bodyCAnglePredicted;
	private float bodyEndAnglePredicted;
	private int bodyStartAngleRot;
	private int bodyBAngleRot;
	private int bodyCAngleRot;
	private int bodyEndAngleRot;
	private int mInterpolationSampleSize;
	private float meanInterpolation;
	public static boolean angleInterpolation = false;
	public static boolean posInterpolation = false;

	public void update(float dx, float dy) {
		float[] posTemp = { 0.0f, 0.07f, 0.0f, 1.0f };
		
		Matrix.setIdentityM(mD.mHeadModelMatrix, 0);
		Matrix.translateM(mD.mHeadModelMatrix, 0, mD.mPosX, mD.mPosY, 0);
		Matrix.rotateM(mD.mHeadModelMatrix, 0, mD.bodyStartAngle, 0, 0, 1);
		Matrix.multiplyMV(posTemp, 0, mD.mHeadModelMatrix, 0, posTemp, 0);
		
		mD.mPosHeadX = posTemp[0]; 
		mD.mPosHeadY = posTemp[1];
		
		mWorldModel.update(mSensor.sense(mD.mPosHeadX, mD.mPosHeadY, mD.mPosX, mD.mPosY));

		if (AI) {
			if (actionDelayCounter == 0) {
				doAction(mPlanner.nextAction(mWorldModel));
				actionDelayCounter = ACTION_DELAY;
			}
		else actionDelayCounter--;
		}
		move(dx, dy);
	}
	
	public void move(float x, float y) {
		
		if (bodyBendCounter == 0) {
			mD.thrust = Math.abs(mD.bodyCAngleTarget - mD.bodyEndAngleTarget);
			mD.bodyEndAngleTarget = mD.bodyCAngleTarget;
			mD.bodyCAngleTarget = mD.bodyBAngleTarget;
			mD.bodyBAngleTarget = mD.bodyStartAngleTarget;
			bodyBendCounter = BODY_BEND_DELAY;
			
			if (mD.bodyStartAngleTarget > mD.bodyStartAngle + mD.rotateSpeed*BODY_BEND_DELAY) bodyStartAngleRot = mD.rotateSpeed;
			else if (mD.bodyStartAngleTarget < mD.bodyStartAngle - mD.rotateSpeed*BODY_BEND_DELAY) bodyStartAngleRot = -mD.rotateSpeed;
			else bodyStartAngleRot = 0;
			
			if (mD.bodyBAngleTarget > mD.bodyBAngle + mD.rotateSpeed*BODY_BEND_DELAY) bodyBAngleRot = +mD.rotateSpeed;
			else if (mD.bodyBAngleTarget < mD.bodyBAngle - mD.rotateSpeed*BODY_BEND_DELAY) bodyBAngleRot = -mD.rotateSpeed;
			else bodyBAngleRot = 0;
			
			if (mD.bodyCAngleTarget > mD.bodyCAngle + mD.rotateSpeed*BODY_BEND_DELAY) bodyCAngleRot = +mD.rotateSpeed;
			else if (mD.bodyCAngleTarget < mD.bodyCAngle - mD.rotateSpeed*BODY_BEND_DELAY) bodyCAngleRot = -mD.rotateSpeed;
			else bodyCAngleRot = 0;
			
			if (mD.bodyEndAngleTarget > mD.bodyEndAngle + mD.rotateSpeed*BODY_BEND_DELAY) bodyEndAngleRot = +mD.rotateSpeed;
			else if (mD.bodyEndAngleTarget < mD.bodyEndAngle - mD.rotateSpeed*BODY_BEND_DELAY) bodyEndAngleRot = -mD.rotateSpeed;
			else bodyEndAngleRot = 0;
			
//			Log.v(TAG, "Passing spin " + mD.bodyStartAngleTarget + " " + mD.bodyBAngle + " " + mD.bodyCAngle + " " + mD.bodyEndAngle);
		}
		else {
			mD.thrust = 0;
			--bodyBendCounter;
		}
		
		mD.bodyStartAngle += bodyStartAngleRot;
		mD.bodyBAngle += bodyBAngleRot;
		mD.bodyCAngle += bodyCAngleRot;
		mD.bodyEndAngle += bodyEndAngleRot;
		//TODO: fix thurst, maybe do it in this method instead of updateSpeed
		updateSpeed(x, y);
		applyFriction();
		mD.mPosX += mD.vx ; mD.mPosY += mD.vy;
	}
	public void flopLeft() {
		mD.bodyStartAngleTarget += mD.angleFlop;
	}

	public void flopRight() {
		mD.bodyStartAngleTarget -= mD.angleFlop;
	}
	public void updateSpeed(float dx, float dy) {
		mD.vx += dx; mD.vy += dy;
		moveForward(mD.thrust);
	}
	
	public void moveForward(float distance) {
		distance *= mD.DISTANCE_TO_ANGLE_RATIO;
//		Log.v(TAG, "Moving the prey forward to a distance of " + distance + " thrust is " + mD.thrust);
		float radAngle = (float)Math.toRadians(mD.bodyStartAngle);
		mD.vx += -FloatMath.sin(radAngle)*distance;
		mD.vy += FloatMath.cos(radAngle)*distance;  
	}
	
	public void applyFriction() {
		mD.vx -= EnvironmentData.frictionCoeff*mD.vx;
		mD.vy -= EnvironmentData.frictionCoeff*mD.vy;
	}
	
	public Prey(float screenWidth, float screenHeight, Environment env) {
		mD = new PreyData();
		
		mD.delayV = new float[mD.delayVLength][2];
		
		mWorldModel = new WorldModel(screenWidth, screenHeight);
		mPlanner = new Planner();
		mEnv = env;
		mSensor = new Sensor(mEnv);
		
		Matrix.setIdentityM(mD.mModelMatrix, 0);
		mD.mPosX = mD.mPosY = 0;

		for (int i = 0; i < 3; ++i) {
			mD.bodyStart4[i] = mD.bodyStart[i];
		}
		for (int i = 0; i < 3; ++i) {
			mD.bodyB4[i] = mD.bodyB[i];
		}
		for (int i = 0; i < 3; ++i) {
			mD.bodyC4[i] = mD.bodyC[i];
		}
		for (int i = 0; i < 3; ++i) {
			mD.bodyEnd4[i] = mD.bodyEnd[i];
		}
		mD.headVerticesData = calcHeadVerticesData();
		mD.leftFinVerticesData = calcLeftFinVerticesData();
		mD.rightFinVerticesData = calcRightFinVerticesData();
		mD.eyeVertexData = D3GLES20.circleVerticesData(mD.eyePosition, mD.eyeSize, mD.eyeDetailsLevel);
		
		mD.finVerticesNum = mD.rightFinVerticesData.length / D3GLES20.COORDS_PER_VERTEX;
		mD.leftFinVertexBuffer = D3GLES20.newFloatBuffer(mD.leftFinVerticesData);
		mD.rightFinVertexBuffer = D3GLES20.newFloatBuffer(mD.rightFinVerticesData);
		mD.headVertexBuffer = D3GLES20.newFloatBuffer(mD.headVerticesData);
		mD.eyeVertexBuffer = D3GLES20.newFloatBuffer(mD.eyeVertexData);
		
		int vertexShaderHandle = D3GLES20.loadShader(GLES20.GL_VERTEX_SHADER, mD.vertexShaderCode);
        int fragmentShaderHandle = D3GLES20.loadShader(GLES20.GL_FRAGMENT_SHADER, mD.fragmentShaderCode);
        
        mD.mProgram = D3GLES20.createProgram(vertexShaderHandle, fragmentShaderHandle);
        
        mD.mMVPMatrixHandle = GLES20.glGetUniformLocation(mD.mProgram, "u_MVPMatrix"); 
        mD.mPositionHandle = GLES20.glGetAttribLocation(mD.mProgram, "a_Position");
        mD.mColorHandle = GLES20.glGetUniformLocation(mD.mProgram, "u_Color");
	}
	
	private float[] calcRightFinVerticesData() {
		return D3GLES20.quadBezierCurveVertices(
				mD.rightFinStart, mD.rightFinB, mD.rightFinC, mD.rightFinEnd, mD.detailsStep, mD.finSize);
	}

	private float[] calcLeftFinVerticesData() {
		return D3GLES20.quadBezierCurveVertices(
				mD.leftFinStart, mD.leftFinB, mD.leftFinC, mD.leftFinEnd, mD.detailsStep, mD.finSize);
	}

	private float[] calcHeadVerticesData() {
		float[] part1 = D3GLES20.quadBezierCurveVertices(
				mD.headPart1Start, mD.headPart1B, mD.headPart1C, mD.headPart2Start, mD.detailsStep, mD.headSize);
		float[] part2 = D3GLES20.quadBezierCurveVertices(
				mD.headPart2Start, mD.headPart2B, mD.headPart2C, mD.headPart3Start, mD.detailsStep, mD.headSize);
		float[] part3 = D3GLES20.quadBezierCurveVertices(
				mD.headPart3Start, mD.headPart3B, mD.headPart3C, mD.headPart1Start, mD.detailsStep, mD.headSize);
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
		mD.headVerticesNum = headVerticesData.length / D3GLES20.COORDS_PER_VERTEX;
		return headVerticesData;
	}

	public void draw(float[] mVMatrix, float[] mProjMatrix, float interpolation) {
//		GLES20.glLineWidth(2f);
		
        // Interpolate
		if (angleInterpolation) {
			bodyStartAnglePredicted = mD.bodyStartAngle + bodyStartAngleRot * interpolation;
			bodyBAnglePredicted = mD.bodyBAngle + bodyBAngleRot * interpolation;
			bodyCAnglePredicted = mD.bodyCAngle + bodyCAngleRot * interpolation;
			bodyEndAnglePredicted = mD.bodyEndAngle + bodyEndAngleRot * interpolation;
		}
		else {
			bodyStartAnglePredicted = mD.bodyStartAngle;
			bodyBAnglePredicted = mD.bodyBAngle;
			bodyCAnglePredicted = mD.bodyCAngle;
			bodyEndAnglePredicted = mD.bodyEndAngle;
		}
        Matrix.setIdentityM(mD.mBodyStartRMatrix, 0);
        Matrix.setIdentityM(mD.mBodyBRMatrix, 0);
        Matrix.setIdentityM(mD.mBodyCRMatrix, 0);
        Matrix.setIdentityM(mD.mBodyEndRMatrix, 0);
        Matrix.rotateM(mD.mBodyStartRMatrix, 0, bodyStartAnglePredicted, 0, 0, 1);
        Matrix.rotateM(mD.mBodyBRMatrix, 0, bodyBAnglePredicted, 0, 0, 1);
        Matrix.rotateM(mD.mBodyCRMatrix, 0, bodyCAnglePredicted, 0, 0, 1);
        Matrix.rotateM(mD.mBodyEndRMatrix, 0, bodyEndAnglePredicted, 0, 0, 1);
		
        if (posInterpolation) {
        	mD.mPredictedPosX = mD.mPosX + mD.vx*interpolation; 
        	mD.mPredictedPosY = mD.mPosY + mD.vy*interpolation;
        }
        else {
        	mD.mPredictedPosX = mD.mPosX; mD.mPredictedPosY = mD.mPosY;
        }
        
        // Rotate the body vertices
        
        updateBodyVertexBuffer();
        
        // Start Drawing
        
		GLES20.glUseProgram(mD.mProgram);
		
        GLES20.glUniform4fv(mD.mColorHandle, 1, mD.preyColor , 0);
        GLES20.glEnableVertexAttribArray(mD.mColorHandle);
        
		// Body
        
        Matrix.setIdentityM(mD.mModelMatrix, 0);
        Matrix.translateM(mD.mModelMatrix , 0, mD.mPredictedPosX, mD.mPredictedPosY, 0);
        Matrix.multiplyMM(mD.mMVPMatrix, 0, mVMatrix, 0, mD.mModelMatrix, 0);
        Matrix.multiplyMM(mD.mMVPMatrix, 0, mProjMatrix, 0, mD.mMVPMatrix, 0);
        
        GLES20.glVertexAttribPointer(mD.mPositionHandle, D3GLES20.COORDS_PER_VERTEX, 
        		GLES20.GL_FLOAT, false, mD.STRIDE_BYTES, mD.bodyVertexBuffer);
        GLES20.glEnableVertexAttribArray(mD.mPositionHandle);
        
        GLES20.glUniformMatrix4fv(mD.mMVPMatrixHandle, 1, false, mD.mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, mD.bodyVerticesNum);
        
        // Feet
        
        Matrix.rotateM(mD.mFeetModelMatrix, 0, mD.mModelMatrix, 0, bodyEndAnglePredicted, 0, 0, 1);
        Matrix.translateM(mD.mFeetModelMatrix, 0, 
        		mD.leftFootPosition[0], mD.leftFootPosition[1], 0);
        Matrix.rotateM(mD.mFeetModelMatrix, 0, mD.mLeftFootAngle, 0, 0, 1);
        Matrix.multiplyMM(mD.mMVPMatrix, 0, mVMatrix, 0, mD.mFeetModelMatrix, 0);
        Matrix.multiplyMM(mD.mMVPMatrix, 0, mProjMatrix, 0, mD.mMVPMatrix, 0);
        
        GLES20.glUniformMatrix4fv(mD.mMVPMatrixHandle, 1, false, mD.mMVPMatrix, 0);
        
        GLES20.glVertexAttribPointer(mD.mPositionHandle, D3GLES20.COORDS_PER_VERTEX, 
        		GLES20.GL_FLOAT, false, mD.STRIDE_BYTES, mD.leftFinVertexBuffer);
        GLES20.glEnableVertexAttribArray(mD.mPositionHandle);
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, mD.finVerticesNum);
        
        Matrix.rotateM(mD.mFeetModelMatrix, 0, mD.mModelMatrix, 0, bodyEndAnglePredicted, 0, 0, 1);
        Matrix.translateM(mD.mFeetModelMatrix, 0, 
        		-mD.leftFootPosition[0], mD.leftFootPosition[1], 0);
        Matrix.rotateM(mD.mFeetModelMatrix, 0, -mD.mRightFootAngle, 0, 0, 1);
        Matrix.multiplyMM(mD.mMVPMatrix, 0, mVMatrix, 0, mD.mFeetModelMatrix, 0);
        Matrix.multiplyMM(mD.mMVPMatrix, 0, mProjMatrix, 0, mD.mMVPMatrix, 0);
        
        GLES20.glUniformMatrix4fv(mD.mMVPMatrixHandle, 1, false, mD.mMVPMatrix, 0);
        
        GLES20.glVertexAttribPointer(mD.mPositionHandle, D3GLES20.COORDS_PER_VERTEX, 
        		GLES20.GL_FLOAT, false, mD.STRIDE_BYTES, mD.rightFinVertexBuffer);
        GLES20.glEnableVertexAttribArray(mD.mPositionHandle);
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, mD.finVerticesNum);
        
        // Head
        Matrix.rotateM(mD.mHeadModelMatrix, 0, mD.mModelMatrix, 0, bodyStartAnglePredicted, 0, 0, 1);
        Matrix.translateM(mD.mHeadModelMatrix , 0, 
        		mD.headPosition[0], mD.headPosition[1], 0);
        Matrix.multiplyMM(mD.mMVPMatrix, 0, mVMatrix, 0, mD.mHeadModelMatrix, 0);
        Matrix.multiplyMM(mD.mMVPMatrix, 0, mProjMatrix, 0, mD.mMVPMatrix, 0);
        
        GLES20.glUniformMatrix4fv(mD.mMVPMatrixHandle, 1, false, mD.mMVPMatrix, 0);
        
        GLES20.glVertexAttribPointer(mD.mPositionHandle, D3GLES20.COORDS_PER_VERTEX, 
        		GLES20.GL_FLOAT, false, mD.STRIDE_BYTES, mD.headVertexBuffer);
        GLES20.glEnableVertexAttribArray(mD.mPositionHandle);
		GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, mD.headVerticesNum );
		
		//EYE
//        mHeadModelMatrix = mModelMatrix.clone();
       
        Matrix.translateM(mD.mHeadModelMatrix , 0, 
        		mD.eyePosition[0], mD.eyePosition[1], 0);
        Matrix.multiplyMM(mD.mMVPMatrix, 0, mVMatrix, 0, mD.mHeadModelMatrix, 0);
        Matrix.multiplyMM(mD.mMVPMatrix, 0, mProjMatrix, 0, mD.mMVPMatrix, 0);
        
        GLES20.glUniformMatrix4fv(mD.mMVPMatrixHandle, 1, false, mD.mMVPMatrix, 0);
        
		GLES20.glVertexAttribPointer(mD.mPositionHandle, D3GLES20.COORDS_PER_VERTEX, 
        		GLES20.GL_FLOAT, false, mD.STRIDE_BYTES, mD.eyeVertexBuffer);
		GLES20.glEnableVertexAttribArray(mD.mPositionHandle);
		GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, mD.eyeDetailsLevel);
		
		if (Planner.SHOW_TARGET) mPlanner.draw(mVMatrix, mProjMatrix);
	}
	
	private void updateBodyVertexBuffer() {
		Matrix.multiplyMV(mD.bodyStartRot, 0, mD.mBodyStartRMatrix, 0, mD.bodyStart4, 0);
		Matrix.multiplyMV(mD.bodyBRot, 0, mD.mBodyBRMatrix, 0, mD.bodyB4, 0);
		Matrix.multiplyMV(mD.bodyCRot, 0, mD.mBodyCRMatrix, 0, mD.bodyC4, 0);
		Matrix.multiplyMV(mD.bodyEndRot, 0, mD.mBodyEndRMatrix, 0, mD.bodyEnd4, 0);
		float[] bodyVerticesData = D3GLES20.quadBezierCurveVertices(mD.bodyStartRot, mD.bodyBRot, mD.bodyCRot, mD.bodyEndRot, mD.detailsStep, mD.bodyLength);
		mD.bodyVerticesNum = bodyVerticesData.length/D3GLES20.COORDS_PER_VERTEX;
		mD.bodyVertexBuffer = D3GLES20.newFloatBuffer(bodyVerticesData);
	}

	public PointF getWorldPosition() {
		return new PointF(D3GLES20.toWorldWidth(mD.mPosX), D3GLES20.toWorldHeight(mD.mPosY));
	}

	public PointF getPosition() {
		return new PointF(mD.mPosX, mD.mPosY);
	}

	private void doAction(Action nextAction) {
		if (nextAction == null) return;
		switch(nextAction) {
		case flopLeft: flopLeft(); break;
		case flopRight: flopRight(); break;
		case eat: eat(); break;
		case none: break;
		default: Log.v(TAG, "Could not process action!");
		}
	}

	private void eat() {
		Log.v(TAG, "Eating food at " + mD.mPosHeadX + " " + mD.mPosHeadY);
		mEnv.eatFood(mD.mPosHeadX, mD.mPosHeadY);
		mWorldModel.eatFood(mD.mPosHeadX, mD.mPosHeadY);
	}
}
