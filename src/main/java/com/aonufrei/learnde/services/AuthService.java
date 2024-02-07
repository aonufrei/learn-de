package com.aonufrei.learnde.services;

import com.aonufrei.learnde.repository.UserRepository;
import com.aonufrei.learnde.security.JwtUtil;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;

@Service
public class AuthService implements UserDetailsService {


	private final Duration TOKEN_EXPIRATION = Duration.of(7, ChronoUnit.DAYS);
	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;

	public AuthService(JwtUtil jwtUtil, UserRepository userRepository) {
		this.jwtUtil = jwtUtil;
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		var user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
		return new UserDetails() {
			@Override
			public Collection<? extends GrantedAuthority> getAuthorities() {
				return List.of((GrantedAuthority) user::getRole);
			}

			@Override
			public String getPassword() {
				return user.getPassword();
			}

			@Override
			public String getUsername() {
				return user.getUsername();
			}
		};
	}

	public String createToken(String username) {
		return jwtUtil.createToken(username, LocalDateTime.now().plus(TOKEN_EXPIRATION));
	}

	public UserDetails getUserDetails(String token) {
		String username = jwtUtil.getUsernameFromJwt(token);
		return loadUserByUsername(username);
	}

}
