package at.jku.videocuttingtool.frontend.mediacontainer;

import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.MediaPlayer;

import java.util.Arrays;

public class SpectrumListener implements AudioSpectrumListener {

    private final int NUM_BANDS;        // number of frequency bands
    private final int AUDIO_THRESHOLD;  // threshold for audio magnitude
    private final MediaPlayer mp;
    private final float[] buffer;       // buffer to make transitions (in time) smoother
    private final XYChart.Data<String, Number>[] data;

    public SpectrumListener(MediaPlayer mediaPlayer, AreaChart<String, Number> spectrum) {

        mp = mediaPlayer;
        NUM_BANDS = mp.getAudioSpectrumNumBands();
        AUDIO_THRESHOLD = mp.getAudioSpectrumThreshold();
        buffer = getInitBuffer(NUM_BANDS, AUDIO_THRESHOLD);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        data = new XYChart.Data[NUM_BANDS];

        for (int i = 0; i < data.length; i++) {

            data[i] = new XYChart.Data<>(String.valueOf(i + 1), 0);   // initialize frequency bands on x-axis
                                                                            // and magnitudes on y-axis
            series.getData().add(data[i]);
        }
        spectrum.getData().add(series);
    }

    @Override
    public void spectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases) {

        for (int i = 0; i < magnitudes.length; i++) {

            if (magnitudes[i] >= buffer[i]) {
                buffer[i] = magnitudes[i];
                data[i].setYValue(magnitudes[i] - AUDIO_THRESHOLD);   // normalize amplitude to 0 being "no sound"

            } else {
                data[i].setYValue(buffer[i] - AUDIO_THRESHOLD);
                buffer[i] -= 0.2;
            }
        }
    }

    private static float[] getInitBuffer(int size, float initValue) {

        float[] buffer = new float[size];
        Arrays.fill(buffer, initValue);
        return buffer;
    }
}
