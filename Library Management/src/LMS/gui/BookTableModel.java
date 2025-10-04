package LMS.gui;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import LMS.model.Book;
import LMS.service.libraryManager;

public class BookTableModel extends AbstractTableModel {
    private List<Book> books;
    private libraryManager manager;
    private String[] columnNames = {"ID", "Title", "Author", "ISBN", "Available"};
    
    public BookTableModel() {
        this.manager = new libraryManager();
        this.books = new ArrayList<>();
        refreshData();
    }
    
    public BookTableModel(libraryManager manager) {
        this.manager = manager;
        this.books = new ArrayList<>();
        refreshData();
    }
    
    @Override
    public int getRowCount() {
        return books.size();
    }
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex >= books.size()) return null;
        
        Book book = books.get(rowIndex);
        switch (columnIndex) {
            case 0: return book.getId();
            case 1: return book.getTitle();
            case 2: return book.getAuthor();
            case 3: return book.getIsbn();
            case 4: return book.getIsBorrowed() ? "No" : "Yes";
            default: return null;
        }
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0: return Long.class;
            case 1: return String.class;
            case 2: return String.class;
            case 3: return Long.class;
            case 4: return String.class;
            default: return Object.class;
        }
    }
    
    public Book getBookAt(int row) {
        if (row >= 0 && row < books.size()) {
            return books.get(row);
        }
        return null;
    }
    
    public void refreshData() {
        books.clear();
        books.addAll(manager.getBooks());
        fireTableDataChanged();
    }
    
    public void setBooks(List<Book> books) {
        this.books = new ArrayList<>(books);
        fireTableDataChanged();
    }
}
