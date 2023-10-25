package airsquared.samplerecorder.app;

import fr.brouillard.oss.cssfx.CSSFX;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

public class Main extends Application {

    public static Stage primaryStage;
    public static final boolean CSS_LIVE_RELOAD = false;

    @Override
    public void start(Stage stage) throws IOException {
        Thread.setDefaultUncaughtExceptionHandler(Main::uncaughtException);
        primaryStage = stage;
        //noinspection DataFlowIssue // intellij bug
        stage.setScene(new Scene(FXMLLoader.load(Main.class.getResource("view.fxml"))));
        stage.setTitle("Recorder");
        stage.show();
        if (CSS_LIVE_RELOAD) {
            CSSFX.addConverter(uri -> Path.of(uri.replaceAll(".*:", "").replace("out/production/classes".replace("/", File.separator), "src/main/resources".replace("/", File.separator))))
                    .start();
        }
    }

    public static void uncaughtException(Thread __, Throwable e) {
        while (e != null && e.getCause() != null && (e instanceof InvocationTargetException || e.getCause() instanceof InvocationTargetException
                || e instanceof UncheckedIOException
                || e.getClass().equals(RuntimeException.class) && (e.getMessage() == null || e.getMessage().equals(e.getCause().toString())))) {
            e = e.getCause();
        }
        e.printStackTrace();
        var text = new StringWriter();
        e.printStackTrace(new PrintWriter(text));
        var area = new TextArea(text.toString());
        area.setEditable(false);
        String content = e.getMessage() == null ? e.toString() : e.getMessage();
        Runnable show = () -> {
            var alert = new Alert(Alert.AlertType.ERROR, content);
            alert.getDialogPane().setExpandableContent(area);
            alert.show();
        };
        if (Platform.isFxApplicationThread())
            show.run();
        else
            Platform.runLater(show);
    }

}