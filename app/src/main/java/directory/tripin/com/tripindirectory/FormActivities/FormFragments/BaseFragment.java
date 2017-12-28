package directory.tripin.com.tripindirectory.FormActivities.FormFragments;

import android.content.Intent;
import android.support.v4.app.Fragment;

import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;

/**
 * Created by Yogesh N. Tikam on 12/19/2017.
 */

public abstract class BaseFragment extends Fragment {
    protected boolean isDataFatched = false;

    abstract public void onUpdate(PartnerInfoPojo partnerInfoPojo);
}
