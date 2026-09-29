package in.raptor.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import in.raptor.entity.Authority;
import in.raptor.repository.AuthorityRepository;

@Component

public class DataInitializer implements CommandLineRunner {

	private final AuthorityRepository authorityRepository;
	
	private static final List <String> DEFAULT_AUTHORITIES =
			List.of("read", "create", "update", "delete");
	
	public DataInitializer(AuthorityRepository authorityRepository) {
		this.authorityRepository = authorityRepository;
	}

	@Override
	public void run(String... args) throws Exception {
		for (String name: DEFAULT_AUTHORITIES) {
			authorityRepository.findByName(name).orElseGet(() -> {
				return authorityRepository.save(
							Authority.of(name)
						);
			});
		}
	}

	
	
}
