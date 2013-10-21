package com.primalpond.hunt;

import com.google.example.games.basegameutils.BaseGameActivity;
import com.primalpond.hunt.world.logic.TheHuntRenderer.ShowNavigationListener;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


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
public class TheHunt extends BaseGameActivity implements PreyChangeDialog.PreyChangeDialogListener, ShowNavigationListener {

	private static final String TAG = "TheHunt";
	private D3GLSurfaceView mGLView;
	private MenuItem mPlayIcon;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getSupportActionBar();
		actionBar.hide();
		//		throw new RuntimeException("This is a crash");
		//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		//		if (android.os.Build.VERSION.SDK_INT >= 14) {
		//			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		//		}

		setContentView(R.layout.clean);
		mGLView = (D3GLSurfaceView)findViewById(R.id.glSurfaceView);
		mGLView.mRenderer.setActivity(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		mPlayIcon = menu.findItem(R.id.action_play_services);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_terraform:
			mGLView.post(new Runnable() {
				public void run() {
					mGLView.mRenderer.regenerateWorld();
				}
			});
			break;
		case R.id.action_play_services:
			if (isSignedIn()) {
				signOut();
				item.setIcon(getResources().getDrawable(R.drawable.games_controller_white));
			}
			else {
				beginUserInitiatedSignIn();
			}
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
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

	public void onToShowNavigation() {
		runOnUiThread(new Runnable() {

			public void run() {
				ActionBar actionBar = getSupportActionBar();
				actionBar.show();
			}
		});
	}

	public void onToHideNavigation() {
		runOnUiThread(new Runnable() {

			public void run() {
				ActionBar actionBar = getSupportActionBar();
				actionBar.hide();
			}
		});
	}

	public void onSignInFailed() {
//		Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show();
		if (mPlayIcon != null) {
			mPlayIcon.setIcon(getResources().getDrawable(R.drawable.games_controller_white));
		}
	}

	public void onSignInSucceeded() {
//		Toast.makeText(this, "Sign in succeeded", Toast.LENGTH_SHORT).show();
		if (mPlayIcon != null) {
			mPlayIcon.setIcon(getResources().getDrawable(R.drawable.games_controller_white_active));
		}
	}
}
