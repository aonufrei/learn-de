package com.aonufrei.learnde;

import com.aonufrei.learnde.dto.TopicIn;
import com.aonufrei.learnde.model.Topic;
import com.aonufrei.learnde.repository.TopicRepository;
import com.aonufrei.learnde.repository.WordRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource(
		locations = "classpath:application-test.yaml")
class LearnDeApplicationTests {

	private static final String TOPIC_ROOT_PATH = "/api/v1/topics";

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private TopicRepository topicRepository;

	@Autowired
	private WordRepository wordRepository;

	@Test
	public void testHealthEndpoint() throws Exception {
		mvc.perform(get("/api/v1/health")).andExpect(status().isOk());
	}

	@Test
	public void testTopicCrud() throws Exception {
		var topic1 = new TopicIn("Topic 1", "Description");
		var topic2 = new TopicIn("Topic 2", "Description");
		var topic3 = new TopicIn(null, "Description");
		var topic4 = new TopicIn("Topic 4", null);
		var topic5 = new TopicIn(null, null);

		// Check that no topics in database
		mvc.perform(getAllTopics()).andExpect(status().is2xxSuccessful())
				.andExpect(content().string("[]"));

		// Testing topics creation
		mvc.perform(getCreateTopicRequest(topic1))
				.andExpect(status().is2xxSuccessful())
				.andExpect(jsonPath("$.name", is(topic1.name())))
				.andExpect(jsonPath("$.description", is(topic1.description())));

		mvc.perform(getCreateTopicRequest(topic2))
				.andExpect(status().is2xxSuccessful())
				.andExpect(jsonPath("$.name", is(topic2.name())))
				.andExpect(jsonPath("$.description", is(topic2.description())));

		mvc.perform(getCreateTopicRequest(topic3))
				.andExpect(status().is4xxClientError());

		mvc.perform(getCreateTopicRequest(topic4))
				.andExpect(status().is2xxSuccessful());
		mvc.perform(getCreateTopicRequest(topic5))
				.andExpect(status().is4xxClientError());

		// test that the required amount is inserted
		List<Topic> topics = topicRepository.findAll();
		Assertions.assertEquals(3, topics.size());

		// Verify that topics are returned
		mvc.perform(getAllTopics()).andExpect(status().is2xxSuccessful());

		Optional<Topic> first = topics.stream().filter(it -> it.getDescription() == null).findFirst();
		Assertions.assertTrue(first.isPresent());
		Topic topicToUpdate = first.get();

		// Test get by id
		mvc.perform(getTopicByIdRequest(topicToUpdate.getId())).andExpect(status().is2xxSuccessful());
		mvc.perform(getTopicByIdRequest(-1L)).andExpect(status().is4xxClientError());

		// Test update
		final String newDescription = "NEW DESCRIPTION";
		var updateRequest = new TopicIn(topicToUpdate.getName(), newDescription);
		mvc.perform(getUpdateTopicRequest(topicToUpdate.getId(), updateRequest))
				.andExpect(status().is2xxSuccessful())
				.andExpect(jsonPath("$.id", is(topicToUpdate.getId().intValue())))
				.andExpect(jsonPath("$.name", is(topicToUpdate.getName())))
				.andExpect(jsonPath("$.description", is(newDescription)));

		// Test that getAll contains changes
		mvc.perform(getAllTopics())
				.andExpect(status().is2xxSuccessful())
				.andExpect(content().string(StringContains.containsString(newDescription)));

		// Test delete
		var ids = topicRepository.findAll().stream()
				.map(Topic::getId)
				.toList();
		for (var id : ids) {
			mvc.perform(getDeleteTopicRequest(id));
		}

		mvc.perform(getDeleteTopicRequest(-1L))
				.andExpect(status().is2xxSuccessful())
				.andExpect(content().string(StringContains.containsString("false")));

		// Verify that not topics remained
		mvc.perform(getAllTopics()).andExpect(content().string("[]"));

	}

	@Test
	public void testWordCrud() {
	}

	public MockHttpServletRequestBuilder getAllTopics() {
		return get(TOPIC_ROOT_PATH);
	}

	public MockHttpServletRequestBuilder getCreateTopicRequest(TopicIn ti) throws JsonProcessingException {
		String request = mapper.writeValueAsString(ti);
		return post(TOPIC_ROOT_PATH).contentType("application/json").content(request);
	}

	public MockHttpServletRequestBuilder getUpdateTopicRequest(Long id, TopicIn ti) throws JsonProcessingException {
		String request = mapper.writeValueAsString(ti);
		return put(TOPIC_ROOT_PATH + "/" + id).contentType("application/json").content(request);
	}

	public MockHttpServletRequestBuilder getDeleteTopicRequest(Long id) {
		return delete(TOPIC_ROOT_PATH + "/" + id);
	}

	public MockHttpServletRequestBuilder getTopicByIdRequest(Long id) {
		return get(TOPIC_ROOT_PATH + "/" + id);
	}
}
