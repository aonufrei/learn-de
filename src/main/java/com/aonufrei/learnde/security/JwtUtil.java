package com.aonufrei.learnde.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class JwtUtil {

	public static final String JWT_CLAIM = "username";

	private final Algorithm algorithm;

	public JwtUtil(Algorithm algorithm) {
		this.algorithm = algorithm;
	}

	public String createToken(String username, LocalDateTime expiredAt) {
		return JWT.create()
				.withClaim(JWT_CLAIM, username)
				.withExpiresAt(expiredAt.toInstant(ZoneOffset.UTC))
				.sign(algorithm);
	}

	public String getUsernameFromJwt(String token) throws JWTVerificationException {
		DecodedJWT decodedJwt = createVerifier().verify(token);
		return decodedJwt.getClaim(JWT_CLAIM).asString();
	}

	public JWTVerifier createVerifier() {
		return JWT.require(algorithm).build();
	}
}
