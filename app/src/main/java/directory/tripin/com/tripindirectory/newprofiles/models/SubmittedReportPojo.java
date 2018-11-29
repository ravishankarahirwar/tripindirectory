package directory.tripin.com.tripindirectory.newprofiles.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class SubmittedReportPojo {

    private String mReporterCompName;
    private String mReporterDisplayName;
    private String mReporterRmn;
    private String mReporterUid;
    private String mReporterFuid;

    private String mComment;
    private Double mNumberOfTags;
    private List<String> mReportTags;

    private String mReportedCompName;
    private String mReportedDisplayName;
    private String mReportedUid;
    private String mReportedRmn;
    private String mReportedFuid;

    @ServerTimestamp
    private Date mTimeStamp;


    public SubmittedReportPojo(String mReporterCompName, String mReporterDisplayName, String mReporterRmn, String mReporterUid, String mReporterFuid, String mComment, Double mNumberOfTags, List<String> mReportTags, String mReportedCompName, String mReportedDisplayName, String mReportedUid, String mReportedRmn, String mReportedFuid) {
        this.mReporterCompName = mReporterCompName;
        this.mReporterDisplayName = mReporterDisplayName;
        this.mReporterRmn = mReporterRmn;
        this.mReporterUid = mReporterUid;
        this.mReporterFuid = mReporterFuid;
        this.mComment = mComment;
        this.mNumberOfTags = mNumberOfTags;
        this.mReportTags = mReportTags;
        this.mReportedCompName = mReportedCompName;
        this.mReportedDisplayName = mReportedDisplayName;
        this.mReportedUid = mReportedUid;
        this.mReportedRmn = mReportedRmn;
        this.mReportedFuid = mReportedFuid;
    }

    public SubmittedReportPojo() {
    }

    public String getmReporterCompName() {
        return mReporterCompName;
    }

    public Date getmTimeStamp() {
        return mTimeStamp;
    }

    public void setmTimeStamp(Date mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }

    public void setmReporterCompName(String mReporterCompName) {
        this.mReporterCompName = mReporterCompName;
    }

    public String getmReporterDisplayName() {
        return mReporterDisplayName;
    }

    public void setmReporterDisplayName(String mReporterDisplayName) {
        this.mReporterDisplayName = mReporterDisplayName;
    }

    public String getmReporterRmn() {
        return mReporterRmn;
    }

    public void setmReporterRmn(String mReporterRmn) {
        this.mReporterRmn = mReporterRmn;
    }

    public String getmReporterUid() {
        return mReporterUid;
    }

    public void setmReporterUid(String mReporterUid) {
        this.mReporterUid = mReporterUid;
    }

    public String getmReporterFuid() {
        return mReporterFuid;
    }

    public void setmReporterFuid(String mReporterFuid) {
        this.mReporterFuid = mReporterFuid;
    }

    public String getmComment() {
        return mComment;
    }

    public void setmComment(String mComment) {
        this.mComment = mComment;
    }

    public Double getmNumberOfTags() {
        return mNumberOfTags;
    }

    public void setmNumberOfTags(Double mNumberOfTags) {
        this.mNumberOfTags = mNumberOfTags;
    }

    public List<String> getmReportTags() {
        return mReportTags;
    }

    public void setmReportTags(List<String> mReportTags) {
        this.mReportTags = mReportTags;
    }

    public String getmReportedCompName() {
        return mReportedCompName;
    }

    public void setmReportedCompName(String mReportedCompName) {
        this.mReportedCompName = mReportedCompName;
    }

    public String getmReportedDisplayName() {
        return mReportedDisplayName;
    }

    public void setmReportedDisplayName(String mReportedDisplayName) {
        this.mReportedDisplayName = mReportedDisplayName;
    }

    public String getmReportedUid() {
        return mReportedUid;
    }

    public void setmReportedUid(String mReportedUid) {
        this.mReportedUid = mReportedUid;
    }

    public String getmReportedRmn() {
        return mReportedRmn;
    }

    public void setmReportedRmn(String mReportedRmn) {
        this.mReportedRmn = mReportedRmn;
    }

    public String getmReportedFuid() {
        return mReportedFuid;
    }

    public void setmReportedFuid(String mReportedFuid) {
        this.mReportedFuid = mReportedFuid;
    }
}
