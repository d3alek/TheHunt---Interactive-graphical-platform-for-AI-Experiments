package com.primalpond.hunt;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class AfterTutorialScreen extends Fragment {
	
	interface OnGPlusSignInListener {
		public void onSignIn();

		public void onUserDecline();
	}
	private static final String TAG = "AfterTutorialScreen";
	OnGPlusSignInListener mSignInListener = new OnGPlusSignInListener() {

		public void onSignIn() {
		}

		public void onUserDecline() {
			
		}

	};
	
	@Override
	public void onAttach(Activity activity) {
		try {
			mSignInListener = (OnGPlusSignInListener)activity;
		} catch (ClassCastException e) {
			Log.e(TAG, "Host activity does not implement OnGPlusSignInListener");
		}
		super.onAttach(activity);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.after_tutorial_screen, container, false);
		view.findViewById(R.id.sign_in_button).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mSignInListener.onSignIn();
			}
		});
		view.findViewById(R.id.another_time).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				mSignInListener.onUserDecline();
			}
		});
		return view;
	}
}
