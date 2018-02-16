package directory.tripin.com.tripindirectory.model.search;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ravishankar
 * @version 1.0
 * @since 10-02-2018
 */

public class TruckProperty {

    public TruckProperty() {
        this.properties = new HashMap<>();
    }

    private String title;
    private Map<String,Boolean> properties;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, Boolean> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Boolean> properties) {
        this.properties = properties;
    }
}
