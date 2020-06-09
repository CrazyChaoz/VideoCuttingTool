package at.jku.videocuttingtool.backend;


import java.util.*;

public class Timeline {
    private final List<Clip> video = new ArrayList<>();
    private final List<Clip> audio = new ArrayList<>();

    public void addVideo(Clip video) {
        this.video.add(video);
    }

    public void addAudio(Clip audio) {
        this.audio.add(audio);
    }

    public void setVideo(Set<Clip> video) {
        this.video.addAll(video);
    }

    public void setAudio(Set<Clip> audio) {
        this.audio.addAll(audio);
    }

    public List<Clip> getVideo() {
        video.sort(Clip::compareTo);
        return video;
    }

    public List<Clip> getAudio() {
        audio.sort(Clip::compareTo);
        return audio;
    }
}
