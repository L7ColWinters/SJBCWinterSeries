package sjbc.l7colwinters.sjbcwinterseries.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import sjbc.l7colwinters.sjbcwinterseries.WsApp;
import sjbc.l7colwinters.sjbcwinterseries.model.TimeTrialRider;

/**
 * Created by L7ColWinters on 12/15/2015.
 *
 * TimeTrialRiderInfo
 * id - primary key autoincrement
 * riderNum - int
 * eventId - int foreign key references TimeTrialEventInfo.id
 * finishTime - long
 *
 */
public class TimeTrialRiderInfo implements TimeTrialRiderCrud {

    private static final String TAG = "TimeTrialRiderInfo";
    public static final String TABLE_NAME = "TimeTrialRiderInfo";

    public static final String
        COLUMN_ID = "_id",
        COLUMN_RIDER_NUM = "riderNum",
        COLUMN_EVENT_ID = "eventId",
        COLUMN_START_TIME = "startTime",
        COLUMN_FINISH = "finishTime";
    public static final String[] ALL_COLUMNS = {COLUMN_RIDER_NUM,COLUMN_EVENT_ID,COLUMN_START_TIME,COLUMN_FINISH};
    public static final String CREATE_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_RIDER_NUM + " INTEGER UNIQUE, " +
            COLUMN_EVENT_ID + " TEXT, " +
            COLUMN_START_TIME + " INTEGER DEFAULT -1, " +
            COLUMN_FINISH + " INTEGER DEFAULT -1, " +
            "FOREIGN KEY(" + COLUMN_EVENT_ID + ") REFERENCES " + TimeTrialEventInfo.TABLE_NAME + "(" + TimeTrialEventInfo.COLUMN_EVENT_ID + "));";
    /*public static final String CREATE_RIDER_NUM_INSERT_TRIGGER = "CREATE TRIGGER time_trial_rider_number_autoincrement AFTER INSERT ON " + TABLE_NAME +
            " FOR EACH ROW WHEN NEW." + COLUMN_RIDER_NUM + "=-1 BEGIN " +
            "INSERT INTO " + TABLE_NAME + " VALUES (NEW." + COLUMN_ID + ",COALESCE((SELECT MAX(" + COLUMN_RIDER_NUM + ") FROM " + TABLE_NAME +
            "),0) + 1, NEW." + COLUMN_EVENT_ID + ",NEW." + COLUMN_START_TIME + ",NEW." + COLUMN_FINISH + "); SELECT RAISE(IGNORE); END;";*/
    private static final String SELECT_MAX_RIDER_NUM = "SELECT COALESCE((SELECT MAX(" + COLUMN_RIDER_NUM + ") FROM " + TABLE_NAME + "),0)";
    public static final String CREATE_RIDER_NUM_INDEX = "CREATE INDEX IF NOT EXISTS " + COLUMN_RIDER_NUM + "_idx ON " + TABLE_NAME + " (" + COLUMN_RIDER_NUM + ");";

    private TimeTrialRiderInfo(){
        dbInstance = new WinterSeriesDb(WsApp.getApplicationInstance());
    }

    private static WinterSeriesDb dbInstance = null;
    private static TimeTrialRiderCrud instance = null;

    public static synchronized TimeTrialRiderCrud getInstance(){
        if(instance == null){
            synchronized (TimeTrialRiderInfo.class){
                instance = new TimeTrialRiderInfo();
            }
        }
        return instance;
    }

    @Override
    public void addRider(TimeTrialRider ttr) {
        if(ttr == null){
            return;
        }
        SQLiteDatabase db = dbInstance.getWritableDatabase();

        //does tt rider exist ?
        //getTimeTrialRiderInfo(ttr.)

        //if so update

        //otherwise insert
        ContentValues values = new ContentValues();
        values.put(COLUMN_RIDER_NUM,getMaxRiderNum() + 1);
        values.put(COLUMN_EVENT_ID,ttr.eventId);
        values.put(COLUMN_START_TIME,ttr.startTime);
        values.put(COLUMN_FINISH,ttr.finishTime);

        db.insert(TABLE_NAME,null,values);
    }

    private int getMaxRiderNum(){
        return (int)dbInstance.getReadableDatabase().compileStatement(SELECT_MAX_RIDER_NUM).simpleQueryForLong();
    }

    @Override
    public void addRiders(List<TimeTrialRider> ttrs) {
        if(ttrs != null) {
            for (TimeTrialRider ttr : ttrs) {
                addRider(ttr);
            }
        }
    }

    @Override
    public TimeTrialRider getTimeTrialRiderInfo(int riderNumber,String eventId) {
        SQLiteDatabase db = dbInstance.getReadableDatabase();

        Cursor c = db.query(TABLE_NAME,
                ALL_COLUMNS,
                COLUMN_RIDER_NUM + " = ? AND " +  COLUMN_EVENT_ID + " = ?",
                new String[]{String.valueOf(riderNumber),eventId},null,null,null);
        List<TimeTrialRider> riders = getRiders(c);
        if(riders != null && riders.size() > 0){
            return riders.get(0);
        }

        return null;
    }

    @Override
    public List<TimeTrialRider> getAllTimeTrialRiderInfo(String eventId) {
        SQLiteDatabase db = dbInstance.getReadableDatabase();

        Cursor c = db.query(TABLE_NAME,
                ALL_COLUMNS,
                COLUMN_EVENT_ID + " = ?",
                new String[]{eventId}, null, null, null);

        return getRiders(c);
    }

    @Override
    public boolean editTimeTrialRiderInfo(TimeTrialRider ttr) {
        if(ttr == null){
            return false;
        }
        SQLiteDatabase db = dbInstance.getWritableDatabase();

        String whereClause = null;
        String[] whereArgs = null;
        if(ttr.riderNumber != -1){
            whereClause = COLUMN_RIDER_NUM + " = ? AND " + COLUMN_EVENT_ID + " = ?";
            whereArgs = new String[]{String.valueOf(ttr.riderNumber), ttr.eventId};
        } else {
            whereClause = COLUMN_FINISH + " = ?";
            whereArgs = new String[]{String.valueOf(ttr.finishTime)};
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_RIDER_NUM,ttr.riderNumber);
        values.put(COLUMN_EVENT_ID,ttr.eventId);
        values.put(COLUMN_START_TIME,ttr.startTime);
        values.put(COLUMN_FINISH,ttr.finishTime);

        int count = db.update(
                TABLE_NAME,
                values,
                whereClause,
                whereArgs);

        if(count == 1){
            return true;
        } else {
            Log.d(TAG, "failed to update event, id=" + ttr.eventId);
            return false;
        }
    }

    @Override
    public void deleteAllTimeTrialRiderInfo() {
        SQLiteDatabase db = dbInstance.getWritableDatabase();

        db.delete(TABLE_NAME, null,null);
    }

    @Override
    public void deleteAllTimeTrialRiderInfo(String eventId) {
        SQLiteDatabase db = dbInstance.getWritableDatabase();

        db.delete(TABLE_NAME,
                COLUMN_EVENT_ID + " = ?",
                new String[]{eventId});
    }

    @Override
    public void deleteTimeTrialRiderInfo(int riderNumber) {
        SQLiteDatabase db = dbInstance.getWritableDatabase();

        db.delete(TABLE_NAME,
                COLUMN_RIDER_NUM + " = ?",
                new String[]{String.valueOf(riderNumber)});
    }

    @Override
    public Cursor query(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbInstance.getReadableDatabase();

        Cursor c = db.query(TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);

        return c;
    }

    @Override
    public void deleteTimeTrialRiderInfo(int riderNumber, String eventId){
        SQLiteDatabase db = dbInstance.getWritableDatabase();

        db.delete(TABLE_NAME,
                COLUMN_RIDER_NUM + " = ? AND " + COLUMN_EVENT_ID + " = ?",
                new String[]{String.valueOf(riderNumber),eventId});
    }

    private List<TimeTrialRider> getRiders(Cursor c){
        if(c == null || !c.moveToFirst()){
            return null;
        }

        List<TimeTrialRider> ttrs = new ArrayList<TimeTrialRider>();
        do{
            TimeTrialRider ttr = new TimeTrialRider();
            ttr.eventId = c.getString(c.getColumnIndex(COLUMN_EVENT_ID));
            ttr.finishTime = c.getLong(c.getColumnIndex(COLUMN_FINISH));
            ttr.startTime = c.getLong(c.getColumnIndex(COLUMN_START_TIME));
            ttr.riderNumber = c.getInt(c.getColumnIndex(COLUMN_RIDER_NUM));
            ttrs.add(ttr);
        }while(c.moveToNext());
        return ttrs;
    }
}
