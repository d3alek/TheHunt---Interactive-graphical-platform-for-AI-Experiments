package com.primalpond.hunt;

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.ta.TransparentActivationSupport;
import com.primalpond.hunt.world.logic.SaveState;

public class MyApplication extends Application {
	
    private static final String DB_FILENAME = "state.db4o";
	private static final String TAG = "MyApplication";
    ObjectContainer db;
	private String mRunningRenderer;
	public Object stateLock = new Object();
    
	@Override
    public void onCreate() {
        super.onCreate();
//		deleteDB();
		createDB();
		Crashlytics.start(this);
    }
	synchronized private void createDB() {
		if (db == null || db.ext().isClosed()) {
			EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
			config.common().exceptionsOnNotStorable(true);
			config.common().add(new TransparentActivationSupport());
			config.common().objectClass(SaveState.class).cascadeOnUpdate(true);
	    	db = Db4oEmbedded.openFile(config, db4oDBFullPath(this));
	    	
	    	if (db == null || db.ext().isClosed()) {
	    		Log.e(TAG, "Database creation error!");
	    	}
	    	else {
	    		Log.v(TAG, "Database created!");
	    	}
		}
		else {
			Log.v(TAG, "DB not created, already active");
		}
	}
	private String db4oDBFullPath(Context context) {
		return context.getDir("data", 0) + "/" + DB_FILENAME;
	}
	
	synchronized public void storeToDB(SaveState toStore) {
			if (db == null || db.ext().isClosed()) {
				createDB();
			}
			Log.v(TAG, db.ext().toString());
			Log.v(TAG, "Storing " + toStore + " " + toStore.getEnv());
			ObjectSet<SaveState> result = db.queryByExample(SaveState.class);
			while (result.hasNext()) {
				SaveState state = result.next();
				db.delete(state);
				Log.v(TAG, "Deleted record!");	
			}
			db.store(toStore);
			Log.v(TAG, "DB Store success");
			db.commit();
			Log.v(TAG, "DB Commit success");
	}
	
	synchronized public SaveState getStateFromDB(String id) {
		if (db == null || db.ext().isClosed()) {
			createDB();
		}
		ObjectSet<SaveState> result = db.queryByExample(SaveState.class);
		Log.v(TAG, "getStateFromDB resulted in " + result.size() + " SaveStates");
		if (result.hasNext()) {
			SaveState state = result.next();
			return state;
		}
		return null;
	}
	
	synchronized public void emptyDB() {
		if (db == null || db.ext().isClosed()) {
			createDB();
		}
		ObjectSet<SaveState> result = db.queryByExample(SaveState.class);
		while (result.hasNext()) {
			SaveState state = result.next();
			db.delete(state);
			Log.v(TAG, "Deleted record!");
			db.commit();
			Log.v(TAG, "DB Commit success");	
		}
	}
	
	synchronized public void deleteDB() {
		if ((new File(db4oDBFullPath(this)).delete())) {
			Log.v(TAG, "DB file deleted!");
		}
		else {
			Log.v(TAG, "DB file not deleted!");
		}
	}
	synchronized public String getRunningRenderer() {
		return mRunningRenderer;
	}
	synchronized public void setRunningRenderer(String runningRenderer) {
		mRunningRenderer = runningRenderer;
	}
}
