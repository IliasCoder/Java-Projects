package LMS.gui;

import LMS.model.Book;
import LMS.model.User;
import LMS.service.libraryManager;

public class LibraryGUITest {
    public static void main(String[] args) {
        // Create a test manager with sample data
        libraryManager manager = new libraryManager();
        
        // Add sample books
        manager.addBook(new Book(1L, "The Great Gatsby", "F. Scott Fitzgerald", 9780743273565L, false));
        manager.addBook(new Book(2L, "To Kill a Mockingbird", "Harper Lee", 9780061120084L, false));
        manager.addBook(new Book(3L, "1984", "George Orwell", 9780451524935L, true));
        manager.addBook(new Book(4L, "Pride and Prejudice", "Jane Austen", 9780141439518L, false));
        
        // Add sample users
        manager.addUser(new User(1L, "John Doe", "john.doe@email.com", "student", true));
        manager.addUser(new User(2L, "Jane Smith", "jane.smith@email.com", "admin", true));
        manager.addUser(new User(3L, "Bob Johnson", "bob.johnson@email.com", "student", true));
        
        // Launch GUI with sample data
        LibraryGUI gui = new LibraryGUI();
        gui.setVisible(true);
    }
}

