package in.raptor.exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import in.raptor.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity <ErrorResponse> handleValidation (MethodArgumentNotValidException exc,
															WebRequest request) {
		Map <String, String> fieldErrors = new HashMap <> ();
		
		for (FieldError fe: exc.getBindingResult().getFieldErrors()) {
			fieldErrors.put(fe.getField(), fe.getDefaultMessage());
		}
		
		return ResponseEntity.badRequest().body(ErrorResponse.of(
				HttpStatus.BAD_REQUEST.value(),
				"Bad Request",
				"Validation Failed",
				request.getDescription(false).replace("uri=", ""),
				fieldErrors));
	}
	
	@ExceptionHandler (DuplicateResourceException.class)
	public ResponseEntity <ErrorResponse> handleDuplicate (DuplicateResourceException exc,
															WebRequest request) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.of(
				HttpStatus.CONFLICT.value(),
				"Conflict",
				exc.getMessage(),
				request.getDescription(false).replace("uri=", "")));
	}
	
	@ExceptionHandler ({ BadCredentialsException.class, UsernameNotFoundException.class })
	public ResponseEntity <ErrorResponse> handleBadCredentials (Exception exc, WebRequest request) {
		
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponse.of(
				HttpStatus.UNAUTHORIZED.value(),
				"Unauthorized",
				"Invalid Credentials",
				request.getDescription(false).replace("uri=", "")));
				
	}
	
	@ExceptionHandler (TokenRefreshException.class)
	public ResponseEntity <ErrorResponse> handleTokenRefresh (TokenRefreshException exc,
																WebRequest request) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponse.of(
				HttpStatus.UNAUTHORIZED.value(),
				"Unauthorized",
				exc.getMessage(),
				request.getDescription(false).replace("uri=", "")));
	}
	
	@ExceptionHandler (AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDenied (AccessDeniedException exc,
															WebRequest request) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse.of(
				HttpStatus.FORBIDDEN.value(),
				"Forbidden",
				"You do not have permission to access this resource",
				request.getDescription(false).replace("uri=", "")));
	}
	
	@ExceptionHandler (Exception.class)
	public ResponseEntity <ErrorResponse> handleGeneric (Exception exc, WebRequest request) {
		log.error("Unhandled exception", exc);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.of(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Internal Server Error",
				// "An unexpected error occurred",
				// exc.getMessage(),
				request.getDescription(false).replace("uri=", "")));
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound (ResourceNotFoundException exc,
														WebRequest request) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.of(
				HttpStatus.NOT_FOUND.value(),
				"Not Found",
				exc.getMessage(),
				request.getDescription(false).replace("uri=", "")));
	}
	
}









