package directory.tripin.com.tripindirectory.newlookcode.pojos;

public class ContactPojo {

    private String mContactNumber;
    private String mContactName;
    private String mContactEmail;
    private String mProviderName;
    private String mProviderRMN;
    private Boolean isInvited = false;
    private Boolean isAlreadyonILN = false;
    private String mTimesContacted;
    private String mWebsite;
    private String mRelation;
    private String mIndustry;


    public ContactPojo(String mContactNumber, String mContactName, String mContactEmail, String mProviderName, String mProviderRMN, Boolean isInvited,
                       Boolean isAlreadyonILN,
                       String mTimesContacted,
                       String mWebsite,
                       String mRelation,
                       String mIndustry) {
        this.mContactNumber = mContactNumber;
        this.mContactName = mContactName;
        this.mContactEmail = mContactEmail;
        this.mProviderName = mProviderName;
        this.mProviderRMN = mProviderRMN;
        this.isInvited = isInvited;
        this.isAlreadyonILN = isAlreadyonILN;
        this.mTimesContacted = mTimesContacted;
        this.mWebsite = mWebsite;
        this.mRelation = mRelation;
        this.mIndustry = mIndustry;
    }

    public ContactPojo() {
    }

    public String getmIndustry() {
        return mIndustry;
    }

    public void setmIndustry(String mIndustry) {
        this.mIndustry = mIndustry;
    }

    public String getmContactNumber() {
        return mContactNumber;
    }

    public String getmRelation() {
        return mRelation;
    }

    public void setmRelation(String mRelation) {
        this.mRelation = mRelation;
    }

    public String getmWebsite() {
        return mWebsite;
    }

    public void setmWebsite(String mWebsite) {
        this.mWebsite = mWebsite;
    }

    public void setmContactNumber(String mContactNumber) {
        this.mContactNumber = mContactNumber;
    }

    public String getmContactName() {
        return mContactName;
    }

    public void setmContactName(String mContactName) {
        this.mContactName = mContactName;
    }

    public String getmProviderName() {
        return mProviderName;
    }

    public void setmProviderName(String mProviderName) {
        this.mProviderName = mProviderName;
    }

    public String getmProviderRMN() {
        return mProviderRMN;
    }

    public void setmProviderRMN(String mProviderRMN) {
        this.mProviderRMN = mProviderRMN;
    }

    public Boolean getInvited() {
        return isInvited;
    }

    public void setInvited(Boolean invited) {
        isInvited = invited;
    }

    public Boolean getAlreadyonILN() {
        return isAlreadyonILN;
    }

    public void setAlreadyonILN(Boolean alreadyonILN) {
        isAlreadyonILN = alreadyonILN;
    }

    public String getmContactEmail() {
        return mContactEmail;
    }

    public void setmContactEmail(String mContactEmail) {
        this.mContactEmail = mContactEmail;
    }

    public String getmTimesContacted() {
        return mTimesContacted;
    }

    public void setmTimesContacted(String mTimesContacted) {
        this.mTimesContacted = mTimesContacted;
    }
}
