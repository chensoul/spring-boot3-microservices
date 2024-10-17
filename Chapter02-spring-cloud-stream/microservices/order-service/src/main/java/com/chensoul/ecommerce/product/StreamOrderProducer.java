package com.chensoul.ecommerce.product;

import com.chensoul.ecommerce.event.Event;
import static com.chensoul.ecommerce.event.Event.Type.ORDER;
import com.chensoul.ecommerce.order.OrderConfirmation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * TODO Comment
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since TODO
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class StreamOrderProducer implements OrderProducer {
  private final StreamBridge streamBridge;

  public void sendNotification(OrderConfirmation orderConfirmation) {
    sendMessage("notification-out-0", orderConfirmation);
  }

  private void sendMessage(String bindingName, OrderConfirmation orderConfirmation) {
    log.info("Sending a {} message to {}", orderConfirmation, bindingName);
    Message message = MessageBuilder.withPayload(new Event(ORDER, orderConfirmation.orderId(), orderConfirmation))
            .setHeader("partitionKey", orderConfirmation.orderId())
            .build();
    streamBridge.send(bindingName, message);
  }
}
