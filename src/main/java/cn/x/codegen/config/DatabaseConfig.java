package cn.x.codegen.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;

@Configuration
@ConfigurationProperties("spring.datasource")
@Getter
@Setter
public class DatabaseConfig {

    private String driverClassName;
    private String url;
    private String username;
    private String password;

    @Bean
    public Connection connection() throws Exception {
        Class.forName(driverClassName).newInstance();
        Connection connection = DriverManager.getConnection(url, username, password);
        return connection;
    }
}
