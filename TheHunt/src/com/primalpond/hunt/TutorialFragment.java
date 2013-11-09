package com.primalpond.hunt;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.primalpond.hunt.TutorialRenderer.TutorialStep;

public class TutorialFragment extends Fragment {
	protected static final String TAG = "TutorialFragment";
	private TextView mText;
	private TutorialStep mStep;
	private TextView mDescription;
	private Button mReplayButton;
	
	interface TutorialListener {
		public void notifyReplayStep();
	}
	
	TutorialListener mTutorialListener = new TutorialListener() {
		
		public void notifyReplayStep() {
			Log.w(TAG, "Ignoring notifyReplayStep");
		}
	};
	private TextView mDemonstratingText;
	private View mDescriptionAndButtonContainer;
	
	public void onAttach(android.app.Activity activity) {
		try {
			mTutorialListener = (TutorialListener)activity;
		} catch(ClassCastException e) {
			Log.w(TAG, "Parent activity does not implement TutorialListener");
		}
		super.onAttach(activity);
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tutorial, container, false);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, 
				LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		view.setLayoutParams(layoutParams);
		mText = (TextView)view.findViewById(R.id.tutorial_text);
		mDescription = (TextView)view.findViewById(R.id.tutorial_description);
		mDescription.setVisibility(View.INVISIBLE);
		mDemonstratingText = (TextView)view.findViewById(R.id.demonstrating_text);
		mDemonstratingText.setVisibility(View.INVISIBLE);
		mReplayButton = (Button)view.findViewById(R.id.tutorial_replay);
		mReplayButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				mTutorialListener.notifyReplayStep();
			}
		});
		mReplayButton.setVisibility(View.GONE);
		mDescriptionAndButtonContainer = (View)view.findViewById(R.id.descriptionAndBtnContainer);
		mDescriptionAndButtonContainer.setVisibility(View.INVISIBLE);
		mStep = TutorialStep.NET_TRAINING;
		return view;
	}

	public void setTutorialStep(int nextTutorialStepNum) {
		mDescriptionAndButtonContainer.setVisibility(View.INVISIBLE);
		mDescription.setVisibility(View.INVISIBLE);
		mReplayButton.setVisibility(View.GONE);
//		mDemonstratingText.setVisibility(View.INVISIBLE);
		mText.setVisibility(View.VISIBLE);
		TutorialStep[] vals = TutorialStep.values();
		if (vals.length <= nextTutorialStepNum) {
			mText.setText(R.string.tut_completed);
		}
		else {
			mStep = vals[nextTutorialStepNum];
			switch (mStep) {
			case NET_TRAINING:
				mText.setText(R.string.net_training);
				break;
			case FOOD_TRAINING:
				mText.setText(R.string.food_training);
				break;
			case CUT_TRAINING:
				mText.setText(R.string.cut_training);
				break;
			}
		}
	}

	public void showStepDescription() {
		mText.setVisibility(View.INVISIBLE);
		mDescription.setVisibility(View.VISIBLE);
		mReplayButton.setVisibility(View.GONE);
//		mDemonstratingText.setVisibility(View.VISIBLE);
		mDescriptionAndButtonContainer.setVisibility(View.VISIBLE);
		switch (mStep) {
		case NET_TRAINING:
			mDescription.setText(R.string.net_training_descr);
			break;
		case FOOD_TRAINING:
			mDescription.setText(R.string.food_training_descr);
			break;
		case CUT_TRAINING:
			mDescription.setText(R.string.cut_training_descr);
			break;
		}
	}
	
	
	public void showTryItYourself() {
//		mDemonstratingText.setVisibility(View.INVISIBLE);
		switch (mStep) {
		case NET_TRAINING:
			mDescription.setText(R.string.tut_try_yourself_net);
			break;
		case FOOD_TRAINING:
			mDescription.setText(R.string.tut_try_yourself_food);
			break;
		case CUT_TRAINING:
			mDescription.setText(R.string.tut_try_yourself_cut);
			break;
		default:
			break;
		}
		mReplayButton.setVisibility(View.VISIBLE);
	}
	
}
