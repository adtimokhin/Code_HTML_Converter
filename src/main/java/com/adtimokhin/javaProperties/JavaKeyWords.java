package com.adtimokhin.javaProperties;

public class JavaKeyWords {

    private static final String[] keyWords = {"public", "private", "protected",
            "this", "return", "class", "interface", "int", "void",
            "float", "boolean", "true", "false" , "extends" , "implements",
            "null", "new", "if", "else", "static"};

    public static boolean isKeyWord(String word){
        for (String keyWord : keyWords) {
            if (keyWord.equals(word)) {
                return true;
            }
        }
        return false;
    }
}
