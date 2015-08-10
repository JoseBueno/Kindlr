package org.bueno.jose.kindlr.utilities;

/**
 *
 * Created by josebueno    on 8/2/15.
 */
public class StringEx {
    public static boolean isNullOrWhiteSpace(String value){
        return (value == null || "".equals(value) || "".equals(value.trim()));
    }

    public static boolean isLikelyToBeJson(String value){
        return (value.startsWith("{") || value.startsWith("["));
    }

    public  static int numberOfWordsInAString(String value){
        int count = 0;
        if (isNullOrWhiteSpace(value)) return count;
        int length = value.length();
        for (int i = 0; i < length; i++){
           if (i+1 == length) break;
           if (value.substring(i, i+1) == " "){
               count +=1;
           }

        }


        return count;
    }


}
