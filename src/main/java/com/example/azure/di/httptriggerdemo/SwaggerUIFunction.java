package com.example.azure.di.httptriggerdemo;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
@Component
public class SwaggerUIFunction {

	@FunctionName("swagger-ui")
	public HttpResponseMessage serveSwaggerUI(
		@HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS, route = "swagger-ui/{*path}")
		HttpRequestMessage<Optional<String>> request,
		@BindingName("path") String path,
		final ExecutionContext context) {

		if (path == null || path.isEmpty()) {
			path = "index.html";
		}

		Path filePath = Paths.get("C:/Users/udbha/Downloads/spring-cloud-function/spring-cloud-function-samples/function-sample-azure-http-trigger/src/main/resources/swagger-ui", path);
		context.getLogger().info("Serving Swagger UI file: " + filePath.toString());

		if (!Files.exists(filePath)) {
			return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("File not found").build();
		}

		try {
			String contentType = Files.probeContentType(filePath);
			byte[] content = Files.readAllBytes(filePath);
			return request.createResponseBuilder(HttpStatus.OK)
				.header("Content-Type", contentType)
				.body(content)
				.build();
		} catch (IOException e) {
			context.getLogger().severe("Failed to read file: " + e.getMessage());
			return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to load file").build();
		}
	}
}
