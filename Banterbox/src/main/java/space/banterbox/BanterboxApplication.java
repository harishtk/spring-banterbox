package space.banterbox;

import me.paulschwarz.springdotenv.DotenvConfig;
import me.paulschwarz.springdotenv.spring.DotenvApplicationInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BanterboxApplication {

	public static void main(String[] args) {

		SpringApplication.run(BanterboxApplication.class, args);
	}

}
