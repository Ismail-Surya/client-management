package in.raptor.dto;

public record AuthResponse(
		
	String accessToken,
	String refreshToken,
	String tokenType,
	long accessExpireInMs,
	long refreshExpireInMs,
	UserResponse user

) {

	public static AuthResponse of (String accessToken,
									String refreshToken,
									long accessExpireInMs,
									long refreshExpireInMs,
									UserResponse user) {
		return new AuthResponse(accessToken, refreshToken, "Bearer", accessExpireInMs, refreshExpireInMs, user);
	}
	
}
