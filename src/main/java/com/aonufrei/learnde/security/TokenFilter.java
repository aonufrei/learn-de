package com.aonufrei.learnde.security;

import com.aonufrei.learnde.services.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class TokenFilter extends OncePerRequestFilter {

	private static final Logger log = LoggerFactory.getLogger(TokenFilter.class);

	private final String TOKEN_PREFIX = "Bearer ";

	private final AuthService authService;

	public TokenFilter(AuthService authService) {
		this.authService = authService;
	}

	@Override
	public void doFilterInternal(HttpServletRequest servletRequest,
								 HttpServletResponse servletResponse,
								 FilterChain filterChain)
			throws IOException, ServletException {
		log.info("Incoming request");
		var token = servletRequest.getHeader("Authorization");
		if (token != null && token.startsWith(TOKEN_PREFIX)) {
			token = token.substring(TOKEN_PREFIX.length());
			UserDetails details = processToken(token);
			var upAuth = new UsernamePasswordAuthenticationToken(
					details.getUsername(),
					details.getPassword(),
					details.getAuthorities()
			);
			upAuth.setDetails(new WebAuthenticationDetailsSource().buildDetails(servletRequest));
			SecurityContextHolder.getContext().setAuthentication(upAuth);
		}
		filterChain.doFilter(servletRequest, servletResponse);
	}

	private UserDetails processToken(String token) {
		return authService.loadUserByUsername(token);
	}

}
