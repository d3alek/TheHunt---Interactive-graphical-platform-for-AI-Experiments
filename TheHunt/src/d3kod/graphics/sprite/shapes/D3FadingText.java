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
	private float mLength;
	private float mG;
	private float mR;
	private float mB;
	
	public D3FadingText(String text, float size, float fadeSpeed) {
		mText = text;
		mSize = size;
		mFadeSpeed = fadeSpeed;
		mPos = new PointF(0, 0);
		mAngle = 0;
		mR = 0; mG = 0; mB = 0;
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
		Log.v(TAG, "Getting length " + mLength);
		glText.setScale(sizeAdj*mSize);
		glText.setSpace(1f);
		return glText.getLength(mText);
//		return mLength;
	}
	
	public void draw(GLText glText, float[] vpMatrix) {
		if (faded()) return;
		glText.begin(mR, mG, mB, mAlpha, vpMatrix);
		glText.setScale(sizeAdj*mSize);
		glText.setSpace(1f);
		drawText(glText, mText, mPos.x, mPos.y, mAngle);
//		mLength = glText.getLength(mText);
		glText.end();
	}
	
	public void draw(GLText glText, float[] projMatrix, float[] viewMatrix) {
		float[] vpMatrix = new float[16];
		Matrix.multiplyMM(vpMatrix, 0, projMatrix, 0, viewMatrix, 0);
		draw(glText, vpMatrix);
	}
	
	protected void drawText(GLText glText, String text, float x, float y, float angle) {
		glText.drawC(text, x, y, angle);	
	}	
	
	public void fade() {
		mAlpha -= mFadeSpeed;
	}
	
	public boolean faded() {
		return D3Maths.compareFloats(mAlpha, 0.1f) <= 0;
	}
	
	public void noFade() {
		mAlpha = 1.0f;
	}

	public String getText() {
		return mText;
	}
	
	public void setText(String text) {
		mText = text;
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
