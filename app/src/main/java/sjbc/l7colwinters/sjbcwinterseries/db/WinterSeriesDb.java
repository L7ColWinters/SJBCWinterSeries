package sjbc.l7colwinters.sjbcwinterseries.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by L7ColWinters on 12/14/2015.
 */
public class WinterSeriesDb extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "WinterSeriesDb";

    public WinterSeriesDb(Context context){
        super(context,DB_NAME,null,DB_VERSION,null);
    }

    public WinterSeriesDb(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL(TimeTrialEventInfo.CREATE_TABLE_STATEMENT);
            db.execSQL(TimeTrialRiderInfo.CREATE_TABLE_STATEMENT);
            //db.execSQL(TimeTrialRiderInfo.CREATE_RIDER_NUM_INSERT_TRIGGER);
            db.execSQL(TimeTrialRiderInfo.CREATE_RIDER_NUM_INDEX);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(DB_NAME, "exception in onCreate, rolling back");
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * make sure foreign keys are enabled since by default they are not
     * @param db
     */
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
}
