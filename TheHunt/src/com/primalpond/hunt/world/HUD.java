package com.primalpond.hunt.world;

import com.primalpond.hunt.world.tools.Tool;

import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.extra.Utilities;
import d3kod.graphics.shader.ShaderProgramManager;
import d3kod.graphics.sprite.SpriteManager;
import d3kod.graphics.sprite.shapes.D3FadingText;
import d3kod.graphics.sprite.shapes.D3Image;
import d3kod.graphics.sprite.shapes.D3Quad;
import d3kod.graphics.sprite.shapes.D3Shape;
import d3kod.graphics.text.GLText;
import d3kod.graphics.texture.TextureInfo;
import d3kod.graphics.texture.TextureManager;
import d3kod.graphics.texture.TextureManager.Texture;

public class HUD {
	class HUDText extends D3FadingText {
		public HUDText(String text, float size, boolean drawCentered) {
			super(text, size, 0, drawCentered);
			setAlpha(0.6f);
		}
		public HUDText(String text, float size, PointF pos, boolean drawCentered) {
			this(text, size, drawCentered);
			setPosition(pos.x, pos.y, 0);
		}
		@Override
		public void draw(GLText glText, float[] projMatrix, float[] viewMatrix) {
//			float[] idMatrix = new float[16];
//			Matrix.setIdentityM(idMatrix, 0);
			super.draw(glText, mProjMatrix, mViewMatrix);
		}
//		@Override
//		protected void drawText(GLText glText, String text, float x, float y,
//				float angle) {
//			glText.draw(text, x, y, angle);
//		}
		@Override
		public void setText(String text) {
			super.setText(text);
		}
		
	}
	class HUDImage extends D3Image {

		public HUDImage(TextureInfo texture, float size, ShaderProgramManager sm) {
			super(texture, size, sm);
		}

		@Override
		public void draw(float[] viewMatrix, float[] projMatrix) {
//			Log.v(TAG, "HUDImage draw invoked");
			super.draw(mViewMatrix, mProjMatrix);
		}
	}
//	private static final float mPreyEnergyTextSize = 2.0f;
//	private static final float mCaughtTextSize = 2.0f;
//	private static final float preyEnergyXAdj = 0.6f;
	private SpriteManager mSpriteManager;
//	private PointF mCaughtTextPos;
	private float[] mViewMatrix;
//	private float posXAdj = 0.1f;
//	private float posYAdj = 0.15f;

	private HUDText mScoreText;
	private HUDText mScore;
	private HUDText mPenalty;
	private Palette mPalette;
	private Camera mCamera;
	private float[] mProjMatrix;
//	private Object prevTouch;
	private String activePaletteElement;
	private DummySprite mPause;
	private D3Image mPauseGraphic;
	private float pausePosX;
	private float pausePosY;
	private boolean mPaused;
	
	private static final float NORMAL_TEXT_SIZE = 2.0f;
	private static final float SCORE_VERTICAL_ADJ = 0.15f;
	private static final String TAG = "HUD";
	private static final float PAUSE_SIZE = 0.1f;
	private static final float PAUSE_HORIZ_ADJ = 0.2f;
	public static final long SHOW_PALETTE_DELAY = 100; //in ms
	private static final float POINTS_EQUAL_DISTANCE_THRESH = 0.04f;
	
	long mTimeFirstTouch;
	boolean mPaletteShown;
	private PointF mFirstTouch;
//	private HUDText mCaughtText;
//	private HUDText mPreyEnergyText;
//	private HUDText mCaught;
//	private HUDText mPreyEnergy;
//	private HUDText mEnvText;
//	private HUDText mEnv;
//	private HUDText mPreyStateText;
//	private HUDText mPreyState;
//	
//	private PointF mPreyEnergyPos;
	private boolean mMovedAway;
	
	
	public HUD(Camera camera) {
		
		float scoreTextPosX = 0;
		float scoreTextPosY = camera.getHeight()/2 - SCORE_VERTICAL_ADJ;
		pausePosX = -camera.getWidth()/2 + PAUSE_HORIZ_ADJ;
		pausePosY = scoreTextPosY;
		
		mTimeFirstTouch = 0;
		mPaletteShown = false;
		
		PointF mScoreTextPos = new PointF(scoreTextPosX, scoreTextPosY);
		
		mScoreText = new HUDText("Score: ", NORMAL_TEXT_SIZE, mScoreTextPos, true);
		mScore = new HUDText("undef", NORMAL_TEXT_SIZE, false);
		mPenalty = new HUDText("undef", NORMAL_TEXT_SIZE, false);
		mCamera = camera;
		
//		float caughtTextPosX = -camera.getWidth()/2 + posXAdj;
//		float caughtTextPosY = camera.getHeight()/2 - posYAdj;
//		float preyEnergyPosX = camera.getWidth()/2 - preyEnergyXAdj;
//		float preyEnergyPosY = camera.getHeight()/2 - posYAdj;
//		
//		mCaughtTextPos = new PointF(caughtTextPosX, caughtTextPosY);
//		mCaughtText = new HUDText("Times Caught: ", mCaughtTextSize, mCaughtTextPos);
//		mCaught = new HUDText("undef", mCaughtTextSize);
//		mPreyEnergyPos = new PointF(preyEnergyPosX, preyEnergyPosY);
//		mPreyEnergyText = new HUDText("Prey Energy: ", mPreyEnergyTextSize, mPreyEnergyPos);
//		mPreyEnergy = new HUDText("undef", mPreyEnergyTextSize);
//		mEnvText = new HUDText("Environment State: ", mCaughtTextSize);
//		mEnv = new HUDText("undef", mCaughtTextSize);
//		mPreyStateText = new HUDText("Prey State: ", mPreyEnergyTextSize);
//		mPreyState = new HUDText("undef", mPreyEnergyTextSize);
	}
	public void initGraphics(SpriteManager spriteManager) {
		mViewMatrix = mCamera.toCenteredViewMatrix();
		mProjMatrix = mCamera.getUnscaledProjMatrix();
		mSpriteManager = spriteManager;
		
		spriteManager.putText(mScoreText);
		mScore.setPosition(mScoreText.getX() + mScoreText.getLength(spriteManager.getTextManager())/2, 
				mScoreText.getY() - mScoreText.getHeight(spriteManager.getTextManager())/2, 0);
		spriteManager.putText(mScore);
		
		mPenalty.setPosition(mScore.getX()+0.05f, mScore.getY(), 0);
		mPenalty.setColor(1, 0, 0);
		spriteManager.putText(mPenalty);
		
		mPauseGraphic = new HUDImage(spriteManager.getTextureManager().getTextureInfo(Texture.BTN_PAUSE),
				PAUSE_SIZE, spriteManager.getShaderManager());
//		mPauseGraphic.noFade();
//		mPauseGraphic.setColor(new float[]{0.0f, 0.0f, 0.0f, 1.0f});
//		mPauseGraphic.setInverted(false);
//		mPauseGraphic = new D3Quad(PAUSE_SIZE, PAUSE_SIZE);
		mPause = new DummySprite(spriteManager, mPauseGraphic);
		mPause.setPosition(new PointF(pausePosX, pausePosY));
		mPause.initGraphic();
//		mPause.setPosition(new PointF(0, 0));
//		mPause.getGraphic().setPosition(0, 0);
//		mPause.getGraphic().noFade();
		initPalette();
		
//		mSpriteManager.putText(mCaughtText);
//		mSpriteManager.putText(mPreyEnergyText);
//		mCaught.setPosition(mCaughtTextPos.x + mCaughtText.getLength(mSpriteManager.getTextManager())
//				, mCaughtTextPos.y, 0);
//		mSpriteManager.putText(mCaught);
//		mPreyEnergy.setPosition(mPreyEnergyPos.x + mPreyEnergyText.getLength(mSpriteManager.getTextManager()),
//				mPreyEnergyPos.y, 0);
//		mSpriteManager.putText(mPreyEnergy);
//		mEnvText.setPosition(mCaughtTextPos.x, 
//				mCaughtTextPos.y - mCaughtText.getHeight(mSpriteManager.getTextManager()), 0);
//		mSpriteManager.putText(mEnvText);
//		mEnv.setPosition(mEnvText.getX() + mEnvText.getLength(mSpriteManager.getTextManager())
//				,mEnvText.getY(), 0);
//		mSpriteManager.putText(mEnv);
//		mPreyStateText.setPosition(mPreyEnergyPos.x, 
//				mPreyEnergyPos.y - mPreyStateText.getHeight(mSpriteManager.getTextManager()), 0);
//		mSpriteManager.putText(mPreyStateText);
//		mPreyState.setPosition(mPreyStateText.getX() + mPreyStateText.getLength(mSpriteManager.getTextManager()), 
//				mPreyStateText.getY(), 0);
//		mSpriteManager.putText(mPreyState);
	}
	private void initPalette() {
		mPalette = new Palette(new PointF(0, 0), mSpriteManager, mProjMatrix, mViewMatrix, mCamera);
		mPalette.initGraphic();
		hidePalette();
	}
//	public void setCaught(int caught) {
//		mCaught.setText(""+caught);
//	}
//	public void setPreyEnergy(int energy, D3Color color) {
//		mPreyEnergy.setText(""+energy);
//		mPreyEnergy.setColor(color.getR(), color.getG(), color.getB());
//	}
//	public void setEnvState(String stateString, D3Color stateColor) {
//		mEnv.setText(stateString);
//		mEnv.setColor(stateColor.getR(), stateColor.getG(), stateColor.getB());
//	}
//	public void setPreyState(String stateString) {
//		mPreyState.setText(stateString);
//	}
	public void setScore(int mCaughtCounter) {
		String s = "" + mCaughtCounter;
		mScore.setText(s);
		mPenalty.setPosition(mScore.getX() + mScore.getLength(mSpriteManager.getTextManager()) + 0.02f, mPenalty.getY(), 0);
	}
	
	public void showPalette(PointF pos, Class<? extends Tool> activeToolClass) {
		if (mPalette == null) {
			Log.e(TAG, "Palette is null!");
//			initPalette();
		}
		mPalette.setPosition(pos);
		mPalette.show(activeToolClass);
	}
	
	public void hidePalette() {
		mPaused = false;
		mPalette.hide();
		mTimeFirstTouch = 0;
		mPaletteShown = false;
	}
	public boolean handleTouch(PointF worldTouch, float screenX, float screenY, int action, Class<? extends Tool> activeToolClass) {
		PointF screenTouch = mCamera.fromScreenToWorld(screenX, screenY, mViewMatrix, mProjMatrix);
		if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
			if (mTimeFirstTouch == 0) {
				if (mPause.contains(screenTouch)) {
					Log.v(TAG, "Pausing");
					mPaused = true;
					return true;
				}
	//			Log.v(TAG, "Showing palette");
	//			showPalette(worldTouch, activeToolClass);
	//			prevTouch = worldTouch;
	//			activePaletteElement = null;
	//			return true;
//				mFirstTouch = worldTouch;
//				mMovedAway = false;
				mTimeFirstTouch = System.currentTimeMillis();
			}
			
//			else if (!mMovedAway && !mPaletteShown && mTimeFirstTouch != 0 && pointsEqual(worldTouch, mFirstTouch) && System.currentTimeMillis() - mTimeFirstTouch >= SHOW_PALETTE_DELAY) {
//				Log.v(TAG, "Showing palette");
//				showPalette(mFirstTouch, activeToolClass);
//	//			prevTouch = worldTouch;
//				mPaletteShown = true;
//				activePaletteElement = null;
//				return true;
//			}
//			else if (!mMovedAway && !pointsEqual(worldTouch, mFirstTouch)) {
//				mMovedAway = true;
//			}
		}
		
		if (action == MotionEvent.ACTION_UP && mPalette.handleTouch(worldTouch)) {
			activePaletteElement = mPalette.getActiveElement();
//			prevTouch = null;
			hidePalette();
			if (activePaletteElement == null) {
				Log.v(TAG, "Active element is null, returning false!");
				return false;
			}
			Log.v(TAG, "Active element is not null, return true!");
			return true;
		}
		if (action == MotionEvent.ACTION_UP) {
//			prevTouch = null;
			hidePalette();
			return false;
		}
		return mPalette.handleTouch(worldTouch);
	}
	public boolean pointsEqual(PointF point1, PointF point2) {
		return D3Maths.distance(point1.x, point1.y, point2.x, point2.y) < POINTS_EQUAL_DISTANCE_THRESH;
	}
	public void update() {
		if (mPalette != null) mPalette.update();
	}
	public String getActivePaletteElement() {
		return activePaletteElement;
	}
	
	public boolean isPaused() {
		return mPaused;
	}
	public void setPenalty(int playerPenalty) {
		if (playerPenalty == 0) {
//			mPenalty.setFaded();
			mPenalty.setText("");
		}
		else {
			mPenalty.setText("-" + playerPenalty);
		}
	}

}
