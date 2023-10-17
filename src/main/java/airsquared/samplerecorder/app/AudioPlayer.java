package airsquared.samplerecorder.app;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

public class AudioPlayer {
    public final BooleanProperty playing = new SimpleBooleanProperty();

    private final Clip clip;
    private File lastFile;

    public AudioPlayer() {
        try {
            clip = AudioSystem.getClip();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        clip.addLineListener(event -> playing.set(clip.isActive()));
    }

    public void playFile(File audioFile) {
        if (clip.isActive()) {
            clip.stop();
        }
        if (audioFile.equals(lastFile)) {
            clip.setFramePosition(0);
            clip.start();
            return;
        }
        try {
            clip.open(AudioSystem.getAudioInputStream(audioFile));
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            throw new RuntimeException(e);
        }
        clip.start();
        lastFile = audioFile;
    }

    public void pause() {
        clip.stop();
    }

    public void toggle() {
        if (clip.isActive()) {
            clip.stop();
        } else {
            clip.start();
        }
    }

}
