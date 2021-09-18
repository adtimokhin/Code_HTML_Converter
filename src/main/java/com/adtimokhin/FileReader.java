package com.adtimokhin;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Simple Class that is used to read data from text files containing java/xml/ftl code.
 * <p>
 * This class is used by {@link CodeHTMLConverter} and thus does not require user to interact with it.
 */
public class FileReader {
    private BufferedReader bufferedReader;

    /**
     * Opens connection with file mentioned in the parameters.
     *
     * @param filename {@link String} identifies name of the file that contains contains to read.
     * @throws IOException if the method fails to establish connection with the file.
     */
    public void openConnection(String filename) throws IOException {
        bufferedReader = new BufferedReader(new java.io.FileReader(filename));
    }

    /**
     * Reads the line from the file with which connection was established in {@link #openConnection(String)}.
     *
     * @return {@link String} that equals to the line of the file.
     * @throws IOException if the method fails to read new line.
     */
    public String readLine() throws IOException {
        return bufferedReader.readLine();
    }

    /**
     * Closes connection with the file after all manipulations are performed.
     * Should be called after the {@link #openConnection(String)}.
     *
     * @throws IOException if the method fails to close connection.
     */
    public void closeConnection() throws IOException {
        bufferedReader.close();
    }
}

