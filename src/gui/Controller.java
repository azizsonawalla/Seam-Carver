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

/**
 * Controller class for JavaFX GUI
 *
 * @author Aziz Sonawalla
 */
public class Controller {

    // Image object for image currently being edited
    private Image image;
    // Image object lock for concurrent access
    private ReadWriteLock imageLock;

    // Cache of preview images for faster previews
    private Map<Double, BufferedImage> cache;

    // Thread pool for background tasks
    private ExecutorService workerPool;

    private Stage stage;
    @FXML private Button loadImageButton;
    @FXML private ImageView imageView;
    @FXML private Slider slider;

    public Controller() {
        workerPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        imageLock = new ReentrantReadWriteLock();
        cache = new HashMap<>();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Initialize JavaFX GUI elements with present values
     * and add event listeners
     */
    public void initializeUIElements() {
        loadImageButton.setOnAction(event -> openFileChooser());
        slider.setVisible(false);
    }

    /**
     * Display file chooser interface, capture input, and render
     * a new preview
     */
    private void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg");
        fileChooser.getExtensionFilters().add(filter);
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            workerPool.execute(() -> initializeImageObject(selectedFile));
        }
    }

    /**
     * Instantiate the Image object with the given image file
     * @param imageFile image file to instantiate Image object with
     */
    private void initializeImageObject(File imageFile) {
        // TODO: Show loading bar
        imageLock.writeLock().lock();
        try {
            image = new Image(imageFile);
        } catch (Exception e) {
            // TODO: Show error pop-up on GUI
        } finally {
            imageLock.writeLock().unlock();
        }
        refreshImageView();
        Platform.runLater(() -> {
            slider.setMin( -image.width()+50);
            slider.setMax(0);
            slider.setValue(0);
            slider.setBlockIncrement(10.0);
            slider.setMinorTickCount(1);
            slider.setShowTickMarks(true);
            slider.valueProperty().addListener((observable, oldValue, newValue) -> refreshImageView());
            slider.setVisible(true);
        });
    }

    /**
     * Get the current value of the slider and update the preview
     * image with the cropped version of the image
     */
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
                        imageLock.writeLock().lock();
                        newPreview = image.getCropped(relativePixels);
                        cache.put(relativePixels, newPreview);
                        imageView.setImage(SwingFXUtils.toFXImage(newPreview, null));
                    } catch (Exception e) {
                        // TODO: Show error pop-up on GUI
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
