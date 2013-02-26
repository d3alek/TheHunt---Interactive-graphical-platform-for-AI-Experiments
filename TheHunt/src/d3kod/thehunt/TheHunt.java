package d3kod.thehunt;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.ToggleButton;
import d3kod.thehunt.agent.prey.PreyData;

public class TheHunt extends FragmentActivity implements PreyChangeDialog.PreyChangeDialogListener {

    private static final String TAG = "TheHunt";
	private D3GLSurfaceView mGLView;

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.clean);
		mGLView = (D3GLSurfaceView)findViewById(R.id.glSurfaceView);
	}
    
    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onPause()
     */
    @Override
    protected void onPause() {
    	mGLView.onPause();
    	super.onPause();
    }
    
    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onResume()
     */
    @Override
    protected void onResume() {
    	Log.v(TAG, "Resuming activity");
    	
    	if (findViewById(R.id.aiToggle) != null) ((ToggleButton)findViewById(R.id.aiToggle)).setChecked(PreyData.AI);
    	mGLView.onResume();
    	super.onResume();
    }
    
    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onStop()
     */
    @Override
    protected void onStop() {
    	super.onStop();
    }
    
    /**
     * @param view
     */
    public void showPreyChangeDialog(View view) {
    	DialogFragment newFragment = new PreyChangeDialog();
    	newFragment.show(getSupportFragmentManager(), "preyChangeDialog");
    }
    
	/* (non-Javadoc)
	 * @see d3kod.thehunt.PreyChangeDialog.PreyChangeDialogListener#onPreyChanged(int)
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
