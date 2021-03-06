package com.ecommerce.common.event.order;

import com.ecommerce.common.event.DomainEventType;
import com.ecommerce.common.model.Address;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;

import static com.ecommerce.common.event.DomainEventType.ORDER_CREATED;

public class OrderCreatedEvent extends OrderEvent {
    private final BigDecimal price;
    private final Instant createdAt;
    private final Address address;

    @JsonCreator
    private OrderCreatedEvent(@JsonProperty("_id") String _id,
                              @JsonProperty("_type") DomainEventType _type,
                              @JsonProperty("_createdAt") Instant _createdAt,
                              @JsonProperty("orderId") String orderId,
                              @JsonProperty("price") BigDecimal price,
                              @JsonProperty("createdAt") Instant createdAt,
                              @JsonProperty("address") Address address) {
        super(orderId, _id, _type, _createdAt);
        this.price = price;
        this.createdAt = createdAt;
        this.address = address;
    }

    public OrderCreatedEvent(String orderId, BigDecimal price, Address address, Instant createdAt) {
        super(orderId, ORDER_CREATED);
        this.price = price;
        this.createdAt = createdAt;
        this.address = address;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Address getAddress() {
        return address;
    }
}
