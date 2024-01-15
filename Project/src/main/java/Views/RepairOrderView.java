package Views;

import Controllers.OrderController;
import Models.RepairOrder;
import Models.SendMailWithAttachment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Vector;

public class RepairOrderView extends JFrame {
    private JPanel dashboardPanel;
    private JLabel companyName;
    private JButton orderViewButton;
    private JButton repairOrdersButton;
    private JButton repaintOrdersButton;
    private JButton manageInventoryButton;
    private JButton sellersButton;
    private JButton employeesButton;
    private JTextField email;
    private JTextField description;
    private JTextField servicePrice;
    private JButton addButton;
    private JButton deleteButton;
    private JButton payInvoiceButton;
    private JTextField employeeEmail;
    private JButton statusBtn;
    private JTable orders;
    private JPanel rightPanel;

    OrderController orderController;
    public RepairOrderView() {
        updateTable();
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String customerEmail = email.getText();
                String des = description.getText();
                Double amount = Double.parseDouble(servicePrice.getText());
                String empEmail = employeeEmail.getText();
                orderController = new OrderController();
                boolean status = true;

                RepairOrder repairOrder = orderController.addRepairOrder(customerEmail, amount, des, empEmail);
                try {
                    SendMailWithAttachment sendMailWithAttachment = new SendMailWithAttachment();
                    sendMailWithAttachment.send(empEmail,"New Order","A new order has been appointed to you!");
                    System.out.println("Message sent");
                }catch (Exception ex){
                    JOptionPane.showMessageDialog(dashboardPanel, "Email is invalid", "Error", 0);
                    status = false;
                }

                if(status){
                    if(orderController.addRepairOrderToDatabase())
                    {
                        JOptionPane.showMessageDialog(dashboardPanel, "Successfully Added a repair order to Database", "Sucess", 1);
                        updateTable();
                    }else {
                        JOptionPane.showMessageDialog(dashboardPanel, "Cannot insert a repair order to DB", "Error", 1);
                    }
                }else{
                    JOptionPane.showMessageDialog(dashboardPanel, "Email is invalid", "Error", 0);
                }

                employeeEmail.setText("");
                description.setText("");
                servicePrice.setText("");
                email.setText("");
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel)orders.getModel();
                int selectedIndex = orders.getSelectedRow();

                String id = model.getValueAt(selectedIndex, 0).toString();
                orderController = new OrderController();
                try {
                    if(orderController.deleteRepairOrder(id)){
                        JOptionPane.showMessageDialog(dashboardPanel, "Repair order deleted.", "Success", 1);
                        updateTable();
                    }
                    else{
                        JOptionPane.showMessageDialog(dashboardPanel, "Could not delete the repair order", "Error", 0);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dashboardPanel, "Could not delete repair order", "Error", 0);
                }
            }
        });

        statusBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel)orders.getModel();
                int selectedIndex = orders.getSelectedRow();

                String status = model.getValueAt(selectedIndex, 3).toString();
                String id = model.getValueAt(selectedIndex, 0).toString();
                String customerEmail = model.getValueAt(selectedIndex,1).toString();
                orderController = new OrderController();

                if (status.equals("0")) {
                    try {
                        if(orderController.updateRepairOrderStatus(id)){
                            JOptionPane.showMessageDialog(dashboardPanel, "Repair order is ready now.", "Success", 1);
                            SendMailWithAttachment sendMailWithAttachment = new SendMailWithAttachment();
                            sendMailWithAttachment.send(customerEmail,"Vehicle is ready","Your vehicle has been repaired. Please come and collect your vehicle!");
                            System.out.println("Message sent");
                            updateTable();
                        }
                        else {
                            JOptionPane.showMessageDialog(dashboardPanel, "Could not update the repair order.", "Error", 0);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dashboardPanel, "Could not update the repair order.", "Error", 0);
                    }
                } else {
                    JOptionPane.showMessageDialog(dashboardPanel, "Already updated this repair order.", "Error", 0);
                }
            }
        });
    }

    public void updateTable() {
        orderController = new OrderController();
        try {
            ResultSet repairOrders = orderController.findRepairOrders();
            ResultSetMetaData resultSetMetaData = repairOrders.getMetaData();
            int c = resultSetMetaData.getColumnCount();

            DefaultTableModel model = (DefaultTableModel)orders.getModel();
            model.setRowCount(0);
            model.setColumnCount(6);
            Vector vector = new Vector();

            for (int i=1; i<=c; i++) {
                vector.add(repairOrders.getString("order_id"));
                vector.add(repairOrders.getString("customer_email"));
                vector.add(repairOrders.getString("employee_email"));
                vector.add(repairOrders.getString("status"));
                vector.add(repairOrders.getString("description"));
                vector.add(repairOrders.getString("amount"));
            }
            model.addRow(vector);
            while (repairOrders.next()) {
                Vector vector1 = new Vector();

                for (int i=1; i<=c; i++) {
                    vector1.add(repairOrders.getString("order_id"));
                    vector1.add(repairOrders.getString("customer_email"));
                    vector1.add(repairOrders.getString("employee_email"));
                    vector1.add(repairOrders.getString("status"));
                    vector1.add(repairOrders.getString("description"));
                    vector1.add(repairOrders.getString("amount"));
                }
                model.addRow(vector1);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dashboardPanel, "Could not fetch repair order details", "Error", 0);
            System.out.println(ex.getMessage());
        }
    }

    public static void main(String[] args) {
        RepairOrderView repairOrderView = new RepairOrderView();
        repairOrderView.setContentPane(repairOrderView.dashboardPanel);
        repairOrderView.setTitle("Order View");
        repairOrderView.setSize(1200, 600);
        repairOrderView.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        repairOrderView.setVisible(true);
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
        statusBtn = new JButton(payIcon);
    }
}
