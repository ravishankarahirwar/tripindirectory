package directory.tripin.com.tripindirectory.volley;

/**
 * Created by Amita.Mistry on 10/23/2016.
 */
public class DataModel
{
    private String file;
    private byte[] content;
    private String type;

    public DataModel() {}

    public DataModel(String name, byte[] data)
    {
        file = name;
        content = data;
    }

    public DataModel(String name, byte[] data, String mimeType)
    {
        file = name;
        content = data;
        type = mimeType;
    }

    public String getFileName()
    {
        return file;
    }

    public void setFileName(String file)
    {
        this.file = file;
    }

    public byte[] getContent()
    {
        return content;
    }

    public void setContent(byte[] content)
    {
        this.content = content;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
}