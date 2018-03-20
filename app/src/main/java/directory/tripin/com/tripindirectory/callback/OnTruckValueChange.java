package directory.tripin.com.tripindirectory.callback;

import java.util.List;

import directory.tripin.com.tripindirectory.model.search.TruckProperty;

/**
 * @author Ravishankar
 * @version 1.0
 * @since 19-03-2018
 */

public interface OnTruckValueChange {
    void onTruckPropertiesChange(List<TruckProperty> truckProperties);
}
