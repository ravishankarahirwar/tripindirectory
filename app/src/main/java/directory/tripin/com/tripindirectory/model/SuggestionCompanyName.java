package directory.tripin.com.tripindirectory.model;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

/**
 * @author Ravishankar
 * @version 1.0
 * @since 26-12-2017
 */

public class SuggestionCompanyName implements SearchSuggestion {

    private String companyName;

    public SuggestionCompanyName() {
    }

    public SuggestionCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public SuggestionCompanyName(Parcel source) {
        this.companyName = source.readString();
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }


    public static final Creator<SuggestionCompanyName> CREATOR = new Creator<SuggestionCompanyName>() {
        @Override
        public SuggestionCompanyName createFromParcel(Parcel in) {
            return new SuggestionCompanyName(in);
        }

        @Override
        public SuggestionCompanyName[] newArray(int size) {
            return new SuggestionCompanyName[size];
        }
    };

    @Override
    public String getBody() {
        return companyName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
