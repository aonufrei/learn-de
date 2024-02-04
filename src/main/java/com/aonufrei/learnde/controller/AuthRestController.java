package com.aonufrei.learnde.controller;

import com.aonufrei.learnde.dto.LoginIn;
import com.aonufrei.learnde.dto.UserIn;
import com.aonufrei.learnde.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
public class AuthRestController {

	private final AuthenticationManager authManager;
	private final UserService userService;

	public AuthRestController(AuthenticationManager authManager, UserService userService) {
		this.authManager = authManager;
		this.userService = userService;
	}

	@PostMapping("login")
	public ResponseEntity<String> login(@RequestBody LoginIn li) {
		try {
			authManager.authenticate(new UsernamePasswordAuthenticationToken(
					li.username(),
					li.password()
			));
			return ResponseEntity.ok("Bearer " + li.username());
		} catch (BadCredentialsException ex) {
			System.out.println("Failed to login");
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}

	@PostMapping("register")
	public String register(@RequestBody UserIn ui) {
		userService.createUser(ui);
		return "ok";
	}
}
