import com.google.gson.Gson;

import javax.annotation.processing.Processor;
import javax.xml.crypto.Data;
import java.sql.*;
// source code was given by TA, modified by me!
public class DatabaseManager {

    private static DatabaseManager databaseManager;
    public static DatabaseManager getInstance() {
        if (databaseManager == null) databaseManager = new DatabaseManager();
        return databaseManager;
    }

    private Connection connection;

    private DatabaseManager() {

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:data/store.db");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public Message process(String requestString) {

        Gson gson = new Gson();
        Message message = gson.fromJson(requestString, Message.class);

        switch (message.getId()) {
            case Message.LOAD_PRODUCT: {
                Product product = loadProduct(Integer.parseInt(message.getContent()));
                Message replyMessage = new Message(Message.LOAD_PRODUCT_REPLY, gson.toJson(product));
                return replyMessage;
            }

            case Message.SAVE_PRODUCT: {
                Product product = gson.fromJson(message.getContent(), Product.class);
                boolean result = saveProduct(product);
                if (result) return new Message(Message.SUCCESS, "Product saved");
                else return new Message(Message.FAIL, "Cannot save the product");
            }

            case Message.LOAD_CUSTOMER: {
                Customer customer = loadCustomer(Integer.parseInt(message.getContent()));
                Message replyMessage = new Message(Message.LOAD_CUSTOMER_REPLY, gson.toJson(customer));
                return replyMessage;
            }

            case Message.SAVE_CUSTOMER: {
                Customer customer = gson.fromJson(message.getContent(), Customer.class);
                boolean result = saveCustomer(customer);
                if (result) return new Message(Message.SUCCESS, "Customer saved");
                else return new Message(Message.FAIL, "Cannot save the customer");
            }

            case Message.SAVE_ORDER: {
                Order order = gson.fromJson(message.getContent(), Order.class);
                boolean result = saveOrder(order);
                if (result) return new Message(Message.SUCCESS, "Order saved");
                else return new Message(Message.FAIL, "Could not save the order");
            }


            default:
                return new Message(Message.FAIL, "Cannot process the message");
        }
    }

    public Product loadProduct(int id) {
        try {
            String query = "SELECT * FROM Products WHERE ProductID = " + id;

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                Product product = new Product();
                product.setProductID(resultSet.getInt(1));
                product.setName(resultSet.getString(2));
                product.setPrice(resultSet.getDouble(3));
                product.setQuantity(resultSet.getDouble(4));
                resultSet.close();
                statement.close();

                return product;
            }

        } catch (SQLException e) {
            System.out.println("Database access error!");
            e.printStackTrace();
        }
        return null;
    }

    public boolean saveProduct(Product product) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Products WHERE ProductID = ?");
            statement.setInt(1, product.getProductID());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) { // this product exists, update its fields
                statement = connection.prepareStatement("UPDATE Products SET Name = ?, Price = ?, Quantity = ? WHERE ProductID = ?");
                statement.setString(1, product.getName());
                statement.setDouble(2, product.getPrice());
                statement.setDouble(3, product.getQuantity());
                statement.setInt(4, product.getProductID());
            }
            else { // this product does not exist, use insert into
                statement = connection.prepareStatement("INSERT INTO Products VALUES (?, ?, ?, ?)");
                statement.setString(2, product.getName());
                statement.setDouble(3, product.getPrice());
                statement.setDouble(4, product.getQuantity());
                statement.setInt(1, product.getProductID());
            }
            statement.execute();
            resultSet.close();
            statement.close();
            return true;        // save successfully

        } catch (SQLException e) {
            System.out.println("Database access error!");
            e.printStackTrace();
            return false; // cannot save!
        }
    }

    public Customer loadCustomer(int id) {
        try {
            String query = "SELECT * FROM Customers WHERE CustomerID = " + id;

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                Customer customer = new Customer();
                customer.setCustomerID(resultSet.getString(1));
                customer.setFirstName(resultSet.getString(2));
                customer.setLastName(resultSet.getString(3));
                customer.setPhoneNumber(resultSet.getString(4));
                resultSet.close();
                statement.close();

                return customer;
            }

        } catch (SQLException e) {
            System.out.println("Database access error!");
            e.printStackTrace();
        }
        return null;
    }

    public boolean saveCustomer(Customer customer) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Customers WHERE CustomerID = ?");
            statement.setString(1, customer.getCustomerID());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) { // this product exists, update its fields
                statement = connection.prepareStatement("UPDATE Customers SET FirstName = ?, LastName = ?, PhoneNumber = ? WHERE CustomerID = ?");
                statement.setString(1, customer.getCustomerID());
                statement.setString(2, customer.getFirstName());
                statement.setString(3, customer.getLastName());
                statement.setString(4, customer.getPhoneNumber());
            }
            else { // this product does not exist, use insert into
                statement = connection.prepareStatement("INSERT INTO Customers VALUES (?, ?, ?, ?)");
                statement.setString(2, customer.getCustomerID());
                statement.setString(3, customer.getFirstName());
                statement.setString(4, customer.getLastName());
                statement.setString(1, customer.getPhoneNumber());
            }
            statement.execute();
            resultSet.close();
            statement.close();
            return true;        // save successfully

        } catch (SQLException e) {
            System.out.println("Database access error!");
            e.printStackTrace();
            return false; // cannot save!
        }
    }
    public boolean saveOrder(Order order) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Purchases WHERE OrderID = ?");
            statement.setInt(1, order.getOrderID());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) { // this product exists, update its fields
                statement = connection.prepareStatement("UPDATE Purchases SET CustomerID = ?, TotalCost = ?, TotalTax = ?, Date = ? WHERE OrderID = ?");
                statement.setInt(1, order.getOrderID());
                statement.setString(2, order.getCustomerID());
                statement.setDouble(3, order.getTotalCost());
                statement.setDouble(4, order.getTotalTax());
                statement.setDate(5, order.getDate());
            }
            else { // this product does not exist, use insert into
                statement = connection.prepareStatement("INSERT INTO Purchases VALUES (?, ?, ?, ?, ?)");
                statement.setInt(2, order.getOrderID());
                statement.setString(3, order.getCustomerID());
                statement.setDouble(4, order.getTotalCost());
                statement.setDouble(1, order.getTotalTax());
                statement.setDate(5, order.getDate());
            }
            statement.execute();
            resultSet.close();
            statement.close();
            return true;        // save successfully

        } catch (SQLException e) {
            System.out.println("Database access error!");
            e.printStackTrace();
            return false; // cannot save!
        }
    }



}
