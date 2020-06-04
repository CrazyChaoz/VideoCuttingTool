package at.jku.videocuttingtool.backend;

import javafx.scene.media.Media;

public class Clip {
    /**
     * original uncut media link
     */
    private final Media media;
    /**
     * start time (relative to the media) of cut media
     * if 0 -> no cut in front
     */
    private final long start;
    /**
     * end time (relative to the media) of cut media
     * if 0 -> no cut in back
     */
    private final long fin;
    /**
     * position of the cut media in the timeline
     */
    private final long pos;
    /**
     * length of the cut media in the timeline
     * if 0 -> whole cut media will be placed at pos
     */
    private final long length;

    public Clip(Media media, long start, long fin ,long pos, long length) {
        this.media = media;
        this.start = start;
        this.fin = fin;
        this.pos = pos;
        this.length = length;
    }

    public Media getMedia() {
        return media;
    }

    public long getStart() {
        return start;
    }

    public long getFin() {
        return fin;
    }

    public long getPos() {
        return pos;
    }

    public long getLength() {
        return length;
    }
}
