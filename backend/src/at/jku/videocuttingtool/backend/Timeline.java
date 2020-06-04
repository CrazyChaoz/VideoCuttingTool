package at.jku.videocuttingtool.backend;

import java.util.ArrayList;
import java.util.List;

public class Timeline {
    /**
     * first video timeline
     */
    private final List<Clip> video = new ArrayList<>();
    /**
     * first audio timeline
     */
    private final List<Clip> audio = new ArrayList<>();

    // TODO maybe more than 1 timeline for video & audio


    public void setVideo(List<Clip> video){
        this.video.addAll(video);
    }

    public void setAudio(List<Clip> audio){
        this.audio.addAll(audio);
    }

    public List<Clip> getVideo() {
        return video;
    }

    public List<Clip> getAudio() {
        return audio;
    }
}
