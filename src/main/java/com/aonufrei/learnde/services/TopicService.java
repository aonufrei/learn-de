package com.aonufrei.learnde.services;

import com.aonufrei.learnde.repository.TopicRepository;

public class TopicService {

	private final TopicRepository topicRepository;

	public TopicService(TopicRepository topicRepository) {
		this.topicRepository = topicRepository;
	}
}
