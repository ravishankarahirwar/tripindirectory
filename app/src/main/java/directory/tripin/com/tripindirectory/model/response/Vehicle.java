package directory.tripin.com.tripindirectory.model.response;

import directory.tripin.com.tripindirectory.model.Driver;

/**
 * @author Ravishankar
 * @version 1.0
 * @since 20-12-2017
 */

public class Vehicle {
    private String type;
    private String bodyType;
    private String number;
    private String payload;
    private String length;
    private String width;
    private Driver driver;
    private boolean isAvailable;

    public Vehicle() {
    }

    public Vehicle(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBodyType() {
        return bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }


}
