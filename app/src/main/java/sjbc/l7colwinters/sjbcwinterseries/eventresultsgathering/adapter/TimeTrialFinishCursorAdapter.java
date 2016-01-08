package sjbc.l7colwinters.sjbcwinterseries.eventresultsgathering.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import sjbc.l7colwinters.sjbcwinterseries.R;
import sjbc.l7colwinters.sjbcwinterseries.custom.views.LongClickEditText;
import sjbc.l7colwinters.sjbcwinterseries.db.TimeTrialRiderInfo;

/**
 * Created by L7ColWinters on 12/22/2015.
 */
public class TimeTrialFinishCursorAdapter extends CursorRecyclerAdapter<TimeTrialFinishCursorAdapter.ViewHolder> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.S");

    public TimeTrialFinishCursorAdapter(Cursor cursor) {
        super(cursor);

        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public void onBindViewHolderCursor(ViewHolder holder, Cursor cursor) {
        holder.place.setText(String.valueOf(cursor.getPosition() + 1));
        holder.riderNum.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(TimeTrialRiderInfo.COLUMN_RIDER_NUM))));
        holder.finishTime.setText(dateFormat.format(
                new Date(cursor.getLong(cursor.getColumnIndex(TimeTrialRiderInfo.COLUMN_FINISH)))
        ));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_trial_result_list_item,null,false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView place = null;
        public LongClickEditText riderNum = null;
        public LongClickEditText finishTime = null;

        public ViewHolder(View itemView) {
            super(itemView);

            place = (TextView) itemView.findViewById(R.id.rider_place);
            riderNum = (LongClickEditText) itemView.findViewById(R.id.rider_number);
            finishTime = (LongClickEditText) itemView.findViewById(R.id.finish_time);
        }
    }
}
