package directory.tripin.com.tripindirectory.model.search;

import java.util.Map;

/**
 * @author Ravishankar
 * @version 1.0
 * @since 09-02-2018
 */

public class Length {
    private String title;

    private Map<String,Boolean> length;

    public Map<String, Boolean> getLength() {
        return length;
    }

    public void setLength(Map<String, Boolean> length) {
        this.length = length;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
