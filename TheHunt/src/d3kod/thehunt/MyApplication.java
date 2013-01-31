package d3kod.thehunt;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.Context;
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
    
	@Override
    public void onCreate() {
		emptyDB();
		createDB();
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
        super.onCreate();
    }
	private void createDB() {
		if (db == null || db.ext().isClosed()) {
			EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
//			config.
			config.common().exceptionsOnNotStorable(true);
			config.common().add(new TransparentActivationSupport());
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
    	db.ext().configure().messageLevel(2);
//    	db.ext().configure().setOut(Log.)
	}
	private String db4oDBFullPath(Context context) {
		return context.getDir("data", 0) + "/" + DB_FILENAME;
	}
	
	public void storeToDB(SaveState toStore) {
		if (db == null || db.ext().isClosed()) {
			createDB();
		}
		Log.v(TAG, db.ext().toString());
		db.store(toStore);
		Log.v(TAG, "DB Store success");
//		db.close(); 
		Log.v(TAG, "DB Close success");
	}
	
	public SaveState getStateFromDB() {
		if (db == null || db.ext().isClosed()) {
			createDB();
		}
		ObjectSet<SaveState> result = db.queryByExample(SaveState.class);
		if (result.hasNext()) {
			SaveState state = result.next();
			return state;
		}
		return null;
	}
	
	public void emptyDB() {
		if (db == null || db.ext().isClosed()) {
			createDB();
		}
		ObjectSet<SaveState> result = db.queryByExample(SaveState.class);
		while (result.hasNext()) {
			SaveState state = result.next();
			db.delete(state);
			Log.v(TAG, "Deleted record!");
		}
	}
}
