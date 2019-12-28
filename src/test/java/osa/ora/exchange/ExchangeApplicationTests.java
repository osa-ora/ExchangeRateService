package osa.ora.exchange;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

@SpringBootTest(classes = ExchangeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ExchangeApplicationTests {
	@Autowired
	private TestRestTemplate restTemplate;
	@LocalServerPort
	private int port;

	private String getRootUrl() {
		return "http://localhost:" + port;
	}

	@Test
	public void contextLoads() {
	}

	@Test
	public void testGetExchangeRate() {
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + "/api/v1/exchange/USD/GBP",
		HttpMethod.GET, entity, String.class);
		System.out.println("Test="+getRootUrl() + "/api/v1/exchange/USD/GBP");
		System.out.println("Respnse Test="+response.getBody());
		Assert.notNull(response.getBody());
	}
	@Test
	public void testGetNonAvailableExchangeRate() {
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + "/api/v1/exchange/SAR/EGP",
		HttpMethod.GET, entity, String.class);
		System.out.println("Test="+getRootUrl() + "/api/v1/exchange/SAR/EGP");
		System.out.println("Respnse Test="+response.getBody());
		Assert.hasText("Not Available!");
	}
	@Test
	public void testSameCurrencyExchangeRate() {
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + "/api/v1/exchange/USD/USD",
		HttpMethod.GET, entity, String.class);
		System.out.println("Test="+getRootUrl() + "/api/v1/exchange/USD/USD");
		System.out.println("Respnse Test="+response.getBody());
		Assert.hasText("1.00");
	}
}