package com.aonufrei.learnde;

import com.aonufrei.learnde.dto.*;
import com.aonufrei.learnde.model.Article;
import com.aonufrei.learnde.model.Topic;
import com.aonufrei.learnde.model.User;
import com.aonufrei.learnde.model.Word;
import com.aonufrei.learnde.repository.TopicRepository;
import com.aonufrei.learnde.repository.UserRepository;
import com.aonufrei.learnde.repository.WordRepository;
import com.aonufrei.learnde.services.AuthService;
import com.aonufrei.learnde.services.TopicService;
import com.aonufrei.learnde.services.WordService;
import com.aonufrei.learnde.utils.IntegrationTestUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

	@Autowired
	private WordService wordService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AuthService authService;

	private final User testAdmin = User.builder()
			.name("Admin")
			.username("admin")
			.password("admin")
			.role("ROLE_ADMIN")
			.build();

	@BeforeEach
	public void tearDown() {
		topicRepository.deleteAll();
		wordRepository.deleteAll();
		// Create admin user for tests
		userRepository.deleteAll();
		userRepository.save(testAdmin);
	}

	@Test
	public void testHealthEndpoint() throws Exception {
		mvc.perform(get("/health")).andExpect(status().isOk());
	}

	@Test
	public void testTopicCrud() throws Exception {
		var integrationUtils = new IntegrationTestUtils(mapper, TOPIC_ROOT_PATH,
				"Bearer " + authService.createToken(testAdmin.getUsername()));

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
		var integrationUtils = new IntegrationTestUtils(mapper, WORD_ROOT_PATH,
				"Bearer " + authService.createToken(testAdmin.getUsername()));

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

	@Test
	public void testGetWordsByTopic() throws Exception {
		TopicOut topic1 = topicService.create(new TopicIn("Topic 1", ""));
		TopicOut topic2 = topicService.create(new TopicIn("Topic 2", ""));
		Map<Long, List<WordOut>> wordsByTopic = Stream.of(
				wordService.create(new WordIn(topic1.id(), "asdfsadf", Article.DER, "sdfasdf")),
				wordService.create(new WordIn(topic1.id(), "asdfsadf", Article.DER, "sdfasdf")),
				wordService.create(new WordIn(topic1.id(), "asdfsadf", Article.DER, "sdfasdf")),
				wordService.create(new WordIn(topic2.id(), "asdfsadf", Article.DER, "sdfasdf")),
				wordService.create(new WordIn(topic2.id(), "asdfsadf", Article.DER, "sdfasdf")),
				wordService.create(new WordIn(topic2.id(), "asdfsadf", Article.DER, "sdfasdf")),
				wordService.create(new WordIn(topic2.id(), "asdfsadf", Article.DER, "sdfasdf")),
				wordService.create(new WordIn(topic2.id(), "asdfsadf", Article.DER, "sdfasdf"))
		).collect(Collectors.groupingBy(WordOut::topicId));

		Set<WordOut> expectedResult1 = new HashSet<>(wordsByTopic.get(topic1.id()));
		Set<WordOut> expectedResult2 = new HashSet<>(wordsByTopic.get(topic2.id()));

		mvc.perform(get("/api/v1/topics/" + topic1.id() + "/words"))
				.andExpect(status().isOk())
				.andExpect(compareObjects(expectedResult1, this::parseToWordSet));
		mvc.perform(get("/api/v1/topics/" + topic2.id() + "/words"))
				.andExpect(status().isOk())
				.andExpect(compareObjects(expectedResult2, this::parseToWordSet));
	}

	@Test
	public void testDeleteWordsCascade() throws Exception {
		var integrationUtils = new IntegrationTestUtils(mapper, TOPIC_ROOT_PATH,
				"Bearer " + authService.createToken(testAdmin.getUsername()));

		var topicIn1 = new TopicIn("Topic 1", "First topic");
		var topic1 = topicService.create(topicIn1);

		var word1 = new WordIn(topic1.id(), "Katze", Article.DIE, "The Cat");
		var word2 = new WordIn(topic1.id(), "Hund", Article.DER, "The Dog");
		var word3 = new WordIn(topic1.id(), "Tisch", Article.DER, "The Table");
		var word4 = new WordIn(topic1.id(), "Stuhl", Article.DER, "The Chair");

		WordOut wordOut1 = wordService.create(word1);
		WordOut wordOut2 = wordService.create(word2);
		WordOut wordOut3 = wordService.create(word3);
		WordOut wordOut4 = wordService.create(word4);

		mvc.perform(integrationUtils.getDeleteRequest(topic1.id()))
				.andExpect(status().isOk())
				.andExpect(content().string("true"));

		System.out.println(wordService.getByTopic(topic1.id()));

		Assertions.assertFalse(wordService.exists(wordOut1.id()));
		Assertions.assertFalse(wordService.exists(wordOut2.id()));
		Assertions.assertFalse(wordService.exists(wordOut3.id()));
		Assertions.assertFalse(wordService.exists(wordOut4.id()));
	}


	@Test
	public void testAuth() throws Exception {
		var integrationUtils = new IntegrationTestUtils(mapper, WORD_ROOT_PATH, "");
		var user1 = new UserIn("Testtttt", "trasdfasd", "21sd3wefsdff");
		mvc.perform(integrationUtils.getRegisterRequest(user1))
				.andExpect(status().isOk());
		var login = new LoginIn(user1.username(), user1.password());
		mvc.perform(integrationUtils.getLoginRequest(login))
				.andExpect(status().isOk());
	}

	private <T> ResultMatcher compareObjects(T expected, Function<String, T> parseFunc) {
		return result -> {
			String content = result.getResponse().getContentAsString();
			T parsedContent = parseFunc.apply(content);
			Assertions.assertEquals(expected, parsedContent);
		};
	}

	private Set<WordOut> parseToWordSet(String content) {
		try {
			return mapper.readValue(content, new TypeReference<>() {
			});
		} catch (JsonProcessingException e) {
			return null;
		}
	}

}
