package com.sideproject.codemoim.config;

import com.sideproject.codemoim.property.DatabaseProperties;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Profile("prod")
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({HibernateProperties.class})
public class DatabaseConfig {

    private final DatabaseProperties databaseProperties;
    private final HibernateProperties hibernateProperties;
    private final JpaProperties jpaProperties;

    public DataSource createDataSource(String url) {
        HikariDataSource hikariDataSource = new HikariDataSource();

        hikariDataSource.setAutoCommit(databaseProperties.getHikari().getAutoCommit());
        hikariDataSource.setJdbcUrl(url);
        hikariDataSource.setDriverClassName(databaseProperties.getDriverClassName());
        hikariDataSource.setUsername(databaseProperties.getUsername());
        hikariDataSource.setPassword(databaseProperties.getPassword());
        hikariDataSource.setMaxLifetime(60000);

        return hikariDataSource;
    }

    @Bean
    public DataSource dataSource() {
        CustomRoutingDataSource customRoutingDataSource = new CustomRoutingDataSource();

        Map<Object, Object> dataSourceMap = new LinkedHashMap<>();

        DataSource masterDataSource = createDataSource(databaseProperties.getUrl());
        dataSourceMap.put("master", masterDataSource);

        DatabaseProperties.Replica replica = databaseProperties.getReplica();

        for (String name : replica.getNames()) {
            DataSource replicaDataSource = createDataSource(replica.getUrl());
            dataSourceMap.put(name, replicaDataSource);
        }

        customRoutingDataSource.setTargetDataSources(dataSourceMap);
        customRoutingDataSource.setDefaultTargetDataSource(masterDataSource);

        customRoutingDataSource.afterPropertiesSet();

        return new LazyConnectionDataSourceProxy(customRoutingDataSource);
    }

//    @Bean
//    public ProxyFactoryBean proxyFactoryBean() {
//        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
//
//        proxyFactoryBean.setTarget(dataSource());
//        proxyFactoryBean.setInterceptorNames("methodExecuteCheckInterceptor");
//        proxyFactoryBean.setProxyTargetClass(true);
//
//        return proxyFactoryBean;
//    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("dataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();

        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();

        Map<String, Object> properties = hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings());

        properties.put("hibernate.connection.provider_disables_autocommit", "true");

        //localContainerEntityManagerFactoryBean.setDataSource((DataSource) proxyFactoryBean().getObject());
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        localContainerEntityManagerFactoryBean.setJpaPropertyMap(properties);
        localContainerEntityManagerFactoryBean.setPackagesToScan("com.sideproject.codemoim.domain");

        return localContainerEntityManagerFactoryBean;
    }

    @Bean
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactory.getObject()));
    }
}
