package in.raptor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.raptor.entity.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {

	Boolean existsByEmail (String email);
	
	Boolean existsByPanNumber (String panNumber);
	
	Boolean existsByEmailAndIdNot (String email, Long id);
	
	Boolean existsByPanNumberAndIdNot (String panNumber, Long id);
	
}
