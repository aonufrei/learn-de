package com.aonufrei.learnde.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class IntegrationTestUtils {

	private final ObjectMapper objectMapper;

	private final String rootPath;

	public IntegrationTestUtils(ObjectMapper objectMapper, String rootPath) {
		this.objectMapper = objectMapper;
		this.rootPath = rootPath;
	}

	public <T> MockHttpServletRequestBuilder getAllRequest() {
		return get(rootPath);
	}

	public <T> MockHttpServletRequestBuilder getCreateRequest(T ti) throws JsonProcessingException {
		String request = objectMapper.writeValueAsString(ti);
		return post(rootPath).contentType("application/json").content(request);
	}

	public <T> MockHttpServletRequestBuilder getUpdateRequest(Long id, T ti) throws JsonProcessingException {
		String request = objectMapper.writeValueAsString(ti);
		return put(rootPath + "/" + id).contentType("application/json").content(request);
	}

	public <T> MockHttpServletRequestBuilder getDeleteRequest(Long id) {
		return delete(rootPath + "/" + id);
	}

	public <T> MockHttpServletRequestBuilder getByIdRequest(Long id) {
		return get(rootPath + "/" + id);
	}

}
