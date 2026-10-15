package in.raptor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CmBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CmBackendApplication.class, args);
	}

}
