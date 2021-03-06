package com.ecommerce.common.event;

import com.ecommerce.common.logging.AutoNamingLoggerFactory;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.slf4j.Logger;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RabbitDomainEventPublisher {
    private static final Logger logger = AutoNamingLoggerFactory.getLogger();
    private final DomainEventDAO eventDAO;
    private final RabbitTemplate rabbitTemplate;


    public RabbitDomainEventPublisher(DomainEventDAO eventDAO,
                                      ConnectionFactory connectionFactory,
                                      MessageConverter messageConverter) {
        this.eventDAO = eventDAO;
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause)
                -> {
            if (ack) {
                eventDAO.delete(correlationData.getId());
            }
        });
        this.rabbitTemplate = rabbitTemplate;
    }


    @Scheduled(fixedDelay = 2000)
    @SchedulerLock(name = "domain-event-publisher", lockAtMostFor = 30000, lockAtLeastFor = 1000)
    public void run() {
        List<DomainEvent> newestEvents = eventDAO.nextBatchEvents();
        newestEvents.forEach(event -> {
            try {
                DomainEventType eventType = event.get_type();
                String exchange = eventType.name().toLowerCase().split("_")[0];
                String routingKey = eventType.name().toLowerCase().replace('_', '.');
                eventDAO.increasePublishTries(event.get_id());
                rabbitTemplate.convertAndSend(exchange,
                        routingKey,
                        event,
                        new CorrelationData(event.get_id()));
            } catch (Throwable t) {
                logger.error("Error while publish domain event {}:{}", event, t.getMessage());
            }
        });
    }

}
