package directory.tripin.com.tripindirectory.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;


public class UserQuery {
    private String queryAction;
    private String query;
    private String uid;
    private String userMobileNo;
    private String queryBy;
    private long queryTime;

    public UserQuery() {
    }

    public UserQuery(String queryAction, String query, String uid, String queryBy, long queryTime) {
        this.queryAction = queryAction;
        this.query = query;
        this.uid = uid;
        this.queryBy = queryBy;
        this.queryTime = queryTime;
    }

    public String getQueryAction() {
        return queryAction;
    }

    public void setQueryAction(String queryAction) {
        this.queryAction = queryAction;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQueryBy() {
        return queryBy;
    }

    public void setQueryBy(String queryBy) {
        this.queryBy = queryBy;
    }

    public long getQueryTime() {
        return queryTime;
    }

    public void setQueryTime(long queryTime) {
        this.queryTime = queryTime;
    }

    public String getUserMobileNo() {
        return userMobileNo;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUserMobileNo(String userMobileNo) {
        this.userMobileNo = userMobileNo;
    }

    public String getmUid() {
        return uid;
    }

    public void setmUid(String mUid) {
        this.uid = mUid;
    }


    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("query", query);
        result.put("queryAction", queryAction);
        result.put("queryBy", queryBy);
        result.put("queryTime", queryTime);
        result.put("userMobileNo", userMobileNo);
        return result;
    }
}
