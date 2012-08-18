package d3kod.thehunt;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.ToggleButton;
import d3kod.d3gles20.D3GLES20;
import d3kod.thehunt.prey.Prey;
import d3kod.thehunt.prey.PreyData;
import d3kod.thehunt.prey.TurnAngle;

public class TheHunt extends Activity {

    private static final String TAG = "TheHunt";
	private MyGLSurfaceView mGLView;
	private TextView mBodyBendDelay;
	private TextView mActionDelay;
	public TextView mPreyState;
	protected TextView mMSperFrame;
	private ToggleButton mPosInterpolation;
	private ToggleButton mAngleInterpolation;
	protected TextView mCaughtCounter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.clean_main);
        mBodyBendDelay = (TextView)findViewById(R.id.bodyBendDelay);
        
//        mActionDelay = (TextView)findViewById(R.id.actionDelay);
//        
//        mPosInterpolation = (ToggleButton)findViewById(R.id.posInterpolationToggle);
//        mAngleInterpolation = (ToggleButton)findViewById(R.id.angleInterpolationToggle);
        
        mPreyState = (TextView)findViewById(R.id.preyState);
        mMSperFrame = (TextView)findViewById(R.id.msPerFrame);
        mCaughtCounter = (TextView)findViewById(R.id.caughtCounter);

        mGLView = (MyGLSurfaceView)findViewById(R.id.glSurfaceView);
        D3GLES20.init();
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
    	
    	((ToggleButton)findViewById(R.id.aiToggle)).setChecked(PreyData.AI);
//    	((ToggleButton)findViewById(R.id.foodToggle)).setChecked(TheHuntRenderer.FEED_WITH_TOUCH);
//    	((ToggleButton)findViewById(R.id.netToggle)).setChecked(TheHuntRenderer.NET_WITH_TOUCH);
    	if (mBodyBendDelay != null) mBodyBendDelay.setText(PreyData.BODY_BENDS_PER_SECOND+"");
    	if (mActionDelay != null) mActionDelay.setText(PreyData.ACTIONS_PER_SECOND+"");
    	if (mPosInterpolation != null) mPosInterpolation.setChecked(Prey.posInterpolation);//mPosInterpolation = (ToggleButton)findViewById(R.id.posInterpolationToggle);
        if (mAngleInterpolation != null) mAngleInterpolation.setChecked(Prey.angleInterpolation);
    	
    	mGLView.onResume();
    	super.onResume();
    }
    
    @Override
    protected void onStop() {
    	D3GLES20.clearGraphics();
    	super.onStop();
    }
    
    public void onToggleClicked(View view) {
    	boolean checked = ((ToggleButton) view).isChecked();
    	
    	switch(view.getId()) {
    	case R.id.aiToggle:
    		PreyData.AI = checked;
    		break;
//    	case R.id.foodToggle:
//    		TheHuntRenderer.FEED_WITH_TOUCH = checked;
//    		TheHuntRenderer.NET_WITH_TOUCH = !checked;
//    		break;
//    	case R.id.netToggle:
//    		TheHuntRenderer.NET_WITH_TOUCH = checked;
//    	case R.id.angleInterpolationToggle:
//    		Prey.angleInterpolation = checked;
//    		break;
//    	case R.id.posInterpolationToggle:
//    		Prey.posInterpolation = checked;
//    		break;
    	}
    }
    
    public void onButtonClicked(View view) {
    	switch(view.getId()) {
    	case R.id.plusBodyBendDelay:
    		if (PreyData.BODY_BENDS_PER_SECOND < PreyData.BODY_BENDS_PER_SECOND_MAX) {
    			//Prey.BODY_BEND_DELAY++;
    			PreyData.BODY_BENDS_PER_SECOND++;
    			PreyData.BODY_BEND_DELAY = TheHuntRenderer.TICKS_PER_SECOND/PreyData.BODY_BENDS_PER_SECOND;
//    			PreyData.rotateSpeed = PreyData.angleFlop/Prey.BODY_BEND_DELAY;
    			Log.v(TAG, "Body bend delay is " + PreyData.BODY_BEND_DELAY);
    		}
    		mBodyBendDelay.setText(PreyData.BODY_BENDS_PER_SECOND+"");
    		break;
    	case R.id.minusBodyBendDelay:
    		if (PreyData.BODY_BENDS_PER_SECOND > 1) {
    			//Prey.BODY_BEND_DELAY--;
    			PreyData.BODY_BENDS_PER_SECOND--;
    			PreyData.BODY_BEND_DELAY = TheHuntRenderer.TICKS_PER_SECOND/PreyData.BODY_BENDS_PER_SECOND;
//    			PreyData.rotateSpeed = PreyData.angleFlop/Prey.BODY_BEND_DELAY;
    			Log.v(TAG, "Body bend delay is " + PreyData.BODY_BEND_DELAY);
    		}
    		mBodyBendDelay.setText(PreyData.BODY_BENDS_PER_SECOND+"");
    		break;
//		case R.id.plusActionDelay:
//			if (Prey.ACTIONS_PER_SECOND < Prey.ACTIONS_PER_SECOND_MAX) Prey.ACTIONS_PER_SECOND++;
//			Prey.ACTION_DELAY = TheHuntRenderer.TICKS_PER_SECOND/Prey.ACTIONS_PER_SECOND;
//			mActionDelay.setText(Prey.ACTIONS_PER_SECOND+"");
//			break;
//		case R.id.minusActionDelay:
//			if (Prey.ACTIONS_PER_SECOND > 1) Prey.ACTIONS_PER_SECOND--;
//			Prey.ACTION_DELAY = TheHuntRenderer.TICKS_PER_SECOND/Prey.ACTIONS_PER_SECOND;
//			mActionDelay.setText(Prey.ACTIONS_PER_SECOND+"");
//			break;
    	case R.id.flopLeft:
    		mGLView.post(new Runnable() {
				public void run() {
					mGLView.mRenderer.mPrey.turn(TurnAngle.LEFT_MEDIUM);
				}
			});
    		break;
    	case R.id.flopRight:
    		mGLView.post(new Runnable() {
				public void run() {
					mGLView.mRenderer.mPrey.turn(TurnAngle.RIGHT_MEDIUM);
				}
			});
    		break;
    	case R.id.flopBack:
    		mGLView.post(new Runnable() {
				public void run() {
					mGLView.mRenderer.mPrey.backFinMotion(TurnAngle.BACK_MEDIUM);
				}
			});
    		break;
    	}
    }
}
