package d3kod.thehunt;

import java.util.HashMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.hardware.SensorEvent;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import d3kod.d3gles20.D3GLES20;
import d3kod.d3gles20.D3Maths;
import d3kod.d3gles20.TextureManager;
import d3kod.d3gles20.Utilities;
import d3kod.d3gles20.shapes.D3Shape;
import d3kod.d3gles20.shapes.D3TempCircle;
import d3kod.thehunt.environment.Environment;
import d3kod.thehunt.environment.EnvironmentData;
import d3kod.thehunt.floating_text.CatchText;
import d3kod.thehunt.floating_text.PlokText;
import d3kod.thehunt.prey.Prey;

public class TheHuntRenderer implements GLSurfaceView.Renderer {
	
	private static final String TAG = "TheHuntRenderer";
	private static final int SMOOTHING_SIZE = 30;
	public static boolean LOGGING_TIME = false;
	public static boolean SHOW_TILES = false;
	public static boolean SHOW_CURRENTS = false;
	public static boolean FEED_WITH_TOUCH = false;
	public static boolean NET_WITH_TOUCH = true;

	private static final boolean SHOW_CIRCLE_CONTAINS_CHECKS = false;
	
	
	private float[] mProjMatrix = new float[16]; 
	private float[] mVMatrix = new float[16];
//	private long timePreviousNS;
	private Environment mEnv;
	public Prey mPrey;
	private CatchNet mNet;
	
    public static final int TICKS_PER_SECOND = 30;
    private static final int RELEASE_DELAY = 1; // in seconds
	private static final int RELEASE_TICKS = RELEASE_DELAY*TICKS_PER_SECOND;
    private final int MILLISEC_PER_TICK = 1000 / TICKS_PER_SECOND;
    private final int MAX_FRAMESKIP = 5;
	private long next_game_tick;
	private Context mContext;
	private long mslf;
	private long smoothMspf;
	private int smoothingCount;
	private int mCaughtCounter;
	private D3TempCircle tempCircle;
	private boolean mGraphicsInitialized = false;
	private HashMap<Integer, D3Shape> mShapes;
	private TextureManager tm;
	private int releaseCountdown;
	private float mScreenToWorldRatioWidth;
	private float mScreenToWorldRatioHeight;
	private Point prev;
	private Camera mCamera;
//	private static float mViewLeft;
//	private static float mViewBottom;
//	private static float mViewWidth;
//	private static float mViewHeight;
	private static int mScreenWidthPx;
	private static int mScreenHeightPx;
	public static float[] bgColor = {
			0.8f, 0.8f, 0.8f, 1.0f};

	private final static int worldWidthPx = 1400;
	private final static int worldHeightPx = 900;
	
	public void onSensorChanged(SensorEvent event) {
		
	}
	
//	@SuppressLint("NewApi")
	public TheHuntRenderer(Context context) {
		mContext = context;
	    mEnv = null;
	    mPrey = null;
	    mNet = null;
	    mCaughtCounter = 0;
	    mShapes = null;
	    tm = new TextureManager(mContext);
	}

	/**
	 * The Surface is created/init()
	 */
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		GLES20.glClearColor(bgColor[0], bgColor[1], bgColor[2], bgColor[3]);
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
	}
	/**
	 * If the surface changes, reset the view
	 */
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		
		mScreenWidthPx = width;
		mScreenHeightPx = height;
		
		mScreenToWorldRatioWidth = mScreenWidthPx/(float)worldWidthPx;
		mScreenToWorldRatioHeight = mScreenHeightPx/(float)worldHeightPx;
		mCamera = new Camera(mScreenToWorldRatioWidth, 
				mScreenToWorldRatioHeight, worldWidthPx/(float)worldHeightPx);
		
	    Log.v(TAG, "mScreenWidth " + mScreenWidthPx + " mScreenHeight " + mScreenHeightPx);
	    
		if (mEnv == null) mEnv = new Environment(worldWidthPx, worldHeightPx);
	    if (mPrey == null) mPrey = new Prey(EnvironmentData.mScreenWidth, EnvironmentData.mScreenHeight, mEnv, tm);
	    if (mNet == null) mNet = new CatchNet(mEnv, tm);
	    if (!mGraphicsInitialized ) {
	    	mEnv.initGraphics(mContext);
	    	mPrey.initGraphics();
	    	mGraphicsInitialized = true;
	    }
	    
	    float ratio = (float) worldWidthPx / worldHeightPx;
	    
	    if (width > height) Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
	    else Matrix.frustumM(mProjMatrix, 0, -1, 1, -1/ratio, 1/ratio, 1, 10);
	}
	/**
	 * Here we do our drawing
	 */
	public void onDrawFrame(GL10 unused) {

		int loops = 0;
		while (next_game_tick < System.currentTimeMillis() && loops < MAX_FRAMESKIP) {
			updateWorld();
			next_game_tick += MILLISEC_PER_TICK;
			loops++;
		}
		
		mslf = System.currentTimeMillis();
		float interpolation = (System.currentTimeMillis() + MILLISEC_PER_TICK - next_game_tick) / (float) MILLISEC_PER_TICK;
		drawWorld(interpolation);
		long mspf = System.currentTimeMillis() - mslf;
		smoothMspf = (smoothMspf*smoothingCount + mspf)/(smoothingCount+1);
		smoothingCount++;
		if (smoothingCount >= SMOOTHING_SIZE) {
			smoothingCount = 0;
			((TheHunt) mContext).runOnUiThread(new Runnable() {
				public void run() {
//					((TheHunt) mContext).mPreyState.setText(Planner.mState.toString());
					((TheHunt) mContext).mMSperFrame.setText(smoothMspf + "");
				}
			});
		}
	}		

	private void updateWorld() {
		PointF preyPos = mPrey.getPosition();
		
		PointF curDirDelta = mEnv.data.getTileFromPos(preyPos).getDir().getDelta();
		mPrey.update(curDirDelta.x * EnvironmentData.currentStep, 
				curDirDelta.y * EnvironmentData.currentStep);
		
		if (SHOW_CIRCLE_CONTAINS_CHECKS && !mPrey.getCaught() && mNet.isBuilt()) {
			tempCircle = D3GLES20.newContainsCheckCircle(mNet.getGraphicIndex(), preyPos.x, preyPos.y);
		}
		if (mPrey.getCaught()) {
			releaseCountdown--;
			if (releaseCountdown <= 0) mPrey.release();
		}
//		if (!mPrey.getCaught() && mNet.isBuilt() && D3GLES20.contains(mNet.getGraphicIndex(), preyPos.x, preyPos.y)) {
		if (!mPrey.getCaught() && mNet.tryToCatch() && D3GLES20.contains(mNet.getGraphicIndex(), preyPos.x, preyPos.y)) {
			Log.v(TAG, "Prey is caught!");
			mPrey.setCaught(true);
			mNet.caughtPrey(mPrey);
			mCaughtCounter += 1;
			((TheHunt) mContext).runOnUiThread(new Runnable() {
				public void run() {
					((TheHunt) mContext).mCaughtCounter.setText(mCaughtCounter + "");
				}
			});
			D3GLES20.putExpiringShape(new CatchText(preyPos.x, preyPos.y, tm));
			releaseCountdown = RELEASE_TICKS;
		}
		
		mNet.update();
		mEnv.update();
	}

	private void drawWorld(float interpolation) {
		int clearMask = GLES20.GL_COLOR_BUFFER_BIT;
		
		if (MultisampleConfigChooser.usesCoverageAa()) {
			final int GL_COVERAGE_BUFFER_BIT_NV = 0x8000;
            clearMask |= GL_COVERAGE_BUFFER_BIT_NV;
		}
		
		GLES20.glClear(clearMask);
		
		mVMatrix = mCamera.toViewMatrix();
				
		D3GLES20.drawAll(mVMatrix, mProjMatrix, interpolation);
		mPrey.draw(mVMatrix, mProjMatrix, interpolation);
		
		if (SHOW_CIRCLE_CONTAINS_CHECKS) {
			if (tempCircle != null) {
				if (tempCircle.isExpired()) tempCircle = null;
				else tempCircle.draw(mVMatrix, mProjMatrix);
			}
		}
	}

	public void handleTouchDown(float x, float y) {
		PointF converted = fromScreenToWorld(x, y);
		
		mNet.start(converted.x, converted.y);
	}

	public void handleTouchMove(float x, float y, boolean doubleFingerSwipe) {
		PointF converted = fromScreenToWorld(x, y);
		if (doubleFingerSwipe) {
			if (prev == null) {
				prev = new Point((int)x, (int)y);
				mNet.finish(converted.x, converted.y);
			}
			else {
				moveCamera(-(x - prev.x)/(float)mScreenWidthPx, (y - prev.y)/(float)mScreenHeightPx);
			}
			prev.set((int)x, (int)y);
		}
		else mNet.next(converted.x, converted.y);
	}

	public void handleTouchUp(float x, float y) {
		PointF converted = fromScreenToWorld(x, y);
		if (prev != null) {
			prev = null;
			return;
		}
		mNet.finish(converted.x, converted.y);
		if (mNet.notShown()) {
			D3GLES20.putExpiringShape(new PlokText(converted.x, converted.y, tm));
			mEnv.putFood(converted.x, converted.y);
		}
	}

	public void pause() {
//		mEnv.clean();
    	D3GLES20.clean();
    	//TODO: use sparseArray
    	mShapes = new HashMap<Integer, D3Shape>();
    	mShapes.putAll(D3GLES20.getShapes());
    	Log.v(TAG, "Clearing graphics!");
    	D3GLES20.clearGraphics();
    	tm.clear();
	}
	public void resume() {
		next_game_tick = System.currentTimeMillis();
		mslf = next_game_tick;
		smoothMspf = 0;
		smoothingCount = 0;
		((TheHunt) mContext).runOnUiThread(new Runnable() {
			public void run() {
				((TheHunt) mContext).mCaughtCounter.setText(mCaughtCounter + "");
			}
		});
		D3GLES20.init();
		if (mShapes != null) {
			D3GLES20.setShapes(mShapes);
			mShapes = null;
		}
	}
	
	public void release() {
		// TODO stuff to release	
	}

	float[] normalizedInPoint = new float[4];
	float[] outPoint = new float[4];
	float[] mPVMatrix = new float[16];
	
	public PointF fromScreenToWorld(float touchX, float touchY) {
		normalizedInPoint[0] = 2f*touchX/mScreenWidthPx - 1;
		normalizedInPoint[1] = 2f*(mScreenHeightPx - touchY)/mScreenHeightPx - 1;
		normalizedInPoint[2] = -1f;
		normalizedInPoint[3] = 1f;
		
		Matrix.multiplyMM(mPVMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
		Matrix.invertM(mPVMatrix, 0, mPVMatrix, 0);
		Matrix.multiplyMV(outPoint, 0, mPVMatrix, 0, normalizedInPoint, 0);
		return new PointF(outPoint[0], outPoint[1]);
	}

	public void moveCamera(float dx, float dy) {
		mCamera.move(dx, dy);
	}
}