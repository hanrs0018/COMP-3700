import com.google.gson.Gson;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;

public class OrderViewController {
    private JTextField orderIDTF;
    private JTextField customerIDTF;
    private JTextField totalCostTF;
    private JTextField totalTaxTF;
    private JTextField dateTF;
    private JButton loadOrderButton;
    private JButton saveOrderButton;
    private JPanel mainPanel;

    private Client client;

    public OrderViewController(Client client) {
        this.client = client;

        saveOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Order order = new Order();

                order.setOrderID(Integer.parseInt(orderIDTF.getText()));
                order.setCustomerID(customerIDTF.getText());
                order.setTotalCost(Double.parseDouble(totalCostTF.getText()));
                order.setTotalTax(Double.parseDouble(totalTaxTF.getText()));
                order.setDate(Date.valueOf(dateTF.getText()));

                Gson gson = new Gson();

                String orderString = gson.toJson(order);

                Message message = new Message(Message.SAVE_ORDER, orderString);
                client.sendMessage(message);
            }
        });
    }
    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void updateOrderInfo(Order order) {
        orderIDTF.setText(String.valueOf(order.getOrderID()));
        customerIDTF.setText(order.getCustomerID());
        totalCostTF.setText(String.valueOf(order.getTotalCost()));
        totalTaxTF.setText(String.valueOf(order.getTotalTax()));
        dateTF.setText(String.valueOf(order.getDate()));
    }
}
