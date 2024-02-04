package com.aonufrei.learnde.services;

import com.aonufrei.learnde.dto.UserIn;
import com.aonufrei.learnde.exceptions.FailedValidationException;
import com.aonufrei.learnde.model.User;
import com.aonufrei.learnde.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

	private final Logger log = LoggerFactory.getLogger(UserService.class);

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public void createUser(UserIn ui) {
		log.info("Creating new user");
		if (userRepository.findByUsername(ui.username()).isPresent()) {
			log.error("User with following username exists");
			throw new FailedValidationException("User with current username exists");
		}
		User toSave = toModel(ui);
		toSave.setRole("ROLE_REGULAR");
		toSave.setVersion(LocalDateTime.now());
		userRepository.save(toSave);
	}

	public User toModel(UserIn ui) {
		return User.builder()
				.name(ui.name())
				.username(ui.username())
				.password(passwordEncoder.encode(ui.password()))
				.build();
	}

}
