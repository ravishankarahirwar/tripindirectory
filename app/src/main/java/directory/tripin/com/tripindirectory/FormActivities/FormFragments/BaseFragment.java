package directory.tripin.com.tripindirectory.FormActivities.FormFragments;

import android.support.v4.app.Fragment;

import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;

/**
 * Created by Yogesh N. Tikam on 12/19/2017.
 */

public abstract class BaseFragment extends Fragment{
    abstract public void onUpdate(PartnerInfoPojo partnerInfoPojo);
}