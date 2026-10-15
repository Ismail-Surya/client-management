package in.raptor.security;

import java.io.IOException;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import in.raptor.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
	private static final String HEADER = "Authorization";
	private static final String PREFIX = "Bearer ";
	
	private final JwtService jwtService;
	private final UserDetailsServiceImpl userDetailsService;
	
	public JwtAuthenticationFilter(JwtService jwtService, UserDetailsServiceImpl userDetailsService) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String token = extractToken(request);
		
		if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			try {
				Claims claims = jwtService.parse(token);
				
				String username = claims.getSubject();
				
				User user = userDetailsService.loadDomainUser(username);
				
				Set <GrantedAuthority> authorities = user.toGrantedAuthorities();
				
				UsernamePasswordAuthenticationToken authentication =
						new UsernamePasswordAuthenticationToken(user, null, authorities);
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} catch (JwtException | IllegalArgumentException exc) {
				log.debug("JWT rejected: {}", exc.getMessage());
			} catch (UsernameNotFoundException exc) {
				log.debug("JWT valid but user no longer exists: {}", exc.getMessage());
			}
		}
		
		chain.doFilter(request, response);
	}

	private String extractToken(HttpServletRequest request) {
		String header = request.getHeader(HEADER);
		if (header != null && header.startsWith(PREFIX)) {
			return header.substring(PREFIX.length()).trim();
		}
		return null;
	}

}













