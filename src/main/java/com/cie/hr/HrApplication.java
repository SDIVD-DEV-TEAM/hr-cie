package com.cie.hr;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SecurityScheme(name = "cie-hr-api", scheme = "bearer", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server URL")}, info = @Info(title = "User API", version = "2.0", description = "User Details"))
@EnableJpaRepositories(basePackages = "com.cie.hr")
@EntityScan(basePackages = "com.cie.hr")
@SpringBootApplication(scanBasePackages = "com.cie.hr", exclude = { ThymeleafAutoConfiguration.class })
public class HrApplication {
	public static void main(String[] args) {
		SpringApplication.run(HrApplication.class, args);
	}
}
