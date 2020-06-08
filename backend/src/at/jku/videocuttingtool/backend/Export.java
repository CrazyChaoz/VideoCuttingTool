package at.jku.videocuttingtool.backend;

import java.io.File;

/**
 * Container Class for an export
 */
public class Export {
    private final Timeline timeline;
    private final File exportDir;
    private final String exportName;
    private final String videoFormat;
    private final String audioFormat;

    /**
     *
     * @param timeline timeline instance which should be exported
     * @param exportDir the directory to export to
     * @param exportName the name of the final exported file
     * @param videoFormat the format / codec of the exported video
     * @param audioFormat the format / codec for the audio of the exported video
     */
    public Export(Timeline timeline, File exportDir, String exportName, String videoFormat, String audioFormat) {
        this.timeline = timeline;
        this.exportDir = exportDir;
        this.exportName = exportName;
        this.videoFormat = videoFormat;
        this.audioFormat = audioFormat;
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public File getExportDir() {
        return exportDir;
    }

    public String getExportName() {
        return exportDir+"\\"+exportName;
    }

    public String getVideoFormat() {
        return videoFormat;
    }

    public String getAudioFormat() {
        return audioFormat;
    }
}
