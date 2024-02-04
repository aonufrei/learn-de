package com.aonufrei.learnde.controller;

import com.aonufrei.learnde.dto.TopicIn;
import com.aonufrei.learnde.dto.TopicOut;
import com.aonufrei.learnde.services.TopicService;
import com.aonufrei.learnde.services.ValidationService;
import jakarta.annotation.security.PermitAll;
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
@RequestMapping("api/v1/topics")
public class TopicRestController {

	private final TopicService service;

	public TopicRestController(TopicService service) {
		this.service = service;
	}

	@GetMapping
	@PermitAll
	private List<TopicOut> getAll() {
		return service.getAll();
	}

	@GetMapping("{id}")
	private TopicOut getById(@PathVariable("id") Long id) {
		return service.getById(id);
	}

	@PostMapping
	private TopicOut create(@RequestBody TopicIn topicIn) {
		ValidationService.validate(topicIn);
		return service.create(topicIn);
	}

	@PutMapping("{id}")
	private TopicOut update(@PathVariable("id") Long id, @RequestBody TopicIn topicIn) {
		ValidationService.validate(topicIn);
		return service.update(id, topicIn);
	}

	@DeleteMapping("{id}")
	private Boolean delete(@PathVariable("id") Long id) {
		return service.delete(id);
	}

}
