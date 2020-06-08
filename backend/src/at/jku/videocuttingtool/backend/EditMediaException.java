package at.jku.videocuttingtool.backend;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class EditMediaException extends Exception{
    private final String msg;

    public EditMediaException(InputStream errorStream){
        BufferedReader err = new BufferedReader(new InputStreamReader(errorStream));
        this.msg = err.lines().collect(Collectors.joining());
    }

    public String getMsg() {
        return msg;
    }
}
