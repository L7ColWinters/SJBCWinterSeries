package sjbc.l7colwinters.sjbcwinterseries.pref;

import android.content.SharedPreferences;

import sjbc.l7colwinters.sjbcwinterseries.WsApp;

/**
 * Created by L7ColWinters on 12/16/2015.
 */
public class Pref {

    public static final String PREF_TIME_TRIAL_START_TIME = "time_trial_start_time";
    public static final String PREF_TIME_TRIAL_MODE = "time_trial_mode";
    public static final String GOOGLE_ACCOUNT_ID = "google_account_id";
    public static final String PREF_TIME_TRIAL_EVENT_ID = "time_trial_event_id";

    private static final String PREF_FILE = "WinterSeriesPrefFile";
    private static SharedPreferences prefs = WsApp.getApplicationInstance().getSharedPreferences(PREF_FILE,0);

    public static void putLong(String key, long val){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key,val);
        editor.commit();
    }

    public static void putInt(String key, int val){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, val);
        editor.commit();
    }

    public static int getInt(String key, int defValue){
        return prefs.getInt(key,defValue);
    }

    public static long getLong(String key,long defValue){
        return prefs.getLong(key,defValue);
    }

    public static void putString(String key, String val){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key,val);
        editor.commit();
    }

    public static String getString(String key,String defValue){
        return prefs.getString(key,defValue);
    }
}
