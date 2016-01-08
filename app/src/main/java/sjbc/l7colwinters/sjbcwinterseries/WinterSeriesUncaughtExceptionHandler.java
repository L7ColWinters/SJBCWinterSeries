package sjbc.l7colwinters.sjbcwinterseries;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by L7ColWinters on 1/5/2016.
 */
public class WinterSeriesUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.w("WSUncaughtException", ex);
    }
}
