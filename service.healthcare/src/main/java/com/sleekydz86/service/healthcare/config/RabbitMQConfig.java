package com.sleekydz86.service.healthcare.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    @Value("${spring.rabbitmq.exchange:healthcare.exchange}")
    private String exchange;
    
    @Value("${spring.rabbitmq.queue.health-data:health.data.queue}")
    private String healthDataQueue;
    
    @Value("${spring.rabbitmq.queue.user:user.queue}")
    private String userQueue;

    @Bean
    public TopicExchange healthcareExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue healthDataQueue() {
        return QueueBuilder.durable(healthDataQueue).build();
    }

    @Bean
    public Queue userQueue() {
        return QueueBuilder.durable(userQueue).build();
    }

    @Bean
    public Binding healthDataBinding() {
        return BindingBuilder
            .bind(healthDataQueue())
            .to(healthcareExchange())
            .with("health.data.*");
    }

    @Bean
    public Binding userBinding() {
        return BindingBuilder
            .bind(userQueue())
            .to(healthcareExchange())
            .with("user.*");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}

