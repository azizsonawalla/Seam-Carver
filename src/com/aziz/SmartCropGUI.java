package com.aziz;

import javax.swing.*;
import java.awt.*;

public class SmartCropGUI {
    public static void main(String[] args) {
        setup();

    }

    private static void setup() {
        //load icon
        ImageIcon icon = new ImageIcon("./graphics/crop-icon.png");
        // Creating instance of JFrame
        JFrame frame = new JFrame("Smart Crop");
        frame.getContentPane().setBackground(Color.white);
        frame.setIconImage(icon.getImage());
        // Setting the width and height of frame
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        frame.setSize((int) (width/1.5), (int) (height/1.2));
        frame.setLocationRelativeTo(null);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Creating panel.
        JPanel panel = new JPanel();
        panel.setBackground(Color.white);
        panel.setSize(frame.getSize());
        // adding panel to frame
        frame.add(panel);

        panel.setLayout(null);

        // Creating JLabel
        JLabel userLabel = new JLabel("Open an image to crop");
        userLabel.setFont(new Font("Calibri", Font.PLAIN, 32));
        userLabel.setBounds(panel.getWidth()/2-190,panel.getHeight()/2-100,300,50);
        panel.add(userLabel);

        // Creating login button
        JButton loginButton = new JButton("Import image");
        loginButton.setBounds(panel.getWidth()/2-190,panel.getHeight()/2-20,300,50);
        loginButton.setBackground(Color.white);
        loginButton.setFont(new Font("Calibri", Font.PLAIN, 24));
        loginButton.setFocusPainted(false);

        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        panel.add(loginButton);

        // Setting the frame visibility to true
        frame.setVisible(true);
    }
}
