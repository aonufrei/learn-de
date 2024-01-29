package com.aonufrei.learnde.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Topic {

	private Long id;

	private String name;

	private String description;

	private LocalDateTime version;

}
