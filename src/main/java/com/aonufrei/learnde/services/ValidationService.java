package com.aonufrei.learnde.services;

import com.aonufrei.learnde.dto.TopicIn;
import com.aonufrei.learnde.dto.WordIn;
import com.aonufrei.learnde.exceptions.FailedValidationException;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {

	private static final int TOPIC_NAME_MIN_LENGTH = 5;
	private static final int TOPIC_NAME_MAX_LENGTH = 30;

	private static final int WORD_TEXT_MIN_LENGTH = 1;
	private static final int WORD_TRANSLATION_MIN_LENGTH = 1;
	private static final int WORD_TEXT_MAX_LENGTH = 60;
	private static final int WORD_TRANSLATION_MAX_LENGTH = 60;

	private final TopicService topicService;

	public ValidationService(TopicService topicService) {
		this.topicService = topicService;
	}

	public static void validate(TopicIn ti) throws FailedValidationException {
		validateRequiredField(ti.name(), "Topic name is required");
		validateMinLength(ti.name(), TOPIC_NAME_MIN_LENGTH,
				String.format("Topic name should be longer than %d symbols", TOPIC_NAME_MIN_LENGTH));
		validateMaxLength(ti.name(), TOPIC_NAME_MAX_LENGTH,
				String.format("Topic name should be shorter than %d symbols", TOPIC_NAME_MAX_LENGTH));
	}

	public void validateTopicId(Long id) {
		if (!topicService.exists(id)) {
			throw new FailedValidationException("Topic does not exist");
		}
	}

	public static void validate(WordIn wi) throws FailedValidationException {
		var emptyErrTmp = "%s cannot be empty";
		validateRequiredField(wi.topicId(), String.format(emptyErrTmp, "Topic id"));
		validateRequiredField(wi.text(), String.format(emptyErrTmp, "Word text"));
		validateRequiredField(wi.article(), String.format(emptyErrTmp, "Article"));
		validateRequiredField(wi.translation(), String.format(emptyErrTmp, "Translation"));

		var minLenErrTmp = "%s length cannot be less than %d";
		validateMinLength(wi.text(), WORD_TEXT_MIN_LENGTH,
				String.format(minLenErrTmp, "Word text", WORD_TEXT_MIN_LENGTH));
		validateMinLength(wi.translation(), WORD_TRANSLATION_MIN_LENGTH,
				String.format(minLenErrTmp, "Translation", WORD_TRANSLATION_MIN_LENGTH));

		var maxLenErrTmp = "%s length cannot be greater than %d";
		validateMaxLength(wi.text(), WORD_TEXT_MAX_LENGTH,
				String.format(maxLenErrTmp, "Word text", WORD_TEXT_MAX_LENGTH));
		validateMaxLength(wi.translation(), WORD_TRANSLATION_MAX_LENGTH,
				String.format(maxLenErrTmp, "Translation", WORD_TRANSLATION_MAX_LENGTH));
	}

	public static <T> void validateRequiredField(T value, String errMsg) {
		if (value == null) {
			throw new FailedValidationException(errMsg);
		}
	}

	public static void validateMinLength(String value, int minLength, String errMsg) {
		if (value.length() < minLength) {
			throw new FailedValidationException(errMsg);
		}
	}

	public static void validateMaxLength(String value, int maxLength, String errMsg) {
		if (value.length() > maxLength) {
			throw new FailedValidationException(errMsg);
		}
	}

}
