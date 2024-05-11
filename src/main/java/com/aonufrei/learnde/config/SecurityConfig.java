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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

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
		http.cors(c -> c.configurationSource(corsConfigurationSource()));
		http.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		http.httpBasic(AbstractHttpConfigurer::disable);
		http.formLogin(AbstractHttpConfigurer::disable);
		http.authorizeHttpRequests(authz -> authz
				.requestMatchers(HttpMethod.GET, "/health").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/v1/auth/login", "/api/v1/auth/register").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/v1/topics","/api/v1/topics/*/words", "/api/v1/topics/*/words/shuffled").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/v1/words").permitAll()
				.anyRequest().hasRole("ADMIN")
		);
		http.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("*"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.addAllowedHeader("*");
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

}
