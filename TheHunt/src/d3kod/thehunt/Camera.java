package d3kod.thehunt;

import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.Matrix;
import d3kod.d3gles20.D3GLES20;
import d3kod.d3gles20.D3Maths;
import d3kod.d3gles20.D3Sprite;
import d3kod.d3gles20.Utilities;
import d3kod.d3gles20.shapes.D3Shape;
import d3kod.thehunt.environment.Dir;
import d3kod.thehunt.environment.EnvironmentData;

public class Camera extends D3Sprite {
	
	class PreyPointerShape extends D3Shape {
		private final float[] pointerColor = {
			0.0f, 0.0f, 0.0f, 1.0f };
		private static final int drawType = GLES20.GL_LINE_LOOP;
		
		private static final float pointerSize = 0.1f;
		private final float[] pointerVertexData = {
			0.0f, pointerSize/2, 0.0f,
			-pointerSize/2, -pointerSize/2, 0.0f,
			pointerSize/2, -pointerSize/2, 0.0f };
		
		protected PreyPointerShape() {
			super();
			super.setDrawType(drawType);
			super.setColor(pointerColor);
			super.setVertexBuffer(Utilities.newFloatBuffer(pointerVertexData));
		}

		@Override
		public void setPosition(float x, float y, float angleDeg) {
			switch((int)angleDeg) {
			case 0: y -= pointerSize/2; break; // dir is N
			case 90: x += pointerSize/2; break; // dir is W
			case -90: x -= pointerSize/2; break; // dir is E
			case 180: y += pointerSize/2; break; // dir is S;
			}
			
			super.setPosition(x, y, angleDeg);
		}
		
		@Override
		public float getRadius() {
			throw new UnsupportedOperationException();
		}

	}
	
	private static final String TAG = "Camera";
	private float mCenterX;
	private float mCenterY;
	private float mViewLeft;
	private float mViewBottom;
	private float[] mVMatrix;
	private boolean recalcViewMatrix;
	private float mWidth;
	private float mHeight;
	private float mWidthToHeightRatio;
	private boolean mPointerShown;
	private PointF mPreyPos;

	public Camera(float screenToWorldWidth, float screenToWorldHeight, float widthToHeightRatio, D3GLES20 d3gles20) {
		super(new PointF(0, 0), d3gles20);
		mCenterX = mCenterY = 0;
		mVMatrix = new float[16];
		mWidth = 2*screenToWorldWidth;//*mScreenWidthPx/(float)mScreenHeightPx;
	    mHeight = 2*screenToWorldHeight;
	    mWidthToHeightRatio = widthToHeightRatio;
	    
	    hidePreyPointer();
	    
	    calcViewMatrix();
	}

	@Override
	public void initGraphic() {
		mGraphic = new PreyPointerShape();
		initGraphic(mGraphic);
	}
	
	public void update() {
		if (contains(mPreyPos)) {
				hidePreyPointer();
			}
			else {
				showPreyPointer();
			}
	}
	
	public void setPreyPosition(PointF preyPos) {
		mPreyPos = preyPos;
	}
	
	public void move(float dx, float dy) {
		recalcViewMatrix = false;
		if (D3Maths.rectContains(0, 0, 
				EnvironmentData.mScreenWidth - mWidth * mWidthToHeightRatio, 
				EnvironmentData.mScreenHeight - mHeight, 
				mCenterX + dx, mCenterY)) {
			mCenterX += dx;
			recalcViewMatrix = true;
		}
		if (D3Maths.rectContains(0, 0, 
				EnvironmentData.mScreenWidth - mWidth * mWidthToHeightRatio, 
				EnvironmentData.mScreenHeight - mHeight, 
				mCenterX, mCenterY+dy)) {
			mCenterY += dy;
			recalcViewMatrix = true;
		}
		if (recalcViewMatrix) {
			calcViewMatrix();
		}
	}
	
	private void calcViewMatrix() {
		mViewLeft = -mWidth/2 + mCenterX;
		mViewBottom = -mHeight/2 + mCenterY;
		Matrix.orthoM(mVMatrix, 0, 
				mViewLeft,
				mViewLeft+mWidth, 
				mViewBottom, 
				mViewBottom+mHeight, 0.1f, 100f);
	}

	public float[] toViewMatrix() {
		return mVMatrix;
	}

	public boolean contains(PointF point) {
		if (point == null) {
			return true;
		}
		return D3Maths.rectContains(mCenterX, mCenterY, mWidth * mWidthToHeightRatio, mHeight, point.x, point.y);
	}

	public void showPreyPointer() {
		mPointerShown = true;
	}

	public void hidePreyPointer() {
		mPointerShown = false;
	}
	
	public void setPreyPointerPosition(PointF preyPosition) {
		if (mPointerShown == false || preyPosition == null) {
			if (mGraphic != null) mGraphic.fade(1);
			return;
		}
		if (mGraphic != null) mGraphic.noFade();
		float mLeftX = mCenterX - mWidth * mWidthToHeightRatio/2;
		float mRightX = mLeftX + mWidth * mWidthToHeightRatio;
		float mBottomY = mCenterY - mHeight/2;
		float mTopY = mBottomY + mHeight;
		float mPreyPointerX, mPreyPointerY;
		Dir facingDir = Dir.UNDEFINED;
		if (preyPosition.x < mLeftX) {
			 mPreyPointerX = mLeftX;
			 facingDir = Dir.W;
		}
		else if (preyPosition.x > mRightX) {
			mPreyPointerX = mRightX;
			facingDir = Dir.E;
		}
		else {
			mPreyPointerX = preyPosition.x;
		}
		if (preyPosition.y < mBottomY) {
			mPreyPointerY = mBottomY;
			facingDir = Dir.S;
		}
		else if (preyPosition.y > mTopY) {
			mPreyPointerY = mTopY;
			facingDir = Dir.N;
		}
		else {
			mPreyPointerY = preyPosition.y;
		}

		if (mGraphic != null) mGraphic.setPosition(mPreyPointerX, mPreyPointerY, facingDir.getAngle());
	}
}
