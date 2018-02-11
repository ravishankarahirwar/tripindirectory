package directory.tripin.com.tripindirectory.model.search;

import java.util.List;
import java.util.Map;

/**
 * @author Ravishankar
 * @version 1.0
 * @since 09-02-2018
 */

public class TruckMaker {
    private String title;

    private Map<String,Boolean> truckMaker;

    public Map<String, Boolean> getTruckMaker() {
        return truckMaker;
    }

    public void setTruckMaker(Map<String, Boolean> truckMaker) {
        this.truckMaker = truckMaker;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
