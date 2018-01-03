package directory.tripin.com.tripindirectory.utils;

/**
 * Created by Yogesh N. Tikam on 1/3/2018.
 */

public class TextUtils {

    public TextUtils() {
    }

    public String toTitleCase(String input) {
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = false;
        boolean isFirstChar = true;


        for (char c : input.toLowerCase().toCharArray()) {

            char cc = c;

            if(isFirstChar){
                cc = (char) (c & 0x5f);//to upper case
                isFirstChar = false;
            }
            if(Character.isSpaceChar(c)){
                nextTitleCase = true;
            }
            if(c=='.'){
                nextTitleCase = true;
            }
            if(nextTitleCase && !Character.isSpaceChar(c) ){
                cc = (char) (c & 0x5f);//to upper case
                nextTitleCase = false;
            }
            titleCase.append(cc);

        }

        return titleCase.toString();
    }


}
