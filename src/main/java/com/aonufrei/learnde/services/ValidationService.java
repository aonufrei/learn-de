package com.aonufrei.learnde.services;

import com.aonufrei.learnde.dto.LoginIn;
import com.aonufrei.learnde.dto.TopicIn;
import com.aonufrei.learnde.dto.UserIn;
import com.aonufrei.learnde.dto.WordIn;
import com.aonufrei.learnde.dto.validation.StringRange;
import com.aonufrei.learnde.exceptions.FailedValidationException;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {

	private static final StringRange TOPIC_NAME_RANGE = new StringRange(2, 30);
	private static final StringRange WORD_TEXT_RANGE = new StringRange(1, 30);
	private static final StringRange WORD_TRANSLATION_RANGE = new StringRange(1, 30);

	private static final StringRange USER_NAME_RANGE = new StringRange(1, 30);
	private static final StringRange USER_USERNAME_RANGE = new StringRange(5, 20);
	private static final StringRange USER_PASSWORD_RANGE = new StringRange(8, 50);

	private final TopicService topicService;

	public ValidationService(TopicService topicService) {
		this.topicService = topicService;
	}

	public static void validate(TopicIn ti) throws FailedValidationException {
		validateRequiredField(ti.name(), "Topic name is required");
		validateMinLength(ti.name(), TOPIC_NAME_RANGE.minLen(),
				String.format("Topic name should be longer than %d symbols", TOPIC_NAME_RANGE.minLen()));
		validateMaxLength(ti.name(), TOPIC_NAME_RANGE.maxLen(),
				String.format("Topic name should be shorter than %d symbols", TOPIC_NAME_RANGE.maxLen()));
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
		validateMinLength(wi.text(), WORD_TEXT_RANGE.minLen(),
				String.format(minLenErrTmp, "Word text", WORD_TEXT_RANGE.minLen()));
		validateMinLength(wi.translation(), WORD_TRANSLATION_RANGE.minLen(),
				String.format(minLenErrTmp, "Translation", WORD_TRANSLATION_RANGE.minLen()));

		var maxLenErrTmp = "%s length cannot be greater than %d";
		validateMaxLength(wi.text(), WORD_TEXT_RANGE.maxLen(),
				String.format(maxLenErrTmp, "Word text", WORD_TEXT_RANGE.maxLen()));
		validateMaxLength(wi.translation(), WORD_TRANSLATION_RANGE.maxLen(),
				String.format(maxLenErrTmp, "Translation", WORD_TRANSLATION_RANGE.maxLen()));
	}

	public static void validate(LoginIn loginIn) throws FailedValidationException {
		var emptyErrTmp = "%s cannot be empty";
		validateRequiredField(loginIn.username(), String.format(emptyErrTmp, "Username"));
		validateRequiredField(loginIn.password(), String.format(emptyErrTmp, "Password"));
	}

	public static void validate(UserIn userIn) throws FailedValidationException {
		var emptyErrTmp = "%s cannot be empty";
		validateRequiredField(userIn.name(), String.format(emptyErrTmp, "Name"));
		validateRequiredField(userIn.username(), String.format(emptyErrTmp, "Username"));
		validateRequiredField(userIn.password(), String.format(emptyErrTmp, "Password"));

		var minLenErrTmp = "%s length cannot be less than %d";
		validateMinLength(userIn.name(), USER_NAME_RANGE.minLen(),
				String.format(minLenErrTmp, "Name", USER_NAME_RANGE.minLen()));
		validateMinLength(userIn.username(), USER_USERNAME_RANGE.minLen(),
				String.format(minLenErrTmp, "Username", USER_USERNAME_RANGE.minLen()));
		validateMinLength(userIn.password(), USER_PASSWORD_RANGE.minLen(),
				String.format(minLenErrTmp, "Password", USER_PASSWORD_RANGE.minLen()));

		var maxLenErrTmp = "%s length cannot be greater than %d";
		validateMaxLength(userIn.name(), USER_NAME_RANGE.maxLen(),
				String.format(maxLenErrTmp, "Name", USER_NAME_RANGE.maxLen()));
		validateMaxLength(userIn.username(), USER_USERNAME_RANGE.maxLen(),
				String.format(maxLenErrTmp, "Username", USER_USERNAME_RANGE.maxLen()));
		validateMaxLength(userIn.password(), USER_PASSWORD_RANGE.maxLen(),
				String.format(maxLenErrTmp, "Password", USER_USERNAME_RANGE.maxLen()));

		// TODO: Password validation
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
