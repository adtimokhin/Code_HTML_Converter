package com.adtimokhin;

public class Main {

    public static void main(String[] args) throws Exception {
        CodeHTMLConverter converter = new CodeHTMLConverter();
        System.out.println(converter.convert("code.txt"));
    }
}
