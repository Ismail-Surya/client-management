package in.raptor.security;

import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.raptor.entity.User;
import in.raptor.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;
	
	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
		
		User user = loadDomainUser(usernameOrEmail);
		
		Set <GrantedAuthority> authorities = user.toGrantedAuthorities();
		
		return org.springframework.security.core.userdetails.User.builder()
				.username(user.getUsername())
				.password(user.getPassword())
				.disabled(!user.isEnabled())
				.authorities(authorities)
				.build();
	}
	
	@Transactional(readOnly = true)
	public User loadDomainUser (String usernameOrEmail) {
		return (usernameOrEmail.contains("@")
				? userRepository.findByEmail(usernameOrEmail)
				: userRepository.findByUsername(usernameOrEmail))
				.orElseThrow(() -> new UsernameNotFoundException(
						"No user found with: " + usernameOrEmail));
	}

}
