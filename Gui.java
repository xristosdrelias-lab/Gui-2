package Gui;

import UseCase1.*;
import UseCase2.*;
import UseCase3.*;
import UseCase4.*;
import UseCase5.*;
import UseCase6.*;
import UseCase7.*;
import UseCase8.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class Gui extends JFrame {
    private DBManager dbManager;
    private DefaultTableModel productTableModel;
    private DefaultTableModel producerOrderTableModel;
    private DefaultTableModel availableDeliveriesModel;
    private DefaultTableModel activeRouteModel;
    private DefaultTableModel settlementTableModel;
    private DefaultTableModel refundTableModel;
    private DeliveryMapPanel mapPanel;
    private JTable availableTable;
    private JTable routeTable;
    private java.util.List<Product> cartProductList = new java.util.ArrayList<>();
    private java.util.List<Integer> cartQuantities = new java.util.ArrayList<>();
    private double cartTotal = 0.0;
    private java.util.List<GroupPurchase> sharedGroupPurchases = new java.util.ArrayList<>();
    private final Color BRAND_COLOR = new Color(0, 157, 224);
    private final Color SUCCESS_GREEN = new Color(34, 197, 94);
    private final Color DANGER_RED = new Color(239, 68, 68);
    private final Color WARNING_ORANGE = new Color(245, 158, 11);
    private final Color DARK_TEXT = new Color(15, 23, 42);
    private final Color LIGHT_BG = new Color(248, 250, 252);
    private final Color BORDER_COLOR = new Color(226, 232, 240);

    public Gui() {
        this.dbManager = new DBManager();
        setTitle("FarmSync");
        setSize(1300, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(LIGHT_BG);
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.BLACK);
        headerPanel.setPreferredSize(new Dimension(1000, 75));
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 30, 15));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        JLabel titleLabel = new JLabel("FarmSync");
        titleLabel.setForeground(BRAND_COLOR);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        JTabbedPane mainTabs = new JTabbedPane();
        mainTabs.setFont(new Font("Segoe UI", Font.BOLD, 16));
        mainTabs.setBackground(Color.WHITE);
        mainTabs.setForeground(DARK_TEXT);
        mainTabs.setFocusable(false);

        mainTabs.addTab("Παραγγελία (Πελάτης)  ", createCustomerPanel());
        mainTabs.addTab("Κατάστημα (Παραγωγός)  ", createProducerPanel());
        mainTabs.addTab("Διανομέας  ", createCourierPanel());
        mainTabs.addTab("Διαχειριστής  ", createAdminPanel());
        JPanel tabContainer = new JPanel(new BorderLayout());
        tabContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        tabContainer.setBackground(LIGHT_BG);
        tabContainer.add(mainTabs, BorderLayout.CENTER);
        add(tabContainer, BorderLayout.CENTER);
        initSampleData();
    }

    private void initSampleData() {
        RefundRequest sampleRequest = new RefundRequest(1001, 5005, 45.0);
        dbManager.saveRefundRequest(sampleRequest);
        if (dbManager.isFirstTimeSetup()) {
            dbManager.saveProduct(new Product("P1", "Φρέσκιες Πατάτες Νάξου", 500, 0.80, "Άνω των 50kg: 0.60€", true, 4.8, "pickup", 5.0));
            dbManager.saveProduct(new Product("P2", "Εξαιρετικό Παρθένο Ελαιόλαδο (5L)", 100, 25.00, "10+ τεμάχια: 22€", true, 4.9, "pickup", 5.0));
            dbManager.saveProduct(new Product("P3", "Ντομάτες Κρήτης (Κιλό)", 200, 1.50, "-", true, 4.5, "pickup", 5.0));
            dbManager.saveProduct(new Product("P4", "Αυγά Ελευθέρας Βοσκής (Καρτέλα 30)", 50, 6.50, "5+ καρτέλες: 5.50€", true, 4.7, "pickup", 5.0));
            dbManager.saveProduct(new Product("P5", "Μέλι Θυμαρίσιο (1kg)", 30, 12.00, "-", true, 5.0, "pickup", 5.0));
        }
        refreshAllTables();
    }

    private void refreshAllTables() {
        refreshProductTable();
        refreshProducerOrderTable();
        refreshCourierTables();
        refreshSettlementTable();
        refreshRefundTable();
    }

    //ΠΕΛΑΤΗΣ
    private JPanel createCustomerPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(25, 0));
        mainPanel.setBackground(LIGHT_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel catalogPanel = new JPanel(new BorderLayout(15, 15));
        catalogPanel.setBackground(LIGHT_BG);
        String[] catalogColumns = {"Προϊόν", "Απόθεμα", "Τιμή"};
        DefaultTableModel catalogModel = new DefaultTableModel(catalogColumns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable catalogTable = new JTable(catalogModel);
        styleTable(catalogTable);
        catalogTable.setRowHeight(50);
        catalogTable.setFont(new Font("Segoe UI", Font.BOLD, 16));

        for (Product product : dbManager.readProducts()) {
            catalogModel.addRow(new Object[]{product.getProductName(), product.getQuantity() + " τεμ", String.format(java.util.Locale.US, "%.2f €", product.getPrice())});
        }
        catalogPanel.add(createTitledPanel("Μενού Καταστήματος", new JScrollPane(catalogTable)), BorderLayout.CENTER);
        JPanel addToCartPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        addToCartPanel.setBackground(Color.WHITE);
        addToCartPanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
        addToCartPanel.add(new JLabel("Ποσότητα:"));
        JTextField quantityField = new JTextField("1", 4);
        quantityField.setFont(new Font("Segoe UI", Font.BOLD, 18));
        quantityField.setHorizontalAlignment(JTextField.CENTER);
        addToCartPanel.add(quantityField);
        JButton addToCartBtn = createRoundedButton("Προσθήκη στην Παραγγελία", BRAND_COLOR);
        JButton refreshCatalogBtn = createRoundedButton("Ανανέωση", Color.GRAY);
        addToCartPanel.add(addToCartBtn);
        addToCartPanel.add(refreshCatalogBtn);
        catalogPanel.add(addToCartPanel, BorderLayout.SOUTH);

        refreshCatalogBtn.addActionListener(e -> {
            catalogModel.setRowCount(0);
            for (Product product : dbManager.readProducts()) {
                catalogModel.addRow(new Object[]{product.getProductName(), product.getQuantity() + " τεμ", String.format(java.util.Locale.US, "%.2f €", product.getPrice())});
            }
        });

        mainPanel.add(catalogPanel, BorderLayout.CENTER);
        JPanel sidebarPanel = new JPanel(new BorderLayout(10, 10));
        sidebarPanel.setPreferredSize(new Dimension(380, 0));
        sidebarPanel.setBackground(Color.WHITE);
        sidebarPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel sidebarTitle = new JLabel("Η Παραγγελία μου");
        sidebarTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        sidebarTitle.setForeground(DARK_TEXT);
        sidebarPanel.add(sidebarTitle, BorderLayout.NORTH);

        DefaultListModel<String> cartListModel = new DefaultListModel<>();
        JList<String> cartList = new JList<>(cartListModel);
        cartList.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        cartList.setFixedCellHeight(40);
        cartList.setSelectionBackground(Color.WHITE);
        cartList.setSelectionForeground(DARK_TEXT);

        JScrollPane cartScroll = new JScrollPane(cartList);
        cartScroll.setBorder(BorderFactory.createEmptyBorder());
        sidebarPanel.add(cartScroll, BorderLayout.CENTER);

        JPanel checkoutPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        checkoutPanel.setBackground(Color.WHITE);

        JLabel totalLabel = new JLabel("Σύνολο: 0.00€", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        totalLabel.setForeground(BRAND_COLOR);

        JButton checkoutBtn = createRoundedButton("Ολοκλήρωση", SUCCESS_GREEN);
        JButton makeGroupBtn = createRoundedButton("Δήλωση ως Ομαδική", BRAND_COLOR);
        JButton clearCartBtn = createRoundedButton("Άδειασμα", DANGER_RED);

        checkoutPanel.add(totalLabel);
        checkoutPanel.add(checkoutBtn);
        checkoutPanel.add(makeGroupBtn);
        checkoutPanel.add(clearCartBtn);
        sidebarPanel.add(checkoutPanel, BorderLayout.SOUTH);

        mainPanel.add(sidebarPanel, BorderLayout.EAST);

        addToCartBtn.addActionListener(e -> {
            int selectedRow = catalogTable.getSelectedRow();
            if (selectedRow == -1) { JOptionPane.showMessageDialog(this, "Επιλέξτε ένα προϊόν από το μενού!"); return; }
            String productName = (String) catalogModel.getValueAt(selectedRow, 0);

            Product selectedProduct = null;
            for (Product product : dbManager.readProducts()) {
                if (product.getProductName().equals(productName)) { selectedProduct = product; break; }
            }

            if (selectedProduct != null) {
                try {
                    int requestedQuantity = Integer.parseInt(quantityField.getText());
                    if (requestedQuantity <= 0 || requestedQuantity > selectedProduct.getQuantity()) {
                        JOptionPane.showMessageDialog(this, "Μη διαθέσιμη ποσότητα!"); return;
                    }
                    cartProductList.add(selectedProduct);
                    cartQuantities.add(requestedQuantity);
                    double itemTotal = requestedQuantity * selectedProduct.getPrice();
                    cartTotal += itemTotal;

                    cartListModel.addElement(requestedQuantity + "x  " + selectedProduct.getProductName() + "   |   " + String.format(java.util.Locale.US, "%.2f €", itemTotal));
                    totalLabel.setText("Σύνολο: " + String.format(java.util.Locale.US, "%.2f €", cartTotal));
                } catch (NumberFormatException exception) { JOptionPane.showMessageDialog(this, "Εισάγετε έγκυρη ποσότητα."); }
            }
        });

        checkoutBtn.addActionListener(e -> {
            if (cartProductList.isEmpty()) { JOptionPane.showMessageDialog(this, "Το καλάθι σας είναι άδειο!"); return; }

            JTextField nameField = new JTextField();
            JTextField addressField = new JTextField();
            JTextField phoneField = new JTextField();

            Object[] message = { "Ονοματεπώνυμο:", nameField, "Διεύθυνση (Οδός, Αριθμός, Πόλη):", addressField, "Τηλέφωνο:", phoneField };
            int option = JOptionPane.showConfirmDialog(this, message, "Στοιχεία Αποστολής", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                String cName = nameField.getText().trim(), cAddress = addressField.getText().trim(), cPhone = phoneField.getText().trim();
                if (cName.isEmpty() || cAddress.isEmpty() || cPhone.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Παρακαλώ συμπληρώστε όλα τα στοιχεία!", "Σφάλμα", JOptionPane.ERROR_MESSAGE); return;
                }

                for (int i = 0; i < cartProductList.size(); i++) {
                    Product p = cartProductList.get(i);
                    p.setQuantity(p.getQuantity() - cartQuantities.get(i));
                }
                dbManager.updateProductsFile();

                String finalDescription = "Καλάθι: " + cartProductList.size() + " προϊόντα [Πελάτης: " + cName + ", Διεύθυνση: " + cAddress + ", Τηλ: " + cPhone + "]";
                Order newOrder = new Order(8000 + dbManager.queryOrders().size(), 1, finalDescription, cartTotal);
                newOrder.setStatus("Εκκρεμής");
                dbManager.addOrder(newOrder);

                cartProductList.clear(); cartQuantities.clear(); cartListModel.clear(); cartTotal = 0.0;
                totalLabel.setText("Σύνολο: 0.00€");
                refreshAllTables();
                JOptionPane.showMessageDialog(this, "Η παραγγελία σας στάλθηκε στο κατάστημα!\nΕυχαριστούμε, " + cName + "!", "Επιτυχία", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        makeGroupBtn.addActionListener(e -> {
            if (cartProductList.isEmpty()) { JOptionPane.showMessageDialog(this, "Το καλάθι είναι άδειο!"); return; }
            for (int i = 0; i < cartProductList.size(); i++) {
                Product p = cartProductList.get(i);
                p.setQuantity(p.getQuantity() - cartQuantities.get(i));
            }
            dbManager.updateProductsFile();

            StringBuilder descBuilder = new StringBuilder();
            for (int i = 0; i < cartProductList.size(); i++) descBuilder.append(cartQuantities.get(i)).append("x ").append(cartProductList.get(i).getProductName()).append(" ");

            GroupPurchase newGroup = new GroupPurchase("GP-" + (9500 + sharedGroupPurchases.size()), descBuilder.toString().trim() + " (Αξίας: " + String.format(java.util.Locale.US, "%.2f", cartTotal) + "€)", 2.0, 1.0, "Πάτρα");
            sharedGroupPurchases.add(newGroup);

            cartProductList.clear(); cartQuantities.clear(); cartListModel.clear(); cartTotal = 0.0;
            totalLabel.setText("Σύνολο: 0.00€");
            refreshAllTables();
            JOptionPane.showMessageDialog(this, "Η παραγγελία δηλώθηκε ως Ομαδική!\nΠεριμένουμε έναν ακόμα συν-αγοραστή.", "Επιτυχία", JOptionPane.INFORMATION_MESSAGE);
        });

        clearCartBtn.addActionListener(e -> {
            if (cartProductList.isEmpty()) return;
            int choice = JOptionPane.showConfirmDialog(this, "Άδειασμα καλαθιού;", "Επιβεβαίωση", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                cartProductList.clear(); cartQuantities.clear(); cartListModel.clear(); cartTotal = 0.0;
                totalLabel.setText("Σύνολο: 0.00€");
            }
        });
        return mainPanel;
    }

    //ΠΑΡΑΓΩΓΟΣ
    private JPanel createProducerPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 20, 20));
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        String[] productColumns = {"ID", "Όνομα", "Απόθεμα", "Τιμή", "Κανόνες"};
        productTableModel = new DefaultTableModel(productColumns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable productTable = new JTable(productTableModel);
        styleTable(productTable);

        JPanel productActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        productActionPanel.setBackground(Color.WHITE);
        JButton addBtn = createRoundedButton("Νέο Προϊόν", BRAND_COLOR);
        JButton editBtn = createRoundedButton("Επεξεργασία", WARNING_ORANGE);
        JButton deleteBtn = createRoundedButton("Διαγραφή", DANGER_RED);
        productActionPanel.add(addBtn); productActionPanel.add(editBtn); productActionPanel.add(deleteBtn);

        JPanel prodContainer = new JPanel(new BorderLayout());
        prodContainer.setBackground(Color.WHITE);
        prodContainer.add(new JScrollPane(productTable), BorderLayout.CENTER);
        prodContainer.add(productActionPanel, BorderLayout.SOUTH);
        panel.add(createTitledPanel("Διαθέσιμα Προϊόντα", prodContainer));
        String[] orderColumns = {"ID Παραγγελίας", "Πελάτης / Προϊόντα", "Κατάσταση"};
        producerOrderTableModel = new DefaultTableModel(orderColumns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable orderTable = new JTable(producerOrderTableModel);
        styleTable(orderTable);

        JPanel orderActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        orderActionPanel.setBackground(Color.WHITE);
        JButton acceptOrderBtn = createRoundedButton("Ετοιμασία Παραγγελίας", SUCCESS_GREEN);
        JButton cancelOrderBtn = createRoundedButton("Ακύρωση", DANGER_RED);
        orderActionPanel.add(acceptOrderBtn); orderActionPanel.add(cancelOrderBtn);
        JPanel ordContainer = new JPanel(new BorderLayout());
        ordContainer.setBackground(Color.WHITE);
        ordContainer.add(new JScrollPane(orderTable), BorderLayout.CENTER);
        ordContainer.add(orderActionPanel, BorderLayout.SOUTH);

        panel.add(createTitledPanel("Εκκρεμείς Παραγγελίες προς Ετοιμασία", ordContainer));
        addBtn.addActionListener(e -> showProductDialog(null));

        editBtn.addActionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Επιλέξτε ένα προϊόν από τον πίνακα για επεξεργασία!", "Προσοχή", JOptionPane.WARNING_MESSAGE); return; }
            String productId = (String) productTableModel.getValueAt(row, 0);
            Product selectedProduct = null;
            for (Product p : dbManager.readProducts()) {
                if (p.getProductId().equals(productId)) { selectedProduct = p; break; }
            }
            if (selectedProduct != null) showProductDialog(selectedProduct);
        });

        deleteBtn.addActionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Επιλέξτε ένα προϊόν από τον πίνακα για διαγραφή!", "Προσοχή", JOptionPane.WARNING_MESSAGE); return; }
            int confirm = JOptionPane.showConfirmDialog(this, "Είστε σίγουροι ότι θέλετε να διαγράψετε αυτό το προϊόν;", "Επιβεβαίωση Διαγραφής", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                String productId = (String) productTableModel.getValueAt(row, 0);
                dbManager.readProducts().removeIf(product -> product.getProductId().equals(productId));
                dbManager.updateProductsFile();
                refreshAllTables();
            }
        });

        acceptOrderBtn.addActionListener(e -> {
            int row = orderTable.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Επιλέξτε παραγγελία!"); return; }
            int orderId = (int) producerOrderTableModel.getValueAt(row, 0);
            for (Order order : dbManager.queryOrders()) {
                if (order.getOrderId() == orderId) { order.setStatus("Έτοιμη για Παραλαβή"); dbManager.updateOrdersFile(); break; }
            }
            refreshAllTables(); JOptionPane.showMessageDialog(this, "Η παραγγελία ετοιμάστηκε! Καλέστηκε Διανομέας.");
        });

        cancelOrderBtn.addActionListener(e -> {
            int row = orderTable.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Επιλέξτε παραγγελία!"); return; }
            int orderId = (int) producerOrderTableModel.getValueAt(row, 0);
            for (Order order : dbManager.queryOrders()) {
                if (order.getOrderId() == orderId) { order.setStatus("Ακυρώθηκε"); dbManager.updateOrdersFile(); break; }
            }
            refreshAllTables();
        });
        return panel;
    }

    private void showProductDialog(Product existingProduct) {
        JDialog dialog = new JDialog(this, existingProduct == null ? "Προσθήκη Νέου Προϊόντος" : "Επεξεργασία Προϊόντος", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);
        JPanel form = new JPanel(new GridLayout(4, 2, 15, 25));
        form.setBorder(BorderFactory.createEmptyBorder(30, 25, 30, 25));
        form.setBackground(Color.WHITE);
        JTextField nameField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField rulesField = new JTextField();
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 15);
        nameField.setFont(fieldFont); quantityField.setFont(fieldFont); priceField.setFont(fieldFont); rulesField.setFont(fieldFont);
        if (existingProduct != null) {
            nameField.setText(existingProduct.getProductName());
            quantityField.setText(String.valueOf(existingProduct.getQuantity()));
            priceField.setText(String.format(java.util.Locale.US, "%.2f", existingProduct.getPrice()));
            rulesField.setText(existingProduct.getPricingRules());
        }
        JLabel nameLbl = new JLabel("Όνομα Προϊόντος:"); nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel qtyLbl = new JLabel("Ποσότητα:"); qtyLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel priceLbl = new JLabel("Τιμή (€):"); priceLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel rulesLbl = new JLabel("Κανόνες (π.χ. 1-10: 5€):"); rulesLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        form.add(nameLbl); form.add(nameField);
        form.add(qtyLbl); form.add(quantityField);
        form.add(priceLbl); form.add(priceField);
        form.add(rulesLbl); form.add(rulesField);
        dialog.add(form, BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttons.setBackground(LIGHT_BG);
        buttons.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));
        JButton saveBtn = createRoundedButton("Αποθήκευση", SUCCESS_GREEN);
        JButton cancelBtn = createRoundedButton("Ακύρωση", Color.GRAY);
        buttons.add(cancelBtn);
        buttons.add(saveBtn);
        dialog.add(buttons, BorderLayout.SOUTH);
        cancelBtn.addActionListener(e -> dialog.dispose());

        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                int qty = Integer.parseInt(quantityField.getText().trim());
                double price = Double.parseDouble(priceField.getText().replace("€", "").replace(",", ".").trim());
                String rules = rulesField.getText().trim();
                if (name.isEmpty()) { JOptionPane.showMessageDialog(dialog, "Το όνομα δεν μπορεί να είναι κενό!");
                    return;
                }
                if (existingProduct == null) {
                    Product newP = new Product("P" + (dbManager.readProducts().size() + 1), name, qty, price, rules, true, 4.5, "pickup", 5.0);
                    dbManager.saveProduct(newP);
                } else {
                    for (int i = 0; i < dbManager.readProducts().size(); i++) {
                        if (dbManager.readProducts().get(i).getProductId().equals(existingProduct.getProductId())) {
                            dbManager.readProducts().set(i, new Product(existingProduct.getProductId(), name, qty, price, rules, true, existingProduct.getProducerRating(), "pickup", 5.0));
                            dbManager.updateProductsFile();
                            break;
                        }
                    }
                }
                refreshAllTables();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, existingProduct == null ? "Το προϊόν προστέθηκε με επιτυχία!" : "Το προϊόν ενημερώθηκε με επιτυχία!", "Επιτυχία", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Παρακαλώ ελέγξτε ότι η ποσότητα και η τιμή είναι έγκυροι αριθμοί!", "Σφάλμα", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.setVisible(true);
    }

    //ΔΙΑΝΟΜΕΑΣ
    class DeliveryMapPanel extends JPanel {
        private String currentStatus = "";
        public void updateMap(String status) { this.currentStatus = status; repaint(); }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(new Color(241, 245, 249)); g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setColor(new Color(186, 230, 253));
            g2d.fillPolygon(new int[]{0, 300, 250, 150, 0}, new int[]{0, 0, getHeight()/2, getHeight(), getHeight()}, 5);
            g2d.setColor(new Color(187, 247, 208));
            g2d.fillOval(350, 40, 150, 90); g2d.fillRoundRect(180, getHeight() - 70, 120, 50, 20, 20);

            g2d.setColor(Color.WHITE); g2d.setStroke(new BasicStroke(5));
            for(int i = 250; i <= 500; i += 30) { g2d.drawLine(i, 20, i - 120, getHeight() - 20); }
            int startX = getWidth() - 150, startY = getHeight() - 60, endX = 280, endY = 80;

            g2d.setColor(BRAND_COLOR);
            g2d.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{12}, 0));
            g2d.drawLine(startX, startY, endX, endY);

            g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
            g2d.drawString("🧑‍🌾", startX - 15, startY + 10); g2d.drawString("🏠", endX - 15, endY + 10);

            int pinX = -100, pinY = -100;
            if ("Έτοιμη για Παραλαβή".equals(currentStatus)) { pinX = startX; pinY = startY; }
            else if ("Προς Παράδοση".equals(currentStatus)) { pinX = startX + (endX - startX) / 2; pinY = startY + (endY - startY) / 2; }
            else if ("Ολοκληρώθηκε".equals(currentStatus)) { pinX = endX; pinY = endY; }

            if (pinX != -100) {
                g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40)); g2d.drawString("📍", pinX - 20, pinY);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 13));
                FontMetrics fm = g2d.getFontMetrics(); int textW = fm.stringWidth(currentStatus);
                g2d.setColor(DARK_TEXT); g2d.fillRoundRect(pinX - textW/2 - 8, pinY - 45, textW + 16, 24, 12, 12);
                g2d.setColor(Color.WHITE); g2d.drawString(currentStatus, pinX - textW/2, pinY - 28);
            }
        }
    }

    private JPanel createCourierPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(LIGHT_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mapPanel = new DeliveryMapPanel();
        mapPanel.setPreferredSize(new Dimension(1000, 280));
        mapPanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 2));
        mainPanel.add(createTitledPanel("Χάρτης Διαδρομής", mapPanel), BorderLayout.NORTH);

        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        tablesPanel.setBackground(LIGHT_BG);

        JPanel availablePanel = new JPanel(new BorderLayout(15, 15));
        availablePanel.setBackground(LIGHT_BG);
        availableDeliveriesModel = new DefaultTableModel(new String[]{"ID", "Παραγγελία", "Κατάσταση"}, 0);
        availableTable = new JTable(availableDeliveriesModel);
        styleTable(availableTable);
        availablePanel.add(createTitledPanel("Σταθμός Φόρτωσης", new JScrollPane(availableTable)), BorderLayout.CENTER);
        JButton routeBtn = createRoundedButton("Ανάληψη Δρομολογίου", BRAND_COLOR);
        availablePanel.add(routeBtn, BorderLayout.SOUTH);
        JPanel activeRoutePanel = new JPanel(new BorderLayout(15, 15));
        activeRoutePanel.setBackground(LIGHT_BG);
        activeRouteModel = new DefaultTableModel(new String[]{"ID", "Παραγγελία", "Κατάσταση"}, 0);
        routeTable = new JTable(activeRouteModel);
        styleTable(routeTable);
        activeRoutePanel.add(createTitledPanel("Ενεργό Δρομολόγιο", new JScrollPane(routeTable)), BorderLayout.CENTER);
        JButton qrBtn = createRoundedButton("Παράδοση & Σκανάρισμα", SUCCESS_GREEN);
        activeRoutePanel.add(qrBtn, BorderLayout.SOUTH);

        tablesPanel.add(availablePanel);
        tablesPanel.add(activeRoutePanel);
        mainPanel.add(tablesPanel, BorderLayout.CENTER);

        availableTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && availableTable.getSelectedRow() != -1) {
                routeTable.clearSelection();
                mapPanel.updateMap("Έτοιμη για Παραλαβή");
            }
        });
        routeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && routeTable.getSelectedRow() != -1) {
                availableTable.clearSelection();
                mapPanel.updateMap("Προς Παράδοση");
            }
        });

        routeBtn.addActionListener(e -> {
            int selectedRow = availableTable.getSelectedRow();
            if (selectedRow == -1) return;
            int orderId = (int) availableDeliveriesModel.getValueAt(selectedRow, 0);
            for (Order order : dbManager.queryOrders()) {
                if (order.getOrderId() == orderId) {
                    order.setStatus("Προς Παράδοση");
                    dbManager.updateOrdersFile();
                    break;
                }
            }
            refreshAllTables();
        });

        qrBtn.addActionListener(actionEvent -> {
            int selectedRow = routeTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Επιλέξτε μια παραγγελία από το Όχημα!", "Προσοχή", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int orderId = (int) activeRouteModel.getValueAt(selectedRow, 0);
            JDialog qrDialog = new JDialog(this, "Σάρωση QR Code Πελάτη", true);
            qrDialog.setSize(350, 420);
            qrDialog.setLocationRelativeTo(this);
            java.awt.image.BufferedImage qrImage = generateMockQRCode("ORDER-" + orderId);
            JLabel qrLabel = new JLabel(new ImageIcon(qrImage), SwingConstants.CENTER);
            JButton confirmScanBtn = createStyledButton("Επιβεβαίωση Σάρωσης", new Color(76, 175, 80));
            confirmScanBtn.addActionListener(e -> {
                qrDialog.dispose();
                for (Order order : dbManager.queryOrders()) {
                    if (order.getOrderId() == orderId) {
                        order.setStatus("Ολοκληρώθηκε");
                        dbManager.updateOrdersFile();
                        break;
                    }
                }
                refreshAllTables();
                JOptionPane.showMessageDialog(this, "Η παραγγελία παραδόθηκε και στάλθηκε στον Διαχειριστή.");
            });

            qrDialog.add(qrLabel, BorderLayout.CENTER);
            qrDialog.add(confirmScanBtn, BorderLayout.SOUTH);
            qrDialog.setVisible(true);
        });
        return mainPanel;
    }

    //ΔΙΑΧΕΙΡΙΣΤΗΣ
    private JPanel createAdminPanel() {
        JPanel mainAdminPanel = new JPanel(new GridLayout(2, 1, 15, 15));
        mainAdminPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        String[] settlementColumns = {"ID Παραγγελίας", "Περιγραφή", "Συνολικό Ποσό", "Κατάσταση"};
        settlementTableModel = new DefaultTableModel(settlementColumns, 0);
        JTable settlementTable = new JTable(settlementTableModel);
        styleTable(settlementTable);
        JPanel settlementPanel = createTitledPanel("Εκκαθαρίσεις Πληρωμών", new JScrollPane(settlementTable));
        JButton executeSettlementBtn = createStyledButton("Εκτέλεση Εκκαθάρισης", new Color(255, 87, 34));
        settlementPanel.add(executeSettlementBtn, BorderLayout.SOUTH);
        mainAdminPanel.add(settlementPanel);
        executeSettlementBtn.addActionListener(actionEvent -> {
            int row = settlementTable.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Επιλέξτε παραγγελία!"); return; }
            int oId = (int) settlementTableModel.getValueAt(row, 0);
            for (Order order : dbManager.queryOrders()) {
                if (order.getOrderId() == oId) {
                    order.setStatus("Εκκαθαρίστηκε");
                    double total = order.getPrice();
                    refreshAllTables();
                    JOptionPane.showMessageDialog(this, "Εκκαθάριση Επιτυχής!\nΣυνολικό Ποσό: €" + total + "\nΠρομήθεια (10%): €" + (total*0.10) + "\nΚαθαρό Παραγωγού: €" + (total*0.90), "Αναφορά", JOptionPane.INFORMATION_MESSAGE);
                    break;
                }
            }
        });

        String[] refundColumns = {"ID Αιτήματος", "Παραγγελία", "Ποσό", "Κατάσταση"};
        refundTableModel = new DefaultTableModel(refundColumns, 0);
        JTable refundTable = new JTable(refundTableModel);
        styleTable(refundTable);
        JPanel refundPanel = createTitledPanel("Διαχείριση Καταγγελιών", new JScrollPane(refundTable));
        JPanel refundActionsPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton approveRefundBtn = createStyledButton("Έγκριση", new Color(76, 175, 80));
        JButton rejectRefundBtn = createStyledButton("Απόρριψη", new Color(244, 67, 54));
        refundActionsPanel.add(approveRefundBtn); refundActionsPanel.add(rejectRefundBtn);
        refundPanel.add(refundActionsPanel, BorderLayout.SOUTH);
        mainAdminPanel.add(refundPanel);
        approveRefundBtn.addActionListener(actionEvent -> {
            int r = refundTable.getSelectedRow();
            if (r != -1) { dbManager.updateRefundStatus((int)refundTableModel.getValueAt(r,0), RefundStatus.APPROVED); refreshAllTables(); }
        });

        rejectRefundBtn.addActionListener(actionEvent -> {
            int r = refundTable.getSelectedRow();
            if (r != -1) { dbManager.updateRefundStatus((int)refundTableModel.getValueAt(r,0), RefundStatus.REJECTED); refreshAllTables(); }
        });

        return mainAdminPanel;
    }

    // --- DATA REFRESH LOGIC ---
    private void refreshProductTable() {
        if (productTableModel == null) return;
        productTableModel.setRowCount(0);
        for (Product product : dbManager.readProducts()) {
            productTableModel.addRow(new Object[]{product.getProductId(), product.getProductName(), product.getQuantity(), String.format(java.util.Locale.US, "%.2f €", product.getPrice()), product.getPricingRules()});
        }
    }

    private void refreshProducerOrderTable() {
        if (producerOrderTableModel == null) return;
        producerOrderTableModel.setRowCount(0);
        for (Order order : dbManager.queryOrders()) {
            if (order.getStatus() != null && (order.getStatus().equals("Εκκρεμής") || order.getStatus().equals("Εκκρεμεί"))) {
                producerOrderTableModel.addRow(new Object[]{order.getOrderId(), order.getProductName(), order.getStatus()});
            }
        }
    }

    private void refreshCourierTables() {
        if (availableDeliveriesModel == null || activeRouteModel == null) return;
        availableDeliveriesModel.setRowCount(0); activeRouteModel.setRowCount(0);
        for (Order order : dbManager.queryOrders()) {
            if (order.getStatus() != null) {
                if (order.getStatus().equals("Έτοιμη για Παραλαβή")) availableDeliveriesModel.addRow(new Object[]{order.getOrderId(), order.getProductName(), order.getStatus()});
                else if (order.getStatus().equals("Προς Παράδοση")) activeRouteModel.addRow(new Object[]{order.getOrderId(), order.getProductName(), "Σε Διαδρομή"});
            }
        }
        SwingUtilities.invokeLater(() -> {
            if (mapPanel != null && availableTable != null && routeTable != null) {
                if (activeRouteModel.getRowCount() > 0) { routeTable.setRowSelectionInterval(0, 0); mapPanel.updateMap("Προς Παράδοση"); }
                else if (availableDeliveriesModel.getRowCount() > 0) { availableTable.setRowSelectionInterval(0, 0); mapPanel.updateMap("Έτοιμη για Παραλαβή"); }
                else mapPanel.updateMap("");
            }
        });
    }

    private void refreshSettlementTable() {
        if (settlementTableModel == null) return;
        settlementTableModel.setRowCount(0);
        for (Order order : dbManager.queryOrders()) {
            if (order.getStatus() != null && (order.getStatus().equals("Ολοκληρώθηκε") || order.getStatus().equals("Εκκαθαρίστηκε"))) {
                settlementTableModel.addRow(new Object[]{order.getOrderId(), order.getProductName(), String.format(java.util.Locale.US, "%.2f €", order.getPrice()), order.getStatus()});
            }
        }
    }

    private void refreshRefundTable() {
        if (refundTableModel == null) return;
        refundTableModel.setRowCount(0);
        for (RefundRequest r : dbManager.getAllRefundRequests()) refundTableModel.addRow(new Object[]{r.getRefundId(), r.getOrderId(), r.getRequestedAmount() + "€", r.getStatus()});
    }

    // --- UI/UX CUSTOM STYLING ---
    private JPanel createTitledPanel(String title, Component content) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(DARK_TEXT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setShowVerticalLines(false);
        table.setGridColor(BORDER_COLOR);

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setForeground(DARK_TEXT);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));

        table.setSelectionBackground(new Color(241, 245, 249));
        table.setSelectionForeground(BRAND_COLOR);

        JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, table);
        if (scrollPane != null) {
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getViewport().setBackground(Color.WHITE);
        }
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text); button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(backgroundColor); button.setForeground(Color.WHITE); button.setFocusPainted(false); button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createRoundedButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bgColor.darker() : bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        return button;
    }

    private java.awt.image.BufferedImage generateMockQRCode(String data) {
        int size = 180;
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE); g.fillRect(0, 0, size, size); g.setColor(Color.BLACK);
        java.util.Random rand = new java.util.Random(data.hashCode());
        int cellSize = 6;
        for (int i = 10; i < size - 10; i += cellSize) {
            for (int j = 10; j < size - 10; j += cellSize) {
                if (rand.nextBoolean()) g.fillRect(i, j, cellSize, cellSize);
            }
        }
        drawQRMarker(g, 10, 10); drawQRMarker(g, size - 46, 10); drawQRMarker(g, 10, size - 46);
        g.dispose(); return image;
    }

    private void drawQRMarker(Graphics2D g, int x, int y) {
        g.setColor(Color.WHITE); g.fillRect(x, y, 36, 36);
        g.setColor(Color.BLACK); g.fillRect(x, y, 36, 36);
        g.setColor(Color.WHITE); g.fillRect(x + 6, y + 6, 24, 24);
        g.setColor(Color.BLACK); g.fillRect(x + 12, y + 12, 12, 12);
    }

    public static void main(String[] args) {
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0,0,0,0));
        UIManager.put("TabbedPane.tabsOverlapBorder", true);
        SwingUtilities.invokeLater(() -> new Gui().setVisible(true));
    }
}