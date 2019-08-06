package gui;

import model.Image;

import java.awt.image.BufferedImage;

// TODO: Put all tasks into worker pool

public class Controller {

    private Image image;

    private void openFileChooser() {
        // TODO: Show file chooser
        String path = ""; // TODO: Get from file chooser
        this.image = new Image(path); // TODO: Show loading bar
        refreshImageView();
    }

    private void refreshImageView() {
        Double relativePixels = 0.0; //TODO: Get from slider
        try {
            BufferedImage newPreview = image.getCropped(relativePixels);
            // TODO: Update image view with new preview
        } catch (Exception e) {
            // TODO: Show error message
        }
    }

    private void save() {
        String pathOut = ""; // TODO: Get from dialog
        try {
            image.saveCroppedImage(pathOut);
        } catch (Exception e) {
            // TODO: Show error message
        }
    }
}
