package com.aonufrei.learnde.services;

import com.aonufrei.learnde.dto.TopicIn;
import com.aonufrei.learnde.dto.TopicOut;
import com.aonufrei.learnde.exceptions.TopicNotFoundException;
import com.aonufrei.learnde.model.Topic;
import com.aonufrei.learnde.repository.TopicRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class TopicService {

	private static final Logger log = LoggerFactory.getLogger(TopicService.class);

	private final TopicRepository topicRepository;

	public TopicService(TopicRepository topicRepository) {
		this.topicRepository = topicRepository;
	}

	public List<TopicOut> getAll() {
		return topicRepository.findAll().stream()
				.map(TopicService::toTopicOut)
				.toList();
	}

	public List<TopicOut> getShuffled(Integer seed) {
		List<TopicOut> all = new ArrayList<>(getAll());
		Collections.shuffle(all, new Random(seed));
		return all;
	}

	public Topic getModelById(Long id) {
		return topicRepository.findById(id)
				.orElseThrow(() -> createNotFoundByIdError(id));
	}

	public TopicOut getById(Long id) {
		return toTopicOut(getModelById(id));
	}

	public TopicOut create(TopicIn ti) {
		Topic topic = toTopic(ti);
		topic.setVersion(LocalDateTime.now());
		Topic saved = topicRepository.save(topic);
		return toTopicOut(saved);
	}

	@Transactional
	public TopicOut update(Long id, TopicIn ti) {
		Topic modelById = getModelById(id);
		modelById.setName(ti.name());
		modelById.setDescription(ti.description());
		modelById.setVersion(LocalDateTime.now());
		Topic updated = topicRepository.save(modelById);
		return toTopicOut(updated);
	}

	@Transactional
	public boolean delete(Long id) {
		if (exists(id)) {
			log.info("Found topic with id [{}]", id);
			topicRepository.deleteById(id);
			return true;
		}
		log.info("Topic with id [{}] was not found", id);
		return false;
	}

	public boolean exists(Long id) {
		return topicRepository.existsById(id);
	}

	public static TopicOut toTopicOut(Topic t) {
		return new TopicOut(t.getId(), t.getName(), t.getDescription());
	}

	public static Topic toTopic(TopicIn ti) {
		return Topic.builder()
				.name(ti.name())
				.description(ti.description())
				.build();
	}

	private TopicNotFoundException createNotFoundByIdError(Long id) {
		return new TopicNotFoundException(String.format("Topic with id [%d] was not found", id));
	}
}
