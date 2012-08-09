package d3kod.thehunt;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

class MyGLSurfaceView extends GLSurfaceView {

    public TheHuntRenderer mRenderer;
//    public MyGLSurfaceView(Context context, )
//    {
//       
//    }
	public MyGLSurfaceView(Context context, AttributeSet attrs){
		super(context, attrs);

        // Set the Renderer for drawing on the GLSurfaceView
     // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        setEGLConfigChooser(new MultisampleConfigChooser());
        setRenderer(mRenderer = new TheHuntRenderer(context));
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
    @Override
    public boolean onTouchEvent(final MotionEvent event) {
    	if (event.getAction() == MotionEvent.ACTION_DOWN) {
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
    					mRenderer.handleTouchMove(x, y);
    					}
    			});
    		}
    		return true;
    	}
    	if (event.getAction() == MotionEvent.ACTION_UP) {
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
}