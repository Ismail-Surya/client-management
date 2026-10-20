package in.raptor.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.raptor.dto.AuthResponse;
import in.raptor.dto.LoginRequest;
import in.raptor.dto.LogoutRequest;
import in.raptor.dto.RefreshRequest;
import in.raptor.dto.RegisterRequest;
import in.raptor.dto.UserResponse;
import in.raptor.entity.Authority;
import in.raptor.entity.RefreshToken;
import in.raptor.entity.User;
import in.raptor.exception.DuplicateResourceException;
import in.raptor.exception.TokenRefreshException;
import in.raptor.repository.AuthorityRepository;
import in.raptor.repository.UserRepository;
import in.raptor.security.JwtProperties;
import in.raptor.security.JwtService;

@Service
public class AuthService {

	public static final List <String> DEFAULT_REGISTRATION_GROUPS =
			List.of("read", "create", "update", "delete");
	
	private final UserRepository userRepository;
	private final AuthorityRepository authorityRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final JwtProperties jwtProperties;
	private final RefreshTokenService refreshTokenService;
	
	public AuthService(UserRepository userRepository, AuthorityRepository authorityRepository,
			PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService,
			JwtProperties jwtProperties, RefreshTokenService refreshTokenService) {
		this.userRepository = userRepository;
		this.authorityRepository = authorityRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.jwtProperties = jwtProperties;
		this.refreshTokenService = refreshTokenService;
	}
	
	@Transactional
	public AuthResponse register (RegisterRequest request) {
		if (userRepository.existsByUsername(request.username())) {
			throw new DuplicateResourceException("Username is already taken");
		}
		
		if (userRepository.existsByEmail(request.email())) {
			throw new DuplicateResourceException("Email is already taken");
		}
		
		User user = new User();
		
		user.setUsername(request.username());
		user.setEmail(request.email());
		user.setPassword(passwordEncoder.encode(request.password()));
		user.setEnabled(true);
		user.setAuthorities(resolveDefaultGroups());
		
		User saved = userRepository.save(user);
		
		return issueTokens(user);
		
	}
	
	@Transactional
	public AuthResponse login (LoginRequest request) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.usernameOrEmail(), request.password()));
		
		UserDetails principal = (UserDetails) authentication.getPrincipal();
		
		User user = userRepository.findByUsername(principal.getUsername())
				.orElseThrow(() -> new IllegalStateException(
						"Authenticated user not found in DB: " + principal.getUsername()));
		
		return issueTokens(user);
	}
	
	@Transactional
	public void logout (LogoutRequest request) {
		refreshTokenService.revoke(request.refreshToken());
	}
	
	@Transactional
	public AuthResponse refresh (RefreshRequest request) {
		RefreshToken stored = refreshTokenService.findActive(request.refreshToken())
				.orElseThrow(() -> new TokenRefreshException("Refresh token is invalid, expired, or revoked"));
		
		User user = stored.getUser();
		
		String newAccessToken = jwtService.generateAccessToken(user);
		
		return AuthResponse.of(newAccessToken, stored.getToken(), jwtProperties.getAccessExpirationMs(), jwtProperties.getRefreshExpirationMs(), UserResponse.from(user));
	}

	private AuthResponse issueTokens(User user) {
		String accessToken = jwtService.generateAccessToken(user);
		
		RefreshToken refreshToken = refreshTokenService.issueForUser(user);
		
		return AuthResponse.of(accessToken, refreshToken.getToken(), jwtProperties.getAccessExpirationMs(), jwtProperties.getRefreshExpirationMs(), UserResponse.from(user));
	}

	private Set<Authority> resolveDefaultGroups() {
		Set <Authority> authorities = new HashSet<>();
		
		for (String name: DEFAULT_REGISTRATION_GROUPS) {
			Authority authority = authorityRepository.findByName(name)
					.orElseThrow(() -> new IllegalStateException(
							"Required authority is missing in DB: " + name
							+ ". Is DataInitializer running?"));
			
			authorities.add(authority);
		}
		
		return authorities;
	}
	
}











