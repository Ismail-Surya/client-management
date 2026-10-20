package in.raptor.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.raptor.dto.AuthResponse;
import in.raptor.dto.LoginRequest;
import in.raptor.dto.LogoutRequest;
import in.raptor.dto.RefreshRequest;
import in.raptor.dto.RegisterRequest;
import in.raptor.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	public final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}
	
	@PostMapping("/register")
	public ResponseEntity <AuthResponse> register (@Valid @RequestBody RegisterRequest request) {
		AuthResponse response = authService.register(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@PostMapping("/login")
	public ResponseEntity <AuthResponse> register (@Valid @RequestBody LoginRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}
	
	@PostMapping("/refresh")
	public ResponseEntity <AuthResponse> refresh (@Valid @RequestBody RefreshRequest request) {
		return ResponseEntity.ok(authService.refresh(request));
	}
	
	@PostMapping("/logout")
	public ResponseEntity <AuthResponse> logout (@Valid @RequestBody LogoutRequest request) {
		authService.logout(request);
		return ResponseEntity.noContent().build();
	}
	
}
















