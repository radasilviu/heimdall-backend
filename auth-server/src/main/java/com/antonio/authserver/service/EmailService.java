package com.antonio.authserver.service;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import com.antonio.authserver.configuration.emailconfig.EmailProperties;
import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.mapper.AppUserMapper;
import com.antonio.authserver.model.CustomException;
import com.antonio.authserver.repository.AppUserRepository;
import freemarker.template.Template;
import freemarker.template.TemplateException;
@Service
public class EmailService{

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

	public void sendActivationEmail(AppUserDto appUserDto, String siteUrl)
			throws IOException, TemplateException, MessagingException {
		String verifyUrl = siteUrl + "/oauth/activate?emailCode=" + appUserDto.getEmailCode();

		Map<String, Object> model = new HashMap<>();
		model.put("Name", "Heimdall Team");
		model.put("VerifyUrl", verifyUrl);
		model.put("Username", appUserDto.getUsername());

		new Thread(() -> sendEmail("email.ftl", model, appUserDto.getEmail(), "Activate your account",
				emailProperties.getUsername())).start();
	}

	public AppUserDto verifyAndActivateEmailCode(String emailCode) throws CustomException {
		AppUser appUser = appUserRepository.findByEmailCode(emailCode).orElseThrow(() -> new CustomException(
				"User with the emailCode [ " + emailCode + " ] could not be found!", HttpStatus.NOT_FOUND));
		if (appUser.getIsActivated()) {
			throw new CustomException(
					"User account with the username " + appUser.getUsername() + " has been already activated.",
					HttpStatus.CONFLICT);
		} else {
			appUser.setIsActivated(true);
			return appUserMapper.toAppUserDto(appUser);
		}
	}

	public void sendEmail(String template, Object model, String to, String subject, String from) {
		JavaMailSenderImpl javaMailSender = configureMailSender();
		MimeMessage message = javaMailSender.createMimeMessage();

		try {
			Template htmlTemplate = freemarkerConfigurer.createConfiguration().getTemplate(template);
			String htmlBody = FreeMarkerTemplateUtils.processTemplateIntoString(htmlTemplate, model);
			message = configureMailMessage(message, to, htmlBody, subject, from);

			javaMailSender.send(message);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
	}

	public JavaMailSenderImpl configureMailSender() {
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

		Properties props = new Properties();
		props.put("mail.smtp.starttls.enable", "true");
		javaMailSender.setJavaMailProperties(props);
		javaMailSender.setHost(emailProperties.getHost());
		javaMailSender.setPort(emailProperties.getPort());
		javaMailSender.setUsername(emailProperties.getUsername());
		javaMailSender.setPassword(emailProperties.getPassword());

		return javaMailSender;
	}

	public MimeMessage configureMailMessage(MimeMessage message, String to, String body, String subject, String from) {
		MimeMessageHelper helper = new MimeMessageHelper(message);

		try {
			helper.setTo(to);
			helper.setText(body, true);
			helper.setSubject(subject);
			helper.setFrom(from);
		} catch (MessagingException e) {
			e.printStackTrace();
		}

		return message;
	}

}
