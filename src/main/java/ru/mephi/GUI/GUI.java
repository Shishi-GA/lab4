package ru.mephi.GUI;

import ru.mephi.BurnupMaker;
import ru.mephi.Handling.DBReader;
import ru.mephi.Manager;
import ru.mephi.Reactors.Reactor;
import ru.mephi.Reactors.ReactorDB;
import ru.mephi.Regions;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.File;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GUI extends JFrame {
    private JButton chooseFileButton;
    private JButton importButton;
    private JButton goCalculateButton;
    private JPanel mainPanel;
    private Regions regions;
    private Map<String, List<ReactorDB>> reactors;
    private HashMap<String, Reactor> reactorsType;
    private HashMap<String, Double> reactorsTypeMap;

    public GUI() throws URISyntaxException {
        setLookAndFeel();
        initializeComponents();
        setupFrame();
        createUIComponents();
        addListeners();
        setVisible(true);
    }

    public static void main(String[] args) throws URISyntaxException {
        new GUI();
    }

    private void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeComponents() {
        chooseFileButton = new JButton("Выберите файл Xml, Yaml, Json");
        importButton = new JButton("Выберите файл БД");
        goCalculateButton = new JButton("Сделать рассчеты");
        mainPanel = new JPanel(new BorderLayout());
        importButton.setEnabled(false);
        goCalculateButton.setEnabled(false);
    }

    private void setupFrame() {
        setTitle("laba4");
        setSize(250, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(mainPanel);
    }

    private void createUIComponents() {
        JPanel buttonPanel = createButtonPanel();

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createGridBagConstraints();

        buttonPanel.add(chooseFileButton, gbc);
        buttonPanel.add(importButton, gbc);
        buttonPanel.add(goCalculateButton, gbc);

        return buttonPanel;
    }

    private GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        return gbc;
    }

    private void addListeners() {
        chooseFileButton.addActionListener(e -> handleChooseFileButtonClick());
        importButton.addActionListener(e -> showFileChooser());
        goCalculateButton.addActionListener(e -> showCalculatorDialog());
    }

    private void handleChooseFileButtonClick() {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON, XML, YAML Files", "json", "xml", "yaml");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(filter);
        fileChooser.setCurrentDirectory(new File("./"));
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                Manager manager = new Manager();
                reactorsType = manager.readCommonClass(selectedFile.getAbsolutePath());
                displayReactorData(reactorsType);
                reactorsTypeMap = manager.getReactorTypeMap(reactorsType);
            } catch (Exception ex) {
                showErrorDialog("Выберите корректный файл");
            }
        }
    }

    private void displayReactorData(HashMap<String, Reactor> reactorsType) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);

        for (Map.Entry<String, Reactor> entry : reactorsType.entrySet()) {
            DefaultMutableTreeNode reactorNode = new DefaultMutableTreeNode(entry.getKey());

            reactorNode.add(new DefaultMutableTreeNode("Burnup: " + entry.getValue().burnup));
            reactorNode.add(new DefaultMutableTreeNode("Class: " + entry.getValue().reactorClass));
            reactorNode.add(new DefaultMutableTreeNode("Electrical Capacity: " + entry.getValue().electricalCapacity));
            reactorNode.add(new DefaultMutableTreeNode("First Load: " + entry.getValue().firstLoad));
            reactorNode.add(new DefaultMutableTreeNode("KPD: " + entry.getValue().kpd));
            reactorNode.add(new DefaultMutableTreeNode("Life Time: " + entry.getValue().lifeTime));
            reactorNode.add(new DefaultMutableTreeNode("Terminal Capacity: " + entry.getValue().terminalCapacity));
            reactorNode.add(new DefaultMutableTreeNode("File Type: " + entry.getValue().fileType));

            treeModel.insertNodeInto(reactorNode, rootNode, treeModel.getChildCount(rootNode));
        }

        JTree tree = new JTree(treeModel);
        JScrollPane scrollPane = new JScrollPane(tree);

        JFrame treeFrame = new JFrame();
        treeFrame.add(scrollPane);
        treeFrame.setSize(400, 400);
        treeFrame.setLocationRelativeTo(null);
        treeFrame.setVisible(true);

        importButton.setEnabled(true);
    }

    private void showFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("DB files", "db");
        fileChooser.setFileFilter(filter);
        fileChooser.setCurrentDirectory(new File("./"));
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (file.getName().toLowerCase().endsWith(".db")) {
                fillReactorData(file);
            } else {
                showErrorDialog("Выберите формат файла .db");
            }
        }
    }

    private void showCalculatorDialog() {
        ConsumptionCalculationsGUI dialog = new ConsumptionCalculationsGUI(regions, reactors);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void fillReactorData(File file) {
        try {
            reactors = new TreeMap<>(DBReader.importReactors(file));
            regions = DBReader.importRegions(file);
            BurnupMaker burnupMaker = new BurnupMaker(reactorsTypeMap, reactors);
            burnupMaker.match();
            goCalculateButton.setEnabled(true);
        } catch (SQLException e) {
            showErrorDialog("Ошибка чтения базы данных");
        }
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
}
