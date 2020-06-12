package at.jku.videocuttingtool.backend;

import com.sun.deploy.util.StringUtils;

import java.io.File;
import java.util.Arrays;

/**
 * Class for a media file,
 * which can include Timestamps [from - to].
 * The media will be cut at the specified timestamps
 */
public class Clip implements Comparable<Clip> {
    private final int pos;
    private final File media;
    private final String start;
    private final String end;

    /**
     * @param media original uncut media link
     * @param pos   the position of the clip in the timeline
     * @param start start time (relative to the media) of cut media
     *              hh:mm:ss.xxx
     *              if "" -> no cut in front
     * @param end   length of the cut media in the timeline
     *              hh:mm:ss.xxx
     *              if "" -> whole cut media will be placed at pos
     */
    public Clip(File media, int pos, String start, String end) {
        this.media = media;
        this.pos = pos;
        this.start = start;
        this.end = end;
    }

    /**
     *
     * @param media original uncut media link
     * @param pos   the position of the clip in the timeline
     */
    public Clip(File media, int pos){
        this.media = media;
        this.pos = pos;
        this.start = "";
        this.end = "";
    }

    @Override
    public String toString() {
        return StringUtils.join(Arrays.asList(
                "" + pos,
                media.getAbsolutePath(),
                start,
                end
        ), ";");
    }

    @Override
    public int compareTo(Clip o) {
        return Integer.compare(pos, o.pos);
    }

    public static Clip parse(String from) {
        String[] split = from.split(";");
        String start = "", end = "";

        if (split.length < 2) {
            return null;
        } else if (split.length == 3) {
            start = split[2];
        } else if (split.length == 4) {
            start = split[2];
            end = split[3];
        }

        return new Clip(
                new File(split[1]),
                Integer.parseInt(split[0]),
                start,
                end
        );
    }

    public int getPos() {
        return pos;
    }

    public File getMedia() {
        return media;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }


}
