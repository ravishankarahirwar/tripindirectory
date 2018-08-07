package directory.tripin.com.tripindirectory.NewLookCode.activities.fragments.loadboard;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class YourLoadsFragment extends LoadsListBaseFragment {

    public YourLoadsFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        return databaseReference.child("user-posts")
                .child(getUid());
    }
}
