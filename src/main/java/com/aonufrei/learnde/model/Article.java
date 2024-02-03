package com.aonufrei.learnde.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum Article {
	DER(0),
	DIE(1),
	DAS(2);

	private final int code;

	Article(int code) {
		this.code = code;
	}

	@JsonValue
	public int getCode() {
		return code;
	}
}
