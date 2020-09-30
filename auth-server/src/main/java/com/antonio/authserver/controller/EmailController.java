package com.antonio.authserver.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.service.EmailService;
@Controller
public class EmailController {
	private EmailService emailService;
	@Autowired
	public EmailController(EmailService emailService) {
		this.emailService = emailService;
	}

	@GetMapping("/oauth/activate")
	public String activateAccount(@Param("emailCode") String emailCode) {
		AppUserDto appUserDto = emailService.verifyAndActivateEmailCode(emailCode);
		Boolean activated = appUserDto.getIsActivated();

		return activated ? "activate_success" : "activate_fail";
	}
	@GetMapping("favicon.ico")
	@ResponseBody
	void returnNoFavicon() {

	}
}
