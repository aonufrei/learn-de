package com.aonufrei.learnde.dto;

import com.aonufrei.learnde.model.Article;

public record WordOut(Long id, Long topicId, String text, Article article, String translation) {
}
