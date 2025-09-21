package LMS.service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import LMS.model.User;
import LMS.model.Book;



public class libraryManager {
	private List<Book> books;
	private List<User> users;
	
	public libraryManager() {
		this.books = new ArrayList<>();
		this.users = new ArrayList<>();
	}
	//Core operations
	public void addBook(Book book) {
		books.add(book);
		System.out.println("Book added Succesfully: "+ book.getTitle());
	}
	public void updateBook(long id, String title, String author) {
		books.stream().filter(book->book.getId() ==id).findFirst().ifPresentOrElse(book->{
			book.setTitle(title);
			book.setAuthor(author);
			System.out.println("Book updated");
		},()->System.out.println("Book not Found!"));
		
	}
	public void deleteBook(long id) {
		if(books.removeIf(book->book.getId()==id)) {
			System.out.println("Book deleted succesfully!");
			
		}else {
			System.out.println("book not found");
		}
	}
	public void listBooks() {
		if(books.isEmpty()) {
			System.out.println("No books found!");
		}else {
			//a better way to print each book
			//it is the same as saying call Sys.out.println() for each element i.e book
			books.forEach(System.out::println);
		}
		
	}
	public void searchBooks(String query) {
		List<Book> foundBooks = books.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(query.toLowerCase())
                             || book.getAuthor().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());

        if (foundBooks.isEmpty()) {
            System.out.println("No books found matching the query.");
        } else {
            foundBooks.forEach(System.out::println);
        }

	}
	public void checkOutBook(long id) {
        books.stream()
             .filter(book -> book.getId() == id && !book.getIsBorrowed())
             .findFirst()
             .ifPresentOrElse(book -> {
                 book.setIsBorrowed(true);
                 System.out.println("Book checked out successfully!");
             }, () -> System.out.println("Book is not available or already borrowed."));
    }

    public void checkInBook(long id) {
        books.stream()
             .filter(book -> book.getId() == id && book.getIsBorrowed())
             .findFirst()
             .ifPresentOrElse(book -> {
                 book.setIsBorrowed(false);
                 System.out.println("Book returned successfully!");
             }, () -> System.out.println("Book not found or was not borrowed."));
    }

	// User Core operations
    public void addUser(User user) {
    	users.add(user);
    	System.out.println("User added successfully");
    }
    public void deactivateUser(long id) {
    	users.stream().filter(user->user.getId()==id).findFirst().ifPresentOrElse(user->{user.setIsActive(false);
    	System.out.println("User deactivated successfully");},()->System.out.println("User not found!"));
    }
    public void listUsers() {
    	if(users.isEmpty()) {
    		System.out.println("No users found!");
    	}else {
    		users.forEach(System.out::println);
    	}
    }
	

}
