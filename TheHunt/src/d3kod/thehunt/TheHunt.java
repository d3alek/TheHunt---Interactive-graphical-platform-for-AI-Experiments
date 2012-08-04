package d3kod.thehunt;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.ToggleButton;
import d3kod.thehunt.prey.Prey;

public class TheHunt extends Activity {

    private static final String TAG = "TheHunt";
	private MyGLSurfaceView mGLView;
	private TextView mBodyBendDelay;
	private TextView mActionDelay;
	public TextView mPreyState;
	protected TextView mMSperFrame;
	private ToggleButton mPosInterpolation;
	private ToggleButton mAngleInterpolation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.main);
        mGLView = (MyGLSurfaceView)findViewById(R.id.glSurfaceView);
        mBodyBendDelay = (TextView)findViewById(R.id.bodyBendDelay);
        
        mActionDelay = (TextView)findViewById(R.id.actionDelay);
        
        mPosInterpolation = (ToggleButton)findViewById(R.id.posInterpolationToggle);
        mAngleInterpolation = (ToggleButton)findViewById(R.id.angleInterpolationToggle);
        
        mPreyState = (TextView)findViewById(R.id.preyState);
        mMSperFrame = (TextView)findViewById(R.id.msPerFrame);        
    }
    
    @Override
    protected void onPause() {
    	Log.v(TAG, "Pausing activity");
    	mGLView.onPause();
    	super.onPause();
    }
    
    @Override
    protected void onResume() {
    	Log.v(TAG, "Resuming activity");
    	
    	((ToggleButton)findViewById(R.id.aiToggle)).setChecked(Prey.AI);
    	if (mBodyBendDelay != null) mBodyBendDelay.setText(Prey.BODY_BEND_DELAY+"");
    	if (mActionDelay != null) mActionDelay.setText(Prey.ACTION_DELAY+"");
    	if (mPosInterpolation != null) mPosInterpolation.setChecked(Prey.posInterpolation);//mPosInterpolation = (ToggleButton)findViewById(R.id.posInterpolationToggle);
        if (mAngleInterpolation != null) mAngleInterpolation.setChecked(Prey.angleInterpolation);
    	
    	mGLView.onResume();
    	super.onResume();
    }
    
    public void onToggleClicked(View view) {
    	boolean checked = ((ToggleButton) view).isChecked();
    	
    	switch(view.getId()) {
    	case R.id.showTilesToggle:
    		TheHuntRenderer.SHOW_TILES = checked;
    		break;
    	case R.id.showCurrentsToggle:
    		TheHuntRenderer.SHOW_CURRENTS = checked;
    		break;
    	case R.id.aiToggle:
    		Prey.AI = checked;
    		break;
    	case R.id.angleInterpolationToggle:
    		Prey.angleInterpolation = checked;
    		break;
    	case R.id.posInterpolationToggle:
    		Prey.posInterpolation = checked;
    		break;
    	}
    }
    
    public void onButtonClicked(View view) {
    	switch(view.getId()) {
    	case R.id.plusBodyBendDelay:
    		if (Prey.BODY_BEND_DELAY < Prey.BODY_BEND_DELAY_MAX) Prey.BODY_BEND_DELAY++;
    		mBodyBendDelay.setText(Prey.BODY_BEND_DELAY+"");
    		break;
    	case R.id.minusBodyBendDelay:
    		if (Prey.BODY_BEND_DELAY > 0) Prey.BODY_BEND_DELAY--;
    		mBodyBendDelay.setText(Prey.BODY_BEND_DELAY+"");
    		break;
		case R.id.plusActionDelay:
			if (Prey.ACTION_DELAY < Prey.ACTION_DELAY_MAX) Prey.ACTION_DELAY++;
			mActionDelay.setText(Prey.ACTION_DELAY+"");
			break;
		case R.id.minusActionDelay:
			if (Prey.ACTION_DELAY > 0) Prey.ACTION_DELAY--;
			mActionDelay.setText(Prey.ACTION_DELAY+"");
			break;
    	case R.id.flopLeft:
    		mGLView.post(new Runnable() {
				public void run() {
					mGLView.mRenderer.mPrey.flopLeft();
				}
			});
    		break;
    	case R.id.flopRight:
    		mGLView.post(new Runnable() {
				public void run() {
					mGLView.mRenderer.mPrey.flopRight();
				}
			});
    		break;
    	}
    }
}
