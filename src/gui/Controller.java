package gui;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// TODO: Put all tasks into worker pool

public class Controller {

    private Image image;
    private Stage stage;
    private ExecutorService workerPool;

    @FXML private Button loadImageButton;
    @FXML private ImageView imageView;
    @FXML private Slider slider;

    public Controller() {
        workerPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public void init() {
        loadImageButton.setOnAction(event -> openFileChooser());

        slider.setMin(-1);
        slider.setMax(0);
        slider.setValue(0);
        slider.valueProperty().addListener((observable, oldValue, newValue) -> refreshImageView());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            workerPool.execute(() -> {
                // TODO: Show loading bar
                image = new Image(selectedFile);
                refreshImageView();
                Platform.runLater(() -> {
                    slider.setMin(-image.width());
                    slider.setValue(0);
                    slider.setBlockIncrement(1.0);
                    slider.setMinorTickCount(1);
                    slider.setShowTickMarks(true);
                });
            });
        }
    }

    private void refreshImageView() {
        workerPool.execute(() -> {
            try {
                Double relativePixels = slider.getValue();
                BufferedImage newPreview = image.getCropped(relativePixels);
                imageView.setImage(SwingFXUtils.toFXImage(newPreview, null));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }

    private void save() {
        workerPool.execute(() -> {
            Double relativePixels = slider.getValue();
            try {
                image.saveCroppedImage(relativePixels, ""); // TODO: Get path from dialog
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }
}
