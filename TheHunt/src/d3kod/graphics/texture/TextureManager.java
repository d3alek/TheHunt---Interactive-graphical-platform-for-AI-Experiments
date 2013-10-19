package d3kod.graphics.texture;

import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import d3kod.graphics.texture.TextureManager.Texture;
import com.primalpond.hunt.R;

public class TextureManager {
	
	public enum Texture {
		FLOP_TEXT(R.drawable.flop_text_small),
		PLOK_TEXT(R.drawable.plok_text),
		SNATCH_TEXT(R.drawable.snatch_text), 
		CATCH_TEXT(R.drawable.catch_text),
		PANIC_TEXT(R.drawable.panic_text),
		CRUNCH_TEXT(R.drawable.crunch_text), 
		ICON_NET(R.drawable.ic_net), 
		ICON_KNIFE(R.drawable.ic_knife),
		BTN_PAUSE(R.drawable.pause);
		
		private int mId;

		private Texture(int id) {
			mId = id;
		}
		public int getId() {
			return mId;
		}
	}

	private static final String TAG = "TextureManager";

	private Context mContext;
	private HashMap<Texture, TextureInfo> mLoadedTextures;
	
	public TextureManager(Context context) {
		mContext = context;
		mLoadedTextures = new HashMap<Texture, TextureInfo>(Texture.values().length);
	}
	
	public int getTextureHandle(Texture texture) {
		if (mLoadedTextures.containsKey(texture)) {
//			Log.v(TAG, "Texture already loaded, get it from cache " + texture);
			
		}
		else {
			Log.v(TAG, "Load texture for the first time " + texture);
			mLoadedTextures.put(texture, TextureHelper.loadTexture(mContext, texture.getId()));
		}
		return mLoadedTextures.get(texture).handle;
	}

	public void clear() {
		Log.v(TAG, "Clearing loaded textures");
		mLoadedTextures.clear();
	}
	
	public void printLoadedTextures() {
		Log.v(TAG, "Loaded textures " + mLoadedTextures.size() + ":");
		for (Texture tex: mLoadedTextures.keySet()) {
			Log.v(TAG, tex.name());
		}
	}

	public TextureInfo getTextureInfo(Texture texture) {
		if (mLoadedTextures.containsKey(texture)) {
//			Log.v(TAG, "Texture already loaded, get it from cache " + texture);
			
		}
		else {
			Log.v(TAG, "Load texture for the first time " + texture);
			mLoadedTextures.put(texture, TextureHelper.loadTexture(mContext, texture.getId()));
		}
		return mLoadedTextures.get(texture);
	}
}
