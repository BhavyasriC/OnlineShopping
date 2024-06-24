package shopping;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OrderProcessingWithDataBase {

    static Connection con;

    public static Connection createDBConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/shopping";
        String userName = "root";
        String password = "Mysql@2024";
        con = DriverManager.getConnection(url, userName, password);
        return con;
    }

    public static void createTables() throws ClassNotFoundException {
        String createProductTableSQL = "CREATE TABLE IF NOT EXISTS productdetails ("
                + "ProductCode INT NOT NULL PRIMARY KEY, "
                + "ProductName VARCHAR(255) NOT NULL, "
                + "price DOUBLE NOT NULL, "
                + "quantity INT NOT NULL )";

        String createOrdersTableSQL = "CREATE TABLE IF NOT EXISTS orders ("
                + "orderId INT PRIMARY KEY AUTO_INCREMENT, "
                + "productCode INT NOT NULL, "
                + "quantity INT NOT NULL)";

        try (Connection connection = createDBConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute(createProductTableSQL);
            stmt.execute(createOrdersTableSQL);
            System.out.println("Tables are created!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean insertProducts(Product product) throws ClassNotFoundException {
        try (Connection connection = createDBConnection()) {
            if (productExist(product.getProductCode())) {
                System.out.println("Product code already exists. Please enter a different product code.");
                return false;
            }
            String query = "INSERT INTO productdetails (ProductCode, ProductName, price, quantity) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, product.getProductCode());
            statement.setString(2, product.getProductName());
            statement.setDouble(3, product.getPrice());
            statement.setInt(4, product.getQuantity());
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean productExist(int productCode) throws ClassNotFoundException {
        try (Connection connection = createDBConnection()) {
            String query = "SELECT COUNT(*) AS count FROM productdetails WHERE ProductCode = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, productCode);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Product readProductByCode(int productCode) throws ClassNotFoundException {
        try (Connection connection = createDBConnection()) {
            String query = "SELECT * FROM productdetails WHERE ProductCode = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, productCode);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
            	 productCode = resultSet.getInt("ProductCode");
                String productName = resultSet.getString("ProductName");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");

                return new Product(productCode, productName, price, quantity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Product readProductByName(String productName) throws ClassNotFoundException {
        try (Connection connection = createDBConnection()) {
            String query = "SELECT * FROM productdetails WHERE ProductName = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, productName);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int productCode = resultSet.getInt("ProductCode");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");

                return new Product(productCode, productName, price, quantity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Product> readAllProducts() throws ClassNotFoundException {
        List<Product> products = new ArrayList<>();
        try (Connection connection = createDBConnection()) {
            String query = "SELECT * FROM productdetails";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int productCode = resultSet.getInt("ProductCode");
                String productName = resultSet.getString("ProductName");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");

                products.add(new Product(productCode, productName, price, quantity));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public static boolean updateProduct(Product product) throws ClassNotFoundException {
        try (Connection connection = createDBConnection()) {
            String query = "UPDATE productdetails SET ProductName = ?, price = ?, quantity = ? WHERE ProductCode = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, product.getProductName());
            statement.setDouble(2, product.getPrice());
            statement.setInt(3, product.getQuantity());
            statement.setInt(4, product.getProductCode());
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteProduct(int productCode) throws ClassNotFoundException {
        try (Connection connection = createDBConnection()) {
            String query = "DELETE FROM productdetails WHERE ProductCode = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, productCode); // Corrected line
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean placeOrder(int productCode, int quantity) throws ClassNotFoundException, SQLException {
        Product product = readProductByCode(productCode);
        if (product == null || product.getQuantity() < quantity) {
            return false;
        }

        String updateProductQuery = "UPDATE productdetails SET quantity = ? WHERE ProductCode = ?";
        try (Connection connection = createDBConnection();
             PreparedStatement updateStatement = connection.prepareStatement(updateProductQuery)) {
            updateStatement.setInt(1, product.getQuantity() - quantity);
            updateStatement.setInt(2, productCode);
            updateStatement.executeUpdate();

            String insertOrderQuery = "INSERT INTO orders (productCode, quantity) VALUES (?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertOrderQuery)) {
                insertStatement.setInt(1, productCode);
                insertStatement.setInt(2, quantity);
                insertStatement.executeUpdate();
            }
            return true;
        }
    }

    public static void displayAllOrders() throws ClassNotFoundException {
        try (Connection connection = createDBConnection()) {
            String query = "SELECT * FROM orders";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            System.out.printf("%-15s | %-8s%n", "Order Id", "Product Code");

            while (resultSet.next()) {
                int orderId = resultSet.getInt("orderId");
                int productCode = resultSet.getInt("productCode");

                System.out.printf("%-15d | %-8d%n", orderId, productCode);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteOrder(int orderId) throws ClassNotFoundException {
        try (Connection connection = createDBConnection()) {
            String query = "DELETE FROM orders WHERE orderId = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, orderId);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Integer> getOrdersByProductCode(int productCode) throws ClassNotFoundException {
        List<Integer> orderIds = new ArrayList<>();
        try (Connection connection = createDBConnection()) {
            String query = "SELECT orderId FROM orders WHERE productCode = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, productCode);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int orderId = resultSet.getInt("orderId");
                orderIds.add(orderId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderIds;
    }

    public static void displayProductDetails() throws ClassNotFoundException {
        try (Connection connection = createDBConnection()) {
            String query = "SELECT * FROM productdetails";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            System.out.printf("%-15s | %-20s | %-20s | %-8s%n", "Product Code", "Product Name", "Price", "Quantity");

            while (resultSet.next()) {
                int productCode = resultSet.getInt("ProductCode");
                String productName = resultSet.getString("ProductName");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");

                System.out.printf("%-15d | %-20s | %-20.2f | %-8d%n", productCode, productName, price, quantity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

