package mx.edu.uteq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Evaluacion1Application {

	public static void main(String[] args) {
		SpringApplication.run(Evaluacion1Application.class, args);
	}

}
