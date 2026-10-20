package in.raptor.security;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final UserDetailsServiceImpl userDetailsServiceImpl;
	
	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
			UserDetailsServiceImpl userDetailsServiceImpl) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.userDetailsServiceImpl = userDetailsServiceImpl;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder () {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider () {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsServiceImpl);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}
	
	@Bean
	public AuthenticationManager authenticationManager (AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
	
	@Bean
	public SecurityFilterChain filterChain (HttpSecurity http) throws Exception {
		http
			.cors().and()
			.csrf().disable()
			.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.exceptionHandling()
				.authenticationEntryPoint((req, res, ex) -> writeError(res, HttpStatus.UNAUTHORIZED,
						"Unauthorized", "Authentication required or token invalid"))
				.accessDeniedHandler((req, res, ex) -> writeError(res, HttpStatus.FORBIDDEN,
						"Forbidden", "You do not have permission to access this resource"))
			.and()
			.authorizeRequests()
				.antMatchers(
						"/api/auth/login",
						"/api/auth/register",
						"/api/auth/refresh",
						"/api/auth/logout"
				).permitAll()
				.antMatchers("/api/**").authenticated()
				.anyRequest().permitAll()
			.and()
			.authenticationProvider(authenticationProvider())
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
	
	@Bean
	public CorsConfigurationSource configurationSource () {
		CorsConfiguration config = new CorsConfiguration();
		
		config.setAllowedOrigins(List.of("http://localhost:5173"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setAllowCredentials(true);
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		
		return source;
	}

	private static void writeError(HttpServletResponse response, HttpStatus status, String error, String message) throws IOException {
		
		response.setStatus(status.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		
		Map <String, Object> body = Map.of(
					"timestamp", Instant.now().toString(),
					"status", status.value(),
					"error", error,
					"message", message
				);
		
		new ObjectMapper().writeValue(response.getWriter(), body);
		
	}
	
}











