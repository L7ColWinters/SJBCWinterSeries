package sjbc.l7colwinters.sjbcwinterseries.db;

import android.database.Cursor;

import java.util.List;

import sjbc.l7colwinters.sjbcwinterseries.model.TimeTrialRider;

/**
 * Created by L7ColWinters on 12/15/2015.
 */
public interface TimeTrialRiderCrud {

    void addRider(TimeTrialRider ttr);
    void addRiders(List<TimeTrialRider> ttrs);

    TimeTrialRider getTimeTrialRiderInfo(int riderNumber,String eventId);
    List<TimeTrialRider> getAllTimeTrialRiderInfo(String eventId);

    boolean editTimeTrialRiderInfo(TimeTrialRider ttr);

    void deleteAllTimeTrialRiderInfo();
    void deleteAllTimeTrialRiderInfo(String eventId);
    void deleteTimeTrialRiderInfo(int riderNumber, String eventId);
    void deleteTimeTrialRiderInfo(int riderNumber);

    Cursor query(String[] projection, String selection, String[] selectionArgs, String sortOrder);
}
