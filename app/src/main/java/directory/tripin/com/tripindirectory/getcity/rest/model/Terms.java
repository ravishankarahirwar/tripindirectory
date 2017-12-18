package directory.tripin.com.tripindirectory.getcity.rest.model;

/**
 * @author Ravishankar Ahirwar
 * @version 1.0
 * @since 17/01/2017
 */

public class Terms {
    private int offset;

    private String value;

    public String getValue() {
        return value;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return "Terms{" +
                "mOffset=" + offset +
                ", mValue='" + value + '\'' +
                '}';
    }
}
