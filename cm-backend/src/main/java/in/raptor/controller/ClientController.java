package in.raptor.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.raptor.dto.ClientRequest;
import in.raptor.dto.ClientResponse;
import in.raptor.service.ClientService;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

	private final ClientService clientService;

	public ClientController(ClientService clientService) {
		this.clientService = clientService;
	}
	
	@GetMapping
	public ResponseEntity<List<ClientResponse>> list() {
		return ResponseEntity.ok(clientService.findAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ClientResponse> get(@PathVariable Long id) {
		return ResponseEntity.ok(clientService.findById(id));
	}
	
	@PostMapping
	public ResponseEntity<ClientResponse> create (@Valid @RequestBody ClientRequest request) {
		ClientResponse created = clientService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<ClientResponse> update (@PathVariable Long id,
								@Valid @RequestBody ClientRequest request) {
		return ResponseEntity.ok(clientService.update(id, request));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete (@PathVariable Long id) {
		clientService.delete(id);
		
		return ResponseEntity.noContent().build();
	}
	
}











