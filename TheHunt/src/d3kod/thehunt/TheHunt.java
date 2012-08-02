package d3kod.thehunt;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;
import d3kod.thehunt.prey.Prey;

public class TheHunt extends Activity {

    private static final String TAG = "TheHunt";
	private GLSurfaceView mGLView;
	private TextView mBodyBendDelay;
	private TextView mActionDelay;
	public TextView mPreyState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.main);
        mGLView = (GLSurfaceView)findViewById(R.id.glSurfaceView);
        mBodyBendDelay = (TextView)findViewById(R.id.bodyBendDelay);
        mBodyBendDelay.setText(Prey.BODY_BEND_DELAY+"");
        mActionDelay = (TextView)findViewById(R.id.actionDelay);
        mActionDelay.setText(Prey.ACTION_DELAY+"");
        mPreyState = (TextView)findViewById(R.id.preyState);
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
    
    public void onToggleClicked(View view) {
    	boolean checked = ((ToggleButton) view).isChecked();
    	
    	switch(view.getId()) {
    	case R.id.showTilesToggle:
    		TheHuntRenderer.SHOW_TILES = checked;
    		break;
    	case R.id.showCurrentsToggle:
    		TheHuntRenderer.SHOW_CURRENTS = checked;
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
		}
    }
}
