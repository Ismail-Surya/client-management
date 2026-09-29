package in.raptor.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import in.raptor.entity.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

	Optional <Authority> findByName(String name);
	
}
