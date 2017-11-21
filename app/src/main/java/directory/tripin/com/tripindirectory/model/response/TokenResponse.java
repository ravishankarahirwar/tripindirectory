package directory.tripin.com.tripindirectory.model.response;


import directory.tripin.com.tripindirectory.factory.Response;

/**
 * Created by Yogesh N. Tikam on 5/10/2017.
 */

public class TokenResponse implements Response {

    private String status;
    private String data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
