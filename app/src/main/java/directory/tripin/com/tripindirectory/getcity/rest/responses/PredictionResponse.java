package directory.tripin.com.tripindirectory.getcity.rest.responses;

import java.util.ArrayList;

import directory.tripin.com.tripindirectory.factory.Response;
import directory.tripin.com.tripindirectory.getcity.rest.model.Prediction;


/**
 * @author Ravishankar Ahirwar
 * @version 1.0
 * @since 17/01/2017
 */

public class PredictionResponse implements Response {

    private ArrayList<Prediction> predictions;

    public ArrayList<Prediction> getPredictionList() {
        return predictions;
    }

    @Override
    public String toString() {
        return "PredictionResponse{" +
                "mPredictionList=" + predictions +
                "} " + super.toString();
    }
}