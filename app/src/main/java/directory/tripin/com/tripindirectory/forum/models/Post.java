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
    public int mFindOrPost;

    public String mSource;
    public String mDestination;
    public String mMeterial;
    public String mDate;
    public String mTruckType;
    public String mTruckBodyType;
    public String mTruckLength;
    public String mPayload;
    public String mRemark;

    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    /**
     *
     * @param uid
     * @param author
     * @param findOrPost
     * @param source
     * @param destination
     * @param meterial
     * @param date
     * @param truckType
     * @param truckLength
     * @param payload
     * @param remark
     */
    public Post(String uid, String author, int findOrPost, String source, String destination, String meterial, String date, String truckType, String bodyType, String truckLength, String payload, String remark) {
        this.mUid = uid;
        this.mAuthor = author;
        this.mFindOrPost = findOrPost;
        this.mSource = source;
        this.mDestination = destination;
        this.mMeterial = meterial;
        this.mDate = date;
        this.mTruckType = truckType;
        this.mTruckBodyType = bodyType;
        this.mTruckLength = truckLength;
        this.mPayload = payload;
        this.mRemark = remark;
    }

    public String getmUid() {
        return mUid;
    }

    public void setmUid(String mUid) {
        this.mUid = mUid;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public void setmAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public int getmFindOrPost() {
        return mFindOrPost;
    }

    public void setmFindOrPost(int mFindOrPost) {
        this.mFindOrPost = mFindOrPost;
    }

    public String getmSource() {
        return mSource;
    }

    public void setmSource(String mSource) {
        this.mSource = mSource;
    }

    public String getmDestination() {
        return mDestination;
    }

    public void setmDestination(String mDestination) {
        this.mDestination = mDestination;
    }

    public String getmMeterial() {
        return mMeterial;
    }

    public void setmMeterial(String mMeterial) {
        this.mMeterial = mMeterial;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public String getmTruckType() {
        return mTruckType;
    }

    public void setmTruckType(String mTruckType) {
        this.mTruckType = mTruckType;
    }

    public String getmTruckBodyType() {
        return mTruckBodyType;
    }

    public void setmTruckBodyType(String mTruckBodyType) {
        this.mTruckBodyType = mTruckBodyType;
    }

    public String getmTruckLength() {
        return mTruckLength;
    }

    public void setmTruckLength(String mTruckLength) {
        this.mTruckLength = mTruckLength;
    }

    public String getmPayload() {
        return mPayload;
    }

    public void setmPayload(String mPayload) {
        this.mPayload = mPayload;
    }

    public String getmRemark() {
        return mRemark;
    }

    public void setmRemark(String mRemark) {
        this.mRemark = mRemark;
    }

    public int getStarCount() {
        return starCount;
    }

    public void setStarCount(int starCount) {
        this.starCount = starCount;
    }

    public Map<String, Boolean> getStars() {
        return stars;
    }

    public void setStars(Map<String, Boolean> stars) {
        this.stars = stars;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("mUid", mUid);
        result.put("mAuthor", mAuthor);
        result.put("mFindOrPost", mFindOrPost);

        result.put("mSource", mSource);
        result.put("mDestination", mDestination);
        result.put("mMeterial", mMeterial);
        result.put("mDate", mDate);
        result.put("mTruckType", mTruckType);
        result.put("mTruckLength", mTruckLength);
        result.put("mPayload", mPayload);
        result.put("mRemark", mRemark);

        result.put("starCount", starCount);
        result.put("stars", stars);

        return result;
    }
    // [END post_to_map]

}
// [END post_class]
