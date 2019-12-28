package osa.ora.exchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootApplication
public class ExchangeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExchangeApplication.class, args);
		System.out.println("For Health Check: http://localhost:PORT/actuator/health");
		System.out.println("Sample URL: http://localhost:PORT/api/v1/exchange/USD/GBP");
	}

}
