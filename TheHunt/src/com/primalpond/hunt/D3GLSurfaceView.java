package com.primalpond.hunt;

import com.primalpond.hunt.world.logic.TheHuntRenderer;

import android.app.Application;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class D3GLSurfaceView extends GLSurfaceView {

    private static final String TAG = null;
	public TheHuntRenderer mRenderer;
	private boolean doubleTouch;
//	private GestureDetector mGestureDetector;
	
	public D3GLSurfaceView(Context context, AttributeSet attrs) {
		this(context, attrs, null);
	}
	public D3GLSurfaceView(Context context, AttributeSet attrs, TheHuntRenderer renderer){
		super(context, attrs);

        // Set the Renderer for drawing on the GLSurfaceView
     // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        setEGLConfigChooser(new MultisampleConfigChooser());
        setDebugFlags(DEBUG_CHECK_GL_ERROR | DEBUG_LOG_GL_CALLS);
        if (renderer == null) {
        	mRenderer = new TheHuntRenderer(context);
        }
        else {
        	mRenderer = renderer;
        }
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        doubleTouch = false;
//        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
//        	@Override
//        	public void onLongPress(final MotionEvent e) {
//        		queueEvent(new Runnable() {
//    			public void run() {
//    				Log.v(TAG, "Long press detected!");
//    				mRenderer.reportLongPress(e);
//    			}
//    		});
//        	}
//        }); 
    }
    public D3GLSurfaceView(Application application) {
    	this(application, null);
	}
	@Override
    public boolean onTouchEvent(final MotionEvent event) {
//    	mGestureDetector.onTouchEvent(event);
       	doubleTouch = doubleTouch ^ TheHuntRenderer.motionEventDoubleTouchChanged(event);
    	if (mRenderer != null) {
    		queueEvent(new Runnable() {
    			public void run() {
    				mRenderer.handleTouch(event, doubleTouch);
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