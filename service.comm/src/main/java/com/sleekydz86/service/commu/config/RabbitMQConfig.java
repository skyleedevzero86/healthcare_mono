package com.sleekydz86.service.commu.config;

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
    
    @Value("${spring.rabbitmq.queue.community:community.queue}")
    private String communityQueue;

    @Bean
    public TopicExchange healthcareExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue communityQueue() {
        return QueueBuilder.durable(communityQueue).build();
    }

    @Bean
    public Binding communityBinding() {
        return BindingBuilder
            .bind(communityQueue())
            .to(healthcareExchange())
            .with("community.*");
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

