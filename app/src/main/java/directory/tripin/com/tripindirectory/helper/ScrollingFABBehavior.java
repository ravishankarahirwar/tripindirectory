package directory.tripin.com.tripindirectory.helper;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;

import directory.tripin.com.tripindirectory.R;

/**
 * Created by Shubham on 2/18/2018.
 */
public class ScrollingFABBehavior extends CoordinatorLayout.Behavior<com.github.clans.fab.FloatingActionMenu> {
    private int toolbarHeight;

    public ScrollingFABBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.toolbarHeight =getToolbarHeight(context);
        Logger.v("toolbar height: "+toolbarHeight);
    }

    public ScrollingFABBehavior() {
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, com.github.clans.fab.FloatingActionMenu fab, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, com.github.clans.fab.FloatingActionMenu fab, View dependency) {
        if (dependency instanceof AppBarLayout) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            int fabBottomMargin = lp.rightMargin;
            int distanceToScroll = (fab.getWidth())+ fabBottomMargin;
            float ratio = dependency.getY() /(float)toolbarHeight;
            Logger.v("deendance.Y:"+dependency.getY());
            fab.setTranslationY(-distanceToScroll * ratio);
        }
        return true;
    }

    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, -1);
        styledAttributes.recycle();

        return toolbarHeight;
    }

    public static int getTabsHeight(Context context) {
        return (int) context.getResources().getDimension(R.dimen.app_bar_height);
    }
}
