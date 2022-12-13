package com.interview;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.interview.model.Address;
import com.interview.repo.AddressRepo;

@Configuration
public class RabbitConfig {

	public static final String EXCHANGE_NAME = "exchange-name";
	public static final String QUEUE_NAME = "queue-name";
	public static final String ROUTING_KEY = "routing-key";

	@Autowired
	AddressRepo addressService;

	@Bean
	public Binding bindingConfiguration() {
		return BindingBuilder.bind(extracted())
				.to(topicExchange())
				.with(RabbitConfig.ROUTING_KEY);
	}


	@RabbitListener(queues = RabbitConfig.QUEUE_NAME)
	public void consumeMessage(Address address) {
		addressService.save(address);
	}

	@Bean
	public Queue extracted() {
		return new Queue(RabbitConfig.QUEUE_NAME);
	}

	@Bean
	public Jackson2JsonMessageConverter extractedMessageConvertor() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public RabbitTemplate template(ConnectionFactory cf) {
		final var rabbitTemplate = new RabbitTemplate(cf);
		rabbitTemplate.setMessageConverter(extractedMessageConvertor());
		return rabbitTemplate;
	}

	@Bean
	public TopicExchange topicExchange() {
		return new TopicExchange(RabbitConfig.EXCHANGE_NAME);
	}

}
