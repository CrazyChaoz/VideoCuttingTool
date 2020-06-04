package at.jku.videocuttingtool.backend;

import javafx.scene.media.Media;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Streaming {
    public static FileInputStream toStream(Media media) throws IOException {
        return new FileInputStream(new File(media.getSource()));
    }
}
