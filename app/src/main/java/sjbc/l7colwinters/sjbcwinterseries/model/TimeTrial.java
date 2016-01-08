package sjbc.l7colwinters.sjbcwinterseries.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by L7ColWinters on 12/14/2015.
 */
public class TimeTrial {

    public String eventId;
    public long date;

    //if I make it to the day most of the time, everyone will have the same event Id, If I try to do it at the
    //hour level, we could screw up if say @ 9am we started the timers.
    private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("MM.dd.yyyy");

    public TimeTrial(){
        eventId = mDateFormat.format(new Date().getTime());
        date = System.currentTimeMillis();
    }

}
