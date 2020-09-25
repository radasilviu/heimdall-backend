package com.antonio.authserver.service;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.model.exceptions.controllerexceptions.UserNotFound;
import com.antonio.authserver.repository.AppUserRepository;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
@Service
public class EmailService {

	private JavaMailSender javaMailSender;
	private AppUserRepository appUserRepository;
	private Configuration configuration;
	@Autowired
	private FreeMarkerConfigurer freemarkerConfigurer;

	@Autowired
	public EmailService(JavaMailSender javaMailSender, AppUserRepository appUserRepository,
			Configuration configuration) {
		this.javaMailSender = javaMailSender;
		this.appUserRepository = appUserRepository;
		this.configuration = configuration;
	}

	public void sendEmail(AppUserDto appUserDto, String siteUrl)
			throws IOException, TemplateException, MessagingException {
		String verifyUrl = siteUrl + "/activate?emailCode=" + appUserDto.getEmailCode();
		JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
		mailSenderImpl.setHost("smtp.mailtrap.io");
		mailSenderImpl.setPort(2525);
		mailSenderImpl.setUsername("98939673ff12ef");
		mailSenderImpl.setPassword("4425af6ec5ec9d");
		MimeMessage message = javaMailSender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(message);

		Map<String, Object> model = new HashMap<>();
		model.put("Name", "Heimdall Team");
		model.put("VerifyUrl", verifyUrl);
		model.put("Username", appUserDto.getUsername());

		Template freemarkerTemplate = freemarkerConfigurer.createConfiguration().getTemplate("email.ftl");
		String htmlBody = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerTemplate, model);

		helper.setTo(appUserDto.getEmail());
		helper.setText(htmlBody, true);
		helper.setSubject("Activate your account");
		helper.setFrom("HeimdallTeam@gmail.com");
		javaMailSender.send(message);
	}

	public Boolean verifyAndActivateEmailCode(String emailCode) {
		AppUser appUser = appUserRepository.findByEmailCode(emailCode).orElseThrow(() -> new UserNotFound("Unknown"));
		if (appUser == null || appUser.getIsActivated()) {
			return false;
		} else {
			appUserRepository.activate(appUser.getUsername());
			return true;
		}
	}
}
