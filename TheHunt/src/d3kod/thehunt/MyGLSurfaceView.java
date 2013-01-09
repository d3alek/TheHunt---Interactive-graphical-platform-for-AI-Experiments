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
        setDebugFlags(DEBUG_CHECK_GL_ERROR | DEBUG_LOG_GL_CALLS);
        setRenderer(mRenderer = new TheHuntRenderer((TheHunt) context));
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
    		count = event.getPointerCount(); // Number of 'fingers' at this time
    		if (count == 2) {
    			doubleFingerSwipe = true;
    		}
    		break;
    	case MotionEvent.ACTION_POINTER_UP:
    		count = event.getPointerCount(); // Number of 'fingers' in this time
    		if (count == 2) {
    			doubleFingerSwipe = false;
    		}
    		break;
    	}
//    	if (event.getAction() == MotionEvent.ACTION_DOWN) {
//    		if (doubleFingerSwipe) return true;
    		if (mRenderer != null) {
    			queueEvent(new Runnable() {
    				public void run() {
    					mRenderer.handleTouch(event, doubleFingerSwipe);
    					}
    			});
    			return true;
    		}
//    	}
//    	if (event.getAction() == MotionEvent.ACTION_MOVE) {
//    		if (mRenderer != null) {
//    			queueEvent(new Runnable() {
//    				public void run() {
//    					mRenderer.handleTouch(event, doubleFingerSwipe);
//    					}
//    			});
//    		}
//    		return true;
//    	}
//    	if (event.getAction() == MotionEvent.ACTION_UP) {
//    		if (mRenderer != null) {
//    			queueEvent(new Runnable() {
//    				public void run() {
//    					mRenderer.handleTouch(event, doubleFingerSwipe);
//    					}
//    			});
//    		}
//    		return true;
//    	}
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