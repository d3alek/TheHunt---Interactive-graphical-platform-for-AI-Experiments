package d3kod.thehunt.world.tools;

import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import d3kod.graphics.extra.D3Maths;
import d3kod.graphics.sprite.D3Sprite;
import d3kod.graphics.sprite.SpriteManager;
import d3kod.thehunt.world.environment.Environment;
import d3kod.thehunt.world.environment.NAlgae;

public class Knife extends D3Sprite implements Tool {

	private static final float MAX_FOOD_PLACEMENT_LENGTH = 0.1f;
	private static final String TAG = "Knife";
	private Environment mEnv;
	private NAlgae cuttingAlgae;
	private PointF start;
	private boolean didCut;
	private boolean mStarted;
	private NAlgae newCuttingAlgae;

	public Knife(Environment env, SpriteManager d3gles20) {
		super(new PointF(0, 0), d3gles20);
		mEnv = env;
	}

	// always returns true
	public boolean handleTouch(int action, PointF location) {
		// TODO Auto-generated method stub\
		if (action == MotionEvent.ACTION_DOWN) {
			cuttingAlgae = mEnv.knifeIntersectsWithAlgae(location.x, location.y);
			if (cuttingAlgae == null) {
				// invalid cut, ignore this action
				didCut = false;
				return true;
			}
			else {
				start = location;
				mStarted = true;
				didCut = false;
				return true;
			}
		}
		if (action == MotionEvent.ACTION_UP) {
//			mStarted = false;
//			if (start == null) return false;
////			cuttingAlgae = null;
//			Log.v(TAG, "UP " + start + " " + didCut);
//			if (didCut) return true;
//			if (!didCut || D3Maths.distance(start.x, start.y, location.x, location.y) < MAX_FOOD_PLACEMENT_LENGTH) {
//				return false;
//			}
//			else return true;
			mStarted = false;
			didCut = false;
			return true;
		}
		if (action == MotionEvent.ACTION_MOVE) {
			if (mStarted && didCut) return true;
			newCuttingAlgae = mEnv.knifeIntersectsWithAlgae(location.x, location.y);
			if (!mStarted) {
				if (newCuttingAlgae != null) {
					start = location;
					mStarted = true;
					didCut = false;
					cuttingAlgae = newCuttingAlgae;
					return true;
				}
				else {
					// invalid cut, ignore this action
					return true;
				}
			}
			if (newCuttingAlgae == null || newCuttingAlgae != cuttingAlgae) {
				// invalid cut, ignore this action
				return true;
			}
			else {
				if (D3Maths.distance(location.x, location.y, start.x, start.y) > cuttingAlgae.getRadius()) {
//					Log.v(TAG, "Do cut!");
//					float deltaX = (start.x - location.x), a, b;
//					if (deltaX == 0) {
//						a = b = location.x;
//					}
//					else {
//						a = (start.y - location.y)/deltaX;
//						b = start.y - start.x*a;
//					}
//					cuttingAlgae.cut(a, b);
//					didCut = true;
					PointF cuttingDir = new PointF(location.x - start.x, location.y - start.y);
					float cuttingDirLength = cuttingDir.length();
					cuttingDir.set(cuttingDir.x/cuttingDirLength, cuttingDir.y/cuttingDirLength);
					cuttingAlgae.cut(cuttingDir);
					didCut = true;
					return true;
				}
			}
		}
//		NAlgae intersectedAlgae = mEnv.knifeIntersectsWithAlgae(location.x, location.y);
//		Log.v(TAG, "IntersectedAlgae: " + intersectedAlgae.getN());
		
		return false;
	}

	public void update() {
		// TODO Auto-generated method stub
		
	}

	public void stop(PointF location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initGraphic() {
		// no graphics for knife yet
	}

	public boolean isActive() {
		return false;
	}

	public boolean didAction() {
		return didCut;
	}

}
