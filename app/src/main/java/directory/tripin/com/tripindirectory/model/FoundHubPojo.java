package directory.tripin.com.tripindirectory.model;

/**
 * Created by Shubham on 2/3/2018.
 */

public class FoundHubPojo {
    private String mHubName;
    private Double mHubDistanceFromCenter;

    public FoundHubPojo(String mHubName, double mHubDistanceFromCenter) {
        this.mHubName = mHubName;
        this.mHubDistanceFromCenter = mHubDistanceFromCenter;
    }

    public String getmHubName() {
        return mHubName;
    }

    public void setmHubName(String mHubName) {
        this.mHubName = mHubName;
    }

    public Double getmHubDistanceFromCenter() {
        return mHubDistanceFromCenter;
    }

    public void setmHubDistanceFromCenter(Double mHubDistanceFromCenter) {
        this.mHubDistanceFromCenter = mHubDistanceFromCenter;
    }
}
