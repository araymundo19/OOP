/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package gui;

/**
 *
 * @author Winter Melon
 */
import services.AttendanceServ;
import services.AuthServ;
import services.LeaveServ;
import util.SalaryComputationHelper;
import java.util.List;
import java.util.Map;

public class EmployeeModule extends javax.swing.JPanel {

    /**
     * Creates new form EmployeeModule
     */
    public EmployeeModule() {
        initComponents();
        setupAttendanceListeners();
        setupPayrollListeners();
        refreshLeaveRequestTable();
        
        // Locks date to not select before current
        java.util.Date today = new java.util.Date();
        txtLeaveRequestStart.getJCalendar().setMinSelectableDate(today);
        txtLeaveRequestEnd.getJCalendar().setMinSelectableDate(today);
    }
    ///^^^ End of Constructor
    
    
    public javax.swing.JTabbedPane getTabPane() {
    return tabEmployeeModule; 
    }
    
    // Fill For Profile
    public void displayData(models.EmployeeSalary emp) {
        if (emp == null) return;
        
        lblEmployeeID.setText(emp.getEmployeeId());
        lblFullName.setText(emp.getFirstName() + " " + emp.getLastName());
        txtDepartment.setText(emp.getDepartment().toString()); 
        txtBirthday.setText(emp.getBirthday());
        txtAddress.setText(emp.getAddress());
        txtPhoneNumber.setText(emp.getPhoneNumber());
        txtStatus.setText(emp.getEmpStatus() != null ? emp.getEmpStatus().toString() : "");
        txtPosition.setText(emp.getPosition());
        txtSupervisor.setText(emp.getImmediateSupervisor());
        txtSSS.setText(emp.getSssNumber());
        txtPhilHealth.setText(emp.getPhilHealthNumber());
        txtTIN.setText(emp.getTinNumber());
        txtPagibig.setText(emp.getPagIbigNumber());
        txtBasicRate.setText(String.format("%,.2f", emp.getBasicSalary()));
        txtGrossSemi.setText(String.format("%,.2f", emp.getGrossSemiMonthlyRate()));
        txtHourlyRate.setText(String.format("%,.2f", emp.getHourlyRate()));
        txtRiceSubsidy.setText(String.format("%,.2f", emp.getRiceSubsidy()));
        txtPhoneAllowance.setText(String.format("%,.2f", emp.getPhoneAllowance()));
        txtClothingAllowance.setText(String.format("%,.2f", emp.getClothingAllowance()));
}
    
    // Listener for Attendance
    public javax.swing.JComboBox<String> getDrpMonth() {
        return drpMyAttendanceMonth;
    }

    public javax.swing.JComboBox<String> getDrpYear() {
        return drpMyAttendanceYear;
    }
    
    private void setupAttendanceListeners() {
        java.awt.event.ActionListener refreshAction = e -> {
            refreshAttendanceTable();
        };

        drpMyAttendanceMonth.addActionListener(refreshAction);
        drpMyAttendanceYear.addActionListener(refreshAction);
    }
    
    public void displayAttendance(java.util.List<String[]> rows) {
        String[] columnNames = {"Date", "Time In", "Time Out", "Work Hours", "Late", "Overtime"};
        
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columnNames, 0) {
           @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        if (rows != null) {
        
            for (String[] row : rows) {
                model.addRow(row);
            }
        }
        
        tblMyAttendance.setModel(model);
        tblMyAttendance.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(javax.swing.JLabel.CENTER);
    
        for (int i = 0; i < tblMyAttendance.getColumnCount(); i++) {
        tblMyAttendance.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
    }
    
        tblMyAttendance.getTableHeader().setReorderingAllowed(false);
    }
        
        

    public void refreshAttendanceTable() {
        AttendanceServ aServ = new AttendanceServ();
    
        String monthName = drpMyAttendanceMonth.getSelectedItem().toString();
        String y = drpMyAttendanceYear.getSelectedItem().toString();
    
        String m = convertMonthNameToNumber(monthName);
        
        java.util.List<String[]> rows = aServ.getAttendanceSummary(m, y);
        displayAttendance(rows);
    }
    
    private String convertMonthNameToNumber(String name) {
        try {
            java.time.format.DateTimeFormatter parser = java.time.format.DateTimeFormatter.ofPattern("MMMM", java.util.Locale.ENGLISH);
            java.time.Month month = java.time.Month.from(parser.parse(name));
            return String.format("%02d", month.getValue());
        } catch (Exception e) {
            return "01";
        }
    }
    
    // Payslip
    private void setupPayrollListeners() {
        java.awt.event.ActionListener payrollAction = e -> {
            updatePayslip();
        };

        drpMyPayslipMonth.addActionListener(payrollAction);
        drpMyPayslipPeriod.addActionListener(payrollAction);
        drpMyPayslipYear.addActionListener(payrollAction);
    }

    public void updatePayslip() {
        models.UserAccount account = services.AuthServ.getLoggedInUser();
        if (account == null) return;
        
        dao.EmployeeDAO empDAO = new dao.EmployeeDAO();
        models.EmployeeSalary currentEmp = (models.EmployeeSalary) empDAO.getEmployeeById(account.getEmployeeId());

        if (currentEmp == null) {
            System.out.println("DEBUG: Employee details not found in CSV!");
            return;
        }
        
        // Default State
        txtMyBasicPay.setText("PHP 0.00");
        txtMyAllowancePay.setText("PHP 0.00");
        txtMyOvertimePay.setText("PHP 0.00");
        txtMyLateDeduc.setText("PHP 0.00");
        txtMyTaxDeduc.setText("PHP 0.00");
        txtMySSSDeduc.setText("PHP 0.00");
        txtMyPhilHealthDeduc.setText("PHP 0.00");
        txtMyPagibigDeduc.setText("PHP 0.00");
        txtMyGrossPay.setText("PHP 0.00");
        txtMyTotalDeductions.setText("PHP 0.00");
        txtMyNetPay.setText("PHP 0.00");

        String mName = drpMyPayslipMonth.getSelectedItem().toString();
        String period = drpMyPayslipPeriod.getSelectedItem().toString();
        String year = drpMyPayslipYear.getSelectedItem().toString();
        String mNum = convertMonthNameToNumber(mName);
        
        if (mName.equals("Month") || year.equals("[Year]") || year.contains("Year")) {
            return; 
        }

        AttendanceServ aServ = new AttendanceServ();
        List<String[]> logs = aServ.getAttendanceSummary(mNum, year);

        Map<String, String> data = SalaryComputationHelper.computeMonthlySalary(
            currentEmp, mNum, year, period, logs
        );

        txtPayrollEID.setText("PHP " + data.get("EmployeeID"));
        txtPayrollName.setText("PHP " + data.get("Name"));
        txtPayrollDate.setText("PHP " + data.get("DateCovered"));

        txtMyBasicPay.setText("PHP " + data.get("BasicPay"));
        txtMyAllowancePay.setText("PHP " + data.get("Allowances"));
        txtMyOvertimePay.setText("PHP " + data.get("Overtime"));

        txtMyLateDeduc.setText("PHP " + data.get("Late"));
        txtMyTaxDeduc.setText("PHP " + data.get("Tax"));
        txtMySSSDeduc.setText("PHP " + data.get("SSS"));
        txtMyPhilHealthDeduc.setText("PHP " + data.get("Philhealth"));
        txtMyPagibigDeduc.setText("PHP " + data.get("Pagibig"));

        txtMyGrossPay.setText("PHP " + data.get("Gross"));
        txtMyTotalDeductions.setText("PHP " + data.get("TotalDeductions"));
        txtMyNetPay.setText("PHP " + data.get("NetPay"));
    }
    
    // Leave Request
    private final services.LeaveServ leaveServ = new services.LeaveServ();
    
    private void submitLeaveRequest() {
        models.UserAccount account = services.AuthServ.getLoggedInUser();
        if (account == null) return;

        String type = drpLeaveRequestType.getSelectedItem().toString();
        String reason = txtLeaveRequestReason.getText().trim();
        
        java.util.Date startDate = txtLeaveRequestStart.getDate(); 
        java.util.Date endDate = txtLeaveRequestEnd.getDate();
        
        if (startDate == null || endDate == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Please select both Start and End dates.");
            return;
        }
        
        if (endDate.before(startDate)) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Invalid Date Range: End date cannot be before the start date.", 
                "Date Error", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (leaveServ.submitLeaveRequest(account.getEmployeeId(), type, startDate, endDate, reason)) {
            javax.swing.JOptionPane.showMessageDialog(this, "Leave Request Sent Successfully!");
            clearLeaveRequestForm();
            refreshLeaveRequestTable();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Failed to submit request. Please check your inputs.");
        }
    }

    public final void refreshLeaveRequestTable() {
        models.UserAccount account = services.AuthServ.getLoggedInUser();
        if (account == null) return;

        java.util.List<models.LeaveRecord> history = leaveServ.getEmployeeLeaveHistory(account.getEmployeeId());

        // Define your headers clearly
        String[] columns = {"Request ID", "Type", "Start Date", "End Date", "Status", "Reason"};

        // Create a fresh model with headers
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make the table read-only
            }
        };

        if (history == null || history.isEmpty()) {
            // Post the "No data" message across the first column
            model.addRow(new Object[]{"No leave request data available", "", "", "", "", ""});
        } else {
            for (models.LeaveRecord r : history) {
                model.addRow(new Object[]{
                    r.getRequestId(),
                    r.getLeaveType(),
                    r.getStartDate(),
                    r.getEndDate(),
                    r.getStatus(),
                    r.getReason()
                });
            }
        }

        tblLeaveRequest.setModel(model);

    tblLeaveRequest.removeColumn(tblLeaveRequest.getColumnModel().getColumn(5));
}

    private void clearLeaveRequestForm() {
        drpLeaveRequestType.setSelectedIndex(0);
        txtLeaveRequestStart.setDate(null);
        txtLeaveRequestEnd.setDate(null);
        txtLeaveRequestReason.setText("");
        txtReqLeaveReason1.setText("");
        txtReqLeaveDecision.setText("");
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlEmployeeModule = new javax.swing.JPanel();
        tabEmployeeModule = new javax.swing.JTabbedPane();
        ScrMyProfile = new javax.swing.JScrollPane();
        pnlMyProfile = new javax.swing.JPanel();
        pnlMyProfileRow1 = new javax.swing.JPanel();
        lblMyProfilePic = new javax.swing.JLabel();
        pnlMyProfileEmployeeDetails = new javax.swing.JPanel();
        lblEmployeeID = new javax.swing.JLabel();
        lblFullName = new javax.swing.JLabel();
        pnlMyProfileRow2 = new javax.swing.JPanel();
        pnlMyProfileEmployee = new javax.swing.JPanel();
        lblDepartment = new javax.swing.JLabel();
        txtDepartment = new javax.swing.JLabel();
        lblPosition = new javax.swing.JLabel();
        txtPosition = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        txtStatus = new javax.swing.JLabel();
        lblSupervisor = new javax.swing.JLabel();
        txtSupervisor = new javax.swing.JLabel();
        vGlue = new javax.swing.JLabel();
        pnlMyProfilePersonal = new javax.swing.JPanel();
        lblBirthday = new javax.swing.JLabel();
        txtBirthday = new javax.swing.JLabel();
        lblPhoneNumber = new javax.swing.JLabel();
        txtPhoneNumber = new javax.swing.JLabel();
        lblAddress = new javax.swing.JLabel();
        txtAddress = new javax.swing.JTextArea();
        vGlue1 = new javax.swing.JLabel();
        pnlMyProfileRow3 = new javax.swing.JPanel();
        pnlMyProfileStatutory = new javax.swing.JPanel();
        lblTIN = new javax.swing.JLabel();
        txtTIN = new javax.swing.JLabel();
        lblSSS = new javax.swing.JLabel();
        txtSSS = new javax.swing.JLabel();
        lblPhilHealth = new javax.swing.JLabel();
        txtPhilHealth = new javax.swing.JLabel();
        lblPagibig = new javax.swing.JLabel();
        txtPagibig = new javax.swing.JLabel();
        vGlue4 = new javax.swing.JLabel();
        pnlMyProfileSalary = new javax.swing.JPanel();
        lblBasicRate = new javax.swing.JLabel();
        txtBasicRate = new javax.swing.JLabel();
        lblGrossSemi = new javax.swing.JLabel();
        txtGrossSemi = new javax.swing.JLabel();
        lblHourlyRate = new javax.swing.JLabel();
        txtHourlyRate = new javax.swing.JLabel();
        vGlue3 = new javax.swing.JLabel();
        pnlMyProfileAllowance = new javax.swing.JPanel();
        lblRiceSubsidy = new javax.swing.JLabel();
        txtRiceSubsidy = new javax.swing.JLabel();
        lblPhoneAllowance = new javax.swing.JLabel();
        txtPhoneAllowance = new javax.swing.JLabel();
        lblClothingAllowance = new javax.swing.JLabel();
        txtClothingAllowance = new javax.swing.JLabel();
        vGlue2 = new javax.swing.JLabel();
        vGlue8 = new javax.swing.JLabel();
        pnlMyProfileVGlue = new javax.swing.JLabel();
        pnlMyAttendance = new javax.swing.JPanel();
        pnlMyAttendanceCtrl = new javax.swing.JPanel();
        drpMyAttendanceMonth = new javax.swing.JComboBox<>();
        drpMyAttendanceYear = new javax.swing.JComboBox<>();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblMyAttendance = new javax.swing.JTable();
        pnlMyPayslip = new javax.swing.JPanel();
        pnlMyPayslipCtrl = new javax.swing.JPanel();
        drpMyPayslipMonth = new javax.swing.JComboBox<>();
        drpMyPayslipPeriod = new javax.swing.JComboBox<>();
        drpMyPayslipYear = new javax.swing.JComboBox<>();
        ScrMyPayslip = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        pnlMyPayslipRow1 = new javax.swing.JPanel();
        lblPayrollEID = new javax.swing.JLabel();
        txtPayrollEID = new javax.swing.JLabel();
        lblPayrollName = new javax.swing.JLabel();
        txtPayrollName = new javax.swing.JLabel();
        lblPayrollDate = new javax.swing.JLabel();
        txtPayrollDate = new javax.swing.JLabel();
        pnlMyPayrollVGlue1 = new javax.swing.JLabel();
        pnlMyPayslipRow2 = new javax.swing.JPanel();
        pnlMyPayrollEarnings = new javax.swing.JPanel();
        lblMyBasicPay = new javax.swing.JLabel();
        txtMyBasicPay = new javax.swing.JLabel();
        lblMyAllowancePay = new javax.swing.JLabel();
        txtMyAllowancePay = new javax.swing.JLabel();
        lblMyOvertimePay = new javax.swing.JLabel();
        txtMyOvertimePay = new javax.swing.JLabel();
        vGlue6 = new javax.swing.JLabel();
        pnlMyPayrollDeductions = new javax.swing.JPanel();
        lblMyLateDeduc = new javax.swing.JLabel();
        txtMyLateDeduc = new javax.swing.JLabel();
        lblMyTaxDeduc = new javax.swing.JLabel();
        txtMyTaxDeduc = new javax.swing.JLabel();
        lblMySSSDeduc = new javax.swing.JLabel();
        txtMySSSDeduc = new javax.swing.JLabel();
        lblMyPhilHealthDeduc = new javax.swing.JLabel();
        txtMyPhilHealthDeduc = new javax.swing.JLabel();
        lblMyPagibigDeduc = new javax.swing.JLabel();
        txtMyPagibigDeduc = new javax.swing.JLabel();
        vGlue5 = new javax.swing.JLabel();
        pnlMyPayrollVGlue2 = new javax.swing.JLabel();
        pnlMyPayslipRow3 = new javax.swing.JPanel();
        pnlMyPayrollSummary = new javax.swing.JPanel();
        lblMyGrossPay = new javax.swing.JLabel();
        txtMyGrossPay = new javax.swing.JLabel();
        lblMyTotalDeductions = new javax.swing.JLabel();
        txtMyTotalDeductions = new javax.swing.JLabel();
        lblMyNetPay = new javax.swing.JLabel();
        txtMyNetPay = new javax.swing.JLabel();
        vGlue7 = new javax.swing.JLabel();
        pnlMyPayrollVGlue3 = new javax.swing.JLabel();
        pnlMyPayslipVGlue = new javax.swing.JLabel();
        pnlLeaveRequest = new javax.swing.JPanel();
        pnlLeaveRequestCtrl = new javax.swing.JPanel();
        drpLeaveRequestType = new javax.swing.JComboBox<>();
        txtLeaveRequestStart = new com.toedter.calendar.JDateChooser();
        txtLeaveRequestEnd = new com.toedter.calendar.JDateChooser();
        txtLeaveRequestReason = new javax.swing.JTextField();
        btnLeaveRequestSend = new javax.swing.JButton();
        btnLeaveRequestClear = new javax.swing.JButton();
        pnlReqLeaveVGlue10 = new javax.swing.JLabel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jScrollPane13 = new javax.swing.JScrollPane();
        tblLeaveRequest = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        pnlLeaveRequestMsg = new javax.swing.JPanel();
        pnlLeaveRequestReason = new javax.swing.JPanel();
        jScrollPane14 = new javax.swing.JScrollPane();
        txtReqLeaveReason1 = new javax.swing.JTextArea();
        vGlue14 = new javax.swing.JLabel();
        pnlLeaveRequestDecision = new javax.swing.JPanel();
        jScrollPane15 = new javax.swing.JScrollPane();
        txtReqLeaveDecision = new javax.swing.JTextArea();
        vGlue15 = new javax.swing.JLabel();
        pnlLeaveRequestVGlue11 = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        pnlEmployeeModule.setLayout(new java.awt.BorderLayout());

        ScrMyProfile.setBorder(null);
        ScrMyProfile.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        pnlMyProfile.setBackground(new java.awt.Color(255, 255, 255));
        pnlMyProfile.setPreferredSize(new java.awt.Dimension(930, 800));
        pnlMyProfile.setLayout(new java.awt.GridBagLayout());

        pnlMyProfileRow1.setOpaque(false);
        pnlMyProfileRow1.setLayout(new java.awt.BorderLayout());

        lblMyProfilePic.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMyProfilePic.setText("My Profile Pic");
        lblMyProfilePic.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblMyProfilePic.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblMyProfilePic.setOpaque(true);
        lblMyProfilePic.setPreferredSize(new java.awt.Dimension(200, 200));
        pnlMyProfileRow1.add(lblMyProfilePic, java.awt.BorderLayout.WEST);

        pnlMyProfileEmployeeDetails.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));
        pnlMyProfileEmployeeDetails.setOpaque(false);
        pnlMyProfileEmployeeDetails.setLayout(new java.awt.GridBagLayout());

        lblEmployeeID.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblEmployeeID.setText("Employee ID");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 0);
        pnlMyProfileEmployeeDetails.add(lblEmployeeID, gridBagConstraints);

        lblFullName.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblFullName.setText("Name");
        lblFullName.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 0);
        pnlMyProfileEmployeeDetails.add(lblFullName, gridBagConstraints);

        pnlMyProfileRow1.add(pnlMyProfileEmployeeDetails, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 33, 20, 20);
        pnlMyProfile.add(pnlMyProfileRow1, gridBagConstraints);

        pnlMyProfileRow2.setBackground(new java.awt.Color(255, 255, 255));
        pnlMyProfileRow2.setLayout(new java.awt.GridBagLayout());

        pnlMyProfileEmployee.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(null, "Employee Details", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(153, 153, 153)), javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15))); // NOI18N
        pnlMyProfileEmployee.setMinimumSize(new java.awt.Dimension(10, 10));
        pnlMyProfileEmployee.setOpaque(false);
        pnlMyProfileEmployee.setLayout(new java.awt.GridBagLayout());

        lblDepartment.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblDepartment.setForeground(new java.awt.Color(140, 140, 140));
        lblDepartment.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDepartment.setText("Department:");
        lblDepartment.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 20);
        pnlMyProfileEmployee.add(lblDepartment, gridBagConstraints);

        txtDepartment.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtDepartment.setForeground(new java.awt.Color(30, 30, 30));
        txtDepartment.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtDepartment.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        pnlMyProfileEmployee.add(txtDepartment, gridBagConstraints);

        lblPosition.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblPosition.setForeground(new java.awt.Color(140, 140, 140));
        lblPosition.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblPosition.setText("Position:");
        lblPosition.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 20);
        pnlMyProfileEmployee.add(lblPosition, gridBagConstraints);

        txtPosition.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtPosition.setForeground(new java.awt.Color(30, 30, 30));
        txtPosition.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtPosition.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        pnlMyProfileEmployee.add(txtPosition, gridBagConstraints);

        lblStatus.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblStatus.setForeground(new java.awt.Color(140, 140, 140));
        lblStatus.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblStatus.setText("Status:");
        lblStatus.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 20);
        pnlMyProfileEmployee.add(lblStatus, gridBagConstraints);

        txtStatus.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtStatus.setForeground(new java.awt.Color(30, 30, 30));
        txtStatus.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtStatus.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        pnlMyProfileEmployee.add(txtStatus, gridBagConstraints);

        lblSupervisor.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblSupervisor.setForeground(new java.awt.Color(140, 140, 140));
        lblSupervisor.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblSupervisor.setText("Immediate Supervisor:");
        lblSupervisor.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 20);
        pnlMyProfileEmployee.add(lblSupervisor, gridBagConstraints);

        txtSupervisor.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtSupervisor.setForeground(new java.awt.Color(30, 30, 30));
        txtSupervisor.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtSupervisor.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        pnlMyProfileEmployee.add(txtSupervisor, gridBagConstraints);

        vGlue.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        vGlue.setForeground(new java.awt.Color(30, 30, 30));
        vGlue.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        vGlue.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlMyProfileEmployee.add(vGlue, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        pnlMyProfileRow2.add(pnlMyProfileEmployee, gridBagConstraints);

        pnlMyProfilePersonal.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(null, "Personal Details", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(153, 153, 153)), javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15))); // NOI18N
        pnlMyProfilePersonal.setMinimumSize(new java.awt.Dimension(10, 10));
        pnlMyProfilePersonal.setOpaque(false);
        pnlMyProfilePersonal.setLayout(new java.awt.GridBagLayout());

        lblBirthday.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblBirthday.setForeground(new java.awt.Color(140, 140, 140));
        lblBirthday.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblBirthday.setText("Birthday:");
        lblBirthday.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 20);
        pnlMyProfilePersonal.add(lblBirthday, gridBagConstraints);

        txtBirthday.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtBirthday.setForeground(new java.awt.Color(30, 30, 30));
        txtBirthday.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtBirthday.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        pnlMyProfilePersonal.add(txtBirthday, gridBagConstraints);

        lblPhoneNumber.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblPhoneNumber.setForeground(new java.awt.Color(140, 140, 140));
        lblPhoneNumber.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblPhoneNumber.setText("Phone Number:");
        lblPhoneNumber.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 20);
        pnlMyProfilePersonal.add(lblPhoneNumber, gridBagConstraints);

        txtPhoneNumber.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtPhoneNumber.setForeground(new java.awt.Color(30, 30, 30));
        txtPhoneNumber.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtPhoneNumber.setText("MEOW");
        txtPhoneNumber.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        pnlMyProfilePersonal.add(txtPhoneNumber, gridBagConstraints);

        lblAddress.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblAddress.setForeground(new java.awt.Color(140, 140, 140));
        lblAddress.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblAddress.setText("Address:");
        lblAddress.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 20);
        pnlMyProfilePersonal.add(lblAddress, gridBagConstraints);

        txtAddress.setEditable(false);
        txtAddress.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtAddress.setLineWrap(true);
        txtAddress.setRows(4);
        txtAddress.setText("sdfdsfasdasdasdasd");
        txtAddress.setBorder(null);
        txtAddress.setFocusable(false);
        txtAddress.setMinimumSize(new java.awt.Dimension(10, 10));
        txtAddress.setOpaque(false);
        txtAddress.setPreferredSize(new java.awt.Dimension(0, 80));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        pnlMyProfilePersonal.add(txtAddress, gridBagConstraints);

        vGlue1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        vGlue1.setForeground(new java.awt.Color(30, 30, 30));
        vGlue1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        vGlue1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlMyProfilePersonal.add(vGlue1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        pnlMyProfileRow2.add(pnlMyProfilePersonal, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 20);
        pnlMyProfile.add(pnlMyProfileRow2, gridBagConstraints);

        pnlMyProfileRow3.setBackground(new java.awt.Color(255, 255, 255));
        pnlMyProfileRow3.setLayout(new java.awt.GridBagLayout());

        pnlMyProfileStatutory.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(null, "Statutory Details", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(153, 153, 153)), javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15))); // NOI18N
        pnlMyProfileStatutory.setMinimumSize(new java.awt.Dimension(10, 10));
        pnlMyProfileStatutory.setOpaque(false);
        pnlMyProfileStatutory.setLayout(new java.awt.GridBagLayout());

        lblTIN.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblTIN.setForeground(new java.awt.Color(140, 140, 140));
        lblTIN.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTIN.setText("TIN #:");
        lblTIN.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 20);
        pnlMyProfileStatutory.add(lblTIN, gridBagConstraints);

        txtTIN.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtTIN.setForeground(new java.awt.Color(30, 30, 30));
        txtTIN.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtTIN.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        pnlMyProfileStatutory.add(txtTIN, gridBagConstraints);

        lblSSS.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblSSS.setForeground(new java.awt.Color(140, 140, 140));
        lblSSS.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblSSS.setText("SSS #:");
        lblSSS.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 20);
        pnlMyProfileStatutory.add(lblSSS, gridBagConstraints);

        txtSSS.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtSSS.setForeground(new java.awt.Color(30, 30, 30));
        txtSSS.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtSSS.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        pnlMyProfileStatutory.add(txtSSS, gridBagConstraints);

        lblPhilHealth.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblPhilHealth.setForeground(new java.awt.Color(140, 140, 140));
        lblPhilHealth.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblPhilHealth.setText("PhilHealth #:");
        lblPhilHealth.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 20);
        pnlMyProfileStatutory.add(lblPhilHealth, gridBagConstraints);

        txtPhilHealth.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtPhilHealth.setForeground(new java.awt.Color(30, 30, 30));
        txtPhilHealth.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtPhilHealth.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        pnlMyProfileStatutory.add(txtPhilHealth, gridBagConstraints);

        lblPagibig.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblPagibig.setForeground(new java.awt.Color(140, 140, 140));
        lblPagibig.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblPagibig.setText("Pagibig #:");
        lblPagibig.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 20);
        pnlMyProfileStatutory.add(lblPagibig, gridBagConstraints);

        txtPagibig.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtPagibig.setForeground(new java.awt.Color(30, 30, 30));
        txtPagibig.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtPagibig.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        pnlMyProfileStatutory.add(txtPagibig, gridBagConstraints);

        vGlue4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        vGlue4.setForeground(new java.awt.Color(30, 30, 30));
        vGlue4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        vGlue4.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlMyProfileStatutory.add(vGlue4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.33;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnlMyProfileRow3.add(pnlMyProfileStatutory, gridBagConstraints);

        pnlMyProfileSalary.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(null, "Salary Details", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(153, 153, 153)), javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15))); // NOI18N
        pnlMyProfileSalary.setMinimumSize(new java.awt.Dimension(10, 10));
        pnlMyProfileSalary.setOpaque(false);
        pnlMyProfileSalary.setLayout(new java.awt.GridBagLayout());

        lblBasicRate.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblBasicRate.setForeground(new java.awt.Color(140, 140, 140));
        lblBasicRate.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblBasicRate.setText("Basic Rate:");
        lblBasicRate.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 20);
        pnlMyProfileSalary.add(lblBasicRate, gridBagConstraints);

        txtBasicRate.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtBasicRate.setForeground(new java.awt.Color(30, 30, 30));
        txtBasicRate.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtBasicRate.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        pnlMyProfileSalary.add(txtBasicRate, gridBagConstraints);

        lblGrossSemi.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblGrossSemi.setForeground(new java.awt.Color(140, 140, 140));
        lblGrossSemi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblGrossSemi.setText("Gross Semi-Monthly:");
        lblGrossSemi.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 20);
        pnlMyProfileSalary.add(lblGrossSemi, gridBagConstraints);

        txtGrossSemi.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtGrossSemi.setForeground(new java.awt.Color(30, 30, 30));
        txtGrossSemi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtGrossSemi.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        pnlMyProfileSalary.add(txtGrossSemi, gridBagConstraints);

        lblHourlyRate.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblHourlyRate.setForeground(new java.awt.Color(140, 140, 140));
        lblHourlyRate.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblHourlyRate.setText("Hourly Rate:");
        lblHourlyRate.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 20);
        pnlMyProfileSalary.add(lblHourlyRate, gridBagConstraints);

        txtHourlyRate.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtHourlyRate.setForeground(new java.awt.Color(30, 30, 30));
        txtHourlyRate.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtHourlyRate.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        pnlMyProfileSalary.add(txtHourlyRate, gridBagConstraints);

        vGlue3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        vGlue3.setForeground(new java.awt.Color(30, 30, 30));
        vGlue3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        vGlue3.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlMyProfileSalary.add(vGlue3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.33;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnlMyProfileRow3.add(pnlMyProfileSalary, gridBagConstraints);

        pnlMyProfileAllowance.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(null, "Allowance Details", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(153, 153, 153)), javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15))); // NOI18N
        pnlMyProfileAllowance.setMinimumSize(new java.awt.Dimension(10, 10));
        pnlMyProfileAllowance.setOpaque(false);
        pnlMyProfileAllowance.setLayout(new java.awt.GridBagLayout());

        lblRiceSubsidy.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblRiceSubsidy.setForeground(new java.awt.Color(140, 140, 140));
        lblRiceSubsidy.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblRiceSubsidy.setText("Rice Subsidy:");
        lblRiceSubsidy.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 20);
        pnlMyProfileAllowance.add(lblRiceSubsidy, gridBagConstraints);

        txtRiceSubsidy.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtRiceSubsidy.setForeground(new java.awt.Color(30, 30, 30));
        txtRiceSubsidy.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtRiceSubsidy.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        pnlMyProfileAllowance.add(txtRiceSubsidy, gridBagConstraints);

        lblPhoneAllowance.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblPhoneAllowance.setForeground(new java.awt.Color(140, 140, 140));
        lblPhoneAllowance.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblPhoneAllowance.setText("Phone Allowance:");
        lblPhoneAllowance.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 20);
        pnlMyProfileAllowance.add(lblPhoneAllowance, gridBagConstraints);

        txtPhoneAllowance.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtPhoneAllowance.setForeground(new java.awt.Color(30, 30, 30));
        txtPhoneAllowance.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtPhoneAllowance.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        pnlMyProfileAllowance.add(txtPhoneAllowance, gridBagConstraints);

        lblClothingAllowance.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblClothingAllowance.setForeground(new java.awt.Color(140, 140, 140));
        lblClothingAllowance.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblClothingAllowance.setText("Clothing Allowance:");
        lblClothingAllowance.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 20);
        pnlMyProfileAllowance.add(lblClothingAllowance, gridBagConstraints);

        txtClothingAllowance.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtClothingAllowance.setForeground(new java.awt.Color(30, 30, 30));
        txtClothingAllowance.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtClothingAllowance.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        pnlMyProfileAllowance.add(txtClothingAllowance, gridBagConstraints);

        vGlue2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        vGlue2.setForeground(new java.awt.Color(30, 30, 30));
        vGlue2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        vGlue2.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlMyProfileAllowance.add(vGlue2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.33;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnlMyProfileRow3.add(pnlMyProfileAllowance, gridBagConstraints);

        vGlue8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        vGlue8.setForeground(new java.awt.Color(30, 30, 30));
        vGlue8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        vGlue8.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlMyProfileRow3.add(vGlue8, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 20, 20);
        pnlMyProfile.add(pnlMyProfileRow3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weighty = 1.0;
        pnlMyProfile.add(pnlMyProfileVGlue, gridBagConstraints);

        ScrMyProfile.setViewportView(pnlMyProfile);

        tabEmployeeModule.addTab("My Profile", ScrMyProfile);

        pnlMyAttendance.setLayout(new java.awt.BorderLayout());

        pnlMyAttendanceCtrl.setBackground(new java.awt.Color(255, 255, 255));

        drpMyAttendanceMonth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "[Month]", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }));
        drpMyAttendanceMonth.setPreferredSize(new java.awt.Dimension(150, 30));
        drpMyAttendanceMonth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drpMyAttendanceMonthActionPerformed(evt);
            }
        });
        pnlMyAttendanceCtrl.add(drpMyAttendanceMonth);

        drpMyAttendanceYear.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "[Year]", "2020", "2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029", "2030" }));
        drpMyAttendanceYear.setPreferredSize(new java.awt.Dimension(150, 30));
        pnlMyAttendanceCtrl.add(drpMyAttendanceYear);

        pnlMyAttendance.add(pnlMyAttendanceCtrl, java.awt.BorderLayout.NORTH);

        jScrollPane5.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        tblMyAttendance.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Title 1"
            }
        ));
        tblMyAttendance.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblMyAttendance.setFillsViewportHeight(true);
        tblMyAttendance.setRowHeight(30);
        tblMyAttendance.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(tblMyAttendance);

        pnlMyAttendance.add(jScrollPane5, java.awt.BorderLayout.CENTER);

        tabEmployeeModule.addTab("My Attendance", pnlMyAttendance);

        pnlMyPayslip.setLayout(new java.awt.BorderLayout());

        pnlMyPayslipCtrl.setBackground(new java.awt.Color(255, 255, 255));

        drpMyPayslipMonth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "[Month]", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }));
        drpMyPayslipMonth.setPreferredSize(new java.awt.Dimension(150, 30));
        drpMyPayslipMonth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drpMyPayslipMonthActionPerformed(evt);
            }
        });
        pnlMyPayslipCtrl.add(drpMyPayslipMonth);

        drpMyPayslipPeriod.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "[Period]", "1st - 15th", "16th - End", "Whole Month" }));
        drpMyPayslipPeriod.setPreferredSize(new java.awt.Dimension(150, 30));
        drpMyPayslipPeriod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drpMyPayslipPeriodActionPerformed(evt);
            }
        });
        pnlMyPayslipCtrl.add(drpMyPayslipPeriod);

        drpMyPayslipYear.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "[Year]", "2020", "2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029", "2030" }));
        drpMyPayslipYear.setPreferredSize(new java.awt.Dimension(150, 30));
        pnlMyPayslipCtrl.add(drpMyPayslipYear);

        pnlMyPayslip.add(pnlMyPayslipCtrl, java.awt.BorderLayout.NORTH);

        ScrMyPayslip.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        pnlMyPayslipRow1.setBackground(new java.awt.Color(255, 255, 255));
        pnlMyPayslipRow1.setLayout(new java.awt.GridBagLayout());

        lblPayrollEID.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblPayrollEID.setForeground(new java.awt.Color(140, 140, 140));
        lblPayrollEID.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblPayrollEID.setText("Employee ID:");
        lblPayrollEID.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 20);
        pnlMyPayslipRow1.add(lblPayrollEID, gridBagConstraints);

        txtPayrollEID.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtPayrollEID.setForeground(new java.awt.Color(30, 30, 30));
        txtPayrollEID.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtPayrollEID.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        pnlMyPayslipRow1.add(txtPayrollEID, gridBagConstraints);

        lblPayrollName.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblPayrollName.setForeground(new java.awt.Color(140, 140, 140));
        lblPayrollName.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblPayrollName.setText("Name:");
        lblPayrollName.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 20);
        pnlMyPayslipRow1.add(lblPayrollName, gridBagConstraints);

        txtPayrollName.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtPayrollName.setForeground(new java.awt.Color(30, 30, 30));
        txtPayrollName.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtPayrollName.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        pnlMyPayslipRow1.add(txtPayrollName, gridBagConstraints);

        lblPayrollDate.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblPayrollDate.setForeground(new java.awt.Color(140, 140, 140));
        lblPayrollDate.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblPayrollDate.setText("Date Covered:");
        lblPayrollDate.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 20);
        pnlMyPayslipRow1.add(lblPayrollDate, gridBagConstraints);

        txtPayrollDate.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtPayrollDate.setForeground(new java.awt.Color(30, 30, 30));
        txtPayrollDate.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtPayrollDate.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        pnlMyPayslipRow1.add(txtPayrollDate, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weighty = 1.0;
        pnlMyPayslipRow1.add(pnlMyPayrollVGlue1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 10, 20);
        jPanel1.add(pnlMyPayslipRow1, gridBagConstraints);

        pnlMyPayslipRow2.setBackground(new java.awt.Color(255, 255, 255));
        pnlMyPayslipRow2.setLayout(new java.awt.GridBagLayout());

        pnlMyPayrollEarnings.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(null, "Earnings", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(153, 153, 153)), javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15))); // NOI18N
        pnlMyPayrollEarnings.setMinimumSize(new java.awt.Dimension(10, 10));
        pnlMyPayrollEarnings.setOpaque(false);
        pnlMyPayrollEarnings.setLayout(new java.awt.GridBagLayout());

        lblMyBasicPay.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblMyBasicPay.setForeground(new java.awt.Color(140, 140, 140));
        lblMyBasicPay.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblMyBasicPay.setText("Basic Pay:");
        lblMyBasicPay.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 20);
        pnlMyPayrollEarnings.add(lblMyBasicPay, gridBagConstraints);

        txtMyBasicPay.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtMyBasicPay.setForeground(new java.awt.Color(30, 30, 30));
        txtMyBasicPay.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtMyBasicPay.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        pnlMyPayrollEarnings.add(txtMyBasicPay, gridBagConstraints);

        lblMyAllowancePay.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblMyAllowancePay.setForeground(new java.awt.Color(140, 140, 140));
        lblMyAllowancePay.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblMyAllowancePay.setText("Allowances:");
        lblMyAllowancePay.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 20);
        pnlMyPayrollEarnings.add(lblMyAllowancePay, gridBagConstraints);

        txtMyAllowancePay.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtMyAllowancePay.setForeground(new java.awt.Color(30, 30, 30));
        txtMyAllowancePay.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtMyAllowancePay.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        pnlMyPayrollEarnings.add(txtMyAllowancePay, gridBagConstraints);

        lblMyOvertimePay.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblMyOvertimePay.setForeground(new java.awt.Color(140, 140, 140));
        lblMyOvertimePay.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblMyOvertimePay.setText("Overtime:");
        lblMyOvertimePay.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 20);
        pnlMyPayrollEarnings.add(lblMyOvertimePay, gridBagConstraints);

        txtMyOvertimePay.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtMyOvertimePay.setForeground(new java.awt.Color(30, 30, 30));
        txtMyOvertimePay.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtMyOvertimePay.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        pnlMyPayrollEarnings.add(txtMyOvertimePay, gridBagConstraints);

        vGlue6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        vGlue6.setForeground(new java.awt.Color(30, 30, 30));
        vGlue6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        vGlue6.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlMyPayrollEarnings.add(vGlue6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        pnlMyPayslipRow2.add(pnlMyPayrollEarnings, gridBagConstraints);

        pnlMyPayrollDeductions.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(null, "Deductions", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(153, 153, 153)), javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15))); // NOI18N
        pnlMyPayrollDeductions.setMinimumSize(new java.awt.Dimension(10, 10));
        pnlMyPayrollDeductions.setOpaque(false);
        pnlMyPayrollDeductions.setLayout(new java.awt.GridBagLayout());

        lblMyLateDeduc.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblMyLateDeduc.setForeground(new java.awt.Color(140, 140, 140));
        lblMyLateDeduc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblMyLateDeduc.setText("Late:");
        lblMyLateDeduc.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 20);
        pnlMyPayrollDeductions.add(lblMyLateDeduc, gridBagConstraints);

        txtMyLateDeduc.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtMyLateDeduc.setForeground(new java.awt.Color(30, 30, 30));
        txtMyLateDeduc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtMyLateDeduc.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        pnlMyPayrollDeductions.add(txtMyLateDeduc, gridBagConstraints);

        lblMyTaxDeduc.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblMyTaxDeduc.setForeground(new java.awt.Color(140, 140, 140));
        lblMyTaxDeduc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblMyTaxDeduc.setText("Withholding Tax:");
        lblMyTaxDeduc.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 20);
        pnlMyPayrollDeductions.add(lblMyTaxDeduc, gridBagConstraints);

        txtMyTaxDeduc.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtMyTaxDeduc.setForeground(new java.awt.Color(30, 30, 30));
        txtMyTaxDeduc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtMyTaxDeduc.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        pnlMyPayrollDeductions.add(txtMyTaxDeduc, gridBagConstraints);

        lblMySSSDeduc.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblMySSSDeduc.setForeground(new java.awt.Color(140, 140, 140));
        lblMySSSDeduc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblMySSSDeduc.setText("SSS:");
        lblMySSSDeduc.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 20);
        pnlMyPayrollDeductions.add(lblMySSSDeduc, gridBagConstraints);

        txtMySSSDeduc.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtMySSSDeduc.setForeground(new java.awt.Color(30, 30, 30));
        txtMySSSDeduc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtMySSSDeduc.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        pnlMyPayrollDeductions.add(txtMySSSDeduc, gridBagConstraints);

        lblMyPhilHealthDeduc.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblMyPhilHealthDeduc.setForeground(new java.awt.Color(140, 140, 140));
        lblMyPhilHealthDeduc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblMyPhilHealthDeduc.setText("PhilHealth:");
        lblMyPhilHealthDeduc.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 20);
        pnlMyPayrollDeductions.add(lblMyPhilHealthDeduc, gridBagConstraints);

        txtMyPhilHealthDeduc.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtMyPhilHealthDeduc.setForeground(new java.awt.Color(30, 30, 30));
        txtMyPhilHealthDeduc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtMyPhilHealthDeduc.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        pnlMyPayrollDeductions.add(txtMyPhilHealthDeduc, gridBagConstraints);

        lblMyPagibigDeduc.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblMyPagibigDeduc.setForeground(new java.awt.Color(140, 140, 140));
        lblMyPagibigDeduc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblMyPagibigDeduc.setText("Pagibig:");
        lblMyPagibigDeduc.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 20);
        pnlMyPayrollDeductions.add(lblMyPagibigDeduc, gridBagConstraints);

        txtMyPagibigDeduc.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtMyPagibigDeduc.setForeground(new java.awt.Color(30, 30, 30));
        txtMyPagibigDeduc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtMyPagibigDeduc.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        pnlMyPayrollDeductions.add(txtMyPagibigDeduc, gridBagConstraints);

        vGlue5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        vGlue5.setForeground(new java.awt.Color(30, 30, 30));
        vGlue5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        vGlue5.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlMyPayrollDeductions.add(vGlue5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        pnlMyPayslipRow2.add(pnlMyPayrollDeductions, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weighty = 1.0;
        pnlMyPayslipRow2.add(pnlMyPayrollVGlue2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 10, 20);
        jPanel1.add(pnlMyPayslipRow2, gridBagConstraints);

        pnlMyPayslipRow3.setBackground(new java.awt.Color(255, 255, 255));
        pnlMyPayslipRow3.setLayout(new java.awt.GridBagLayout());

        pnlMyPayrollSummary.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(null, "Pay Summary", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(153, 153, 153)), javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15))); // NOI18N
        pnlMyPayrollSummary.setMinimumSize(new java.awt.Dimension(10, 10));
        pnlMyPayrollSummary.setOpaque(false);
        pnlMyPayrollSummary.setLayout(new java.awt.GridBagLayout());

        lblMyGrossPay.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblMyGrossPay.setForeground(new java.awt.Color(140, 140, 140));
        lblMyGrossPay.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblMyGrossPay.setText("Gross Pay:");
        lblMyGrossPay.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 20);
        pnlMyPayrollSummary.add(lblMyGrossPay, gridBagConstraints);

        txtMyGrossPay.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtMyGrossPay.setForeground(new java.awt.Color(30, 30, 30));
        txtMyGrossPay.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtMyGrossPay.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        pnlMyPayrollSummary.add(txtMyGrossPay, gridBagConstraints);

        lblMyTotalDeductions.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblMyTotalDeductions.setForeground(new java.awt.Color(140, 140, 140));
        lblMyTotalDeductions.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblMyTotalDeductions.setText("Total Deductions:");
        lblMyTotalDeductions.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 20);
        pnlMyPayrollSummary.add(lblMyTotalDeductions, gridBagConstraints);

        txtMyTotalDeductions.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtMyTotalDeductions.setForeground(new java.awt.Color(30, 30, 30));
        txtMyTotalDeductions.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtMyTotalDeductions.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        pnlMyPayrollSummary.add(txtMyTotalDeductions, gridBagConstraints);

        lblMyNetPay.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblMyNetPay.setForeground(new java.awt.Color(140, 140, 140));
        lblMyNetPay.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblMyNetPay.setText("Net Pay:");
        lblMyNetPay.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 20);
        pnlMyPayrollSummary.add(lblMyNetPay, gridBagConstraints);

        txtMyNetPay.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtMyNetPay.setForeground(new java.awt.Color(30, 30, 30));
        txtMyNetPay.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtMyNetPay.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 10);
        pnlMyPayrollSummary.add(txtMyNetPay, gridBagConstraints);

        vGlue7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        vGlue7.setForeground(new java.awt.Color(30, 30, 30));
        vGlue7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        vGlue7.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlMyPayrollSummary.add(vGlue7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        pnlMyPayslipRow3.add(pnlMyPayrollSummary, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weighty = 1.0;
        pnlMyPayslipRow3.add(pnlMyPayrollVGlue3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 20);
        jPanel1.add(pnlMyPayslipRow3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(pnlMyPayslipVGlue, gridBagConstraints);

        ScrMyPayslip.setViewportView(jPanel1);

        pnlMyPayslip.add(ScrMyPayslip, java.awt.BorderLayout.CENTER);

        tabEmployeeModule.addTab("My Payroll", pnlMyPayslip);

        pnlLeaveRequest.setLayout(new java.awt.BorderLayout());

        pnlLeaveRequestCtrl.setBackground(new java.awt.Color(255, 255, 255));
        pnlLeaveRequestCtrl.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(null, "Request Leave Form", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(204, 204, 204)), javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15))); // NOI18N

        drpLeaveRequestType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "[Leave Type]", "Sick Leave", "Vacation Leave", "Emergency", "Maternity/Paternity", "Etc" }));
        drpLeaveRequestType.setPreferredSize(new java.awt.Dimension(110, 30));
        pnlLeaveRequestCtrl.add(drpLeaveRequestType);

        txtLeaveRequestStart.setPreferredSize(new java.awt.Dimension(120, 30));
        txtLeaveRequestStart.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtLeaveRequestStartPropertyChange(evt);
            }
        });
        pnlLeaveRequestCtrl.add(txtLeaveRequestStart);

        txtLeaveRequestEnd.setPreferredSize(new java.awt.Dimension(120, 30));
        pnlLeaveRequestCtrl.add(txtLeaveRequestEnd);

        txtLeaveRequestReason.setText("Reason");
        txtLeaveRequestReason.setPreferredSize(new java.awt.Dimension(180, 30));
        pnlLeaveRequestCtrl.add(txtLeaveRequestReason);

        btnLeaveRequestSend.setText("Send Request");
        btnLeaveRequestSend.setPreferredSize(new java.awt.Dimension(120, 30));
        btnLeaveRequestSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLeaveRequestSendActionPerformed(evt);
            }
        });
        pnlLeaveRequestCtrl.add(btnLeaveRequestSend);

        btnLeaveRequestClear.setText("Clear");
        btnLeaveRequestClear.setPreferredSize(new java.awt.Dimension(100, 30));
        btnLeaveRequestClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLeaveRequestClearActionPerformed(evt);
            }
        });
        pnlLeaveRequestCtrl.add(btnLeaveRequestClear);
        pnlLeaveRequestCtrl.add(pnlReqLeaveVGlue10);

        pnlLeaveRequest.add(pnlLeaveRequestCtrl, java.awt.BorderLayout.NORTH);

        jSplitPane2.setBackground(new java.awt.Color(255, 255, 255));
        jSplitPane2.setDividerLocation(350);
        jSplitPane2.setDividerSize(8);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setResizeWeight(1.0);
        jSplitPane2.setOneTouchExpandable(true);
        jSplitPane2.setOpaque(false);

        jScrollPane13.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane13.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jScrollPane13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jScrollPane13MouseClicked(evt);
            }
        });

        tblLeaveRequest.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblLeaveRequest.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblLeaveRequest.setFillsViewportHeight(true);
        tblLeaveRequest.setRowHeight(30);
        tblLeaveRequest.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblLeaveRequest.setShowGrid(true);
        tblLeaveRequest.getTableHeader().setReorderingAllowed(false);
        tblLeaveRequest.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblLeaveRequestMouseClicked(evt);
            }
        });
        jScrollPane13.setViewportView(tblLeaveRequest);

        jSplitPane2.setTopComponent(jScrollPane13);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setLayout(new java.awt.GridBagLayout());

        pnlLeaveRequestMsg.setBackground(new java.awt.Color(255, 255, 255));
        pnlLeaveRequestMsg.setLayout(new java.awt.GridBagLayout());

        pnlLeaveRequestReason.setBackground(new java.awt.Color(255, 255, 255));
        pnlLeaveRequestReason.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(null, "Employee Reason", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(153, 153, 153)), javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15))); // NOI18N
        pnlLeaveRequestReason.setMinimumSize(new java.awt.Dimension(0, 0));
        pnlLeaveRequestReason.setOpaque(false);
        pnlLeaveRequestReason.setLayout(new java.awt.GridBagLayout());

        jScrollPane14.setBackground(new java.awt.Color(255, 255, 255));

        txtReqLeaveReason1.setEditable(false);
        txtReqLeaveReason1.setBackground(new java.awt.Color(255, 255, 255));
        txtReqLeaveReason1.setLineWrap(true);
        txtReqLeaveReason1.setRows(4);
        txtReqLeaveReason1.setWrapStyleWord(true);
        txtReqLeaveReason1.setBorder(null);
        txtReqLeaveReason1.setFocusable(false);
        txtReqLeaveReason1.setMinimumSize(new java.awt.Dimension(10, 10));
        jScrollPane14.setViewportView(txtReqLeaveReason1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlLeaveRequestReason.add(jScrollPane14, gridBagConstraints);

        vGlue14.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        vGlue14.setForeground(new java.awt.Color(30, 30, 30));
        vGlue14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        vGlue14.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlLeaveRequestReason.add(vGlue14, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        pnlLeaveRequestMsg.add(pnlLeaveRequestReason, gridBagConstraints);

        pnlLeaveRequestDecision.setBackground(new java.awt.Color(255, 255, 255));
        pnlLeaveRequestDecision.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(null, "Decision", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(153, 153, 153)), javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15))); // NOI18N
        pnlLeaveRequestDecision.setMinimumSize(new java.awt.Dimension(0, 0));
        pnlLeaveRequestDecision.setOpaque(false);
        pnlLeaveRequestDecision.setLayout(new java.awt.GridBagLayout());

        txtReqLeaveDecision.setEditable(false);
        txtReqLeaveDecision.setBackground(new java.awt.Color(255, 255, 255));
        txtReqLeaveDecision.setLineWrap(true);
        txtReqLeaveDecision.setRows(4);
        txtReqLeaveDecision.setWrapStyleWord(true);
        txtReqLeaveDecision.setBorder(null);
        txtReqLeaveDecision.setFocusable(false);
        txtReqLeaveDecision.setMinimumSize(new java.awt.Dimension(10, 10));
        jScrollPane15.setViewportView(txtReqLeaveDecision);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlLeaveRequestDecision.add(jScrollPane15, gridBagConstraints);

        vGlue15.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        vGlue15.setForeground(new java.awt.Color(30, 30, 30));
        vGlue15.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        vGlue15.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlLeaveRequestDecision.add(vGlue15, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        pnlLeaveRequestMsg.add(pnlLeaveRequestDecision, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weighty = 1.0;
        pnlLeaveRequestMsg.add(pnlLeaveRequestVGlue11, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 10, 20);
        jPanel5.add(pnlLeaveRequestMsg, gridBagConstraints);

        jSplitPane2.setRightComponent(jPanel5);

        pnlLeaveRequest.add(jSplitPane2, java.awt.BorderLayout.CENTER);

        tabEmployeeModule.addTab("Request Leave", pnlLeaveRequest);

        pnlEmployeeModule.add(tabEmployeeModule, java.awt.BorderLayout.CENTER);

        add(pnlEmployeeModule, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void drpMyAttendanceMonthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drpMyAttendanceMonthActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_drpMyAttendanceMonthActionPerformed

    private void drpMyPayslipMonthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drpMyPayslipMonthActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_drpMyPayslipMonthActionPerformed

    private void drpMyPayslipPeriodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drpMyPayslipPeriodActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_drpMyPayslipPeriodActionPerformed

    private void jScrollPane13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane13MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jScrollPane13MouseClicked

    private void tblLeaveRequestMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLeaveRequestMouseClicked
    int row = tblLeaveRequest.getSelectedRow();
    if (row != -1) {
        String id = tblLeaveRequest.getValueAt(row, 0).toString();
        if (id.equals("No leave request data available")) return;

        models.UserAccount account = services.AuthServ.getLoggedInUser();
        java.util.List<models.LeaveRecord> history = leaveServ.getEmployeeLeaveHistory(account.getEmployeeId());
        
        models.LeaveRecord selected = history.get(row);
        
         txtReqLeaveReason1.setText(selected.getReason());
        txtReqLeaveDecision.setText(selected.getStatus());
    }
    }//GEN-LAST:event_tblLeaveRequestMouseClicked

    private void txtLeaveRequestStartPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtLeaveRequestStartPropertyChange
    if ("date".equals(evt.getPropertyName())) {
        java.util.Date selectedStart = txtLeaveRequestStart.getDate();
        if (selectedStart != null) {
            // This allows the End Date to be the SAME as the Start Date
            txtLeaveRequestEnd.getJCalendar().setMinSelectableDate(selectedStart);
            
            // If the user previously picked an end date that is now invalid,
            // automatically move it to match the start date.
            if (txtLeaveRequestEnd.getDate() != null && txtLeaveRequestEnd.getDate().before(selectedStart)) {
                txtLeaveRequestEnd.setDate(selectedStart);
            }
        }
    }
    }//GEN-LAST:event_txtLeaveRequestStartPropertyChange

    private void btnLeaveRequestSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLeaveRequestSendActionPerformed
    submitLeaveRequest();
    refreshLeaveRequestTable();
    }//GEN-LAST:event_btnLeaveRequestSendActionPerformed

    private void btnLeaveRequestClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLeaveRequestClearActionPerformed
    clearLeaveRequestForm();
    }//GEN-LAST:event_btnLeaveRequestClearActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane ScrMyPayslip;
    private javax.swing.JScrollPane ScrMyProfile;
    private javax.swing.JButton btnLeaveRequestClear;
    private javax.swing.JButton btnLeaveRequestSend;
    private javax.swing.JComboBox<String> drpLeaveRequestType;
    private javax.swing.JComboBox<String> drpMyAttendanceMonth;
    private javax.swing.JComboBox<String> drpMyAttendanceYear;
    private javax.swing.JComboBox<String> drpMyPayslipMonth;
    private javax.swing.JComboBox<String> drpMyPayslipPeriod;
    private javax.swing.JComboBox<String> drpMyPayslipYear;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JLabel lblAddress;
    private javax.swing.JLabel lblBasicRate;
    private javax.swing.JLabel lblBirthday;
    private javax.swing.JLabel lblClothingAllowance;
    private javax.swing.JLabel lblDepartment;
    private javax.swing.JLabel lblEmployeeID;
    private javax.swing.JLabel lblFullName;
    private javax.swing.JLabel lblGrossSemi;
    private javax.swing.JLabel lblHourlyRate;
    private javax.swing.JLabel lblMyAllowancePay;
    private javax.swing.JLabel lblMyBasicPay;
    private javax.swing.JLabel lblMyGrossPay;
    private javax.swing.JLabel lblMyLateDeduc;
    private javax.swing.JLabel lblMyNetPay;
    private javax.swing.JLabel lblMyOvertimePay;
    private javax.swing.JLabel lblMyPagibigDeduc;
    private javax.swing.JLabel lblMyPhilHealthDeduc;
    private javax.swing.JLabel lblMyProfilePic;
    private javax.swing.JLabel lblMySSSDeduc;
    private javax.swing.JLabel lblMyTaxDeduc;
    private javax.swing.JLabel lblMyTotalDeductions;
    private javax.swing.JLabel lblPagibig;
    private javax.swing.JLabel lblPayrollDate;
    private javax.swing.JLabel lblPayrollEID;
    private javax.swing.JLabel lblPayrollName;
    private javax.swing.JLabel lblPhilHealth;
    private javax.swing.JLabel lblPhoneAllowance;
    private javax.swing.JLabel lblPhoneNumber;
    private javax.swing.JLabel lblPosition;
    private javax.swing.JLabel lblRiceSubsidy;
    private javax.swing.JLabel lblSSS;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblSupervisor;
    private javax.swing.JLabel lblTIN;
    private javax.swing.JPanel pnlEmployeeModule;
    private javax.swing.JPanel pnlLeaveRequest;
    private javax.swing.JPanel pnlLeaveRequestCtrl;
    private javax.swing.JPanel pnlLeaveRequestDecision;
    private javax.swing.JPanel pnlLeaveRequestMsg;
    private javax.swing.JPanel pnlLeaveRequestReason;
    private javax.swing.JLabel pnlLeaveRequestVGlue11;
    private javax.swing.JPanel pnlMyAttendance;
    private javax.swing.JPanel pnlMyAttendanceCtrl;
    private javax.swing.JPanel pnlMyPayrollDeductions;
    private javax.swing.JPanel pnlMyPayrollEarnings;
    private javax.swing.JPanel pnlMyPayrollSummary;
    private javax.swing.JLabel pnlMyPayrollVGlue1;
    private javax.swing.JLabel pnlMyPayrollVGlue2;
    private javax.swing.JLabel pnlMyPayrollVGlue3;
    private javax.swing.JPanel pnlMyPayslip;
    private javax.swing.JPanel pnlMyPayslipCtrl;
    private javax.swing.JPanel pnlMyPayslipRow1;
    private javax.swing.JPanel pnlMyPayslipRow2;
    private javax.swing.JPanel pnlMyPayslipRow3;
    private javax.swing.JLabel pnlMyPayslipVGlue;
    private javax.swing.JPanel pnlMyProfile;
    private javax.swing.JPanel pnlMyProfileAllowance;
    private javax.swing.JPanel pnlMyProfileEmployee;
    private javax.swing.JPanel pnlMyProfileEmployeeDetails;
    private javax.swing.JPanel pnlMyProfilePersonal;
    private javax.swing.JPanel pnlMyProfileRow1;
    private javax.swing.JPanel pnlMyProfileRow2;
    private javax.swing.JPanel pnlMyProfileRow3;
    private javax.swing.JPanel pnlMyProfileSalary;
    private javax.swing.JPanel pnlMyProfileStatutory;
    private javax.swing.JLabel pnlMyProfileVGlue;
    private javax.swing.JLabel pnlReqLeaveVGlue10;
    private javax.swing.JTabbedPane tabEmployeeModule;
    private javax.swing.JTable tblLeaveRequest;
    private javax.swing.JTable tblMyAttendance;
    private javax.swing.JTextArea txtAddress;
    private javax.swing.JLabel txtBasicRate;
    private javax.swing.JLabel txtBirthday;
    private javax.swing.JLabel txtClothingAllowance;
    private javax.swing.JLabel txtDepartment;
    private javax.swing.JLabel txtGrossSemi;
    private javax.swing.JLabel txtHourlyRate;
    private com.toedter.calendar.JDateChooser txtLeaveRequestEnd;
    private javax.swing.JTextField txtLeaveRequestReason;
    private com.toedter.calendar.JDateChooser txtLeaveRequestStart;
    private javax.swing.JLabel txtMyAllowancePay;
    private javax.swing.JLabel txtMyBasicPay;
    private javax.swing.JLabel txtMyGrossPay;
    private javax.swing.JLabel txtMyLateDeduc;
    private javax.swing.JLabel txtMyNetPay;
    private javax.swing.JLabel txtMyOvertimePay;
    private javax.swing.JLabel txtMyPagibigDeduc;
    private javax.swing.JLabel txtMyPhilHealthDeduc;
    private javax.swing.JLabel txtMySSSDeduc;
    private javax.swing.JLabel txtMyTaxDeduc;
    private javax.swing.JLabel txtMyTotalDeductions;
    private javax.swing.JLabel txtPagibig;
    private javax.swing.JLabel txtPayrollDate;
    private javax.swing.JLabel txtPayrollEID;
    private javax.swing.JLabel txtPayrollName;
    private javax.swing.JLabel txtPhilHealth;
    private javax.swing.JLabel txtPhoneAllowance;
    private javax.swing.JLabel txtPhoneNumber;
    private javax.swing.JLabel txtPosition;
    private javax.swing.JTextArea txtReqLeaveDecision;
    private javax.swing.JTextArea txtReqLeaveReason1;
    private javax.swing.JLabel txtRiceSubsidy;
    private javax.swing.JLabel txtSSS;
    private javax.swing.JLabel txtStatus;
    private javax.swing.JLabel txtSupervisor;
    private javax.swing.JLabel txtTIN;
    private javax.swing.JLabel vGlue;
    private javax.swing.JLabel vGlue1;
    private javax.swing.JLabel vGlue14;
    private javax.swing.JLabel vGlue15;
    private javax.swing.JLabel vGlue2;
    private javax.swing.JLabel vGlue3;
    private javax.swing.JLabel vGlue4;
    private javax.swing.JLabel vGlue5;
    private javax.swing.JLabel vGlue6;
    private javax.swing.JLabel vGlue7;
    private javax.swing.JLabel vGlue8;
    // End of variables declaration//GEN-END:variables

    

}
