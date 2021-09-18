package com.adtimokhin;

import com.adtimokhin.htmlProperties.HTMLTags;
import com.adtimokhin.htmlProperties.Tags;
import com.adtimokhin.javaProperties.JavaKeyWords;

import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.util.Stack;

import static com.adtimokhin.htmlProperties.HTMLTags.BR_TAG;

/**
 * This is a class that reads text from a file and then converts it to html with all proper tags,
 * so that I don't need to do that
 */
public class CodeHTMLConverter {

    private static final FileReader fileReader = new FileReader();

    private static final String JAVA = "-- JAVA --";
    private static final String BREAKPOINT = "--";
    private static final String CODE_BLOCK_START = "BLOCKQUE.START";
    private static final String CODE_BLOCK_END = "BLOCKQUE.END";


    public static String convert(String textFile) throws Exception {
        String code = "";
        fileReader.openConnection(textFile);

        String codeType = fileReader.readline();

        if (JAVA.equals(codeType)) {
            code = convertJava();
        }

        fileReader.closeConnection();

        return code;
    }


    private static String convertJava() throws Exception {
        StringBuilder generalStringBuilder = new StringBuilder();
        String nextLine = fileReader.readline();

        if (!CODE_BLOCK_START.equals(nextLine)) {
            throw new Exception("Block of code must start with:\n" + CODE_BLOCK_START);
        } else {
            nextLine = fileReader.readline();
        }

        StringBuilder multiLineCommentStringBuilder = null;
        boolean devComment = false;
        while (!CODE_BLOCK_END.equals(nextLine)) {
            StringBuilder localStringBuilder = new StringBuilder();
            char[] chars = nextLine.toCharArray();

            // entering all spaces that come at the start of the line
            int i = 0;
            for (char aChar : chars) {
                if (aChar == " ".toCharArray()[0]) {
//                    localStringBuilder.append("&#160;");
                    i++;
                } else {
                    break;
                }
            }


            // figuring out all of the key-words and stuff:

            // Step 1 - remove spaces calculated before
            nextLine = nextLine.substring(i);

            // Multi-line comments
            // dev-comments
            if ((nextLine.startsWith("/**") || nextLine.contains("/**")) && multiLineCommentStringBuilder == null) {
                multiLineCommentStringBuilder = new StringBuilder();
                if (i != 0) {
                    for (int j = 0; j < i; j++) {
                        multiLineCommentStringBuilder.append("&#160;");
                    }
                }
                multiLineCommentStringBuilder.append(nextLine);
                multiLineCommentStringBuilder.append(BR_TAG);
                nextLine = fileReader.readline();
                devComment = true;
                continue;
            } else if ((nextLine.startsWith("**/") || nextLine.contains("**/")) && multiLineCommentStringBuilder != null && devComment) {
                if (i != 0) {
                    for (int j = 0; j < i; j++) {
                        multiLineCommentStringBuilder.append("&#160;");
                    }
                }
                multiLineCommentStringBuilder.append(nextLine);
                localStringBuilder.append(HTMLTags.getCodeInTag(multiLineCommentStringBuilder.toString(), Tags.SPAN_DEV_COMMENT));
                multiLineCommentStringBuilder = null;
            } else if ((nextLine.startsWith("/*") || nextLine.contains("/*")) && multiLineCommentStringBuilder == null) {
                multiLineCommentStringBuilder = new StringBuilder();
                if (i != 0) {
                    for (int j = 0; j < i; j++) {
                        multiLineCommentStringBuilder.append("&#160;");
                    }
                }
                multiLineCommentStringBuilder.append(nextLine);
                multiLineCommentStringBuilder.append(BR_TAG);
                nextLine = fileReader.readline();
                devComment = false;
                continue;
            } else if ((nextLine.startsWith("*/") || nextLine.contains("*/")) && multiLineCommentStringBuilder != null && !devComment && !nextLine.contains("**/")) {
                if (i != 0) {
                    for (int j = 0; j < i; j++) {
                        multiLineCommentStringBuilder.append("&#160;");
                    }
                }
                multiLineCommentStringBuilder.append(nextLine);
                localStringBuilder.append(HTMLTags.getCodeInTag(multiLineCommentStringBuilder.toString(), Tags.SPAN_COMMENT));
                multiLineCommentStringBuilder = null;
            } else if (multiLineCommentStringBuilder != null) {
                if (i != 0) {
                    for (int j = 0; j < i; j++) {
                        multiLineCommentStringBuilder.append("&#160;");
                    }
                }
                multiLineCommentStringBuilder.append(nextLine);
                multiLineCommentStringBuilder.append(BR_TAG);
                nextLine = fileReader.readline();
                continue;
            } else {
                if (i != 0) {
                    for (int j = 0; j < i; j++) {
                        localStringBuilder.append("&#160;");
                    }
                }
                //Step 2 - find keywords
                // () и . - знаки, которые показывают, что в строке нужно более детальное рассмотрение.
                if (!nextLine.contains("().")) {
                    localStringBuilder = convertOneLineWithNoSpecialSymbols(nextLine, localStringBuilder);
                }
            }

            //final step - put everything into a standard <p></p> tags
            generalStringBuilder.append(HTMLTags.getCodeInTag(localStringBuilder.toString(), Tags.P_TAG));

            nextLine = fileReader.readline();
        }

        nextLine = fileReader.readline();
        if (BREAKPOINT.equals(nextLine)) {
            return HTMLTags.getCodeInTag(HTMLTags.getCodeInTag(generalStringBuilder.toString(), Tags.DIV_CODE_BLOCK), Tags.DIV_CODE_CONTAINER);
        } else {
            throw new Exception("Ты что, дурачок? Почему твой блок не заканчивается " + BREAKPOINT + " ?");
        }


    }

    private static StringBuilder convertOneLineWithNoSpecialSymbols(String nextLine, StringBuilder localStringBuilder) throws Exception {
        // normal line with no brackets or stops.
        String[] words = nextLine.split(" ");// split into words
        StringBuilder StringInCode = null;
        boolean isInString = false;
        int wordDifference = 0;
        for (int j = 0; j < words.length; j++) {
            int l = j + wordDifference;
            if (!(l < words.length)) {
                break;
            }
            String word = words[l];
            boolean isEnd = false;
            if (j == words.length - 1 && word.endsWith(";")) {
                word = word.replace(";", "");
                isEnd = true;
            }


            if (word.contains("(") && !isInString) {
                // step 1 - get line inside brackets
                char[] chars = nextLine.toCharArray();
                int posStart = -1;
                int posEnd = -1;
                int i = 0;
                // finding the right-most (
                while (posStart == -1) {
                    if (chars[i] == '(') {
                        posStart = i;
                        i = 0;
                    } else {
                        i++;
                    }
                }

                // finding the left-most )
                while (posEnd == -1) {
                    if (chars[i] == ')') {
                        posEnd = i;
                    } else {
                        i++;
                    }
                }
                localStringBuilder.append(word.split("\\(")[0]).append("(");
                String stringInBrackets = nextLine.substring(posStart+1, posEnd + 1);
                localStringBuilder.append(convertOneLineWithNoSpecialSymbols(stringInBrackets, new StringBuilder()).toString());

                // now we need to start looking at the words that are left after the brackets
                String wordsAfterBrackets[] = (nextLine.substring(posEnd)).split(" ");

                if(wordsAfterBrackets.length == 1){
                    localStringBuilder.append(convertOneLineWithNoSpecialSymbols(wordsAfterBrackets[0], localStringBuilder));
                }
                    wordDifference = words.length - wordsAfterBrackets.length;

            }
            else if (word.startsWith("@")) {//i.e. it is a annotation
                localStringBuilder.append(HTMLTags.getCodeInTag(word, Tags.SPAN_ANNOTATION));
            } else if (JavaKeyWords.isKeyWord(word)) {// i.e. it is a key word
                localStringBuilder.append(HTMLTags.getCodeInTag(word, Tags.SPAN_KEY_WORD));
                localStringBuilder.append(" ");
            } else if (word.startsWith("//") || word.contains("//")) {// i.e. the rest of the line is comment
                StringBuilder restOfLine = new StringBuilder();
                for (int k = j; k < words.length; k++) {
                    restOfLine.append(words[k]);
                    if (k != words.length - 1) {
                        restOfLine.append(" ");
                    }
                }
                localStringBuilder.append(HTMLTags.getCodeInTag(restOfLine.toString(), Tags.SPAN_COMMENT));
                break;
            } else if (word.startsWith("\"")) {// i.e. it is a String
                StringInCode = new StringBuilder();
                StringInCode.append(word);

                if (word.endsWith("\"")) {
                    localStringBuilder.append(HTMLTags.getCodeInTag(StringInCode.toString(), Tags.SPAN_STRING));
                    StringInCode = null;
                    isInString = false;
                } else {
                    StringInCode.append(" ");
                    isInString = true;
                }
            } else if (word.endsWith("\"") && StringInCode != null) {
                StringInCode.append(word);
                localStringBuilder.append(HTMLTags.getCodeInTag(StringInCode.toString(), Tags.SPAN_STRING));
                StringInCode = null;
                isInString = false;
            } else if (isInString) {
                if (StringInCode == null) {
                    throw new Exception("This is an internal server error caused by the fact that you haven't began a String");
                }
                StringInCode.append(word);
                StringInCode.append(" ");
            } else {// i.e. not a special symbol
                localStringBuilder.append(word);
                localStringBuilder.append(" ");
            }
            if (isEnd) {
                localStringBuilder.append(HTMLTags.getCodeInTag(";", Tags.SPAN_KEY_WORD));
            }


        }

        return localStringBuilder;
    }
}
