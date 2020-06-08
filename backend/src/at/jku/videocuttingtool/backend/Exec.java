package at.jku.videocuttingtool.backend;

/**
 * Class to build a ffmpeg command
 */
public class Exec {
    private final String ffmpeg;
    private final StringBuilder build = new StringBuilder();

    /**
     *
     * @param ffmpeg location of the ffmpeg.exe
     */
    public Exec(String ffmpeg) {
        this.ffmpeg = ffmpeg;
        build.append(ffmpeg);
    }

    /**
     * add an input to the command
     * @param in the input file /file location
     * @return the new instance of this command object
     */
    public Exec addInput(String in){
        build.append(" -i \"").append(in).append('\"');
        return this;
    }

    /**
     * add an parameter to the ffmpeg command
     * @param arg parameter, that can be only one, but also multiple sequential ones
     * @return the new instance of this command object
     */
    public Exec addArg(String arg){
        build.append(' ').append(arg);
        return this;
    }

    /**
     * add a output file to the command
     * @param out the file /file location
     * @return the new instance of this command object
     */
    public Exec addOutput(String out){
        build.append(" \"").append(out).append('\"');
        return this;
    }

    /**
     * get the completed command
     * @return the completed command as a String
     */
    public String done(){
        return build.toString();
    }
}
