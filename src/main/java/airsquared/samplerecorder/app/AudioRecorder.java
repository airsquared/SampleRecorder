package airsquared.samplerecorder.app;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;

public class AudioRecorder {

    public final BooleanProperty recording = new SimpleBooleanProperty();
    public LineListener listener;

    private TargetDataLine line;
    private Thread thread;

    public void start(File file, float sampleRate) throws LineUnavailableException {
        if (isRunning()) {
            stop();
        }

        Objects.requireNonNull(file);

        var format = new AudioFormat(sampleRate, 16, 1, true, false);
        var info = new DataLine.Info(TargetDataLine.class, format);

        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("No microphones found.", e);
        }
        if (listener != null) {
            line.addLineListener(listener);
        }
        line.addLineListener(event -> Platform.runLater(() -> recording.set(line.isActive())));
        line.open(format);
        line.start();

        AudioInputStream stream = new AudioInputStream(line);
        thread = new Thread(() -> {
            try {
                AudioSystem.write(stream, AudioFileFormat.Type.WAVE, file);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public boolean isRunning() {
        return recording.get();
    }

    public void stop() {
        thread.interrupt();
        line.stop();
        line.close();
    }

}
