package d3kod.thehunt;

import d3kod.thehunt.world.logic.TheHuntRenderer;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class D3GLSurfaceView extends GLSurfaceView {

    private static final String TAG = null;
	public TheHuntRenderer mRenderer;
	private boolean doubleFingerSwipe;
	
	public D3GLSurfaceView(Context context, AttributeSet attrs){
		super(context, attrs);

        // Set the Renderer for drawing on the GLSurfaceView
     // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        setEGLConfigChooser(new MultisampleConfigChooser());
        setDebugFlags(DEBUG_CHECK_GL_ERROR | DEBUG_LOG_GL_CALLS);
        setRenderer(mRenderer = new TheHuntRenderer((TheHunt) context));
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
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
    	if (mRenderer != null) {
    		queueEvent(new Runnable() {
    			public void run() {
    				mRenderer.handleTouch(event, doubleFingerSwipe);
    			}
    		});
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