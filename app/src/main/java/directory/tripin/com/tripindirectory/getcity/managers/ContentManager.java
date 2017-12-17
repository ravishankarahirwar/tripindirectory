package directory.tripin.com.tripindirectory.getcity.managers;

import android.content.Context;

import java.util.ArrayList;

import directory.tripin.com.tripindirectory.getcity.rest.model.Prediction;


/**
 * @author Ravishankar Ahirwar
 * @version 1.0
 * @since 17/01/2017
 */
public class ContentManager {

    public static final String LOG_TAG = ContentManager.class.getSimpleName();
    private static ContentManager msInstance;
    private final Context mContext;
    private ArrayList<Prediction > mPredictionList = new ArrayList<>();

    private ContentManager(Context context) {
        mContext = context;
    }

    public static ContentManager init(Context context) {
        if (msInstance == null) {
            msInstance = new ContentManager(context);
        }
        return msInstance;
    }

    public static ContentManager getInstance(Context context) {
        if (msInstance == null) {
            msInstance = new ContentManager(context);
        }
        return msInstance;    }

    public ArrayList<Prediction> getPredictionList() {
        return mPredictionList;
    }

    public void setPredictionList(ArrayList<Prediction> predictionList) {
        mPredictionList = predictionList;
    }

    public ArrayList<String> getPredictionDescriptionList() {
        ArrayList<String> descriptionList = new ArrayList<>();
        String cityStr = "";
        String stateStr = "";
        String countryStr = "";

        for (Prediction prediction : mPredictionList) {
            String desc = prediction.getDescription();
            if (desc != null && desc.length() > 0) {
                String[] itemList = desc.split(",");
                if (itemList.length == 3) {
                    cityStr = itemList[0];
                    stateStr = itemList[1];
                    countryStr = itemList[2];
                    if (countryStr.trim().equalsIgnoreCase("India")) {

                        descriptionList.add(prediction.getDescription());
                    }
                }
            }
        }

        return descriptionList;
    }

}
