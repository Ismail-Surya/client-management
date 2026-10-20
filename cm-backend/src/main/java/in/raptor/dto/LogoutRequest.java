package in.raptor.dto;

import javax.validation.constraints.NotBlank;

public record LogoutRequest(
	
	@NotBlank(message = "Refresh token is required")
	String refreshToken
		
) {}