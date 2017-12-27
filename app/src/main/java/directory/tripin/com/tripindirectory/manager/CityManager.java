package directory.tripin.com.tripindirectory.manager;

import android.content.Context;

import java.util.List;

import directory.tripin.com.tripindirectory.Communication.RquestHandler;
import directory.tripin.com.tripindirectory.dataprovider.ApiTag;
import directory.tripin.com.tripindirectory.dataprovider.RequestProvider;
import directory.tripin.com.tripindirectory.factory.RequestListener;
import directory.tripin.com.tripindirectory.factory.Response;
import directory.tripin.com.tripindirectory.getcity.managers.ContentManager;
import directory.tripin.com.tripindirectory.getcity.rest.responses.PredictionResponse;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.model.SuggestionCompanyName;
import directory.tripin.com.tripindirectory.model.response.GetPartnersResponse;
import directory.tripin.com.tripindirectory.role.IGetCityOptions;


/**
 * Created by Yogesh N. Tikam on 12/09/2017.
 */

public class CityManager implements RequestListener, IGetCityOptions {

    private Context mContext;

    private RequestProvider mRequestProvider;

    private CitySuggestionListener mCitySuggestionListener;




    public interface CitySuggestionListener {
        void onSuccess(List<SuggestionCompanyName> suggestions);
        void onFaailed();
    }

    public CityManager(Context context) {
        this.mContext = context;
        this.mRequestProvider = new RequestProvider(mContext);
    }

    public CityManager(Context context, String source, CitySuggestionListener  citySuggestionListener) {
        this.mContext = context;
        this.mRequestProvider = new RequestProvider(mContext);
        mCitySuggestionListener = citySuggestionListener;
        getCitys(source);
    }


    public void getCitySuggestionList(String source) {
        getCitys(source);
    }

    @Override
    public void onResponse(int responseResult, int apiTag, Response response) {
        switch (apiTag) {
            case ApiTag.GOOGLE_API_FOR_CITY :
                PredictionResponse predictionResponse = (PredictionResponse) response;
                Logger.v("Prediction response incoming: " + predictionResponse);
                ContentManager.getInstance(mContext).setPredictionList(predictionResponse.getPredictionList());
                break;
        }
    }

    @Override
    public void onError(int responseResult, int apiTag, String message, Response response) {
        switch (apiTag) {
            case ApiTag.GOOGLE_API_FOR_CITY :

                break;
        }
    }

    public interface GetCityListener {
        void onSuccess(GetPartnersResponse getPartnersResponse);
        void onFailed();
    }


    @Override
    public void getCitys(String source) {
        RquestHandler rquestHandler = new RquestHandler(mContext);
        rquestHandler.send(mRequestProvider.getCityRequest(source,this));
    }
}
