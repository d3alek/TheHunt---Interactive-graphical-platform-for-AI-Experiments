package d3kod.thehunt;

import android.util.Log;
import d3kod.d3gles20.D3GLES20;
import d3kod.d3gles20.TextureManager;
import d3kod.d3gles20.programs.Program;
import d3kod.thehunt.environment.Environment;
import d3kod.thehunt.floating_text.PlokText;
import d3kod.thehunt.prey.Prey;

public class CatchNet {
	
	D3CatchNetPath mPathGraphic;
	
	private int mPathGraphicIndex;

	private static final String TAG = "CatchNet";
	private static final float MAX_FOOD_PLACEMENT_LENGTH = 0.1f;

	private static final float MAX_NET_LENGTH = 2f;
	
	private Prey mCaughtPrey;
	private Environment mEnv;
	private boolean notShown;
	private TextureManager tm;
	private float firstY;
	private float firstX;
//	private boolean ploked;

	private D3GLES20 mD3GLES20;

//	private boolean showSnatchText;

	private Program mProgram;

	private D3CatchNet mNetGraphic;

	private int mNetGraphicIndex;

	public CatchNet(Environment env, TextureManager tm, D3GLES20 d3GLES20) {
		mD3GLES20 = d3GLES20;
		mEnv = env;
		this.tm = tm;
		mProgram = d3GLES20.getShaderManager().getDefaultProgram();
	}
	
	public void update() {
//		if (mPathGraphic == null) return;
//		if (mPathGraphic.fadeDone()) {	
//			mPathGraphic = null;
//			mD3GLES20.removeShape(mGraphicIndex);
//			
//			if (mCaughtPrey != null) {
//				Log.v(TAG, "Prey is in net!!! YAY");
//				Log.v(TAG, "Let it go now...");
////				mCaughtPrey.release();
//			}
//		}
		if (mPathGraphic == null) return;
		if (mPathGraphic.fadeDone()) {
			mPathGraphic = null;
			mD3GLES20.removeShape(mPathGraphicIndex);
		}
		else if (mPathGraphic.isFinished() && mNetGraphic == null) {
			mNetGraphic = new D3CatchNet(
					mPathGraphic.getCenterX(), mPathGraphic.getCenterY(),mPathGraphic.getRadius(), mProgram);
			mNetGraphicIndex = mD3GLES20.putShape(mNetGraphic);
			mD3GLES20.putExpiringShape(new PlokText(mPathGraphic.getCenterX(), mPathGraphic.getCenterY(), tm, mD3GLES20.getShaderManager()));
			mEnv.putNoise(mPathGraphic.getCenterX(), mPathGraphic.getCenterY(), Environment.LOUDNESS_PLOK);
		}
		if (mNetGraphic == null) return;
		if (mNetGraphic.getScale() < 1) {
			mNetGraphic.grow();
		}
		else if (mNetGraphic.fadeDone()) {
			mPathGraphic = null;
			mD3GLES20.removeShape(mPathGraphicIndex);
			
			mNetGraphic = null;
			mD3GLES20.removeShape(mNetGraphicIndex);
			
			if (mCaughtPrey != null) {
				Log.v(TAG, "Prey is in net!!! YAY");
				Log.v(TAG, "Let it go now...");
			}
		}
//		else if (!showSnatchText && mPathGraphic.isFinished()) {
//			mD3GLES20.putExpiringShape(new SnatchText(mPathGraphic.getCenterX(), mPathGraphic.getCenterY(), tm, mD3GLES20.getShaderManager()));
//			showSnatchText = true;
//		}
	}
	
	public void start(float x, float y) {
		if (mPathGraphic != null) {
			return;
		}
		if (mEnv.netObstacle(x, y)) {
			return;
		}
		
		notShown = false;
//		showSnatchText = false;
		
		mPathGraphic = new D3CatchNetPath(mProgram);
		mPathGraphicIndex = mD3GLES20.putShape(mPathGraphic);
		
		mCaughtPrey = null;
		firstX = x; firstY = y;
//		ploked = false;
		mPathGraphic.addVertex(x, y);
	}

	public void next(float x, float y) {
		if (mPathGraphic == null || mPathGraphic.isInvalid() || mPathGraphic.isFinished()) return;
		if (!mPathGraphic.isFarEnoughFromLast(x, y)) return;
		if (mEnv.netObstacle(x, y) || mPathGraphic.getLength() > MAX_NET_LENGTH) {
			mPathGraphic.setInvalid();
		}
		mPathGraphic.addVertex(x, y);
//		if (!ploked && mPathGraphic.getLength() >= MIN_LENGTH) {
//			mD3GLES20.putExpiringShape(new PlokText(firstX, firstY, tm, mD3GLES20.getShaderManager()));
//			mEnv.putNoise(x, y, Environment.LOUDNESS_PLOK);
//			ploked = true;
//		}
	}

	public void finish(float x, float y) {
		if (mPathGraphic == null || mPathGraphic.isInvalid() || mPathGraphic.isFinished()) return;
		if (mPathGraphic.getLength() < MAX_FOOD_PLACEMENT_LENGTH) {
			// assume food placement was meant
			mPathGraphic = null;
			mD3GLES20.removeShape(mPathGraphicIndex);
			notShown = true;
			return;
		}
		if (mPathGraphic.canFinishWith(x, y)) {
			mPathGraphic.setFinished();
			mNetGraphic = new D3CatchNet(
					mPathGraphic.getCenterX(), mPathGraphic.getCenterY(),mPathGraphic.getRadius(), mProgram);
			mNetGraphicIndex = mD3GLES20.putShape(mNetGraphic);
			mD3GLES20.putExpiringShape(new PlokText(firstX, firstY, tm, mD3GLES20.getShaderManager()));
			mEnv.putNoise(x, y, Environment.LOUDNESS_PLOK); //TODO: put noise in the center
		}
		else {
			mPathGraphic.setInvalid();
		}
	}

	public int getGraphicIndex() {
		return mNetGraphicIndex;
	}

//	public boolean isBuilt() {
//		return mPathGraphic != null && mPathGraphic.isFinished();
//	}

	public void caughtPrey(Prey mPrey) {
		mCaughtPrey = mPrey;
	}

	public boolean tryToCatch() {
		return mNetGraphic != null && mNetGraphic.getScale() >= 1;
	}

	public boolean notShown() {
		return notShown;
	}
	
}
