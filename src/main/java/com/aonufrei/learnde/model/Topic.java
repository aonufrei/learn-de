package com.aonufrei.learnde.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Entity
public class Topic {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String description;

	@OneToMany(mappedBy = "topicId", targetEntity = Word.class, cascade = CascadeType.ALL)
	private List<Word> words;

	private LocalDateTime version;

}
