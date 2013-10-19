package d3kod.graphics.sprite.shapes;

import android.graphics.PointF;
import android.opengl.Matrix;
import android.util.Log;
import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.text.GLText;

public class D3FadingText {

//	private FloatBuffer mTextureCoordinates;
//	private int mTextureCoordinateHandle;
//	private int mTextureUniformHandle;
//	private int mTextureDataHandle;
	private static final float sizeAdj = 0.001f;
	private static final float[] colorData = {
		0.0f, 0.0f, 0.0f, 1.0f
	};
	private static final String TAG = "D3FadingText";
//	private static final int drawType = GLES20.GL_TRIANGLE_STRIP;
//
//	private final int mTextureCoordinateDataSize = 2;
//	
//	final float[] squareTextureCoordinateData = {
//	        // Front face
//	        0.0f, 0.0f,
//	        0.0f, 1.0f,
//	        1.0f, 0.0f,
//	        1.0f, 1.0f
//	};
//	final float[] squarePositionDataDefault = {
//			-0.5f, 0.5f, 0f,				
//			-0.5f, -0.5f, 0f,
//			0.5f, 0.5f, 0f,
//			0.5f, -0.5f, 0f
//	};
//	float[] squarePositionData = new float[12];
	private float mSize;
	private String mText;
	private float mFadeSpeed;
	private float mAlpha = 1.0f;
	private PointF mPos;
	private float mAngle;
//	private GLText mGLText;
	private float mG;
	private float mR;
	private float mB;
	private boolean mAmCentered;
	private float mScale;
	
	public D3FadingText(String text, float size, float fadeSpeed, boolean drawCentered) {
		this(text, size, fadeSpeed);
		setCentered(drawCentered);
	}
	
	public D3FadingText(String text, float size, float fadeSpeed) {
		mText = text;
		mSize = size;
		mFadeSpeed = fadeSpeed;
		mPos = new PointF(0, 0);
		mAngle = 0;
		mR = 0; mG = 0; mB = 0;
		mScale = 1f;
//		mGLText = glText;
		//super(colorData.clone(), drawType, sm.getTextProgram(), fadeSpeed, maxFade);
		//super.setVertexBuffer(makeVertexBuffer(textSize));
//		mTextureDataHandle = textureHandle;
//		mTextureCoordinates = Utilities.newFloatBuffer(squareTextureCoordinateData);
//        mTextureCoordinateHandle = AttribVariable.A_TexCoordinate.getHandle();//GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");
//        mTextureUniformHandle = GLES20.glGetUniformLocation(super.getProgram().getHandle(), "u_Texture");
	}
	
	public void setPosition(float x, float y, float angleDeg) {
		mPos.x = x; mPos.y = y; mAngle = angleDeg;
	}
	
	public void setColor(float r, float g, float b) {
		mR = r; mG = g; mB = b;
	}
	
	public void setAlpha(float alpha) {
		mAlpha = alpha;
	}
	
	public float getX() {
		return mPos.x;
	}
	
	public float getY() {
		return mPos.y;
	}
	
	public float getAngle() {
		return mAngle;
	}
	
	public float getLength(GLText glText) {
		glText.setScale(sizeAdj*mSize*mScale);
		glText.setSpace(1f);
		return glText.getLength(mText);
	}
	
	public float getHeight(GLText glText) {
		glText.setScale(sizeAdj*mSize*mScale);
		glText.setSpace(1f);
		return glText.getCharHeight();
	}
	
	public void draw(GLText glText, float[] vpMatrix) {
		if (faded()) return;
		glText.begin(mR, mG, mB, mAlpha, vpMatrix);
		glText.setScale(sizeAdj*mSize*mScale);
		glText.setSpace(1f);
		drawText(glText, mText, mPos.x, mPos.y, mAngle);
		glText.end();
	}
	
	public void draw(GLText glText, float[] projMatrix, float[] viewMatrix) {
		float[] vpMatrix = new float[16];
		Matrix.multiplyMM(vpMatrix, 0, projMatrix, 0, viewMatrix, 0);
		draw(glText, vpMatrix);
	}
	
	protected void drawText(GLText glText, String text, float x, float y, float angle) {
		if (mAmCentered) glText.drawC(text, x, y, angle);	
		else glText.draw(text, x, y, angle);
	}	
	
	public void fade() {
		mAlpha -= mFadeSpeed;
	}
	
	public boolean faded() {
		return D3Maths.compareFloats(mAlpha, 0.1f) <= 0;
	}
	
	public void noFade() {
		mAlpha = 1.0f;
		mFadeSpeed = 0;
	}

	public String getText() {
		return mText;
	}
	
	public void setText(String text) {
		mText = text;
	}

	public void setCentered(boolean drawCentered) {
		mAmCentered = drawCentered;
	}

	public boolean fades() {
		return D3Maths.compareFloats(mFadeSpeed, 0) != 0;
	}
	
	public void setScale(float scale) {

		mScale = scale;
	}

	public void setFaded() {
		mFadeSpeed = 1;
	}
//	private FloatBuffer makeVertexBuffer(float size) {
//		for (int i = 0; i < squarePositionData.length; ++i) {
//			squarePositionData[i] = squarePositionDataDefault[i]*size;
//		}
//		return Utilities.newFloatBuffer(squarePositionData);
//	}
	
//	@Override
//	public float getRadius() {
//		throw (new UnsupportedOperationException());
//	}

//	@Overrideu
	
}
