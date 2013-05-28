package d3kod.thehunt.world;

import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import d3kod.graphics.sprite.SpriteManager;
import d3kod.graphics.sprite.shapes.D3FadingText;
import d3kod.graphics.text.GLText;

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
	private Palette mPalette;
	private Camera mCamera;
	private float[] mProjMatrix;
	private Object prevTouch;
	private String activePaletteElement;
	
	private static final float NORMAL_TEXT_SIZE = 2.0f;
	private static final float SCORE_VERTICAL_ADJ = 0.15f;
	private static final String TAG = "HUD";
	
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
	
	
	public HUD(Camera camera) {
		
		float scoreTextPosX = 0;
		float scoreTextPosY = camera.getHeight()/2 - SCORE_VERTICAL_ADJ;
		
		PointF mScoreTextPos = new PointF(scoreTextPosX, scoreTextPosY);
		
		mScoreText = new HUDText("Score: ", NORMAL_TEXT_SIZE, mScoreTextPos, true);
		mScore = new HUDText("undef", NORMAL_TEXT_SIZE, false);
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
		mPalette = new Palette(new PointF(0, 0), spriteManager, mProjMatrix, mViewMatrix);
		mPalette.initGraphic();
		hidePalette();
		
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
		mScore.setText("" + mCaughtCounter);
	}
	
	public void showPalette(PointF pos) {
		mPalette.setPosition(pos);
		mPalette.show();
	}
	
	public void hidePalette() {
		mPalette.hide();
	}
	public boolean handleTouch(PointF touch, int action) {
//		Log.v(TAG, "Action is " + action);
		if (prevTouch == null && (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE)) {
			Log.v(TAG, "Showing palette");
			showPalette(touch);
			prevTouch = touch;
			activePaletteElement = null;
			return true;
		}
		
		if (action == MotionEvent.ACTION_UP && mPalette.handleTouch(touch)) {
			activePaletteElement = mPalette.getActiveElement();
			prevTouch = null;
			hidePalette();
			if (activePaletteElement == null) {
				Log.v(TAG, "Active element is null, returning false!");
				return false;
			}
			Log.v(TAG, "Active element is not null, return true!");
			return true;
		}
		if (action == MotionEvent.ACTION_UP) {
			prevTouch = null;
			hidePalette();
			return false;
		}
		return mPalette.handleTouch(touch);
	}
	public void update() {
		if (mPalette != null) mPalette.update();
	}
	public String getActivePaletteElement() {
		return activePaletteElement;
	}

}
