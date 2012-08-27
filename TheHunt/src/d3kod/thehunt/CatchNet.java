package d3kod.thehunt;

import android.util.Log;
import d3kod.d3gles20.D3GLES20;
import d3kod.d3gles20.TextureManager;
import d3kod.thehunt.environment.Environment;
import d3kod.thehunt.floating_text.PlokText;
import d3kod.thehunt.floating_text.SnatchText;
import d3kod.thehunt.prey.Prey;

public class CatchNet {
	
	D3CatchNet mGraphic;
//	int mGraphicIndex = -1;
	
	private int mGraphicIndex;

	private static final String TAG = "CatchNet";
	private static final float MIN_LENGTH = 0.1f;
	
	private boolean beingBuilt;
	private boolean isBuilt;
	private Prey mCaughtPrey;
	private Environment mEnv;
//	private boolean isInvalid;
	private boolean notShown;
	private TextureManager tm;
	private float firstY;
	private float firstX;
	private boolean ploked;

	private D3GLES20 mD3GLES20;

	private boolean showSnatchText;

	private int mProgramHandle;

	public CatchNet(Environment env, TextureManager tm, D3GLES20 d3GLES20) {
		mD3GLES20 = d3GLES20;
		mEnv = env;
		this.tm = tm;
		mProgramHandle = d3GLES20.getShaderManager().getDefaultProgramHandle();
	}
	
	public void update() {
		if (mGraphic == null) return;
		if (mGraphic.fadeDone()) {
			
			mGraphic = null;
			mD3GLES20.removeShape(mGraphicIndex);
			
			if (mCaughtPrey != null) {
				Log.v(TAG, "Prey is in net!!! YAY");
				Log.v(TAG, "Let it go now...");
//				mCaughtPrey.release();
			}
			
			return;
		}
		
		if (!showSnatchText && mGraphic.isFinished()) {
			mD3GLES20.putExpiringShape(new SnatchText(mGraphic.getCenterX(), mGraphic.getCenterY(), tm, mD3GLES20.getShaderManager()));
			showSnatchText = true;
		}
		
		return;
	}
	
	public void start(float x, float y) {
		if (mGraphic != null) {
			return;
		}
		if (mEnv.netObstacle(x, y)) {
			return;
		}
//		beingBuilt = true;
//		isBuilt = false;
//		isInvalid = false;
		notShown = false;
		showSnatchText = false;
		
		mGraphic = new D3CatchNet(tm, mProgramHandle);
		mGraphicIndex = mD3GLES20.putShape(mGraphic);
		mCaughtPrey = null;
		firstX = x; firstY = y;
		ploked = false;
		mGraphic.addVertex(x, y);
	}

	public void next(float x, float y) {
		if (mGraphic == null || mGraphic.isInvalid() || mGraphic.isFinished()) return;
		if (!mGraphic.isFarEnoughFromLast(x, y)) return;
		if (mEnv.netObstacle(x, y)) {
			mGraphic.setInvalid();
		}
		mGraphic.addVertex(x, y);
		if (!ploked && mGraphic.getLength() >= MIN_LENGTH) {
			mD3GLES20.putExpiringShape(new PlokText(firstX, firstY, tm, mD3GLES20.getShaderManager()));
			ploked = true;
		}
	}

	public void finish(float x, float y) {
		if (mGraphic == null || mGraphic.isInvalid() || mGraphic.isFinished()) return;
		if (mGraphic.isCloseEnoughToStart(x, y)) {
			if (mGraphic.getLength() < MIN_LENGTH) {
				// assume food placement was meant
				mGraphic = null;
				mD3GLES20.removeShape(mGraphicIndex);
				notShown = true;
				return;
			}
			else mGraphic.setFinished();
		}
		else {
			mGraphic.setInvalid();
		}
	}

	public int getGraphicIndex() {
		return mGraphicIndex;
	}

	public boolean isBuilt() {
		return mGraphic != null && mGraphic.isFinished();
	}

	public void caughtPrey(Prey mPrey) {
		mCaughtPrey = mPrey;
	}

	public boolean tryToCatch() {
		return mGraphic != null && mGraphic.fadeDone();
	}

	public boolean notShown() {
		return notShown;
	}
	
}
