package com.codependent.mutualauth;

import ch.qos.logback.core.net.ssl.KeyStoreFactoryBean;
import org.apache.catalina.webresources.FileResource;
import org.apache.http.HttpHost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

@SpringBootApplication
public class SecureClientApplication {
    static public final String TRUST_STORE_FILE = "/Users/cchrysostom/github.com/codependent/spring-boot-ssl-mutual-authentication/client-truststore.jks";
    static public final String KEYS_STORE_FILE = "/Users/cchrysostom/github.com/codependent/spring-boot-ssl-mutual-authentication/client-keystore.jks";
    static public final int POOL_MAX = 500;
    static public final int POOL_MAX_ROUTE = 500;
	@PostConstruct
	public void initSsl(){
		System.setProperty("javax.net.ssl.keyStore", KEYS_STORE_FILE);
		System.setProperty("javax.net.ssl.keyStorePassword", "secret");
		System.setProperty("javax.net.ssl.trustStore", TRUST_STORE_FILE);
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
	
	@Bean(name="restTemplate")
	public RestTemplate template() throws Exception{
		RestTemplate template = new RestTemplate();
		return template;
	}

	@Bean(name="restTemplatePool")
    public RestTemplate restTemplatePool() throws Exception {
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(new FileInputStream(TRUST_STORE_FILE), "secret".toCharArray());
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream(KEYS_STORE_FILE), "secret".toCharArray());

        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(trustStore, TrustSelfSignedStrategy.INSTANCE)
                .loadKeyMaterial(keyStore, "secret".toCharArray())
                .build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
        Registry<ConnectionSocketFactory> r = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", plainsf)
                .register("https", sslsf)
                .build();

        PoolingHttpClientConnectionManager poolingConnManager
                = new PoolingHttpClientConnectionManager(r);
        poolingConnManager.setMaxTotal(POOL_MAX);
        poolingConnManager.setDefaultMaxPerRoute(POOL_MAX_ROUTE);
        HttpHost localhost = new HttpHost("locahost", 8443);
        poolingConnManager.setMaxPerRoute(new HttpRoute(localhost), POOL_MAX_ROUTE);

        CloseableHttpClient client = HttpClients.custom()
                .setConnectionManager(poolingConnManager)
                .build();
		RestTemplate restTemplatePool = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
		return restTemplatePool;
	}
	
	public static void main(String[] args) {
		SpringApplication.run(SecureClientApplication.class, args);
	}
}
