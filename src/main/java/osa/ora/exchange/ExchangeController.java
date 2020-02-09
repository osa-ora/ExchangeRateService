package osa.ora.exchange;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;

@RestController
@RequestMapping("/api/v1")
public class ExchangeController {
	private static Jedis jedis;
	private static int cacheSeconds=3600;
	/**
	 * default constructor 
     * Includes initialization of environment variables
	 */
	public ExchangeController() {
		super();
		String redisHost = System.getenv("REDIS_HOST");
		String redisPort = System.getenv("REDIS_PORT");
		String redisPassword = System.getenv("REDIS_PASSWORD");
		String caching = System.getenv("CACHE_SECONDS");
		//is caching configured?
		if(caching!=null){
			try{
				cacheSeconds=Integer.parseInt(caching);
			}catch(Throwable t){
				System.out.println("Caching Expiration is not configured properly!");
				cacheSeconds=3600;
			}
		}else{
			System.out.println("Caching Expiration is not configured properly!, will assume the default:"+cacheSeconds);
		}
		//is redis server location configured?
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
				System.out.println("Redis Server configurations: " + redisHost + ":" + redisPort+" password:"+redisPassword);
				JedisShardInfo shardInfo = new JedisShardInfo(redisHost, redisPort);
				shardInfo.setPassword(redisPassword);
				jedis = new Jedis(shardInfo);
				jedis.connect();
				System.out.println("Connection configured to Redis server sucessfully at: "+ redisHost + ":" + redisPort);
			}
		} catch (Throwable t) {
			t.printStackTrace();
			System.out.println("Failed to connect to Redis Server! configured at: "+ redisHost + ":" + redisPort+" Error:"+t.getLocalizedMessage());
			jedis=null;
		}

	}
	/**
	 * Private method to get a value for a key from the Redis cluster
	 * @param key
	 * @return the value of this key if exist or null
	 */
	private String getFromRedis(String key){
		try{
			return jedis.get(key);
		}catch(Throwable t){
			t.printStackTrace();
			System.out.println("Redis is not responding to get from it!");
			return null;
		}
	}
	/**
	 * Set a value for a key in the Redis cluster with optional cachingExpiration in seconds 
	 * @param key
	 * @param value
	 * @param cachingExpiration
	 */
	private void setIntoRedis(String key,String value, int cachingExpiration){
		try{
			if(jedis!=null) {
				jedis.set(key, value);
				if(cachingExpiration==-1){
					//no specific expiration is requested, then use the global default value: cacheSeconds
					jedis.expire(key, cacheSeconds);
				}else{
					//use the requested value
					jedis.expire(key, cachingExpiration);
				}
			}
		}catch(Throwable t){
			t.printStackTrace();
			System.out.println("Redis is not responding to store cache!");
		}		
	}
	/**
	 * Rest Service to return the exchange rate for specific source and target currency
	 * If not available will return "Not Available!"
	 * @param source
	 * @param target
	 * @return the exchange rate
	 */
	@GetMapping("/exchange/{source}/{target}")
	public String getExchangeRate(@PathVariable(value = "source") String source,
			@PathVariable(value = "target") String target) {
		String response = "Not Available!";
		if (source != null && target != null) {
			String cached = null;
			if(jedis!=null) {
				cached=getFromRedis(source + target);
			}else{
				System.out.println("Redis is not connected!");
			}
			if (cached != null) {
				System.out.println("Return the value from the cach=" + cached);
				response = cached;
			} else {
				System.out.println("Redis is not configured or nothing in the cache, retireve the exchange rate");
				if ("USD".equalsIgnoreCase(source) && "EGP".equalsIgnoreCase(target)) {
					response = "16.06";
					setIntoRedis(source + target, response,-1);
				}
				if ("EGP".equalsIgnoreCase(source) && "USD".equalsIgnoreCase(target)) {
					response = "0.13";
					setIntoRedis(source + target, response,-1);
				}
				if ("USD".equalsIgnoreCase(source) && "GBP".equalsIgnoreCase(target)) {
					response = "0.96";
					setIntoRedis(source + target, response,-1);
				}
				if ("GBP".equalsIgnoreCase(source) && "USD".equalsIgnoreCase(target)) {
					response = "1.11";
					setIntoRedis(source + target, response,-1);
				}
				if ("GBP".equalsIgnoreCase(source) && "EGP".equalsIgnoreCase(target)) {
					response = "20.03";
					setIntoRedis(source + target, response,-1);
				}
				if ("EGP".equalsIgnoreCase(source) && "GBP".equalsIgnoreCase(target)) {
					response = "0.045";
					setIntoRedis(source + target, response,-1);
				}
				if (source.equalsIgnoreCase(target)) {
					response = "1.00";
					setIntoRedis(source + target, response,-1);
				}
			}
		}
		
		System.out.println("Exchange Rate for " + source + " into " + target + " equal = " + response);
		return response;
	}
}
