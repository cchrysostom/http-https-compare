package com.codependent.mutualauth.web;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
	private static final int LOOP_COUNT = 50;
	private static final int[] TEST_COUNTS = { 1, 25, 50, 75, 100, 125, 150, 175, 200 };

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
            sb.append(notlsPool(TEST_COUNTS[i]));
            sb.append(tlsNoPool(TEST_COUNTS[i]));
            sb.append(tlsPool(TEST_COUNTS[i]));
        }
        return sb.toString();
    }

    public String tlsNoPool(int count) throws URISyntaxException {
        ExecutorService es = Executors.newCachedThreadPool();
        long start = System.nanoTime();
        for (int i=0; i<count; i++) {
            es.execute(new NonPoolHttpsThread());
        }
        es.shutdown();
        long end  = System.nanoTime();

        String m = String.format("https single. Count: %d,  Elapsed: %d nanoseconds.\n", count, end - start);
        logger.info(m);
        return m;
    }

    public String tlsPool(int count) throws URISyntaxException {
        ExecutorService es = Executors.newCachedThreadPool();
        long start = System.nanoTime();
        for (int i=0; i<count; i++) {
            es.execute(new PooledHttpsThread());
        }
        es.shutdown();
        long end  = System.nanoTime();

        String m = String.format("https pool. Count: %d, time: %d nanoseconds.\n", count, end - start);
        logger.info(m);
        return m;
    }

    public String notls(int count) throws URISyntaxException {
        ExecutorService es = Executors.newCachedThreadPool();
        long start = System.nanoTime();
        for (int i=0; i<count; i++) {
            es.execute(new NonPoolHttpThread());
        }
        es.shutdown();
        long end  = System.nanoTime();

        String m = String.format("http single. Count: %d, Elapsed: %d nanoseconds\n", count, end - start);
        logger.info(m);
        return m;
    }

    public String notlsPool(int count) throws URISyntaxException {
      ExecutorService es = Executors.newCachedThreadPool();
      long start = System.nanoTime();
      for (int i=0; i<count; i++) {
        es.execute(new PooledHttpThread());
      }
      es.shutdown();
      long end  = System.nanoTime();

      String m = String.format("http pooled. Count: %d, Elapsed: %d nanoseconds\n", count, end - start);
      logger.info(m);
      return m;
    }

    private class NonPoolHttpsThread implements Runnable {

        @Override
        public void run() {
            try {
                restTemplate.getForObject(new URI("https://localhost:8443"), String.class);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private class PooledHttpsThread implements Runnable {

        @Override
        public void run() {
            try {
                restTemplatePool.getForObject(new URI("https://localhost:8443"), String.class);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private class NonPoolHttpThread implements Runnable {
        @Override
        public void run() {
            try {
                restTemplate.getForObject(new URI("http://localhost:8400/notlsmsg"), String.class);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private class PooledHttpThread implements Runnable {
	    @Override
      public void run() {
	      try {
	        restTemplatePool.getForObject(new URI("http://localhost:8400/notlsmsg"), String.class);
        } catch (URISyntaxException e) {
	        e.printStackTrace();
        }
      }
    }
}
