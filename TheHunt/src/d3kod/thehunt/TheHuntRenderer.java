package d3kod.thehunt;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.PointF;
import android.hardware.SensorEvent;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;
import d3kod.d3gles20.D3GLES20;
import d3kod.d3gles20.ShaderManager;
import d3kod.d3gles20.TextureManager;
import d3kod.thehunt.environment.Environment;
import d3kod.thehunt.floating_text.PlokText;
import d3kod.thehunt.prey.DefaultPrey;
import d3kod.thehunt.prey.DummyPrey;
import d3kod.thehunt.prey.Prey;
//import d3kod.d3gles20.shapes.D3TempCircle;

public class TheHuntRenderer implements GLSurfaceView.Renderer {
	
	private static final String TAG = "TheHuntRenderer";
	private static final int SMOOTHING_SIZE = 30;
	public static boolean LOGGING_TIME = false;
	public static boolean SHOW_TILES = false;
	public static boolean SHOW_CURRENTS = false;
	public static boolean FEED_WITH_TOUCH = false;
	public static boolean NET_WITH_TOUCH = true;

	private float[] mProjMatrix = new float[16]; 
	private float[] mVMatrix = new float[16];
	private Environment mEnv;
	public Prey mPrey;
	
    public static final int TICKS_PER_SECOND = 30;
    private static final int RELEASE_DELAY = 1; // in seconds
	private static final int RELEASE_TICKS = RELEASE_DELAY*TICKS_PER_SECOND;
    private final int MILLISEC_PER_TICK = 1000 / TICKS_PER_SECOND;
    private final int MAX_FRAMESKIP = 5;
	private long next_game_tick;
	private TheHunt mActivity;
	private long mslf;
	private long smoothMspf;
	private int smoothingCount;
	private int mCaughtCounter;
	private boolean mGraphicsInitialized = false;
	private TextureManager tm;
	private int releaseCountdown;
	private float mScreenToWorldRatioWidth;
	private float mScreenToWorldRatioHeight;
	private MotionEvent prev;
	private Camera mCamera;
	
	private State mState;
	private D3GLES20 mD3GLES20;
	private ShaderManager sm;
	private Tool mTool;
	
	private static int mScreenWidthPx;
	private static int mScreenHeightPx;
	public static float[] bgColor = {
			0.8f, 0.8f, 0.8f, 1.0f};

	private final static int worldWidthPx = 1500;
	private final static int worldHeightPx = 900;
	
	protected static final int YELLOW_TEXT_ENERGY = 60;
	protected static final int RED_TEXT_ENERGY = 30;
	private boolean mScrolling = false;
	private int mIgnoreNextTouch;
	public static enum PreyType {NONE, DEFAULT};
	
	public void onSensorChanged(SensorEvent event) {
		
	}
	
	public TheHuntRenderer(TheHunt activity) {
		mActivity = activity;
		mEnv = null;
		mTool = null;
		sm = new ShaderManager();
		mD3GLES20 = new D3GLES20(sm);
		mPrey = null;
		mIgnoreNextTouch = -1;
		mCaughtCounter = 0;
		tm = new TextureManager(mActivity);
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
			mScreenToWorldRatioHeight, worldWidthPx/(float)worldHeightPx, mD3GLES20);
	
    Log.v(TAG, "mScreenWidth " + mScreenWidthPx + " mScreenHeight " + mScreenHeightPx);
    
	if (mEnv == null) mEnv = new Environment(worldWidthPx, worldHeightPx, mD3GLES20);
	if (mTool == null) mTool = new CatchNet(mEnv, tm, mD3GLES20);
    if (!mGraphicsInitialized ) {
    	//TODO why not initialize tool graphics as well?
    	mCamera.initGraphic();
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
	mslf = System.currentTimeMillis();
	
	while (next_game_tick < System.currentTimeMillis() && loops < MAX_FRAMESKIP) {
		updateWorld();
		next_game_tick += MILLISEC_PER_TICK;
		loops++;
	}
	if (loops >= MAX_FRAMESKIP) {
		Log.w(TAG, "Skipping " + loops + " frames!");
	}
	
	float interpolation = (System.currentTimeMillis() + MILLISEC_PER_TICK - next_game_tick) / (float) MILLISEC_PER_TICK;
	drawWorld(interpolation);
	long mspf = System.currentTimeMillis() - mslf;
	smoothMspf = (smoothMspf*smoothingCount + mspf)/(smoothingCount+1);
	smoothingCount++;
	if (smoothingCount >= SMOOTHING_SIZE) {
		smoothingCount = 0;
		// TODO: Set HUD details (prey state, ms per frame, energy
	}
}		

private void updateWorld() {
	mEnv.update();
	if (mPrey != null) {
		PointF preyPos = mPrey.getPosition();
		if (preyPos != null) {
			PointF curDirDelta = mEnv.data.getTileFromPos(preyPos).getDir().getDelta();
			mPrey.updateVelocity(curDirDelta.x, curDirDelta.y);
			mPrey.update();
		}

		preyPos = mPrey.getPosition();
		if (mPrey.getCaught()) {
			if (releaseCountdown < 0) {
				releaseCountdown = RELEASE_TICKS;
			}
			releaseCountdown--;
			if (releaseCountdown == 0) {
				mPrey.release();
				releaseCountdown = -1;
			}
		}
		mCamera.setPreyPosition(preyPos);
	}

	mTool.update();

	mCamera.update();
}

	private void drawWorld(float interpolation) {
		if (mState != State.PLAY) return;
		
		if (mPreyChange) {
			mPreyChange = false;
			if (mPrey != null) mPrey.clearGraphic();
			switch (mPreyChangeTo) {
			case NONE: mPrey = new DummyPrey(mEnv, mD3GLES20); break;
			case DEFAULT: mPrey = new DefaultPrey(mEnv, tm, mD3GLES20); break;
			}
			mPrey.initGraphic();
		}
		
		int clearMask = GLES20.GL_COLOR_BUFFER_BIT;
		
		if (MultisampleConfigChooser.usesCoverageAa()) {
			final int GL_COVERAGE_BUFFER_BIT_NV = 0x8000;
            clearMask |= GL_COVERAGE_BUFFER_BIT_NV;
		}
		
		GLES20.glClear(clearMask);
		
		mVMatrix = mCamera.toViewMatrix();
		
		if (mPrey != null) mCamera.setPreyPointerPosition(mPrey.getPredictedPosition());
		
		mD3GLES20.drawAll(mVMatrix, mProjMatrix, interpolation);
	}

	public void handleTouch(final MotionEvent me, boolean doubleTouch) {
		//TODO: receive int, PointF instead of MotionEvent
		if (prev != null && prev.getX() == me.getX() 
			&& prev.getY() == me.getY() && prev.getAction() == me.getAction()) return;

		PointF location = fromScreenToWorld(me.getX(), me.getY());
		
		if (doubleTouch && !mScrolling) {
			mScrolling = true;
			Log.v(TAG, "Scrolling is true");
			mTool.stop(location);
		}
		else if (!doubleTouch && mScrolling) {
			mScrolling = false;
			mIgnoreNextTouch = MotionEvent.ACTION_UP;
			Log.v(TAG, "Scrolling is false");
		}
		else {
			if (mScrolling) {
				if (prev != null) {
					PointF prevWorld = fromScreenToWorld(prev.getX(), prev.getY());
					mCamera.move(prevWorld.x - location.x, prevWorld.y - location.y);				
				}
			}				
		
			else {
				if (!mTool.handleTouch(me.getAction(), location)) {
					Log.v(TAG, "mTool can't handle touch. Ignoring if " + mIgnoreNextTouch);
					if (mIgnoreNextTouch != me.getAction() && 
							(me.getAction() == MotionEvent.ACTION_DOWN || // to place food while net is snatching
							me.getAction() == MotionEvent.ACTION_UP)) { // to place food otherwise
						mD3GLES20.putExpiringShape(new PlokText(location.x, location.y, tm, mD3GLES20.getShaderManager()));
						mEnv.putNoise(location.x, location.y, Environment.LOUDNESS_PLOK);
						mEnv.putFoodGM(location.x, location.y);
					}
				}
				if (mIgnoreNextTouch == me.getAction()) mIgnoreNextTouch = -1;
			}
		}
		prev = MotionEvent.obtain(me);
	}
	
	public void pause() {
    	mState = State.PAUSE;
    	Log.v(TAG, "State is " + mState);
    	//TODO: use sparseArray
    	tm.clear();
    	sm.clear();
	}
	public void resume() {
		mState = State.RESUME;
		Log.v(TAG, "State is " + mState);
		next_game_tick = System.currentTimeMillis();
		mslf = next_game_tick;
		smoothMspf = 0;
		smoothingCount = 0;
		// TODO: restore caught counter
	
		if (sm == null) {
			sm = new ShaderManager();
		}
		if (mD3GLES20 == null) {
			mD3GLES20 = new D3GLES20(sm);
		}
		mState = State.PLAY;
		Log.v(TAG, "State is " + mState);
	}
	
	public void release() {
		// TODO stuff to release	
	}

	float[] normalizedInPoint = new float[4];
	float[] outPoint = new float[4];
	float[] mPVMatrix = new float[16];
	private boolean mPreyChange;
	private PreyType mPreyChangeTo;
	
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

	public void changePrey(int which) {
		mPreyChange = true;
		mPreyChangeTo = PreyType.values()[which];
	}
}
