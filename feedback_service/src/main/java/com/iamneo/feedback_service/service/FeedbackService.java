package com.iamneo.feedback_service.service;

import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.iamneo.feedback_service.dto.FeedbackRequest;
import com.iamneo.feedback_service.dto.FeedbackResponse;
import com.iamneo.feedback_service.entity.Feedback;
import com.iamneo.feedback_service.repository.FeedbackRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedbackService {
	private final FeedbackRepository feedbackRepository;
	private final JavaMailSender mailSender;
	public void sendEmail(String email,String feedback){
		MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setFrom("no-reply@example.com"); // Set the "no-reply" email address here
			helper.setTo(email);
			helper.setSubject("Feedback");
	
			// Create the HTML content of the email with the button and the image URL
			String imageUrl = "https://media.istockphoto.com/id/1256603011/photo/business-network-concept-customer-support-shaking-hands.jpg?s=612x612&w=0&k=20&c=uRYJ6c2EyNO92w0wQlCScEVwbdoyHaXzfIxw1q7zNLA=";
			String htmlContent = "<h1>Thank You for submitting feedback</h1>"
								+ "<p>Human resources isn't a thing we do. It's the thing that runs our business.</p>"
								+ "<img src='" + imageUrl + "'>";
	
			helper.setText(htmlContent, true);
	
			mailSender.send(message);
			System.out.println("MAIL SENT SUCCESSFULLY");
		} catch (MessagingException e) {
			System.out.println("Error sending HTML email: " + e.getMessage());
		}
	}
	public boolean addFeedback(FeedbackRequest feedbackRequest) {
		var feedback = Feedback.builder().email(feedbackRequest.getEmail()).feedback(feedbackRequest.getFeedback()).build();
		feedbackRepository.save(feedback);
		List<Feedback> feedbackData = feedbackRepository.findAll();
		// var email=Feedback.builder().email(feedbackRequest.getEmail()).build();
		String email=feedbackRequest.getEmail();
		String feedbackMsg=feedbackRequest.getFeedback();
		if(feedbackData.size() > 0) {
			sendEmail(email,feedbackMsg);
			return true;
		} else {
			return false;
		}

	}

	public List<FeedbackResponse> getFeedback() {
		List<Feedback> feedbacks = feedbackRepository.findAll();
		return feedbacks.stream().map(feedback -> mapToFeedbackResponse(feedback)).toList();
	}

	private FeedbackResponse mapToFeedbackResponse(Feedback feedback) {
		return FeedbackResponse.builder().email(feedback.getEmail()).feedback(feedback.getFeedback()).build();
	}

}
