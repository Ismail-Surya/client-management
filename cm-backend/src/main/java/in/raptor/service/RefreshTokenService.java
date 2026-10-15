package in.raptor.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.raptor.entity.RefreshToken;
import in.raptor.entity.User;
import in.raptor.repository.RefreshTokenRepository;
import in.raptor.security.JwtProperties;

@Service
public class RefreshTokenService {

	private static final int TOKEN_BYTES = 64;
	
	private final RefreshTokenRepository refreshTokenRepository;
	
	private final JwtProperties jwtProperties;
	
	private final SecureRandom secureRandom = new SecureRandom();

	public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtProperties jwtProperties) {
		this.refreshTokenRepository = refreshTokenRepository;
		this.jwtProperties = jwtProperties;
	}
	
	@Transactional
	public RefreshToken issueForUser (User user) {
		String rawToken = generateRawToken();
		Instant expiry = Instant.now()
				.plusMillis(jwtProperties.getRefreshExpirationMs());
		
		RefreshToken entity = new RefreshToken(rawToken, user, expiry);
		return refreshTokenRepository.save(entity);
	}
	
	@Transactional
	public Optional <RefreshToken> findActive (String token) {
		return refreshTokenRepository.findByToken(token)
				.filter(RefreshToken::isActive);
	}
	
	@Transactional
	public void revoke (String token) {
		refreshTokenRepository.findByToken(token).ifPresent(rt -> {
			rt.setRevoked(true);
			refreshTokenRepository.save(rt);
		});
	}
	
	@Transactional
	public void revokeAllForUser (User user) {
		refreshTokenRepository.deleteAllByUser(user);
	}
	
	@Transactional
	public int purgeExpired () {
		var now = Instant.now();
		
		var expired = refreshTokenRepository.findAll().stream()
				.filter(rt -> rt.getExpiryDate().isBefore(now))
				.toList();
		
		expired.forEach(refreshTokenRepository::delete);
		return expired.size();
	}

	private String generateRawToken() {
		byte[] bytes = new byte[TOKEN_BYTES];
		secureRandom.nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}
	
}

















