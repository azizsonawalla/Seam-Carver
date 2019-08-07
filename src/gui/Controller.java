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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Controller {

    private Image image;
    private ReadWriteLock imageLock;
    private Stage stage;
    private ExecutorService workerPool;
    private Map<Double, BufferedImage> cache = new HashMap<>();

    @FXML private Button loadImageButton;
    @FXML private ImageView imageView;
    @FXML private Slider slider;

    public Controller() {
        workerPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        imageLock = new ReentrantReadWriteLock();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void initializeUIElements() {
        loadImageButton.setOnAction(event -> openFileChooser());

        slider.setMin(-1);
        slider.setMax(0);
        slider.setValue(0);
        slider.valueProperty().addListener((observable, oldValue, newValue) -> refreshImageView());
    }

    private void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg");
        fileChooser.getExtensionFilters().add(filter);
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            workerPool.execute(() -> {
                // TODO: Show loading bar
                imageLock.writeLock().lock();
                try {
                    image = new Image(selectedFile);
                } catch (Exception e) {
                    // TODO: Show error
                } finally {
                    imageLock.writeLock().unlock();
                }
                refreshImageView();
                Platform.runLater(() -> {
                    int min = -image.width()+50;
                    slider.setMin(min);
                    slider.setValue(0);
                    slider.setBlockIncrement(5.0);
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
                BufferedImage newPreview;
                if (cache.containsKey(relativePixels)) {
                    newPreview = cache.get(relativePixels);
                    imageView.setImage(SwingFXUtils.toFXImage(newPreview, null));
                } else {
                    try {
                        newPreview = image.getCropped(relativePixels);
                        cache.put(relativePixels, newPreview);
                        imageView.setImage(SwingFXUtils.toFXImage(newPreview, null));
                    } catch (Exception e) {
                        // TODO: Show error
                    } finally {
                        imageLock.writeLock().unlock();
                    }
                }
            } catch (Exception e) {
                // TODO: Show error
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
