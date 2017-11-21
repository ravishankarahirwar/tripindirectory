package directory.tripin.com.tripindirectory.dataprovider;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import directory.tripin.com.tripindirectory.factory.Request;
import directory.tripin.com.tripindirectory.factory.RequestListener;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.model.request.ElasticSearchRequest;
import directory.tripin.com.tripindirectory.model.request.GetPartnersRequest;


/**
 * This class is a collection of all the request need to send to server,
 * for getting a specific request you need to pass certain parameter this will return a full request
 * object you just need to pass to your manager to controller
 *
 * @author Ravishankar
 * @version 1.0
 * @since 18-04-2017
 */

public class RequestProvider {

    private Context mContext;

    public RequestProvider(Context context) {
        mContext = context;
    }

    public Request getPartnersRequest(String source, String destination, String vehicle,
                                      String payload, String length, String goodsType,
                                      String serviceType, String lat, String lng, String start, String end, RequestListener listener) {

        Map<String, String> params = new HashMap<>();
        params.put(GetPartnersRequest.SOURCE, source);
        params.put(GetPartnersRequest.DESTINATION, destination);
        params.put(GetPartnersRequest.VEHICLE, vehicle);
        params.put(GetPartnersRequest.PAYLOAD, payload);
        params.put(GetPartnersRequest.LENGTH, length);
        params.put(GetPartnersRequest.GOODS_TYPE, goodsType);
        params.put(GetPartnersRequest.SERVICE_TYPE, serviceType);
        params.put(GetPartnersRequest.LAT, lat);
        params.put(GetPartnersRequest.LNG, lng);
        params.put(GetPartnersRequest.START, start);
        params.put(GetPartnersRequest.END, end);

        return new Request.RequestBuilder(mContext, listener)
                .type(Request.Method.POST)
                .url(ApiProvider.getApiByTag(ApiTag.GET_PARTNERS))
                .postParams(params)
                .tag(ApiTag.GET_PARTNERS)
                .build();
    }

    public Request getElasticSearchRequest(String query, RequestListener listener) {

        Map<String, String> headerParams = new HashMap<>();
        headerParams.put("Content-Type", "application/json");

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put(ElasticSearchRequest.QUERY_STRING, query);
            jsonBody.put(ElasticSearchRequest.FROM, "1");
            jsonBody.put(ElasticSearchRequest.SIZE, "5");
        } catch (JSONException eJsonException) {
            eJsonException.printStackTrace();
        }

        final String requestBody = jsonBody.toString();
        Logger.v("rawData string generated from data in activity:  " + requestBody);

        return new Request.RequestBuilder(mContext, listener)
                .type(Request.Method.SEND_JSON)
                .url(ApiProvider.getApiByTag(ApiTag.ELASTIC_SEARCH))
                .headerParams(headerParams)
//                .postParams(params)
                .rawData(requestBody)
                .tag(ApiTag.ELASTIC_SEARCH)
                .build();

    }

}
