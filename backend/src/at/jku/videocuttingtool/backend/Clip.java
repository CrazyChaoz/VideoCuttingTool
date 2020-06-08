package at.jku.videocuttingtool.backend;

import java.io.File;

/**
 * Class for a media file,
 * which can include Timestamps [from - to].
 * The media will be cut at the specified timestamps
 */
public class Clip implements Comparable<Clip>{
    private final int pos;
    private final File media;
    private final String start;
    private final String end;

    /**
     *
     * @param media original uncut media link
     * @param pos the position of the clip in the timeline
     * @param start start time (relative to the media) of cut media
     *              hh:mm:ss.xxx
     *              if "" -> no cut in front
     * @param end length of the cut media in the timeline
     *            hh:mm:ss.xxx
     *            if "" -> whole cut media will be placed at pos
     */
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
