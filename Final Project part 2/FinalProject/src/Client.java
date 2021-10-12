import com.google.gson.Gson;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
// source code was given by TA, modified by me!
public class Client {
    private JTextArea messageTextArea;
    private JButton manageCheckoutButton;
    private JButton manageProductButton;
    private JButton manageCustomerButton;
    private JPanel mainPanel;


    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    private Gson gson;

    private Worker worker;

    private ProductViewController productViewController;

    private CustomerViewController customerViewController;

    private OrderViewController orderViewController;

    public Client() {
        try {
            socket = new Socket(InetAddress.getByName("127.0.0.1"), 12002);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        gson = new Gson();

        worker = new Worker();
        Thread workerThread = new Thread(worker);
        workerThread.start();

        this.productViewController = new ProductViewController(this);
        this.customerViewController = new CustomerViewController(this);
        this.orderViewController = new OrderViewController(this);

        manageProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("Manage Product");
                frame.setContentPane(productViewController.getMainPanel());
                frame.setMinimumSize(new Dimension(800, 400));
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });
        manageCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("Manage Customer");
                frame.setContentPane(customerViewController.getMainPanel());
                frame.setMinimumSize(new Dimension(800, 400));
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });
        manageCheckoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("Checkout");
                frame.setContentPane(orderViewController.getMainPanel());
                frame.setMinimumSize(new Dimension(800, 400));
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });

    }

    public void sendMessage(Message message) {

        String str = gson.toJson(message);
        try {
            dataOutputStream.writeUTF(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private class Worker implements Runnable {

        @Override
        public void run() {
            while (true) {
                String replyString = null;
                try {
                    replyString = dataInputStream.readUTF();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Message message = gson.fromJson(replyString, Message.class);

                processMessage(message);

            }
        }
    }

    private void processMessage(Message message) {
        messageTextArea.append(gson.toJson(message) + "\n");
        switch (message.getId()) {
            case Message.LOAD_PRODUCT_REPLY: {
                Product product = gson.fromJson(message.getContent(), Product.class);
                productViewController.updateProductInfo(product);
                break;
            }
            case Message.LOAD_CUSTOMER_REPLY: {
                Customer customer = gson.fromJson(message.getContent(), Customer.class);
                customerViewController.updateCustomerInfo(customer);
                break;
            }
            case Message.SAVE_ORDER: {
                Order order = gson.fromJson(message.getContent(), Order.class);
                orderViewController.updateOrderInfo(order);
                break;
            }

            default:
                return;
        }

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Client");
        frame.setContentPane(new Client().mainPanel);
        frame.setMinimumSize(new Dimension(800, 400));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
