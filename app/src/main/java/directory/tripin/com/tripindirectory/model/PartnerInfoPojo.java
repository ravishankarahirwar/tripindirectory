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

        //nature of business
        mNatureOfBusiness = new HashMap<>();


        mTypesOfServices = new HashMap<>();
        mTypesOfServices.put("FTL",false);
        mTypesOfServices.put("Part Loads",false);
        mTypesOfServices.put("Parcel",false);
        mTypesOfServices.put("ODC",false);
        mTypesOfServices.put("Import Containers",false);
        mTypesOfServices.put("Export Containers",false);
        mTypesOfServices.put("Chemical",false);
        mTypesOfServices.put("Petrol",false);
        mTypesOfServices.put("Diesel",false);
        mTypesOfServices.put("Oil",false);


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
