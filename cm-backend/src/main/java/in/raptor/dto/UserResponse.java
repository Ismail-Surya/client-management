package in.raptor.dto;

import java.util.Set;
import java.util.stream.Collectors;

import in.raptor.entity.User;

public record UserResponse(
	
	Long id,
	String username,
	String email,
	Set <String> groups
		
) {
	
	public static UserResponse from (User user) {
		Set <String> groups = user.getAuthorities().stream()
				.map(a -> a.getName().toLowerCase())
				.collect(Collectors.toSet());
		
		return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), groups);
	}
	
}






