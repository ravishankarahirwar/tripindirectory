package directory.tripin.com.tripindirectory.model.search;

import java.util.List;

/**
 * @author Ravishankar
 * @version 1.0
 * @since 09-02-2018
 */

public class Fleet {
    private List<Truck> trucks;

    public List<Truck> getTrucks() {
        return trucks;
    }

    public void setTrucks(List<Truck> trucks) {
        this.trucks = trucks;
    }
}
