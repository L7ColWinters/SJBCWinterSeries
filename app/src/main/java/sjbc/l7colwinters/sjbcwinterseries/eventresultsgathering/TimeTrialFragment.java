package sjbc.l7colwinters.sjbcwinterseries.eventresultsgathering;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sjbc.l7colwinters.sjbcwinterseries.R;
import sjbc.l7colwinters.sjbcwinterseries.db.TimeTrialRiderInfo;
import sjbc.l7colwinters.sjbcwinterseries.eventresultsgathering.adapter.CursorRecyclerAdapter;
import sjbc.l7colwinters.sjbcwinterseries.service.StopWatchService;

/**
 * Created by L7ColWinters on 12/22/2015.
 */
public abstract class TimeTrialFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "TimeTrialFragment";
    protected static final int LOADER_START_INFO = 0;

    // TODO: Rename and change types of parameters
    protected String mCurrentEventId;

    protected RecyclerView mRecyclerView;
    protected CursorRecyclerAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    protected String[] mProjection = null;
    protected String mSelection = null;
    protected String[] mSelectionArgs = null;
    protected String mSortOrder = null;

    public void onNewItemRequested(){
        Context c = getContext();
        if(c != null){
            Intent intent = new Intent(c,StopWatchService.class);
            intent.setAction(StopWatchService.INTENT_ACTION_LAP);
            c.startService(intent);
        }
    }
    public void refreshViews(){
        if(mCurrentEventId != null) {
            getLoaderManager().restartLoader(LOADER_START_INFO, null, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_trial, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.start_times_list);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = getTimeTrialAdapter();
        mRecyclerView.setAdapter(mAdapter);

        if(mCurrentEventId != null) {
            getLoaderManager().initLoader(LOADER_START_INFO, null, this);
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        getLoaderManager().destroyLoader(LOADER_START_INFO);
    }

    protected abstract CursorRecyclerAdapter getTimeTrialAdapter();

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id) {
            case LOADER_START_INFO:
                return new CursorLoader(
                        getActivity(),
                        null,
                        mProjection,
                        mSelection,
                        mSelectionArgs,
                        mSortOrder
                ){
                    //I don't want to use a content provider and this seems like the simplest way to get around it.
                    @Override
                    public Cursor loadInBackground() {
                        return TimeTrialRiderInfo.getInstance().query(getProjection(),getSelection(),getSelectionArgs(),getSortOrder());
                    }
                };
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    public void setCurrentEventId(String currentEventId) {
        mCurrentEventId = currentEventId;
        mSelectionArgs = new String[]{mCurrentEventId};
        refreshViews();
    }
}
