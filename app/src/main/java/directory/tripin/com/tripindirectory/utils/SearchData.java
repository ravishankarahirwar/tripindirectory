package directory.tripin.com.tripindirectory.utils;

import android.content.Context;
import android.widget.Filter;


import java.util.ArrayList;
import java.util.List;

import directory.tripin.com.tripindirectory.model.SuggestionCompanyName;

public class SearchData {

//    public ArrayList<SearchResult> getSearchResult(Context context) {
//        ArrayList<SearchResult> searchResults = new ArrayList<>();
//        List<Station> searchableData = getSearchableData();
//        for (Station station : searchableData) {
//            SearchResult option = new SearchResult(station.getName(),
//                    context.getResources().getDrawable(R.drawable.ic_history));
//            searchResults.add(option);
//        }
//        return searchResults;
//    }

    private List<SuggestionCompanyName> getSearchableData() {
        List<SuggestionCompanyName> searchableData = new ArrayList<>();
        searchableData.add(new SuggestionCompanyName("Mumbai"));
        searchableData.add(new SuggestionCompanyName("Rajkot"));
        searchableData.add(new SuggestionCompanyName("Bhopal"));
        searchableData.add(new SuggestionCompanyName("Nagpur"));
        searchableData.add(new SuggestionCompanyName("Pune"));
        searchableData.add(new SuggestionCompanyName(""));

        return searchableData;
    }
    public interface OnFindSuggestionsListener {
        void onResults(List<SuggestionCompanyName> results);
    }

    public void findSuggestions(Context context, String query, final int limit, final long simulatedDelay,
                                       final OnFindSuggestionsListener listener) {
        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                try {
                    Thread.sleep(simulatedDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                List<SuggestionCompanyName> suggestionList = new ArrayList<>();
                if (!(constraint == null || constraint.length() == 0)) {

                    for (SuggestionCompanyName station : getSearchableData()) {
                        if (station.getBody().toUpperCase()
                                .startsWith(constraint.toString().toUpperCase())) {

                            suggestionList.add(station);
                            if (limit != -1 && suggestionList.size() == limit) {
                                break;
                            }
                        }
                    }
                }

                FilterResults results = new FilterResults();
//                Collections.sort(suggestionList, new Comparator<SearchSuggestion>() {
//                    @Override
//                    public int compare(SearchSuggestion lhs, SearchSuggestion rhs) {
//                        return lhs.getIsHistory() ? -1 : 0;
//                    }
//                });
                results.values = suggestionList;
                results.count = suggestionList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null) {
                    listener.onResults((List<SuggestionCompanyName>) results.values);
                }
            }
        }.filter(query);

    }


}
