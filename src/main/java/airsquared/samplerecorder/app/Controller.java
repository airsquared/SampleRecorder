package airsquared.samplerecorder.app;

import com.jfoenix.controls.JFXButton;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.sound.sampled.LineUnavailableException;
import java.io.File;

public class Controller {

    public static final String initialPath = System.getProperty("user.home") + File.separator + "file.wav";


    @FXML private TextField pathField, sampleRate;
    @FXML public JFXButton record, play;

    private final AudioRecorder recorder = new AudioRecorder();
    private final AudioPlayer player = new AudioPlayer();

    private static final Ikon playIcon = new FontIcon("mdmz-play_circle_filled").getIconCode();
    private static final Ikon pauseIcon = new FontIcon("mdomz-pause_circle_outline").getIconCode();

    public void initialize() {
        sampleRate.setTextFormatter(new TextFormatter<>(change -> isNumeric(change.getText()) ? change : null));
        record.textProperty().bind(Bindings.when(recorder.recording).then("Stop").otherwise("Record"));
        record.disableProperty().bind(player.playing);
        play.disableProperty().bind(recorder.recording);
        ((FontIcon) play.getGraphic()).iconCodeProperty().bind(Bindings.when(player.playing).then(pauseIcon).otherwise(playIcon));
    }

    private static boolean isNumeric(String s) {
        if (s == null || s.isEmpty()) {
            return true;
        }
        for (int i = 0; i < s.length(); i++) {
            if (Character.isDigit(s.charAt(i)) || s.charAt(i) == ',') {
                // valid
            } else {
                return false;
            }
        }
        return true;
    }

    public void filePickerHandler() {
        var initialDirectory = new File(pathField.getText());
        var fileChooser = new FileChooser();
        if (initialDirectory.isFile() || pathField.getText().endsWith(".wav")) {
            fileChooser.setInitialFileName(initialDirectory.getName());
        }
        for (int i = 0; i < 20; i++) { // limit loop iterations to 20
            if (initialDirectory == null || initialDirectory.isDirectory()) {
                fileChooser.setInitialDirectory(initialDirectory);
                break;
            } else {
                initialDirectory = initialDirectory.getParentFile();
            }
        }

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Wave File", "*.wav"));
        File result = fileChooser.showSaveDialog(Main.primaryStage);
        if (result != null) {
            pathField.setText(result.getAbsolutePath());
        }
    }

    public void record() throws LineUnavailableException {
        if (recorder.isRunning()) {
            recorder.stop();
        } else {
            String path = pathField.getText();
            if (!path.endsWith(".wav")) {
                path += ".wav";
            }
            var file = new File(path);
            file.delete();
            recorder.start(file, Float.parseFloat(sampleRate.getText().replace(",", "")));
        }
    }

    public void play() {
        if (player.playing.get()) {
            player.pause();
        } else {
            player.playFile(new File(pathField.getText()));
        }
    }

}