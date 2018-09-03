package directory.tripin.com.tripindirectory.newlookcode.activities.fragments.loadboard.models;



import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Post {

    public String mUid;
    public String mFuid;
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
    public String mContactNo;
    public String mRemark;
    public String mPhotoUrl;


    public int commentCount = 0;

    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    /**
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
    public Post(String fuid, String uid, String author, int findOrPost, String source, String destination, String meterial, String date, String truckType, String bodyType, String truckLength, String payload, String remark, String contactNo, String mPhotoUrl) {
        this.mFuid = fuid;
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
        this.mContactNo = contactNo;
        this.mPhotoUrl = mPhotoUrl;
    }

    public String getmPhotoUrl() {
        return mPhotoUrl;
    }

    public void setmPhotoUrl(String mPhotoUrl) {
        this.mPhotoUrl = mPhotoUrl;
    }

    public String getmFuid() {
        return mFuid;
    }

    public void setmFuid(String mFuid) {
        this.mFuid = mFuid;
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


    public String getmContactNo() {
        return mContactNo;
    }

    public void setmContactNo(String mContactNo) {
        this.mContactNo = mContactNo;
    }


    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("mUid", mUid);
        result.put("mFuid", mFuid);
        result.put("mPhotoUrl", mPhotoUrl);

        result.put("mAuthor", mAuthor);
        result.put("mFindOrPost", mFindOrPost);

        result.put("mSource", mSource);
        result.put("mDestination", mDestination);
        result.put("mMeterial", mMeterial);
        result.put("mDate", mDate);
        result.put("mTruckType", mTruckType);
        result.put("mTruckBodyType", mTruckBodyType);
        result.put("mTruckLength", mTruckLength);
        result.put("mPayload", mPayload);
        result.put("mContactNo", mContactNo);
        result.put("mRemark", mRemark);

        result.put("commentCount", commentCount);
        result.put("starCount", starCount);
        result.put("stars", stars);

        return result;
    }
    // [END post_to_map]


    @Override
    public String toString() {
        return  " Source =" + mSource + "\n" +
                " Destination = " + mDestination + "\n" +
                " Material = " + mMeterial + "\n" +
                " Date = " + mDate + "\n" +
                " TruckType = " + mTruckType + "\n" +
                " TruckBodyType = " + mTruckBodyType + "\n" +
                " TruckLength = " + mTruckLength + "\n" +
                " Payload = " + mPayload + "\n" +
                " Remark = " + mRemark + "\n"  +
                " Share By: Indian Logistics Network \n" + "http://bit.ly/ILNAPPS";
    }
}
// [END post_class]
