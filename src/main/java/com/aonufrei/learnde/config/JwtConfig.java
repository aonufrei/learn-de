package com.aonufrei.learnde.config;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

	@Bean
	public Algorithm createJwtAlgorithm(@Value("${jwt.secret}") String secret) {
		return Algorithm.HMAC256(secret);
	}

}
