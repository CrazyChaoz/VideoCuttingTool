package at.jku.videocuttingtool.backend;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Timeline {
    private final Set<Clip> video = new TreeSet<>();
    private final Set<Clip> audio = new TreeSet<>();

    public void addVideo(Clip video){
        this.video.add(video);
    }

    public void addAudio(Clip audio){
        this.audio.add(audio);
    }

    public void setVideo(List<Clip> video){
        this.video.addAll(video);
    }

    public void setAudio(List<Clip> audio){
        this.audio.addAll(audio);
    }

    public Set<Clip> getVideo() {
        return video;
    }

    public Set<Clip> getAudio() {
        return audio;
    }
}
