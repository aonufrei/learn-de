package com.aonufrei.learnde.controller;

import com.aonufrei.learnde.dto.LoginIn;
import com.aonufrei.learnde.dto.UserIn;
import com.aonufrei.learnde.services.AuthService;
import com.aonufrei.learnde.services.UserService;
import com.aonufrei.learnde.services.ValidationService;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class AuthRestController {

	private final AuthService authService;
	private final AuthenticationManager authManager;
	private final UserService userService;

	@PostMapping("login")
	public ResponseEntity<String> login(@RequestBody LoginIn li) {
		ValidationService.validate(li);
		try {
			authManager.authenticate(new UsernamePasswordAuthenticationToken(
					li.username(),
					li.password()
			));
			return ResponseEntity.ok("Bearer " + authService.createToken(li.username()));
		} catch (BadCredentialsException ex) {
			System.out.println("Failed to login");
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}

	@PostMapping("register")
	public String register(@RequestBody UserIn ui) {
		ValidationService.validate(ui);
		userService.createUser(ui);
		return "ok";
	}
}
