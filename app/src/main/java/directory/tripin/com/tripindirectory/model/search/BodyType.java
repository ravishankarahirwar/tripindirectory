package directory.tripin.com.tripindirectory.model.search;

import java.util.Map;

/**
 * @author Ravishankar
 * @version 1.0
 * @since 09-02-2018
 */

public class BodyType {
    private String title;

    private Map<String,Boolean> bodyType;

    public Map<String, Boolean> getBodyType() {
        return bodyType;
    }

    public void setBodyType(Map<String, Boolean> bodyType) {
        this.bodyType = bodyType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
