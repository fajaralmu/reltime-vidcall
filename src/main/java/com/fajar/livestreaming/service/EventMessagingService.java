package com.fajar.livestreaming.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.support.PayloadMethodArgumentResolver; 
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
//@KafkaListener
@Service
public class EventMessagingService {
	
	public EventMessagingService() {
		PayloadMethodArgumentResolver ccc = null;
		log.debug("Instantiated EventMessagingService"); 
	}

	@KafkaListener(topics = "test_topic",groupId = "test_kafka")
	public void listenGroupFoo(Object message) {
	    log.info("Received Message in group foo: " + message);
	}

}
