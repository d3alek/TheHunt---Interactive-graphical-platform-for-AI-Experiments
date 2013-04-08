package d3kod.thehunt.world;

import android.graphics.PointF;
import d3kod.graphics.extra.D3Color;
import d3kod.graphics.sprite.SpriteManager;
import d3kod.graphics.sprite.shapes.D3FadingText;
import d3kod.graphics.text.GLText;

public class HUD {
	class HUDText extends D3FadingText {
		public HUDText(String text, float size) {
			super(text, size, 0);
			setAlpha(0.6f);
		}
		public HUDText(String text, float size, PointF pos) {
			this(text, size);
			setPosition(pos.x, pos.y, 0);
		}
		@Override
		public void draw(GLText glText, float[] projMatrix, float[] viewMatrix) {
//			float[] idMatrix = new float[16];
//			Matrix.setIdentityM(idMatrix, 0);
			super.draw(glText, projMatrix, mViewMatrix);
		}
		@Override
		protected void drawText(GLText glText, String text, float x, float y,
				float angle) {
			glText.draw(text, x, y, angle);
		}
		@Override
		public void setText(String text) {
			super.setText(text);
		}
		
	}
	private static final float mPreyEnergyTextSize = 2.0f;
	private static final float mCaughtTextSize = 2.0f;
	private static final float preyEnergyXAdj = 0.6f;
	private SpriteManager mSpriteManager;
	private PointF mCaughtTextPos;
	private float[] mViewMatrix;
	private float posXAdj = 0.1f;
	private float posYAdj = 0.15f;
	
	private HUDText mCaughtText;
	private HUDText mPreyEnergyText;
	private HUDText mCaught;
	private HUDText mPreyEnergy;
	private HUDText mEnvText;
	private HUDText mEnv;
	private HUDText mPreyStateText;
	private HUDText mPreyState;
	
	private PointF mPreyEnergyPos;
	
	
	public HUD(Camera camera, SpriteManager spriteManager) {
		mSpriteManager = spriteManager;
		mViewMatrix = camera.toCenteredViewMatrix();
		
		float caughtTextPosX = -camera.getWidth()/2 + posXAdj;
		float caughtTextPosY = camera.getHeight()/2 - posYAdj;
		float preyEnergyPosX = camera.getWidth()/2 - preyEnergyXAdj;
		float preyEnergyPosY = camera.getHeight()/2 - posYAdj;
		
		mCaughtTextPos = new PointF(caughtTextPosX, caughtTextPosY);
		mCaughtText = new HUDText("Times Caught: ", mCaughtTextSize, mCaughtTextPos);
		mCaught = new HUDText("undef", mCaughtTextSize);
		mPreyEnergyPos = new PointF(preyEnergyPosX, preyEnergyPosY);
		mPreyEnergyText = new HUDText("Prey Energy: ", mPreyEnergyTextSize, mPreyEnergyPos);
		mPreyEnergy = new HUDText("undef", mPreyEnergyTextSize);
		mEnvText = new HUDText("Environment State: ", mCaughtTextSize);
		mEnv = new HUDText("undef", mCaughtTextSize);
		mPreyStateText = new HUDText("Prey State: ", mPreyEnergyTextSize);
		mPreyState = new HUDText("undef", mPreyEnergyTextSize);
	}
	public void initGraphics() {
		mSpriteManager.putText(mCaughtText);
		mSpriteManager.putText(mPreyEnergyText);
		mCaught.setPosition(mCaughtTextPos.x + mCaughtText.getLength(mSpriteManager.getTextManager())
				, mCaughtTextPos.y, 0);
		mSpriteManager.putText(mCaught);
		mPreyEnergy.setPosition(mPreyEnergyPos.x + mPreyEnergyText.getLength(mSpriteManager.getTextManager()),
				mPreyEnergyPos.y, 0);
		mSpriteManager.putText(mPreyEnergy);
		mEnvText.setPosition(mCaughtTextPos.x, 
				mCaughtTextPos.y - mCaughtText.getHeight(mSpriteManager.getTextManager()), 0);
		mSpriteManager.putText(mEnvText);
		mEnv.setPosition(mEnvText.getX() + mEnvText.getLength(mSpriteManager.getTextManager())
				,mEnvText.getY(), 0);
		mSpriteManager.putText(mEnv);
		mPreyStateText.setPosition(mPreyEnergyPos.x, 
				mPreyEnergyPos.y - mPreyStateText.getHeight(mSpriteManager.getTextManager()), 0);
		mSpriteManager.putText(mPreyStateText);
		mPreyState.setPosition(mPreyStateText.getX() + mPreyStateText.getLength(mSpriteManager.getTextManager()), 
				mPreyStateText.getY(), 0);
		mSpriteManager.putText(mPreyState);
	}
	public void setCaught(int caught) {
		mCaught.setText(""+caught);
	}
	public void setPreyEnergy(int energy, D3Color color) {
		mPreyEnergy.setText(""+energy);
		mPreyEnergy.setColor(color.getR(), color.getG(), color.getB());
	}
	public void setEnvState(String stateString, D3Color stateColor) {
		mEnv.setText(stateString);
		mEnv.setColor(stateColor.getR(), stateColor.getG(), stateColor.getB());
	}
	public void setPreyState(String stateString) {
		mPreyState.setText(stateString);
	}

}
