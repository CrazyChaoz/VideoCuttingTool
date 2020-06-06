package at.jku.videocuttingtool.backend;

public class Export {
    private final Timeline timeline;
    private final String exportDir;
    private final String exportName;
    private final String videoCodec;
    private final String audioCodec;

    public Export(Timeline timeline, String exportDir, String exportName, String videoCodec, String audioCodec) {
        this.timeline = timeline;
        this.exportDir = exportDir;
        this.exportName = exportName;
        this.videoCodec = videoCodec;
        this.audioCodec = audioCodec;
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public String getExportDir() {
        return exportDir;
    }

    public String getExportName() {
        return exportName;
    }

    public String getVideoCodec() {
        return videoCodec;
    }

    public String getAudioCodec() {
        return audioCodec;
    }
}
