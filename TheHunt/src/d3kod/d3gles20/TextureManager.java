package d3kod.d3gles20;

import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import d3kod.thehunt.R;

public class TextureManager {
	public enum Texture {
		FLOP_TEXT(R.drawable.flop_text_small);
		
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
	private HashMap<Texture, Integer> mLoadedTextures;
	
	public TextureManager(Context context) {
		mContext = context;
		mLoadedTextures = new HashMap<Texture, Integer>(Texture.values().length);
	}
	
	public int getTextureHandle(Texture texture) {
		if (mLoadedTextures.containsKey(texture)) {
			Log.v(TAG, "Texture already loaded, get it from cache " + texture);
			
		}
		else {
			Log.v(TAG, "Load texture for the first time " + texture);
			mLoadedTextures.put(texture, TextureHelper.loadTexture(mContext, texture.getId()));
		}
		return mLoadedTextures.get(texture);
	}
}
