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
import d3kod.thehunt.prey.PreyData;

public class TheHunt extends FragmentActivity implements PreyChangeDialog.PreyChangeDialogListener {

    private static final String TAG = "TheHunt";
	private MyGLSurfaceView mGLView;
	private TextView mBodyBendDelay;
	private TextView mActionDelay;
	public TextView mPreyState;
	protected TextView mMSperFrame;
//	private ToggleButton mPosInterpolation;
//	private ToggleButton mAngleInterpolation;
	protected TextView mScore;
//	protected TextView mEnergyCounter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.clean_release);
        mMSperFrame = (TextView)findViewById(R.id.msPerFrame);
        mScore = (TextView)findViewById(R.id.caughtCounter);
//        mEnergyCounter = (TextView)findViewById(R.id.preyEnergy);
        mGLView = (MyGLSurfaceView)findViewById(R.id.glSurfaceView);
        mPreyState = (TextView)findViewById(R.id.preyState);
        showPreyChangeDialog(null);
//        ErrorReporter.getInstance().handleException(null);
    }
    
    @Override
    protected void onPause() {
//    	Log.v(TAG, "Pausing activity");
    	mGLView.onPause();
    	super.onPause();
    }
    
    @Override
    protected void onResume() {
    	Log.v(TAG, "Resuming activity");
    	
    	if (findViewById(R.id.aiToggle) != null) ((ToggleButton)findViewById(R.id.aiToggle)).setChecked(PreyData.AI);
    	if (mBodyBendDelay != null) mBodyBendDelay.setText(PreyData.BODY_BENDS_PER_SECOND+"");
    	if (mActionDelay != null) mActionDelay.setText(PreyData.ACTIONS_PER_SECOND+"");
//    	if (mPosInterpolation != null) mPosInterpolation.setChecked(Prey.posInterpolation);//mPosInterpolation = (ToggleButton)findViewById(R.id.posInterpolationToggle);
//        if (mAngleInterpolation != null) mAngleInterpolation.setChecked(Prey.angleInterpolation);
//    	
    	mGLView.onResume();
    	super.onResume();
    }
    
    @Override
    protected void onStop() {
//    	D3GLES20.clearGraphics();
    	super.onStop();
    }
    
    public void onToggleClicked(View view) {
    	boolean checked = ((ToggleButton) view).isChecked();
    	
    	switch(view.getId()) {
    	case R.id.aiToggle:
    		PreyData.AI = checked;
    		break;
    	}
    }
    
    public void onButtonClicked(View view) {
    	switch(view.getId()) {
    	case R.id.flopLeft:
//    		mGLView.post(new Runnable() {
//				public void run() {
//					mGLView.mRenderer.mPrey.turn(TurnAngle.LEFT_SMALL);
//				}
//			});
    		break;
    	case R.id.flopRight:
//    		mGLView.post(new Runnable() {
//				public void run() {
//					mGLView.mRenderer.mPrey.turn(TurnAngle.RIGHT_SMALL);
//				}
//			});
    		break;
    	case R.id.flopBack:
//    		mGLView.post(new Runnable() {
//				public void run() {
//					mGLView.mRenderer.mPrey.backFinMotion(TurnAngle.BACK_LARGE);
//				}
//			});
    		break;
    	}
    }

    public void showPreyChangeDialog(View view) {
    	DialogFragment newFragment = new PreyChangeDialog();
    	newFragment.show(getSupportFragmentManager(), "preyChangeDialog");
//    	newFragment.show(getFragmentManager(), "yo");
    }
    
	public void onPreyChanged(final int which) {
		Log.v(TAG, "Prey changed to " + which);
//		mGLView.mRenderer.changePrey(which);
		mGLView.post(new Runnable() {
			
			public void run() {
				mGLView.mRenderer.changePrey(which);
			}
		});
	}
}
