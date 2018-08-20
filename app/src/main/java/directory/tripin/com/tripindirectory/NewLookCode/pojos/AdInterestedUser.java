package directory.tripin.com.tripindirectory.NewLookCode.pojos;

public class AdInterestedUser {

    private String mAdname;
    private String mUserRMN;
    private String mUserName = "";
    private String mUserEmail = "";

    public AdInterestedUser() {
    }

    public AdInterestedUser(String mAdname, String mUserRMN, String mUserName, String mUserEmail) {
        this.mAdname = mAdname;
        this.mUserRMN = mUserRMN;
        this.mUserName = mUserName;
        this.mUserEmail = mUserEmail;
    }

    public String getmAdname() {
        return mAdname;
    }

    public void setmAdname(String mAdname) {
        this.mAdname = mAdname;
    }

    public String getmUserRMN() {
        return mUserRMN;
    }

    public void setmUserRMN(String mUserRMN) {
        this.mUserRMN = mUserRMN;
    }

    public String getmUserName() {
        return mUserName;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getmUserEmail() {
        return mUserEmail;
    }

    public void setmUserEmail(String mUserEmail) {
        this.mUserEmail = mUserEmail;
    }
}
