package d3kod.thehunt.world;

import java.util.ArrayList;
import java.util.Arrays;

import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.extra.Utilities;
import d3kod.graphics.sprite.D3Sprite;
import d3kod.graphics.sprite.SpriteManager;
import d3kod.graphics.sprite.shapes.D3Shape;
import d3kod.thehunt.world.environment.Dir;
import d3kod.thehunt.world.environment.EnvironmentData;

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
			case 0: y -= pointerSize*getScale()/2; break; // dir is N
			case 90: x += pointerSize*getScale()/2; break; // dir is W
			case -90: x -= pointerSize*getScale()/2; break; // dir is E
			case 180: y += pointerSize*getScale()/2; break; // dir is S;
			}
			
			super.setPosition(x, y, angleDeg);
		}
		
		@Override
		public float getRadius() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void draw(float[] mVMatrix, float[] mProjMatrix,
				float interpolation) {
			super.draw(mVMatrix, mProjMatrix, interpolation);
		}

	}
	
	private static final String TAG = "Camera";
	private static final float ZOOM_SPACING_THRESH = 8.0f;
	private float mCenterX;
	private float mCenterY;
	private float mViewLeft;
	private float mViewBottom;
	private float[] mVMatrix;
	private boolean recalcViewMatrix;
	private float mWidth;
	private float mHeight;
	private float mWidthScaled, mHeightScaled;
	private float mScale;
	private float mWidthToHeightRatio;
	private boolean mPointerShown;
	private PointF mPreyPos;
	private float[] mProjMatrix;
	private float[] mUnscaledProjMatrix;
	private float mRatio;
	private ArrayList<D3Sprite> mScaleDependentSprites;
	private float mScreenWidthPx;
	private float mScreenHeightPx;

	public Camera(float screenWidthPx, float screenHeightPx, float screenToWorldWidth, float screenToWorldHeight, float widthToHeightRatio, SpriteManager d3gles20) {
		super(new PointF(0, 0), d3gles20);
		mScreenWidthPx = screenWidthPx;
		mScreenHeightPx = screenHeightPx;
		mCenterX = mCenterY = 0;
		mVMatrix = new float[16];
		mProjMatrix = new float[16];
		mScaleDependentSprites = new ArrayList<D3Sprite>();
		mWidth = 2*screenToWorldWidth;//*mScreenWidthPx/(float)mScreenHeightPx;
	    mHeight = 2*screenToWorldHeight;
	    mWidthScaled = mWidth; 
	    mHeightScaled = mHeight;
	    mWidthToHeightRatio = widthToHeightRatio;
	    mScale = 1;
	    mRatio = widthToHeightRatio;
	    mUnscaledProjMatrix = getUnscaledProjMatrix();
	    hidePreyPointer();
	    
	    calcProjMatrix();
	    calcViewMatrix();
	}

	public void addScaleDependentSprite(D3Sprite sprite) {
		mScaleDependentSprites.add(sprite);
	}
	public void removeScaleDependentSprite(D3Sprite sprite) {
		mScaleDependentSprites.remove(sprite);
	}
	
	private void calcProjMatrix() {
	    if (mWidth > mHeight) Matrix.frustumM(mProjMatrix, 0, -mRatio*mScale, mRatio*mScale, -mScale, mScale, 1, 10);
		else Matrix.frustumM(mProjMatrix, 0, -mScale, mScale, -mScale*mRatio, mScale*mRatio, 1, 10);
	    if (mGraphic != null) mGraphic.setScale(mScale);
	    for (D3Sprite sprite: mScaleDependentSprites) {
	    	sprite.getGraphic().setScale(mScale);
	    }
//	    mWidthScaled = mWidth/mScale;
//	    mHeightScaled = mHeight/mScale;
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
	
	public void move(float dx, float dy, float prevSpacing, float thisSpacing) {
		recalcViewMatrix = false;
		
		if (Math.abs(prevSpacing - thisSpacing) > ZOOM_SPACING_THRESH) {
			//ZOOM
			Log.v(TAG, "Init zoom!");
			if (prevSpacing > thisSpacing && mWidthScaled*mWidthToHeightRatio*mScale >= EnvironmentData.mScreenWidth) {
				Log.v(TAG, "Changing center from " + mCenterX + " " + mCenterY + " to " + EnvironmentData.mScreenWidth/2
						+ " " + EnvironmentData.mScreenHeight/2);
				mCenterX = 0;
				mCenterY = 0;
				mScale = EnvironmentData.mScreenHeight/mHeight;
					
				calcViewMatrix();
				calcProjMatrix();
				return;
			}
			mScale *= prevSpacing/thisSpacing;
			Log.v(TAG, "mScale is " + mScale);
			
			float mLeftX = mCenterX - mWidthScaled * mWidthToHeightRatio * mScale/2;
			float mRightX = mLeftX + mWidthScaled * mWidthToHeightRatio * mScale;
			float mBottomY = mCenterY - mHeightScaled * mScale/2;
			float mTopY = mBottomY + mHeightScaled * mScale;
			
			Log.v(TAG, "Coords " + mLeftX + " " + mRightX + " " + mBottomY + " " + mTopY);
			
			if (mLeftX < -EnvironmentData.mScreenWidth/2) {
				Log.v(TAG, "Adjusting centerX because of left");
				mCenterX = -EnvironmentData.mScreenWidth/2 + mWidthScaled * mWidthToHeightRatio * mScale/2;
			}
			
			else if (mRightX > EnvironmentData.mScreenWidth/2) {
				Log.v(TAG, "Adjusting centerX because of right");
				mCenterX = EnvironmentData.mScreenWidth/2 - mWidthScaled * mWidthToHeightRatio * mScale/2;
			}
			
			if (mBottomY < -EnvironmentData.mScreenHeight/2) {
				Log.v(TAG, "Adjusting centerY because of bottom");
				mCenterY = -EnvironmentData.mScreenHeight/2 + mHeightScaled * mScale/2;
			}
			
			else if (mTopY > EnvironmentData.mScreenHeight/2) {
				Log.v(TAG, "Adjusting centerY because of top");
				mCenterY = EnvironmentData.mScreenHeight/2 - mHeightScaled * mScale / 2;
			}
			
			if (!D3Maths.rectContains(0, 0, 
					EnvironmentData.mScreenWidth - mWidthScaled * mWidthToHeightRatio*mScale, 
					EnvironmentData.mScreenHeight - mHeightScaled*mScale, 
					mCenterX, mCenterY)) {
				mCenterX += dx;
				recalcViewMatrix = true;
			}
			if (D3Maths.rectContains(0, 0, 
					EnvironmentData.mScreenWidth - mWidthScaled * mWidthToHeightRatio*mScale, 
					EnvironmentData.mScreenHeight - mHeightScaled*mScale, 
					mCenterX, mCenterY+dy)) {
				mCenterY += dy;
				recalcViewMatrix = true;
			}
			calcViewMatrix();
			calcProjMatrix();
		}
		else {
			if (D3Maths.rectContains(0, 0, 
					EnvironmentData.mScreenWidth - mWidthScaled * mWidthToHeightRatio*mScale, 
					EnvironmentData.mScreenHeight - mHeightScaled*mScale, 
					mCenterX + dx, mCenterY)) {
				mCenterX += dx;
				recalcViewMatrix = true;
			}
			if (D3Maths.rectContains(0, 0, 
					EnvironmentData.mScreenWidth - mWidthScaled * mWidthToHeightRatio*mScale, 
					EnvironmentData.mScreenHeight - mHeightScaled*mScale, 
					mCenterX, mCenterY+dy)) {
				mCenterY += dy;
				recalcViewMatrix = true;
			}
			if (recalcViewMatrix) {
				calcViewMatrix();
			}
		}
	}
	
	private void calcViewMatrix() {
		mViewLeft = -mWidthScaled/2 + mCenterX;
		mViewBottom = -mHeightScaled/2 + mCenterY;
		Matrix.orthoM(mVMatrix, 0, 
				mViewLeft,
				mViewLeft+mWidthScaled, 
				mViewBottom, 
				mViewBottom+mHeightScaled, 0.1f, 100f);
	}
	
	public float[] toCenteredViewMatrix() {
		float[] viewMatrix = new float[16];
		mViewLeft = -mWidthScaled/2;
		mViewBottom = -mHeightScaled/2;
		Matrix.orthoM(viewMatrix, 0, 
				mViewLeft,
				mViewLeft+mWidthScaled, 
				mViewBottom, 
				mViewBottom+mHeightScaled, 0.1f, 100f);
		return viewMatrix;
	}

	public float[] toViewMatrix() {
		return mVMatrix;
	}
	public float[] toProjMatrix() {
		return mProjMatrix;
	}

	public boolean contains(PointF point) {
		if (point == null) {
			return true;
		}
		return D3Maths.rectContains(mCenterX, mCenterY, mWidthScaled * mWidthToHeightRatio * mScale, mHeightScaled * mScale, point.x, point.y);
	}
	public float getWidth() {
		return mWidthScaled * mWidthToHeightRatio;
	}
	public float getHeight() {
		return mHeightScaled;
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
		float mLeftX = mCenterX - mWidthScaled * mWidthToHeightRatio * mScale/2;
		float mRightX = mLeftX + mWidthScaled * mWidthToHeightRatio * mScale;
		float mBottomY = mCenterY - mHeightScaled * mScale/2;
		float mTopY = mBottomY + mHeightScaled * mScale;
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

	public float[] getUnscaledProjMatrix() {
		float saveScale = mScale;
		mScale = 1f;
		calcProjMatrix();
		float[] mat = Arrays.copyOf(toProjMatrix(), 16);
		mScale = saveScale;
		calcProjMatrix();
		return mat;
	}
	
	float[] normalizedInPoint = new float[4];
	float[] outPoint = new float[4];
	float[] mPVMatrix = new float[16];
	
	public PointF fromScreenToWorld(float touchX, float touchY) {
		return fromScreenToWorld(touchX, touchY, mVMatrix, mProjMatrix);
	}	
	
	public PointF fromScreenToWorld(float touchX, float touchY, float[] viewMatrix, float[] projMatrix) {
		normalizedInPoint[0] = 2f*touchX/mScreenWidthPx - 1;
		normalizedInPoint[1] = 2f*(mScreenHeightPx - touchY)/mScreenHeightPx - 1;
		normalizedInPoint[2] = -1f;
		normalizedInPoint[3] = 1f;
		
		Matrix.multiplyMM(mPVMatrix, 0, projMatrix, 0, viewMatrix, 0);
		Matrix.invertM(mPVMatrix, 0, mPVMatrix, 0);
		Matrix.multiplyMV(outPoint, 0, mPVMatrix, 0, normalizedInPoint, 0);
		return new PointF(outPoint[0], outPoint[1]);
	}
}
