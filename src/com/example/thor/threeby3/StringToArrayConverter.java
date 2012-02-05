package com.example.thor.threeby3;

import java.util.StringTokenizer;

public class StringToArrayConverter {

    public static String[] convertTokenizedStringToStringArray(String tokenizedString, String token) {
        String stringArray[] = null;
        if ((tokenizedString != null) && (token != null)) {
            StringTokenizer st = new StringTokenizer(tokenizedString, token);
            int i = 0;
            stringArray = new String[st.countTokens()];
            while (st.hasMoreElements()) {
                stringArray[i] = st.nextToken();
                i++;
            }
        }
        return stringArray;
    }

    public static int[] convertTokenizedStringToIntArray(String tokenizedString, String token) {
        int intArray[] = null;
        if ((tokenizedString != null) && (token != null)) {
            StringTokenizer st = new StringTokenizer(tokenizedString, token);
            int i = 0;
            intArray = new int[st.countTokens()];
            while (st.hasMoreElements()) {
                intArray[i] = Integer.parseInt(st.nextToken());
                i++;
            }
        }
        return intArray;
    }
}