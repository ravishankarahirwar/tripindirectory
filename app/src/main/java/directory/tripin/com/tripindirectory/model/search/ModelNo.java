package directory.tripin.com.tripindirectory.model.search;

import java.util.Map;

/**
 * @author Ravishankar
 * @version 1.0
 * @since 09-02-2018
 */

public class ModelNo {
    private String title;
    private Map<String,Boolean> modelNo;

    public Map<String, Boolean> getModelNo() {
        return modelNo;
    }

    public void setModelNo(Map<String, Boolean> modelNo) {
        this.modelNo = modelNo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
