package directory.tripin.com.tripindirectory.newprofiles.models;

public class CompanyCardPojo {

    private DenormalizerPojo mDetails;
    private Double mBidValue;

    public CompanyCardPojo() {
    }

    public DenormalizerPojo getmDetails() {
        return mDetails;
    }

    public void setmDetails(DenormalizerPojo mDetails) {
        this.mDetails = mDetails;
    }

    public Double getmBidValue() {
        return mBidValue;
    }

    public void setmBidValue(Double mBidValue) {
        this.mBidValue = mBidValue;
    }
}
