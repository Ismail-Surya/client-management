package in.raptor.entity;

import java.time.Instant;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(
	name = "refresh_tokens",
	uniqueConstraints = @UniqueConstraint(columnNames = "token"),
	indexes = @Index(name = "idx_refresh_tokens_user_id", columnList = "user_id")
)
public class RefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, length = 255)
	private String token;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@Column(nullable = false)
	private Instant expiryDate;
	
	@Column(nullable = false)
	private boolean revoked = false;
	
	@Column(length = 255)
	private String replacedByToken;
	
	@Column(nullable = false, updatable = false)
	private Instant createdAt = Instant.now();

	public RefreshToken() {
	}

	public RefreshToken(String token, User user, Instant expiryDate) {
		this.token = token;
		this.user = user;
		this.expiryDate = expiryDate;
	}
	
	public boolean isExpired () {
		return expiryDate.isBefore(Instant.now());
	}
	
	public boolean isActive () {
		return !revoked && !isExpired();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Instant getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Instant expiryDate) {
		this.expiryDate = expiryDate;
	}

	public boolean isRevoked() {
		return revoked;
	}

	public void setRevoked(boolean revoked) {
		this.revoked = revoked;
	}

	public String getReplacedByToken() {
		return replacedByToken;
	}

	public void setReplacedByToken(String replacedByToken) {
		this.replacedByToken = replacedByToken;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}
	
	@Override
	public boolean equals (Object o) {
		if (this == o) return true;
		
		if (!(o instanceof RefreshToken)) return false;
		
		RefreshToken that = (RefreshToken) o;
		
		return id != null && id.equals(that.id);
	}
	
	@Override
	public int hashCode () {
		return Objects.hash(id);
	}
	
	@Override
    public String toString() {
        return "RefreshToken{id=" + id
                + ", userId=" + (user != null ? user.getId() : null)
                + ", expiryDate=" + expiryDate
                + ", revoked=" + revoked
                + "}";
    }
	
}
