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
	private static final int LOOP_COUNT = 100;

	@Autowired
	private RestTemplate restTemplate;
	
	@GetMapping("/")
	public String home() throws RestClientException, URISyntaxException{
		String response = "";
		long start = System.nanoTime();
		for (int i=0; i<LOOP_COUNT; i++) {
			response = restTemplate.getForObject(new URI("https://localhost:8443"), String.class);
		}
		long end  = System.nanoTime();

		String m = String.format("Elapsed TLS time: %d nanoseconds.", end - start);
		logger.info(m);
		return String.format("Response sample: %s, %s", response, m);
	}

	@GetMapping("/nontls")
	public String nontls() throws URISyntaxException {
		String response = "";
		long start = System.nanoTime();
		for (int i=0; i<LOOP_COUNT; i++) {
			response = restTemplate.getForObject(new URI("http://localhost:8400/notlsmsg"), String.class);
		}
		long end  = System.nanoTime();

		String m = String.format("Elapsed non-TLS time: %d nano-seconds", end - start);
		logger.info(m);
		return String.format("Response sample: %s, %s", response, m);

	}
}
