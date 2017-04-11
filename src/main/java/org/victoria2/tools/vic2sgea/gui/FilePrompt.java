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

/**
 * Created by anth on 12.02.2017.
 */
public class FilePrompt extends HBox {

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


    public String getPath() {
        return pathField.getText();
    }

    public void setPath(String path) {
        this.pathField.setText(path);
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
                String temp = "";

                //yes, these two block are almost identical
                if (promptType == FilePromptType.DIR) {
                    DirectoryChooser chooser = new DirectoryChooser();
                    if (!pathField.getText().isEmpty()) {
                        //initialFile
                        File initialFile = new File(pathField.getText()).getParentFile();
                        chooser.setInitialDirectory(initialFile);
                    }

                    File file = chooser.showDialog(null);
                    temp = file.getPath().replace("\\", "/"); // Usable pathField
                } else if (promptType == FilePromptType.FILE) {
                    FileChooser chooser = new FileChooser();
                    if (!pathField.getText().isEmpty()) {
                        //initialFile
                        File initialFile = new File(pathField.getText()).getParentFile();
                        chooser.setInitialDirectory(initialFile);
                    }

                    File file = chooser.showOpenDialog(null);
                    temp = file.getPath().replace("\\", "/"); // Usable pathField
                }

                setPath(temp);

            } catch (NullPointerException ignored) {
            }
        });
    }

    public FilePromptType getPromptType() {
        return promptType;
    }

}
