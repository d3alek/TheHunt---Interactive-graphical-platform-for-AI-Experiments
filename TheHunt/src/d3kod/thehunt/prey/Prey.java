package d3kod.thehunt.prey;

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
	private static final int DELAY_START = 2;
	private static final int ACTION_DELAY = 5;
	private Planner mPlanner;
	private WorldModel mWorldModel;
	private Sensor mSensor;
	private Environment mEnv;
	private PreyData mD;
	private int delay;
	private int actionDelay;

	public void update(float dx, float dy) {
		float[] posTemp = { 0.0f, 0.07f, 0.0f, 1.0f };
		
		Matrix.setIdentityM(mD.mHeadModelMatrix, 0);
		Matrix.translateM(mD.mHeadModelMatrix, 0, mD.mPosX, mD.mPosY, 0);
		Matrix.rotateM(mD.mHeadModelMatrix, 0, mD.bodyStartAngle, 0, 0, 1);
		Matrix.multiplyMV(posTemp, 0, mD.mHeadModelMatrix, 0, posTemp, 0);
		
		mD.mPosHeadX = posTemp[0]; 
		mD.mPosHeadY = posTemp[1];
		
		mWorldModel.update(mSensor.sense(mD.mPosHeadX, mD.mPosHeadY, mD.mPosX, mD.mPosY));
		
		if (actionDelay == 0) {
			doAction(mPlanner.nextAction(mWorldModel));
			actionDelay = ACTION_DELAY;
		}
		else actionDelay--;
		move(dx, dy);
	}
	
	public void move(float x, float y) {
		
		if (delay == 0) {
//			if (mD.bodyEndAngleTarget != mD.bodyCAngleTarget) {
//				//going to change the end angle target and thus create thrust
//				//TODO: think it through
//				mD.thrust = Math.abs(mD.bodyCAngleTarget - mD.bodyEndAngle);
//			}
//			if (mD.bodyEndAngleTarget != mD.bodyCAngleTarget) {
				//going to change the end angle target and thus create thrust
				mD.thrust = Math.abs(mD.bodyCAngleTarget - mD.bodyEndAngleTarget);
//			}
			mD.bodyEndAngleTarget = mD.bodyCAngleTarget;
			mD.bodyCAngleTarget = mD.bodyBAngleTarget;
			mD.bodyBAngleTarget = mD.bodyStartAngleTarget;
			delay = DELAY_START;
//			Log.v(TAG, "Passing spin " + mD.bodyStartAngleTarget + " " + mD.bodyBAngle + " " + mD.bodyCAngle + " " + mD.bodyEndAngle);
		}
		else {
			mD.thrust = 0;
			--delay;
		}
		
		if (mD.bodyStartAngleTarget > mD.bodyStartAngle) mD.bodyStartAngle += mD.rotateSpeed;
		else if (mD.bodyStartAngleTarget < mD.bodyStartAngle) mD.bodyStartAngle -= mD.rotateSpeed;

		if (mD.bodyBAngleTarget > mD.bodyBAngle) mD.bodyBAngle += mD.rotateSpeed;
		else if (mD.bodyBAngleTarget < mD.bodyBAngle) mD.bodyBAngle -= mD.rotateSpeed;

		if (mD.bodyCAngleTarget > mD.bodyCAngle) mD.bodyCAngle += mD.rotateSpeed;
		else if (mD.bodyCAngleTarget < mD.bodyCAngle) mD.bodyCAngle -= mD.rotateSpeed;

		if (mD.bodyEndAngleTarget > mD.bodyEndAngle) mD.bodyEndAngle += mD.rotateSpeed;
		else if (mD.bodyEndAngleTarget < mD.bodyEndAngle) mD.bodyEndAngle -= mD.rotateSpeed;

		updateSpeed(x, y);
		mD.mPosX += mD.vx ; mD.mPosY += mD.vy;
		applyFriction();
	}
	public void flopLeft() {
//		delay = DELAY_START;
		mD.bodyStartAngleTarget += mD.angleFlopHead;
	}

	public void flopRight() {
//		delay = DELAY_START;
		mD.bodyStartAngleTarget -= mD.angleFlopHead;
	}
	public void updateSpeed(float dx, float dy) {
		mD.vx += dx; mD.vy += dy;
		moveForward(mD.thrust);
	}
	
	public void moveForward(float distance) {
		distance *= mD.DISTANCE_TO_ANGLE_RATIO;
		Log.v(TAG, "Moving the prey forward to a distance of " + distance + " thrust is " + mD.thrust);
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
//		mD.bodyStart4[3] = 1;
		for (int i = 0; i < 3; ++i) {
			mD.bodyB4[i] = mD.bodyB[i];
		}
		for (int i = 0; i < 3; ++i) {
			mD.bodyC4[i] = mD.bodyC[i];
		}
		for (int i = 0; i < 3; ++i) {
			mD.bodyEnd4[i] = mD.bodyEnd[i];
		}
//		mD.bodyB4[3] = 1;
		mD.headVerticesData = calcHeadVerticesData();
		mD.leftFinVerticesData = calcLeftFinVerticesData();
		mD.rightFinVerticesData = calcRightFinVerticesData();
		mD.eyeVertexData = D3GLES20.circleVerticesData(mD.eyePosition, mD.eyeSize, mD.eyeDetailsLevel);
		
		mD.finVerticesNum = mD.rightFinVerticesData.length / D3GLES20.COORDS_PER_VERTEX;
//		bodyVerticesNum =
//		bodyVertexBuffer = D3GLES20.newFloatBuffer(bodyVerticesData);
		mD.leftFinVertexBuffer = D3GLES20.newFloatBuffer(mD.leftFinVerticesData);
		mD.rightFinVertexBuffer = D3GLES20.newFloatBuffer(mD.rightFinVerticesData);
		mD.headVertexBuffer = D3GLES20.newFloatBuffer(mD.headVerticesData);
		mD.eyeVertexBuffer = D3GLES20.newFloatBuffer(mD.eyeVertexData);
		
		int vertexShaderHandle = TheHuntRenderer.loadShader(GLES20.GL_VERTEX_SHADER, mD.vertexShaderCode);
        int fragmentShaderHandle = TheHuntRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, mD.fragmentShaderCode);
        
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
//		GLES20.glEnable(GLES20.GL_PO)
//		GLES20.glLineWidth(3f);
		
        //Body rotate matrices
        Matrix.setIdentityM(mD.mBodyStartRMatrix, 0);
        Matrix.setIdentityM(mD.mBodyBRMatrix, 0);
        Matrix.setIdentityM(mD.mBodyCRMatrix, 0);
        Matrix.setIdentityM(mD.mBodyEndRMatrix, 0);
        Matrix.rotateM(mD.mBodyStartRMatrix, 0, mD.bodyStartAngle, 0, 0, 1);
        Matrix.rotateM(mD.mBodyBRMatrix, 0, mD.bodyBAngle, 0, 0, 1);
        Matrix.rotateM(mD.mBodyCRMatrix, 0, mD.bodyCAngle, 0, 0, 1);
        Matrix.rotateM(mD.mBodyEndRMatrix, 0, mD.bodyEndAngle, 0, 0, 1);
		
		GLES20.glUseProgram(mD.mProgram);
		
        GLES20.glUniform4fv(mD.mColorHandle, 1, mD.preyColor , 0);
        GLES20.glEnableVertexAttribArray(mD.mColorHandle);
        
        mD.mPredictedPosX = mD.mPosX + mD.vx*interpolation; mD.mPredictedPosY = mD.mPosY + mD.vy*interpolation;
        Matrix.setIdentityM(mD.mModelMatrix, 0);
        Matrix.translateM(mD.mModelMatrix , 0, mD.mPredictedPosX, mD.mPredictedPosY, 0);
//      TODO:  mPredictedAngle = mAngle + // depend on the deg, make a flag for rotating
//        Matrix.rotateM(mD.mRModelMatrix, 0, mD.mModelMatrix, 0, mD.bodyEndAngle, 0, 0, 1);
        Matrix.rotateM(mD.mRModelMatrix, 0, mD.mModelMatrix, 0, 0, 0, 0, 1);
//        mD.mRModelMatrix = mD.mModelMatrix.clone();
        
//        Matrix.rotateM(mD.mBodyStartRotateMatrix, 0, mD.mModelMatrix, 0, mD.bodyStartAngle, 0, 0, 1);
        Matrix.multiplyMM(mD.mMVPMatrix, 0, mVMatrix, 0, mD.mRModelMatrix, 0);
        Matrix.multiplyMM(mD.mMVPMatrix, 0, mProjMatrix, 0, mD.mMVPMatrix, 0);
        
		// Body
        updateBodyVertexBuffer();
        
        GLES20.glVertexAttribPointer(mD.mPositionHandle, D3GLES20.COORDS_PER_VERTEX, 
        		GLES20.GL_FLOAT, false, mD.STRIDE_BYTES, mD.bodyVertexBuffer);
        GLES20.glEnableVertexAttribArray(mD.mPositionHandle);
        
        GLES20.glUniformMatrix4fv(mD.mMVPMatrixHandle, 1, false, mD.mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, mD.bodyVerticesNum);
        
        // Feet
//        mD.mFeetModelMatrix = mD.mRModelMatrix.clone();
        Matrix.rotateM(mD.mFeetModelMatrix, 0, mD.mModelMatrix, 0, mD.bodyEndAngle, 0, 0, 1);
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
        
//        mD.mFeetModelMatrix = mD.mRModelMatrix.clone();
        Matrix.rotateM(mD.mFeetModelMatrix, 0, mD.mModelMatrix, 0, mD.bodyEndAngle, 0, 0, 1);
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
//        mHeadModelMatrix = mRModelMatrix.clone();
        Matrix.rotateM(mD.mHeadModelMatrix, 0, mD.mModelMatrix, 0, mD.bodyStartAngle, 0, 0, 1);
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
//		Matrix.multiplyMV(mD.bodyBRot, 0, mD.mHeadModelMatrix, 0, mD.bodyB4, 0);
		float[] bodyVerticesData = D3GLES20.quadBezierCurveVertices(mD.bodyStartRot, mD.bodyBRot, mD.bodyCRot, mD.bodyEndRot, mD.detailsStep, mD.bodyLength);
		mD.bodyVerticesNum = bodyVerticesData.length/D3GLES20.COORDS_PER_VERTEX;
		mD.bodyVertexBuffer = D3GLES20.newFloatBuffer(bodyVerticesData);
	}
	
//	public float getAngle() {
//		return mD.mAngleFins; 
//	}
	
//	public void updateSpeed(float dx, float dy) {
//		mD.vx += dx; mD.vy += dy;
//		
//		mD.vx -= EnvironmentData.frictionCoeff*mD.vx;
//		mD.vy -= EnvironmentData.frictionCoeff*mD.vy;
//		
//		mD.vHeadLeft -= EnvironmentData.frictionCoeff*mD.vHeadLeft;
//		mD.vHeadRight -= EnvironmentData.frictionCoeff*mD.vHeadRight;
//		
//		mD.vTailLeft -= EnvironmentData.frictionCoeff*mD.vTailLeft;
//		mD.vTailRight -= EnvironmentData.frictionCoeff*mD.vTailRight;
//		
//		float curSpeed = FloatMath.sqrt(mD.vx*mD.vx+mD.vy*mD.vy);
//		if (curSpeed > mD.MAX_SPEED) {
//			float ratio = mD.MAX_SPEED/curSpeed;
//			mD.vx *= ratio; mD.vy *= ratio;
//		}
//	}
	
//	public void move(float x, float y) {
//		mD.forwardAngleSpeed = 0;
//		if (mD.vHeadLeft > 0 && mD.vHeadLeft <= mD.vHeadRight) {
//			mD.forwardAngleSpeed = mD.vHeadLeft;
//			mD.vHeadRight -= mD.vHeadLeft;
////			mD.vTailRight -= mD.vTailLeft;
//			mD.vHeadLeft = 0;
////			mD.vTailLeft = 0;
//		}
//		else if (mD.vHeadRight > 0 && mD.vHeadRight < mD.vHeadLeft) {
//			mD.forwardAngleSpeed = mD.vHeadRight;
//			mD.vHeadLeft -= mD.vHeadRight;
////			mD.vTailLeft = mD.vTailRight;
//			mD.vHeadRight = 0;
////			mD.vTailRight = 0;
//		}
//		
//		progressVelocity();
//		
//		if (mD.forwardAngleSpeed > 0) {
//			Log.v(TAG, mD.vHeadLeft + " " + mD.vHeadRight + " " + mD.forwardAngleSpeed);
//			moveForward(mD.forwardAngleSpeed);
//		}
//		
//		moveFins();
//		moveHead();
//		mD.mPosX += mD.vx ; mD.mPosY += mD.vy;
//		updateSpeed(x, y);
//	}

//	private void progressVelocity() {
//		mD.vTailLeft = mD.delayV[mD.delayVLength-1][0];
//		mD.vTailRight = mD.delayV[mD.delayVLength-1][1];
////		if (mD.vTailLeft != 0) Log.v(TAG, "vTailLeft is not zero!");
////		String out = "progressVelocity: ";
//		for (int i = mD.delayVLength-1; i > 0; --i) {
////			out += (mD.delayV[i][0]) + " ";
//			mD.delayV[i][0] = mD.delayV[i-1][0];
//			mD.delayV[i][1] = mD.delayV[i-1][1];
//		}
////		Log.v(TAG, out);
//		mD.delayV[0][0] = mD.vHeadLeft;
//		mD.delayV[0][1] = mD.vHeadRight;
//	}

//	private void moveFins() {
////		if (mD.mLeftFootAngle > mD.minAngle) {
////			mD.mLeftFootAngle -= mD.angleBackSpeed;
////		}
////		if (mD.mRightFootAngle > mD.minAngle) {
////			mD.mRightFootAngle -= mD.angleBackSpeed;
////		}
////		if (mD.mAngleFins < mD.mAngleHead) {
////		if (mD.mAngleFins - mD.mAngleHead < mD.mAngleHead
////				|| -mD.mAngleFins + mD.mAngleHead > mD.mAngleHead)
////		if (Math.abs(mD.mAngleHead - mD.mAngleFins) < mD.MAX_BODY_BEND_ANGLE)
//			mD.mAngleFins += mD.vTailLeft - mD.vTailRight;
////		else mD.mAngleFins += mD.vHeadLeft - mD.vHeadRight;
////		}
////		if (mD.mAngleFins > mD.mAngleHead) {
////			mD.mAngleFins -= mD.angleBackSpeed;
////		}
//	}

//	private void moveHead() {
////		Log.v(TAG, "Head and fins " + mAngleHead + " " + mAngleFins);
////		if (mD.mAngleHead + mD.MAX_BODY_BEND_ANGLE < mD.mAngleFins) {
////			mD.mAngleHead += mD.vLeft - mD.vRight;
////			//if (mAngleHead > mAngleFins) mAngleHead = mAngleFins;
////		}
////		else if (mD.mAngleHead - mD.MAX_BODY_BEND_ANGLE > mD.mAngleFins) {
////			mD.mAngleHead += mD.vLeft - mD.vRight;
////			//if (mAngleHead < mAngleFins) mAngleHead = mAngleFins;
////		}
//		//TODO: // Straighten the body
//		mD.mAngleHead += mD.vHeadLeft - mD.vHeadRight;
//	}

	public PointF getWorldPosition() {
		return new PointF(D3GLES20.toWorldWidth(mD.mPosX), D3GLES20.toWorldHeight(mD.mPosY));
	}

	public PointF getPosition() {
		return new PointF(mD.mPosX, mD.mPosY);
	}

//	public void flopLeft() {
////		if (mD.mAngleHead - mD.mAngleFins > mD.MAX_BODY_BEND_ANGLE) {
////			Log.v(TAG, "Angle head: " + mD.mAngleHead + " Angle fins: " + mD.mAngleFins);
////			return;
////		}
//		targetAngleHead
//		if (mD.vHeadLeft < mD.MAX_SPIN_SPEED) {
//			mD.vHeadLeft += mD.angleSpeedIncrement;
////			mD.vTailLeft -= mD.angleSpeedIncrement;
//		}
////		mD.mLeftFootAngle = mD.maxAngle;
////		mD.mAngleHead += mD.angleSpeedHead;
//	}
//
//	public void flopRight() {
////		if (-mD.mAngleHead + mD.mAngleFins > mD.MAX_BODY_BEND_ANGLE) {
////			Log.v(TAG, "Angle head: " + mD.mAngleHead + " Angle fins: " + mD.mAngleFins);
////			return; 
////		}
////		if (mD.vHeadRight < mD.MAX_SPIN_SPEED) {
////			mD.vHeadRight += mD.angleSpeedIncrement;
//////			mD.vTailRight -= mD.angleSpeedIncrement;
////		}
////		mD.mRightFootAngle = mD.maxAngle;
////		mD.mAngleHead -= mD.angleSpeedHead;
//	}

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
