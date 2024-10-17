package com.chensoul.ecommerce.event;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import java.time.ZonedDateTime;
import lombok.Data;

@Data
public class Event<K, T> {
  private Type eventType;
  private K key;
  private T data;

  @JsonSerialize(using = ZonedDateTimeSerializer.class)
  private ZonedDateTime eventCreatedAt;

  public Event() {
  }

  public Event(Type eventType, K key, T data) {
    this.eventType = eventType;
    this.key = key;
    this.data = data;
    this.eventCreatedAt = ZonedDateTime.now();
  }

  public enum Type {
    ORDER,
    PAYMENT
  }
}