package at.jku.videocuttingtool.backend;

import java.io.File;

/**
 * Container Class for an export
 */
public class Export {
    private final Timeline timeline;
    private final File export;
    private final String videoFormat;
    private final String audioFormat;

    /**
     * @param timeline    timeline instance which should be exported
     * @param export      the directory+filename to export to
     * @param videoFormat the format / codec of the exported video
     * @param audioFormat the format / codec for the audio of the exported video
     */
    public Export(Timeline timeline, File export, String videoFormat, String audioFormat) {
        this.timeline = timeline;
        this.export = export;
        this.videoFormat = videoFormat;
        this.audioFormat = audioFormat;
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public File getExport() {
        return export;
    }

    public String getVideoFormat() {
        return videoFormat;
    }

    public String getAudioFormat() {
        return audioFormat;
    }
}
