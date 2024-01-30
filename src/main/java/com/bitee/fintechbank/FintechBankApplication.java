package com.bitee.fintechbank;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
		info = @Info(
				title = "Bitee Fintech Application",
				description = "Backend Rest Apis for BFA",
				version = "v1.0",
				contact = @Contact(
						name = "Caleb Bitiyong",
						email = "calebduniya45@gmail.com",
						url = "https://github.com/Bitee-cd"
				),
				license = @License(
						name = "Caleb Bitiyong",
						url = "https://github.com/Bitee-cd"
				)
		),
		externalDocs = @ExternalDocumentation(
				description = "Bitee fintech App Documentation",
				url = "https://github.com/Bitee-cd"
		)
)
@SpringBootApplication
public class FintechBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(FintechBankApplication.class, args);
	}
}
