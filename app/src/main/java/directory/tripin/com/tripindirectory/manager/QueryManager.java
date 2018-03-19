package directory.tripin.com.tripindirectory.manager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import directory.tripin.com.tripindirectory.model.QueryBookmarkPojo;
import directory.tripin.com.tripindirectory.utils.DB;

/**
 * @author Ravishankar
 * @version 1.0
 * @since 18-03-2018
 */

public class QueryManager {
    public Query getBookMarkQuery(String userId) {
        Query queryBookmark = FirebaseFirestore
                .getInstance()
                .collection(DB.Collection.PARTNER)
                .document(userId)
                .collection(DB.Collection.QUERYBOOKMARKS).orderBy(DB.QueryBookmarkFields.TIMESTAMP, Query.Direction.DESCENDING);
        return queryBookmark;
    }

    public FirestoreRecyclerOptions<QueryBookmarkPojo> getBookMarkOptions(String userId) {
        FirestoreRecyclerOptions optionsbookmark = new FirestoreRecyclerOptions.Builder<QueryBookmarkPojo>()
                .setQuery(getBookMarkQuery(userId), QueryBookmarkPojo.class)
                .build();

        return optionsbookmark;
    }
}
