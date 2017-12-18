package directory.tripin.com.tripindirectory.getcity.rest.model;

/**
 * @author Ravishankar Ahirwar
 * @since 17/01/2017
 * @version 1.0
 */


public class MatchedSubstring
{
    private int length;

    private int offset;

    public int getLength()
    {
        return length;
    }

    public int getOffset()
    {
        return offset;
    }

    @Override
    public String toString()
    {
        return "MatchedSubstring{" +
                "mLength=" + length +
                ", mOffset=" + offset +
                '}';
    }
}
