package sjbc.l7colwinters.sjbcwinterseries.eventresultsgathering.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import sjbc.l7colwinters.sjbcwinterseries.R;
import sjbc.l7colwinters.sjbcwinterseries.db.TimeTrialRiderInfo;

/**
 * Created by L7ColWinters on 12/21/2015.
 */
public class TimeTrialStartCursorAdapter extends CursorRecyclerAdapter<TimeTrialStartCursorAdapter.ViewHolder> {

    private SimpleDateFormat mDateFormat = new SimpleDateFormat("HH:mm:ss.S");

    public TimeTrialStartCursorAdapter(Cursor cursor) {
        super(cursor);

        mDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public void onBindViewHolderCursor(ViewHolder holder, Cursor cursor) {
        holder.riderNum.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(TimeTrialRiderInfo.COLUMN_RIDER_NUM))));
        holder.startTime.setText(mDateFormat.format(
                new Date(cursor.getLong(cursor.getColumnIndex(TimeTrialRiderInfo.COLUMN_START_TIME)))
        ));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_trial_start_list_item, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public EditText riderNum = null;
        public EditText startTime = null;

        public ViewHolder(View itemView) {
            super(itemView);

            riderNum = (EditText)itemView.findViewById(R.id.rider_number);
            startTime = (EditText)itemView.findViewById(R.id.start_time);
        }
    }
}
