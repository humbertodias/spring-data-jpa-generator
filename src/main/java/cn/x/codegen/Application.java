package cn.x.codegen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main
 */
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan({"cn.x.codegen", "generated"})
public class Application  {

    public static void main(String ... args) {
        SpringApplication.run(Application.class, args);
    }

}