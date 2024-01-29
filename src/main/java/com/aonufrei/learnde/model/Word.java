package com.aonufrei.learnde.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Word {

	private Long id;

	private Long topicId;

	private String text;

	private Article article;

	private String translation;

	private LocalDateTime version;

}
