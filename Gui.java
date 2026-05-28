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
import java.io.File;
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
    
    private final Color BRAND_PRIMARY = new Color(79, 70, 229);   // Indigo / Royal Blue
    private final Color SUCCESS_GREEN = new Color(16, 185, 129);  // Emerald Green
    private final Color DANGER_RED = new Color(239, 68, 68);     // Rose Red
    private final Color WARNING_ORANGE = new Color(245, 158, 11); // Amber
    private final Color DARK_TEXT = new Color(30, 41, 59);        // Slate 800
    private final Color LIGHT_TEXT = new Color(148, 163, 184);    // Slate 400
    private final Color LIGHT_BG = new Color(248, 250, 252);      // Slate 50 (Φόντο εφαρμογής)
    private final Color CARD_BG = Color.WHITE;
    private final Color BORDER_COLOR = new Color(241, 245, 249);  // Slate 100
    private final Color SIDEBAR_BG = new Color(15, 23, 42);       // Slate 900 (Σκούρο Sidebar)

    private CardLayout cardLayout;
    private JPanel contentContainer;

    public Gui() {
        this.dbManager = new DBManager();
        setTitle("FarmSync - Smart Agriculture Management");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel mainLayout = new JPanel(new BorderLayout());
        mainLayout.setBackground(LIGHT_BG);

        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(280, 900));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        headerPanel.setBackground(SIDEBAR_BG);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        try {
            ImageIcon logoIcon = new ImageIcon("logo.jpg");
            Image scaledLogo = logoIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
            headerPanel.add(logoLabel);
        } catch (Exception e) {
            System.out.println("Το αρχείο logo.jpg δεν βρέθηκε.");
        }

        JLabel titleLabel = new JLabel("FarmSync");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        headerPanel.add(titleLabel);
        
        sidebar.add(headerPanel);
        sidebar.add(Box.createRigidArea(new Dimension(10, 20)));

        cardLayout = new CardLayout();
        contentContainer = new JPanel(cardLayout);
        contentContainer.setBackground(LIGHT_BG);
        contentContainer.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        contentContainer.add(createHomePanel(), "HOME");
        contentContainer.add(createCustomerPanel(), "PRODUCER");
        contentContainer.add(createProducerPanel(), "CUSTOMER");
        contentContainer.add(createCourierPanel(), "COURIER");
        contentContainer.add(createAdminPanel(), "ADMIN");
        contentContainer.add(createContactPanel(), "CONTACT");

        addSidebarButton(sidebar, "Αρχική", "HOME");
        addSidebarButton(sidebar, "Παραγωγός", "PRODUCER");
        addSidebarButton(sidebar, "Πελάτης", "CUSTOMER");
        addSidebarButton(sidebar, "Διανομές", "COURIER");
        addSidebarButton(sidebar, "Διαχείριση", "ADMIN");
        addSidebarButton(sidebar, "Επικοινωνία", "CONTACT");

        sidebar.add(Box.createVerticalGlue());

        mainLayout.add(sidebar, BorderLayout.WEST);
        mainLayout.add(contentContainer, BorderLayout.CENTER);
        add(mainLayout, BorderLayout.CENTER);

        cardLayout.show(contentContainer, "HOME");

        initSampleData();
    }

    private void addSidebarButton(JPanel sidebar, String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(LIGHT_TEXT);
        btn.setBackground(SIDEBAR_BG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(240, 45));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(Color.WHITE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setForeground(LIGHT_TEXT);
            }
        });

        btn.addActionListener(e -> cardLayout.show(contentContainer, cardName));
        sidebar.add(btn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
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

    private JPanel createHomePanel() {
        JPanel mainHomePanel = new JPanel(new GridBagLayout()) {
            private Image backgroundImage;
            {
                try {
                    backgroundImage = new ImageIcon("app.background.jpg").getImage();
                } catch (Exception e) {
                    System.out.println("Το αρχείο app.background.jpg δεν βρέθηκε.");
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    
                    int imgWidth = backgroundImage.getWidth(this);
                    int imgHeight = backgroundImage.getHeight(this);
                    double screenPos = (double) getWidth() / getHeight();
                    double imgPos = (double) imgWidth / imgHeight;
                    
                    int drawWidth, drawHeight;
                    if (screenPos > imgPos) {
                        drawWidth = getWidth();
                        drawHeight = (int) (getWidth() / imgPos);
                    } else {
                        drawHeight = getHeight();
                        drawWidth = (int) (getHeight() * imgPos);
                    }
                    int x = (getWidth() - drawWidth) / 2;
                    int y = (getHeight() - drawHeight) / 2;
                    
                    g2d.drawImage(backgroundImage, x, y, drawWidth, drawHeight, this);
                } else {
                    g.setColor(LIGHT_BG);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        JPanel textCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 230));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(226, 232, 240));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
            }
        };
        textCard.setOpaque(false);
        textCard.setLayout(new BoxLayout(textCard, BoxLayout.Y_AXIS));
        textCard.setBorder(BorderFactory.createEmptyBorder(35, 40, 35, 40));
        textCard.setPreferredSize(new Dimension(650, 320));

        JLabel welcomeTitle = new JLabel("Καλωσήρθατε στο FarmSync");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        welcomeTitle.setForeground(DARK_TEXT);
        welcomeTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea descriptionText = new JTextArea(
            "Το FarmSync αποτελεί μια ολοκληρωμένη και σύγχρονη πλατφόρμα " +
            "έξυπνης διαχείρισης και αυτοματοποίησης της αγροτικής εφοδιαστικής αλυσίδας.\n\n" +
            "Μέσω της εφαρμογής, οι παραγωγοί αποκτούν άμεση πρόσβαση στη διαχείριση του αποθέματός τους, " +
            "οι πελάτες μπορούν να πραγματοποιούν λιανικές ή ομαδικές αγορές προϊόντων, " +
            "ενώ οι διανομείς επωφελούνται από live εργαλεία παρακολούης και ψηφιακής επιβεβαίωσης παραδόσεων."
        );
        descriptionText.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        descriptionText.setForeground(new Color(71, 85, 105));
        descriptionText.setLineWrap(true);
        descriptionText.setWrapStyleWord(true);
        descriptionText.setEditable(false);
        descriptionText.setOpaque(false);
        descriptionText.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        descriptionText.setAlignmentX(Component.CENTER_ALIGNMENT);

        textCard.add(welcomeTitle);
        textCard.add(descriptionText);

        mainHomePanel.add(textCard);
        return mainHomePanel;
    }

    private JPanel createContactPanel() {
        JPanel contactMainPanel = new JPanel(new GridBagLayout());
        contactMainPanel.setBackground(LIGHT_BG);

        JPanel contactCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
            }
        };
        contactCard.setOpaque(false);
        contactCard.setLayout(new BorderLayout());
        contactCard.setBorder(BorderFactory.createEmptyBorder(30, 35, 35, 35));
        contactCard.setPreferredSize(new Dimension(750, 420));

        JLabel contactTitle = new JLabel("Ομάδα Ανάπτυξης & Επικοινωνία", SwingConstants.CENTER);
        contactTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        contactTitle.setForeground(DARK_TEXT);
        contactTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        contactCard.add(contactTitle, BorderLayout.NORTH);

        String[] columns = {"Ονοματεπώνυμο", "Πανεπιστημιακά Email"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        
        model.addRow(new Object[]{"Χρήστος Ντρέλιας", "up1112136@ac.upatras.gr"});
        model.addRow(new Object[]{"Νεκτάριος Μπληγιάννης", "up1108401@ac.upatras.gr"});
        model.addRow(new Object[]{"Χρήστος Κόλιας", "up1112119@ac.upatras.gr"});
        model.addRow(new Object[]{"Κωνσταντίνος Ζήρος", "up1108374@ac.upatras.gr"});
        model.addRow(new Object[]{"Σωτήρης Μαρκάκης", "up1108363@ac.upatras.gr"});

        JTable contactTable = new JTable(model);
        styleTable(contactTable);
        contactTable.setRowHeight(50);
        
        JScrollPane scrollPane = new JScrollPane(contactTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        contactCard.add(scrollPane, BorderLayout.CENTER);

        contactMainPanel.add(contactCard);
        return contactMainPanel;
    }

    private JPanel createCustomerPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(25, 0));
        mainPanel.setBackground(LIGHT_BG);
        
        JPanel catalogPanel = new JPanel(new BorderLayout(20, 20));
        catalogPanel.setBackground(LIGHT_BG);
        
        String[] catalogColumns = {"Προϊόν", "Απόθεμα", "Τιμή"};
        DefaultTableModel catalogModel = new DefaultTableModel(catalogColumns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable catalogTable = new JTable(catalogModel);
        styleTable(catalogTable);
        catalogTable.setRowHeight(55);

        for (Product product : dbManager.readProducts()) {
            catalogModel.addRow(new Object[]{product.getProductName(), product.getQuantity() + " τεμ", String.format(java.util.Locale.US, "%.2f €", product.getPrice())});
        }
        catalogPanel.add(createTitledPanel("Μενού Καταστήματος", new JScrollPane(catalogTable)), BorderLayout.CENTER);
        
        JPanel addToCartPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        addToCartPanel.setBackground(CARD_BG);
        addToCartPanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
        
        JLabel qtyLabel = new JLabel("Ποσότητα:");
        qtyLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addToCartPanel.add(qtyLabel);
        
        JTextField quantityField = new JTextField("1", 4);
        quantityField.setFont(new Font("Segoe UI", Font.BOLD, 16));
        quantityField.setHorizontalAlignment(JTextField.CENTER);
        addToCartPanel.add(quantityField);
        
        JButton addToCartBtn = createRoundedButton("Προσθήκη στην Παραγγελία", BRAND_PRIMARY);
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
        
        JPanel sidebarPanel = new JPanel(new BorderLayout(15, 15));
        sidebarPanel.setPreferredSize(new Dimension(380, 0));
        sidebarPanel.setBackground(CARD_BG);
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
        cartList.setFixedCellHeight(45);

        JScrollPane cartScroll = new JScrollPane(cartList);
        cartScroll.setBorder(BorderFactory.createEmptyBorder());
        sidebarPanel.add(cartScroll, BorderLayout.CENTER);

        JPanel checkoutPanel = new JPanel(new GridLayout(4, 1, 12, 12));
        checkoutPanel.setBackground(CARD_BG);

        JLabel totalLabel = new JLabel("Σύνολο: 0.00€", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        totalLabel.setForeground(BRAND_PRIMARY);

        JButton checkoutBtn = createRoundedButton("Ολοκλήρωση Αγοράς", SUCCESS_GREEN);
        JButton makeGroupBtn = createRoundedButton("Δήλωση ως Ομαδική", BRAND_PRIMARY);
        JButton clearCartBtn = createRoundedButton("Καθαρισμός Καλαθιού", DANGER_RED);

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

    private JPanel createProducerPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 25, 25));
        panel.setBackground(LIGHT_BG);

        String[] productColumns = {"ID", "Όνομα", "Απόθεμα", "Τιμή", "Κανόνες"};
        productTableModel = new DefaultTableModel(productColumns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable productTable = new JTable(productTableModel);
        styleTable(productTable);

        JPanel productActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        productActionPanel.setBackground(CARD_BG);
        JButton addBtn = createRoundedButton("Νέο Προϊόν", BRAND_PRIMARY);
        JButton editBtn = createRoundedButton("Επεξεργασία", WARNING_ORANGE);
        JButton deleteBtn = createRoundedButton("Διαγραφή", DANGER_RED);
        productActionPanel.add(addBtn); productActionPanel.add(editBtn); productActionPanel.add(deleteBtn);

        JPanel prodContainer = new JPanel(new BorderLayout());
        prodContainer.add(new JScrollPane(productTable), BorderLayout.CENTER);
        prodContainer.add(productActionPanel, BorderLayout.SOUTH);
        panel.add(createTitledPanel("Διαθέσιμα Προϊόντα στο Στοκ", prodContainer));

        String[] orderColumns = {"ID Παραγγελίας", "Πελάτης / Προϊόντα", "Κατάσταση"};
        producerOrderTableModel = new DefaultTableModel(orderColumns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable orderTable = new JTable(producerOrderTableModel);
        styleTable(orderTable);

        JPanel orderActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        orderActionPanel.setBackground(CARD_BG);
        JButton acceptOrderBtn = createRoundedButton("Ετοιμασία Παραγγελίας", SUCCESS_GREEN);
        JButton cancelOrderBtn = createRoundedButton("Ακύρωση", DANGER_RED);
        orderActionPanel.add(acceptOrderBtn); orderActionPanel.add(cancelOrderBtn);
        
        JPanel ordContainer = new JPanel(new BorderLayout());
        ordContainer.add(new JScrollPane(orderTable), BorderLayout.CENTER);
        ordContainer.add(orderActionPanel, BorderLayout.SOUTH);

        panel.add(createTitledPanel("Εκκρεμείς Παραγγελίες προς Προετοιμασία", ordContainer));

        addBtn.addActionListener(e -> showProductDialog(null));
        editBtn.addActionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Επιλέξτε ένα προϊόν από τον πίνακα!", "Προσοχή", JOptionPane.WARNING_MESSAGE); return; }
            String productId = (String) productTableModel.getValueAt(row, 0);
            Product selectedProduct = null;
            for (Product p : dbManager.readProducts()) {
                if (p.getProductId().equals(productId)) { selectedProduct = p; break; }
            }
            if (selectedProduct != null) showProductDialog(selectedProduct);
        });

        deleteBtn.addActionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Επιλέξτε ένα προϊόν για διαγραφή!", "Προσοχή", JOptionPane.WARNING_MESSAGE); return; }
            int confirm = JOptionPane.showConfirmDialog(this, "Θέλετε να διαγράψετε αυτό το προϊόν;", "Επιβεβαίωση", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String productId = (String) productTableModel.getValueAt(row, 0);
                dbManager.readProducts().removeIf(p -> p.getProductId().equals(productId));
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
            refreshAllTables(); JOptionPane.showMessageDialog(this, "Η παραγγελία ετοιμάστηκε! Ειδοποιήθηκε ο διανομέας.");
        });

        return panel;
    }

    private void showProductDialog(Product existingProduct) {
        JDialog dialog = new JDialog(this, existingProduct == null ? "Προσθήκη Νέου Προϊόντος" : "Επεξεργασία Προϊόντος", true);
        dialog.setSize(480, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel form = new JPanel(new GridLayout(4, 2, 15, 25));
        form.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        form.setBackground(CARD_BG);
        
        JTextField nameField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField rulesField = new JTextField();
        
        if (existingProduct != null) {
            nameField.setText(existingProduct.getProductName());
            quantityField.setText(String.valueOf(existingProduct.getQuantity()));
            priceField.setText(String.format(java.util.Locale.US, "%.2f", existingProduct.getPrice()));
            rulesField.setText(existingProduct.getPricingRules());
        }

        form.add(new JLabel("Όνομα Προϊόντος:")); form.add(nameField);
        form.add(new JLabel("Διαθέσιμο Απόθεμα:")); form.add(quantityField);
        form.add(new JLabel("Τιμή Μονάδας (€):")); form.add(priceField);
        form.add(new JLabel("Κανόνες Χονδρικής:")); form.add(rulesField);
        
        dialog.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttons.setBackground(LIGHT_BG);
        JButton saveBtn = createRoundedButton("Αποθήκευση", SUCCESS_GREEN);
        JButton cancelBtn = createRoundedButton("Ακύρωση", Color.GRAY);
        buttons.add(cancelBtn); buttons.add(saveBtn);
        dialog.add(buttons, BorderLayout.SOUTH);

        cancelBtn.addActionListener(e -> dialog.dispose());
        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                int qty = Integer.parseInt(quantityField.getText().trim());
                double price = Double.parseDouble(priceField.getText().replace(",", ".").trim());
                if (name.isEmpty()) return;
                
                if (existingProduct == null) {
                    dbManager.saveProduct(new Product("P" + (dbManager.readProducts().size() + 1), name, qty, price, rulesField.getText(), true, 4.5, "pickup", 5.0));
                } else {
                    for (int i = 0; i < dbManager.readProducts().size(); i++) {
                        if (dbManager.readProducts().get(i).getProductId().equals(existingProduct.getProductId())) {
                            dbManager.readProducts().set(i, new Product(existingProduct.getProductId(), name, qty, price, rulesField.getText(), true, existingProduct.getProducerRating(), "pickup", 5.0));
                            dbManager.updateProductsFile();
                            break;
                        }
                    }
                }
                refreshAllTables();
                dialog.dispose();
            } catch (Exception ex) { JOptionPane.showMessageDialog(dialog, "Σφάλμα εισαγωγής δεδομένων."); }
        });
        dialog.setVisible(true);
    }

    class DeliveryMapPanel extends JPanel {
        private String currentStatus = "";
        public void updateMap(String status) { this.currentStatus = status; repaint(); }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(new Color(241, 245, 249)); g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setColor(new Color(224, 231, 255));
            g2d.fillPolygon(new int[]{0, 350, 280, 180, 0}, new int[]{0, 0, getHeight()/2, getHeight(), getHeight()}, 5);

            g2d.setColor(Color.WHITE); g2d.setStroke(new BasicStroke(6));
            for(int i = 250; i <= 600; i += 40) { g2d.drawLine(i, 20, i - 140, getHeight() - 20); }
            int startX = getWidth() - 180, startY = getHeight() - 70, endX = 220, endY = 70;

            g2d.setColor(BRAND_PRIMARY);
            g2d.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{12}, 0));
            g2d.drawLine(startX, startY, endX, endY);

            g2d.setColor(SUCCESS_GREEN);
            g2d.fillOval(startX - 12, startY - 12, 24, 24);
            g2d.setColor(DANGER_RED);
            g2d.fillRect(endX - 12, endY - 12, 24, 24);

            int pinX = -100, pinY = -100;
            if ("Έτοιμη για Παραλαβή".equals(currentStatus)) { pinX = startX; pinY = startY; }
            else if ("Προς Παράδοση".equals(currentStatus)) { pinX = startX + (endX - startX) / 2; pinY = startY + (endY - startY) / 2; }
            else if ("Ολοκληρώθηκε".equals(currentStatus)) { pinX = endX; pinY = endY; }

            if (pinX != -100) {
                g2d.setColor(BRAND_PRIMARY);
                g2d.fillOval(pinX - 8, pinY - 8, 16, 16);
                g2d.setStroke(new BasicStroke(2));
                g2d.setColor(Color.WHITE);
                g2d.drawOval(pinX - 8, pinY - 8, 16, 16);
            }
        }
    }

    private JPanel createCourierPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(25, 25));
        mainPanel.setBackground(LIGHT_BG);
        
        mapPanel = new DeliveryMapPanel();
        mapPanel.setPreferredSize(new Dimension(1000, 260));
        mainPanel.add(createTitledPanel("Live Παρακολούθηση Διαδρομής Διανομής", mapPanel), BorderLayout.NORTH);

        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 25, 25));
        tablesPanel.setBackground(LIGHT_BG);

        JPanel availablePanel = new JPanel(new BorderLayout(15, 15));
        availableDeliveriesModel = new DefaultTableModel(new String[]{"ID", "Παραγγελία", "Κατάσταση"}, 0);
        availableTable = new JTable(availableDeliveriesModel);
        styleTable(availableTable);
        availablePanel.add(createTitledPanel("Διαθέσιμες Παραγγελίες προς Παραλαβή", new JScrollPane(availableTable)), BorderLayout.CENTER);
        JButton routeBtn = createRoundedButton("Αποδοχή & Ανάληψη Δρομολογίου", BRAND_PRIMARY);
        availablePanel.add(routeBtn, BorderLayout.SOUTH);

        JPanel activeRoutePanel = new JPanel(new BorderLayout(15, 15));
        activeRouteModel = new DefaultTableModel(new String[]{"ID", "Παραγγελία", "Κατάσταση"}, 0);
        routeTable = new JTable(activeRouteModel);
        styleTable(routeTable);
        activeRoutePanel.add(createTitledPanel("Το Τρέχον Φορτίο μου", new JScrollPane(routeTable)), BorderLayout.CENTER);
        JButton qrBtn = createRoundedButton("Παράδοση (Σκανάρισμα QR)", SUCCESS_GREEN);
        activeRoutePanel.add(qrBtn, BorderLayout.SOUTH);

        tablesPanel.add(availablePanel);
        tablesPanel.add(activeRoutePanel);
        mainPanel.add(tablesPanel, BorderLayout.CENTER);

        availableTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && availableTable.getSelectedRow() != -1) {
                routeTable.clearSelection(); mapPanel.updateMap("Έτοιμη για Παραλαβή");
            }
        });
        routeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && routeTable.getSelectedRow() != -1) {
                availableTable.clearSelection(); mapPanel.updateMap("Προς Παράδοση");
            }
        });

        routeBtn.addActionListener(e -> {
            int selectedRow = availableTable.getSelectedRow();
            if (selectedRow == -1) return;
            int orderId = (int) availableDeliveriesModel.getValueAt(selectedRow, 0);
            for (Order order : dbManager.queryOrders()) {
                if (order.getOrderId() == orderId) { order.setStatus("Προς Παράδοση"); dbManager.updateOrdersFile(); break; }
            }
            refreshAllTables();
        });

        qrBtn.addActionListener(actionEvent -> {
            int selectedRow = routeTable.getSelectedRow();
            if (selectedRow == -1) return;
            int orderId = (int) activeRouteModel.getValueAt(selectedRow, 0);
            JDialog qrDialog = new JDialog(this, "Ψηφιακή Απόδειξη Παράδοσης", true);
            qrDialog.setSize(360, 440);
            qrDialog.setLocationRelativeTo(this);
            qrDialog.setLayout(new BorderLayout(10, 10));
            
            java.awt.image.BufferedImage qrImage = generateMockQRCode("ORDER-" + orderId);
            JLabel qrLabel = new JLabel(new ImageIcon(qrImage), SwingConstants.CENTER);
            JButton confirmScanBtn = createRoundedButton("Επιβεβαίωση Παράδοσης", SUCCESS_GREEN);
            
            confirmScanBtn.addActionListener(e -> {
                qrDialog.dispose();
                for (Order order : dbManager.queryOrders()) {
                    if (order.getOrderId() == orderId) { order.setStatus("Ολοκληρώθηκε"); dbManager.updateOrdersFile(); break; }
                }
                refreshAllTables();
            });

            qrDialog.add(qrLabel, BorderLayout.CENTER);
            qrDialog.add(confirmScanBtn, BorderLayout.SOUTH);
            qrDialog.setVisible(true);
        });

        return mainPanel;
    }

    private JPanel createAdminPanel() {
        JPanel mainAdminPanel = new JPanel(new GridLayout(2, 1, 25, 25));
        mainAdminPanel.setBackground(LIGHT_BG);

        String[] settlementColumns = {"ID Παραγγελίας", "Περιγραφή", "Συνολικό Ποσό", "Κατάσταση"};
        settlementTableModel = new DefaultTableModel(settlementColumns, 0);
        JTable settlementTable = new JTable(settlementTableModel);
        styleTable(settlementTable);
        
        JPanel settlementPanel = createTitledPanel("Οικονομικές Εκκαθαρίσεις Παραγωγών", new JScrollPane(settlementTable));
        JButton executeSettlementBtn = createRoundedButton("Εκτέλεση Εκκαθάρισης Πληρωμής", WARNING_ORANGE);
        settlementPanel.add(executeSettlementBtn, BorderLayout.SOUTH);
        mainAdminPanel.add(settlementPanel);

        executeSettlementBtn.addActionListener(actionEvent -> {
            int row = settlementTable.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Επιλέξτε μια ολοκληρωμένη παραγγελία!"); return; }
            int oId = (int) settlementTableModel.getValueAt(row, 0);
            for (Order order : dbManager.queryOrders()) {
                if (order.getOrderId() == oId) {
                    order.setStatus("Εκκαθαρίστηκε");
                    double total = order.getPrice();
                    refreshAllTables();
                    JOptionPane.showMessageDialog(this, "Η εκκαθάριση ολοκληρώθηκε!\n\nΑξία: " + total + " ευρώ\nΠρομήθεια FarmSync (10%): " + (total*0.10) + " ευρώ\nΚαθαρό Ποσό Παραγωγού (90%): " + (total*0.90) + " ευρώ", "Financial Statement", JOptionPane.INFORMATION_MESSAGE);
                    break;
                }
            }
        });

        String[] refundColumns = {"ID Αιτήματος", "Παραγγελία", "Ποσό", "Κατάσταση"};
        refundTableModel = new DefaultTableModel(refundColumns, 0);
        JTable refundTable = new JTable(refundTableModel);
        styleTable(refundTable);
        
        JPanel refundPanel = createTitledPanel("Κέντρο Επίλυσης Διαφορών και Καταγγελιών", new JScrollPane(refundTable));
        JPanel refundActionsPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        refundActionsPanel.setBackground(CARD_BG);
        JButton approveRefundBtn = createRoundedButton("Έγκριση Επιστροφής Χρημάτων", SUCCESS_GREEN);
        JButton rejectRefundBtn = createRoundedButton("Απόρριψη Αιτήματος", DANGER_RED);
        refundActionsPanel.add(approveRefundBtn); refundActionsPanel.add(rejectRefundBtn);
        refundPanel.add(refundActionsPanel, BorderLayout.SOUTH);
        
        mainAdminPanel.add(refundPanel);

        approveRefundBtn.addActionListener(e -> {
            int r = refundTable.getSelectedRow();
            if (r != -1) { dbManager.updateRefundStatus((int)refundTableModel.getValueAt(r,0), RefundStatus.APPROVED); refreshAllTables(); }
        });
        rejectRefundBtn.addActionListener(e -> {
            int r = refundTable.getSelectedRow();
            if (r != -1) { dbManager.updateRefundStatus((int)refundTableModel.getValueAt(r,0), RefundStatus.REJECTED); refreshAllTables(); }
        });

        return mainAdminPanel;
    }

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
    }

    private void refreshSettlementTable() {
        if (settlementTableModel == null) return;
        textCardSettlement();
    }
    
    private void textCardSettlement() {
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

    private JPanel createTitledPanel(String title, Component content) {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(DARK_TEXT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(46);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowVerticalLines(false);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(new Color(243, 244, 246));
        table.setSelectionForeground(BRAND_PRIMARY);

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setForeground(DARK_TEXT);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, table);
        if (scrollPane != null) {
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getViewport().setBackground(Color.WHITE);
        }
    }

    private JButton createRoundedButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bgColor.darker() : bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        return button;
    }

    private java.awt.image.BufferedImage generateMockQRCode(String data) {
        int size = 200;
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE); g.fillRect(0, 0, size, size); g.setColor(new Color(15, 23, 42));
        java.util.Random rand = new java.util.Random(data.hashCode());
        int cellSize = 8;
        for (int i = 16; i < size - 16; i += cellSize) {
            for (int j = 16; j < size - 16; j += cellSize) {
                if (rand.nextBoolean()) g.fillRect(i, j, cellSize, cellSize);
            }
        }
        drawQRMarker(g, 16, 16); drawQRMarker(g, size - 56, 16); drawQRMarker(g, 16, size - 56);
        g.dispose(); return image;
    }

    private void drawQRMarker(Graphics2D g, int x, int y) {
        g.setColor(Color.WHITE); g.fillRect(x, y, 40, 40);
        g.setColor(new Color(15, 23, 42)); g.fillRect(x, y, 40, 40);
        g.setColor(Color.WHITE); g.fillRect(x + 6, y + 6, 28, 28);
        g.setColor(new Color(15, 23, 42)); g.fillRect(x + 12, y + 12, 16, 16);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new Gui().setVisible(true));
    }
}
