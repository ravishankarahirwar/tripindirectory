package directory.tripin.com.tripindirectory.model;

/**
 * Created by Shubham on 12/12/2017.
 */

public class CompanyAddressPojo {

    private String mAddress;
    private String mCity;
    private String mState;

    public CompanyAddressPojo(String mAddress, String mCity, String mState) {
        this.mAddress = mAddress;
        this.mCity = mCity;
        this.mState = mState;
    }

    public CompanyAddressPojo() {
    }

    public String getmAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public String getmCity() {
        return mCity;
    }

    public void setmCity(String mCity) {
        this.mCity = mCity;
    }

    public String getmState() {
        return mState;
    }

    public void setmState(String mState) {
        this.mState = mState;
    }
}
