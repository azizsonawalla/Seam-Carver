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
import util.ImageUtil;

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

    // Thread pool for background tasks
    private ExecutorService workerPool;

    // Minimum change in slider before preview refreshes
    private int BLOCK_INCREMENT = 2;
    // Preview image max height
    private int PREVIEW_HEIGHT = 800;

    private Stage stage;
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
            BufferedImage bufferedImage = ImageUtil.readFromFile(imageFile);
            BufferedImage scaledBufferedImage = ImageUtil.reduceHeight(bufferedImage, PREVIEW_HEIGHT);
            System.out.println(String.format("Image: %dx%d", scaledBufferedImage.getWidth(), scaledBufferedImage.getHeight()));
            image = new Image(scaledBufferedImage);
        } catch (Exception e) {
            // TODO: Show error pop-up on GUI
            return;
        } finally {
            imageLock.writeLock().unlock();
        }
        refreshSlider();
        refreshImageView(null, null);
    }

    /**
     * Refresh the slider min/max values and increments based on
     * the current Image object. Also set event listener.
     */
    private void refreshSlider() {
        Platform.runLater(() -> {
            slider.setMin( -image.width()+50);
            slider.setMax(0);
            slider.setValue(0);
            slider.setMinorTickCount(1);
            slider.setMajorTickUnit(10.0);
            slider.setSnapToTicks(true);
            slider.setShowTickMarks(true);
            slider.valueProperty().addListener((observable, oldValue, newValue)
                    -> refreshImageView(oldValue, newValue));
            slider.setVisible(true);
        });
    }

    /**
     * Get the current value of the slider and update the preview
     * image with the cropped version of the image
     */
    private void refreshImageView(Number oldValue, Number newValue) {
        if (oldValue instanceof Double && newValue instanceof Double
                && Math.abs((Double)oldValue-(Double)newValue) < BLOCK_INCREMENT) return;

        workerPool.execute(() -> {
            try {
                Double relativePixels = slider.getValue();
                BufferedImage newPreview = null;
                try {
                    imageLock.writeLock().lock();
                    newPreview = image.getCropped(relativePixels);
                } catch (Exception e) {
                    // TODO: Show error pop-up on GUI
                } finally {
                    imageLock.writeLock().unlock();
                }
                imageView.setImage(SwingFXUtils.toFXImage(newPreview, null));
                centerImageView();
            } catch (Exception e) {
                // TODO: Show error
            }
        });
    }

    /**
     * Center the image currently displayed in imageView
     */
    private void centerImageView() {
        javafx.scene.image.Image preview = imageView.getImage();
        double w, h = 0;
        double ratioX = imageView.getFitWidth() / preview.getWidth();
        double ratioY = imageView.getFitHeight() / preview.getHeight();
        double reducCoeff = ratioX >= ratioY ? ratioY : ratioX;
        w = preview.getWidth() * reducCoeff;
        h = preview.getHeight() * reducCoeff;
        imageView.setX((imageView.getFitWidth() - w) / 2);
        imageView.setY((imageView.getFitHeight() - h) / 2);
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
