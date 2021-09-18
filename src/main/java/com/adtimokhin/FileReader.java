package com.adtimokhin;

import java.io.BufferedReader;
import java.io.IOException;

public class FileReader {
    private BufferedReader bufferedReader;

    public void openConnection(String filename) throws IOException {
        bufferedReader = new BufferedReader(new java.io.FileReader(filename));
    }

    public String readline() throws IOException {
        return bufferedReader.readLine();
    }

    public void closeConnection() throws IOException {
        bufferedReader.close();
    }
}

