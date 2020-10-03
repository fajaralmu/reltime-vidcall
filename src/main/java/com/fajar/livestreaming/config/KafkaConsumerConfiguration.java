package com.fajar.livestreaming.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import lombok.extern.slf4j.Slf4j;

//@Configuration
//@EnableKafka
@Slf4j
public class KafkaConsumerConfiguration implements DynamicBean {

	@Value(value = "${kafka.bootstrapAddress}")
	private String bootstrapAddress;
	@Value(value = "${kafka.groupId}")
	private String groupId;
	@Value("${kafka.topic}")
	private String kafkaTopic;
	
	public KafkaConsumerConfiguration() {
//		org.springframework.messaging.handler.annotation.support.PayloadMethodArgume.class
//		 org/springframework/messaging/handler/annotation/support/PayloadMethodArgumentResolver
		log.info("................KafkaConsumerConfiguration..............");
	}

//	public KafkaConsumerConfiguration(ConfigurableBeanFactory factory, String bootstrappedAddress, String groupId) {
//		this.factory = factory;
//		this.bootstrapAddress = bootstrappedAddress;
//		this.groupId = groupId;
//	}

	@Bean
	public ConsumerFactory<String, String> consumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		return new DefaultKafkaConsumerFactory<>(props);
	}
//
	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {

		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		return factory;
	}

	@Override
	public void registerBean() {
//		try {
//			factory.registerSingleton("consumerFactory", consumerFactory());
//			factory.registerSingleton("kafkaListenerContainerFactory", kafkaListenerContainerFactory());
//		}catch (Exception e) {
//			e.printStackTrace();
//		}

	}
}