package gui;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Winter Melon
 */
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class SalaryDetailsPanel extends JPanel {
    private final JPanel fieldsPanel;

    public SalaryDetailsPanel(Map<String, String> salaryData) {
        setLayout(new BorderLayout());
        fieldsPanel = new JPanel(new GridBagLayout());
        JScrollPane scrollPane = new JScrollPane(fieldsPanel);
        scrollPane.setPreferredSize(new Dimension(600, 500));
        add(scrollPane, BorderLayout.CENTER);
        populateFields(salaryData);
    }

    private void populateFields(Map<String, String> data) {
        fieldsPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(1, 5, 1, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String label = entry.getKey();
            String value = entry.getValue();

            gbc.gridy = row++;

            if (value.isEmpty()) {
                // Section Header
                JLabel header = new JLabel(label);
                header.setFont(new Font("Arial", Font.BOLD, 13));
                gbc.gridwidth = 2;
                fieldsPanel.add(header, gbc);
                gbc.gridwidth = 1;
            } else {
                JLabel keyLabel = new JLabel(label + ":");
                JTextField valueField = new JTextField(value);
                valueField.setEditable(false);
                fieldsPanel.add(keyLabel, gbc);

                gbc.gridx = 1;
                fieldsPanel.add(valueField, gbc);
                gbc.gridx = 0;
            }
        }

        revalidate();
        repaint();
    }
}
