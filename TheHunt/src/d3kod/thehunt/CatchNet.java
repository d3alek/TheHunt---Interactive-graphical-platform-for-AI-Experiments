package d3kod.thehunt;

import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import d3kod.d3gles20.D3GLES20;
import d3kod.d3gles20.TextureManager;
import d3kod.d3gles20.programs.Program;
import d3kod.thehunt.environment.Environment;
import d3kod.thehunt.floating_text.PlokText;
import d3kod.thehunt.floating_text.SnatchText;
import d3kod.thehunt.prey.Prey;

//TODO: support multiple nets at the same time, refactor, interface to the graphics (similar to floating object)
public class CatchNet implements Tool {
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

	private boolean showSnatchText;

	private Program mProgram;

	private D3CatchNet mNetGraphic;
	private D3CatchNetPath mPathGraphic;
	
	private int mNetGraphicIndex;
	private int mPathGraphicIndex;
	
	private boolean mStarted;
	
	public CatchNet(Environment env, TextureManager tm, D3GLES20 d3GLES20) {
		mD3GLES20 = d3GLES20;
		mEnv = env;
		this.tm = tm;
		mProgram = d3GLES20.getShaderManager().getDefaultProgram();
	}
	
	public void update() {
		if (mPathGraphic == null) return;
		if (mPathGraphic.fadeDone()) {
			mPathGraphic = null;
			mD3GLES20.removeShape(mPathGraphicIndex);
		}
		else if (mPathGraphic.isFinished() && !mPathGraphic.isInvalid() && mNetGraphic == null) {
			if (mEnv.netIntersectsWithAlgae(mPathGraphic.getCenterX(), mPathGraphic.getCenterY(), mPathGraphic.getRadius())) {
				mPathGraphic.setInvalid();
				//TODO: block text
//				mD3GLES20.putExpiringShape(new BlockedText(mNetGraphic.getCenterX(), mNetGraphic.getCenterY(), tm, mD3GLES20.getShaderManager()));
			}
			else {
				mNetGraphic = new D3CatchNet(
						mPathGraphic.getCenterX(), mPathGraphic.getCenterY(), mPathGraphic.getRadius(), mProgram);
				mNetGraphicIndex = mD3GLES20.putShape(mNetGraphic);
				mD3GLES20.putExpiringShape(new PlokText(mPathGraphic.getCenterX(), mPathGraphic.getCenterY(), tm, mD3GLES20.getShaderManager()));
				mEnv.putNoise(mPathGraphic.getCenterX(), mPathGraphic.getCenterY(), Environment.LOUDNESS_PLOK);				
			}
		}
		if (mNetGraphic == null) return;
		if (mNetGraphic.getScale() < 1) {
			mNetGraphic.grow();
		}
		else {
			if (!showSnatchText) {
				mD3GLES20.putExpiringShape(new SnatchText(mNetGraphic.getCenterX(), mNetGraphic.getCenterY(), tm, mD3GLES20.getShaderManager()));
				showSnatchText = true;
			}
			if (mNetGraphic.fadeDone()) {
				mPathGraphic = null;
				mD3GLES20.removeShape(mPathGraphicIndex);
				
				mNetGraphic = null;
				mD3GLES20.removeShape(mNetGraphicIndex);
				
				if (mCaughtPrey != null) {
					Log.v(TAG, "Prey is in net!!! YAY");
					Log.v(TAG, "Let it go now...");
				}
			}
		}
//		else if (!showSnatchText && mPathGraphic.isFinished()) {
//			mD3GLES20.putExpiringShape(new SnatchText(mPathGraphic.getCenterX(), mPathGraphic.getCenterY(), tm, mD3GLES20.getShaderManager()));
//			showSnatchText = true;
//		}
	}
	
	public void start(float x, float y) {
		if (mPathGraphic != null) {
			Log.v(TAG, "Returning from start as path graphic is not null");
			return;
		}
		if (mEnv.netObstacle(x, y)) {
			Log.v(TAG, "Returing from start as there is obstacle");
			return;
		}
		
		mStarted = true;
		notShown = true;
		showSnatchText = false;
		
		mPathGraphic = new D3CatchNetPath(mProgram);
		mPathGraphicIndex = mD3GLES20.putShape(mPathGraphic);
		
		mCaughtPrey = null;
		firstX = x; firstY = y;
//		ploked = false;
		mPathGraphic.addVertex(x, y);
	}

	public void next(float x, float y) {
		if (!mStarted) {
			//TODO: not sure it is needed
			Log.v(TAG, "Starting net from next now");
			start(x, y);
			return;
		}		
		if (mPathGraphic == null || mPathGraphic.isInvalid() || mPathGraphic.isFinished()) {
			Log.v(TAG, "Returning from next 1");
			return;
		}
		if (!mPathGraphic.isFarEnoughFromLast(x, y)) {
			Log.v(TAG, "Returning from next 2");
			return;
		}
		if (mEnv.netObstacle(x, y) || mPathGraphic.getLength() > MAX_NET_LENGTH) {
			mPathGraphic.setInvalid();
		}
		mPathGraphic.addVertex(x, y);
		notShown = false;
	}

	public void finish(float x, float y) {
		// TODO: remove restrictions on net placement, handle in game logic
		mStarted = false;
		if (mPathGraphic == null) {
//			Log.v(TAG, "Setting notShown to true because of 1");
//			notShown = true;
			return;
		}
		if (mPathGraphic.isInvalid() || mPathGraphic.isFinished()) {
			// TODO: is mPathGraphic null now? ...
			return;
		}
		if (mPathGraphic.getLength() < MAX_FOOD_PLACEMENT_LENGTH) {
			// assume food placement was meant
			mPathGraphic = null;
			mD3GLES20.removeShape(mPathGraphicIndex);
			Log.v(TAG, "Setting notShown to true because of 2");
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

	public void caughtPrey(Prey mPrey) {
		mCaughtPrey = mPrey;
	}

	public boolean tryToCatch() {
		return mNetGraphic != null && mNetGraphic.getScale() >= 1;
	}

	public boolean notShown() {
		return notShown;
	}
	
	public boolean handleTouch(int action, PointF location) {
		//if (mPathGraphic != null && mPathGraphic.isFinished()) {
		//	// Can't handle the touch right now
		//	return false;
		//}
		if (action == MotionEvent.ACTION_DOWN) {
			Log.v(TAG, "Net received down " + location.x + " " + location.y);
			start(location.x, location.y);
			return true;
		}
		else if (action == MotionEvent.ACTION_MOVE) {
			Log.v(TAG, "Net received move " + location.x + " " + location.y);
			next(location.x, location.y);
			return true;
		}
		else if (action == MotionEvent.ACTION_UP) {
			Log.v(TAG, "Net received up " + location.x + " " + location.y);
			if (notShown) {
				stop(location);
				return false;
			}
			finish(location.x, location.y);
			Log.v(TAG, "After up notshown is " + notShown);
			return !notShown;
		}
		return false;
	}
	
	public void stop(PointF location) {
		finish(location.x, location.y);
	}
}
