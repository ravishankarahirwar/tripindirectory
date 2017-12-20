package directory.tripin.com.tripindirectory.FormActivities.FormFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;

/**
 * A simple {@link Fragment} subclass.
 */
public class FleetFormFragment extends BaseFragment {


    public FleetFormFragment() {
        // Required empty public constructor
    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.v("önpausemmmmmmmmmmmmmmmmmmm3");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fleet_form, container, false);
    }
    @Override
    public void onUpdate(PartnerInfoPojo partnerInfoPojo) {

    }

}
