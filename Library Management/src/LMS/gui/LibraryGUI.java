package LMS.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import LMS.service.libraryManager;
import LMS.model.Book;
import LMS.model.User;

public class LibraryGUI extends JFrame {
    private libraryManager manager;
    private JTabbedPane tabbedPane;
    
    // Book management components
    private JTable bookTable;
    private BookTableModel bookTableModel;
    private JTextField bookTitleField, bookAuthorField, bookIsbnField;
    private JTextField searchField;
    
    // User management components
    private JTable userTable;
    private UserTableModel userTableModel;
    private JTextField userNameField, userEmailField, userRoleField;
    
    public LibraryGUI() {
        manager = new libraryManager();
        initializeGUI();
        setupEventHandlers();
    }
    
    private void initializeGUI() {
        setTitle("Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Create main layout
        setLayout(new BorderLayout());
        
        // Create menu bar
        createMenuBar();
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Add tabs
        createBookManagementTab();
        createUserManagementTab();
        createCheckoutTab();
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }
    
    private void createBookManagementTab() {
        JPanel bookPanel = new JPanel(new BorderLayout());
        
        // Book table
        bookTableModel = new BookTableModel(manager);
        bookTable = new JTable(bookTableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane bookScrollPane = new JScrollPane(bookTable);
        bookScrollPane.setPreferredSize(new Dimension(600, 300));
        
        // Book input panel
        JPanel bookInputPanel = createBookInputPanel();
        
        // Search panel
        JPanel searchPanel = createSearchPanel();
        
        // Button panel
        JPanel buttonPanel = createBookButtonPanel();
        
        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(bookInputPanel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        bookPanel.add(topPanel, BorderLayout.NORTH);
        bookPanel.add(bookScrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("Book Management", bookPanel);
    }
    
    private JPanel createBookInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        panel.setBorder(BorderFactory.createTitledBorder("Add/Update Book"));
        
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title field
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        bookTitleField = new JTextField(20);
        panel.add(bookTitleField, gbc);
        
        // Author field
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1;
        bookAuthorField = new JTextField(20);
        panel.add(bookAuthorField, gbc);
        
        // ISBN field
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1;
        bookIsbnField = new JTextField(20);
        panel.add(bookIsbnField, gbc);
        
        return panel;
    }
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Search Books"));
        
        panel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        panel.add(searchField);
        
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchBooks());
        panel.add(searchButton);
        
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> {
            searchField.setText("");
            refreshBookTable();
        });
        panel.add(clearButton);
        
        return panel;
    }
    
    private JPanel createBookButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton addButton = new JButton("Add Book");
        addButton.addActionListener(e -> addBook());
        panel.add(addButton);
        
        JButton updateButton = new JButton("Update Book");
        updateButton.addActionListener(e -> updateBook());
        panel.add(updateButton);
        
        JButton deleteButton = new JButton("Delete Book");
        deleteButton.addActionListener(e -> deleteBook());
        panel.add(deleteButton);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshBookTable());
        panel.add(refreshButton);
        
        return panel;
    }
    
    private void createUserManagementTab() {
        JPanel userPanel = new JPanel(new BorderLayout());
        
        // User table
        userTableModel = new UserTableModel(manager);
        userTable = new JTable(userTableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane userScrollPane = new JScrollPane(userTable);
        userScrollPane.setPreferredSize(new Dimension(600, 300));
        
        // User input panel
        JPanel userInputPanel = createUserInputPanel();
        
        // User button panel
        JPanel userButtonPanel = createUserButtonPanel();
        
        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(userInputPanel, BorderLayout.NORTH);
        topPanel.add(userButtonPanel, BorderLayout.SOUTH);
        
        userPanel.add(topPanel, BorderLayout.NORTH);
        userPanel.add(userScrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("User Management", userPanel);
    }
    
    private JPanel createUserInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        panel.setBorder(BorderFactory.createTitledBorder("Add User"));
        
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Name field
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        userNameField = new JTextField(20);
        panel.add(userNameField, gbc);
        
        // Email field
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        userEmailField = new JTextField(20);
        panel.add(userEmailField, gbc);
        
        // Role field
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        userRoleField = new JTextField(20);
        panel.add(userRoleField, gbc);
        
        return panel;
    }
    
    private JPanel createUserButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton addUserButton = new JButton("Add User");
        addUserButton.addActionListener(e -> addUser());
        panel.add(addUserButton);
        
        JButton deactivateButton = new JButton("Deactivate User");
        deactivateButton.addActionListener(e -> deactivateUser());
        panel.add(deactivateButton);
        
        JButton refreshUserButton = new JButton("Refresh");
        refreshUserButton.addActionListener(e -> refreshUserTable());
        panel.add(refreshUserButton);
        
        return panel;
    }
    
    private void createCheckoutTab() {
        JPanel checkoutPanel = new JPanel(new BorderLayout());
        
        // Checkout/Return panel
        JPanel checkoutInputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        checkoutInputPanel.setBorder(BorderFactory.createTitledBorder("Book Checkout/Return"));
        
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        JLabel bookIdLabel = new JLabel("Book ID:");
        gbc.gridx = 0; gbc.gridy = 0;
        checkoutInputPanel.add(bookIdLabel, gbc);
        
        JTextField bookIdField = new JTextField(10);
        gbc.gridx = 1;
        checkoutInputPanel.add(bookIdField, gbc);
        
        JButton checkoutButton = new JButton("Check Out");
        checkoutButton.addActionListener(e -> {
            try {
                long bookId = Long.parseLong(bookIdField.getText());
                manager.checkOutBook(bookId);
                JOptionPane.showMessageDialog(this, "Book checked out successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshBookTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid book ID", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        gbc.gridx = 2;
        checkoutInputPanel.add(checkoutButton, gbc);
        
        JButton returnButton = new JButton("Return");
        returnButton.addActionListener(e -> {
            try {
                long bookId = Long.parseLong(bookIdField.getText());
                manager.checkInBook(bookId);
                JOptionPane.showMessageDialog(this, "Book returned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshBookTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid book ID", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        gbc.gridx = 3;
        checkoutInputPanel.add(returnButton, gbc);
        
        // Available books table
        JTable availableBooksTable = new JTable(bookTableModel);
        JScrollPane availableBooksScrollPane = new JScrollPane(availableBooksTable);
        availableBooksScrollPane.setPreferredSize(new Dimension(600, 300));
        
        checkoutPanel.add(checkoutInputPanel, BorderLayout.NORTH);
        checkoutPanel.add(availableBooksScrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("Checkout/Return", checkoutPanel);
    }
    
    private void setupEventHandlers() {
        // Add double-click handler for book table
        bookTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int selectedRow = bookTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        Book book = bookTableModel.getBookAt(selectedRow);
                        bookTitleField.setText(book.getTitle());
                        bookAuthorField.setText(book.getAuthor());
                        bookIsbnField.setText(String.valueOf(book.getIsbn()));
                    }
                }
            }
        });
    }
    
    // Book management methods
    private void addBook() {
        try {
            String title = bookTitleField.getText().trim();
            String author = bookAuthorField.getText().trim();
            long isbn = Long.parseLong(bookIsbnField.getText().trim());
            
            if (title.isEmpty() || author.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            long id = System.currentTimeMillis(); // Simple ID generation
            Book book = new Book(id, title, author, isbn, false);
            manager.addBook(book);
            
            clearBookFields();
            refreshBookTable();
            JOptionPane.showMessageDialog(this, "Book added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid ISBN number", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a book to update", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            Book selectedBook = bookTableModel.getBookAt(selectedRow);
            String title = bookTitleField.getText().trim();
            String author = bookAuthorField.getText().trim();
            
            if (title.isEmpty() || author.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            manager.updateBook(selectedBook.getId(), title, author);
            clearBookFields();
            refreshBookTable();
            JOptionPane.showMessageDialog(this, "Book updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a book to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this book?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            Book selectedBook = bookTableModel.getBookAt(selectedRow);
            manager.deleteBook(selectedBook.getId());
            clearBookFields();
            refreshBookTable();
        }
    }
    
    private void searchBooks() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            refreshBookTable();
            return;
        }
        
        // For now, we'll refresh the table and let the user see all books
        // In a real implementation, you'd want to filter the table model
        refreshBookTable();
        JOptionPane.showMessageDialog(this, "Search functionality - showing all books. Filter by: " + query, "Search", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void clearBookFields() {
        bookTitleField.setText("");
        bookAuthorField.setText("");
        bookIsbnField.setText("");
    }
    
    private void refreshBookTable() {
        bookTableModel.refreshData();
    }
    
    // User management methods
    private void addUser() {
        try {
            String name = userNameField.getText().trim();
            String email = userEmailField.getText().trim();
            String role = userRoleField.getText().trim();
            
            if (name.isEmpty() || email.isEmpty() || role.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            long id = System.currentTimeMillis(); // Simple ID generation
            User user = new User(id, name, email, role, true);
            manager.addUser(user);
            
            clearUserFields();
            refreshUserTable();
            JOptionPane.showMessageDialog(this, "User added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Invalid email address", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deactivateUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to deactivate", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to deactivate this user?", "Confirm Deactivate", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            User selectedUser = userTableModel.getUserAt(selectedRow);
            manager.deactivateUser(selectedUser.getId());
            refreshUserTable();
        }
    }
    
    private void clearUserFields() {
        userNameField.setText("");
        userEmailField.setText("");
        userRoleField.setText("");
    }
    
    private void refreshUserTable() {
        userTableModel.refreshData();
    }
    
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this, 
            "Library Management System\nVersion 1.0\n\nA comprehensive library management solution.", 
            "About", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LibraryGUI().setVisible(true);
        });
    }
}
