package com.aonufrei.learnde.utils;

import com.aonufrei.learnde.dto.TopicIn;
import com.aonufrei.learnde.exceptions.FailedValidationException;
import org.springframework.stereotype.Service;

@Service
public class ValidationUtils {

	private static final int TOPIC_NAME_MIN_LENGTH = 5;
	private static final int TOPIC_NAME_MAX_LENGTH = 30;

	public static void validate(TopicIn ti) throws FailedValidationException {
		if (ti.name() == null) {
			throw new FailedValidationException("Topic name is required");
		}
		if (ti.name().length() < TOPIC_NAME_MIN_LENGTH) {
			final String msgTemplate = "Topic name should be longer than %d symbols";
			throw new FailedValidationException(String.format(msgTemplate, TOPIC_NAME_MIN_LENGTH));
		}
		if (ti.name().length() > TOPIC_NAME_MAX_LENGTH) {
			final String msgTemplate = "Topic name should be longer than %d symbols";
			throw new FailedValidationException(String.format(msgTemplate, TOPIC_NAME_MAX_LENGTH));
		}
	}

	public static void validate() throws FailedValidationException {
		// TODO: WordIn
	}

}
