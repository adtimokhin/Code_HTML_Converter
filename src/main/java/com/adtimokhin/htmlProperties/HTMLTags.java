package com.adtimokhin.htmlProperties;

import com.adtimokhin.CodeHTMLConverter;

/**
 * Class used by {@link com.adtimokhin.CodeHTMLConverter} class to put some code into appropriate HTML tags.
 */
public class HTMLTags {


    /**
     * Constants used to identify all possible HTML tags.
     **/

    private static final String DIV_CODE_BLOCK_START = "<div class=\"code-block\" style=\"overflow-y:auto;\">";
    private static final String DIV_CODE_CONTAINER_START = "<div class=\"code-container\">";


    private static final String SPAN_COMMENT_START = "<span class=\"comment\">";
    private static final String SPAN_DEV_COMMENT_START = "<span class=\"dev-comment\">";
    private static final String SPAN_ANNOTATION_START = "<span class=\"annotation\">";
    private static final String SPAN_KEY_WORD_START = "<span class=\"key-word\">";
    private static final String SPAN_STRING_START = "<span class=\"string\">";


    private static final String P_FILE_NAME_START = "<p class=\"file-name\">";


    private static final String DIV_FINISH = "</div>";
    private static final String SPAN_FINISH = "</span>";
    private static final String P_FINISH = "</p>";
    private static final String P_START = "<p>";


    public static final String BR_TAG = "<br/>";


    /**
     * Method that is used in methods that are used in {@link CodeHTMLConverter#convert(String)} method.
     * Its purpose to put code like Java into appropriate HTML tags, that are listed in enum {@link Tags}.
     *
     * @param code code that needs to be put into HTML tags. Called from methods used in
     *             {@link com.adtimokhin.CodeHTMLConverter#convert(String)} method,
     *             like {@link CodeHTMLConverter#convertJava()}.
     * @param tag  {@link Tags} tag that identifies what HTML tags should be used to convert other code into HTML.
     * @return {@link String} that is code that has been placed inside the appropriate HTML tags.
     * @throws Exception if unidentified {@link Tags} was used.
     */
    public static String getCodeInTag(String code, Tags tag) throws Exception {
        switch (tag) {
            case DIV_CODE_BLOCK:
                return putCodeInTags(code, DIV_CODE_BLOCK_START, DIV_FINISH);

            case DIV_CODE_CONTAINER:
                return putCodeInTags(code, DIV_CODE_CONTAINER_START, DIV_FINISH);

            case SPAN_COMMENT:
                return putCodeInTags(code, SPAN_COMMENT_START, SPAN_FINISH);

            case SPAN_DEV_COMMENT:
                return putCodeInTags(code, SPAN_DEV_COMMENT_START, SPAN_FINISH);

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


    /**
     * Method that parses code into HTML analogue.
     *
     * @param code      {@link String} that contains code to be placed into appropriate tags,
     *                  identified by {@link Tags}.
     * @param startTag  {@link String} that is used as a first part of HTML tag pair.
     *                  They are defined in {@link HTMLTags}.
     * @param finishTag {@link String} that is used as a finishing part of HTML tag pair.
     *                  They are defined in {@link HTMLTags}.
     * @return {@link String} that is parsed to be HTML code.
     */
    private static String putCodeInTags(String code, String startTag, String finishTag) {

        return startTag + code +
                finishTag;
    }

}
