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

            if (nextLine.equals("")) {
                generalStringBuilder.append(BR_TAG);
            }

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
                    localStringBuilder = convertLine(nextLine, localStringBuilder, " ");
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

    // Depreciated :)
    private static StringBuilder convertOneLineWithNoSpecialSymbols(String nextLine, StringBuilder localStringBuilder) throws Exception {
        // normal line with no brackets or stops.
        String[] words = nextLine.split(" ");// split into words
        if (words.length == 0) {//i.e. this is just an empty line
            return new StringBuilder(BR_TAG);
        }
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
            if (word.endsWith(";")) {
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
                String stringInBrackets = nextLine.substring(posStart + 1, posEnd + 1);
                localStringBuilder.append(convertOneLineWithNoSpecialSymbols(stringInBrackets, new StringBuilder()).toString());

                // now we need to start looking at the words that are left after the brackets
                String wordsAfterBrackets[] = (nextLine.substring(posEnd)).split(" ");

                if (wordsAfterBrackets.length == 1) {
                    localStringBuilder.append(convertOneLineWithNoSpecialSymbols(wordsAfterBrackets[0], localStringBuilder));
                }
                wordDifference = words.length - wordsAfterBrackets.length;

            } else if (word.startsWith("@")) {//i.e. it is a annotation
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


    // If you put a String into a line that is split by .'s, then make sure that the String is all in one word.
    private static StringBuilder convertLine(String line, StringBuilder localStringBuilder, String separator) throws Exception {
        // right now we make the following assumptions:
        // a single line does not use any words that are grouped either by brackets or by fullstops.

        // Step 1: break the line into words
        String[] words = line.split(separator);
        if (words.length == 0) {
            return localStringBuilder.append(BR_TAG);
        }

        if (separator.equals("\\.")) {
            separator = ".";
        }

        // Step 2: Go through every word and put everything into appropriate tags.
        boolean endsWithSemiColumn = false;
        boolean containsLineComment = false;
        boolean isString = false;
        boolean isWordThatIsSeparatedByDots = false;
        boolean isDataInBrackets = false;
        StringBuilder lineCommentBuilder = new StringBuilder();
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder bracketsStringBuilder = new StringBuilder();
        int numberOfSeparators = 0;
        for (int i = 0; i < words.length; i++) {
            // We need to test every word
            int k = i + numberOfSeparators;
            if (!(k < words.length)) {
                return localStringBuilder;
            }
            String word = words[k];

            if (containsLineComment) { // if at some point line had // the rest of the line is considered to be a comment
                lineCommentBuilder.append(word);
                lineCommentBuilder.append(separator);
                continue;
            }

            if (word.endsWith(";")) { // if word ends with semi column we will remove it and treat them separately
                word = word.replace(";", "");
                endsWithSemiColumn = true;
            }

            if (isString) { // if we have found " in the line, we assume that there is a String. We will go through words until we find a pairing "
                stringBuilder.append(word);

                if (word.endsWith("\"") && !word.endsWith("\\\"")) {
                    isString = false;
                    localStringBuilder.append(HTMLTags.getCodeInTag(stringBuilder.toString(), Tags.SPAN_STRING));
                    if (!endsWithSemiColumn) {
                        localStringBuilder.append(" ");
                    } else {
                        localStringBuilder.append(HTMLTags.getCodeInTag("; ", Tags.SPAN_KEY_WORD));
                        endsWithSemiColumn = false;
                    }
                } else {
                    stringBuilder.append(" ");

                }
                continue;
            }

            if (word.contains("(")) {
                // this means that we need to extract words that are inside the brackets

                // Step 1: Find the closing bracket
                char[] chars = line.toCharArray();// todo: here is a great area for the optimisation; though, I have no time to do it now.
                int positionOfOpeningBracket = -1;
                int positionOfClosingBracket = -1;
                numberOfSeparators = 0;
                char sep = separator.charAt(separator.length() - 1);
                for (int j = 0; j < chars.length; j++) {
                    if (chars[j] == '(' && positionOfOpeningBracket == -1) {
                        positionOfOpeningBracket = j;
                    } else if (chars[j] == ')') {
                        positionOfClosingBracket = j;
                    }
                }

                for (int j = positionOfOpeningBracket; j < positionOfClosingBracket; j++) {
                    if (chars[j] == sep) {
                        numberOfSeparators++;
                    }
                }
//                numberOfSeparators -= (i);
                if (positionOfClosingBracket == -1 || positionOfOpeningBracket == -1) {
                    throw new Exception("This system has to have \')\' at the end of the same line where you have your \'(\'");
                }

                isDataInBrackets = true;
                String dataInBrackets = line.substring(positionOfOpeningBracket + 1, positionOfClosingBracket);
                bracketsStringBuilder = convertLine(dataInBrackets, bracketsStringBuilder, separator);
                bracketsStringBuilder.deleteCharAt(bracketsStringBuilder.length() - 1);

                word = word.split("\\(")[0];
            }

            if (word.contains(".") && !word.contains(". ")) {
                // if word contains '.' then it is made out of multiple words, and hence every
                // single one of them shall be treated separately
                localStringBuilder.append(convertLine(word, new StringBuilder(), "\\."));
                isWordThatIsSeparatedByDots = true;

            }


            if (!isWordThatIsSeparatedByDots) {
                // check what the word is equal to
                if (word.startsWith("@")) { // it is an annotation
                    localStringBuilder.append(HTMLTags.getCodeInTag(word, Tags.SPAN_ANNOTATION));
                    localStringBuilder.append(separator);
                } else if (JavaKeyWords.isKeyWord(word)) {// it is a key word
                    localStringBuilder.append(HTMLTags.getCodeInTag(word, Tags.SPAN_KEY_WORD));
                    localStringBuilder.append(separator);
                } else if (word.startsWith("//")) { // it is a single line comment
                    containsLineComment = true;
                    lineCommentBuilder.append(word);
                    lineCommentBuilder.append(" ");
                } else if (word.startsWith("\"")) { // it is a String
                    if (word.endsWith("\"") && !word.endsWith("\\\"")) {
                        localStringBuilder.append(HTMLTags.getCodeInTag(word, Tags.SPAN_STRING));
                        if (!endsWithSemiColumn) {
                            localStringBuilder.append(separator);
                        }
                    } else {
                        isString = true;
                        stringBuilder.append(word);
                        stringBuilder.append(" ");
                    }
                } else { // it is a common words
                    localStringBuilder.append(getCodeFormatedFromSpecialSymbols(word));
                    if (!endsWithSemiColumn) {
                        localStringBuilder.append(separator);
                    }
                }
            } else {
                isWordThatIsSeparatedByDots = false;
            }
            // at the end, if we need to add semi column we need to add it:
            if (isDataInBrackets) {
                localStringBuilder.append("(");
                localStringBuilder.append(bracketsStringBuilder.toString());
                localStringBuilder.append(")");
                localStringBuilder.append(separator);
                isDataInBrackets = false;
            }
            if (endsWithSemiColumn) {
                localStringBuilder.append(HTMLTags.getCodeInTag("; ", Tags.SPAN_KEY_WORD));
                endsWithSemiColumn = false;
            }
        }
        // if there was a comment at the end of the line, we need to add it.
        if (containsLineComment) {
            localStringBuilder.append(HTMLTags.getCodeInTag(lineCommentBuilder.toString(), Tags.SPAN_COMMENT));
        }

        if (separator.equals(".")) {
            localStringBuilder.deleteCharAt(localStringBuilder.length() - 1); // we do not want our line that is separated by . to end with a dot
        }
        return localStringBuilder;
    }


    private static String getCodeFormatedFromSpecialSymbols(String code){
        char[] chars = code.toCharArray();
        StringBuilder codeRebuilt = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if(chars[i] == '<'){
                codeRebuilt.append("&#60;");
            }else if(chars[i] == '>' ){
                codeRebuilt.append("&#62;");
            }
            else {
                codeRebuilt.append(chars[i]);
            }
        }

        return codeRebuilt.toString();
    }
}
