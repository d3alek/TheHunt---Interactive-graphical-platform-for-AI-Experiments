package d3kod.thehunt;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

class MyGLSurfaceView extends GLSurfaceView {

    private TheHuntRenderer mRenderer;
	public MyGLSurfaceView(Context context){
        super(context);

        // Set the Renderer for drawing on the GLSurfaceView
     // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        setEGLConfigChooser(new MultisampleConfigChooser());
        setRenderer(mRenderer = new TheHuntRenderer());
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
    @Override
    public boolean onTouchEvent(final MotionEvent event) {
    	if (event.getAction() == MotionEvent.ACTION_DOWN) {
    		if (mRenderer != null) {
    			final float x = event.getX(), y = event.getY();
    			queueEvent(new Runnable() {
    				public void run() {
    					mRenderer.handleTouch(x, y);
    					}
    			});
    		}
    		return true;
    	}
    	return super.onTouchEvent(event);
    }
}