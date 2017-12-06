package directory.tripin.com.tripindirectory.manager;

import android.content.Context;

import directory.tripin.com.tripindirectory.Communication.RquestHandler;
import directory.tripin.com.tripindirectory.dataprovider.ApiTag;
import directory.tripin.com.tripindirectory.dataprovider.RequestProvider;
import directory.tripin.com.tripindirectory.factory.RequestListener;
import directory.tripin.com.tripindirectory.factory.Response;
import directory.tripin.com.tripindirectory.model.response.ElasticSearchResponse;
import directory.tripin.com.tripindirectory.model.response.GetPartnersResponse;
import directory.tripin.com.tripindirectory.model.response.LikeDislikeResponse;
import directory.tripin.com.tripindirectory.role.IGetPartnerOptions;


/**
 * Created by Yogesh N. Tikam on 12/09/2017.
 */

public class PartnersManager implements RequestListener, IGetPartnerOptions {

    private Context mContext;

    private RequestProvider mRequestProvider;

    private GetPartnersListener mGetPartnersListener;

    private ElasticSearchListener mElasticSearchListener;

    private LikeDislikeListener mLikeDislikeListener;

    public PartnersManager(Context context) {
        this.mContext = context;
        this.mRequestProvider = new RequestProvider(mContext);
    }

    public void getPartnersList(String source, String destination, String vehicle,
                                String payload, String length, String goodsType, String serviceType,
                                String lat, String lng, String start, String end, GetPartnersListener getPartnersListenerCallback) {
        this.mGetPartnersListener = getPartnersListenerCallback;
        getPartners(source, destination, vehicle, payload, length,
                goodsType, serviceType, lat, lng, start, end);
    }

    public void getElasticSearchRequest(String query, String from_which_page, String page_size, ElasticSearchListener elasticSearchCallback) {
        this.mElasticSearchListener = elasticSearchCallback;
        getElasticSearch(query, from_which_page, page_size);
    }

    public void likeDislikeRequest(String orgId, String point, LikeDislikeListener likeDislikeListener) {
        this.mLikeDislikeListener = likeDislikeListener;
        getLikeDislikeRank(orgId, point);
    }

    @Override
    public void onResponse(int responseResult, int apiTag, Response response) {
        switch (apiTag) {
            case ApiTag.GET_PARTNERS:
                mGetPartnersListener.onSuccess((GetPartnersResponse) response);
                break;

            case ApiTag.ELASTIC_SEARCH:
                mElasticSearchListener.onSuccess((ElasticSearchResponse) response);
                break;

            case ApiTag.LIKE_DISLIKE:
                mLikeDislikeListener.onSuccess((LikeDislikeResponse) response);
                break;

        }
    }

    @Override
    public void onError(int responseResult, int apiTag, String message, Response response) {
        switch (apiTag) {
            case ApiTag.GET_PARTNERS:
                mGetPartnersListener.onFailed();
                break;
            case ApiTag.ELASTIC_SEARCH:
                mElasticSearchListener.onFailed();
                break;
            case ApiTag.LIKE_DISLIKE:
                mLikeDislikeListener.onFailed();
                break;
        }
    }

    @Override
    public void getPartners(String source, String destination, String vehicle, String payload,
                            String length, String goodsType, String serviceType, String lat, String lng, String start, String end) {
        RquestHandler rquestHandler = new RquestHandler(mContext);
        rquestHandler.send(mRequestProvider.getPartnersRequest(source, destination, vehicle,
                payload, length, goodsType, serviceType, lat, lng, start, end, this));
    }

    @Override
    public void getElasticSearch(String query, String from_which_page, String page_size) {
        RquestHandler rquestHandler = new RquestHandler(mContext);
        rquestHandler.send(mRequestProvider.getElasticSearchRequest(query, from_which_page, page_size,this));
    }

    @Override
    public void getLikeDislikeRank(String orgId, String point) {
        RquestHandler rquestHandler = new RquestHandler(mContext);
        rquestHandler.send(mRequestProvider.getLikeDislikeRequest(orgId, point, this));
    }

    public interface GetPartnersListener {
        void onSuccess(GetPartnersResponse getPartnersResponse);

        void onFailed();
    }

    public interface ElasticSearchListener {
        void onSuccess(ElasticSearchResponse elasticSearchResponse);

        void onFailed();
    }

    public interface LikeDislikeListener {
        void onSuccess(LikeDislikeResponse likeDislikeResponse);

        void onFailed();
    }
}
