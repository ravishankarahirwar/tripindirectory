package directory.tripin.com.tripindirectory.LoadBoardActivities.models;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Shubham on 2/18/2018.
 */

public class FleetPostPojo {

    private String mSourceCity = "";
    private GeoPoint mSourceCityLatLang;
    private String mDestinationCity = "";
    private GeoPoint mDestinationCityLatLang;

    private Date mPickUpTimeStamp;

    private String mVehicleType = "";
    private String mBodyType = "";
    private String mFleetLength = "";
    private String mFleetPayLoad = "";
    private String mPersonalNote = "";
    private String mVehicleNumber = "";



    private String mSourceHub = "";
    private String mDestinationHub ="";
    private String mEstimatedDistance = "";
    private String mEstimatedTime;
    private String mEstimatedCost;


    private String mCompanyName = "";
    private String mRMN = "";
    private String mPostersUid = "";
    private String mFcmToken = "";
    private List<String> mImagesUrl;


    @ServerTimestamp
    private Date mTimeStamp;



    private HashMap<String,Boolean> mIntrestedPeopleList;
    private HashMap<String,Boolean> mSharedPeopleList;
    private HashMap<String,Boolean> mInboxedPeopleList;
    private HashMap<String,Boolean> mCalledPeopleList;

    public FleetPostPojo() {
    }

    public String getmVehicleNumber() {
        return mVehicleNumber;
    }

    public void setmVehicleNumber(String mVehicleNumber) {
        this.mVehicleNumber = mVehicleNumber;
    }

    public String getmSourceCity() {
        return mSourceCity;
    }

    public void setmSourceCity(String mSourceCity) {
        this.mSourceCity = mSourceCity;
    }

    public GeoPoint getmSourceCityLatLang() {
        return mSourceCityLatLang;
    }

    public void setmSourceCityLatLang(GeoPoint mSourceCityLatLang) {
        this.mSourceCityLatLang = mSourceCityLatLang;
    }

    public String getmDestinationCity() {
        return mDestinationCity;
    }

    public void setmDestinationCity(String mDestinationCity) {
        this.mDestinationCity = mDestinationCity;
    }

    public GeoPoint getmDestinationCityLatLang() {
        return mDestinationCityLatLang;
    }

    public void setmDestinationCityLatLang(GeoPoint mDestinationCityLatLang) {
        this.mDestinationCityLatLang = mDestinationCityLatLang;
    }

    public Date getmPickUpTimeStamp() {
        return mPickUpTimeStamp;
    }

    public void setmPickUpTimeStamp(Date mPickUpTimeStamp) {
        this.mPickUpTimeStamp = mPickUpTimeStamp;
    }

    public String getmVehicleType() {
        return mVehicleType;
    }

    public void setmVehicleType(String mVehicleType) {
        this.mVehicleType = mVehicleType;
    }

    public String getmBodyType() {
        return mBodyType;
    }

    public void setmBodyType(String mBodyType) {
        this.mBodyType = mBodyType;
    }

    public String getmFleetLength() {
        return mFleetLength;
    }

    public void setmFleetLength(String mFleetLength) {
        this.mFleetLength = mFleetLength;
    }

    public String getmFleetPayLoad() {
        return mFleetPayLoad;
    }

    public void setmFleetPayLoad(String mFleetPayLoad) {
        this.mFleetPayLoad = mFleetPayLoad;
    }

    public String getmPersonalNote() {
        return mPersonalNote;
    }

    public void setmPersonalNote(String mPersonalNote) {
        this.mPersonalNote = mPersonalNote;
    }

    public String getmSourceHub() {
        return mSourceHub;
    }

    public void setmSourceHub(String mSourceHub) {
        this.mSourceHub = mSourceHub;
    }

    public String getmDestinationHub() {
        return mDestinationHub;
    }

    public void setmDestinationHub(String mDestinationHub) {
        this.mDestinationHub = mDestinationHub;
    }

    public String getmEstimatedDistance() {
        return mEstimatedDistance;
    }

    public void setmEstimatedDistance(String mEstimatedDistance) {
        this.mEstimatedDistance = mEstimatedDistance;
    }

    public String getmEstimatedTime() {
        return mEstimatedTime;
    }

    public void setmEstimatedTime(String mEstimatedTime) {
        this.mEstimatedTime = mEstimatedTime;
    }

    public String getmEstimatedCost() {
        return mEstimatedCost;
    }

    public void setmEstimatedCost(String mEstimatedCost) {
        this.mEstimatedCost = mEstimatedCost;
    }

    public String getmCompanyName() {
        return mCompanyName;
    }

    public void setmCompanyName(String mCompanyName) {
        this.mCompanyName = mCompanyName;
    }

    public String getmRMN() {
        return mRMN;
    }

    public void setmRMN(String mRMN) {
        this.mRMN = mRMN;
    }

    public String getmPostersUid() {
        return mPostersUid;
    }

    public void setmPostersUid(String mPostersUid) {
        this.mPostersUid = mPostersUid;
    }

    public String getmFcmToken() {
        return mFcmToken;
    }

    public void setmFcmToken(String mFcmToken) {
        this.mFcmToken = mFcmToken;
    }

    public List<String> getmImagesUrl() {
        return mImagesUrl;
    }

    public void setmImagesUrl(List<String> mImagesUrl) {
        this.mImagesUrl = mImagesUrl;
    }

    public Date getmTimeStamp() {
        return mTimeStamp;
    }

    public void setmTimeStamp(Date mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }

    public HashMap<String, Boolean> getmIntrestedPeopleList() {
        return mIntrestedPeopleList;
    }

    public void setmIntrestedPeopleList(HashMap<String, Boolean> mIntrestedPeopleList) {
        this.mIntrestedPeopleList = mIntrestedPeopleList;
    }

    public HashMap<String, Boolean> getmSharedPeopleList() {
        return mSharedPeopleList;
    }

    public void setmSharedPeopleList(HashMap<String, Boolean> mSharedPeopleList) {
        this.mSharedPeopleList = mSharedPeopleList;
    }

    public HashMap<String, Boolean> getmInboxedPeopleList() {
        return mInboxedPeopleList;
    }

    public void setmInboxedPeopleList(HashMap<String, Boolean> mInboxedPeopleList) {
        this.mInboxedPeopleList = mInboxedPeopleList;
    }

    public HashMap<String, Boolean> getmCalledPeopleList() {
        return mCalledPeopleList;
    }

    public void setmCalledPeopleList(HashMap<String, Boolean> mCalledPeopleList) {
        this.mCalledPeopleList = mCalledPeopleList;
    }

    public boolean isMinimumRequiredInputsSet() {
        if (mFleetLength.isEmpty()
                || mFleetPayLoad.isEmpty()
                ||mVehicleType.isEmpty()
                ||mVehicleNumber.isEmpty()
                ||mBodyType.isEmpty()
                || mSourceCity.isEmpty()
                || mDestinationCity.isEmpty()
                || mPickUpTimeStamp==null) {
            return false;
        } else {
            return true;
        }
    }
}
