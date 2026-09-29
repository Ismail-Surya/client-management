package in.raptor.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import in.raptor.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional <User> findByUsername (String username);
	
	Optional <User> findByEmail (String email);
	
	Optional <User> findByUsernameOrEmail (String username, String email);
	
	Boolean existsByUsername (String username);
	
	Boolean existsByEmail (String email);
	
}
