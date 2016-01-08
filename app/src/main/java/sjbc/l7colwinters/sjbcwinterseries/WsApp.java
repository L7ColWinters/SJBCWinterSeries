package sjbc.l7colwinters.sjbcwinterseries;

import android.app.Application;
import android.os.StrictMode;

/**
 * Created by L7ColWinters on 12/14/2015.
 */
public class WsApp extends Application {

    private static WsApp instance = null;
    private final boolean DEVELOPER_MODE = true;

    public synchronized static Application getApplicationInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        // Only enable when developing your app!
        if (DEVELOPER_MODE) {
            // Tell Android what thread issues you want to detect and what to do when found.
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()    // or use .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            // Tell Android what VM issues you want to detect and what to do when found.
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()       // Log the problem
                    .penaltyDeath()     // Then kill the app
                    .build());
        }
        super.onCreate();
    }

    public WsApp() {
        super();
        instance = this;

        //Thread.setDefaultUncaughtExceptionHandler(new WinterSeriesUncaughtExceptionHandler());
    }
}
