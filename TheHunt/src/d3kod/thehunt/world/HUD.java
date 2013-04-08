package d3kod.thehunt.world;

import android.graphics.Color;
import android.graphics.PointF;
import d3kod.graphics.extra.D3Color;
import d3kod.graphics.sprite.SpriteManager;
import d3kod.graphics.sprite.shapes.D3FadingText;
import d3kod.graphics.text.GLText;

public class HUD {
	class HUDText extends D3FadingText {
		public HUDText(String text, float size) {
			super(text, size, 0);
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
	
	private PointF mPreyEnergyPos;
	
	public HUD(Camera camera, SpriteManager spriteManager) {
		mSpriteManager = spriteManager;
		mViewMatrix = camera.toCenteredViewMatrix();
		
		float caughtTextPosX = -camera.getWidth()/2 + posXAdj;
		float caughtTextPosY = camera.getHeight()/2 - posYAdj;
		float preyEnergyPosX = camera.getWidth()/2 - preyEnergyXAdj;
		float preyEnergyPosY = camera.getHeight()/2 - posYAdj;
		
		mCaughtTextPos = new PointF(caughtTextPosX, caughtTextPosY);
		mCaughtText = new HUDText("Times caught: ", mCaughtTextSize, mCaughtTextPos);
		mCaught = new HUDText("undef", mCaughtTextSize);
		mPreyEnergyPos = new PointF(preyEnergyPosX, preyEnergyPosY);
		mPreyEnergyText = new HUDText("Prey energy: ", mPreyEnergyTextSize, mPreyEnergyPos);
		mPreyEnergy = new HUDText("undef", mPreyEnergyTextSize);
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
	}
	public void setCaught(int caught) {
		mCaught.setText(""+caught);
	}
	public void setPreyEnergy(int energy, D3Color color) {
		mPreyEnergy.setText(""+energy);
		mPreyEnergy.setColor(color.getR(), color.getG(), color.getB());
	}

}
