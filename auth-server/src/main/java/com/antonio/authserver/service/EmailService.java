package com.antonio.authserver.service;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import com.antonio.authserver.configuration.emailconfig.EmailProperties;
import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.mapper.AppUserMapper;
import com.antonio.authserver.model.exceptions.controllerexceptions.UserAccountIsAlreadyActivated;
import com.antonio.authserver.model.exceptions.controllerexceptions.UserNotFound;
import com.antonio.authserver.repository.AppUserRepository;
import freemarker.template.Template;
import freemarker.template.TemplateException;
@Service
public class EmailService {
	private AppUserRepository appUserRepository;
	private EmailProperties emailProperties;
	private AppUserMapper appUserMapper;
	@Autowired
	private FreeMarkerConfigurer freemarkerConfigurer;

	@Autowired
	public EmailService(AppUserRepository appUserRepository, EmailProperties emailProperties,
			AppUserMapper appUserMapper) {
		this.appUserRepository = appUserRepository;
		this.emailProperties = emailProperties;
		this.appUserMapper = appUserMapper;
	}

	public void sendEmail(AppUserDto appUserDto, String siteUrl)
			throws IOException, TemplateException, MessagingException {
		String verifyUrl = siteUrl + "/activate?emailCode=" + appUserDto.getEmailCode();
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

		Properties props = new Properties();
		props.put("mail.smtp.starttls.enable", "true");
		javaMailSender.setJavaMailProperties(props);
		javaMailSender.setHost(emailProperties.getHost());
		javaMailSender.setPort(emailProperties.getPort());
		javaMailSender.setUsername(emailProperties.getUsername());
		javaMailSender.setPassword(emailProperties.getPassword());

		MimeMessage message = javaMailSender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(message);

		Map<String, Object> model = new HashMap<>();
		model.put("Name", "Heimdall Team");
		model.put("VerifyUrl", verifyUrl);
		model.put("Username", appUserDto.getUsername());

		Template freemarkerTemplate = freemarkerConfigurer.createConfiguration().getTemplate("email.ftl");
		String htmlBody = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerTemplate, model);

		helper.setTo("octavianp555@gmail.com"); // !!! IMPORTANT CHANGE
		helper.setText(htmlBody, true);
		helper.setSubject("Activate your account");
		helper.setFrom("heimdallteam0@gmail.com");
		javaMailSender.send(message);
	}

	public AppUserDto verifyAndActivateEmailCode(String emailCode) {
		AppUser appUser = appUserRepository.findByEmailCode(emailCode).orElseThrow(() -> new UserNotFound("Unknown"));
		if (appUser == null || appUser.getIsActivated()) {
			throw new UserAccountIsAlreadyActivated(appUser.getUsername());
		} else {
			appUser.setIsActivated(true);
			return appUserMapper.toAppUserDto(appUser);
		}
	}
}
