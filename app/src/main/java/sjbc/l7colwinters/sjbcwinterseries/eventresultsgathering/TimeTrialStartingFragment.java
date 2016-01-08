package sjbc.l7colwinters.sjbcwinterseries.eventresultsgathering;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import sjbc.l7colwinters.sjbcwinterseries.R;
import sjbc.l7colwinters.sjbcwinterseries.db.TimeTrialRiderInfo;
import sjbc.l7colwinters.sjbcwinterseries.eventresultsgathering.adapter.CursorRecyclerAdapter;
import sjbc.l7colwinters.sjbcwinterseries.eventresultsgathering.adapter.TimeTrialStartCursorAdapter;

public class TimeTrialStartingFragment extends TimeTrialFragment{

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_starting_finishing).setTitle(getString(R.string.time_trial_start_mode));
    }

    public TimeTrialStartingFragment() {
        mProjection = new String[]{TimeTrialRiderInfo.COLUMN_ID,TimeTrialRiderInfo.COLUMN_RIDER_NUM,TimeTrialRiderInfo.COLUMN_START_TIME};
        mSelection = TimeTrialRiderInfo.COLUMN_EVENT_ID + " = ? AND " + TimeTrialRiderInfo.COLUMN_START_TIME + " != -1";
        mSortOrder = TimeTrialRiderInfo.COLUMN_START_TIME + " DESC";
    }

    @Override
    protected CursorRecyclerAdapter getTimeTrialAdapter() {
        return new TimeTrialStartCursorAdapter(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);

        View replacedView = view.findViewById(R.id.FragmentHeaderLayout);
        ViewGroup parent = (ViewGroup)replacedView.getParent();
        int index = parent.indexOfChild(replacedView);
        parent.removeView(replacedView);
        replacedView = inflater.inflate(R.layout.fragment_time_trial_start_header, parent, false);//not sure this will work?
        parent.addView(replacedView,index);
        return view;
    }
}
