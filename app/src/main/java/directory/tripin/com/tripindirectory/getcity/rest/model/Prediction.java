package directory.tripin.com.tripindirectory.getcity.rest.model;

import java.util.ArrayList;


/**
 * @author Ravishankar Ahirwar
 * @version 1.0
 * @since 17/01/2017
 */


public class Prediction {

    private String description;

    private String id;

    private ArrayList<MatchedSubstring> matched_substrings;

    private String place_id;

    private String reference;

    private ArrayList<Terms> terms;

    private ArrayList<String> types;


    public String getDescription() {
        return description;
    }

    public String getID() {
        return id;
    }

    public ArrayList<MatchedSubstring> getMatchedSubstrings() {
        return matched_substrings;
    }

    public String getPlaceID() {
        return place_id;
    }

    public String getReference() {
        return reference;
    }

    public ArrayList<Terms> getTerms() {
        return terms;
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    @Override
    public String toString() {
        return "Prediction{" +
                "mDescription='" + description + '\'' +
                ", mID='" + id + '\'' +
                ", mMatchedSubstrings=" + matched_substrings +
                ", mPlaceID='" + place_id + '\'' +
                ", mReference='" + reference + '\'' +
                ", mTerms=" + terms +
                ", mTypes=" + types +
                '}';
    }

}
