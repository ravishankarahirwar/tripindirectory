package directory.tripin.com.tripindirectory.LoadBoardActivities.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import directory.tripin.com.tripindirectory.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyPostsFragment extends Fragment {


    public MyPostsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_posts, container, false);
    }

}
