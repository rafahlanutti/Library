package com.estudos.springboot.libraryapi.service;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {

	private static final String CRON_LATE_LOANS = "0 0 0 1/1 * ?";

	private final LoanService loanService;
	private final EmailService emailService;

	@Value("$email.message")
	private String message;

	@Scheduled(cron = CRON_LATE_LOANS)
	public void sendMailToLateLoans() {
		var loans = loanService.getAllLateLoans();
		var mailList = loans.stream().map(loan -> loan.getCustomerEmail()).collect(Collectors.toList());

		emailService.sendMails(message, mailList);
	}

}
