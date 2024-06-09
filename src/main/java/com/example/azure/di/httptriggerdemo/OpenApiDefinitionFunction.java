package com.example.azure.di.httptriggerdemo;

import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.ExecutionContext;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
@Component
public class OpenApiDefinitionFunction {

	@FunctionName("openapi")
	public HttpResponseMessage getOpenApiDefinition(
		@HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS)
		HttpRequestMessage<Optional<String>> request,
		final ExecutionContext context
	) {
		context.getLogger().info("Returning OpenAPI definition.");

		try {
			String openApiDefinition = new String(Files.readAllBytes(Paths.get("C:/Users/udbha/Downloads/spring-cloud-function/spring-cloud-function-samples/function-sample-azure-http-trigger/src/main/resources/openapi.json")));
			return request.createResponseBuilder(HttpStatus.OK)
				.header("Content-Type", "application/json")
				.body(openApiDefinition)
				.build();
		} catch (IOException e) {
			context.getLogger().severe("Failed to read OpenAPI definition file: " + e.getMessage());
			return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to load OpenAPI definition").build();
		}
	}
}
