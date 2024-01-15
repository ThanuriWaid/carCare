package Views;

import Controllers.SparePartsController;

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

public class SpareParts extends JFrame {
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
    private JTextField price;
    private JButton addButton;
    private JButton deleteButton;
    private JButton EditBtn;
    private JTable orders;

    SparePartsController sparePartsController;

    public SpareParts() {
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String itemName = productCode.getText();
                String itemBrand = productName.getText();
                Double itemPrice = Double.parseDouble(price.getText());
                sparePartsController = new SparePartsController();
                Models.SparePart sparePart = sparePartsController.addItem(itemName,itemBrand, itemPrice);
                if(sparePartsController.addSparePartsToDB()){
                    JOptionPane.showMessageDialog(dashboardPanel, "Spare part Inserted", "Success", 1);
                    updateTable();
                }
                else{
                    JOptionPane.showMessageDialog(dashboardPanel, "Could not add spare part", "Error", 0);
                }
                productCode.setText("");
                productName.setText("");
                price.setText("");
            }
        });
        orders.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                DefaultTableModel model = (DefaultTableModel)orders.getModel();
                int selectedIndex = orders.getSelectedRow();

                productCode.setText(model.getValueAt(selectedIndex, 1).toString());
                productName.setText(model.getValueAt(selectedIndex, 2).toString());
                price.setText(model.getValueAt(selectedIndex, 3).toString());
                super.mouseClicked(e);
            }
        });
        EditBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel)orders.getModel();
                int selectedIndex = orders.getSelectedRow();

                int itemId = Integer.parseInt(model.getValueAt(selectedIndex, 0).toString());
                String itemName = productCode.getText();
                String itemBrand = productName.getText();
                Double itemPrice = Double.parseDouble(price.getText());
                sparePartsController = new SparePartsController();
                try {
                    if(sparePartsController.updateSparePart(itemId, itemName, itemBrand, itemPrice)){
                        JOptionPane.showMessageDialog(dashboardPanel, "Spare part updated", "Success", 1);
                        updateTable();
                        productCode.setText("");
                        productName.setText("");
                        price.setText("");
                    }
                    else{
                        JOptionPane.showMessageDialog(dashboardPanel, "Could not update spare parts", "Error", 0);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dashboardPanel, "Could not update spare parts", "Error", 0);
                }
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel)orders.getModel();
                int selectedIndex = orders.getSelectedRow();

                int itemId = Integer.parseInt(model.getValueAt(selectedIndex, 0).toString());
                sparePartsController = new SparePartsController();
                try {
                    if(sparePartsController.deleteSparePart(itemId)){
                        JOptionPane.showMessageDialog(dashboardPanel, "Spare part deleted", "Success", 1);
                        updateTable();
                    }
                    else{
                        JOptionPane.showMessageDialog(dashboardPanel, "Could not delete spare part", "Error", 0);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dashboardPanel, "Could not delete spare part", "Error", 0);
                }
            }
        });
    }

    public void updateTable() {
        sparePartsController = new SparePartsController();
        try {
            ResultSet items = sparePartsController.findSpareParts();
            ResultSetMetaData resultSetMetaData = items.getMetaData();
            int c = resultSetMetaData.getColumnCount();
            System.out.println(c);

            DefaultTableModel model = (DefaultTableModel)orders.getModel();
            model.setRowCount(0);
            model.setColumnCount(5);
            Vector vector = new Vector();

            for (int i=1; i<=c; i++) {
                vector.add(items.getString("id"));
                vector.add(items.getString("name"));
                vector.add(items.getString("brand"));
                vector.add(items.getString("price"));
                vector.add(items.getString("qty"));
            }
            model.addRow(vector);
            while (items.next()) {
                Vector vector1 = new Vector();

                for (int i=1; i<=c; i++) {
                    vector1.add(items.getString("id"));
                    vector1.add(items.getString("name"));
                    vector1.add(items.getString("brand"));
                    vector1.add(items.getString("price"));
                    vector1.add(items.getString("qty"));
                }
                model.addRow(vector1);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dashboardPanel, "Could not fetch spare parts details", "Error", 0);
            System.out.println(ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SpareParts spareParts = new SpareParts();
        spareParts.setContentPane(spareParts.dashboardPanel);
        spareParts.setTitle("Manage Inventory");
        spareParts.setSize(800, 500);
        spareParts.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        spareParts.setVisible(true);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        spareParts.setLocation(dim.width/2-spareParts.getSize().width/2, dim.height/2-spareParts.getSize().height/2);
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
