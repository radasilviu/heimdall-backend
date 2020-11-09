package com.antonio.authserver.controller;

import com.antonio.authserver.repository.RealmRepository;
import com.antonio.authserver.request.ForgotPasswordRequest;
import com.antonio.authserver.request.ChangePasswordRequest;
import com.antonio.authserver.request.ProfileLoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.model.Code;
import com.antonio.authserver.model.JwtObject;
import com.antonio.authserver.model.LoginCredential;
import com.antonio.authserver.model.ResponseMessage;
import com.antonio.authserver.request.ClientLoginRequest;
import com.antonio.authserver.service.AuthService;
import com.antonio.authserver.service.EmailService;
import com.antonio.authserver.service.UserService;
import com.antonio.authserver.utils.EmailUtility;
import freemarker.template.TemplateException;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("oauth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    private RealmRepository realmRepository;
    @Autowired
    public AuthController(RealmRepository realmRepository) {
        this.realmRepository = realmRepository;
    }
    @PostMapping(path = "/client-login")
    public ResponseEntity<?> clientLogin(@RequestBody ClientLoginRequest loginRequest) {
        Code code = authService.getCode(loginRequest);
        authService.checkIfAccountIsActivated(loginRequest);
        if (code != null) {
            return ResponseEntity.ok().body(code);
        }
        final ResponseMessage responseMessage = new ResponseMessage("Failed to generate JWT Code");
        return ResponseEntity.unprocessableEntity().body(responseMessage);
    }
    @PostMapping(path = "/token")
    public ResponseEntity<?> getToken(@RequestBody LoginCredential loginCredential) {
        JwtObject jwtObject = authService.login(loginCredential);
        return ResponseEntity.ok().body(jwtObject);
    }

    @PutMapping(path = "/refreshToken")
    public ResponseEntity<?> getNewTokenByRefreshToken(@RequestBody JwtObject refreshToken) {
        JwtObject jwtObject = authService.generateNewAccessToken(refreshToken);
        return ResponseEntity.ok().body(jwtObject);
    }

    @PostMapping(path = "/token/delete")
    public ResponseEntity<?> deleteToken(@RequestBody JwtObject jwtObject) {
        authService.logout(jwtObject);
        final ResponseMessage responseMessage = new ResponseMessage("User loogged out");
        return ResponseEntity.ok().body(responseMessage);
    }

    @GetMapping(path = "/access")
    public ResponseEntity<?> verifyToken() {
        final ResponseMessage responseMessage = new ResponseMessage("Valid Token");
        return ResponseEntity.ok().body(responseMessage);
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody AppUserDto user, HttpServletRequest httpServletRequest)
            throws TemplateException, IOException, MessagingException {
        String siteUrl = EmailUtility.getSiteUrl(httpServletRequest);
        userService.create(realmRepository.findByName("master0").get().getName(),user); ///NEEDS FIX (REGISTERS ALL NEW USERS TO MASTER0)
        emailService.sendActivationEmail(user, siteUrl);
        final ResponseMessage responseMessage = new ResponseMessage("User successfully saved");
        return ResponseEntity.ok().body(responseMessage);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> sendForgotPasswordEmail(@RequestBody ForgotPasswordRequest request) {
        this.authService.sendForgotPasswordEmail(request.getEmail());
        final ResponseMessage responseMessage = new ResponseMessage("Email sent");
        return ResponseEntity.ok().body(responseMessage);
    }

	@PostMapping("/change-password")
	public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
		this.authService.changePassword(request.getPassword(), request.getConfirmPassword(), request.getEmail(), request.getForgotPasswordCode());
		final ResponseMessage responseMessage = new ResponseMessage("Password change");
		return ResponseEntity.ok().body(responseMessage);
	}

	@PostMapping(path = "/user-profile/login")
	public ResponseEntity<?> profileLogin(@RequestBody ProfileLoginRequest profileLoginRequest) {
		JwtObject jwt = authService.profileLogin(profileLoginRequest.getUsername(), profileLoginRequest.getPassword(),
				profileLoginRequest.getRealm());

		if (jwt != null) {
			return ResponseEntity.ok().body(jwt);
		}
		final ResponseMessage responseMessage = new ResponseMessage("Failed to generate JWT Code");
		return ResponseEntity.unprocessableEntity().body(responseMessage);
	}
}
