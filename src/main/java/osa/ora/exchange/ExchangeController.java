package osa.ora.exchange;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import redis.clients.jedis.Jedis;

@RestController
@RequestMapping("/api/v1")
public class ExchangeController {
	private static Jedis jedis;

	public ExchangeController() {
		super();
		try {
			String redisHost = System.getenv("REDIS_HOST");
			String redisPort = System.getenv("REDIS_PORT");
			if (redisHost == null || redisPort == null) {
				System.out.println("Missing Redis Server configurations, will use the default localhost:6379");
				jedis = new Jedis("localhost", 6379);
			} else {
				System.out.println("Redis Server configurations: " + redisHost + ":" + redisPort);
				jedis = new Jedis(redisHost, Integer.parseInt(redisPort));
			}
			System.out.println("Connection to Redis server sucessfully");
		} catch (Throwable t) {
			System.out.println("Failed to connect to Redis Server sucessfully");
		}

	}

	@GetMapping("/exchange/{source}/{target}")
	public String getUsersById(@PathVariable(value = "source") String source,
			@PathVariable(value = "target") String target) {
		String response = "Not Available!";
		if (source != null && target != null) {
			String cached = null;
			if(jedis!=null) {
				cached=jedis.get(source + target);
			}
			if (cached != null) {
				System.out.println("Return the value from the cach=" + cached);
				response = cached;
			} else {
				System.out.println("No thing in the cache, retireve the exchange rate");
				if ("USD".equalsIgnoreCase(source) && "EGP".equalsIgnoreCase(target)) {
					response = "16.06";
					if(jedis!=null) jedis.set(source + target, response);
				}
				if ("EGP".equalsIgnoreCase(source) && "USD".equalsIgnoreCase(target)) {
					response = "0.13";
					if(jedis!=null) jedis.set(source + target, response);
				}
				if ("USD".equalsIgnoreCase(source) && "GBP".equalsIgnoreCase(target)) {
					response = "0.96";
					if(jedis!=null) jedis.set(source + target, response);
				}
				if ("GBP".equalsIgnoreCase(source) && "USD".equalsIgnoreCase(target)) {
					response = "1.11";
					if(jedis!=null) jedis.set(source + target, response);
				}
				if ("GBP".equalsIgnoreCase(source) && "EGP".equalsIgnoreCase(target)) {
					response = "20.03";
					if(jedis!=null) jedis.set(source + target, response);
				}
				if ("EGP".equalsIgnoreCase(source) && "GBP".equalsIgnoreCase(target)) {
					response = "0.045";
					if(jedis!=null) jedis.set(source + target, response);
				}
				if (source.equalsIgnoreCase(target)) {
					response = "1.00";
					if(jedis!=null) jedis.set(source + target, response);
				}
			}
		}
		
		System.out.println("Exchange Rate for " + source + " into " + target + " equal = " + response);
		return response;
	}
}
