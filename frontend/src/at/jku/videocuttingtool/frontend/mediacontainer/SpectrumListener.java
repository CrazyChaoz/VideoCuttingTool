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
    private final XYChart.Data<String, Number>[] liveData;
    private final XYChart.Series<Number, Number> timeSeries = new XYChart.Series<>();

    public SpectrumListener(MediaPlayer mediaPlayer, AreaChart<String, Number> liveSpectrum, AreaChart<Number, Number> timeSpectrum) {

        mp = mediaPlayer;
        NUM_BANDS = mp.getAudioSpectrumNumBands();
        AUDIO_THRESHOLD = mp.getAudioSpectrumThreshold();
        buffer = getInitBuffer(NUM_BANDS, AUDIO_THRESHOLD);

        XYChart.Series<String, Number> liveSeries = new XYChart.Series<>();
        liveData = new XYChart.Data[NUM_BANDS];

        for (int i = 0; i < liveData.length; i++) {

            liveData[i] = new XYChart.Data<>(String.valueOf(i + 1), 0);   // initialize frequency bands on x-axis
                                                                            // and magnitudes on y-axis
            liveSeries.getData().add(liveData[i]);
        }
        liveSpectrum.getData().add(liveSeries);
        timeSpectrum.getData().add(timeSeries);
    }

    @Override
    public void spectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases) {

        for (int i = 0; i < magnitudes.length; i++) {

            if (magnitudes[i] >= buffer[i]) {
                buffer[i] = magnitudes[i];
                liveData[i].setYValue(magnitudes[i] - AUDIO_THRESHOLD);   // normalize amplitude to 0 being "no sound"

            } else {
                liveData[i].setYValue(buffer[i] - AUDIO_THRESHOLD);
                buffer[i] -= 0.2;
            }
        }

        float maxAmplitude = Integer.MIN_VALUE;
        for (float f: magnitudes) {
            if (f > maxAmplitude)
                maxAmplitude = f;
        }
        maxAmplitude -= AUDIO_THRESHOLD;
        timeSeries.getData().add(new XYChart.Data<>(timestamp, maxAmplitude));
    }

    private static float[] getInitBuffer(int size, float initValue) {

        float[] buffer = new float[size];
        Arrays.fill(buffer, initValue);
        return buffer;
    }
}
