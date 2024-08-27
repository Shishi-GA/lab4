package ru.mephi.GUI;

import ru.mephi.ConsumptionCalculator;
import ru.mephi.Regions;
import ru.mephi.Reactors.ReactorDB;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;

public class ConsumptionCalculationsGUI extends JDialog {
    private final ConsumptionCalculator calculator;
    private final Regions regions;
    private JPanel contentPane;
    private JButton byCountryButton;
    private JButton byOperatorButton;
    private JButton byRegionButton;
    private JTable resultTable;
    private Map<String, Map<Integer, Double>> currentCountryData;
    private Map<String, Map<Integer, Double>> currentOperatorData;
    private Map<String, Map<Integer, Double>> currentRegionData;

    public ConsumptionCalculationsGUI(Regions regions, Map<String, List<ReactorDB>> reactors) {
        this.regions = regions;
        calculator = new ConsumptionCalculator(reactors);

        initializeComponents();
        setupDialog();
        addListeners();
    }

    public static void main(String[] args) {
        Regions regions = new Regions();
        Map<String, List<ReactorDB>> reactors = new HashMap<>();
        HashMap<String, Double> reactorsTypeMap = new HashMap<>();
        ConsumptionCalculationsGUI dialog = new ConsumptionCalculationsGUI(regions, reactors);
        dialog.pack();
        dialog.setVisible(true);
    }

    private void initializeComponents() {
        contentPane = new JPanel(new BorderLayout());

        JPanel leftPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        byCountryButton = new JButton("Страны");
        leftPanel.add(byCountryButton, gbc);
        byOperatorButton = new JButton("Операторы");
        leftPanel.add(byOperatorButton, gbc);
        byRegionButton = new JButton("Регионы");
        leftPanel.add(byRegionButton, gbc);

        contentPane.add(leftPanel, BorderLayout.WEST);

        resultTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(resultTable);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        setContentPane(contentPane);
    }

    private void setupDialog() {
        setModal(true);
        setTitle("Потребление");
        setSize(600, 400);
        setLocationRelativeTo(null);
    }

    private void addListeners() {
        addCalculateListeners();

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
        contentPane.registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void addCalculateListeners() {
        byCountryButton.addActionListener(e -> {
            currentCountryData = calculator.calculateConsumptionByCountries();
            currentCountryData = sortDataByCountry(currentCountryData);
            updateTable(currentCountryData, "Страна");
        });

        byOperatorButton.addActionListener(e -> {
            currentOperatorData = calculator.calculateConsumptionByOperator();
            currentOperatorData = sortDataByCountry(currentOperatorData);
            updateTable(currentOperatorData, "Оператор");

        });

        byRegionButton.addActionListener(e -> {
            currentRegionData = calculator.calculateConsumptionByRegions(regions);
            currentRegionData = sortDataByCountry(currentRegionData);
            updateTable(currentRegionData, "Регион");

        });
    }



    private Map<String, Map<Integer, Double>> sortDataByCountry(Map<String, Map<Integer, Double>> data) {
        Map<String, Map<Integer, Double>> sortedData = new TreeMap<>(Comparator.naturalOrder());
        sortedData.putAll(data);
        return sortedData;
    }


    private void updateTable(Map<String, Map<Integer, Double>> data, String header) {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{header, "Потребление", "Год"});

        data.forEach((group, consumptionByYear) -> {
            consumptionByYear.keySet().stream().sorted().forEach(year -> {
                Double consumption = consumptionByYear.get(year);
                model.addRow(new Object[]{group, String.format("%1$.2f", consumption), year});
            });
        });

        resultTable.setModel(model);
    }

}