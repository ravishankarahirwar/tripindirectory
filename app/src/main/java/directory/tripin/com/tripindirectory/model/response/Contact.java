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
    private String address;
    private String contactName;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Contact() {

    }

    public Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public Contact(String name, String companyPerson, String phone, String address) {
        this.name = name;
        this.contactName = companyPerson;
        this.phone = phone;
        this.address = address;
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

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
}
