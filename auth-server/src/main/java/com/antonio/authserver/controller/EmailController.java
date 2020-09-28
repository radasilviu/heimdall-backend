package com.antonio.authserver.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.service.EmailService;
@Controller
@RequestMapping("/oauth")
public class EmailController {
	private EmailService emailService;
	@Autowired
	public EmailController(EmailService emailService) {
		this.emailService = emailService;
	}
	@CrossOrigin("http://localhost:8081")
	@GetMapping("/activate")
	public String activateAccount(@Param("emailCode") String emailCode) {
		AppUserDto appUserDto = emailService.verifyAndActivateEmailCode(emailCode);
		Boolean activated = appUserDto.getIsActivated();

		return activated ? "activate_success" : "activate_fail";
	}
}
