package d3kod.thehunt;

import java.io.File;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.Context;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.ta.TransparentActivationSupport;

import d3kod.thehunt.world.logic.SaveState;

@ReportsCrashes(formKey = "dGZSenlWRjFVTk9nVFZpRjc3SHVBSnc6MQ") 
public class MyApplication extends Application {
	
    private static final String DB_FILENAME = "state.db4o";
	private static final String TAG = "MyApplication";
    ObjectContainer db;
//    private String readID; // ID that read last in the current DB state
	private String mRunningRenderer;
	public Object stateLock = new Object();
    
	@Override
    public void onCreate() {
//		emptyDB();
//		readID = "";
		deleteDB();
		createDB();
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
        super.onCreate();
    }
	synchronized private void createDB() {
		if (db == null || db.ext().isClosed()) {
			EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
//			config.
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
//    	db.ext().configure().messageLevel(2);
//    	db.ext().configure().setOut(Log.)
	}
	private String db4oDBFullPath(Context context) {
		return context.getDir("data", 0) + "/" + DB_FILENAME;
	}
	
	synchronized public void storeToDB(SaveState toStore) {
//		while (true) {
			if (db == null || db.ext().isClosed()) {
				createDB();
			}
			Log.v(TAG, db.ext().toString());
			Log.v(TAG, "Storing " + toStore + " " + toStore.getEnv());
			db.store(toStore);
			Log.v(TAG, "DB Store success");
			db.commit();
			Log.v(TAG, "DB Commit success");
//			db.close(); 
//			Log.v(TAG, "DB Close success");
//			readID = "";
//		}
	}
	
	synchronized public SaveState getStateFromDB(String id) {
//		if (id == readID) {
//			SaveState state = new SaveState(null);
//			state.setSameAsLast(true);
//			return state;
//		}
//		readID = id;
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
//			db.close();
//			Log.v(TAG, "DB closed!");
		}
	}
	
	synchronized public void deleteDB() {
//		deleteFile(db4oDBFullPath(this));
//		if (deleteFile(DB_FILENAME)) {
		if ((new File(db4oDBFullPath(this)).delete())) {
			Log.v(TAG, "DB file deleted!");
		}
		else {
			Log.v(TAG, "DB file not deleted!");
		}
//		if (new File(db4oDBFullPath(this) + ".log").delete())
//			Log.v(TAG, ".log file deleted!");
//		if (new File(db4oDBFullPath(this) + ".lck").delete())
//			Log.v(TAG, ".lck file deleted!");	
//		if (new File(db4oDBFullPath(this) + ".properties").delete())
//			Log.v(TAG, ".properties file deleted!");	
//		if (new File(db4oDBFullPath(this) + ".script").delete())
//			Log.v(TAG, ".script file deleted!");	
//		new File(hsqldbFileName + ".script").delete();
	}
	synchronized public String getRunningRenderer() {
		return mRunningRenderer;
	}
	synchronized public void setRunningRenderer(String runningRenderer) {
		mRunningRenderer = runningRenderer;
	}
}
