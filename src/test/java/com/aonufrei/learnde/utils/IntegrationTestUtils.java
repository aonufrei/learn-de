package com.aonufrei.learnde.utils;

import com.aonufrei.learnde.dto.LoginIn;
import com.aonufrei.learnde.dto.UserIn;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class IntegrationTestUtils {

	private final ObjectMapper objectMapper;

	private final String rootPath;

	private final String authToken;

	public IntegrationTestUtils(ObjectMapper objectMapper, String rootPath, String authToken) {
		this.objectMapper = objectMapper;
		this.rootPath = rootPath;
		this.authToken = authToken;
	}

	public <T> MockHttpServletRequestBuilder getAllRequest() {
		return get(rootPath);
	}

	public <T> MockHttpServletRequestBuilder getCreateRequest(T ti) throws JsonProcessingException {
		String request = objectMapper.writeValueAsString(ti);
		return post(rootPath).contentType("application/json")
				.header("Authorization", authToken)
				.content(request);
	}

	public <T> MockHttpServletRequestBuilder getUpdateRequest(Long id, T ti) throws JsonProcessingException {
		String request = objectMapper.writeValueAsString(ti);
		return put(rootPath + "/" + id).contentType("application/json")
				.header("Authorization", authToken)
				.content(request);
	}

	public <T> MockHttpServletRequestBuilder getRegisterRequest(UserIn ui) throws JsonProcessingException {
		String request = objectMapper.writeValueAsString(ui);
		return post("/api/v1/auth/register").contentType("application/json")
				.content(request);
	}

	public <T> MockHttpServletRequestBuilder getLoginRequest(LoginIn li) throws JsonProcessingException {
		String request = objectMapper.writeValueAsString(li);
		return post("/api/v1/auth/login").contentType("application/json")
				.content(request);
	}

	public <T> MockHttpServletRequestBuilder getDeleteRequest(Long id) {
		return delete(rootPath + "/" + id).header("Authorization", authToken);
	}

	public <T> MockHttpServletRequestBuilder getByIdRequest(Long id) {
		return get(rootPath + "/" + id).header("Authorization", authToken);
	}

}
