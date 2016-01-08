package sjbc.l7colwinters.sjbcwinterseries.eventresultsgathering;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.design.widget.NavigationView;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import sjbc.l7colwinters.sjbcwinterseries.R;
import sjbc.l7colwinters.sjbcwinterseries.pref.Pref;
import sjbc.l7colwinters.sjbcwinterseries.service.StopWatchService;

public class TimeTrialActivity extends BaseWinterSeriesActivity
        implements NavigationView.OnNavigationItemSelectedListener,StopWatchService.StopWatchListener {

    private static final String TAG = "TimeTrialActivity";
    private static SimpleDateFormat sTitleBarTimeFormat = new SimpleDateFormat("HH:mm:ss.S");

    private TimeTrialFragment resultsGatheringFragment = null;
    private StopWatchService mService = null;
    private boolean mBound = false;
    private Menu mOptionsMenu = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sTitleBarTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultsGatheringFragment.onNewItemRequested();
            }
        });

        navigationView.getMenu().removeItem(R.id.nav_time_trial);

        //this is incorrect!! this looks at the service mode and not of the time trial start mode
        if(Pref.getInt(Pref.PREF_TIME_TRIAL_MODE, StopWatchService.TIMER_MODE_STARTING) == StopWatchService.TIMER_MODE_STARTING) {
            resultsGatheringFragment = new TimeTrialStartingFragment();
        } else {
            resultsGatheringFragment = new TimeTrialFinishFragment();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.fragment_container,resultsGatheringFragment);
        fragmentTransaction.commit();

        long currentTime = Pref.getLong(Pref.PREF_TIME_TRIAL_START_TIME,0);
        if(currentTime != 0){
            getSupportActionBar().setTitle(sTitleBarTimeFormat.format(new Date(currentTime)));
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, StopWatchService.class);
        bindService(intent,mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mServiceConnection);
            mBound = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);//adds settings

        getMenuInflater().inflate(R.menu.time_trial_menu, menu);

        mOptionsMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Intent intent = new Intent(this, StopWatchService.class);
        if (id == R.id.action_start_stop) {
            if(item.getTitle().equals(getString(R.string.time_trial_start_service))){
                intent.setAction(StopWatchService.INTENT_ACTION_START);
                mService.setListener(this);
                startService(intent);
            }else if (item.getTitle().equals(getString(R.string.time_trial_stop_service))){
                stopService(intent);
            }
            return true;
        } else if (id == R.id.action_reset) {
            intent.setAction(StopWatchService.INTENT_ACTION_RESET);
            startService(intent);
            return true;
        } else if (id == R.id.action_starting_finishing){
            TimeTrialFragment replacingFragment;
            intent.setAction(StopWatchService.INTENT_ACTION_START);
            if(resultsGatheringFragment instanceof TimeTrialStartingFragment){
                if(mService!= null && mService.isTimerRunning()){
                    intent.putExtra(StopWatchService.PARAM_TIME_MODE, StopWatchService.TIMER_MODE_FINISHING);
                }
                replacingFragment = new TimeTrialFinishFragment();
            } else {
                if(mService!= null && mService.isTimerRunning()){
                    intent.putExtra(StopWatchService.PARAM_TIME_MODE,StopWatchService.TIMER_MODE_STARTING);
                }
                replacingFragment = new TimeTrialStartingFragment();
            }
            startService(intent);

            //replace fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.fragment_container, replacingFragment);
            fragmentTransaction.commit();

            resultsGatheringFragment = replacingFragment;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            StopWatchService.LocalBinder binder = (StopWatchService.LocalBinder) service;
            mService = binder.getService();
            mService.setListener(TimeTrialActivity.this);
            mBound = true;

            if(resultsGatheringFragment != null && mService != null) {
                resultsGatheringFragment.setCurrentEventId(mService.getCurrentEventId());
            }

            if(mOptionsMenu != null) {
                mOptionsMenu.findItem(R.id.action_start_stop)
                        .setTitle(
                                getString((mService.isTimerRunning() ? R.string.time_trial_stop_service : R.string.time_trial_start_service))
                        );
            } else {
                Log.wtf(TAG, "onServiceConnected: mOptionsMenu is null??");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            mService.setListener(null);
            mService = null;
        }
    };

    @Override
    public void onLapSaved() {
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                if(resultsGatheringFragment != null) {
                    resultsGatheringFragment.refreshViews();
                }
            }
        });
    }

    @Override
    public void onTimerTick(final long ms) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Date date = new Date(ms);
                Log.d(TAG, "onTimerTick: ms = " + ms + " date time = " + date.getTime());
                getSupportActionBar().setTitle(sTitleBarTimeFormat.format(new Date(ms)));
            }
        });
    }

    @Override
    public void onTimerStopped() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mOptionsMenu.findItem(R.id.action_start_stop).setTitle(getString(R.string.time_trial_start_service));
            }
        });
    }

    @Override
    public void onTimerReset() {
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                if(resultsGatheringFragment != null) {
                    resultsGatheringFragment.refreshViews();
                }
                getSupportActionBar().setTitle(sTitleBarTimeFormat.format(new Date(0)));
            }
        });
    }

    @Override
    public void onTimerStarted(){
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                if(resultsGatheringFragment != null && mService != null) {
                    resultsGatheringFragment.setCurrentEventId(mService.getCurrentEventId());
                }
                mOptionsMenu.findItem(R.id.action_start_stop).setTitle(getString(R.string.time_trial_stop_service));
            }
        });
    }
}
