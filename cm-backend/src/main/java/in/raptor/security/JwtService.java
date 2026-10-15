package in.raptor.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import in.raptor.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private static final String CLAIMS_GROUPS = "groups";
	
	private final JwtProperties jwtProperties;
	
	private final SecretKey signingKey;

	public JwtService(JwtProperties jwtProperties) {
		this.jwtProperties = jwtProperties;
		this.signingKey = Keys.hmacShaKeyFor(
				jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
		);
	}
	
	public String generateAccessToken (User user) {
		Date now = new Date();
		
		Date expiry = new Date(now.getTime() + jwtProperties.getAccessExpirationMs());
		
		List<String> groups = user.getAuthorities().stream()
				.map(a -> a.getName())
				.collect(Collectors.toList());
		
		return Jwts.builder()
				.setSubject(user.getUsername())
				.setIssuer(jwtProperties.getIssuer())
				.setIssuedAt(now)
				.setExpiration(expiry)
				.claim(CLAIMS_GROUPS, groups)
				.signWith(signingKey, SignatureAlgorithm.HS256)
				.compact();
	}
	
	public Claims parse (String token) {
		return Jwts.parserBuilder()
				.setSigningKey(signingKey)
				.requireIssuer(jwtProperties.getIssuer())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}
	
	public boolean isValid (String token) {
		try {
			parse (token);
			return true;
		} catch (ExpiredJwtException exc) {
			return false;
		} catch (JwtException | IllegalArgumentException exc) {
			return false;
		}
	}
	
	public String extractUsername (String token) {
		return parse (token).getSubject();
	}
	
	@SuppressWarnings("unchecked")
	public Set <String> extractGroups (String token) {
		Object raw = parse (token).get(CLAIMS_GROUPS);
		
		if (raw instanceof List <?>) {
			return ((List<?>) raw).stream()
					.filter(String.class::isInstance)
					.map(String.class::cast)
					.collect(Collectors.toSet());
		}
		
		return Set.of();
	}
	
}
