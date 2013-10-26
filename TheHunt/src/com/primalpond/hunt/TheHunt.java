package com.primalpond.hunt;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.leaderboard.LeaderboardBuffer;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardScoreBuffer;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.OnLeaderboardScoresLoadedListener;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.primalpond.hunt.world.logic.TheHuntRenderer.ShowNavigationListener;


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
public class TheHunt extends BaseGameActivity implements PreyChangeDialog.PreyChangeDialogListener, ShowNavigationListener, OnLeaderboardScoresLoadedListener {

	private static final String TAG = "TheHunt";
	private static final int REQUEST_LEADERBOARD = 3;
	private D3GLSurfaceView mGLView;
	private MenuItem mPlayIcon;
	private GamesClient mGamesClient;
	private String LEADERBOARD_ID;
	private boolean mToShowScoreOnSuccess;

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
		LEADERBOARD_ID = getResources().getString(R.string.leaderboard_prey_caught);
		
		findViewById(R.id.actionBarPart).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				mGLView.mRenderer.pauseWorld();
				onToShowNavigation();
			}
		});
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
		case R.id.action_show_scoreboard:
			if (isSignedIn()) {
				if (mGLView != null && mGLView.mRenderer != null) {
					mGamesClient.submitScore(LEADERBOARD_ID, mGLView.mRenderer.mCaughtCounter);
				}
				startActivityForResult(mGamesClient.getLeaderboardIntent(LEADERBOARD_ID), REQUEST_LEADERBOARD);
			}
			else {
				beginUserInitiatedSignIn();
				mToShowScoreOnSuccess = true;
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
//		if (mGamesClient != null) {
//			mGamesClient.loadPlayerCenteredScores(this, LEADERBOARD_ID, 2, LeaderboardVariant.COLLECTION_PUBLIC, 10);
//		}
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
		mToShowScoreOnSuccess = false;
		if (mPlayIcon != null) {
			mPlayIcon.setIcon(getResources().getDrawable(R.drawable.games_controller_white));
		}
	}

	public void onSignInSucceeded() {
//		Toast.makeText(this, "Sign in succeeded", Toast.LENGTH_SHORT).show();
		if (mPlayIcon != null) {
			mPlayIcon.setIcon(getResources().getDrawable(R.drawable.games_controller_white_active));
		}
		mGamesClient = getGamesClient();
		if (mToShowScoreOnSuccess) {
			startActivityForResult(mGamesClient.getLeaderboardIntent(LEADERBOARD_ID), REQUEST_LEADERBOARD);
			mToShowScoreOnSuccess = false;
		}
		
		issueLeaderboardRefresh();
		
//		mGamesClient.loadPlayerCenteredScores(this, LEADERBOARD_ID, 2, LeaderboardVariant.COLLECTION_PUBLIC, 10);
		
	}

	public void onLeaderboardScoresLoaded(int statusCode,
			LeaderboardBuffer leaderboard, LeaderboardScoreBuffer scores) {
		boolean found = false;
		if (statusCode == GamesClient.STATUS_OK && mGLView != null && mGLView.mRenderer != null) {
			String mId = mGamesClient.getCurrentPlayerId();
			Log.i(TAG, "onLeaderboardScoresLoaded: ");
			for (int i = 0; i < scores.getCount(); ++i) {
				LeaderboardScore score = scores.get(i);
				LeaderboardScore fScore = score.freeze();
				if (fScore.getScoreHolder().getPlayerId().equals(mId)) {
					found = true;
					Log.i(TAG, "Found me at position " + i);
					mGLView.mRenderer.setMyStats(fScore.getDisplayRank(), fScore.getDisplayScore(), (int)fScore.getRawScore());
					if (i > 0) {
						Log.i(TAG, "Found prev ");
						LeaderboardScore prevScore = scores.get(i-1).freeze();
						mGLView.mRenderer.setPrevPlayer(prevScore.getDisplayRank(), prevScore.getDisplayScore());
					}
					else {
						mGLView.mRenderer.setPrevPlayer("", "");
					}
					if (i < scores.getCount()-1) {
						Log.i(TAG, "Found next ");
						LeaderboardScore nextScore = scores.get(i+1).freeze();
						mGLView.mRenderer.setNextPlayer(nextScore.getDisplayRank(), nextScore.getDisplayScore());
					}
					else {
						mGLView.mRenderer.setNextPlayer("", "");
					}
					break;
				}
			}
			if (!found) {
				mGLView.mRenderer.setMyStats("", "", -1);
			}
		}
		else {
			Toast.makeText(this, "Error retrieving scores " + statusCode, Toast.LENGTH_SHORT).show();
		}
		leaderboard.close();
		scores.close();

	}

	public void issueLeaderboardRefresh() {
		if (isSignedIn() && mGamesClient != null) {
			if (mGLView != null && mGLView.mRenderer != null) {
				mGamesClient.submitScore(LEADERBOARD_ID, mGLView.mRenderer.mCaughtCounter);
			}
			mGamesClient.loadPlayerCenteredScores(this, LEADERBOARD_ID, 2, LeaderboardVariant.COLLECTION_PUBLIC, 10, true);
		}
	}
}
