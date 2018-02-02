package directory.tripin.com.tripindirectory.model;

/**
 * Created by Shubham on 2/1/2018.
 */

public class FilterPojo {
    private String mFilterName;
    private int mFilterType;
    private int mFilterTag;

    public FilterPojo(String mFilterName, int mFilterType, int mFilterTag) {
        this.mFilterName = mFilterName;
        this.mFilterType = mFilterType;
        this.mFilterTag = mFilterTag;
    }

    public FilterPojo() {
    }

    public String getmFilterName() {
        return mFilterName;
    }

    public void setmFilterName(String mFilterName) {
        this.mFilterName = mFilterName;
    }

    public int getmFilterType() {
        return mFilterType;
    }

    public void setmFilterType(int mFilterType) {
        this.mFilterType = mFilterType;
    }

    public int getmFilterTag() {
        return mFilterTag;
    }

    public void setmFilterTag(int mFilterTag) {
        this.mFilterTag = mFilterTag;
    }
}
