package directory.tripin.com.tripindirectory.model;

/**
 * Created by Shubham on 12/12/2017.
 */

public class ContactPersonPojo {

    private String mContactPresonName;
    private String getmContactPersonMobile;

    public ContactPersonPojo() {
    }

    public ContactPersonPojo(String mContactPresonName, String getmContactPersonMobile) {
        this.mContactPresonName = mContactPresonName;
        this.getmContactPersonMobile = getmContactPersonMobile;
    }

    public String getmContactPresonName() {
        return mContactPresonName;
    }

    public void setmContactPresonName(String mContactPresonName) {
        this.mContactPresonName = mContactPresonName;
    }

    public String getGetmContactPersonMobile() {
        return getmContactPersonMobile;
    }

    public void setGetmContactPersonMobile(String getmContactPersonMobile) {
        this.getmContactPersonMobile = getmContactPersonMobile;
    }
}
