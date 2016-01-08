package sjbc.l7colwinters.sjbcwinterseries.eventresultsgathering;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import sjbc.l7colwinters.sjbcwinterseries.R;
import sjbc.l7colwinters.sjbcwinterseries.db.TimeTrialRiderInfo;
import sjbc.l7colwinters.sjbcwinterseries.eventresultsgathering.adapter.CursorRecyclerAdapter;
import sjbc.l7colwinters.sjbcwinterseries.eventresultsgathering.adapter.TimeTrialFinishCursorAdapter;

public class TimeTrialFinishFragment extends TimeTrialFragment {

    private static final String TAG = "TimeTrialFinishFragment";

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_starting_finishing).setTitle(getString(R.string.time_trial_finish_mode));//this doesn't seem to be working!
    }

    public TimeTrialFinishFragment(){
        mProjection = new String[]{TimeTrialRiderInfo.COLUMN_ID,TimeTrialRiderInfo.COLUMN_RIDER_NUM,TimeTrialRiderInfo.COLUMN_FINISH};
        mSelection = TimeTrialRiderInfo.COLUMN_EVENT_ID + " = ? AND " + TimeTrialRiderInfo.COLUMN_FINISH + " != -1";
        mSortOrder = TimeTrialRiderInfo.COLUMN_FINISH + " DESC";
    }

    @Override
    protected CursorRecyclerAdapter getTimeTrialAdapter() {
        return new TimeTrialFinishCursorAdapter(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);

        View replacedView = view.findViewById(R.id.FragmentHeaderLayout);
        ViewGroup parent = (ViewGroup)replacedView.getParent();
        int index = parent.indexOfChild(replacedView);
        parent.removeView(replacedView);
        replacedView = inflater.inflate(R.layout.fragment_time_trial_finish_header, parent, false);//not sure this will work?
        parent.addView(replacedView,index);
        return view;
    }
}
