package directory.tripin.com.tripindirectory.forum.models;



import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Post {

    public String mUid;
    public String mAuthor;
    public String mFindOrPost;

    public String mSource;
    public String mDestination;
    public String mMeterial;
    public String mDate;
    public String mTruckType;
    public String mTruckLength;
    public String mPayload;
    public String mRemark;


    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String uid, String author, String findOrPost, String source, String destination, String meterial, String date, String truckType, String truckLength, String payload, String remark) {
        this.mUid = uid;
        this.mAuthor = author;
        this.mFindOrPost = findOrPost;
        this.mSource = source;
        this.mDestination = destination;
        this.mMeterial = meterial;
        this.mDate = date;
        this.mTruckType = truckType;
        this.mTruckLength = truckLength;
        this.mPayload = payload;
        this.mRemark = remark;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", mUid);
        result.put("author", mAuthor);
        result.put("title", mFindOrPost);

        result.put("source", mSource);
        result.put("destination", mDestination);
        result.put("meterial", mMeterial);
        result.put("date", mDate);
        result.put("truckType", mTruckType);
        result.put("truckLength", mTruckLength);
        result.put("payload", mPayload);
        result.put("remark", mRemark);

        result.put("starCount", starCount);
        result.put("stars", stars);

        return result;
    }
    // [END post_to_map]

}
// [END post_class]
