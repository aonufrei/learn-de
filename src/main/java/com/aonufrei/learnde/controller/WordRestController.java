package com.aonufrei.learnde.controller;

import com.aonufrei.learnde.dto.WordIn;
import com.aonufrei.learnde.dto.WordOut;
import com.aonufrei.learnde.services.ValidationService;
import com.aonufrei.learnde.services.WordService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/words")
public class WordRestController {

	private final WordService service;
	private final ValidationService validationService;

	public WordRestController(WordService service, ValidationService validationService) {
		this.service = service;
		this.validationService = validationService;
	}

	@GetMapping
	private List<WordOut> getAll() {
		return service.getAll();
	}

	@GetMapping("{id}")
	private WordOut getById(@PathVariable("id") Long id) {
		return service.getById(id);
	}

	@PostMapping
	private WordOut create(@RequestBody WordIn wordIn) {
		ValidationService.validate(wordIn);
		validationService.validateTopicId(wordIn.topicId());
		return service.create(wordIn);
	}

	@PutMapping("{id}")
	private WordOut update(@PathVariable("id") Long id, @RequestBody WordIn wordIn) {
		ValidationService.validate(wordIn);
		validationService.validateTopicId(wordIn.topicId());
		return service.update(id, wordIn);
	}

	@DeleteMapping("{id}")
	private Boolean delete(@PathVariable Long id) {
		return service.delete(id);
	}

}
