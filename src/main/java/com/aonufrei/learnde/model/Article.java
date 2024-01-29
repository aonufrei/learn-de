package com.aonufrei.learnde.model;

public enum Article {
	DER(0),
	DIE(1),
	DAS(2);

	private final int code;

	Article(int code) {
		this.code = code;
	}

	private int getCode() {
		return code;
	}
}
