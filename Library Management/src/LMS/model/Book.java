package LMS.model;

import java.util.Objects;

public class Book {

	private long id;
	private String title;
	private String author;
	private long isbn;
	private boolean isBorrowed;
	public Book(long id, String title, String author,long isbn, boolean isBorrowed) {
		this.id = id;
		this.title = title;
		this.author = author;
		this.isbn = isbn;
		this.isBorrowed = isBorrowed;
		
		
	}
	//Getters and Setters
	public long getId() {
		return id;
	}
	public  void setId(long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public long getIsbn() {
		return isbn;
	}
	public void setIsbn(long isbn) {
		this.isbn = isbn;
	}
	public boolean getIsBorrowed()
	{
		return isBorrowed;
	}
	public void setIsBorrowed(boolean isBorrowed) {
		this.isBorrowed = isBorrowed;
	}
	// A copy constructor is useful when cloning or duplicating a Book object
		public Book(Book other) {
			this.id =  other.id;
			this.title = other.title;
			this.isbn = other.isbn;
			this.isBorrowed = other.isBorrowed;
		}

	
	//toString
	@Override
	public String toString() {
		return String.format("Book[id=%d, title='%s', author='%s', isbn=%d, borrowed=%b]",id, title,author,isbn,isBorrowed);
	}
	//.equals()
	@Override
	public boolean equals(Object o) {
		if(this ==o) return true;
		if(!(o instanceof Book)) return false;
		Book book = (Book) o;
		return id == book.id && title.equals(book.title) && author.equals(book.author);
		
	}
	@Override
	public int hashCode() {
		return Objects.hash(id,title,author,isbn,isBorrowed);
	}
	
}
