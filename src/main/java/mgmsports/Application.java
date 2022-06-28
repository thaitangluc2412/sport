package mgmsports;

import mgmsports.common.property.FileStorageProperties;
import mgmsports.common.property.SecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

/**
 * Application
 *
 * @author haitran
 */
@SpringBootApplication
@EnableConfigurationProperties({
        SecurityProperties.class,
        FileStorageProperties.class
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
