package d3kod.thehunt.livewallpaper;

import android.content.Context;
import d3kod.thehunt.world.logic.TheHuntRenderer;

public class TheHuntLiveRenderer extends TheHuntRenderer implements GLWallpaperService.Renderer {
    public static final int TICKS_PER_SECOND = 25;	
    private static String TAG = "TheHuntLiveRenderer";
    
	public TheHuntLiveRenderer(Context context) {
		super(context, TAG);
//		changePrey(1);
	}
	
}