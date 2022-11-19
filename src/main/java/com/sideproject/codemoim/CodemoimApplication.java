package com.sideproject.codemoim;

import com.sideproject.codemoim.property.CustomProperties;
import com.sideproject.codemoim.property.DatabaseProperties;
import com.sideproject.codemoim.property.S3Properties;
import com.sideproject.codemoim.service.SearchService;
import io.awspring.cloud.autoconfigure.context.ContextInstanceDataAutoConfiguration;
import io.awspring.cloud.autoconfigure.context.ContextStackAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.LegacyCookieProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.TomcatServletWebServerFactoryCustomizer;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatEmbeddedWebappClassLoader;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;

import java.security.Security;

@Slf4j
@EnableRetry
@EnableJpaAuditing
@SpringBootApplication
//@EnableConfigurationProperties(value = {CustomProperties.class, DatabaseProperties.class, S3Properties.class})
@ConfigurationPropertiesScan("com.sideproject.codemoim.property")
@EnableAspectJAutoProxy
public class CodemoimApplication {

//	@Autowired
//	Environment environment;
//
//	@Autowired(required = false)
//	private SearchService searchService;

	public static void main(String[] args) {
		disableAddressCache();
		SpringApplication.run(CodemoimApplication.class, args);
	}

	private static void disableAddressCache() {
		Security.setProperty("networkaddress.cache.ttl", "0");
		Security.setProperty("networkaddress.cache.negative.ttl", "0");
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

//	@Bean
//	public WebServerFactoryCustomizer tomcatCustomizer() {
//		return factory -> {
//			if(factory instanceof TomcatServletWebServerFactory) {
//				TomcatServletWebServerFactory tomcat = (TomcatServletWebServerFactory) factory;
//				tomcat.addContextCustomizers(context -> context.setCookieProcessor(new LegacyCookieProcessor()));
//			}
//		};
//	}

	@Bean
	public BeanPostProcessor beanPostProcessor() {
		return new BeanPostProcessor() {
			@Override
			public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
				if (bean instanceof WebSocketMessageBrokerStats) {
					WebSocketMessageBrokerStats webSocketMessageBrokerStats = (WebSocketMessageBrokerStats) bean;
					webSocketMessageBrokerStats.setLoggingPeriod(30 * 60 * 1000);
				}
				return bean;
			}

			@Override
			public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
				return bean;
			}
		};
	}

//	@Override
//	public void run(String... args) throws Exception {
//		String[] activeProfiles = environment.getActiveProfiles();
//		boolean flag = true;
//
//		for (String activeProfile : activeProfiles) {
//			if(activeProfile.equals("test")) {
//				flag = false;
//			}
//		}
//
//		if(flag) {
//			searchService.buildSearchIndex();
//		}
//	}
}
