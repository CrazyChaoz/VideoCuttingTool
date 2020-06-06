package at.jku.videocuttingtool.backend;

import java.io.File;

public class Clip implements Comparable<Clip>{
    private final int pos;
    /**
     * original uncut media link
     */
    private final File media;
    /**
     * start time (relative to the media) of cut media
     * hh:mm:ss.xxx
     * if "" -> no cut in front
     */
    private final String start;
    /**
     * length of the cut media in the timeline
     * hh:mm:ss.xxx
     * if "" -> whole cut media will be placed at pos
     */
    private final String end;

    public Clip(File media, int pos, String start, String end) {
        this.media = media;
        this.pos = pos;
        this.start = start;
        this.end = end;
    }

    @Override
    public int compareTo(Clip o) {
        return Integer.compare(pos,o.pos);
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
