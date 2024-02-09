package com.aonufrei.learnde.services;

import com.aonufrei.learnde.dto.WordIn;
import com.aonufrei.learnde.dto.WordOut;
import com.aonufrei.learnde.exceptions.WordNotFoundException;
import com.aonufrei.learnde.model.Word;
import com.aonufrei.learnde.repository.WordRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WordService {

	private static final Logger log = LoggerFactory.getLogger(WordService.class);

	private final WordRepository wordRepository;

	public WordService(WordRepository wordRepository) {
		this.wordRepository = wordRepository;
	}

	public List<WordOut> getAll() {
		return wordRepository.findAll().stream()
				.map(WordService::toWordOut)
				.toList();
	}

	public Word getModelById(Long id) {
		return wordRepository.findById(id).orElseThrow(() -> createNotFoundByIdError(id));
	}

	public WordOut getById(Long id) {
		return toWordOut(getModelById(id));
	}

	public List<WordOut> getByTopic(Long topicId) {
		return wordRepository.findAllByTopicId(topicId).stream()
				.map(WordService::toWordOut)
				.toList();
	}

	public WordOut create(WordIn wi) {
		Word word = toWord(wi);
		word.setVersion(LocalDateTime.now());
		Word saved = wordRepository.save(word);
		return toWordOut(saved);
	}

	@Transactional
	public WordOut update(Long id, WordIn wi) {
		Word word = getModelById(id);
		word.setTopicId(wi.topicId());
		word.setText(wi.text());
		word.setArticle(wi.article());
		word.setTranslation(wi.translation());
		word.setVersion(LocalDateTime.now());
		Word updated = wordRepository.save(word);
		return toWordOut(updated);
	}

	@Transactional
	public boolean delete(Long id) {
		if (wordRepository.existsById(id)) {
			log.info("Found word with id [{}]", id);
			wordRepository.deleteById(id);
			return true;
		}
		log.info("Word with id [{}] was not found", id);
		return false;
	}

	public static WordOut toWordOut(Word word) {
		return new WordOut(
				word.getId(),
				word.getTopicId(),
				word.getText(),
				word.getArticle(),
				word.getTranslation()
		);
	}

	public static Word toWord(WordIn wi) {
		return Word.builder()
				.topicId(wi.topicId())
				.text(wi.text())
				.translation(wi.translation())
				.article(wi.article())
				.build();
	}

	private WordNotFoundException createNotFoundByIdError(Long id) {
		return new WordNotFoundException(String.format("Word with id [%d] was not found", id));
	}
}
