package sjbc.l7colwinters.sjbcwinterseries.eventresultsgathering;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import sjbc.l7colwinters.sjbcwinterseries.R;

/**
 * Created by L7ColWinters on 12/21/2015.
 */
public class WinterSeriesActivity extends BaseWinterSeriesActivity {

    private Fragment mFragment = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fab.setVisibility(View.GONE);

        navigationView.getMenu().removeItem(R.id.nav_entry);

        mFragment = new HomeFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.fragment_container, mFragment);
        fragmentTransaction.commit();
    }
}
