package ru.yandex.practicum.filmorate.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class DataSourceConfiguration {

    @Bean(name = "dbUnit")
    @Primary
    public DataSource dbUnitDataSource() throws IOException {
        Properties properties = new Properties();
        DataSourceBuilder dataSource = DataSourceBuilder.create();
        properties.load(new FileInputStream("./src/test/resources/application-integrationtest.properties"));

        dataSource.url(properties.getProperty("spring.datasource.url"));
        dataSource.driverClassName(properties.getProperty("spring.datasource.driverClassName"));
        dataSource.username(properties.getProperty("spring.datasource.username"));
        dataSource.password(properties.getProperty("spring.datasource.password"));
        return dataSource.build();
    }

    @Bean
    public DataSourceInitializer dataSourceInitializerDBUnit(@Qualifier("dbUnit") DataSource dataSource) {
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        resourceDatabasePopulator.addScript(new ClassPathResource("schema.sql"));

        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(dataSource);
        dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);

        return dataSourceInitializer;
    }

    @Bean(name = "mainDataSource")
    public DataSource mainDataSource() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("./src/main/resources/application.properties"));

        DataSourceBuilder dataSource = DataSourceBuilder.create();
        dataSource.url(properties.getProperty("spring.datasource.url"));
        dataSource.driverClassName(properties.getProperty("spring.datasource.driverClassName"));
        dataSource.username(properties.getProperty("spring.datasource.username"));
        dataSource.password(properties.getProperty("spring.datasource.password"));

        return dataSource.build();
    }

    @Bean
    public DataSourceInitializer dataSourceInitializerMain(@Qualifier("mainDataSource") DataSource dataSource) {
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        resourceDatabasePopulator.addScript(new ClassPathResource("schema.sql"));

        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(dataSource);
        dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);

        return dataSourceInitializer;
    }

}
