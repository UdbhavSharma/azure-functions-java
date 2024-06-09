/*
 * Copyright 2021-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.azure.di.httptriggerdemo;

import java.util.Optional;
import java.util.function.Function;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.stereotype.Component;

@Component
public class MyAzureFunction {

    /**
     * Plain Spring bean (not Spring Cloud Functions!)
     */
    @Autowired
    private Function<String, String> echo;

    /**
     * Plain Spring bean (not Spring Cloud Functions!)
     */
    @Autowired
    private Function<String, String> uppercase;

    /**
     * The FunctionCatalog leverages the Spring Cloud Function framework.
     */
    @Autowired
    private FunctionCatalog functionCatalog;

    @FunctionName("bean")
    public String plainBeans(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            ExecutionContext context) {

        return echo.andThen(uppercase).apply(request.getBody().get());
    }

    @FunctionName("scf")
    public String springCloudFunction(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            ExecutionContext context) {

        // Use SCF composition. Composed functions are not just spring beans but SCF such.
        Function composed = this.functionCatalog.lookup("echo|reverse|uppercase");

        return (String) composed.apply(request.getBody().get());
    }

	@FunctionName("hello")
	public HttpResponseMessage hello(
		@HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS)
		HttpRequestMessage<Optional<String>> request,
		@BindingName("Name") String Name, @BindingName("category") String category,
		final ExecutionContext context) {

		context.getLogger().info("Java HTTP trigger processed a request.");

		// Get the Authorization header
		String authorizationHeader = request.getHeaders().get("Authorization");
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			return request.createResponseBuilder(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header").build();
		}

		String token = authorizationHeader.substring(7); // Remove "Bearer " prefix

		try {
			// Validate the token
			// Proceed with the function logic
			String name = request.getQueryParameters().get("name");
			if (name == null) {
				return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string").build();
			}

			return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
		} catch (Exception e) {
			return request.createResponseBuilder(HttpStatus.UNAUTHORIZED).body("Invalid token").build();
		}
	}





}
