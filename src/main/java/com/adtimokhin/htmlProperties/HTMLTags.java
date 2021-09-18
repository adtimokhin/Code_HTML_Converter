package com.adtimokhin.htmlProperties;

public class HTMLTags {

    public static final String DIV_CODE_BLOCK_START = "<div class=\"code-block\" style=\"overflow-y:auto;\">";
    public static final String DIV_CODE_CONTAINER_START ="<div class=\"code-container\">";


    public static final String SPAN_COMMENT_START = "<span class=\"comment\">";
    public static final String SPAN_DEV_COMMENT_START = "<span class=\"dev-comment\">";
    public static final String SPAN_ANNOTATION_START = "<span class=\"annotation\">";
    public static final String SPAN_KEY_WORD_START = "<span class=\"key-word\">";
    public static final String SPAN_STRING_START = "<span class=\"string\">";


    public static final String P_FILE_NAME_START = "<p class=\"file-name\">";



    public static final String DIV_FINISH = "</div>";
    public static final String SPAN_FINISH = "</span>";
    public static final String P_FINISH = "</p>";
    public static final String P_START = "<p>";

    public static final String BR_TAG = "<br/>";



    public static String getCodeInTag(String code, Tags tag) throws Exception {
        switch (tag){
            case DIV_CODE_BLOCK:
                return putCodeInTags(code, DIV_CODE_BLOCK_START, DIV_FINISH);

            case DIV_CODE_CONTAINER:
                return putCodeInTags(code, DIV_CODE_CONTAINER_START, DIV_FINISH);

            case SPAN_COMMENT:
                return putCodeInTags(code, SPAN_COMMENT_START, SPAN_FINISH);

            case SPAN_DEV_COMMENT:
                return putCodeInTags(code,SPAN_DEV_COMMENT_START, SPAN_FINISH);

            case SPAN_ANNOTATION:
                return putCodeInTags(code, SPAN_ANNOTATION_START, SPAN_FINISH);

            case SPAN_KEY_WORD:
                return putCodeInTags(code, SPAN_KEY_WORD_START, SPAN_FINISH);

            case SPAN_STRING:
                return putCodeInTags(code, SPAN_STRING_START, SPAN_FINISH);

            case P_FILE_NAME:
                return putCodeInTags(code, P_FILE_NAME_START, P_FINISH);

            case P_TAG:
                return putCodeInTags(code, P_START, P_FINISH);

            default:
                throw new Exception("No such tag was found");


                
        }
    }

    private static String putCodeInTags(String code, String startTag, String finishTag){

        return startTag + code+
                finishTag;
    }

}
