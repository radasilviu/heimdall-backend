package com.antonio.authserver.service;
import com.antonio.authserver.configuration.emailconfig.EmailProperties;
import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.entity.AppUser;
import com.antonio.authserver.mapper.AppUserMapper;
import com.antonio.authserver.model.CustomException;
import com.antonio.authserver.repository.AppUserRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

	@Mock
	private AppUserRepository appUserRepository;
	@Mock
	private EmailProperties emailProperties;
	@Mock
	private AppUserMapper appUserMapper;
	@Mock
	private FreeMarkerConfigurer freemarkerConfigurer;
	@InjectMocks
	private EmailService emailService;

	@Test
	void sendActivationEmail() {
	}
	@Test
	void withGivenEmailCode_shouldVerifyAndActivateAccount() {
		AppUser appUser = new AppUser("A","A",null,"a@gmail.com",false,"a");
		when(appUserRepository.findByEmailCode(anyString())).thenReturn(Optional.of(appUser));
		when(appUserMapper.toAppUserDto(any(AppUser.class))).thenReturn(new AppUserDto("A","A","A","A",null,"a@gmail.com",false,"a"));
		AppUserDto appUserDto = emailService.verifyAndActivateEmailCode("a");
		Assert.assertEquals(appUser.getEmail(),appUserDto.getEmail());
	}
	@Test
	void withGivenEmailCode_shouldReturnUserNotFoundException(){
		when(appUserRepository.findByEmailCode(anyString())).thenReturn(Optional.empty());
		CustomException exception = assertThrows(CustomException.class,() -> emailService.verifyAndActivateEmailCode("Dummy"));
		assertTrue(exception.getMessage().contains(" could not be found!"));
	}
	@Test
	void withGiveEmailCode_shouldReturnUserAlreadyActivatedException(){
		AppUser appUser = new AppUser("A","A",null,"a@gmail.com",true,"a");
		when(appUserRepository.findByEmailCode(anyString())).thenReturn(Optional.of(appUser));
		CustomException exception = assertThrows(CustomException.class,() -> emailService.verifyAndActivateEmailCode("A"));
		assertTrue(exception.getMessage().contains(" has been already activated."));
	}
	@Test
	void sendEmail() {
	}
	@Test
	void configureMailSender() {
	}
	@Test
	void configureMailMessage() {

	}

}