package com.chensoul.ecommerce.email;

import static com.chensoul.ecommerce.email.EmailTemplates.ORDER_CONFIRMATION;
import static com.chensoul.ecommerce.email.EmailTemplates.PAYMENT_CONFIRMATION;
import com.chensoul.ecommerce.product.ProductResponse;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

  private final JavaMailSender mailSender;
  private final SpringTemplateEngine templateEngine;

  @Async
  public void sendPaymentSuccessEmail(
          String destinationEmail,
          String customerName,
          BigDecimal amount,
          Integer orderId
  ) throws MessagingException {

    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, UTF_8.name());
    messageHelper.setFrom("contact@chensoul.com");

    final String templateName = PAYMENT_CONFIRMATION.getTemplate();

    Map<String, Object> variables = new HashMap<>();
    variables.put("customerName", customerName);
    variables.put("amount", amount);
    variables.put("orderId", orderId);

    Context context = new Context();
    context.setVariables(variables);
    messageHelper.setSubject(PAYMENT_CONFIRMATION.getSubject());

    try {
      String htmlTemplate = templateEngine.process(templateName, context);
      messageHelper.setText(htmlTemplate, true);

      messageHelper.setTo(destinationEmail);
      mailSender.send(mimeMessage);
      log.info(String.format("INFO - Email successfully sent to %s with template %s ", destinationEmail, templateName));
    } catch (MessagingException e) {
      log.warn("WARNING - Cannot send Email to {} ", destinationEmail);
    }

  }

  @Async
  public void sendOrderConfirmationEmail(
          String destinationEmail,
          String customerName,
          BigDecimal amount,
          Integer orderId,
          List<ProductResponse> productResponses
  ) throws MessagingException {

    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, UTF_8.name());
    messageHelper.setFrom("contact@chensoul.com");

    final String templateName = ORDER_CONFIRMATION.getTemplate();

    Map<String, Object> variables = new HashMap<>();
    variables.put("customerName", customerName);
    variables.put("totalAmount", amount);
    variables.put("orderId", orderId);
    variables.put("products", productResponses);

    Context context = new Context();
    context.setVariables(variables);
    messageHelper.setSubject(ORDER_CONFIRMATION.getSubject());

    try {
      String htmlTemplate = templateEngine.process(templateName, context);
      messageHelper.setText(htmlTemplate, true);

      messageHelper.setTo(destinationEmail);
      mailSender.send(mimeMessage);
      log.info(String.format("INFO - Email successfully sent to %s with template %s ", destinationEmail, templateName));
    } catch (MessagingException e) {
      log.warn("WARNING - Cannot send Email to {} ", destinationEmail);
    }

  }
}
