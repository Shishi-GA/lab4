package ru.mephi;

import ru.mephi.GUI.GUI;

import javax.swing.*;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new GUI();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
