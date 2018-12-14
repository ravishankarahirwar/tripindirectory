package directory.tripin.com.tripindirectory.model;

/**
 * Created by Shubham on 12/12/2017.
 */

public class CompanyAddressPojo {

    private String address;
    private String city;
    private String state;
    private String pincode;
    private String mLatitude;
    private String mLongitude;
    private boolean isLatLongSet = false;

    public CompanyAddressPojo() {
    }

    public CompanyAddressPojo(String address, String city, String state) {
        this.address = address;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
    }

    public CompanyAddressPojo(String address, String city, String state, String pincode) {
        this.address = address;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
    }

    public boolean isLatLongSet() {
        return isLatLongSet;
    }

    public void setLatLongSet(boolean latLongSet) {
        isLatLongSet = latLongSet;
    }

    public String getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(String mLatitude) {
        this.mLatitude = mLatitude;
    }

    public String getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(String mLongitude) {
        this.mLongitude = mLongitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }
}