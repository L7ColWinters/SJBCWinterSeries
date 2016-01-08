package sjbc.l7colwinters.sjbcwinterseries.custom.views.behaviors;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by L7ColWinters on 1/3/2016.
 */
public class DefaultFragmentWithToolbarBehavior extends CoordinatorLayout.Behavior<FrameLayout> {

    public DefaultFragmentWithToolbarBehavior(Context context, AttributeSet attrs) {}

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FrameLayout child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FrameLayout child, View dependency) {
        child.setTranslationY(dependency.getHeight());
        return true;
    }


}
