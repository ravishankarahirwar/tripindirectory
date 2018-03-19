package directory.tripin.com.tripindirectory.callback;

import java.util.Map;

/**
 * @author Ravishankar
 * @version 1.0
 * @since 19-03-2018
 */

public interface OnTruckPropertyValueChange {
    void onPropertyChange(Map<String,Boolean> properties);
}
