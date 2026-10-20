package in.raptor.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record RegisterRequest (
	
	@NotBlank(message = "Username is required")
	@Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
	String username,
	
	@NotBlank(message = "Email is required")
	@Email(message = "Email must be valid")
	@Size(max = 100)
	String email,
	
	@NotBlank(message = "Password is required")
	@Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
	String password
	
) {}