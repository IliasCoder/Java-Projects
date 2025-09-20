package LMS.model;

import java.util.Objects;

public class User {
	//Admin or student
	private long id;
	private String name;
	private String email;
	private boolean isActive;
	private String role; //student or admin
	
	//Constructor
	public User(long id, String name, String email, String role, boolean isActive) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.role = role;
		this.isActive = isActive;
	}
	//Getters and setters
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return this.email;
	}
	public void setEmail(String email) {
		if(email == null || !email.contains("@")) {
			throw new IllegalArgumentException("Invalid email address");
			
		}
		this.email = email;
	}
	public String getRole() {
		return this.role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public boolean getIsActive() {
		return this.isActive;
	}
	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}
	//Copy constructor
	public User(User other) {
		this.id = other.id;
		this.email = other.email;
		this.name = other.name;
		this.isActive = other.isActive;
	}
	@Override
	public String toString() {
		return String.format("User[id=%d,name=%s,email=%s,role=%s,active=%b");
	}
	@Override
	public boolean equals(Object o) {
		if(this==o) return true;
		if(!(o instanceof User)) return false;
		User user = (User) o;
		return id == user.id && isActive == user.isActive && Objects.equals(name, user.name)
				&& Objects.equals(email, user.email) && Objects.equals(role, user.role);
		
	}
	@Override
	public int hashCode() {
		return Objects.hash(id,name,email,role,isActive);
	}









}
