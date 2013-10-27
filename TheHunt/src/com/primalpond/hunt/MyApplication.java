package com.primalpond.hunt;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.crashlytics.android.Crashlytics;

public class MyApplication extends Application {
	
	private static final String TAG = "MyApplication";
	private static final String ENV_JSON = "json_env";
	private static final String ENV_SHARED_PREFS = "env_shared_prefs";
	private static final String KEY_CAUGHT_COUNTER = "caught_counter";
	private static final String KEY_PREY = "prey";
	public static float TOUCH_RADIUS_PX = 0;
	private String mRunningRenderer;
	public Object stateLock = new Object();
	public static MyApplication APPLICATION;
    
	SharedPreferences envSharedPrefs;
	
	@Override
    public void onCreate() {
        super.onCreate();
        APPLICATION = this;
//		deleteDB();
//		createDB();
		TOUCH_RADIUS_PX = getResources().getDimensionPixelSize(R.dimen.touch_radius);
		Crashlytics.start(this);
    }
//	synchronized private void createDB() {
//		if (db == null || db.ext().isClosed()) {
//			EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
//			config.common().exceptionsOnNotStorable(true);
//			config.common().add(new TransparentActivationSupport());
//			config.common().objectClass(SaveState.class).cascadeOnUpdate(true);
//			config.common().exceptionsOnNotStorable(false);
//			config.common().objectClass(NAlgae.class).callConstructor(false);
//	    	db = Db4oEmbedded.openFile(config, db4oDBFullPath(this));
//	    	
//	    	if (db == null || db.ext().isClosed()) {
//	    		Log.e(TAG, "Database creation error!");
//	    	}
//	    	else {
//	    		Log.v(TAG, "Database created!");
//	    	}
//		}
//		else {
//			Log.v(TAG, "DB not created, already active");
//		}
//	}

//	synchronized public void storeToDB(SaveState toStore) {
//			if (db == null || db.ext().isClosed()) {
//				createDB();
//			}
//			Log.v(TAG, db.ext().toString());
//			Log.v(TAG, "Storing " + toStore + " " + toStore.getEnv());
//			ObjectSet<SaveState> result = db.queryByExample(SaveState.class);
//			while (result.hasNext()) {
//				SaveState state = result.next();
//				db.delete(state);
//				Log.v(TAG, "Deleted record!");	
//			}
//			db.store(toStore);
//			Log.v(TAG, "DB Store success");
//			db.commit();
//			Log.v(TAG, "DB Commit success");
//	}
	
//	synchronized public SaveState getStateFromDB(String id) {
//		if (db == null || db.ext().isClosed()) {
//			createDB();
//		}
//		ObjectSet<SaveState> result = db.queryByExample(SaveState.class);
//		Log.v(TAG, "getStateFromDB resulted in " + result.size() + " SaveStates");
//		if (result.hasNext()) {
//			SaveState state = result.next();
//			return state;
//		}
//		return null;
//	}
	
//	synchronized public void emptyDB() {
//		if (db == null || db.ext().isClosed()) {
//			createDB();
//		}
//		ObjectSet<SaveState> result = db.queryByExample(SaveState.class);
//		while (result.hasNext()) {
//			SaveState state = result.next();
//			db.delete(state);
//			Log.v(TAG, "Deleted record!");
//			db.commit();
//			Log.v(TAG, "DB Commit success");	
//		}
//	}
	
//	synchronized public void deleteDB() {
//		if ((new File(db4oDBFullPath(this)).delete())) {
//			Log.v(TAG, "DB file deleted!");
//		}
//		else {
//			Log.v(TAG, "DB file not deleted!");
//		}
//	}
	synchronized public String getRunningRenderer() {
		return mRunningRenderer;
	}
	synchronized public void setRunningRenderer(String runningRenderer) {
		mRunningRenderer = runningRenderer;
	}
	public JSONArray getSavedEnvSprites() {
		if (envSharedPrefs == null) {
			envSharedPrefs = getSharedPreferences(ENV_SHARED_PREFS, MODE_PRIVATE);
		}
		JSONArray jsonArray;
		try {
			String saved = envSharedPrefs.getString(ENV_JSON, null);
			if (saved == null) {
				return null;
			}
			jsonArray = new JSONArray(saved);
		} catch (JSONException e) {
			e.printStackTrace();
			jsonArray = null;
		}
		return jsonArray;
	}
	public JSONObject getSavedPrey() {
		String saved = envSharedPrefs.getString(KEY_PREY, null);
		if (saved == null) {
			return null;
		}
		try {
			return new JSONObject(saved);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	public int getSavedCaught() {
		if (envSharedPrefs == null) {
			envSharedPrefs = getSharedPreferences(ENV_SHARED_PREFS, MODE_PRIVATE);
		}
		return envSharedPrefs.getInt(KEY_CAUGHT_COUNTER, 0);
	}
	public void save(JSONArray jsonEnv, JSONObject jsonPrey, int caughtCounter) {
		Editor e = envSharedPrefs.edit();
		e.clear();
		e.putString(ENV_JSON, jsonEnv.toString());
		e.putString(KEY_PREY, jsonPrey.toString());
		e.putInt(KEY_CAUGHT_COUNTER, caughtCounter);
		e.apply();
	}
	
}
