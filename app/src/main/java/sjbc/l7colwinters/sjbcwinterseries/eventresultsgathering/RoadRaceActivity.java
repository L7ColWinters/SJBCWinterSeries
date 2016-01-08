package sjbc.l7colwinters.sjbcwinterseries.eventresultsgathering;

import android.os.Bundle;

import sjbc.l7colwinters.sjbcwinterseries.R;

/**
 * Created by L7ColWinters on 12/21/2015.
 */
public class RoadRaceActivity extends BaseWinterSeriesActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        navigationView.getMenu().removeItem(R.id.nav_road);
    }
}
