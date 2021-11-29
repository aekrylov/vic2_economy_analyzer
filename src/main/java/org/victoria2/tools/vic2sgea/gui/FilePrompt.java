package org.victoria2.tools.vic2sgea.gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by anth on 12.02.2017.
 */
public class FilePrompt extends HBox {

    private Path path;

    public enum FilePromptType {
        FILE, DIR
    }

    private Label label = new Label();
    private TextField pathField = new TextField();
    private Button browseButton = new Button("Browse");
    private FilePromptType promptType;


    public FilePrompt() {
        super(5.);
        getChildren().addAll(label, pathField, browseButton);
        setHgrow(pathField, Priority.ALWAYS);
        label.setPrefWidth(100.);

        labelWidth.addListener((observable, oldValue, newValue) -> label.setPrefWidth((Double) newValue));
        pathField.textProperty().addListener((observable, oldValue, newValue) -> path = newValue == null ? null : Paths.get(newValue));
    }

    public FilePrompt(FilePromptType promptType, String labelText) {
        this();
        this.promptType = promptType;
        label.setText(labelText);
    }

    private DoubleProperty labelWidth = new SimpleDoubleProperty(100.);

    public double getLabelWidth() {
        return labelWidth.get();
    }

    public void setLabelWidth(double labelWidth) {
        this.labelWidth.set(labelWidth);
    }

    public String getLabelText() {
        return label.getText();
    }

    public void setLabelText(String labelText) {
        label.setText(labelText);
    }

    private javafx.event.EventHandler<? super MouseEvent> onButtonClicked;


    public Path getPath() {
        return path;
    }

    @Deprecated
    public void setPath(String path) {
        setPath(Paths.get(path));
    }

    public void setPath(Path path) {
        this.path = path;
        this.pathField.setText(path.toString());
    }

    public EventHandler<? super MouseEvent> getOnButtonClicked() {
        return onButtonClicked;
    }

    public void setOnButtonClicked(EventHandler<? super MouseEvent> onButtonClicked) {
        this.onButtonClicked = onButtonClicked;
        browseButton.setOnMouseClicked(onButtonClicked);
    }

    public void setPromptType(FilePromptType promptType) {
        this.promptType = promptType;
        browseButton.setOnMouseClicked(event -> {
            try {
                File file = null;

                //yes, these two block are almost identical
                if (promptType == FilePromptType.DIR) {
                    DirectoryChooser chooser = new DirectoryChooser();
                    if (!pathField.getText().isEmpty()) {
                        //initialFile
                        File initialFile = new File(pathField.getText()).getParentFile();
                        chooser.setInitialDirectory(initialFile);
                    }

                    file = chooser.showDialog(null);
                } else if (promptType == FilePromptType.FILE) {
                    FileChooser chooser = new FileChooser();
                    if (!pathField.getText().isEmpty()) {
                        //initialFile
                        File initialFile = new File(pathField.getText()).getParentFile();
                        chooser.setInitialDirectory(initialFile);
                    }

                    file = chooser.showOpenDialog(null);
                }

                setPath(file.toPath());

            } catch (NullPointerException ignored) {
            }
        });
    }

    public FilePromptType getPromptType() {
        return promptType;
    }

}
