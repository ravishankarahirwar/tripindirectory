package directory.tripin.com.tripindirectory.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import directory.tripin.com.tripindirectory.model.response.Vehicle;

/**
 * Created by Shubham on 12/12/2017.
 */

public class PartnerInfoPojo {

    private String mCompanyName;
    private String mEmailId;
    private int mLikes;
    private int mDislikes;
    private String mRMN;


    private boolean isVerified = false;

    private List<String> mCompanyLandLineNumbers;
    private List<String> mImagesUrl;

    private List<ContactPersonPojo> mContactPersonsList;

    private List<Vehicle> vehicles;

    private CompanyAddressPojo mCompanyAdderss;

    private Map<String,Boolean> mSourceCities;
    private Map<String,Boolean> mDestinationCities;
    private Map<String,Boolean> mNatureOfBusiness;
    private Map<String,Boolean> mTypesOfServices;




    public PartnerInfoPojo() {
    }

    public PartnerInfoPojo(String mCompanyName, List<ContactPersonPojo> mContactPersonsList, List<String> companyLandLineNumbers, CompanyAddressPojo mCompanyAdderss, List<String> mImagesUrl, boolean isVerified, Map<String, Boolean> mSourceCities, Map<String, Boolean> mDestinationCities) {
        this.mCompanyName = mCompanyName;
        this.mContactPersonsList = mContactPersonsList;
        this.mCompanyLandLineNumbers = companyLandLineNumbers;
        this.mCompanyAdderss = mCompanyAdderss;
        this.mImagesUrl = mImagesUrl;
        this.isVerified = isVerified;
        this.mSourceCities = mSourceCities;
        this.mDestinationCities = mDestinationCities;
    }

    public String getmRMN() {
        return mRMN;
    }

    public void setmRMN(String mRMN) {
        this.mRMN = mRMN;
    }

    public int getmLikes() {
        return mLikes;
    }

    public void setmLikes(int mLikes) {
        this.mLikes = mLikes;
    }

    public int getmDislikes() {
        return mDislikes;
    }

    public void setmDislikes(int mDislikes) {
        this.mDislikes = mDislikes;
    }

    public String getmEmailId() {
        return mEmailId;
    }

    public void setmEmailId(String mEmailId) {
        this.mEmailId = mEmailId;
    }

    public Map<String, Boolean> getmNatureOfBusiness() {
        return mNatureOfBusiness;
    }

    public void setmNatureOfBusiness(Map<String, Boolean> mNatureOfBusiness) {
        this.mNatureOfBusiness = mNatureOfBusiness;
    }

    public Map<String, Boolean> getmTypesOfServices() {
        return mTypesOfServices;
    }

    public void setmTypesOfServices(Map<String, Boolean> mTypesOfServices) {
        this.mTypesOfServices = mTypesOfServices;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public Map<String, Boolean> getmSourceCities() {
        return mSourceCities;
    }

    public void setmSourceCities(Map<String, Boolean> mSourceCities) {
        this.mSourceCities = mSourceCities;
    }

    public Map<String, Boolean> getmDestinationCities() {
        return mDestinationCities;
    }

    public void setmDestinationCities(Map<String, Boolean> mDestinationCities) {
        this.mDestinationCities = mDestinationCities;
    }

    public List<String> getImagesUrl() {
        return mImagesUrl;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public void setImagesUrl(List<String> mImagesUrl) {
        this.mImagesUrl = mImagesUrl;
    }

    public String getmCompanyName() {
        return mCompanyName;
    }

    public void setCompanyName(String mCompanyName) {
        this.mCompanyName = mCompanyName;
    }

    public List<ContactPersonPojo> getmContactPersonsList() {
        return mContactPersonsList;
    }

    public void setContactPersonsList(List<ContactPersonPojo> mContactPersonsList) {
        this.mContactPersonsList = mContactPersonsList;
    }

    public List<String> getmCompanyLandLineNumbers() {
        return mCompanyLandLineNumbers;
    }

    public void setmCompanyLandLineNumbers(List<String> mCompanyLandLineNumbers) {
        this.mCompanyLandLineNumbers = mCompanyLandLineNumbers;
    }

    public CompanyAddressPojo getmCompanyAdderss() {
        return mCompanyAdderss;
    }

    public void setCompanyAdderss(CompanyAddressPojo mCompanyAdderss) {
        this.mCompanyAdderss = mCompanyAdderss;
    }
}
