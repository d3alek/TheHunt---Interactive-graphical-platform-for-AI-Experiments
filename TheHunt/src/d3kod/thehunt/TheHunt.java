package d3kod.thehunt;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Window;

public class TheHunt extends Activity {

    private GLSurfaceView mGLView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);
    }
    
    @Override
    protected void onPause() {
    	mGLView.onPause();
    	super.onPause();
    }
    
    @Override
    protected void onResume() {
    	mGLView.onResume();
    	super.onResume();
    }
}
