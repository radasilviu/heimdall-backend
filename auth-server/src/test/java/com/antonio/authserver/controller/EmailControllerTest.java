package com.antonio.authserver.controller;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;
import javax.mail.MessagingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.service.EmailService;
import freemarker.template.TemplateException;
@ExtendWith(MockitoExtension.class)
class EmailControllerTest {
	@Mock
	private EmailService emailService;
	private EmailController emailController;
	@BeforeEach
	void setUp() {
		emailService = mock(EmailService.class);
		emailController = new EmailController(emailService);
	}
	@Test
	void activateAccount_callsVerifyAndActivateEmailCode() throws TemplateException, IOException, MessagingException {
		emailController.activateAccount(anyString());
		when(any(AppUserDto.class).getIsActivated()).thenReturn(true);
		verify(emailService, times(1)).sendActivationEmail(any(AppUserDto.class), anyString());
	}
	@Test
	void returnNoFavicon() {
	}
}