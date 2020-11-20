import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.Key;
import java.util.Base64;

public class EncrypterWindow extends JFrame implements ActionListener {

    public static final String APP_TITLE = "Encrypter and Decrypter";
    public static final int HORIZONTAL_MARGIN = 20;
    public static final int VERTICAL_MARGIN = 20;
    public static final String METHOD_AES = "AES";
    public static final String METHOD_BASE64 = "BASE64";

    private static final byte[] keyValue =
            new byte[]{'T', 'h', 'e', 'B', 'e', 's', 't', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y'};


    JLabel methodLabel, inputLabel, outputLabel, historyLabel;
    JComboBox<String> methodCB;
    JTextArea inputField, outputField, historyText;
    JButton encryptButton, decryptButton;

    public EncrypterWindow(){
        configWindow();
        addComponents();
        setListeners();
    }

    private void configWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle(APP_TITLE);
        setSize(440, 500);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(null);
    }

    private void addComponents() {
        // row 1 Method
        methodLabel = new JLabel("Select method");
        methodCB = new JComboBox<>();
        methodCB.addItem("AES");
        methodCB.addItem("BASE64");
        methodLabel.setBounds(HORIZONTAL_MARGIN, VERTICAL_MARGIN, 200, 30);
        methodCB.setBounds(HORIZONTAL_MARGIN + 100, VERTICAL_MARGIN, 80, 30);
        add(methodLabel);
        add(methodCB);

        // row 2 Input
        inputLabel = new JLabel("Input");
        inputField = new JTextArea();
        inputField.setLineWrap(true);
        JScrollPane inputPane = new JScrollPane(inputField);
        inputPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        inputLabel.setBounds(HORIZONTAL_MARGIN, VERTICAL_MARGIN + 50, 200, 30);
        inputPane.setBounds(HORIZONTAL_MARGIN + 100, VERTICAL_MARGIN + 50, 300, 60);
        add(inputLabel);
        add(inputPane);

        // row 3 Output
        outputLabel = new JLabel("Output");
        outputField = new JTextArea();
        outputField.setLineWrap(true);
        outputField.setEditable(false);
        JScrollPane outputPane = new JScrollPane(outputField);
        outputPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        outputLabel.setBounds(HORIZONTAL_MARGIN, VERTICAL_MARGIN + 130, 200, 30);
        outputPane.setBounds(HORIZONTAL_MARGIN + 100, VERTICAL_MARGIN + 130, 300, 60);
        add(outputLabel);
        add(outputPane);

        //row 4 History
        historyLabel = new JLabel("History");
        historyText = new JTextArea();
        historyText.setEditable(false);
        historyText.setLineWrap(true);
        JScrollPane historyPane = new JScrollPane(historyText);
        historyPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        historyLabel.setBounds(HORIZONTAL_MARGIN, VERTICAL_MARGIN + 210, 200, 30);
        historyPane.setBounds(HORIZONTAL_MARGIN + 100, VERTICAL_MARGIN + 210, 300, 120);
        add(historyLabel);
        add(historyPane);

        // row 5 Buttons
        encryptButton = new JButton("ENCRYPT");
        decryptButton = new JButton("DECRYPT");
        encryptButton.setBounds(HORIZONTAL_MARGIN, VERTICAL_MARGIN + 350, 400, 30);
        decryptButton.setBounds(HORIZONTAL_MARGIN, VERTICAL_MARGIN + 400, 400, 30);
        add(encryptButton);
        add(decryptButton);
    }

    private void setListeners() {
        encryptButton.addActionListener(this);
        decryptButton.addActionListener(this);
    }

    private String getSelectedMethod(){
        return (String) methodCB.getSelectedItem();
    }


    private void encrypt(){
        if (inputField.getText().trim().isEmpty()) {
            emptyInput();
            return;
        }

        if (getSelectedMethod().equals(METHOD_BASE64)){
            try {
                String encodedString = Base64.getEncoder().encodeToString(inputField.getText().getBytes());
                outputField.setText(encodedString);
            } catch (Exception e) {
                unableToComplete("Encrypt", e.getMessage());
            }
        }
        else if (getSelectedMethod().equals(METHOD_AES)){
            Key key = new SecretKeySpec(keyValue, "AES");
            try {
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, key);
                byte[] encrypted = cipher.doFinal(inputField.getText().getBytes());
                outputField.setText( new String(Base64.getEncoder().encode(encrypted)));
            } catch (Exception e) {
                unableToComplete("Encrypt", e.getMessage());
            }
        }

        logHistory("Encrypt");
    }

    private void decrypt(){
        if (inputField.getText().trim().isEmpty()) {
            emptyInput();
            return;
        }

        if (getSelectedMethod().equals(METHOD_BASE64)){
            try {
                byte[] decodedString = Base64.getDecoder().decode(inputField.getText());
                outputField.setText(new String(decodedString));
            } catch (Exception e) {
                unableToComplete("Decrypt", e.getMessage());
            }
        }
        else if (getSelectedMethod().equals(METHOD_AES)){
            Key key = new SecretKeySpec(keyValue, "AES");
            try {
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, key);
                byte[] decrypted = cipher.doFinal( Base64.getDecoder().decode(inputField.getText().getBytes()));
                outputField.setText( new String(decrypted));
            } catch (Exception e) {
                unableToComplete("Decrypt", e.getMessage());
            }
        }

        logHistory("Decrypt");
    }

    private void logHistory(String action) {
        String record = "--------" + action + " " + getSelectedMethod() + "--------"  + "\nInput: " + inputField.getText() + "\nOutput: " + outputField.getText() + "\n\n";
        historyText.setText(historyText.getText() + record);
    }

    private void emptyInput(){
        JOptionPane.showMessageDialog(this, "Input field can not be empty", "Enter a message", JOptionPane.ERROR_MESSAGE);
    }

    private void unableToComplete(String action, String message){
        JOptionPane.showMessageDialog(this, "Error message: " + message, "Unable to " + action, JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == encryptButton){
            encrypt();
        }
        else if (e.getSource() == decryptButton){
            decrypt();
        }
    }

    public static void main(String[] args) {
        EncrypterWindow app = new EncrypterWindow();
        app.setVisible(true);
    }
}
