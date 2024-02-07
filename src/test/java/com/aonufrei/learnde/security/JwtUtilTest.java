package com.aonufrei.learnde.security;

import com.aonufrei.learnde.config.JwtConfig;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

	private final JwtConfig jwtConfig = new JwtConfig();
	private final JwtUtil jwtUtil = new JwtUtil(jwtConfig.createJwtAlgorithm("testtesttesttest"));

	@Test
	public void testCreateRegularToken() {
		final String username = "username";
		String token = jwtUtil.createToken(username, getUtcNow().plusHours(1));
		assertEquals(username, jwtUtil.getUsernameFromJwt(token));
	}

	@Test
	public void testCreateExpiredToken() {
		final String username = "username";
		String token = jwtUtil.createToken(username, getUtcNow().minusHours(1));
		assertThrows(TokenExpiredException.class, () -> jwtUtil.getUsernameFromJwt(token));
	}

	private LocalDateTime getUtcNow() {
		return LocalDateTime.ofInstant(Instant.now(), ZoneId.of("UTC"));
	}

}