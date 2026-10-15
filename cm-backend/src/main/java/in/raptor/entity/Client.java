package in.raptor.entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name = "clients")
public class Client {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	@Size(max = 50)
	@Column(nullable = false, length = 50)
	private String firstName;
	
	@Size(max = 50)
	@Column(length = 50)
	private String middleName;
	
	@NotBlank
	@Size(max = 50)
	@Column(nullable = false, length = 50)
	private String lastName;
	
	@NotBlank
	@Email
	@Size(max = 50)
	@Column(nullable = false, length = 100)
	private String email;
	
	@NotBlank
	@Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
	@Column(nullable = false, length = 10)
	private String mobileNumber;

	@NotBlank
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "PAN must match format ABCDE1234F")
	@Column(nullable = false, length = 10)
	private String panNumber;
	
	public Client() {
	}

	public Client(String firstName, String middleName,
			String lastName, String email,
			String mobileNumber,
			String panNumber) {
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.email = email;
		this.mobileNumber = mobileNumber;
		this.panNumber = panNumber;
	}

	public Client(Long id, String firstName, String middleName,
			String lastName, String email,
			String mobileNumber,
			String panNumber) {
		this.id = id;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.email = email;
		this.mobileNumber = mobileNumber;
		this.panNumber = panNumber;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}
	
	@Override
	public boolean equals (Object o) {
		if (this == o) return true;
		
		if (!(o instanceof Client)) return false;
		
		Client client = (Client) o;
		
		return id != null && id.equals(client.id);
	}
	
	@Override
	public int hashCode () {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "Client [id=" + id + ", firstName=" + firstName + ", middleName=" + middleName + ", lastName=" + lastName
				+ ", email=" + email + ", mobileNumber=" + mobileNumber + ", panNumber=" + panNumber + "]";
	}
	
	
}
