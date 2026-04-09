import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.swing.*;
import java.awt.*;

public class Client extends JFrame 
{
    private ServerFunction service;
    private String currentUser = null;

    // GUI components
    private CardLayout cardLayout;
    private JPanel mainPanel;

    private JTextField loginUserField, regUserField;
    private JPasswordField loginPassField, regPassField, regConfirmPassField;
    private JLabel statusLabel;

    public Client(String host) 
    {
        try 
        {
        	Registry registry = LocateRegistry.getRegistry(host);
            service = (ServerFunction) registry.lookup("Server");
        } 
        catch (Exception e) 
        {
            JOptionPane.showMessageDialog(this, "Cannot connect to server: " + e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        setTitle("Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        createLoginPanel();
        createRegisterPanel();
        createMainPanel();

        add(mainPanel);
        cardLayout.show(mainPanel, "Login");
    }

    private void createLoginPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Username:"));
        loginUserField = new JTextField();
        panel.add(loginUserField);

        panel.add(new JLabel("Password:"));
        loginPassField = new JPasswordField();
        panel.add(loginPassField);

        JButton loginBtn = new JButton("Login");
        JButton regBtn = new JButton("Register");

        loginBtn.addActionListener(e -> attemptLogin());
        regBtn.addActionListener(e -> {
        	setTitle("Register");
        	cardLayout.show(mainPanel, "Register");
        	});

        panel.add(loginBtn);
        panel.add(regBtn);

        statusLabel = new JLabel(" ", JLabel.CENTER);
        panel.add(statusLabel);

        mainPanel.add(panel, "Login");
    }

    private void createRegisterPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Username:"));
        regUserField = new JTextField();
        panel.add(regUserField);

        panel.add(new JLabel("Password:"));
        regPassField = new JPasswordField();
        panel.add(regPassField);

        panel.add(new JLabel("Confirm Password:"));
        regConfirmPassField = new JPasswordField();
        panel.add(regConfirmPassField);

        JButton regBtn = new JButton("Register");
        JButton backBtn = new JButton("Cancel");

        regBtn.addActionListener(e -> attemptRegister());
        backBtn.addActionListener(e -> {
        	setTitle("Login");
        	cardLayout.show(mainPanel, "Login");
        });

        panel.add(regBtn);
        panel.add(backBtn);

        mainPanel.add(panel, "Register");
    }

    private void createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> attemptLogout());

        panel.add(welcomeLabel, BorderLayout.CENTER);
        panel.add(logoutBtn, BorderLayout.SOUTH);

        mainPanel.add(panel, "Main");
    }

    private void attemptLogin() 
    {
        String user = loginUserField.getText().trim();
        String pass = new String(loginPassField.getPassword());

        try 
        {
            if (service.login(user, pass)) 
            {
                currentUser = user;
                statusLabel.setText("Login successful!");
                setTitle("JPoker 24-Game");
                cardLayout.show(mainPanel, "Main");
            } 
            else 
            {
                JOptionPane.showMessageDialog(this, "Invalid username or password / already logged in","Error",JOptionPane.ERROR_MESSAGE);
            }
        } 
        catch (Exception ex) 
        {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void attemptRegister() 
    {
        String user = regUserField.getText().trim();
        String pass = new String(regPassField.getPassword());
        String confirm = new String(regConfirmPassField.getPassword());
        
        if(user.isEmpty()) 
        {
        	 JOptionPane.showMessageDialog(this, "Username cannot be empty!","Error",JOptionPane.ERROR_MESSAGE);
             return;
        }

        if (!pass.equals(confirm)) 
        {
            JOptionPane.showMessageDialog(this, "Passwords do not match!","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }

        try 
        {
            if (service.register(user, pass)) 
            {
                currentUser = user;
                JOptionPane.showMessageDialog(this, "Registration successful! Auto logged in.");
                setTitle("JPoker 24-Game");
                cardLayout.show(mainPanel, "Main");
            } 
            else 
            {
                JOptionPane.showMessageDialog(this, "Username already exists!","Error",JOptionPane.ERROR_MESSAGE);
            }
        } 
        catch (Exception ex) 
        {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void attemptLogout() {
        try 
        {
            if (service.logout(currentUser)) {
                currentUser = null;
                setTitle("Login");
                cardLayout.show(mainPanel, "Login");
                loginUserField.setText("");
                loginPassField.setText("");
                statusLabel.setText("");
            }
        } 
        catch (Exception ex) 
        {
            JOptionPane.showMessageDialog(this, "Logout error: " + ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Client(args[0]).setVisible(true));
    }
}