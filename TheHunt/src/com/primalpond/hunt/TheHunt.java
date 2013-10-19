package com.primalpond.hunt;

import com.crashlytics.android.Crashlytics;
import com.primalpond.hunt.R;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * <strong>TheHunt - Interactive graphical platform for AI Experiments</strong>
 * 
 * <p>
 * <a href="http://d3kod.github.com/TheHunt---Interactive-graphical-platform-for-AI-Experiments/">Official Website</a>
 * </p>
 * 
 * <p>
 * Open source under The MIT License, full source available on <a href="https://github.com/d3kod/TheHunt---Interactive-graphical-platform-for-AI-Experiments">Github</a>.
 * </p>
 * <p>
 * Feel free to fork, file a bug report, submit a pull request, or just tell me what you think!
 * </p>
 * 
 * @author Aleksandar Kodzhabashev (d3kod) 
 *
 */
public class TheHunt extends FragmentActivity implements PreyChangeDialog.PreyChangeDialogListener {

    private static final String TAG = "TheHunt";
	private D3GLSurfaceView mGLView;

	/** Instantiate the GLView 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Crashlytics.start(this);
//		throw new RuntimeException("This is a crash");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.clean);
		mGLView = (D3GLSurfaceView)findViewById(R.id.glSurfaceView);
	}
    
    /** Pause the GLView
     * @see android.support.v4.app.FragmentActivity#onPause()
     */
    @Override
    protected void onPause() {
    	mGLView.onPause();
    	super.onPause();
    }
    
    /** Resume the GLView
     * @see android.support.v4.app.FragmentActivity#onResume()
     */
    @Override
    protected void onResume() {
    	Log.v(TAG, "Resuming activity");
    	
//    	if (findViewById(R.id.aiToggle) != null) ((ToggleButton)findViewById(R.id.aiToggle)).setChecked(PreyData.AI);
    	mGLView.onResume();
    	super.onResume();
    }
    
    /**
     * @see android.support.v4.app.FragmentActivity#onStop()
     */
    @Override
    protected void onStop() {
    	super.onStop();
    }
    
    /** Pop up a dialog to choose a prey from a predefined list of preys
     * @see com.primalpond.hunt.PreyChangeDialog#PreyChangeDialog()
     * @param view
     */
    public void showPreyChangeDialog(View view) {
    	DialogFragment newFragment = new PreyChangeDialog();
    	newFragment.show(getSupportFragmentManager(), "preyChangeDialog");
    }
    
	/**
	 * @see com.primalpond.hunt.PreyChangeDialog.PreyChangeDialogListener#onPreyChanged(int)
	 */
	public void onPreyChanged(final int which) {
		Log.v(TAG, "Prey changed to " + which);
		mGLView.post(new Runnable() {
			public void run() {
				mGLView.mRenderer.changePrey(which);
			}
		});
	}
}
