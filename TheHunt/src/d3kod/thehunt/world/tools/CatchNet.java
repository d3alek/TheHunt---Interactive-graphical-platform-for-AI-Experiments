package d3kod.thehunt.world.tools;

import java.util.ArrayList;

import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import d3kod.graphics.sprite.D3Sprite;
import d3kod.graphics.sprite.SpriteManager;
import d3kod.thehunt.agent.Agent;
import d3kod.thehunt.world.environment.Environment;
import d3kod.thehunt.world.environment.FloatingObject;
import d3kod.thehunt.world.environment.NAlgae;
import d3kod.thehunt.world.floating_text.PlokText;
import d3kod.thehunt.world.floating_text.SnatchText;

//TODO: support multiple nets at the same time, refactor, interface to the graphics (similar to floating object)
public class CatchNet extends D3Sprite implements Tool {
	private static final String TAG = "CatchNet";
	private static final float MAX_FOOD_PLACEMENT_LENGTH = 0.1f;

	private static final float MAX_NET_LENGTH = 2f;

	
	private Agent mCaughtPrey;
	
	private Environment mEnv;
	private boolean notShown;
	private float firstY;
	private float firstX;

	private SpriteManager mD3GLES20;

	private boolean showSnatchText;

	private D3CatchNet mNetGraphic;
	private D3CatchNetPath mPathGraphic;
	
	private boolean mStarted;
	private int mNegativeScore;
	private ArrayList<FloatingObject> mCaughtObjects;
	private boolean mDidAction;
	
	public CatchNet(Environment env, SpriteManager d3GLES20) {
		super(new PointF(0, 0), d3GLES20);
		mD3GLES20 = d3GLES20;
		mEnv = env;
	}
	@Override
	public void initGraphic() {
		// Do nothing. Instead, graphics are initialized at each press.
	}
	@Override
	public void update() {
		if (mPathGraphic == null) return;
		if (mPathGraphic.fadeDone()) {
			mPathGraphic = null;
		}
		else if (mPathGraphic.isFinished() && !mPathGraphic.isInvalid() && mNetGraphic == null) {
			initNetGraphic();
			mD3GLES20.putText(new PlokText(mPathGraphic.getCenterX(), mPathGraphic.getCenterY()));
			mEnv.putNoise(mPathGraphic.getCenterX(), mPathGraphic.getCenterY(), Environment.LOUDNESS_PLOK);
			mDidAction = true;
		}
		if (mNetGraphic == null) return;
		if (mNetGraphic.getScale() < 1) {
			mNetGraphic.grow();
		}
		else {
			if (!showSnatchText) {
				mD3GLES20.putText(new SnatchText(mNetGraphic.getCenterX(), mNetGraphic.getCenterY()));
				showSnatchText = true;
			}
			if (mNetGraphic.fadeDone()) {
				mPathGraphic = null;
				mNetGraphic = null;
				
				if (mCaughtPrey != null) {
					Log.v(TAG, "Prey is in net!!! YAY");
					Log.v(TAG, "Let it go now...");
				}
			}
			
			if (tryToCatch()) {
				// may be a redundant check
//				Log.v(TAG, "Tryign to catch!");
				Agent prey = mEnv.getPrey();
				if (prey != null && !prey.getCaught() && contains(prey.getPosition())) {
					Log.v(TAG, "I caught the prey!");
					prey.setCaught(true);
				}
				for (FloatingObject fo:mEnv.seeObjects(getX(), getY(), getRadius())) {
					if (mCaughtObjects.contains(fo)) continue;
					Log.v(TAG, "catching floating object " + fo.getType());
					mCaughtObjects.add(fo);
					mEnv.playerRemoves(fo);
				}
				
			}
		}
	}
	
	public void start(float x, float y) {
		if (mPathGraphic != null) {
			return;
		}
		if (mEnv.netObstacle(x, y)) {
			return;
		}
		
		mDidAction = false;
		mStarted = true;
		notShown = true;
		showSnatchText = false;
		mNegativeScore = 0;
		mCaughtObjects = new ArrayList<FloatingObject>();
		
		mPathGraphic = new D3CatchNetPath();
		setPosition(new PointF(0, 0));
		initGraphic(mPathGraphic);
		
		mCaughtPrey = null;
		firstX = x; firstY = y;
		mPathGraphic.addVertex(x, y);
	}

	public void next(float x, float y) {
		if (!mStarted) {
			//TODO: not sure it is needed
			start(x, y);
			return;
		}		
		if (mPathGraphic == null || mPathGraphic.isInvalid() || mPathGraphic.isFinished()) {
			return;
		}
		if (!mPathGraphic.isFarEnoughFromLast(x, y)) {
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
			return;
		}
		if (mPathGraphic.isInvalid() || mPathGraphic.isFinished()) {
			// TODO: is mPathGraphic null now? ...
			return;
		}
		if (mPathGraphic.getLength() < MAX_FOOD_PLACEMENT_LENGTH) {
			// assume food placement was meant
			mPathGraphic = null;
//			mD3GLES20.removeShape(mPathGraphicIndex);
//			Log.v(TAG, "Setting notShown to true because of 2");
			notShown = true;
			return;
		}
		if (mPathGraphic.canFinishWith(x, y)) {
			mPathGraphic.setFinished();
			initNetGraphic();
			mD3GLES20.putText(new PlokText(firstX, firstY));
			mEnv.putNoise(x, y, Environment.LOUDNESS_PLOK); //TODO: put noise in the center
		}
		else {
			mPathGraphic.setInvalid();
		}
	}

	public void initNetGraphic() {
		mNetGraphic = new D3CatchNet(
				mPathGraphic.getCenterX(), mPathGraphic.getCenterY(), mPathGraphic.getRadius());
		setPosition(mPathGraphic.getCenter());
		initGraphic(mNetGraphic);
	}
	
	public void caughtPrey(Agent mPrey) {
		mCaughtPrey = mPrey;
	}

	private boolean tryToCatch() {
		return mNetGraphic != null && mNetGraphic.getScale() >= 1;
	}

	public boolean notShown() {
		return notShown;
	}
	
	public boolean handleTouch(int action, PointF location) {
		if (action == MotionEvent.ACTION_DOWN) {
			start(location.x, location.y);
			return true;
		}
		else if (action == MotionEvent.ACTION_MOVE) {
			next(location.x, location.y);
			return true;
		}
		else if (action == MotionEvent.ACTION_UP) {
			if (notShown) {
//				stop(location);
				finish(location.x, location.y);
				return false;
			}
			finish(location.x, location.y);
			return !notShown;
		}
		return false;
	}
	
	public boolean isActive() {
		return mNetGraphic != null;
	}
	
	public void stop(PointF location) {
		Log.i(TAG, "Stop initiated!");
//		finish(location.x, location.y);
		if (mPathGraphic != null) {
			getSpriteManager().removeShape(mPathGraphic);
			mPathGraphic = null;
			getSpriteManager().putSprite(this);
		}
		if (mNetGraphic != null) {
			getSpriteManager().removeShape(mNetGraphic);
			mNetGraphic = null;
			getSpriteManager().putSprite(this);
		}
	}

	@Override
	public void draw(float[] vMatrix, float[] projMatrix, float interpolation) {
		if (mPathGraphic != null) mPathGraphic.draw(vMatrix, projMatrix, interpolation);
		if (mNetGraphic != null) mNetGraphic.draw(vMatrix, projMatrix, interpolation);
	}
	public boolean didAction() {
		return mDidAction;
	}

}
