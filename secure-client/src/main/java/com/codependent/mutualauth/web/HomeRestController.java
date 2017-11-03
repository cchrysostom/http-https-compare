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

	@Autowired
	private RestTemplate restTemplate;
	
	@GetMapping("/")
	public String home() throws RestClientException, URISyntaxException{
		String response;
		long start = System.nanoTime();
		response = restTemplate.getForObject(new URI("https://localhost:8443"), String.class);
		long end  = System.nanoTime();

		logger.info("Elapsed time: {} nano-seconds", end - start);
		return response;
	}
	
}
