package in.raptor.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public record ClientRequest (
	
	@NotBlank (message = "First name is required")
	@Size (max = 50)
	String firstName,
	
	@Size (max = 50)
	String middleName,
	
	@NotBlank (message = "Last name is required")
	@Size (max = 50)
	String lastName,
	
	@NotBlank (message = "Email is required")
	@Email (message = "Email must be valid")
	@Size (max = 100)
	String email,
	
	@NotBlank (message = "Mobile number is required")
	@Pattern (regexp = "^[0-9]{10}$", message = "Mobile number must be exactly 10 digits")
	String mobileNumber,
	
	@NotBlank(message = "PAN number is required")
	@Pattern (regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "PAN must match format ABCDE1234F")
	String panNumber
		
) {}