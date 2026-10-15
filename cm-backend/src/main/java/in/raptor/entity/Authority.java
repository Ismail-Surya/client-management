package in.raptor.entity;

import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Entity
@Table(name = "authorities")
public class Authority {
	
	private static final String SPRING_PREFIX = "GROUP_";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true, length = 64)
	private String name;

	public Authority() {
	}

	public Authority(String name) {
		this.name = name;
	}

	public Authority(Long id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public static Authority of(String name) {
        return new Authority(name);
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public GrantedAuthority toGrantedAuthority() {
		return new SimpleGrantedAuthority(SPRING_PREFIX + name.toUpperCase());
	}
	
	@Override
	public boolean equals (Object o) {
		if (this == o) return true;
		
		if (!(o instanceof Authority)) return false;
		
		Authority authority = (Authority) o;
		
		return id != null && id.equals(authority.id);
	}
	
	@Override
	public int hashCode () {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "Authority [id=" + id + ", name=" + name + "]";
	}
	
}
