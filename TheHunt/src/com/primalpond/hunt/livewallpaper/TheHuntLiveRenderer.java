package com.primalpond.hunt.livewallpaper;

import com.primalpond.hunt.world.logic.TheHuntRenderer;

import android.content.Context;

public class TheHuntLiveRenderer extends TheHuntRenderer implements GLWallpaperService.Renderer {
    public static final int TICKS_PER_SECOND = 25;	
    private static String TAG = "TheHuntLiveRenderer";
    
	public TheHuntLiveRenderer(Context context) {
		super(context, TAG);
//		changePrey(1);
	}
	
}