package sjbc.l7colwinters.sjbcwinterseries.db;

import java.util.List;

import sjbc.l7colwinters.sjbcwinterseries.model.TimeTrial;

/**
 * Created by L7ColWinters on 12/14/2015.
 */
public interface TimeTrialCrud {

    //create
    void createTTEvent(TimeTrial tt);

    //read
    TimeTrial getTTEvent(String id);
    List<TimeTrial> getAllTT();

    //delete
    void deleteAll();
    boolean deleteEvent(String id);

}
