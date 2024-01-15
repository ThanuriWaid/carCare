package Views;

import Controllers.OrderController;
import Models.Order;
import Models.SalesProduct;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;

public class OrderView extends JFrame {
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
    private JTextField qty;
    private JButton addButton;
    private JButton deleteButton;
    private JButton statusBtn;
    private JTable orders;
    private JTextField customerEmail;
    private JTextField subTotal;
    private JButton payInvoiceButton;

    OrderController orderController;
    public OrderView() {

        productCode.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    int productId = Integer.parseInt(productCode.getText());
                    orderController = new OrderController();
                    ResultSet resultSet = orderController.findProductsFromDatabaseById(productId);
                    try {
                        String name = resultSet.getString("name");
                        Double productPrice = resultSet.getDouble("price");

                        productName.setText(name);
                        price.setText(String.valueOf(productPrice));

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dashboardPanel, "Product not found", "Error", 0);
                    }

                }
                super.keyPressed(e);
            }
        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int productId = Integer.parseInt(productCode.getText());
                String name = productName.getText();
                Double productPrice = Double.parseDouble(price.getText());
                int customerQty = Integer.parseInt(qty.getText());
                Double amount = productPrice * customerQty;

                ResultSet resultSet = orderController.findProductsFromDatabaseById(productId);
                try {
                    boolean status = true;
                    int actualQty = resultSet.getInt(("qty"));

                    if (actualQty < customerQty ) {
                        JOptionPane.showMessageDialog(dashboardPanel, "Available qty : " + actualQty, "Error", 0);
                        JOptionPane.showMessageDialog(dashboardPanel, "Not available qty", "Error", 0);
                    } else {
                        DefaultTableModel model = (DefaultTableModel)orders.getModel();
                        model.setColumnCount(4);
                        for (int i =1; i<orders.getRowCount(); i++) {
                            if (orders.getValueAt(i, 0).equals(productId)) {
                                status = false;
                            }
                        }

                        if (status) {
                            model.addRow(new Object[]{productId, name, amount, customerQty});
                        } else {
                            JOptionPane.showMessageDialog(dashboardPanel, "This product is already added.", "Error", 0);
                        }

                        productCode.setText("");
                        productName.setText("");
                        price.setText("");
                        qty.setText("");

                        Double sum =0.0;
                        for (int i=0; i<orders.getRowCount(); i++) {
                            sum += Double.parseDouble(orders.getValueAt(i, 2).toString());
                        }

                        subTotal.setText(Double.toString(sum));
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dashboardPanel, "Product not found", "Error", 0);
                    System.out.println(ex.getMessage());
                }
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel)orders.getModel();
                model.removeRow(orders.getSelectedRow());
                Double sum =0.0;
                for (int i=1; i<orders.getRowCount(); i++) {
                    sum += Double.parseDouble(orders.getValueAt(i, 2).toString());
                }

                subTotal.setText(Double.toString(sum));
            }
        });
        payInvoiceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Double total = Double.parseDouble(subTotal.getText());
                String email = customerEmail.getText();
                Order order = orderController.addOrder(email, total);
                int lastIndex = 0;

                try {
                    ResultSet resultSet = orderController.addOrderToDatabase();

                    if (resultSet.next()) {
                        lastIndex = resultSet.getInt(1);
                    }

                    int rowCount = orders.getRowCount();

                    int productId = 0;
                    int orderId = 0;
                    int qty = 0;
                    Double amount = 0.0;

                    SalesProduct salesProduct = null;

                    for (int i =0; i<rowCount; i++) {
                        productId = (int)orders.getValueAt(i, 0);
                        qty = (int)orders.getValueAt(i, 3);
                        amount = (Double)orders.getValueAt(i, 2);
                        salesProduct = orderController.addSalesProduct(productId, lastIndex, amount, qty);
                        if(!orderController.addSalesProductToDatabase()) {
                            JOptionPane.showMessageDialog(dashboardPanel, "Could not add products", "Error", 0);
                        }

                        if (!orderController.reduceQty(productId, qty)) {
                            JOptionPane.showMessageDialog(dashboardPanel, "Could not reduce qty", "Error", 0);
                        }
                    }

                    JOptionPane.showMessageDialog(dashboardPanel, "Successfully Added a order to Database", "Sucess", 1);
                    customerEmail.setText("Email");
                    subTotal.setText("0");
                    DefaultTableModel model = (DefaultTableModel) orders.getModel();
                    int count = model.getRowCount();
                    for (int i = rowCount; i >= 1 ; i--){
                        model.removeRow(i-1);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dashboardPanel, "Cannot insert a repair order to DB : " + ex.getMessage(), "Error", 1);
                }
            }
        });
    }

    public static void main(String[] args) {
        OrderView orderView = new OrderView();
        orderView.setContentPane(orderView.dashboardPanel);
        orderView.setTitle("Order View");
        orderView.setSize(1200, 600);
        orderView.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        orderView.setVisible(true);
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
