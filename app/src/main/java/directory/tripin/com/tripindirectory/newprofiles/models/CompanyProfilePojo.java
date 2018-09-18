package directory.tripin.com.tripindirectory.newprofiles.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class CompanyProfilePojo {
    private String mProfileData;
    private List<String> mFleets;
    private Double mBid;
    private Double mLastActive;
    private Boolean isActive = false;
    private Double mRatings;

    @ServerTimestamp
    private Date mTimeStamp;

    public CompanyProfilePojo() {
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public List<String> getmFleets() {
        return mFleets;
    }

    public void setmFleets(List<String> mFleets) {
        this.mFleets = mFleets;
    }

    public String getmProfileData() {
        return mProfileData;
    }

    public void setmProfileData(String mProfileData) {
        this.mProfileData = mProfileData;
    }

    public Double getmLastActive() {
        return mLastActive;
    }

    public void setmLastActive(Double mLastActive) {
        this.mLastActive = mLastActive;
    }

    public Double getmBid() {
        return mBid;
    }

    public void setmBid(Double mBid) {
        this.mBid = mBid;
    }

    public Double getmRatings() {
        return mRatings;
    }

    public void setmRatings(Double mRatings) {
        this.mRatings = mRatings;
    }

    public Date getmTimeStamp() {
        return mTimeStamp;
    }

    public void setmTimeStamp(Date mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }
}

