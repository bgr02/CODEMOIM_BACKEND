package com.sideproject.codemoim.config;

import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.tomcat.util.http.LegacyCookieProcessor;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
public class CustomWebServerFactory implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.addContextCustomizers(context -> context.setCookieProcessor(new LegacyCookieProcessor()));

        factory.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> {
            ProtocolHandler protocolHandler = connector.getProtocolHandler();
            if (protocolHandler instanceof AbstractHttp11Protocol) {
                applyProperties((AbstractHttp11Protocol) protocolHandler);
            }
        });
    }

    private void applyProperties(AbstractHttp11Protocol protocolHandler) {
        protocolHandler.setConnectionTimeout(3000);
        protocolHandler.setMaxKeepAliveRequests(300);
        protocolHandler.setKeepAliveTimeout(60000);
        //protocolHandler.setAcceptCount(1023);
    }

}
