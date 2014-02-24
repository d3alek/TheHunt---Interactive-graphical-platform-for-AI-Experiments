package com.primalpond.hunt;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.leaderboard.Leaderboard;
import com.google.android.gms.games.leaderboard.LeaderboardBuffer;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardScoreBuffer;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.OnLeaderboardScoresLoadedListener;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.primalpond.hunt.AfterTutorialScreen.OnGPlusSignInListener;
import com.primalpond.hunt.TutorialFragment.TutorialListener;
import com.primalpond.hunt.world.logic.TheHuntRenderer;
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
public class TheHunt extends BaseGameActivity implements PreyChangeDialog.PreyChangeDialogListener, ShowNavigationListener, OnLeaderboardScoresLoadedListener, TutorialListener, OnGPlusSignInListener {

	private static final String TAG = "TheHunt";
	private static final int REQUEST_LEADERBOARD = 3;
	private D3GLSurfaceView mGLView;
	private MenuItem mPlayIcon;
	private GamesClient mGamesClient;
	private String LEADERBOARD_ID;
	private boolean mToShowScoreOnSuccess;
	private int REQ_TUTORIAL = 10;
	private boolean mInTutorial;
	private TutorialFragment mTutorialFragment;
	private Fragment mAfterTutorialScreen;
	private boolean mAfterTutorialSignIn;

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
		if (MyApplication.firstRun()) {
			beginTutorial();
		}
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
		case R.id.action_help:
			//			Intent i = new Intent(this, Tutorial.class);
			//			startActivityForResult(i, REQ_TUTORIAL);
			beginTutorial();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void beginTutorial() {
		mInTutorial = true;

		if (mGLView != null) {
			mGLView.onPause();
		}
		final RelativeLayout rootLayout = (RelativeLayout)findViewById(R.id.root_layout);
		Handler handler = new Handler();
		class RefreshRunnable implements Runnable{

			public RefreshRunnable(){

			}

			public void run(){
				rootLayout.removeView(findViewById(R.id.glSurfaceView));

				D3GLSurfaceView surfaceview = new D3GLSurfaceView(getApplication(),
						null,
						new TutorialRenderer(TheHunt.this));
				surfaceview.setId(R.id.glSurfaceView);
				rootLayout.addView(surfaceview);
				
				View actionBarPart = findViewById(R.id.actionBarPart);
				actionBarPart.bringToFront();

				mGLView = (D3GLSurfaceView) findViewById(R.id.glSurfaceView);

				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				mTutorialFragment = new TutorialFragment();
				ft.add(R.id.root_layout, mTutorialFragment);
				ft.commit();
				mGLView.mRenderer.setActivity(TheHunt.this);
				onToHideNavigation();
			}
		};

		RefreshRunnable r = new RefreshRunnable();
		handler.postDelayed(r, 500);
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
		
		if (mAfterTutorialSignIn) {
			returnFromTutorial();
		}
		
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

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.e(TAG, "OnSaveInstanceState called!");
		super.onSaveInstanceState(outState);
	}

	public void onToShowTutorialText(final int nextTutorialStepNum) {
		runOnUiThread(new Runnable() {

			public void run() {
				mTutorialFragment.setTutorialStep(nextTutorialStepNum);
				getSupportFragmentManager().beginTransaction().show(mTutorialFragment).commit();

			}
		});


	}

	public void onToHideTutorialText() {
		getSupportFragmentManager().beginTransaction().hide(mTutorialFragment).commit();
	}

	public void onToShowTutorialDescriptiveText() {
		runOnUiThread(new Runnable() {

			public void run() {
				mTutorialFragment.showStepDescription();

			}
		});

	}

	public void onToShowTutTryText() {
		runOnUiThread(new Runnable() {

			public void run() {

				mTutorialFragment.showTryItYourself();
			}
		});
	}

	public void notifyReplayStep() {
		mGLView.post(new Runnable() {
			public void run() {
				((TutorialRenderer)mGLView.mRenderer).showDemonstrator();
			}
		});
	}
	
	private void returnFromTutorial() {
		mInTutorial = false;

		mGLView.onPause();
		final RelativeLayout rootLayout = (RelativeLayout)findViewById(R.id.root_layout);
		Handler handler = new Handler();
		class RefreshRunnable implements Runnable{

			public RefreshRunnable(){

			}

			public void run(){
				rootLayout.removeView(findViewById(R.id.glSurfaceView));

				D3GLSurfaceView surfaceview = new D3GLSurfaceView(getApplication(),
						null,
						new TheHuntRenderer(TheHunt.this));
				surfaceview.setId(R.id.glSurfaceView);
				rootLayout.addView(surfaceview);

				mGLView = (D3GLSurfaceView) findViewById(R.id.glSurfaceView);
				
				View actionBarPart = findViewById(R.id.actionBarPart);
				actionBarPart.bringToFront();
				
				mGLView.mRenderer.setActivity(TheHunt.this);
				MyApplication.setFirstRun(false);
			}
		};

		RefreshRunnable r = new RefreshRunnable();
		handler.postDelayed(r, 500);
	}

	public void notifyTutorialFinished() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.remove(mTutorialFragment);
		mAfterTutorialScreen = new AfterTutorialScreen();
		ft.add(R.id.root_layout, mAfterTutorialScreen);
		ft.commit();
	}

	public void onSignIn() {
		mAfterTutorialSignIn = true;
		beginUserInitiatedSignIn();
	}

	public void onUserDecline() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.remove(mAfterTutorialScreen);
		ft.commit();
		returnFromTutorial();
	}

	public void onLeaderboardScoresLoaded(int statusCode,
			Leaderboard leaderboard, LeaderboardScoreBuffer scores) {
		Log.i(TAG, "onLeaderScoresLoaded");
	}
}
