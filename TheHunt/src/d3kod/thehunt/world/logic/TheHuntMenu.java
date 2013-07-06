package d3kod.thehunt.world.logic;

import android.graphics.PointF;
import android.view.MotionEvent;
import d3kod.graphics.shader.programs.Program;
import d3kod.graphics.sprite.D3Sprite;
import d3kod.graphics.sprite.SpriteManager;
import d3kod.graphics.sprite.shapes.D3FadingText;
import d3kod.graphics.sprite.shapes.D3Quad;
import d3kod.graphics.text.GLText;
import d3kod.thehunt.world.Camera;

public class TheHuntMenu extends D3Sprite {

	private float mWidth = 0.3f;
	private float mHeight = 0.5f;
	private Program mProgram;
	private D3FadingText mButtonResume;
	private GLText mGLText;
	private static final float[] bgHideColor = {0.0f, 0.0f, 0.0f, 1.0f};
	private static final float[] bgShowColor = {1.0f, 1.0f, 1.0f, 1.0f};

	public TheHuntMenu(SpriteManager d3gles20, Camera camera) {
		super(new PointF(0, 0), null);
		mProgram = d3gles20.getDefaultProgram();
		mGLText = d3gles20.getTextManager();
	}

	public void show() {
		mGraphic.setColor(bgShowColor);
		mGraphic.noFade();
	}
	
	public void hide() {
		mGraphic.setColor(bgHideColor);
		mGraphic.setFaded();
	}

	@Override
	public void initGraphic() {
		mGraphic = new D3Quad(mWidth, mHeight);
		mGraphic.setProgram(mProgram);
		mButtonResume = new D3FadingText("Resume", 10, 0);
		mButtonResume.setCentered(true);
		mButtonResume.setPosition(0, 0, 0);
		hide();
	}
	
	@Override
	public void draw(float[] vMatrix, float[] projMatrix, float interpolation) {
		super.draw(vMatrix, projMatrix, interpolation);
//		float[] vpMatrix = new float[16];
		mButtonResume.draw(mGLText, projMatrix, vMatrix);
	}

	public boolean handleTouch(PointF location, int action) {
		// on first up do nothing
		// otherwise return false if not in rectange
		return false;
	}

}