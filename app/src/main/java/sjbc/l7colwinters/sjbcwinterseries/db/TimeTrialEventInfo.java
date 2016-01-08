package sjbc.l7colwinters.sjbcwinterseries.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import sjbc.l7colwinters.sjbcwinterseries.WsApp;
import sjbc.l7colwinters.sjbcwinterseries.model.TimeTrial;

/**
 * Created by L7ColWinters on 12/14/2015.
 *
 * TimeTrialEventInfo
 * id - primary key autoincrement
 * date - int                           the calendar date of the event
 * event_id - text                      id that all apps will make when starting an event whether in sync or not.
 *
 */
public class TimeTrialEventInfo implements TimeTrialCrud {

    private static final String TAG = "TimeTrialEventInfo";
    public static final String TABLE_NAME = "TimeTrailEventInfo";

    public static final String
            COLUMN_EVENT_ID = "event_id",
            COLUMN_DATE = "date";

    private static final String[] ALL_COLUMNS = {COLUMN_EVENT_ID,COLUMN_DATE};

    public static final String CREATE_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
        COLUMN_EVENT_ID + " TEXT PRIMARY KEY, " +
        COLUMN_DATE + " INTEGER); ";

    private static TimeTrialCrud instance = null;
    private static WinterSeriesDb dbInstance = null;

    private TimeTrialEventInfo(){
        dbInstance = new WinterSeriesDb(WsApp.getApplicationInstance());
    }

    public static synchronized TimeTrialCrud getInstance(){
        if(instance == null){
            synchronized (TimeTrialEventInfo.class) {
                instance = new TimeTrialEventInfo();
            }
        }
        return instance;
    }

    @Override
    public void createTTEvent(TimeTrial tt) {
        SQLiteDatabase db = dbInstance.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE,tt.date);
        values.put(COLUMN_EVENT_ID,tt.eventId);

        try {
            db.insert(TABLE_NAME, null, values);
        } catch (SQLiteException e){
            Log.e(TAG,"createTTEvent exception occured" + e.getMessage());
        }
    }

    @Override
    public TimeTrial getTTEvent(String id) {
        SQLiteDatabase db = dbInstance.getReadableDatabase();

        Cursor c = db.query(TABLE_NAME,
                ALL_COLUMNS,
                COLUMN_EVENT_ID + " = ?",
                new String[]{id},null,null,null);
        List<TimeTrial> tts = getTimeTrials(c);
        if(tts != null && tts.size() > 0){
            return tts.get(0);
        }

        return null;
    }

    @Override
    public List<TimeTrial> getAllTT() {

        SQLiteDatabase db = dbInstance.getReadableDatabase();

        Cursor c = db.query(TABLE_NAME,
                ALL_COLUMNS,
                null,
                null,
                null,
                null,
                null);

        return getTimeTrials(c);
    }

    @Override
    public void deleteAll() {
        SQLiteDatabase db = dbInstance.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);
    }

    @Override
    public boolean deleteEvent(String id) {
        SQLiteDatabase db = dbInstance.getWritableDatabase();

        int rowsDeleted = db.delete(TABLE_NAME,
                COLUMN_EVENT_ID + " = ?",
                new String[]{id}
        );

        if(rowsDeleted == 1){
            return true;
        } else {
            Log.d(TAG,"unable to delete event, id=" + id);
            return false;
        }
    }

    /**
     * @param cursor
     * @return gets the list of TimeTrial events from a cursor object
     */
    private List<TimeTrial> getTimeTrials(Cursor cursor){
        if(cursor == null || !cursor.moveToFirst()){
            return null;
        }

        List<TimeTrial> list = new ArrayList<TimeTrial>();
        do{
            TimeTrial tt = new TimeTrial();
            tt.eventId = cursor.getString(cursor.getColumnIndex(COLUMN_EVENT_ID));
            tt.date = cursor.getLong(cursor.getColumnIndex(COLUMN_DATE));
            list.add(tt);
        } while(cursor.moveToNext());

        return list;
    }
}
