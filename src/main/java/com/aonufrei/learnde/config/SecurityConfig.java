package com.aonufrei.learnde.config;

import com.aonufrei.learnde.security.TokenFilter;
import com.aonufrei.learnde.services.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final TokenFilter tokenFilter;

	public SecurityConfig(AuthService userService) {
		this.tokenFilter = new TokenFilter(userService);
	}

	@Bean
	public SecurityFilterChain createFilterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception {
		http.authenticationManager(authManager);
		http.csrf(AbstractHttpConfigurer::disable);
		http.httpBasic(AbstractHttpConfigurer::disable);
		http.formLogin(AbstractHttpConfigurer::disable);
		http.authorizeHttpRequests(authz -> authz
				.requestMatchers(HttpMethod.GET, "/health").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/v1/auth/login", "/api/v1/auth/register").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/v1/topics", "/api/v1/topics/*/words").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/v1/words").permitAll()
				.anyRequest().hasRole("ADMIN")
		);
		http.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);
		var chain = http.build();
		System.out.println(chain.getFilters());
		return chain;
	}

}
