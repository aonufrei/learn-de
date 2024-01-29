package com.aonufrei.learnde.services;

import com.aonufrei.learnde.repository.WordRepository;

public class WordService {

	private final WordRepository wordRepository;

	public WordService(WordRepository wordRepository) {
		this.wordRepository = wordRepository;
	}
}
