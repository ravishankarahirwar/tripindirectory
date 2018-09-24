package directory.tripin.com.tripindirectory.newprofiles.models;

import java.util.List;
import java.util.Map;

public class DenormalizerPojo {

    private String mCompanyName;
    private String mDisplayName;
    private String mRMN;
    private String mUID;
    private String mFUID;
    private String mPhotoUrl;
    private String mLocationCity;
    private String mFcmToken;

    private List<String> mFleetsSort;
    private Map<String,Boolean> mOperationHubs;

    private Double mLastActive;
    public Boolean isActive;
    private Double mAvgRating;
    private Double mNumRatings;


    public DenormalizerPojo(String mCompanyName, String mDisplayName, String mRMN, String mUID, String mFUID, String mPhotoUrl, String mLocationCity, String mFcmToken, List<String> mFleetsSort, Map<String, Boolean> mOperationHubs, Double mLastActive, Boolean isActive, Double mAvgRating, Double mNumRatings) {
        this.mCompanyName = mCompanyName;
        this.mDisplayName = mDisplayName;
        this.mRMN = mRMN;
        this.mUID = mUID;
        this.mFUID = mFUID;
        this.mPhotoUrl = mPhotoUrl;
        this.mLocationCity = mLocationCity;
        this.mFcmToken = mFcmToken;
        this.mFleetsSort = mFleetsSort;
        this.mOperationHubs = mOperationHubs;
        this.mLastActive = mLastActive;
        this.isActive = isActive;
        this.mAvgRating = mAvgRating;
        this.mNumRatings = mNumRatings;
    }

    public DenormalizerPojo() {
    }

    public String getmFcmToken() {
        return mFcmToken;
    }

    public void setmFcmToken(String mFcmToken) {
        this.mFcmToken = mFcmToken;
    }

    public String getmCompanyName() {
        return mCompanyName;
    }

    public void setmCompanyName(String mCompanyName) {
        this.mCompanyName = mCompanyName;
    }

    public String getmDisplayName() {
        return mDisplayName;
    }

    public void setmDisplayName(String mDisplayName) {
        this.mDisplayName = mDisplayName;
    }

    public String getmRMN() {
        return mRMN;
    }

    public void setmRMN(String mRMN) {
        this.mRMN = mRMN;
    }

    public String getmUID() {
        return mUID;
    }

    public void setmUID(String mUID) {
        this.mUID = mUID;
    }

    public String getmFUID() {
        return mFUID;
    }

    public void setmFUID(String mFUID) {
        this.mFUID = mFUID;
    }

    public String getmPhotoUrl() {
        return mPhotoUrl;
    }

    public void setmPhotoUrl(String mPhotoUrl) {
        this.mPhotoUrl = mPhotoUrl;
    }

    public String getmLocationCity() {
        return mLocationCity;
    }

    public void setmLocationCity(String mLocationCity) {
        this.mLocationCity = mLocationCity;
    }

    public List<String> getmFleetsSort() {
        return mFleetsSort;
    }

    public void setmFleetsSort(List<String> mFleetsSort) {
        this.mFleetsSort = mFleetsSort;
    }

    public Map<String, Boolean> getmOperationHubs() {
        return mOperationHubs;
    }

    public void setmOperationHubs(Map<String, Boolean> mOperationHubs) {
        this.mOperationHubs = mOperationHubs;
    }

    public Double getmLastActive() {
        return mLastActive;
    }

    public void setmLastActive(Double mLastActive) {
        this.mLastActive = mLastActive;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Double getmAvgRating() {
        return mAvgRating;
    }

    public void setmAvgRating(Double mAvgRating) {
        this.mAvgRating = mAvgRating;
    }

    public Double getmNumRatings() {
        return mNumRatings;
    }

    public void setmNumRatings(Double mNumRatings) {
        this.mNumRatings = mNumRatings;
    }

}
