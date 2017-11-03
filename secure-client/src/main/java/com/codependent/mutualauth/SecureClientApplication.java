package com.codependent.mutualauth;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class SecureClientApplication {

	@PostConstruct
	public void initSsl(){
		System.setProperty("javax.net.ssl.keyStore", "/Users/cchrysostom/github.com/codependent/spring-boot-ssl-mutual-authentication/client-keystore.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "secret");
		System.setProperty("javax.net.ssl.trustStore", "/Users/cchrysostom/github.com/codependent/spring-boot-ssl-mutual-authentication/client-truststore.jks");
		System.setProperty("javax.net.ssl.trustStorePassword", "secret");
		/*
		javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
			(hostname,sslSession) -> {
				if (hostname.equals("localhost")) {
					return true;
				}
				return false;
			});*/
	}
	
	@Bean
	public RestTemplate template() throws Exception{
		RestTemplate template = new RestTemplate();
		return template;
	}
	
	public static void main(String[] args) {
		SpringApplication.run(SecureClientApplication.class, args);
	}
}
