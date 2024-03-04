package com.aonufrei.learnde.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Entity
public class Word {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "topic_id")
	private Long topicId;

	@ManyToOne
	@JoinColumn(name = "topic_id", referencedColumnName = "id", insertable = false, updatable = false)
	private Topic topic;

	private String text;

	private Article article;

	private String translation;

	private LocalDateTime version;

}
