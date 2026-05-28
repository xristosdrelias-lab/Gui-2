package Gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.*;

public class DBManager {
    private List<Product> products = new ArrayList<>();
    private List<Order> orders = new ArrayList<>();
    private List<GroupPurchase> groupPurchases = new ArrayList<>();
    private final Map<Integer, RefundRequest> refundRequests = new HashMap<>();
    private final Map<Integer, Order> orderMap = new HashMap<>();
    private final Map<Integer, UserProfile> users = new HashMap<>();
    private final String PRODUCTS_FILE = "products.txt";
    private final String ORDERS_FILE = "orders.txt";

    public DBManager() {
        loadProductsFromFile();
        loadOrdersFromFile();

        groupPurchases.add(new GroupPurchase("GP001", "Πατάτες Νάξου", 500, 320, "Αθήνα"));
        users.put(1, new UserProfile(1, "Γιάννης Παπαδόπουλος", "giannis@email.com", "CUSTOMER"));
    }

    public boolean isFirstTimeSetup() {
        File file = new File(PRODUCTS_FILE);
        return !file.exists();
    }

    public void updateProductsFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PRODUCTS_FILE))) {
            bw.write("ID|ΟΝΟΜΑ_ΠΡΟΪΟΝΤΟΣ|ΑΠΟΘΕΜΑ|ΤΙΜΗ|ΚΑΝΟΝΕΣ_ΤΙΜΟΛΟΓΗΣΗΣ|ΔΙΑΘΕΣΙΜΟ|ΒΑΘΜΟΛΟΓΙΑ|ΠΑΡΑΔΟΣΗ|ΑΠΟΣΤΑΣΗ");
            bw.newLine();
            for (Product p : products) {
                String line = p.getProductId() + "|" + p.getProductName() + "|" + p.getQuantity() + "|" +
                        p.getPrice() + "|" + p.getPricingRules() + "|" + p.isAvailable() + "|" +
                        p.getProducerRating() + "|" + p.getDeliveryMethod() + "|" + p.getDistance();
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Σφάλμα αποθήκευσης προϊόντων: " + e.getMessage());
        }
    }

    private void loadProductsFromFile() {
        File file = new File(PRODUCTS_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) { isFirstLine = false; continue; }
                String[] parts = line.split("\\|", -1);
                if (parts.length >= 9) {
                    products.add(new Product(parts[0], parts[1], Integer.parseInt(parts[2]),
                            Double.parseDouble(parts[3]), parts[4], Boolean.parseBoolean(parts[5]),
                            Double.parseDouble(parts[6]), parts[7], Double.parseDouble(parts[8])));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Σφάλμα ανάγνωσης προϊόντων: " + e.getMessage());
        }
    }

    public void updateOrdersFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ORDERS_FILE))) {
            bw.write("ORDER_ID|USER_ID|ΠΕΡΙΓΡΑΦΗ_ΠΡΟΪΟΝΤΩΝ|ΣΤΟΙΧΕΙΑ ΠΕΛΑΤΗ|ΣΥΝΟΛΙΚΟ_ΚΟΣΤΟΣ|ΚΑΤΑΣΤΑΣΗ");
            bw.newLine();
            for (Order o : orders) {
                String line = o.getOrderId() + "|1|" + o.getProductName() + "|" + o.getPrice() + "|" + o.getStatus();
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Σφάλμα αποθήκευσης παραγγελιών: " + e.getMessage());
        }
    }

    private void loadOrdersFromFile() {
        File file = new File(ORDERS_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) { isFirstLine = false; continue; }
                String[] parts = line.split("\\|", -1);
                if (parts.length >= 5) {
                    Order order = new Order(
                            Integer.parseInt(parts[0]),
                            Integer.parseInt(parts[1]),
                            parts[2],
                            Double.parseDouble(parts[3])
                    );
                    order.setStatus(parts[4]);
                    orders.add(order);
                    orderMap.put(order.getOrderId(), order);
                }
            }
        } catch (Exception e) {
            System.out.println("Σφάλμα ανάγνωσης παραγγελιών: " + e.getMessage());
        }
    }
    public void addOrder(Order order) {
        orders.add(order);
        orderMap.put(order.getOrderId(), order);
        updateOrdersFile();
    }
    public List<Product> readProducts() { return products; }
    public boolean validateProductDetails(Product p) { return p.getQuantity() >= 0; }
    public boolean checkPricingRules(Product p) { return p.getPricingRules() != null; }
    public void saveProduct(Product p) {
        products.add(p);
        updateProductsFile();
    }
    public List<Order> queryOrders() { return orders; }
    public boolean checkQuantity() { return true; }
    public void reduceQuantity() { updateProductsFile(); }
    public void validateOrderDetails() {}
    public List<Product> searchProducts(String keyword) { return products; }
    public boolean checkAvailability(Product p, int qty) { return p.getQuantity() >= qty; }
    public void reserveStock(Product p, int qty) {
        p.setQuantity(p.getQuantity() - qty);
        updateProductsFile();
    }
    public void storeRecurringOrder(Object ro) {}
    public void findAvailableProducers() {}
    public void sendRequestToProducers() {}
    public List<GroupPurchase> findGroupPurchasesByLocation(String location) { return groupPurchases; }
    public boolean validateQuantity(double qty) { return qty > 0; }
    public void registerUserToGroupPurchase(Object u, GroupPurchase gp, double qty) {
        gp.setCurrentQuantity(gp.getCurrentQuantity() + qty);
    }
    public void lockOrders(Object o) {}
    public boolean checkQRCode(String code) { return true; }
    public void saveRefundRequest(RefundRequest request) {
        refundRequests.put(request.getRefundId(), request);
    }
    public List<RefundRequest> getAllRefundRequests() {
        return new ArrayList<>(refundRequests.values());
    }
    public Order getOrder(int orderId) {
        return orderMap.get(orderId);
    }
    public UserProfile getUserProfile(int userId) {
        return users.get(userId);
    }
    public void updateRefundStatus(int refundId, RefundStatus status) {
        RefundRequest req = refundRequests.get(refundId);
        if (req != null) req.setStatus(status);
    }
}