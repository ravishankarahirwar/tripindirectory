package directory.tripin.com.tripindirectory.newprofiles.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class LoadPostPojo {

    private String mSourceCity;
    private String mDestinationCity;
    private String mSourceHub ;
    private String mDestinationHub;

    private String mVehicleType;
    private String mBodyType;

    private String mPayload;
    private String mPayloadUnit;
    private String mVehichleLenght;
    private String mVehichleLenghtUnit;

    private String mMaterial;
    private String mRemark;

    private String mDisplayName;
    private String mCompanyName;
    private String mUid;
    private String mFuid;
    private String mRmn;
    private String mPhotoUrl;

    private Long mNumViews;

    @ServerTimestamp
    private Date mTimeStamp;

    public LoadPostPojo() {
    }

    public LoadPostPojo(String mSourceCity, String mDestinationCity, String mSourceHub, String mDestinationHub, String mVehicleType, String mBodyType, String mPayload, String mPayloadUnit, String mVehichleLenght, String mVehichleLenghtUnit, String mMaterial, String mRemark, String mDisplayName, String mCompanyName, String mUid, String mFuid, String mRmn, String mPhotoUrl, Long mNumViews) {
        this.mSourceCity = mSourceCity;
        this.mDestinationCity = mDestinationCity;
        this.mSourceHub = mSourceHub;
        this.mDestinationHub = mDestinationHub;
        this.mVehicleType = mVehicleType;
        this.mBodyType = mBodyType;
        this.mPayload = mPayload;
        this.mPayloadUnit = mPayloadUnit;
        this.mVehichleLenght = mVehichleLenght;
        this.mVehichleLenghtUnit = mVehichleLenghtUnit;
        this.mMaterial = mMaterial;
        this.mRemark = mRemark;
        this.mDisplayName = mDisplayName;
        this.mCompanyName = mCompanyName;
        this.mUid = mUid;
        this.mFuid = mFuid;
        this.mRmn = mRmn;
        this.mPhotoUrl = mPhotoUrl;
        this.mNumViews = mNumViews;
    }


    public Long getmNumViews() {
        return mNumViews;
    }

    public void setmNumViews(Long mNumViews) {
        this.mNumViews = mNumViews;
    }

    public String getmSourceCity() {
        return mSourceCity;
    }

    public void setmSourceCity(String mSourceCity) {
        this.mSourceCity = mSourceCity;
    }

    public String getmDestinationCity() {
        return mDestinationCity;
    }

    public void setmDestinationCity(String mDestinationCity) {
        this.mDestinationCity = mDestinationCity;
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


    public String getmPayloadUnit() {
        return mPayloadUnit;
    }

    public void setmPayloadUnit(String mPayloadUnit) {
        this.mPayloadUnit = mPayloadUnit;
    }

    public String getmPayload() {
        return mPayload;
    }

    public void setmPayload(String mPayload) {
        this.mPayload = mPayload;
    }

    public String getmVehichleLenght() {
        return mVehichleLenght;
    }

    public void setmVehichleLenght(String mVehichleLenght) {
        this.mVehichleLenght = mVehichleLenght;
    }

    public String getmVehichleLenghtUnit() {
        return mVehichleLenghtUnit;
    }

    public void setmVehichleLenghtUnit(String mVehichleLenghtUnit) {
        this.mVehichleLenghtUnit = mVehichleLenghtUnit;
    }

    public String getmMaterial() {
        return mMaterial;
    }

    public void setmMaterial(String mMaterial) {
        this.mMaterial = mMaterial;
    }

    public String getmRemark() {
        return mRemark;
    }

    public void setmRemark(String mRemark) {
        this.mRemark = mRemark;
    }

    public String getmDisplayName() {
        return mDisplayName;
    }

    public void setmDisplayName(String mDisplayName) {
        this.mDisplayName = mDisplayName;
    }

    public String getmCompanyName() {
        return mCompanyName;
    }

    public void setmCompanyName(String mCompanyName) {
        this.mCompanyName = mCompanyName;
    }

    public String getmUid() {
        return mUid;
    }

    public void setmUid(String mUid) {
        this.mUid = mUid;
    }

    public String getmFuid() {
        return mFuid;
    }

    public void setmFuid(String mFuid) {
        this.mFuid = mFuid;
    }

    public String getmRmn() {
        return mRmn;
    }

    public void setmRmn(String mRmn) {
        this.mRmn = mRmn;
    }

    public String getmPhotoUrl() {
        return mPhotoUrl;
    }

    public void setmPhotoUrl(String mPhotoUrl) {
        this.mPhotoUrl = mPhotoUrl;
    }

    public Date getmTimeStamp() {
        return mTimeStamp;
    }

    public void setmTimeStamp(Date mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }

    public String toString(){

        return  " Source =" + getmSourceCity() + "\n" +
                " Destination = " + getmDestinationCity() + "\n" +
                " Material = " + getmMaterial() + "\n" +
                " Date = " + getmTimeStamp() + "\n" +
                " TruckType = " + getmVehicleType() + "\n" +
                " TruckBodyType = " + getmBodyType() + "\n" +
                " TruckLength = " + getmVehichleLenght()+getmVehichleLenghtUnit() + "\n" +
                " Payload = " + getmPayload()+getmPayloadUnit() + "\n" +
                " Remark = " + getmRemark() + "\n"  +
                " Share By: Indian Logistics Network \n" + "http://bit.ly/ILNAPPS";


    }
}
