package d3kod.thehunt;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

class MyGLSurfaceView extends GLSurfaceView {

    private static final String TAG = null;
	public TheHuntRenderer mRenderer;
//	private float prevDoubleTapX;
//	private float prevDoubleTapY;
	private boolean doubleFingerSwipe;
	
	public MyGLSurfaceView(Context context, AttributeSet attrs){
		super(context, attrs);

        // Set the Renderer for drawing on the GLSurfaceView
     // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        setEGLConfigChooser(new MultisampleConfigChooser());
        setRenderer(mRenderer = new TheHuntRenderer(context));
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
//        prevDoubleTapX = prevDoubleTapY = -1;
        doubleFingerSwipe = false;
    }
    @Override
    public boolean onTouchEvent(final MotionEvent event) {
    	int action = event.getAction();
    	int count;
    	switch (action & MotionEvent.ACTION_MASK) {
    	case MotionEvent.ACTION_POINTER_DOWN:
    		count = event.getPointerCount(); // Number of 'fingers' in this time
//    		Log.v(TAG, "Pointer Count down: " + count);
    		if (count == 2) {
//    			final float x = event.getX(), y = event.getY();
//    			if (prevDoubleTapX != -1 && prevDoubleTapY != -1) {
//        			mRenderer.moveCamera(x - prevDoubleTapX, y - prevDoubleTapY);
//    			}
//    			prevDoubleTapX = x; prevDoubleTapY = y;
//    			return true;
    			doubleFingerSwipe = true;
    		}
    		break;
    	case MotionEvent.ACTION_POINTER_UP:
    		count = event.getPointerCount(); // Number of 'fingers' in this time
//    		Log.v(TAG, "Pointer Count up: " + count);
    		if (count == 2) {
    			doubleFingerSwipe = false;
//    			prevDoubleTapX = prevDoubleTapY = -1;
    		}
    		break;
    	}
//    	if (event.getAction() == MotionEvent.ACTION_POINTER_DOWN) {
//    		Log.v(TAG, "Pointer Count: " + event.getPointerCount());
//    	}
    	if (event.getAction() == MotionEvent.ACTION_DOWN) {
    		if (doubleFingerSwipe) return true;
//    		Log.v(TAG, "ACTION_DOWN");
    		if (mRenderer != null) {
    			final float x = event.getX(), y = event.getY();
    			queueEvent(new Runnable() {
    				public void run() {
    					mRenderer.handleTouchDown(x, y);
    					}
    			});
    		}
    		return true;
    	}
    	if (event.getAction() == MotionEvent.ACTION_MOVE) {
    		if (mRenderer != null) {
    			final float x = event.getX(), y = event.getY();
    			queueEvent(new Runnable() {
    				public void run() {
    					mRenderer.handleTouchMove(x, y, doubleFingerSwipe);
    					}
    			});
//    			Log.v(TAG, "ACTION_MOVE");
//    			Log.v(TAG, "Resetting prevDoubleTap");prevDoubleTapX = prevDoubleTapY = -1;
    		}
    		return true;
    	}
    	if (event.getAction() == MotionEvent.ACTION_UP) {
//    		Log.v(TAG, "ACTION_UP");
    		if (mRenderer != null) {
    			final float x = event.getX(), y = event.getY();
    			queueEvent(new Runnable() {
    				public void run() {
    					mRenderer.handleTouchUp(x, y);
    					}
    			});
    		}
    		return true;
    	}
    	return super.onTouchEvent(event);
    }
    @Override
    public void onResume() {
    	mRenderer.resume();
    	super.onResume();
    }
    @Override
    public void onPause() {
    	mRenderer.pause();
    	super.onPause();
    }
}