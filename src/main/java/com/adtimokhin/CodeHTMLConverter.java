package com.adtimokhin;

import com.adtimokhin.htmlProperties.HTMLTags;
import com.adtimokhin.htmlProperties.Tags;
import com.adtimokhin.javaProperties.JavaKeyWords;
import com.sun.istack.internal.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static com.adtimokhin.htmlProperties.HTMLSpecialSymbols.GREATER_THAN_SIGN;
import static com.adtimokhin.htmlProperties.HTMLSpecialSymbols.LESS_THAN_SIGN;
import static com.adtimokhin.htmlProperties.HTMLSpecialSymbols.SPACE_SIGN;
import static com.adtimokhin.htmlProperties.HTMLTags.BR_TAG;

/**
 * This is a class that reads text from a file and then converts it to HTML with all proper tags,
 * so that I don't need to do that.
 */
public class CodeHTMLConverter {

    private static final FileReader fileReader = new FileReader();


    /**
     * Constants used to identify what special symbols are used to identify special actions.
     **/
    private final String JAVA; // MARKS CODE AS JAVA CODE

    private final String BREAKPOINT; // MARKS THE END OF CODE RUNNING

    private final String CODE_BLOCK_START; // MARKS THE START OF CODE BLOCK.
                                          // WHERE THE ACTUAL CODE THAT WILL BE GROUPED TOGETHER STARTS

    private final String CODE_BLOCK_END; // MARKS THE END OF CODE BLOCK.
                                        // WHERE THE ACTUAL CODE THAT WILL BE GROUPED TOGETHER ENDS


    /**
     * Loads properties into the {@link CodeHTMLConverter} object for later use in code.
     *
     * @throws IOException if there was an error with getting properties from
     *                     <a href= "java_conversion.properties"> file </a>.
     */
    public CodeHTMLConverter() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("java_conversion.properties"));

        // setting properties
        JAVA = properties.getProperty("JAVA");
        BREAKPOINT = properties.getProperty("BREAKPOINT");
        CODE_BLOCK_START = properties.getProperty("CODE_BLOCK_START");
        CODE_BLOCK_END = properties.getProperty("CODE_BLOCK_END");
    }


    /**
     * Only method that user can communicate with this class.
     * It is used to convert Java/XML/FTL code to HTML.
     * <p>
     * FIXME: 18.09.2021 Right now only Java code parsing is functioning.
     *
     * @param textFile name of text file that contains java code to convert.
     * @return converted to HTML Java code.
     * @throws Exception if any errors occur during parsing the java code in file from {@param textFile}.
     */
    public String convert(String textFile) throws Exception {
        String code = "";
        fileReader.openConnection(textFile);

        String codeType = fileReader.readLine();

        if (JAVA.equals(codeType)) {
            code = convertJava();
        }

        fileReader.closeConnection();

        return code;
    }


    /**
     * Internal method used for parsing Java code into HTML.
     *
     * @return converted to HTML Java code.
     * @throws Exception if any errors occur during parsing the java code.
     */
    private String convertJava() throws Exception {
        StringBuilder generalStringBuilder = new StringBuilder();
        String nextLine = fileReader.readLine();

        if (!CODE_BLOCK_START.equals(nextLine)) {
            throw new Exception("Block of code must start with:\n" + CODE_BLOCK_START);
        } else {
            nextLine = fileReader.readLine();
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
                        multiLineCommentStringBuilder.append(SPACE_SIGN);
                    }
                }
                multiLineCommentStringBuilder.append(nextLine);
                multiLineCommentStringBuilder.append(BR_TAG);
                nextLine = fileReader.readLine();
                devComment = true;
                continue;
            } else if ((nextLine.startsWith("**/") || nextLine.contains("**/")) && multiLineCommentStringBuilder != null && devComment) {
                if (i != 0) {
                    for (int j = 0; j < i; j++) {
                        multiLineCommentStringBuilder.append(SPACE_SIGN);
                    }
                }
                multiLineCommentStringBuilder.append(nextLine);
                localStringBuilder.append(HTMLTags.getCodeInTag(multiLineCommentStringBuilder.toString(), Tags.SPAN_DEV_COMMENT));
                multiLineCommentStringBuilder = null;
            } else if ((nextLine.startsWith("/*") || nextLine.contains("/*")) && multiLineCommentStringBuilder == null) {
                multiLineCommentStringBuilder = new StringBuilder();
                if (i != 0) {
                    for (int j = 0; j < i; j++) {
                        multiLineCommentStringBuilder.append(SPACE_SIGN);
                    }
                }
                multiLineCommentStringBuilder.append(nextLine);
                multiLineCommentStringBuilder.append(BR_TAG);
                nextLine = fileReader.readLine();
                devComment = false;
                continue;
            } else if ((nextLine.startsWith("*/") || nextLine.contains("*/")) && multiLineCommentStringBuilder != null && !devComment && !nextLine.contains("**/")) {
                if (i != 0) {
                    for (int j = 0; j < i; j++) {
                        multiLineCommentStringBuilder.append(SPACE_SIGN);
                    }
                }
                multiLineCommentStringBuilder.append(nextLine);
                localStringBuilder.append(HTMLTags.getCodeInTag(multiLineCommentStringBuilder.toString(), Tags.SPAN_COMMENT));
                multiLineCommentStringBuilder = null;
            } else if (multiLineCommentStringBuilder != null) {
                if (i != 0) {
                    for (int j = 0; j < i; j++) {
                        multiLineCommentStringBuilder.append(SPACE_SIGN);
                    }
                }
                multiLineCommentStringBuilder.append(nextLine);
                multiLineCommentStringBuilder.append(BR_TAG);
                nextLine = fileReader.readLine();
                continue;
            } else {
                if (i != 0) {
                    for (int j = 0; j < i; j++) {
                        localStringBuilder.append(SPACE_SIGN);
                    }
                }
                //Step 2 - find keywords
                localStringBuilder = convertLine(nextLine, localStringBuilder, " ");
            }

            //final step - put everything into a standard <p></p> tags
            generalStringBuilder.append(HTMLTags.getCodeInTag(localStringBuilder.toString(), Tags.P_TAG));

            nextLine = fileReader.readLine();
        }

        nextLine = fileReader.readLine();
        if (BREAKPOINT.equals(nextLine)) {
            return HTMLTags.getCodeInTag(HTMLTags.getCodeInTag(generalStringBuilder.toString(), Tags.DIV_CODE_BLOCK), Tags.DIV_CODE_CONTAINER);
        } else {
            throw new Exception("Your code segment must end with: " + BREAKPOINT);
        }


    }

    /**
     * This method turns one line of Java code into its HTML analogue.
     * <p>
     * This method identifies keywords inside {@link Tags} and uses special annotation to identify them.
     * This method will perform the same kind of search for annotations, line comments and Strings.
     * <p>
     * This method will also identify any keywords, annotations and Strings inside the line if they are separated by '.'
     * or inside brackets.
     * <p>
     * <p>
     * FIXME: 18.09.2021 Current system only works if String that is stored inside line, separated by .'s, is written all in one word.
     *
     * @param line               {@link String} one line of Java code to convert to HTML.
     * @param localStringBuilder {@link StringBuilder} that contains data associated with all spaces in the {@param line}.
     * @param separator          {@link String} a symbol combination used to indicate where new word begins. By default,
     *                           this value should be set to " ". But it also supports "\\.".
     * @return StringBuilder containing a single line of code converted to HTML.
     * @throws Exception is thrown if {@param line} has any errors.
     **/
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

                if ((word.endsWith("\"") && !word.endsWith("\\\"")) || (word.endsWith("\'") && !word.endsWith("\\\'"))) {
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
                } else if (word.startsWith("\"") || word.startsWith("\'")) { // it is a String
                    if ((word.endsWith("\"") && !word.endsWith("\\\"")) || (word.endsWith("\'") && !word.endsWith("\\\'"))) {
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
                    localStringBuilder.append(getCodeFormattedFromSpecialSymbols(word));
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


    /**
     * This method takes some String and replaces all special symbols to their HTML analogue, ensuring safe conversion
     * from Java to HTML.
     * <p>
     * Currently, system works only for < and > symbols.
     *
     * @param code this is a piece of code that is about to go into HTML tags.
     * @return reformatted code that has all special symbols changed to appropriate HTML values.
     **/
    @NotNull
    private static String getCodeFormattedFromSpecialSymbols(@NotNull String code) {
        char[] chars = code.toCharArray();
        StringBuilder codeRebuilt = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '<') {
                codeRebuilt.append(LESS_THAN_SIGN);
            } else if (chars[i] == '>') {
                codeRebuilt.append(GREATER_THAN_SIGN);
            } else {
                codeRebuilt.append(chars[i]);
            }
        }

        return codeRebuilt.toString();
    }
}
