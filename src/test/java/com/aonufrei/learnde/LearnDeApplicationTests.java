package com.aonufrei.learnde;

import com.aonufrei.learnde.dto.TopicIn;
import com.aonufrei.learnde.dto.WordIn;
import com.aonufrei.learnde.model.Article;
import com.aonufrei.learnde.model.Topic;
import com.aonufrei.learnde.model.Word;
import com.aonufrei.learnde.repository.TopicRepository;
import com.aonufrei.learnde.repository.WordRepository;
import com.aonufrei.learnde.services.TopicService;
import com.aonufrei.learnde.utils.IntegrationTestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Objects;
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
@ActiveProfiles("test")
class LearnDeApplicationTests {

	private static final String TOPIC_ROOT_PATH = "/api/v1/topics";
	private static final String WORD_ROOT_PATH = "/api/v1/words";

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private TopicRepository topicRepository;

	@Autowired
	private WordRepository wordRepository;

	@Autowired
	private TopicService topicService;

	@BeforeEach
	public void tearDown() {
		topicRepository.deleteAll();
		wordRepository.deleteAll();
	}

	@Test
	public void testHealthEndpoint() throws Exception {
		mvc.perform(get("/api/v1/health")).andExpect(status().isOk());
	}

	@Test
	public void testTopicCrud() throws Exception {
		var integrationUtils = new IntegrationTestUtils(mapper, TOPIC_ROOT_PATH);

		var topic1 = new TopicIn("Topic 1", "Description");
		var topic2 = new TopicIn("Topic 2", "Description");
		var topic3 = new TopicIn(null, "Description");
		var topic4 = new TopicIn("Topic 4", null);
		var topic5 = new TopicIn(null, null);

		// Check that no topics in database
		mvc.perform(integrationUtils.getAllRequest()).andExpect(status().is2xxSuccessful())
				.andExpect(content().string("[]"));

		// Testing topics creation
		mvc.perform(integrationUtils.getCreateRequest(topic1))
				.andExpect(status().is2xxSuccessful())
				.andExpect(jsonPath("$.name", is(topic1.name())))
				.andExpect(jsonPath("$.description", is(topic1.description())));

		mvc.perform(integrationUtils.getCreateRequest(topic2))
				.andExpect(status().is2xxSuccessful())
				.andExpect(jsonPath("$.name", is(topic2.name())))
				.andExpect(jsonPath("$.description", is(topic2.description())));

		mvc.perform(integrationUtils.getCreateRequest(topic3))
				.andExpect(status().is4xxClientError());

		mvc.perform(integrationUtils.getCreateRequest(topic4))
				.andExpect(status().is2xxSuccessful());
		mvc.perform(integrationUtils.getCreateRequest(topic5))
				.andExpect(status().is4xxClientError());

		// test that the required amount is inserted
		List<Topic> topics = topicRepository.findAll();
		Assertions.assertEquals(3, topics.size());

		// Verify that topics are returned
		mvc.perform(integrationUtils.getAllRequest()).andExpect(status().is2xxSuccessful());

		Optional<Topic> first = topics.stream().filter(it -> it.getDescription() == null).findFirst();
		Assertions.assertTrue(first.isPresent());
		Topic topicToUpdate = first.get();

		// Test get by id
		mvc.perform(integrationUtils.getByIdRequest(topicToUpdate.getId())).andExpect(status().is2xxSuccessful());
		mvc.perform(integrationUtils.getByIdRequest(-1L)).andExpect(status().is4xxClientError());

		// Test update
		final String newDescription = "NEW DESCRIPTION";
		var updateRequest = new TopicIn(topicToUpdate.getName(), newDescription);
		mvc.perform(integrationUtils.getUpdateRequest(topicToUpdate.getId(), updateRequest))
				.andExpect(status().is2xxSuccessful())
				.andExpect(jsonPath("$.id", is(topicToUpdate.getId().intValue())))
				.andExpect(jsonPath("$.name", is(topicToUpdate.getName())))
				.andExpect(jsonPath("$.description", is(newDescription)));

		// Test that getAll contains changes
		mvc.perform(integrationUtils.getAllRequest())
				.andExpect(status().is2xxSuccessful())
				.andExpect(content().string(StringContains.containsString(newDescription)));

		// Test delete
		var ids = topicRepository.findAll().stream()
				.map(Topic::getId)
				.toList();
		for (var id : ids) {
			mvc.perform(integrationUtils.getDeleteRequest(id));
		}

		mvc.perform(integrationUtils.getDeleteRequest(-1L))
				.andExpect(status().is2xxSuccessful())
				.andExpect(content().string(StringContains.containsString("false")));

		// Verify that not topics remained
		mvc.perform(integrationUtils.getAllRequest()).andExpect(content().string("[]"));

	}

	@Test
	public void testWordCrud() throws Exception {
		var integrationUtils = new IntegrationTestUtils(mapper, WORD_ROOT_PATH);

		var topicIn1 = new TopicIn("Topic 1", "First topic");
		var topicIn2 = new TopicIn("Topic 2", "Second topic");
		var topic1 = topicService.create(topicIn1);
		var topic2 = topicService.create(topicIn2);

		var word1 = new WordIn(topic1.id(), "Katze", Article.DIE, "The Cat");
		var word2 = new WordIn(topic1.id(), "Hund", Article.DER, "The Dog");
		var word3 = new WordIn(topic2.id(), "Tisch", Article.DER, "The Table");
		var word4 = new WordIn(topic2.id(), "Stuhl", Article.DER, "The Chair");

		// Verify that no words exist
		mvc.perform(integrationUtils.getAllRequest()).andExpect(content().string("[]"));

		// Test create words
		mvc.perform(integrationUtils.getCreateRequest(word1))
				.andExpect(status().isOk());
		mvc.perform(integrationUtils.getCreateRequest(word2))
				.andExpect(status().isOk());
		mvc.perform(integrationUtils.getCreateRequest(word3))
				.andExpect(status().isOk());
		mvc.perform(integrationUtils.getCreateRequest(word4))
				.andExpect(status().isOk());

		// Verify that words persist
		mvc.perform(integrationUtils.getAllRequest())
				.andExpect(status().isOk());

		// Test update
		Optional<Word> wordToUpdateOpt = wordRepository.findAll().stream()
				.filter(it -> Objects.equals(it.getText(), word1.text())).findFirst();
		Assertions.assertTrue(wordToUpdateOpt.isPresent());
		Word wordToUpdate = wordToUpdateOpt.get();
		final String nText = "Computer";
		final Article nArticle = Article.DER;
		final String nTranslation = "The Computer";
		var nWord1 = new WordIn(word1.topicId(), nText, nArticle, nTranslation);

		mvc.perform(integrationUtils.getUpdateRequest(wordToUpdate.getId(), nWord1));

		// Test get by id and updated changes
		mvc.perform(integrationUtils.getByIdRequest(wordToUpdate.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.text", is(nText)))
				.andExpect(jsonPath("$.article", is(nArticle.getCode())))
				.andExpect(jsonPath("$.translation", is(nTranslation)));

		Assertions.assertEquals(4, wordRepository.count());

		mvc.perform(integrationUtils.getByIdRequest(-1L))
				.andExpect(status().is4xxClientError());

		// Test delete
		var wIds = wordRepository.findAll().stream().mapToLong(Word::getId).toArray();
		for (var id : wIds) {
			mvc.perform(integrationUtils.getDeleteRequest(id))
					.andExpect(status().isOk())
					.andExpect(content().string("true"));
		}
		mvc.perform(integrationUtils.getDeleteRequest(100L))
				.andExpect(status().isOk())
				.andExpect(content().string("false"));

		mvc.perform(integrationUtils.getAllRequest())
				.andExpect(status().isOk())
				.andExpect(content().string("[]"));
	}
}
