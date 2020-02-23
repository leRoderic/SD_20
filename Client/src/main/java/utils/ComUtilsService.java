package utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ComUtilsService {
    private ComUtils comUtils;

    public ComUtilsService(InputStream inputStream, OutputStream outputStream) throws IOException {
        comUtils = new ComUtils(inputStream, outputStream);
    }

    public void writeTest() throws IOException {

        String name = "RodrigoCabezasQuiros";
        String comment = "This sucks!";
        int age = 20;

        comUtils.write_string(name);
        comUtils.write_int32(age);
        comUtils.write_string_variable(5, comment);
        comUtils.write_char('~');

    }

    public String readTest() throws IOException{

        String result = "";
        result += "Name: " + comUtils.read_string() + "\n";
        result += "Age: " + comUtils.read_int32() + "\n";
        result += "Comment:  " + comUtils.read_string_variable(5) + "\n";
        result += "Char: " + comUtils.read_char();

        return result;
    }



}
