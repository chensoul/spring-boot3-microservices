package com.chensoul.bookstore.notification.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderEventRepository extends MongoRepository<OrderEventEntity, Long> {
    boolean existsByEventId(String eventId);
}
