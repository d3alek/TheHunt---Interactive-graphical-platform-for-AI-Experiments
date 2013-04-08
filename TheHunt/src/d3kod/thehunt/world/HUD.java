package d3kod.thehunt.world;

import android.graphics.PointF;
import d3kod.graphics.sprite.SpriteManager;
import d3kod.graphics.sprite.shapes.D3FadingText;
import d3kod.graphics.text.GLText;

public class HUD {
	class HUDText extends D3FadingText {
		private String mFieldText;
		public HUDText(String fieldText, float size, PointF pos) {
			super(fieldText, size, 0);
			mFieldText = fieldText;
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
			super.setText(mFieldText + ": " + text);
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
	private PointF mPreyEnergyPos;
	public HUD(Camera camera, SpriteManager spriteManager) {
		mSpriteManager = spriteManager;
		mViewMatrix = camera.toCenteredViewMatrix();
		float caughtTextPosX = -camera.getWidth()/2 + posXAdj;
		float caughtTextPosY = camera.getHeight()/2 - posYAdj;
		float preyEnergyPosX = camera.getWidth()/2 - preyEnergyXAdj;
		float preyEnergyPosY = camera.getHeight()/2 - posYAdj;
		mCaughtTextPos = new PointF(caughtTextPosX, caughtTextPosY);
		mCaughtText = new HUDText("Times caught", mCaughtTextSize, mCaughtTextPos);
		mPreyEnergyPos = new PointF(preyEnergyPosX, preyEnergyPosY);
		mPreyEnergyText = new HUDText("Prey energy", mPreyEnergyTextSize, mPreyEnergyPos);
	}
	public void initGraphics() {
		mSpriteManager.putText(mCaughtText);
		mSpriteManager.putText(mPreyEnergyText);
	}
	public void setCaught(int caught) {
		mCaughtText.setText(""+caught);
	}
	public void setPreyEnergy(int energy) {
		mPreyEnergyText.setText(""+energy);
	}

}
