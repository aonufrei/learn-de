package com.aonufrei.learnde.repository;

import com.aonufrei.learnde.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

	List<Word> findAllByTopicId(Long id);

}
