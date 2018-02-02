package directory.tripin.com.tripindirectory.model;

import java.util.List;

/**
 * Created by Shubham on 2/1/2018.
 */

public class QueryBookmarkPojo {

    private List<FilterPojo> mFiltersList;
    private String mBookmarkName;
    private int mSortIndex;
    private int mSearchTag;
    private String mSearchQuery;
    private String mTimeStamp;

    public QueryBookmarkPojo(List<FilterPojo> mFiltersList, String mBookmarkName, int mSortIndex, int mSearchTag, String mSearchQuery, String mTimeStamp) {
        this.mFiltersList = mFiltersList;
        this.mBookmarkName = mBookmarkName;
        this.mSortIndex = mSortIndex;
        this.mSearchTag = mSearchTag;
        this.mSearchQuery = mSearchQuery;
        this.mTimeStamp = mTimeStamp;
    }

    public QueryBookmarkPojo() {
    }

    public String getmTimeStamp() {
        return mTimeStamp;
    }

    public void setmTimeStamp(String mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }

    public List<FilterPojo> getmFiltersList() {
        return mFiltersList;
    }

    public void setmFiltersList(List<FilterPojo> mFiltersList) {
        this.mFiltersList = mFiltersList;
    }

    public String getmBookmarkName() {
        return mBookmarkName;
    }

    public void setmBookmarkName(String mBookmarkName) {
        this.mBookmarkName = mBookmarkName;
    }

    public int getmSortIndex() {
        return mSortIndex;
    }

    public void setmSortIndex(int mSortIndex) {
        this.mSortIndex = mSortIndex;
    }

    public int getmSearchTag() {
        return mSearchTag;
    }

    public void setmSearchTag(int mSearchTag) {
        this.mSearchTag = mSearchTag;
    }

    public String getmSearchQuery() {
        return mSearchQuery;
    }

    public void setmSearchQuery(String mSearchQuery) {
        this.mSearchQuery = mSearchQuery;
    }
}
