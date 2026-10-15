package in.raptor.security;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

	private String secret;

	private long accessExpirationMs;

	private long refreshExpirationMs;

	private String issuer;

	public JwtProperties() {
	}

	public JwtProperties(String secret, long accessExpirationMs, long refreshExpirationMs, String issuer) {
		this.secret = secret;
		this.accessExpirationMs = accessExpirationMs;
		this.refreshExpirationMs = refreshExpirationMs;
		this.issuer = issuer;
	}

	@PostConstruct
	void validate() {
		if (secret == null || secret.getBytes().length < 32) {
			throw new IllegalStateException("app.jwt.secret must be set and at least 32 bytes long for HS256");
		}

		if (issuer == null || issuer.isBlank()) {
			throw new IllegalStateException("app.jwt.issuer must be set");
		}

		if (accessExpirationMs <= 0 || refreshExpirationMs <= 0) {
			throw new IllegalStateException(
					"app.jwt.access-expiration-ms and app.jwt.refresh-expiration-ms must be > 0");
		}
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public long getAccessExpirationMs() {
		return accessExpirationMs;
	}

	public void setAccessExpirationMs(long accessExpirationMs) {
		this.accessExpirationMs = accessExpirationMs;
	}

	public long getRefreshExpirationMs() {
		return refreshExpirationMs;
	}

	public void setRefreshExpirationMs(long refreshExpirationMs) {
		this.refreshExpirationMs = refreshExpirationMs;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	@Override
	public String toString() {
		return "JwtProperties [secret=" + secret + ", accessExpirationMs=" + accessExpirationMs
				+ ", refreshExpirationMs=" + refreshExpirationMs + ", issuer=" + issuer + "]";
	}

}
