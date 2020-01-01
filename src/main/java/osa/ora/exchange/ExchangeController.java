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
		String redisHost = System.getenv("REDIS_HOST");
		String redisPort = System.getenv("REDIS_PORT");
		try {
			if(redisPort==null){
				redisPort="6379";
			}
			if (redisHost == null) {
				System.out.println("Missing Redis Server configurations, will use the default localhost:6379");
				jedis = new Jedis("localhost", 6379);
				jedis.connect();
				System.out.println("Connection configured to Redis server sucessfully at: "+ redisHost + ":" + redisPort);
			} else {
				System.out.println("Redis Server configurations: " + redisHost + ":" + redisPort);
				jedis = new Jedis(redisHost, Integer.parseInt(redisPort));
				jedis.connect();
				System.out.println("Connection configured to Redis server sucessfully at: "+ redisHost + ":" + redisPort);
			}
		} catch (Throwable t) {
			System.out.println("Failed to connect to Redis Server! configured at: "+ redisHost + ":" + redisPort+" Error:"+t.getLocalizedMessage());
			jedis=null;
		}

	}
	private String getFromRedis(String key){
		try{
			return jedis.get(key);
		}catch(Throwable t){
			t.printStackTrace();
			System.out.println("Redis is not responding to get from it!");
			return null;
		}
	}
	private void setIntoRedis(String key,String value){
		try{
			if(jedis!=null) jedis.set(key, value);
		}catch(Throwable t){
			t.printStackTrace();
			System.out.println("Redis is not responding to store cache!");
		}		
	}

	@GetMapping("/exchange/{source}/{target}")
	public String getExchangeRate(@PathVariable(value = "source") String source,
			@PathVariable(value = "target") String target) {
		String response = "Not Available!";
		if (source != null && target != null) {
			String cached = null;
			if(jedis!=null) {
				try {
					cached=getFromRedis(source + target);
				}catch(Throwable t){
					
				}
			}
			if (cached != null) {
				System.out.println("Return the value from the cach=" + cached);
				response = cached;
			} else {
				System.out.println("No thing in the cache, retireve the exchange rate");
				if ("USD".equalsIgnoreCase(source) && "EGP".equalsIgnoreCase(target)) {
					response = "16.06";
					setIntoRedis(source + target, response);
				}
				if ("EGP".equalsIgnoreCase(source) && "USD".equalsIgnoreCase(target)) {
					response = "0.13";
					setIntoRedis(source + target, response);
				}
				if ("USD".equalsIgnoreCase(source) && "GBP".equalsIgnoreCase(target)) {
					response = "0.96";
					setIntoRedis(source + target, response);
				}
				if ("GBP".equalsIgnoreCase(source) && "USD".equalsIgnoreCase(target)) {
					response = "1.11";
					setIntoRedis(source + target, response);
				}
				if ("GBP".equalsIgnoreCase(source) && "EGP".equalsIgnoreCase(target)) {
					response = "20.03";
					setIntoRedis(source + target, response);
				}
				if ("EGP".equalsIgnoreCase(source) && "GBP".equalsIgnoreCase(target)) {
					response = "0.045";
					setIntoRedis(source + target, response);
				}
				if (source.equalsIgnoreCase(target)) {
					response = "1.00";
					setIntoRedis(source + target, response);
				}
			}
		}
		
		System.out.println("Exchange Rate for " + source + " into " + target + " equal = " + response);
		return response;
	}
}
