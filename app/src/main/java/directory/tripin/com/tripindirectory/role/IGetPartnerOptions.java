package directory.tripin.com.tripindirectory.role;

/**
 * Created by Yogesh N. Tikam on 12/9/2017.
 */

public interface IGetPartnerOptions {
    void getPartners(String source, String destination, String vehicle,
                     String payload, String length, String goodsType, String serviceType, String lat, String lng,String start, String end);

    void getElasticSearch(String query);

    void getLikeDislikeRank(String orgId, String point);
}
