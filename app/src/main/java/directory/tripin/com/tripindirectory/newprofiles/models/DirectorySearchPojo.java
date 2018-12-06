package directory.tripin.com.tripindirectory.newprofiles.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

public class DirectorySearchPojo implements Serializable {

    private String mSourceCity;
    private String mDestinationCity;
    private String mSourceHub;
    private String mDestinationHub;

    @ServerTimestamp
    private Date mTimeStamp;

    public DirectorySearchPojo(String mSourceCity, String mDestinationCity, String mSourceHub, String mDestinationHub) {
        this.mSourceCity = mSourceCity;
        this.mDestinationCity = mDestinationCity;
        this.mSourceHub = mSourceHub;
        this.mDestinationHub = mDestinationHub;
    }

    public DirectorySearchPojo() {
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

    public Date getmTimeStamp() {
        return mTimeStamp;
    }

    public void setmTimeStamp(Date mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }
}
