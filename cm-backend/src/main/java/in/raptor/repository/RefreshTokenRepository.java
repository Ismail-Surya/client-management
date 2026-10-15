package in.raptor.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import in.raptor.entity.RefreshToken;
import in.raptor.entity.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	Optional <RefreshToken> findByToken (String token);
	
	List <RefreshToken> findAllByUser (User user);
	
	void deleteAllByUser (User user);
	
	void deleteAllByExpiryDateBefore(Instant cutoff);
	
}
