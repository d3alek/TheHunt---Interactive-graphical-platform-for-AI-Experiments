package d3kod.thehunt;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(formKey = "dGZSenlWRjFVTk9nVFZpRjc3SHVBSnc6MQ") 
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
        super.onCreate();
    }
}
