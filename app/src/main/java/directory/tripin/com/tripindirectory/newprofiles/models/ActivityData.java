package directory.tripin.com.tripindirectory.newprofiles.models;

import java.util.Date;

public class ActivityData{
    private Double mBid;
    private Date mLastActive;
    private Double mRatings;

    public ActivityData(Double mBid, Date mLastActive, Double mRatings) {
        this.mBid = mBid;
        this.mLastActive = mLastActive;
        this.mRatings = mRatings;
    }

    public ActivityData() {
    }

    public Double getmBid() {
        return mBid;
    }

    public void setmBid(Double mBid) {
        this.mBid = mBid;
    }

    public Date getmLastActive() {
        return mLastActive;
    }

    public void setmLastActive(Date mLastActive) {
        this.mLastActive = mLastActive;
    }

    public Double getmRatings() {
        return mRatings;
    }

    public void setmRatings(Double mRatings) {
        this.mRatings = mRatings;
    }
}
