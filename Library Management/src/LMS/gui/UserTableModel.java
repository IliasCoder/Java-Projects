package LMS.gui;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import LMS.model.User;
import LMS.service.libraryManager;

public class UserTableModel extends AbstractTableModel {
    private List<User> users;
    private libraryManager manager;
    private String[] columnNames = {"ID", "Name", "Email", "Role", "Active"};
    
    public UserTableModel() {
        this.manager = new libraryManager();
        this.users = new ArrayList<>();
        refreshData();
    }
    
    public UserTableModel(libraryManager manager) {
        this.manager = manager;
        this.users = new ArrayList<>();
        refreshData();
    }
    
    @Override
    public int getRowCount() {
        return users.size();
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
        if (rowIndex >= users.size()) return null;
        
        User user = users.get(rowIndex);
        switch (columnIndex) {
            case 0: return user.getId();
            case 1: return user.getName();
            case 2: return user.getEmail();
            case 3: return user.getRole();
            case 4: return user.getIsActive() ? "Yes" : "No";
            default: return null;
        }
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0: return Long.class;
            case 1: return String.class;
            case 2: return String.class;
            case 3: return String.class;
            case 4: return String.class;
            default: return Object.class;
        }
    }
    
    public User getUserAt(int row) {
        if (row >= 0 && row < users.size()) {
            return users.get(row);
        }
        return null;
    }
    
    public void refreshData() {
        users.clear();
        users.addAll(manager.getUsers());
        fireTableDataChanged();
    }
    
    public void setUsers(List<User> users) {
        this.users = new ArrayList<>(users);
        fireTableDataChanged();
    }
}
