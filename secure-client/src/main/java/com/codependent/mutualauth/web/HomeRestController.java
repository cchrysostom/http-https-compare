package com.codependent.mutualauth.web;

import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RestController
public class HomeRestController {
	private static final Logger logger = LoggerFactory.getLogger(HomeRestController.class);
	private static final int LOOP_COUNT = 500;
    private static final int[] TEST_COUNTS = { 1, 500, 1000, 1500, 2000 };

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private RestTemplate restTemplatePool;
	
	@GetMapping("/")
	public String home() throws RestClientException, URISyntaxException{
		return tlsNoPool(LOOP_COUNT);
	}

    @GetMapping("/tlspool")
	public String tlspool() throws RestClientException, URISyntaxException {
		return tlsPool(LOOP_COUNT);
	}

	@GetMapping("/nontls")
	public String nontls() throws URISyntaxException {
        return notls(LOOP_COUNT);

	}

	@GetMapping("/complete")
    public String complete() throws URISyntaxException {
	    StringBuilder sb = new StringBuilder();
        for (int i=0; i<TEST_COUNTS.length; i++) {
            sb.append(notls(TEST_COUNTS[i]));
            sb.append(tlsNoPool(TEST_COUNTS[i]));
            sb.append(tlsPool(TEST_COUNTS[i]));
        }
        return sb.toString();
    }

    public String tlsNoPool(int count) throws URISyntaxException {
        String response = "";
        long start = System.nanoTime();
        for (int i=0; i<count; i++) {
            response = restTemplate.getForObject(new URI("https://localhost:8443"), String.class);
        }
        long end  = System.nanoTime();

        String m = String.format("TLS Single Connection. Count: %d,  Elapsed: %d nanoseconds.\n", count, end - start);
        logger.info(m);
        return m;
    }

    public String tlsPool(int count) throws URISyntaxException {
        String response = "";
        long start = System.nanoTime();
        for (int i=0; i<count; i++) {
            response = restTemplatePool.getForObject(new URI("https://localhost:8443"), String.class);
        }
        long end  = System.nanoTime();

        String m = String.format("TLS Pooled Connection. Count: %d, time: %d nanoseconds.\n", count, end - start);
        logger.info(m);
        return m;
    }

    public String notls(int count) throws URISyntaxException {
        String response = "";
        long start = System.nanoTime();
        for (int i=0; i<count; i++) {
            response = restTemplate.getForObject(new URI("http://localhost:8400/notlsmsg"), String.class);
        }
        long end  = System.nanoTime();

        String m = String.format("Simple HTTP Single Connection. Count: %d, Elapsed: %d nanoseconds\n", count, end - start);
        logger.info(m);
        return m;
    }
}
