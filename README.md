# Code HTML Converter

## OVERVIEW:

This is a project designed to simplify the process of converting code into something that can be used in HTML documents.


## HOW TO USE:

To convert code into HTML create CodeHTMLConverter object, and call convert() method on it.
Create a text file that stores your code for parsing. Store the file under the root folder.
Specify the filename inside the parameters of the convert() method.

The return value will be the code in HTML tags.

Example:

 ```java
   CodeHTMLConverter converter = new CodeHTMLConverter();
   converter.convert("code.txt");
  ```

## HOW TO FORMAT CODE IN THE FILE:

This program uses a set of predefined Strings to identify where code entered by user ends/starts, for example.
These values can be found inside the [conversion_constants.properties file](https://github.com/adtimokhin/Code_HTML_Converter/blob/master/src/main/resources/conversion_constants.properties).

 JAVA <-- value used for specifying that code type is JAVA
 
 BREAKPOINT <-- value used for specifying that program should terminate running through the file
 
 CODE_BLOCK_START <--- value used for specifying the place where the block of code that requires translation starts
 
 CODE_BLOCK_END <--- value used for specifying the place where the block of code that requires translation ends
 
 Here are the default values for the special values:
 ```java
JAVA = "-- JAVA --"
BREAKPOINT = "--"
CODE_BLOCK_START = "BLOCKQUE.START"
CODE_BLOCK_END = "BLOCKQUE.END"
 ```


Users can change the default values to what they desire inside the file mentioned above.

In order to format the code, every special value should be placed at the start of a new line, without any spaces or any other other symbols in front.
Thus, the values chosen should be unique and must not appear at the actual code.



## WHAT CODE CAN BE PARSED:

Currently, system allows to parse only Java code.
In the future, the program will also be able to perform manipulations on XML and FTL code.


## License

[MIT](https://choosealicense.com/licenses/mit/)
