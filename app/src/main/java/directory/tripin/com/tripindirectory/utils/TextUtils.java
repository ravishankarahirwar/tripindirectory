package directory.tripin.com.tripindirectory.utils;

/**
 * Created by Shubham on 1/3/2018.
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

            if(nextTitleCase && !Character.isSpaceChar(c) ){
                cc = (char) (c & 0x5f);//to upper case
                nextTitleCase = false;
            }

            if(Character.isSpaceChar(c)||c=='.'){
                nextTitleCase = true;
            }

            titleCase.append(cc);

        }

        return titleCase.toString();
    }


}
