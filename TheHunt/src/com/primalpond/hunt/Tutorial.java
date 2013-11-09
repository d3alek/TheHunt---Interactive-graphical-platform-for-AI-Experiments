package com.primalpond.hunt;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Tutorial extends ActionBarActivity {
	private static final float DP8 = 8;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tutorial);
		findViewById(R.id.background).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				finish();
			}
		});
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.tutorial_items, new TutorialFragment());
		ft.commit();
	}


}
