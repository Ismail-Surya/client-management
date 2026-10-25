package in.raptor.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import in.raptor.dto.ClientRequest;
import in.raptor.dto.ClientResponse;
import in.raptor.entity.Client;
import in.raptor.exception.DuplicateResourceException;
import in.raptor.exception.ResourceNotFoundException;
import in.raptor.repository.ClientRepository;

@Service
public class ClientService {

	private final ClientRepository clientRepository;

	public ClientService(ClientRepository clientRepository) {
		this.clientRepository = clientRepository;
	}
	
	@PreAuthorize("hasAuthority('GROUP_READ')")
	@Transactional
	public List <ClientResponse> findAll () {
		return clientRepository.findAll().stream()
				.map(ClientResponse::from)
				.toList();
	}
	
	@PreAuthorize("hasAuthority('GROUP_READ')")
	@Transactional
	public ClientResponse findById (Long id) {
		Client client = clientRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Client not found: " + id));
		
		return ClientResponse.from(client);
	}
	
	@PreAuthorize("hasAuthority('GROUP_CREATE')")
	@Transactional
	public ClientResponse create (ClientRequest request) {
		if (clientRepository.existsByEmail(request.email())) {
			throw new DuplicateResourceException("A client with this email already exists: " + request.email());
		}
		
		if (clientRepository.existsByPanNumber(request.panNumber())) {
			throw new DuplicateResourceException("A client with this PAN already exists: " + request.panNumber());
		}
		
		Client client = new Client();
		applyRequest(client, request);
		
		Client saved = clientRepository.save(client);
		
		return ClientResponse.from(saved);
	}
	
	@PreAuthorize("hasAuthority('GROUP_UPDATE')")
	@Transactional
	public ClientResponse update (Long id, ClientRequest request) {
		Client client = clientRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Client not found: " + id));
		
		if (clientRepository.existsByEmailAndIdNot(request.email(), id)) {
			throw new DuplicateResourceException(
					"Another client already uses this email: " + request.email()
					);
		}
		
		if (clientRepository.existsByPanNumberAndIdNot(request.panNumber(), id)) {
			throw new DuplicateResourceException(
					"Another client already uses this PAN: " + request.panNumber()
					);
		}
		
		applyRequest(client, request);
		
		Client saved = clientRepository.save(client);
		
		return ClientResponse.from(client);
	}
	
	@PreAuthorize("hasAuthority('GROUP_DELETE')")
	public void delete (Long id) {
		Client client = clientRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Client not found: " + id));
		
		clientRepository.delete(client);
	}

	private void applyRequest(Client client, ClientRequest request) {
		client.setFirstName(request.firstName());
		client.setMiddleName(request.middleName());
		client.setLastName(request.lastName());
		client.setEmail(request.email());
		client.setMobileNumber(request.mobileNumber());
		client.setPanNumber(request.panNumber());
	}
	
}










