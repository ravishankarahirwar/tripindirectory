package directory.tripin.com.tripindirectory.getcity.rest;

import android.content.ContentValues;
import android.content.Context;

import directory.tripin.com.tripindirectory.dataprovider.ConnectionListener;
import directory.tripin.com.tripindirectory.dataprovider.ParamsProvider;
import directory.tripin.com.tripindirectory.factory.Response;
import directory.tripin.com.tripindirectory.factory.ResponseResults;
import directory.tripin.com.tripindirectory.getcity.managers.ContentManager;
import directory.tripin.com.tripindirectory.getcity.rest.responses.PredictionResponse;
import directory.tripin.com.tripindirectory.helper.Logger;


/**
 * Created by Yogesh N. Tikam on 3/6/2017.
 */

public class CityPrediction  {

    /**
     * The following method is for calling Google Place API Autocomplete Service.
     * Please note that this Api is not documented in our Tripin Api document.
     * It is a third party Api which is used to autocomplete the written suggestions for any place name entered.
     * However we use this Api in our application to filter out choices of city as per the state name provided by the user.
     * This Api represents a GET Request to the server where there are 2 parameters to be sent.
     * First parameter is input . In our app, the value will be the city initials entered by the user.
     * However the results will be filtered from our end according to the state entered by the user.
     *
     * @Logic: We call the following method from the adapter. One of it's input parameter is the state name entered by the user so that response is saved
     * in onResponse Method . The state name is one of the two GET parameters to be sent while calling the service
     * alongwith the other parameter - the Google API Key. The service then returns the list of cities in the onResponse method which
     * will call and send response to ContentManager class which will filter the choices according to the state
     */

//    public void getRequest(Context context, final String input) {
//        ContentValues paramSet = new ContentValues();
//        paramSet.put(ServerParams.INPUT, input);
//
//        mConnection = new ConnectionFactory(context, this);
//        mConnection.setPostParams(ParamsProvider.getParams(paramSet));
//        mConnection.createConnection(ApiTag.GOOGLE_API_FOR_CITY);
//    }
//
//    @Override
//    public void onResponse(int responseResult, Response response) {
//        if (responseResult == ResponseResults.RESPONSE_OK && response instanceof PredictionResponse) {
//            PredictionResponse predictionResponse = (PredictionResponse) response;
//            Logger.v("Prediction response incoming: " + predictionResponse);
//            ContentManager.getInstance().setPredictionList(predictionResponse.getPredictionList());
//        }
//    }
//
//
//    @Override
//    public void onError(int responseResult, int apiTag, String message, Response response) {
//        Logger.v("Prediction Response is error");
//    }


}
