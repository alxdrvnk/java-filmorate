package ru.yandex.practicum.filmorate.configuration;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;

@TestConfiguration
@PropertySource(value = {"application-integrationtest.properties"})
public class DbUnitConfig {

    @Bean
    public DataSource dataSource(@Value("${filmorate.db.url}") String url,
                                 @Value("${filmorate.db.driverClassName}") String driverClassName,
                                 @Value("${filmorate.db.username}") String user,
                                 @Value("${filmorate.db.password}") String password) {
        SingleConnectionDataSource dataSource = new SingleConnectionDataSource(
                url, user, password, true);
        dataSource.setAutoCommit(false);
        dataSource.setDriverClassName(driverClassName);
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection(DataSource h2FilmorateDataSource) {
        DatabaseConfigBean bean = new DatabaseConfigBean();
        bean.setDatatypeFactory(new H2DataTypeFactory());

        DatabaseDataSourceConnectionFactoryBean factoryBean = new DatabaseDataSourceConnectionFactoryBean();
        factoryBean.setDataSource(h2FilmorateDataSource);
        bean.setQualifiedTableNames(true);
        factoryBean.setDatabaseConfig(bean);
        return factoryBean;
    }
}
