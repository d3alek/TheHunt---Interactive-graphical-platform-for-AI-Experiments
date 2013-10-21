package com.primalpond.hunt.world.logic;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.SyncResult;
import android.graphics.PointF;
import android.hardware.SensorEvent;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

import com.crashlytics.android.Crashlytics;
import com.primalpond.hunt.MultisampleConfigChooser;
import com.primalpond.hunt.MyApplication;
import com.primalpond.hunt.PreyChangeDialog;
import com.primalpond.hunt.TheHunt;
import com.primalpond.hunt.agent.Agent;
import com.primalpond.hunt.agent.DummyPrey;
import com.primalpond.hunt.agent.prey.Prey;
import com.primalpond.hunt.world.Camera;
import com.primalpond.hunt.world.HUD;
import com.primalpond.hunt.world.environment.Environment;
import com.primalpond.hunt.world.floating_text.PlokText;
import com.primalpond.hunt.world.floating_text.ToolText;
import com.primalpond.hunt.world.tools.CatchNet;
import com.primalpond.hunt.world.tools.Knife;
import com.primalpond.hunt.world.tools.Tool;

import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.shader.ShaderProgramManager;
import d3kod.graphics.sprite.SpriteManager;
import d3kod.graphics.texture.TextureManager;

public class TheHuntRenderer implements GLSurfaceView.Renderer {
	private static final String DEFAULT_TAG = "TheHuntRenderer";
	private String TAG;// = "TheHuntRenderer";

	public interface ShowNavigationListener {
		public void onToShowNavigation();
		public void onToHideNavigation();
	}

	ShowNavigationListener mOnPauseListener = new ShowNavigationListener() {
		public void onToShowNavigation() {
		}
		public void onToHideNavigation() {
		}
	};

	private static final int SMOOTHING_SIZE = 30;
	public static boolean LOGGING_TIME = false;
	public static boolean SHOW_TILES = false;
	public static boolean SHOW_CURRENTS = false;
	public static boolean FEED_WITH_TOUCH = false;
	public static boolean NET_WITH_TOUCH = true;

	private float[] mProjMatrix = new float[16]; 
	private float[] mVMatrix = new float[16];
	private Environment mEnv;
	public Agent mPrey;

	public static final int TICKS_PER_SECOND = 30;
	private static final int RELEASE_DELAY = 1; // in seconds
	private static final int RELEASE_TICKS = RELEASE_DELAY*TICKS_PER_SECOND;
	private final int MILLISEC_PER_TICK = 1000 / TICKS_PER_SECOND;
	private final int MAX_FRAMESKIP = 5;
	private long next_game_tick;
	//	private Context mContext;
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
	private SpriteManager mD3GLES20;
	private ShaderProgramManager sm;
	private Tool mTool;

	private static int mScreenWidthPx;
	private static int mScreenHeightPx;
	public static float[] bgColor = {
		0.8f, 0.8f, 0.8f, 1.0f};

	private final static int worldWidthPx = 1500;
	private final static int worldHeightPx = 900;

	public final static float SCREEN_HEIGHT = 2f;
	public final static float SCREEN_WIDTH = (SCREEN_HEIGHT * worldWidthPx)/worldHeightPx;

	protected static final int YELLOW_TEXT_ENERGY = 60;
	protected static final int RED_TEXT_ENERGY = 30;
	private boolean mScrolling = false;
	private int mIgnoreNextTouch;
	private MyApplication mContext;
	private HUD mHUD;
	private int mScore;
	private TheHuntContextMenu mContextMenu;
	private float mScale;
	private TheHuntMenu mMenu;
	private int mReducePenaltyCounter;
	private boolean mPaletteShown;
	private boolean mMovedAway;
	private long mTimeFirstTouch;
	private PointF mFirstTouch;
	private State mStateBeforePause;
	//	private GLText mGLText;
	/**
	 * The possible Prey type values (for example, returned from a {@link PreyChangeDialog}.
	 * 
	 * @author Aleksandar Kodzhabashev (d3kod) 
	 * @see PreyChangeDialog
	 *
	 */
	public static enum PreyType {NONE, DEFAULT};

	public void onSensorChanged(SensorEvent event) {

	}

	public TheHuntRenderer(Context context) {
		this(context, DEFAULT_TAG);
	}

	public TheHuntRenderer(Context context, String tag) {
		TAG = tag;
		Log.v(TAG, "onCreate called!");
		mContext = ((MyApplication) context.getApplicationContext());
		mContext.setRunningRenderer(TAG);

		tm = new TextureManager(context);
		//		prepareState();
		//		mEnv = null;
		//		mTool = null;
		//		mD3GLES20 = new SpriteManager(sm);
		//		while (mPrey == null) {

		//		}
		//		if (mPrey == null) {
		//			if (context instanceof TheHunt) {
		//				((TheHunt) context).showPreyChangeDialog(null);
		//			}
		//			else {
		//				mPrey = new DummyPrey(mEnv, mD3GLES20);
		//			}
		//		}

	}

	private void prepareState() {
		sm = new ShaderProgramManager();
		synchronized (mContext.stateLock) {
			loadSavedState(sm);
		}		mTool = new CatchNet(mEnv, mD3GLES20);
		//		mTool = new Knife(mEnv, mD3GLES20);
		mIgnoreNextTouch = -1;
		mGraphicsInitialized = false;
		mScale = 1;
		next_game_tick = System.currentTimeMillis();
	}

	private SpriteManager loadSavedState(ShaderProgramManager shaderManager) {
		Log.v(TAG, "Loading saved state");
		SaveState state = mContext.getStateFromDB(TAG);
		//		SaveState state = null;

		if (state == null) {
			Log.v(TAG, "SaveState is empty, creating new SaveState");
			mD3GLES20 = new SpriteManager(shaderManager, tm, mContext);
			mEnv = new Environment(mD3GLES20);
			mPrey = new Prey(mEnv, mD3GLES20);
			//			mPrey = new DummyPrey(mEnv, mD3GLES20);
			//			saveState();

		}
		else {
			//			if (state.getSameAsLast()) {
			//				Log.i(TAG, "SavedState is same as last, ignoring loadSavedState call");
			//				return mD3GLES20;
			//			}
			//			mD3GLES20 = state.getSpriteManager();
			//			mEnv = state.getEnv();
			//			mPrey = state.getAgent();
			//			ArrayList<D3Sprite> sprites = state.getSprites();
			//			Enum4
			mD3GLES20 = new SpriteManager(shaderManager, tm, mContext);
			Log.i(TAG, "mScore from state is " + mScore);
			mScore = state.getScore();
			mCaughtCounter = mScore/10;
			mEnv = state.getEnv(); 
			//			mPrey = new DummyPrey(mEnv, mD3GLES20);
			mPrey = mEnv.getPrey();
			if (mPrey == null) {
				Crashlytics.log("mPrey is null but state loaded. Create new Prey...");
				mPrey = new Prey(mEnv, mD3GLES20);
			}
			//			mPrey.setTextureManager(tm);
			//			mEnv.initSprites(sprites);
			//			mPrey = mEnv.getPrey();
			Log.v(TAG, "SaveState loaded!");
		}

		Log.v(TAG, "Finished loading saved state!");



		return mD3GLES20;
	}

	private void saveState() {
		Log.v(TAG, "Saving state!");
		//		SaveState state = new SeventaveState(mEnv.getSprites());
		SaveState state = new SaveState(mEnv, mScore);
		mContext.storeToDB(state);
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

		Log.i(TAG, "onSurfaceChanged " + width + " " + height);

		mScreenWidthPx = width;
		mScreenHeightPx = height;

		mScreenToWorldRatioWidth = mScreenWidthPx/(float)worldWidthPx;
		mScreenToWorldRatioHeight = mScreenHeightPx/(float)worldHeightPx;

		//		mGraphicsInitialized
		Log.v(TAG, "mScreenWidth " + mScreenWidthPx + " mScreenHeight " + mScreenHeightPx);

		//		if (mEnv == null) mEnv = new Environment(worldWidthPx, worldHeightPx, mD3GLES20);
		//		if (mTool == null) mTool = new CatchNet(mEnv, tm, mD3GLES20);
		synchronized (mContext.stateLock) {
			mCamera = new Camera(mScreenWidthPx, mScreenHeightPx, mScreenToWorldRatioWidth, 
					mScreenToWorldRatioHeight, worldWidthPx/(float)worldHeightPx, mD3GLES20);

			if (!mGraphicsInitialized ) {
				//			Log.v(TAG, "Initializing graphics");
				//TODO why not initialize tool graphics as well?
				mD3GLES20.init(mContext);
				//			mD3GLES20.putText1(new D3FadingText("Fafa", 1000.0f, 0)); 

				tm = new TextureManager(mContext);
				mEnv.initGraphics(mD3GLES20);
				mCamera.initGraphic();
				mHUD = new HUD(mCamera);

				mHUD.initGraphics(mD3GLES20);
				//			mHUD.setCaught(mCaughtCounter);
				mHUD.setScore(mCaughtCounter);
				mEnv.clearPlayerPenalty();
				mMenu = new TheHuntMenu(mD3GLES20, mCamera);
				mMenu.initGraphic();
				//			mContextMenu = new TheHuntContextMenu(mD3GLES20);
				//			mContextMenu.initGraphic();

				if (mPrey != null) {
					mPrey.setSpriteManager(mD3GLES20);
					mPrey.setTextureManager(tm);	
					mPrey.initGraphic();
				}

				mGraphicsInitialized = true;
			}

		}

		float ratio = (float) worldWidthPx / worldHeightPx;
		//
		//		if (width > height) Matrix.frustumM(mProjMatrix, 0, -ratio/mScale, ratio/mScale, mScale, mScale, 1, 10);
		//		else Matrix.frustumM(mProjMatrix, 0, -mScale, mScale, -mScale/ratio, mScale/ratio, 1, 10);
	}
	/**
	 * Here we do our drawing
	 */
	public void onDrawFrame(GL10 unused) {

		synchronized (mContext.stateLock) {
			if (mState == State.PAUSE) {
				return;
			}
			if (mState == State.MENU) {
				if (mMenu == null) {
					Log.e(TAG, "Menu is null on draw");
				}
				else {
					drawWorld(0);
					mMenu.draw();
				}
				return;
			}
			if (mContext.getRunningRenderer() != TAG) {
				if (mContext.getRunningRenderer() == "") {
					Log.v(TAG, "No renderer is running, let me run!");
					mContext.setRunningRenderer(TAG);
				}
				else Log.v(TAG, "I'm not supposed to be running! Sorry...");
				return;
			}

			//			Log.v(TAG, "Obtained stateLock!");
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
	}		

	public void updateWorld() {
		if (mFirstTouch != null && !mMovedAway && !mPaletteShown && mTimeFirstTouch != 0 && System.currentTimeMillis() - mTimeFirstTouch >= mHUD.SHOW_PALETTE_DELAY) {
			mHUD.showPalette(mFirstTouch, mTool.getClass());
			mPaletteShown = true;
		}
		//		Log.v(TAG, "Update world called!");
		mEnv.update();
		//		if (!mContextMenu.isHidden()) mContextMenu.update();
		//		mHUD.setEnvState(mEnv.getStateString(), mEnv.getStateColor());
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
					++mCaughtCounter;
				}
				releaseCountdown--;
				if (releaseCountdown == 0) {
					mPrey.release();
					releaseCountdown = -1;
				}
			}
			mCamera.setPreyPosition(preyPos);
			//			mHUD.setPreyEnergy(mPrey.getEnergy(), mPrey.getMoodColor());
			//			mHUD.setPreyState(mPrey.getStateString());
		}

		if (mTool != null) mTool.update();

		mCamera.update();

		mD3GLES20.updateAll();
		mScore = mCaughtCounter*10;
		mHUD.setScore(mScore);
		mHUD.setPenalty(mEnv.getPlayerPenalty());
		mReducePenaltyCounter++;
		if (mReducePenaltyCounter > MILLISEC_PER_TICK) {
			mEnv.reducePlayerPenalty();
			mReducePenaltyCounter = 0;
		}
		mHUD.update();
	}

	public void drawWorld(float interpolation) {
		if (mState == State.PAUSE) return;

		//		tm.clear();

		if (mPreyChange) {
			mPreyChange = false;
			if (mPrey != null) mPrey.clearGraphic();
			switch (mPreyChangeTo) {
			case NONE: mPrey = new DummyPrey(mEnv, mD3GLES20); break;
			case DEFAULT: mPrey = new Prey(mEnv, mD3GLES20); break;
			}
			mPrey.initGraphic();
		}

		int clearMask = GLES20.GL_COLOR_BUFFER_BIT;

		if (MultisampleConfigChooser.usesCoverageAa()) {
			final int GL_COVERAGE_BUFFER_BIT_NV = 0x8000;
			clearMask |= GL_COVERAGE_BUFFER_BIT_NV;
		}

		GLES20.glClear(clearMask);

		//		float[] vpMatrix = new float[16];
		//		Matrix.multiplyMM(vpMatrix, 0, mProjMatrix, 0, mVMatrix, 0);

		//		mGLText.drawTexture( 100, 100, vpMatrix);            // Draw the Entire Texture

		//		mGLText.begin(0.0f, 0.0f, 0.0f, 1.0f, vpMatrix);
		//		mGLText.setScale(0.003f);
		//		mGLText.setSpace(1f);
		//		mGLText.drawC( "Test String :)", 0, 0, 10);	
		//		mGLText.end();

		mVMatrix = mCamera.toViewMatrix();
		mProjMatrix = mCamera.toProjMatrix();

		if (mPrey != null && mPrey.getGraphic() != null) mCamera.setPreyPointerPosition(mPrey.getPredictedPosition());

		mD3GLES20.drawAll(mVMatrix, mProjMatrix, interpolation);
	}

	public void handleTouch(final MotionEvent event, boolean doubleTouch) {

		//TODO: receive int, PointF instead of MotionEvent
		//		if (prev != null && prev.getX() == event.getX() 
		//			&& prev.getY() == event.getY() && prev.getAction() == event.getAction()) return;

		PointF location = mCamera.fromScreenToWorld(event.getX(), event.getY());

		if (mState == State.MENU) {
			if (mMenu == null) {
				Log.e(TAG, "Menu is null on handle Touch");
				return;
			}
			if (!mMenu.handleTouch(event.getX(), event.getY(), event.getAction())) {
				mState = State.PLAY;
				mMenu.hide();
				mOnPauseListener.onToHideNavigation();
				next_game_tick = System.currentTimeMillis();
				mIgnoreNextTouch = MotionEvent.ACTION_UP;
				//				return;
			}

		}

		if (doubleTouch && !mScrolling) {
			mScrolling = true;
			Log.v(TAG, "Scrolling is true");
			if (mTool != null) mTool.stop(location);
			if (mHUD != null) mHUD.hidePalette();
		}
		else if (!doubleTouch && mScrolling) {
			mScrolling = false;
			mIgnoreNextTouch = MotionEvent.ACTION_UP;
			Log.v(TAG, "Scrolling is false");
		}
		else {
			if (mScrolling) {
				if (prev != null) {
					float prevSpacing, thisSpacing;
					try {
						prevSpacing = D3Maths.distance(prev.getX(0), prev.getY(0), prev.getX(1), prev.getY(1));
						thisSpacing = D3Maths.distance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
					} catch (IllegalArgumentException e) {
						Log.e(TAG, "Illegal argument while scrolling/zooming ");
						e.printStackTrace();
						mScrolling = false;
						prevSpacing = 0;
						thisSpacing = 0;
					}
					if (prevSpacing != 0) {
						PointF prevWorld = mCamera.fromScreenToWorld(prev.getX(), prev.getY());
						mCamera.move(prevWorld.x - location.x, prevWorld.y - location.y, prevSpacing, thisSpacing);				
					}
				}
			}				

			if (mState == State.MENU) {
			}
			else {
				if (event.getAction() == MotionEvent.ACTION_DOWN || (mFirstTouch == null && event.getAction() == MotionEvent.ACTION_MOVE)) {
					mPaletteShown = false;
					mMovedAway = false;
					mTimeFirstTouch = System.currentTimeMillis();
					mFirstTouch = location;

				}
				if (mFirstTouch != null && !mMovedAway && !mHUD.pointsEqual(location, mFirstTouch)) {
					mMovedAway = true;
				}
				if (mHUD.handleTouch(location, event.getX(), event.getY(), event.getAction(), mTool.getClass())) {
					if (mHUD.isPaused()) {
						mState = State.MENU;
						mOnPauseListener.onToShowNavigation();
						mMenu.show();
						mHUD.hidePalette();
						mTool.stop(location);
					}
					else if (event.getAction() != mIgnoreNextTouch && event.getAction() == MotionEvent.ACTION_UP) {
						// there is an HUD action request
						String active = mHUD.getActivePaletteElement();
						if (mTool.isActive()) {
							Log.v(TAG, "Ignore HUD action request because tool is active");
						}
						else if (active == null) {
							Log.e(TAG, "Expected active pallete element, got null instead");
						}
						else {
							Log.v(TAG, "Gonna change tool, class is " + mTool.getClass());
							if (active == "Net" && mTool.getClass() != CatchNet.class) {
								Log.v(TAG, "Changing tool to Net");
								mTool.stop(location);
								mD3GLES20.putText(new ToolText("Net!", location.x, location.y));
								mTool = new CatchNet(mEnv, mD3GLES20);

							}
							else if (active == "Knife" && mTool.getClass() != Knife.class) {
								mTool.stop(location);
								mD3GLES20.putText(new ToolText("Knife!", location.x, location.y));
								Log.v(TAG, "Changing tool to Knife");
								mTool = new Knife(mEnv, mD3GLES20);
							}

						}
					}
				}
				if (mTool != null && mTool.handleTouch(event.getAction(), location)) {
					if (mTool.didAction()) {
						mHUD.hidePalette();
					}
				}
				else if (prev != null && prev.getX() == event.getX() && prev.getY() == event.getY() && prev.getAction() == event.getAction()) {
				}
				else
					if (mIgnoreNextTouch != event.getAction() && 
					//							(event.getAction() == MotionEvent.ACTION_DOWN || // to place food while net is snatching
					event.getAction() == MotionEvent.ACTION_UP) { // to place food otherwise
						mD3GLES20.putText(new PlokText(location.x, location.y));
						mEnv.putNoise(location.x, location.y, Environment.LOUDNESS_PLOK);
						mEnv.putFoodGM(location.x, location.y);
					}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					mHUD.hidePalette();
					mFirstTouch = null;
				}
				if (mIgnoreNextTouch == event.getAction()) mIgnoreNextTouch = -1;
			}
		}
		prev = MotionEvent.obtain(event);
	}

	public static boolean motionEventDoubleTouchChanged(MotionEvent event) {
		String TAG = "motionEventDoubleTouchChanged";
		int action = event.getAction();
		int count;
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_POINTER_DOWN:
			count = event.getPointerCount(); // Number of 'fingers' at this time
			Log.v(TAG, "Down Count is " + count);
			if (count == 2) {
				return true;
			}
			break;
		case MotionEvent.ACTION_POINTER_UP:
			count = event.getPointerCount(); // Number of 'fingers' in this time
			Log.v(TAG, "UP Count is " + count);
			if (count == 2) {
				return true;
			}
			break;
		}
		return false;
	}

	public void pause() {
		synchronized (mContext.stateLock) {

			if (mContext.getRunningRenderer() == TAG) {
				Log.v(TAG, "Telling the context I'm not running any more...");
				mContext.setRunningRenderer("");
			}
			else {
				Log.v(TAG, "Running renderer not changed as it is " + mContext.getRunningRenderer());
			}
			//    	tm.clear();
			mStateBeforePause = mState;
			//		if (mState == State.PLAY) {
			mState = State.PAUSE;
			saveState();
			//		}
			mState = State.PAUSE;
			Log.v(TAG, "State is " + mState);
		}
		//TODO: use sparseArray
		//    	sm.clear();
		//    	mContext.modeLock.unlock();
		//    	Log.v(TAG, Thread.currentThread().getName() + " released modeLock!");
	}

	public void resume() {
		synchronized (mContext.stateLock) {
			//			if (mContext.getRunningRenderer() != TAG) {
			//				if (mContext.getRunningRenderer() == "") {
			//					Log.v(TAG, "No renderer is running, let me run!");
			//					mContext.setRunningRenderer(TAG);
			//				}
			//				else Log.v(TAG, "I'm not supposed to be running! Sorry...");
			//				return;
			//			}			
			//		mContext.modeLock.lock();
			//		Log.v(TAG, Thread.currentThread().getName() + " aqcuired modeLock!");
			mContext.setRunningRenderer(TAG);
			Log.v(TAG, "resume called!");
			mState = State.RESUME;
			Log.v(TAG, "State is " + mState);
			next_game_tick = System.currentTimeMillis();
			mslf = next_game_tick;
			smoothMspf = 0;
			smoothingCount = 0;
			// TODO: restore caught counter
			//		mEnv = null;
			//		mTool = null;	
			//		if (sm == null) {
			//			Log.v(TAG, "ShaderProgramManager is null!");
			//			sm = new ShaderProgramManager();
			//		}
			//		tm = new TextureManager(mContext);
			// db4o does magic and synchronizes the environment with the one it loaded onCreate, so this is not neccessary
			// loadSavedState(sm);
			//		if (mD3GLES20 == null) {
			//			mD3GLES20 = new SpriteManager(sm);
			//		}
			//		if (mDGLES20 == null) loadSavedState(sm);
			//		sm = new ShaderProgramManager();
			//		sm = new ShaderProgramManager();
			//		mD3GLES20.setShaderManager(sm);
			//		sm = new ShaderProgramManager();
			//		mD3GLES20 = new SpriteManager(sm);
			//		mGraphicsInitialized = false;
			tm.printLoadedTextures();
			prepareState();
			tm.printLoadedTextures();
			//		Log.v(TAG, "ShaderProgramManager " + mD3GLES20.getShaderManager() + " " + sm);
			mState = mStateBeforePause;
			if (mState == null) {
				mState = State.PLAY;
			}
			//TODO: if MENU draw once!
			Log.v(TAG, "State is " + mState);
		}
	}

	public void release() {
		// TODO stuff to release	
	}

	//	float[] outPoint = new float[4];
	//	float[] mPVMatrix = new float[16];
	private boolean mPreyChange;
	private PreyType mPreyChangeTo;
	//	private boolean mLongPress;


	public void changePrey(int which) {
		mPreyChange = true;
		mPreyChangeTo = PreyType.values()[which];
	}

	public void setActivity(TheHunt theHunt) {
		try {
			mOnPauseListener = theHunt;
		} catch (ClassCastException e) {
			Crashlytics.log("Activity does not implement PauseListener");
		}
	}

	public void regenerateWorld() {
		synchronized (mContext.stateLock) {
			//			mD3GLES20.clearAll();
			mEnv.regenerate();
			mPrey.release();
			mState = State.PLAY;
			mMenu.hide();
			mOnPauseListener.onToHideNavigation();
			next_game_tick = System.currentTimeMillis();
			//			mEnv = new Environment(mD3GLES20);
			//			mEnv.initGraphics(mD3GLES20);
			//			if (mPrey != null) {
			//				mPrey = new Prey(mEnv, mD3GLES20);
			//				mPrey.initGraphic();
			//			}
		}
	}
}
