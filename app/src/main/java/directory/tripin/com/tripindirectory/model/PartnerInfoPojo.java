package directory.tripin.com.tripindirectory.model;

import java.util.List;
import java.util.Map;

import directory.tripin.com.tripindirectory.model.response.Vehicle;

/**
 * Created by Shubham on 12/12/2017.
 */

public class PartnerInfoPojo {

    private String mCompanyName;
    private String natureOfBusiness;
    private String natureEmail;
    private String natureWebsite;

    private boolean isVerified = false;

    private List<String> mCompanyLandLineNumbers;
    private List<String> mImagesUrl;

    private List<ContactPersonPojo> mContactPersonsList;

    private List<Vehicle> vehicles;

    private CompanyAddressPojo mCompanyAdderss;

    private Map<String,Boolean> mSourceCities;
    private Map<String,Boolean> mDestinationCities;

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

    public String getNatureOfBusiness() {
        return natureOfBusiness;
    }

    public void setNatureOfBusiness(String natureOfBusiness) {
        this.natureOfBusiness = natureOfBusiness;
    }

    public String getNatureEmail() {
        return natureEmail;
    }

    public void setNatureEmail(String natureEmail) {
        this.natureEmail = natureEmail;
    }

    public String getNatureWebsite() {
        return natureWebsite;
    }

    public void setNatureWebsite(String natureWebsite) {
        this.natureWebsite = natureWebsite;
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

    public Map<String, Boolean> getDestinationCities() {
        return mDestinationCities;
    }

    public void setDestinationCities(Map<String, Boolean> mDestinationCities) {
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
