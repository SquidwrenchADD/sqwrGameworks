package org.squidwrench.gameworks.tictactoe;

import java.util.StringTokenizer;


/**
 * This class demonstrates on how to convert a tokenized string to an array 
 * @author JavaIQ.net
 * Creation Date Dec 10, 2010
 */
public class StringToArrayConverter {

    /**
     * Method to convert a tokenized string to a string array
     * @param tokenizedString -- the tokenized string which needs to be converted
     * @param token -- the token which separates the values in the input string.
     * @return stringArray -- the converted string array
     */
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

    /**
     * Method to convert a tokenized string to an int array
     * @param tokenizedString -- the tokenized string which needs to be converted
     * @param token -- the token which separates the values in the input string.
     * @return stringArray -- the converted int array
     */
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