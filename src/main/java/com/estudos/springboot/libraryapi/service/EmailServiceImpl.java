package com.estudos.springboot.libraryapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

	@Autowired
	private JavaMailSender javaMailSendler;

	@Value("$email.remetente")
	private String defaultRemetente;

	@Override
	public void sendMails(String message, List<String> mailList) {
		SimpleMailMessage messageList = new SimpleMailMessage();
		messageList.setSubject(defaultRemetente);

		messageList.setText(message);
		String[] mails = mailList.toArray(new String[mailList.size()]);
		messageList.setTo(mails);

		javaMailSendler.send(messageList);

	}

}
