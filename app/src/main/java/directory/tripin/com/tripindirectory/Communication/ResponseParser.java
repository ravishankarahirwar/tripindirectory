package directory.tripin.com.tripindirectory.Communication;



import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


import org.json.JSONException;
import org.json.JSONObject;

import directory.tripin.com.tripindirectory.dataprovider.ApiTag;
import directory.tripin.com.tripindirectory.factory.Request;
import directory.tripin.com.tripindirectory.factory.RequestListener;
import directory.tripin.com.tripindirectory.factory.Response;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.model.response.ElasticSearchResponse;
import directory.tripin.com.tripindirectory.model.response.GetPartnersResponse;

/**
 * @author Ravishankar
 * @version 1.0
 * @since 17-04-2017
 */

public class ResponseParser {

    private int mApiTag;
    private RequestListener mConnectionListener;

    public void parseJson(String data, Request request) {
        mApiTag = request.getTag();
        mConnectionListener = request.getConnectionListener();
        try {
            Gson gson = new Gson();

            /**
             * The following else condition handles the exception for PredictionResponse class for handling
             * Json response of Google Place API Autocomplete Service.
             */

            JSONObject jObj = new JSONObject(data);

            Response response = getResponseObject(jObj, gson);
            Logger.v("parse json object: " + response);
            mConnectionListener.onResponse(RequestListener.RESPONSE_OK, mApiTag, response);
        } catch (JsonSyntaxException eJsonSyntaxException) {
            eJsonSyntaxException.printStackTrace();
            Logger.v("Exception JsonSyntaxException= " + eJsonSyntaxException.getMessage());
            mConnectionListener.onError(RequestListener.PARSE_ERR0R, mApiTag, eJsonSyntaxException.getMessage(), null);
        } catch (JSONException eJSONException) {
            Logger.v("Exception JsonSyntaxException= " + eJSONException.getMessage());
            eJSONException.printStackTrace();
        }
    }

    private Response getResponseObject(JSONObject jsonObject, Gson gson) {
        Response response = null;
        try {
            switch (this.mApiTag) {
                case ApiTag.GET_PARTNERS: //1
                    GetPartnersResponse getPartnersResponse = gson.fromJson(jsonObject.toString(), GetPartnersResponse.class);
                    response = getPartnersResponse;
                    if(getPartnersResponse != null) {
                        Logger.v(getPartnersResponse.toString());
                        Logger.v( getPartnersResponse.getData().get(0).getName());
                    }
                    break;


                case ApiTag.ELASTIC_SEARCH: //1
                    ElasticSearchResponse elasticSearchResponse = gson.fromJson(jsonObject.toString(), ElasticSearchResponse.class);
                    response = elasticSearchResponse;
                    break;
            }
        } catch (JsonSyntaxException eJsonSyntaxException) {
            eJsonSyntaxException.printStackTrace();
        } catch (Exception e) {
            Logger.v("Coming here in Exception in API TAg : " + this.mApiTag + "Exception message = " + e.getMessage());
        }
        return response;
    }
}
