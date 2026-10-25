package in.raptor.dto;

import in.raptor.entity.Client;

public record ClientResponse (
	
	Long id,
	String firstName,
	String middleName,
	String lastName,
	String email,
	String mobileNumber,
	String panNumber
		
) {
	
	public static ClientResponse from (Client client) {
		return new ClientResponse(client.getId(), client.getFirstName(), client.getMiddleName(),
				client.getLastName(), client.getEmail(), client.getMobileNumber(), client.getPanNumber());
	}
	
}