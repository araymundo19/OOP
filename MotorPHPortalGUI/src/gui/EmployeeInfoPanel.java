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
import java.util.List;
import java.text.DecimalFormat;

public class EmployeeInfoPanel extends JPanel {

    public EmployeeInfoPanel(String employeeId) {
        setLayout(new BorderLayout());

        // Scrollable panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(1, 10, 1, 10); // Half spacing
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        List<String[]> employees = CSVHelper.loadEmployeeData();
        String[] data = null;
        for (String[] emp : employees) {
            if (emp[0].equals(employeeId)) {
                data = emp;
                break;
            }
        }

        if (data == null) {
            add(new JLabel("Employee not found."), BorderLayout.CENTER);
            return;
        }

        int row = 0;

        // Section: Employee Details
        row = addSectionLabel(contentPanel, "Employee Details", row, gbc);
        row = addField(contentPanel, "Employee ID:", data[0], row, gbc);
        row = addField(contentPanel, "First Name:", data[1], row, gbc);
        row = addField(contentPanel, "Last Name:", data[2], row, gbc);
        row = addField(contentPanel, "Birthday:", data[3], row, gbc);

        // Section: Additional Details
        row = addSectionLabel(contentPanel, "Additional Details", row, gbc);
        row = addField(contentPanel, "Address:", data[4], row, gbc);
        row = addField(contentPanel, "Phone:", data[5], row, gbc);
        row = addField(contentPanel, "SSS #:", data[6], row, gbc);
        row = addField(contentPanel, "PhilHealth #:", data[7], row, gbc);
        row = addField(contentPanel, "TIN #:", data[8], row, gbc);
        row = addField(contentPanel, "Pag-IBIG #:", data[9], row, gbc);

        // Section: Employment Details
        row = addSectionLabel(contentPanel, "Employment Details", row, gbc);
        row = addField(contentPanel, "Status:", data[10], row, gbc);
        row = addField(contentPanel, "Position:", data[11], row, gbc);
        row = addField(contentPanel, "Supervisor:", data[12], row, gbc);

        // Section: Salary Information
        row = addSectionLabel(contentPanel, "Salary Details", row, gbc);
        row = addField(contentPanel, "Basic Salary:", "PHP " + moneyFormat.format(Double.parseDouble(data[13].replace(",", ""))), row, gbc);
        row = addField(contentPanel, "Rice Subsidy:", "PHP " + moneyFormat.format(Double.parseDouble(data[14].replace(",", ""))), row, gbc);
        row = addField(contentPanel, "Phone Allowance:", "PHP " + moneyFormat.format(Double.parseDouble(data[15].replace(",", ""))), row, gbc);
        row = addField(contentPanel, "Clothing Allowance:", "PHP " + moneyFormat.format(Double.parseDouble(data[16].replace(",", ""))), row, gbc);
        row = addField(contentPanel, "Gross Rate:", "PHP " + moneyFormat.format(Double.parseDouble(data[17].replace(",", ""))), row, gbc);
        row = addField(contentPanel, "Hourly Rate:", "PHP " + moneyFormat.format(Double.parseDouble(data[18].replace(",", ""))), row, gbc);



        add(scrollPane, BorderLayout.CENTER);
    }
            
    // Finance format for Salary Section
    DecimalFormat moneyFormat = new DecimalFormat("#,##0.00");
    
    // Safety check for Number formats
    private String safeFormat(String input) {
    try {
        return moneyFormat.format(Double.parseDouble(input.replace(",", "")));
    } catch (NumberFormatException e) {
        return input; // fallback to raw text
    }
}

    private int addField(JPanel panel, String label, String value, int row, GridBagConstraints gbc) {
        gbc.gridy = row;
        gbc.gridx = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        JTextField field = new JTextField(value);
        field.setEditable(false);
        field.setBackground(Color.WHITE);
        panel.add(field, gbc);

        return row + 1;
    }

    private int addSectionLabel(JPanel panel, String sectionName, int row, GridBagConstraints gbc) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JLabel sectionLabel = new JLabel(sectionName);
        sectionLabel.setFont(sectionLabel.getFont().deriveFont(Font.BOLD, 14f));
        sectionLabel.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.GRAY));
        panel.add(sectionLabel, gbc);
        gbc.gridwidth = 1;
        return row + 1;
    }   
}
