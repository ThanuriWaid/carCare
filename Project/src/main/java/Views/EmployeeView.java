package Views;

import Controllers.EmployeeController;
import Models.Employee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Vector;

public class EmployeeView extends JFrame {

    private JPanel dashboardPanel;
    private JPanel rightPanel;
    private JLabel companyName;
    private JButton orderViewButton;
    private JButton repaintOrdersButton;
    private JButton manageInventoryButton;
    private JButton sellersButton;
    private JButton employeesButton;
    private JButton repairOrdersButton;
    private JTextField productCode;
    private JTextField productName;
    private JButton addButton;
    private JButton deleteButton;
    private JButton EditBtn;
    private JTable orders;

    EmployeeController employeeController;

    public EmployeeView() {
        updateTable();
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String employeeEmail = productCode.getText();
                String employeeName = productName.getText();
                employeeController = new EmployeeController();
                Employee employee = employeeController.addEmployee(employeeEmail,employeeName);
                if(employeeController.addEmployeeToDatabase()){
                    JOptionPane.showMessageDialog(dashboardPanel, "Employee Inserted", "Success", 1);
                    updateTable();
                }
                else{
                    JOptionPane.showMessageDialog(dashboardPanel, "Could not add employee", "Error", 0);
                }
                productCode.setText("");
                productName.setText("");
            }
        });
        orders.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                DefaultTableModel model = (DefaultTableModel)orders.getModel();
                int selectedIndex = orders.getSelectedRow();

                productCode.setText(model.getValueAt(selectedIndex, 0).toString());
                productName.setText(model.getValueAt(selectedIndex, 1).toString());
                super.mouseClicked(e);
            }
        });
        EditBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel)orders.getModel();
                int selectedIndex = orders.getSelectedRow();

                String email = model.getValueAt(selectedIndex, 0).toString();
                String name = productName.getText();
                employeeController = new EmployeeController();
                try {
                    if(employeeController.updateEmployeeDB(email, name)){
                        JOptionPane.showMessageDialog(dashboardPanel, "Employee Updated", "Success", 1);
                        updateTable();
                        productCode.setText("");
                        productName.setText("");
                    }
                    else{
                        JOptionPane.showMessageDialog(dashboardPanel, "Could not update employee", "Error", 0);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dashboardPanel, "Could not update employee", "Error", 0);
                }
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel)orders.getModel();
                int selectedIndex = orders.getSelectedRow();

                String email = model.getValueAt(selectedIndex, 0).toString();
                employeeController = new EmployeeController();
                try {
                    if(employeeController.deleteEmployeeDB(email)){
                        JOptionPane.showMessageDialog(dashboardPanel, "Employee deleted", "Success", 1);
                        updateTable();
                    }
                    else{
                        JOptionPane.showMessageDialog(dashboardPanel, "Could not delete employee", "Error", 0);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dashboardPanel, "Could not delete employee", "Error", 0);
                }
            }
        });
    }

    public void updateTable() {
        employeeController = new EmployeeController();
        try {
            ResultSet employees = employeeController.findEmployees();
            ResultSetMetaData resultSetMetaData = employees.getMetaData();
            int c = resultSetMetaData.getColumnCount();

            DefaultTableModel model = (DefaultTableModel)orders.getModel();
            model.setRowCount(0);
            model.setColumnCount(2);
            Vector vector = new Vector();

            for (int i=1; i<=c; i++) {
                vector.add(employees.getString("email"));
                vector.add(employees.getString("name"));
            }
            model.addRow(vector);
            while (employees.next()) {
                Vector vector1 = new Vector();

                for (int i=1; i<=c; i++) {
                    vector1.add(employees.getString("email"));
                    vector1.add(employees.getString("name"));
                }
                model.addRow(vector1);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dashboardPanel, "Could not fetch employee details", "Error", 0);
            System.out.println(ex.getMessage());
        }
    }

    public static void main(String[] args) {
        EmployeeView employeeView = new EmployeeView();
        employeeView.setContentPane(employeeView.dashboardPanel);
        employeeView.setTitle("Employee view");
        employeeView.setSize(1200, 600);
        employeeView.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        employeeView.setVisible(true);
    }

    private void createUIComponents() {
        rightPanel = new JPanel();

        rightPanel.setPreferredSize(new Dimension(200, 600));

        // Company icon
        ImageIcon icon = new ImageIcon("D:\\SLIIT\\1st Year\\Semester 2\\OOP\\OOP Project - Java Application\\car care\\carCare\\Project\\src\\main\\java\\Views\\images\\logo.png");
        Image iconImage = icon.getImage();
        Image modified = iconImage.getScaledInstance(120, 80, Image.SCALE_SMOOTH);
        icon = new ImageIcon(modified);
        companyName = new JLabel( icon);

        // Order view icon
        ImageIcon orderViewIcon = new ImageIcon("D:\\SLIIT\\1st Year\\Semester 2\\OOP\\OOP Project - Java Application\\car care\\carCare\\Project\\src\\main\\java\\Views\\images\\orderView.png");
        Image orderViewIconImage = orderViewIcon.getImage();
        Image modifiedOrderViewIcon = orderViewIconImage.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        orderViewIcon = new ImageIcon(modifiedOrderViewIcon);
        orderViewButton = new JButton( orderViewIcon);

        // Repair view icon
        ImageIcon repairIcon = new ImageIcon("D:\\SLIIT\\1st Year\\Semester 2\\OOP\\OOP Project - Java Application\\car care\\carCare\\Project\\src\\main\\java\\Views\\images\\repair.png");
        Image repairIconImage = repairIcon.getImage();
        Image modifiedRepairIcon = repairIconImage.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        repairIcon = new ImageIcon(modifiedRepairIcon);
        repairOrdersButton = new JButton(repairIcon);

        // Repaint view icon
        ImageIcon repaintIcon = new ImageIcon("D:\\SLIIT\\1st Year\\Semester 2\\OOP\\OOP Project - Java Application\\car care\\carCare\\Project\\src\\main\\java\\Views\\images\\repaint.png");
        Image repaintIconImage = repaintIcon.getImage();
        Image modifiedRepaintIcon = repaintIconImage.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        repaintIcon = new ImageIcon(modifiedRepaintIcon);
        repaintOrdersButton = new JButton(repaintIcon);

        // Inventory icon
        ImageIcon inventoryIcon = new ImageIcon("D:\\SLIIT\\1st Year\\Semester 2\\OOP\\OOP Project - Java Application\\car care\\carCare\\Project\\src\\main\\java\\Views\\images\\inventory.png");
        Image inventoryIconImage = inventoryIcon.getImage();
        Image modifiedInventoryIcon = inventoryIconImage.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        inventoryIcon = new ImageIcon(modifiedInventoryIcon);
        manageInventoryButton = new JButton(inventoryIcon);

        // Sellers icon
        ImageIcon sellersIcon = new ImageIcon("D:\\SLIIT\\1st Year\\Semester 2\\OOP\\OOP Project - Java Application\\car care\\carCare\\Project\\src\\main\\java\\Views\\images\\sellers.png");
        Image sellersIconImage = sellersIcon.getImage();
        Image modifiedSellersIcon = sellersIconImage.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        sellersIcon = new ImageIcon(modifiedSellersIcon);
        sellersButton = new JButton(sellersIcon);

        // Employee icon
        ImageIcon employeeIcon = new ImageIcon("D:\\SLIIT\\1st Year\\Semester 2\\OOP\\OOP Project - Java Application\\car care\\carCare\\Project\\src\\main\\java\\Views\\images\\employee.png");
        Image employeeIconImage = employeeIcon.getImage();
        Image modifiedEmployeeIcon = employeeIconImage.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        employeeIcon = new ImageIcon(modifiedEmployeeIcon);
        employeesButton = new JButton(employeeIcon);

        // Add icon
        ImageIcon addIcon = new ImageIcon("D:\\SLIIT\\1st Year\\Semester 2\\OOP\\OOP Project - Java Application\\car care\\carCare\\Project\\src\\main\\java\\Views\\images\\add.png");
        Image addIconImage = addIcon.getImage();
        Image modifiedAddIcon = addIconImage.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        addIcon = new ImageIcon(modifiedAddIcon);
        addButton = new JButton(addIcon);

        // Delete icon
        ImageIcon deleteIcon = new ImageIcon("D:\\SLIIT\\1st Year\\Semester 2\\OOP\\OOP Project - Java Application\\car care\\carCare\\Project\\src\\main\\java\\Views\\images\\delete.png");
        Image deleteIconImage = deleteIcon.getImage();
        Image modifiedDeleteIcon = deleteIconImage.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        deleteIcon = new ImageIcon(modifiedDeleteIcon);
        deleteButton = new JButton(deleteIcon);

        // Pay icon
        ImageIcon payIcon = new ImageIcon("D:\\SLIIT\\1st Year\\Semester 2\\OOP\\OOP Project - Java Application\\car care\\carCare\\Project\\src\\main\\java\\Views\\images\\payInvoice.png");
        Image payIconImage = payIcon.getImage();
        Image modifiedPayIcon = payIconImage.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        payIcon = new ImageIcon(modifiedPayIcon);
        EditBtn = new JButton(payIcon);
    }
}
