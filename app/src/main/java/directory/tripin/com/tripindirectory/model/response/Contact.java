package directory.tripin.com.tripindirectory.model.response;

/**
 * @author Ravishankar
 * @version 1.0
 * @since 15-09-2017
 */

public class Contact {
    private String name;
    private String phone;
    private String directoryName;

    public Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }
}
