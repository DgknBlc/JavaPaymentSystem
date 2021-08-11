import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class
MainForm extends JDialog {
    private JPanel contentPane;
    private JPanel connectPanel;
    private JTextField ipTField;
    private JButton connectBtn;
    private JPanel buyPanel;
    private JTextField telTField;
    private JButton buyBtn;
    private JTable productTable;
    private JScrollPane productPane;

    Socket s;
    DataInputStream din;
    DataOutputStream dout;

    public MainForm() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(connectBtn);

        buyPanel.setVisible(false);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        connectBtn.addActionListener(e -> {
            try {
                s = new Socket(ipTField.getText(),3333);
                din = new DataInputStream(s.getInputStream());
                dout = new DataOutputStream(s.getOutputStream());
                buyPanel.setVisible(true);
                connectPanel.setVisible(false);
                getRootPane().setDefaultButton(buyBtn);
                dout.writeUTF("gap");
                String str = din.readUTF();
                writeToTable(str);
            } catch (IOException connectException){
                JOptionPane.showMessageDialog(contentPane, "Hata : Baglantı Saglanamadi.");
                connectException.printStackTrace();
            }
        });
        buyBtn.addActionListener(e -> {

            try {
                if(telTField.getText().length() == 11){
                    int row = productTable.getSelectedRow();
                    dout.writeUTF(telTField.getText() + " " + productTable.getModel().getValueAt(row, 0));
                    dout.flush();
                    JOptionPane.showMessageDialog(contentPane, "İşlemin Tamamlanması İçin Telefonuzdan Onay Verin.");
                    String str = din.readUTF();
                    JOptionPane.showMessageDialog(contentPane, str);
                }else{
                    JOptionPane.showMessageDialog(contentPane, "Tel No Boş ya da Yanlış.");
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }


        });
        this.setMinimumSize(new Dimension(300,300));
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        MainForm dialog = new MainForm();
        dialog.setSize(500,500);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    void writeToTable(String str){

        String[] row = str.split("!");

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Seller");
        model.addColumn("Price");
        productTable.setModel(model);
        for (String value : row) {
            model.addRow(value.split("-"));
        }

    }
}
