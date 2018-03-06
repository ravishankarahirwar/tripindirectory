package directory.tripin.com.tripindirectory.model.search;

import java.util.Map;

/**
 * @author Ravishankar
 * @version 1.0
 * @since 09-02-2018
 */

public class PayLoad {
    private String title;

    private Map<Integer,Boolean> payLoad;

    public Map<Integer, Boolean> getPayLoad() {
        return payLoad;
    }

    public void setPayLoad(Map<Integer, Boolean> payLoad) {
        this.payLoad = payLoad;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
