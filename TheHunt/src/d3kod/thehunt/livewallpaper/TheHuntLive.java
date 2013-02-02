package d3kod.thehunt.livewallpaper;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import d3kod.thehunt.MultisampleConfigChooser;
import d3kod.thehunt.world.logic.TheHuntRenderer;

public class TheHuntLive extends Wallpaper {

	private final String TAG = "TheHuntLive";
	
	private final Handler mHandler = new Handler();
	@Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "Creating TheHunt Wallpaper!");
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "Destroing TheHunt Wallpaper!");
        super.onDestroy();
    }
    
    @Override
    public GLEngine onCreateEngine() {
        return new HuntLiveEngine(this);
    }
    
    class HuntLiveEngine extends WallpaperEngine {
    	private static final int DEBUG_CHECK_GL_ERROR = 1;
    	public static final int DEBUG_LOG_GL_CALLS = 2;
		public TheHuntLiveRenderer mRenderer;
    	private boolean doubleTouch;
    	
        HuntLiveEngine(Context context) {
			super();
			setEGLContextFactory(new ContextFactory());
			setEGLConfigChooser(new MultisampleConfigChooser());//new ConfigChooser(5, 6, 5, 0, 16, 0));
			setDebugFlags(DEBUG_CHECK_GL_ERROR | DEBUG_LOG_GL_CALLS);
			mRenderer = new TheHuntLiveRenderer(context);
			setRenderer(mRenderer);
			setRenderMode(RENDERMODE_CONTINUOUSLY);
			doubleTouch = false;
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            // By default we don't get touch events, so enable them.
            setTouchEventsEnabled(true);
        }

        @Override
        public void onDestroy() {
        	if (mRenderer != null) {
         		mRenderer.release(); // assuming yours has this method - it should!
         	}
         	mRenderer = null;
         	super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
        	Log.i(TAG, "Visibility changed " + visible);
//        	if (!visible) {
//        		mRenderer.pause();
//        	}
//        	else mRenderer.resume();
        	super.onVisibilityChanged(visible);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
        }
        // TODO: Not working!
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
        @Override
        public void onTouchEvent(final MotionEvent event) {
       	doubleTouch = doubleTouch ^ TheHuntRenderer.motionEventDoubleTouchChanged(event);
        	if (mRenderer != null) {
        		queueEvent(new Runnable() {
        			public void run() {
        				mRenderer.handleTouch(event, doubleTouch);
        			}
        		});
        		//        		return true;
        	}
        	//        	return super.onTouchEvent(event);
        }
//        @Override
//        public void onResume() {
//        	mRenderer.resume();
//        	super.onResume();
//        }
//        @Override
//        public void onPause() {
//        	mRenderer.pause();
//        	super.onPause();
//        }}
    }
}

