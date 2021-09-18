package com.adtimokhin.javaProperties;

/**
 * Class that groups keywords used in Java together.
 */
public class JavaKeyWords {

    /**
     * An array containing all keywords in Java for usage in {@link #isKeyWord(String)} method.
     */
    private static final String[] keyWords = {"public", "private", "protected",
            "this", "return", "class", "interface", "int", "void",
            "float", "boolean", "true", "false" , "extends" , "implements",
            "null", "new", "if", "else", "static", "for"};


    /**
     * Method that checks if {@code word} is a keyword.
     *
     *
     * @param word a word that needs to be compared to check if it is a keyword or not.
     * @return true if the {@code word} is in {@link #keyWords} array.
     */
    public static boolean isKeyWord(String word){
        for (String keyWord : keyWords) {
            if (keyWord.equals(word)) {
                return true;
            }
        }
        return false;
    }
}
