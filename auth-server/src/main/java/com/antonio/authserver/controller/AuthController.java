package com.antonio.authserver.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.antonio.authserver.model.Code;
import com.antonio.authserver.model.JwtObject;
import com.antonio.authserver.model.LoginCredential;
import com.antonio.authserver.model.ResponseMessage;
import com.antonio.authserver.request.ClientLoginRequest;
import com.antonio.authserver.service.AuthService;
import com.antonio.authserver.service.EmailService;

@RestController
@RequestMapping("/oauth")
public class AuthController {

	@Autowired
	private AuthService authService;
	@Autowired
	private EmailService emailService;

	@CrossOrigin("http://localhost:4201")
	@PostMapping(path = "/client-login")
	public ResponseEntity<?> clientLogin(@RequestBody ClientLoginRequest loginRequest) {
		Code code = authService.getCode(loginRequest);

		if (code != null) {
			return ResponseEntity.ok().body(code);
		}
		final ResponseMessage responseMessage = new ResponseMessage("Failed to generate JWT Code");
		return ResponseEntity.unprocessableEntity().body(responseMessage);
	}

	@CrossOrigin("http://localhost:4201")
	@PostMapping(path = "/token")
	public ResponseEntity<?> getToken(@RequestBody LoginCredential loginCredential) {
		JwtObject jwtObject = authService.login(loginCredential);
		return ResponseEntity.ok().body(jwtObject);
	}

	@CrossOrigin("http://localhost:4201")
	@PostMapping(path = "/token/delete")
	public ResponseEntity<?> deleteToken(@RequestBody JwtObject jwtObject) {
		authService.logout(jwtObject);
		final ResponseMessage responseMessage = new ResponseMessage("User logged out");
		return ResponseEntity.ok().body(responseMessage);
	}

	@CrossOrigin("http://localhost:8080")
	@GetMapping(path = "/access")
	public ResponseEntity<?> verifyToken() {
		final ResponseMessage responseMessage = new ResponseMessage("Valid Token");
		return ResponseEntity.ok().body(responseMessage);
	}

	// Move to service
	@CrossOrigin("http://localhost:8081")
	@GetMapping("/activate")
	public String activateAccount(@Param("emailCode") String emailCode) {
		Boolean verified = emailService.verifyAndActivateEmailCode(emailCode);
		if (verified)
			return "redirect:localhost:4200/home";
		else
			return "redirect:localhost:4200/home/users";
	}

}
