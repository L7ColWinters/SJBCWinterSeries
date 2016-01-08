package sjbc.l7colwinters.sjbcwinterseries.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import sjbc.l7colwinters.sjbcwinterseries.db.TimeTrialEventInfo;
import sjbc.l7colwinters.sjbcwinterseries.db.TimeTrialRiderInfo;
import sjbc.l7colwinters.sjbcwinterseries.model.TimeTrial;
import sjbc.l7colwinters.sjbcwinterseries.model.TimeTrialRider;
import sjbc.l7colwinters.sjbcwinterseries.pref.Pref;

public class StopWatchService extends Service {

    public static final String INTENT_ACTION_LAP = "sjbc.l7colwinters.sjbcwinterseries.timetrial.lap";
    public static final String INTENT_ACTION_RESET = "sjbc.l7colwinters.sjbcwinterseries.timetrial.reset";
    /** to send the mode send it with a start intent, if already started but want to switch modes specify new mode in start intent */
    public static final String INTENT_ACTION_START = "sjbc.l7colwinters.sjbcwinterseries.timetrial.start";

    public static final String PARAM_TIME_MODE = "timer_mode";

    public static final int TIMER_MODE_STARTING = 1;
    public static final int TIMER_MODE_FINISHING = 2;
    
    private static final String TAG = "StopwatchIntentService";
    private static final long TIMER_PERIOD = 100;

    private final IBinder mBinder = new LocalBinder();

    private Timer mTimer = null;
    private long mCurrentTime = 0;
    private String mEventId = null;
    private int mStartMode = TIMER_MODE_STARTING;

    private StopWatchListener listener = null;

    public StopWatchService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mCurrentTime = Pref.getLong(Pref.PREF_TIME_TRIAL_START_TIME,0);
        mStartMode = Pref.getInt(Pref.PREF_TIME_TRIAL_MODE,TIMER_MODE_STARTING);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        onStop();
    }

    public class LocalBinder extends Binder {
        public StopWatchService getService(){
            return StopWatchService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        new Thread(new Runnable(){
            @Override
            public void run() {
                if (intent != null) {
                    final String action = intent.getAction();
                    if (INTENT_ACTION_LAP.equals(action)) {
                        handleLapAction();
                    } else if (INTENT_ACTION_RESET.equals(action)) {
                        handleResetAction();
                    } else if(INTENT_ACTION_START.equals(action)){
                        handleStartAction(intent.getIntExtra(PARAM_TIME_MODE,TIMER_MODE_STARTING));
                    } else {
                        Log.w(TAG,"onHandleIntent: intent was not handled with action " + action);
                    }
                }
            }
        }).start();

        return START_STICKY;
    }

    private void onStop() {
        Pref.putLong(Pref.PREF_TIME_TRIAL_START_TIME, mCurrentTime);
        if(mTimer != null){
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;

            if(listener != null){
                listener.onTimerStopped();
            }
            listener = null;
        } else {
            Log.w(TAG, "handleStopAction: time already in stopped state");
        }
    }

    private void handleStartAction(int startMode) {
        Log.i(TAG,"handleStartAction is called");
        if(mTimer == null){
            mTimer = new Timer();

            mEventId = Pref.getString(Pref.PREF_TIME_TRIAL_EVENT_ID,null);
            TimeTrial event = new TimeTrial();
            if(mEventId == null || !event.eventId.equals(mEventId)){
                TimeTrialEventInfo.getInstance().createTTEvent(event);
                mEventId = event.eventId;
                Pref.putString(Pref.PREF_TIME_TRIAL_EVENT_ID, mEventId);
            }

            mTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    mCurrentTime += TIMER_PERIOD;
                    if (listener != null) {
                        listener.onTimerTick(mCurrentTime);
                    }
                }
            }, 0, TIMER_PERIOD);

            if(listener != null){
                listener.onTimerStarted();
            }
        } else {
            Log.w(TAG, "handleStartAction: timer already in started state");
        }

        mStartMode = startMode;
        Pref.putInt(Pref.PREF_TIME_TRIAL_MODE,mStartMode);
    }

    public void handleLapAction(){
        if(mTimer == null){//if not started, start it
            handleStartAction(TIMER_MODE_STARTING);
        }

        Log.d(TAG, "handleLapAction: mode = " + mStartMode);
        TimeTrialRider ttr;
        //need to save a lap
        switch(mStartMode){
            case TIMER_MODE_STARTING:
                ttr = new TimeTrialRider();
                ttr.startTime = mCurrentTime;
                ttr.eventId = mEventId;
                TimeTrialRiderInfo.getInstance().addRider(ttr);
                break;
            case TIMER_MODE_FINISHING:
                ttr = new TimeTrialRider();
                ttr.eventId = mEventId;
                ttr.finishTime = mCurrentTime;
                Log.d(TAG, "handleLapAction: rider finish = " + ttr.finishTime);
                TimeTrialRiderInfo.getInstance().addRider(ttr);
                break;
            default:
                //make sure it's set at 0 and not running, don't notify listener
                Log.w(TAG, "handleLapAction: mode not supported, mode = " + mStartMode);
                handleResetAction();
                return;
        }

        if(listener != null){
            listener.onLapSaved();
        }
    }

    public boolean isTimerRunning(){
        return mTimer != null;
    }

    public void handleResetAction(){
        onStop();
        mCurrentTime = 0;
        Pref.putLong(Pref.PREF_TIME_TRIAL_START_TIME,mCurrentTime);
        TimeTrialRiderInfo.getInstance().deleteAllTimeTrialRiderInfo(mEventId);
        Pref.putString(Pref.PREF_TIME_TRIAL_EVENT_ID, null);

        if(listener != null){
            listener.onTimerReset();
        }
    }

    public void setListener(StopWatchListener listener){
        this.listener = listener;
    }

    public String getCurrentEventId(){
        return mEventId;
    }

    public interface StopWatchListener {
        void onLapSaved();
        void onTimerTick(long ms);
        void onTimerStarted();
        void onTimerStopped();
        void onTimerReset();
    }
}
