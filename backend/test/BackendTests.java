import at.jku.videocuttingtool.backend.Backend;
import at.jku.videocuttingtool.backend.Clip;
import at.jku.videocuttingtool.backend.Export;
import at.jku.videocuttingtool.backend.Timeline;
import org.junit.jupiter.api.Test;

import java.io.File;

public class BackendTests {

	@Test
	public void addSourcesTest(){

	}

	@Test
	public void setWorkingDirTest(){

	}

	@Test
	public void randomTest(){
		try {
			Backend b = new Backend();

			File test = new File("D:/ShadowPlay/Desktop/f1_1.mp4");
			File test2 = new File("D:/ShadowPlay/Desktop/f1_2.mp4");
			File test3 = new File("D:/ShadowPlay/Desktop/f1_3.mp4");

			Timeline tm = new Timeline();
			tm.addVideo(new Clip(test, 1));
			//tm.addVideo(new Clip(test2, 2,"",""));
			//tm.addVideo(new Clip(test3, 3,"",""));

			File audio = new File("D:/OneDrive/JKU/Multimediasysteme/audio/Inception Soundtrack HD - #12 Time (Hans Zimmer).mp3");
			//tm.addAudio(new Clip(audio, 0, "00:00:25", "00:00:35"));
			tm.addAudio(new Clip(audio, 1));

			Export ex = new Export(tm, new File("D:/ShadowPlay/Desktop/export.mp4"), "mp4", "mp3");
			b.export(ex);

			//b.saveProject(tm, new File("D:/ShadowPlay/Desktop/save.vct"));
			//b.saveProject(b.loadProject(new File("D:/ShadowPlay/Desktop/save.vct")), new File("D:/ShadowPlay/Desktop/otherSave.vct"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
